package es.tfm.refactoring.analysis;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import es.tfm.refactoring.detection.NestedIfDetector;
import es.tfm.refactoring.detection.RefactoringOpportunity;
import es.tfm.refactoring.transformation.NestedIfTransformer;

import java.util.List;

/**
 * Orquesta el pipeline de medición before/after para evaluar el impacto
 * de la refactorización sobre la complejidad cognitiva estimada.
 * <p>
 * Flujo:
 * <ol>
 *   <li>Medir complejidad cognitiva del método original (before).</li>
 *   <li>Clonar el método para preservar el original.</li>
 *   <li>Detectar oportunidades de refactorización sobre el clon.</li>
 *   <li>Aplicar transformaciones.</li>
 *   <li>Medir complejidad cognitiva del clon transformado (after).</li>
 *   <li>Construir el registro {@link RefactoringImpact}.</li>
 * </ol>
 * <p>
 * Contribuye a RQ2: ¿Qué impacto tienen estas refactorizaciones en la
 * complejidad cognitiva?
 * <p>
 * <strong>NOTA:</strong> los valores de complejidad son estimaciones provisionales.
 * La validación final contra SonarQube/SonarLint forma parte del protocolo
 * experimental previsto en el TFM.
 */
public class RefactoringImpactAnalyzer {

    private final NestedIfDetector detector;
    private final NestedIfTransformer transformer;
    private final CognitiveComplexityCalculator calculator;

    public RefactoringImpactAnalyzer() {
        this.detector = new NestedIfDetector();
        this.transformer = new NestedIfTransformer();
        this.calculator = new CognitiveComplexityCalculator();
    }

    public RefactoringImpactAnalyzer(NestedIfDetector detector,
                                     NestedIfTransformer transformer,
                                     CognitiveComplexityCalculator calculator) {
        this.detector = detector;
        this.transformer = transformer;
        this.calculator = calculator;
    }

    /**
     * Analiza el impacto de la refactorización sobre un método.
     * El método original no se modifica.
     *
     * @param method el método a analizar
     * @return registro de impacto con before/after/delta
     */
    public RefactoringImpact analyze(MethodDeclaration method) {
        // 1. Before
        int before = calculator.calculate(method);

        // 2. Clonar para no modificar el original
        MethodDeclaration clone = method.clone();

        // 3. Detectar sobre el clon
        List<RefactoringOpportunity> opportunities = detector.detect(clone);
        int detected = opportunities.size();

        // 4. Transformar
        int applied = 0;
        for (RefactoringOpportunity opp : opportunities) {
            if (transformer.apply(opp)) {
                applied++;
            }
        }

        // 5. After
        int after = calculator.calculate(clone);

        // 6. Construir resultado
        String methodSig = method.getDeclarationAsString(false, false, false);
        return new RefactoringImpact(
                methodSig, before, after, detected, applied, clone.toString());
    }

    /**
     * Analiza el impacto sobre el primer método del código fuente proporcionado.
     *
     * @param sourceCode código Java completo (clase con al menos un método)
     * @return registro de impacto, o null si no se encuentra ningún método
     */
    public RefactoringImpact analyze(String sourceCode) {
        CompilationUnit cu = StaticJavaParser.parse(sourceCode);
        return cu.findFirst(MethodDeclaration.class)
                .map(this::analyze)
                .orElse(null);
    }
}
