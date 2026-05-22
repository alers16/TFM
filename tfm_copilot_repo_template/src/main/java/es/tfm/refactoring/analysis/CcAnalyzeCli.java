package es.tfm.refactoring.analysis;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Herramienta CLI que calcula y muestra la complejidad cognitiva de cada método
 * en uno o varios ficheros Java.
 *
 * <p>Uso:
 * <pre>
 *   java es.tfm.refactoring.analysis.CcAnalyzeCli &lt;fichero.java&gt; [fichero2.java ...]
 * </pre>
 *
 * <p>Ejemplo desde la raíz del proyecto:
 * <pre>
 *   java -cp "target/classes;$(cat target/classpath.txt)" \
 *        es.tfm.refactoring.analysis.CcAnalyzeCli CognitiveComplexityMethodCheckMax0.java
 * </pre>
 */
public class CcAnalyzeCli {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Uso: CcAnalyzeCli <fichero.java> [fichero2.java ...]");
            System.exit(1);
        }

        JavaParser parser = new JavaParser(
                new ParserConfiguration()
                        .setLanguageLevel(ParserConfiguration.LanguageLevel.RAW));
        CognitiveComplexityCalculator calculator = new CognitiveComplexityCalculator();

        for (String arg : args) {
            Path path = Paths.get(arg);
            if (!Files.exists(path)) {
                System.err.println("[ERROR] Fichero no encontrado: " + path);
                continue;
            }
            analyzeFile(path, parser, calculator);
        }
    }

    private static void analyzeFile(Path path, JavaParser parser,
                                    CognitiveComplexityCalculator calculator) throws IOException {
        String src = Files.readString(path);
        ParseResult<CompilationUnit> result = parser.parse(src);

        if (result.getResult().isEmpty()) {
            System.err.println("[ERROR] No se pudo parsear: " + path);
            result.getProblems().forEach(p -> System.err.println("  " + p));
            return;
        }

        CompilationUnit cu = result.getResult().get();
        List<MethodDeclaration> methods = cu.findAll(MethodDeclaration.class);

        // Construir filas: (nombre con parámetros, línea, CC)
        record Row(String signature, int line, int cc) {}
        List<Row> rows = methods.stream()
                .map(md -> {
                    String params = md.getParameters().stream()
                            .map(p -> p.getType().asString())
                            .collect(Collectors.joining(", "));
                    String sig = md.getNameAsString() + "(" + params + ")";
                    int line = md.getRange().map(r -> r.begin.line).orElse(0);
                    int cc   = calculator.calculate(md);
                    return new Row(sig, line, cc);
                })
                .sorted(Comparator.comparingInt(Row::cc).reversed())
                .collect(Collectors.toList());

        // Ancho de columna para alinear
        int maxSigLen = rows.stream().mapToInt(r -> r.signature().length()).max().orElse(20);
        maxSigLen = Math.max(maxSigLen, 20);
        String fmt = "  %-" + maxSigLen + "s  %4s  línea %d%n";

        System.out.println();
        System.out.println("Complejidad cognitiva: " + path.getFileName());
        System.out.println("=".repeat(maxSigLen + 22));
        System.out.printf("  %-" + maxSigLen + "s  %4s  %s%n", "Método", "CC", "Línea");
        System.out.println("-".repeat(maxSigLen + 22));

        for (Row row : rows) {
            String bar = row.cc() > 0 ? " " + "█".repeat(Math.min(row.cc(), 30)) : "";
            System.out.printf("  %-" + maxSigLen + "s  %4d  línea %-4d%s%n",
                    row.signature(), row.cc(), row.line(), bar);
        }

        System.out.println("-".repeat(maxSigLen + 22));
        int total  = rows.stream().mapToInt(Row::cc).sum();
        long above5 = rows.stream().filter(r -> r.cc() >= 5).count();
        System.out.printf("  Métodos: %-4d  CC total: %-4d  Con CC>=5: %d%n",
                rows.size(), total, above5);
        System.out.println();
    }
}
