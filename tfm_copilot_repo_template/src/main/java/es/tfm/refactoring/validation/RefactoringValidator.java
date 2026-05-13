package es.tfm.refactoring.validation;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import es.tfm.refactoring.detection.DetectionMode;
import es.tfm.refactoring.detection.NestedIfDetector;
import es.tfm.refactoring.detection.RefactoringOpportunity;
import es.tfm.refactoring.experiment.ExperimentCase;
import es.tfm.refactoring.transformation.NestedIfTransformer;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Valida que el código producido por el transformador sea compilable por {@code javac}.
 * <p>
 * <strong>Propósito experimental:</strong> evidencia mínima de que la transformación
 * no genera código roto. Responde a la pregunta "¿el output del prototipo es Java válido?"
 * de forma dinámica (compilación real), no solo estática (parseo JavaParser).
 * <p>
 * <strong>Alcance:</strong>
 * <ul>
 *   <li>Solo valida casos elegibles (transformados). Los no elegibles se marcan SKIPPED.</li>
 *   <li>No ejecuta tests de comportamiento del proyecto fuente. Eso requeriría descargar
 *       y configurar cada proyecto origen (trabajo futuro).</li>
 *   <li>Usa el compilador del JDK en ejecución. Si se ejecuta en una JRE sin {@code javac},
 *       los casos se marcan ERROR con descripción informativa.</li>
 * </ul>
 * <p>
 * <strong>Estrategia de compilación:</strong>
 * <ol>
 *   <li>Re-parsea {@code ExperimentCase.sourceCode} (clase completa) con JavaParser.</li>
 *   <li>Aplica la transformación directamente sobre el AST adjunto (sin clonar).</li>
 *   <li>Imprime el AST transformado como cadena y escribe a fichero temporal.</li>
 *   <li>Invoca {@code javac} mediante {@code javax.tools.JavaCompiler}.</li>
 *   <li>Recoge diagnósticos y devuelve {@link ValidationResult}.</li>
 *   <li>Borra el directorio temporal al finalizar.</li>
 * </ol>
 */
public class RefactoringValidator {

    static final int MAX_PASSES = 10;

    private final DetectionMode mode;

    public RefactoringValidator() {
        this(DetectionMode.STRICT);
    }

    public RefactoringValidator(DetectionMode mode) {
        this.mode = mode;
    }

    /**
     * Valida un caso: transforma y compila. Si el caso no es elegible devuelve SKIPPED.
     *
     * @param experimentCase caso experimental con el código fuente de la clase completa
     * @return resultado de la validación
     */
    public ValidationResult validate(ExperimentCase experimentCase) {
        String caseId = experimentCase.getCaseId();

        // 1. Parseo del source completo
        CompilationUnit cu;
        try {
            cu = StaticJavaParser.parse(experimentCase.getSourceCode());
        } catch (Exception e) {
            return ValidationResult.error(caseId, "Error de parseo: " + e.getMessage());
        }

        // 2. Buscar el primer método
        MethodDeclaration method = cu.findFirst(MethodDeclaration.class).orElse(null);
        if (method == null) {
            return ValidationResult.error(caseId, "No se encontró ningún método en el source");
        }

        // 3. Aplicar transformación sobre el AST adjunto (modo punto fijo)
        NestedIfDetector detector = new NestedIfDetector(mode);
        NestedIfTransformer transformer = new NestedIfTransformer();
        int totalApplied = 0;
        int passes = 0;
        while (passes < MAX_PASSES) {
            List<RefactoringOpportunity> opps = detector.detect(method);
            if (opps.isEmpty()) break;
            if (transformer.apply(opps.get(0))) totalApplied++;
            passes++;
        }

        // 4. Si no se aplicó ninguna transformación, el caso no es elegible
        if (totalApplied == 0) {
            return ValidationResult.skipped(caseId);
        }

        // 5. Obtener nombre de la clase para el fichero
        String className = cu.findFirst(ClassOrInterfaceDeclaration.class)
                .map(ClassOrInterfaceDeclaration::getNameAsString)
                .orElse("TransformedClass_" + caseId.replaceAll("[^A-Za-z0-9]", "_"));

        // 6. Compilar
        return compileSource(caseId, className, cu.toString());
    }

    // -------------------------------------------------------------------------
    // Compilación con javax.tools.JavaCompiler
    // -------------------------------------------------------------------------

    private ValidationResult compileSource(String caseId,
                                            String className,
                                            String source) {
        var compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            return ValidationResult.error(caseId,
                    "javax.tools.JavaCompiler no disponible "
                    + "(¿se ejecuta sobre JRE sin javac?)");
        }

        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("tfm-val-");
            Path javaFile = tempDir.resolve(className + ".java");
            Files.writeString(javaFile, source, StandardCharsets.UTF_8);

            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
            try (var fileManager = compiler.getStandardFileManager(
                    diagnostics, null, StandardCharsets.UTF_8)) {

                var compilationUnits = fileManager.getJavaFileObjects(javaFile.toFile());
                var task = compiler.getTask(
                        null, fileManager, diagnostics,
                        List.of("-d", tempDir.toString()),
                        null, compilationUnits);

                boolean success = task.call();

                if (success) {
                    return ValidationResult.pass(caseId);
                } else {
                    String errors = diagnostics.getDiagnostics().stream()
                            .filter(d -> d.getKind() == javax.tools.Diagnostic.Kind.ERROR)
                            .map(d -> "L" + d.getLineNumber() + ": " + d.getMessage(null))
                            .collect(Collectors.joining(" | "));
                    return ValidationResult.fail(caseId, errors);
                }
            }

        } catch (IOException e) {
            return ValidationResult.error(caseId, "Error I/O al compilar: " + e.getMessage());
        } finally {
            deleteTempDir(tempDir);
        }
    }

    /** Elimina el directorio temporal y su contenido. */
    private static void deleteTempDir(Path dir) {
        if (dir == null) return;
        try {
            Files.walk(dir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(p -> {
                        try { Files.deleteIfExists(p); } catch (IOException ignored) {}
                    });
        } catch (IOException ignored) {}
    }
}
