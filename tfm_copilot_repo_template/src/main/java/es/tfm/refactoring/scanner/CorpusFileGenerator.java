package es.tfm.refactoring.scanner;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Generador automático de ficheros de corpus a partir del CSV producido por
 * {@link CorpusScanRunner}.
 * <p>
 * Para cada caso elegible del CSV:
 * <ol>
 *   <li>Localiza el fichero Java de origen.</li>
 *   <li>Extrae el método por nombre y línea.</li>
 *   <li>Genera un fichero corpus con los metadatos en cabecera y el método
 *       envuelto en una clase siguiendo el formato de {@code /real-corpus/}.</li>
 * </ol>
 *
 * <h2>Uso</h2>
 * <pre>
 * java -cp ... es.tfm.refactoring.scanner.CorpusFileGenerator \
 *     output/corpus-scan/corpus-scan-results.csv \
 *     tmp-projects [tmp-tutor-projects ...] \
 *     output/corpus-scan/generated-corpus
 * </pre>
 *
 * <p>Se puede indicar más de un directorio de proyectos: todos los argumentos
 * entre el CSV y el directorio de salida se tratan como raíces de búsqueda.
 *
 * <p>Los ficheros generados deben revisarse antes de añadirse al corpus
 * oficial: verificar que la envoltura compila, que los metadatos son correctos
 * y que el CC estimado coincide con la medición real de SonarQube.
 */
public class CorpusFileGenerator {

