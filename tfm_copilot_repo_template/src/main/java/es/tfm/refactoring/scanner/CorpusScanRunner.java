package es.tfm.refactoring.scanner;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Punto de entrada para el análisis masivo de proyectos Java.
 * <p>
 * Escanea uno o varios proyectos, recopila todos los candidatos a
 * combinación de condicionales anidados y genera dos artefactos de salida:
 * <ul>
 *   <li>{@code corpus-scan-results.csv} — un registro por candidato.</li>
 *   <li>{@code corpus-scan-report.md} — resumen legible por proyecto.</li>
 * </ul>
 *
 * <h2>Modos de uso</h2>
 * <pre>
 * # Modo 1: un directorio raíz cuyas subcarpetas son los proyectos
 * java -cp ... es.tfm.refactoring.scanner.CorpusScanRunner --root /ruta/proyectos
 *
 * # Modo 2: proyectos individuales especificados como pares nombre=ruta
 * java -cp ... es.tfm.refactoring.scanner.CorpusScanRunner \
 *     --project commons-lang=/ruta/commons-lang \
 *     --project commons-io=/ruta/commons-io
 *
 * # Modo 3: directorio único tratado como un solo proyecto
 * java -cp ... es.tfm.refactoring.scanner.CorpusScanRunner --dir /ruta/proyecto
 * </pre>
 *
 * <p>Los resultados se escriben en el directorio {@code output/corpus-scan/}
 * relativo al directorio de trabajo actual.
 */
public class CorpusScanRunner {

    private static final Logger LOGGER = Logger.getLogger(CorpusScanRunner.class.getName());
    private static final String OUTPUT_DIR = "output/corpus-scan";

    public static void main(String[] args) throws IOException {
        Map<String, Path> projects = parseArguments(args);

        if (projects.isEmpty()) {
            printUsage();
            System.exit(1);
        }

        Path outputDir = Paths.get(OUTPUT_DIR);
        Files.createDirectories(outputDir);

        ProjectCorpusScanner scanner = new ProjectCorpusScanner();
        List<ScanFinding> allFindings = new ArrayList<>();

        for (Map.Entry<String, Path> entry : projects.entrySet()) {
            String name = entry.getKey();
            Path root = entry.getValue();
            System.out.printf("Escaneando proyecto: %s (%s)%n", name, root);
            List<ScanFinding> findings = scanner.scan(name, root);
            allFindings.addAll(findings);
            long eligible = findings.stream().filter(ScanFinding::isEligible).count();
            System.out.printf("  → %d candidatos encontrados (%d elegibles, %d no elegibles)%n",
                    findings.size(), eligible, findings.size() - eligible);
        }

        writeCsv(allFindings, outputDir.resolve("corpus-scan-results.csv"));
        writeMarkdownReport(allFindings, outputDir.resolve("corpus-scan-report.md"), projects);

        System.out.printf("%nResultados guardados en: %s%n", outputDir.toAbsolutePath());
        System.out.printf("Total candidatos: %d | Elegibles: %d | No elegibles: %d%n",
                allFindings.size(),
                allFindings.stream().filter(ScanFinding::isEligible).count(),
                allFindings.stream().filter(f -> !f.isEligible()).count());
    }

    // ── Argumentos ───────────────────────────────────────────────────────────

