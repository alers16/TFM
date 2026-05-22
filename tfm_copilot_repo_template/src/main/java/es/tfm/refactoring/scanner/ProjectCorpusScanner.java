package es.tfm.refactoring.scanner;

import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;

import es.tfm.refactoring.analysis.CognitiveComplexityCalculator;
import es.tfm.refactoring.detection.DetectionResult;
import es.tfm.refactoring.detection.DetectionMode;
import es.tfm.refactoring.detection.DiscardReason;
import es.tfm.refactoring.detection.NestedIfDetector;
import es.tfm.refactoring.detection.RefactoringOpportunity;
import es.tfm.refactoring.transformation.NestedIfTransformer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Analizador masivo de proyectos Java para detección de oportunidades de
 * combinación de condicionales anidados.
 * <p>
 * Para cada proyecto analizado:
 * <ol>
 *   <li>Recorre recursivamente todos los ficheros {@code .java}.</li>
 *   <li>Parsea cada fichero con JavaParser en modo leniente (sin resolución
 *       de tipos externos).</li>
 *   <li>Para cada método encontrado, ejecuta {@link NestedIfDetector}
 *       en modo {@link DetectionMode#STRICT}.</li>
 *   <li>Para cada candidato aceptado, calcula la CC antes/después.</li>
 *   <li>Registra todos los resultados como {@link ScanFinding}, incluyendo
 *       los candidatos rechazados con sus motivos de descarte.</li>
 * </ol>
 * <p>
 * Contribuye a la ampliación del corpus experimental para RQ1 y RQ2.
 */
public class ProjectCorpusScanner {

    private static final Logger LOGGER = Logger.getLogger(ProjectCorpusScanner.class.getName());

    private final NestedIfDetector detector;
    private final NestedIfTransformer transformer;
    private final CognitiveComplexityCalculator calculator;

    public ProjectCorpusScanner() {
        this.detector = new NestedIfDetector(DetectionMode.STRICT);
        this.transformer = new NestedIfTransformer();
        this.calculator = new CognitiveComplexityCalculator();
    }

    /**
     * Escanea un directorio de proyecto y devuelve todos los hallazgos.
     *
     * @param projectName nombre lógico del proyecto (aparecerá en el CSV)
     * @param projectRoot directorio raíz del proyecto a analizar
     * @return lista de hallazgos (candidatos aceptados y rechazados)
     */
    public List<ScanFinding> scan(String projectName, Path projectRoot) {
        List<ScanFinding> findings = new ArrayList<>();

        configureParser();

        try (Stream<Path> paths = Files.walk(projectRoot)) {
            paths.filter(p -> p.toString().endsWith(".java"))
                 .forEach(javaFile -> scanFile(projectName, projectRoot, javaFile, findings));
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Error al recorrer el directorio: " + projectRoot, e);
        }

        return findings;
    }

    // ── Parseo ───────────────────────────────────────────────────────────────

    private static void configureParser() {
        ParserConfiguration config = new ParserConfiguration();
        config.setLanguageLevel(ParserConfiguration.LanguageLevel.RAW);
        StaticJavaParser.setConfiguration(config);
    }

    private void scanFile(String projectName, Path projectRoot,
                          Path javaFile, List<ScanFinding> findings) {
        CompilationUnit cu;
        try {
            cu = StaticJavaParser.parse(javaFile);
        } catch (Exception e) {
            LOGGER.log(Level.FINE, "No se pudo parsear: " + javaFile.getFileName(), e);
            return;
        }

        String relPath = projectRoot.relativize(javaFile).toString().replace('\\', '/');

        // Recorrer todas las clases e interfaces de nivel superior y anidadas
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(classDecl -> {
            String className = classDecl.getNameAsString();
            classDecl.getMethods().forEach(method ->
                    scanMethod(projectName, relPath, className, method, findings));
        });

        // También enumeraciones
        cu.findAll(EnumDeclaration.class).forEach(enumDecl -> {
            String className = enumDecl.getNameAsString();
            enumDecl.getMethods().forEach(method ->
                    scanMethod(projectName, relPath, className, method, findings));
        });
    }

    // ── Análisis por método ───────────────────────────────────────────────────

    private void scanMethod(String projectName, String relPath,
                            String className, MethodDeclaration method,
                            List<ScanFinding> findings) {
        if (method.getBody().isEmpty()) {
            return; // métodos abstractos o de interfaz
        }

        List<DetectionResult> results = detector.detectWithReasons(method);
        if (results.isEmpty()) {
            return; // no hay ningún if en el método
        }

        int ccBefore = calculator.calculate(method);
        String methodSig = method.getDeclarationAsString(false, false, false);

        for (DetectionResult result : results) {
            findings.add(buildFinding(
                    projectName, relPath, className,
                    method.getNameAsString(), methodSig,
                    ccBefore, method, result));
        }
    }

    private ScanFinding buildFinding(String projectName,
                                     String relPath,
                                     String className,
                                     String methodName,
                                     String methodSig,
                                     int ccBefore,
                                     MethodDeclaration method,
                                     DetectionResult result) {
        if (result.isAccepted()) {
            RefactoringOpportunity opp = result.getOpportunity();

            // Calcular CC después clonando y aplicando TODAS las oportunidades del método
            int ccAfter = computeCcAfter(method);

            return new ScanFinding(
                    projectName, relPath, className, methodName, methodSig,
                    opp.getStartLine(),
                    true,
                    Collections.emptyList(),
                    ccBefore, ccAfter, ccAfter - ccBefore,
                    opp.getOuterCondition(),
                    opp.getInnerCondition());
        } else {
            return new ScanFinding(
                    projectName, relPath, className, methodName, methodSig,
                    result.getCandidateLine(),
                    false,
                    result.getDiscardReasons(),
                    -1, -1, 0,
                    "", "");
        }
    }

    /**
     * Estima la CC tras aplicar todas las oportunidades disponibles sobre un clon
     * del método. El método original no se modifica.
     */
    private int computeCcAfter(MethodDeclaration method) {
        MethodDeclaration clone = method.clone();
        List<RefactoringOpportunity> opportunities = detector.detect(clone);
        for (RefactoringOpportunity opp : opportunities) {
            transformer.apply(opp);
        }
        // Reaplicar para convergencia (triple-nested, etc.)
        List<RefactoringOpportunity> second = detector.detect(clone);
        for (RefactoringOpportunity opp : second) {
            transformer.apply(opp);
        }
        return calculator.calculate(clone);
    }
}