    private static final Logger LOGGER = Logger.getLogger(CorpusFileGenerator.class.getName());

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.err.println("Uso: CorpusFileGenerator <csv> <projects-root> [projects-root2 ...] <output-dir>");
            System.exit(1);
        }

        Path csvPath  = Paths.get(args[0]);
        Path outputDir = Paths.get(args[args.length - 1]);
        List<Path> projectsRoots = new ArrayList<>();
        for (int i = 1; i < args.length - 1; i++) {
            projectsRoots.add(Paths.get(args[i]));
        }

        Files.createDirectories(outputDir);

        configureParser();

        List<String[]> eligibleRows = readEligibleRows(csvPath);
        System.out.printf("Casos elegibles en CSV: %d%n", eligibleRows.size());

        int generated = 0;
        int skipped   = 0;
        Map<String, Integer> generatedByProject = new LinkedHashMap<>();

        for (String[] row : eligibleRows) {
            String project   = row[0];
            String file      = row[1];
            String className = row[2];
            String method    = row[3];
            String signature = row[4];
            int    line      = Integer.parseInt(row[5]);
            int    ccBefore  = Integer.parseInt(row[8]);
            int    ccAfter   = Integer.parseInt(row[9]);
            int    delta     = Integer.parseInt(row[10]);

            // Intentar varios paths de src conocidos para el proyecto
            Path javaFile = resolveJavaFile(projectsRoots, project, file);
            if (javaFile == null || !Files.exists(javaFile)) {
                System.err.printf("  [SKIP] No se encontró: %s/%s%n", project, file);
                skipped++;
                continue;
            }

            MethodDeclaration md = extractMethod(javaFile, method, line);
            if (md == null) {
                System.err.printf("  [SKIP] Método no encontrado: %s#%s() L%d%n",
                        file, method, line);
                skipped++;
                continue;
            }

            // Generar nombre del fichero corpus
            String caseId = buildCaseId(project, className, method, line);
            String corpusFileName = buildCorpusFileName(project, className, method, line);
            Path outFile = outputDir.resolve(corpusFileName);

            writeCorpusFile(outFile, caseId, project, file, className, method,
                    signature, ccBefore, ccAfter, delta, md);

            generatedByProject.merge(project, 1, Integer::sum);
            generated++;
            System.out.printf("  [OK] %s → %s%n", project + "/" + method, corpusFileName);
        }

        System.out.println();
        System.out.printf("Generados: %d | Omitidos: %d%n", generated, skipped);
        System.out.println("Ficheros por proyecto:");
        generatedByProject.forEach((p, c) -> System.out.printf("  %s: %d%n", p, c));
        System.out.printf("%nFicheros en: %s%n", outputDir.toAbsolutePath());
        System.out.println();
        System.out.println("IMPORTANTE: Revisar los ficheros generados antes de añadirlos al corpus:");
        System.out.println("  1. Verificar que la envoltura compila (javac o mvn compile).");
        System.out.println("  2. Confirmar CC con SonarQube si es posible.");
        System.out.println("  3. Añadir los ficheros a src/main/resources/real-corpus/");
        System.out.println("  4. Registrar en RealDatasetLoader.standardRealFiles()");
    }

    // ── Lectura CSV ───────────────────────────────────────────────────────────

    private static List<String[]> readEligibleRows(Path csv) throws IOException {
        List<String[]> rows = new ArrayList<>();
        List<String> lines = Files.readAllLines(csv, StandardCharsets.UTF_8);
        // Saltar cabecera
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            String[] cols = parseCsvLine(line);
            if (cols.length > 6 && "YES".equals(cols[6])) {
                rows.add(cols);
            }
        }
        return rows;
    }

    /** Parseo CSV básico con soporte para campos entre comillas dobles. */
    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    // ── Resolución de rutas ───────────────────────────────────────────────────

    /**
     * Busca el fichero Java en las rutas src tipicas del proyecto.
     * Estrategia de resolucion (en orden):
     * <ol>
     *   <li>Rutas src canonicas dentro de {@code root/project/} para cada raiz.</li>
     *   <li>Busqueda recursiva dentro de {@code root/project/} (sub-modulos).</li>
     *   <li>Busqueda recursiva en todo {@code root/} (nombre de carpeta distinto
     *       al nombre del proyecto en el CSV, ej. "cybercaptor" vs "cybercaptor-server").</li>
     * </ol>
     */
    private static Path resolveJavaFile(List<Path> roots, String project, String relFile) {
        String[] srcPaths = {
            "src/main/java", "src/main", "src", ""
        };
        for (Path projectsRoot : roots) {
            for (String src : srcPaths) {
                Path candidate = src.isEmpty()
                        ? projectsRoot.resolve(project).resolve(relFile)
                        : projectsRoot.resolve(project).resolve(src).resolve(relFile);
                if (Files.exists(candidate)) {
                    return candidate;
                }
            }
        }
        // Tambien probar con sub-modulos (ej. jmetal-problem/src/main/java)
        for (Path projectsRoot : roots) {
            try {
                Path found = Files.walk(projectsRoot.resolve(project))
                        .filter(p -> p.toString().endsWith(relFile.replace('/', '\\'))
                                     || p.toString().endsWith(relFile))
                        .findFirst()
                        .orElse(null);
                if (found != null) return found;
            } catch (IOException e) {
                // La carpeta del proyecto no existe en esta raiz; continuar
            }
        }
        // Fallback: buscar en todas las raices (cubre nombres de carpeta distintos
        // al nombre del proyecto en el CSV, ej. "cybercaptor" vs "cybercaptor-server")
        for (Path projectsRoot : roots) {
            try {
                Path found = Files.walk(projectsRoot)
                        .filter(p -> p.toString().endsWith(relFile.replace('/', '\\'))
                                     || p.toString().endsWith(relFile))
                        .findFirst()
                        .orElse(null);
                if (found != null) return found;
            } catch (IOException e) {
                // Continuar con la siguiente raiz
            }
        }
        return null;
    }

    // ── Extracción de método ──────────────────────────────────────────────────

    private static void configureParser() {
        ParserConfiguration cfg = new ParserConfiguration();
        cfg.setLanguageLevel(ParserConfiguration.LanguageLevel.RAW);
        StaticJavaParser.setConfiguration(cfg);
    }

    private static MethodDeclaration extractMethod(Path javaFile, String methodName, int line) {
        CompilationUnit cu;
        try {
            cu = StaticJavaParser.parse(javaFile);
        } catch (Exception e) {
            return null;
        }

        return cu.findAll(MethodDeclaration.class).stream()
                .filter(m -> m.getNameAsString().equals(methodName))
                .filter(m -> m.getBegin().map(b -> Math.abs(b.line - line) <= 5).orElse(false))
                .findFirst()
                .orElseGet(() ->
                        // fallback: solo por nombre (si hay varios con el mismo nombre, el primero)
                        cu.findAll(MethodDeclaration.class).stream()
                                .filter(m -> m.getNameAsString().equals(methodName))
                                .findFirst()
                                .orElse(null));
    }

    // ── Generación de fichero corpus ──────────────────────────────────────────

    private static void writeCorpusFile(Path outFile,
                                        String caseId,
                                        String project,
                                        String relFile,
                                        String className,
                                        String method,
                                        String signature,
                                        int ccBefore,
                                        int ccAfter,
                                        int delta,
                                        MethodDeclaration md) throws IOException {
        // Determinar origen y licencia según el proyecto
        String origin  = guessOrigin(project);
        String license = guessLicense(project);

        String methodSource = md.toString();

        String content = "// @caseId " + caseId + "\n"
                + "// @origin " + origin + "\n"
                + "// @project " + toDisplayName(project) + "\n"
                + "// @file " + relFile.replaceAll(".*/", "") + "\n"
                + "// @method " + method + "(" + extractParamTypes(signature) + ")\n"
                + "// @license " + license + "\n"
                + "// @sonarCCBefore " + ccBefore + "\n"
                + "// @sonarCCAfter " + ccAfter + "   (estimado; CC delta=" + delta + ")\n"
                + "// @eligible YES\n"
                + "// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por"
                + " CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.\n"
                + "\n"
                + "class " + toClassName(caseId) + " {\n"
                + indent(methodSource) + "\n"
                + "}\n";

        Files.writeString(outFile, content, StandardCharsets.UTF_8);
    }

    private static String buildCaseId(String project, String className, String method, int line) {
        String p = project.toUpperCase().replace("-", "_");
        String c = toSnake(className).toUpperCase();
        String m = toSnake(method).toUpperCase();
        return p + "_" + c + "_" + m;
    }

    private static String buildCorpusFileName(String project, String className, String method, int line) {
        String p = toCamel(project);
        String c = className;
        String m = capitalize(method);
        return "Real" + p + c + m + ".java";
    }

    private static String guessOrigin(String project) {
        return switch (project) {
            case "commons-lang"     -> "commons-lang-3.14.0";
            case "commons-io"       -> "commons-io-2.15.1";
            case "commons-math"     -> "commons-math-3.6.1";
            case "commons-compress" -> "commons-compress-1.26.0";
            case "ant"              -> "ant-1.10.14";
            case "jmetal"           -> "jmetal-5.11";
            case "bytecode-viewer"  -> "bytecode-viewer-2.11.2";
            default                 -> project;
        };
    }

    private static String guessLicense(String project) {
        return switch (project) {
            case "commons-lang", "commons-io", "commons-math",
                 "commons-compress", "ant" -> "Apache-2.0";
            case "jmetal"           -> "MIT";
            case "bytecode-viewer"  -> "GPL-3.0";
            default                 -> "[PENDIENTE]";
        };
    }

    private static String toDisplayName(String project) {
        return switch (project) {
            case "commons-lang"     -> "Apache Commons Lang";
            case "commons-io"       -> "Apache Commons IO";
            case "commons-math"     -> "Apache Commons Math";
            case "commons-compress" -> "Apache Commons Compress";
            case "ant"              -> "Apache Ant";
            case "jmetal"           -> "jMetal";
            case "bytecode-viewer"  -> "Bytecode Viewer";
            default -> project;
        };
    }

    private static String extractParamTypes(String signature) {
        int start = signature.indexOf('(');
        int end   = signature.lastIndexOf(')');
        if (start < 0 || end < 0) return "";
        return signature.substring(start + 1, end);
    }

    private static String indent(String code) {
        return code.lines()
                .map(l -> "    " + l)
                .reduce("", (a, b) -> a.isEmpty() ? b : a + "\n" + b);
    }

    private static String toSnake(String s) {
        return s.replaceAll("([A-Z])", "_$1").toLowerCase().replaceAll("^_", "");
    }

    private static String toCamel(String s) {
        String[] parts = s.split("[-_]");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            sb.append(capitalize(p));
        }
        return sb.toString();
    }

    private static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private static String toClassName(String caseId) {
        String[] parts = caseId.split("_");
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            sb.append(capitalize(p.toLowerCase()));
        }
        return sb.toString();
    }
}