    /**
     * Parsea los argumentos de la línea de comandos y devuelve un mapa
     * nombre-proyecto → directorio.
     * <p>
     * Los tres modos soportados son:
     * <ul>
     *   <li>{@code --root <dir>}: las subcarpetas de {@code dir} son los proyectos.</li>
     *   <li>{@code --project nombre=<dir>}: especificación explícita de proyecto.</li>
     *   <li>{@code --dir <dir>}: un único proyecto cuyo nombre es el del directorio.</li>
     * </ul>
     */
    private static Map<String, Path> parseArguments(String[] args) throws IOException {
        Map<String, Path> projects = new LinkedHashMap<>();

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--root" -> {
                    if (i + 1 >= args.length) die("--root requiere un argumento");
                    Path root = Paths.get(args[++i]);
                    assertDir(root);
                    Files.list(root)
                         .filter(Files::isDirectory)
                         .sorted()
                         .forEach(sub -> projects.put(sub.getFileName().toString(), sub));
                }
                case "--project" -> {
                    if (i + 1 >= args.length) die("--project requiere nombre=ruta");
                    String spec = args[++i];
                    int eq = spec.indexOf('=');
                    if (eq < 1) die("--project requiere formato nombre=ruta");
                    String name = spec.substring(0, eq);
                    Path path = Paths.get(spec.substring(eq + 1));
                    assertDir(path);
                    projects.put(name, path);
                }
                case "--dir" -> {
                    if (i + 1 >= args.length) die("--dir requiere un argumento");
                    Path dir = Paths.get(args[++i]);
                    assertDir(dir);
                    projects.put(dir.getFileName().toString(), dir);
                }
                default -> LOGGER.warning("Argumento desconocido: " + args[i]);
            }
        }

        return projects;
    }

    private static void assertDir(Path p) {
        if (!Files.isDirectory(p)) {
            die("No es un directorio válido: " + p);
        }
    }

    private static void die(String message) {
        System.err.println("Error: " + message);
        printUsage();
        System.exit(1);
    }

    private static void printUsage() {
        System.err.println("""
                Uso:
                  --root <dir>              Las subcarpetas de <dir> son los proyectos
                  --project nombre=<dir>    Proyecto con nombre explícito (puede repetirse)
                  --dir <dir>               Un único proyecto

                Ejemplos:
                  --root C:/repos/open-source-java
                  --project commons-lang=C:/repos/commons-lang-3.12.0 --project ant=C:/repos/ant-1.10
                """);
    }

    // ── Salida CSV ────────────────────────────────────────────────────────────

    private static void writeCsv(List<ScanFinding> findings, Path output) throws IOException {
        // Ordenar: elegibles primero, luego por proyecto, fichero, línea
        List<ScanFinding> sorted = findings.stream()
                .sorted(Comparator
                        .comparing((ScanFinding f) -> f.isEligible() ? 0 : 1)
                        .thenComparing(ScanFinding::getProject)
                        .thenComparing(ScanFinding::getFile)
                        .thenComparingInt(ScanFinding::getCandidateLine))
                .collect(Collectors.toList());

        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(output, StandardCharsets.UTF_8))) {
            pw.println(ScanFinding.csvHeader());
            for (ScanFinding f : sorted) {
                pw.println(f.toCsvRow());
            }
        }
        System.out.println("CSV escrito: " + output);
    }

    // ── Informe Markdown ──────────────────────────────────────────────────────

    private static void writeMarkdownReport(List<ScanFinding> allFindings,
                                            Path output,
                                            Map<String, Path> projects) throws IOException {
        Map<String, List<ScanFinding>> byProject = allFindings.stream()
                .collect(Collectors.groupingBy(ScanFinding::getProject,
                        LinkedHashMap::new, Collectors.toList()));

        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(output, StandardCharsets.UTF_8))) {
            pw.println("# Informe de Escaneo Masivo de Corpus");
            pw.println();
            pw.printf("Proyectos analizados: **%d**  |  "
                    + "Candidatos totales: **%d**  |  "
                    + "Elegibles: **%d**  |  "
                    + "No elegibles: **%d**%n%n",
                    projects.size(),
                    allFindings.size(),
                    allFindings.stream().filter(ScanFinding::isEligible).count(),
                    allFindings.stream().filter(f -> !f.isEligible()).count());

            pw.println("---");
            pw.println();

            for (Map.Entry<String, List<ScanFinding>> entry : byProject.entrySet()) {
                writeProjectSection(pw, entry.getKey(), entry.getValue());
            }

            pw.println("---");
            pw.println("*Generado automáticamente por CorpusScanRunner (TFM)*");
        }
        System.out.println("Informe Markdown escrito: " + output);
    }

    private static void writeProjectSection(PrintWriter pw,
                                            String project,
                                            List<ScanFinding> findings) {
        List<ScanFinding> eligible = findings.stream().filter(ScanFinding::isEligible).toList();
        List<ScanFinding> ineligible = findings.stream().filter(f -> !f.isEligible()).toList();

        pw.printf("## %s%n%n", project);
        pw.printf("- Candidatos totales: **%d**%n", findings.size());
        pw.printf("- Elegibles (combinables): **%d**%n", eligible.size());
        pw.printf("- No elegibles: **%d**%n%n", ineligible.size());

        if (!eligible.isEmpty()) {
            pw.println("### Oportunidades de refactorización");
            pw.println();
            pw.println("| Fichero | Clase | Método | Línea | CC antes | CC después | Delta | Condición externa |");
            pw.println("|---------|-------|--------|-------|----------|-----------|-------|-------------------|");
            for (ScanFinding f : eligible) {
                pw.printf("| `%s` | %s | `%s` | %d | %d | %d | %+d | `%s` |%n",
                        f.getFile(), f.getClassName(),
                        truncate(f.getMethodName(), 40),
                        f.getCandidateLine(),
                        f.getCcBefore(), f.getCcAfter(), f.getDelta(),
                        truncate(f.getOuterCondition(), 60));
            }
            pw.println();
        }

        if (!ineligible.isEmpty()) {
            // Agrupar por motivo de descarte para dar una visión compacta
            Map<String, Long> reasonCount = ineligible.stream()
                    .flatMap(f -> f.getDiscardReasons().stream())
                    .collect(Collectors.groupingBy(r -> r.name(), Collectors.counting()));

            pw.println("### Motivos de descarte (resumen)");
            pw.println();
            reasonCount.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .forEach(e -> pw.printf("- `%s`: %d casos%n", e.getKey(), e.getValue()));
            pw.println();

            // Listado detallado (primeros 30 para no saturar el informe)
            int limit = Math.min(30, ineligible.size());
            if (limit > 0) {
                pw.printf("#### Detalle (primeros %d de %d)%n%n", limit, ineligible.size());
                pw.println("| Fichero | Método | Línea | Motivos |");
                pw.println("|---------|--------|-------|---------|");
                ineligible.stream().limit(limit).forEach(f ->
                        pw.printf("| `%s` | `%s` | %d | %s |%n",
                                truncate(f.getFile(), 60),
                                truncate(f.getMethodName(), 40),
                                f.getCandidateLine(),
                                f.getDiscardReasonsString()));
                pw.println();
            }
        }
    }

    private static String truncate(String s, int max) {
        if (s == null || s.length() <= max) return s == null ? "" : s;
        return s.substring(0, max) + "…";
    }
}
