package es.tfm.refactoring.experiment;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import es.tfm.refactoring.analysis.CognitiveComplexityCalculator;
import es.tfm.refactoring.detection.DetectionResult;
import es.tfm.refactoring.detection.DiscardReason;
import es.tfm.refactoring.detection.NestedIfDetector;
import es.tfm.refactoring.detection.RefactoringOpportunity;
import es.tfm.refactoring.transformation.NestedIfTransformer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Ejecuta el pipeline de refactorización sobre un lote de casos experimentales
 * y produce resultados trazables para RQ1 y RQ2.
 * <p>
 * <strong>Política de múltiples oportunidades: una transformación por pase.</strong>
 * <p>
 * Cada pase:
 * <ol>
 *   <li>Detecta todas las oportunidades sobre el estado actual del AST.</li>
 *   <li>Aplica únicamente la primera oportunidad detectada.</li>
 *   <li>Vuelve a detectar desde cero.</li>
 * </ol>
 * <p>
 * Se detiene cuando no se detectan más oportunidades o se alcanza {@link #MAX_PASSES}.
 * <p>
 * <strong>Justificación del cambio respecto a la versión anterior:</strong>
 * La política anterior aplicaba todas las oportunidades de un pase simultáneamente,
 * lo que causaba sobreconteo por nodos huérfanos en el AST cuando transformaciones
 * de un mismo pase afectaban a nodos anidados. La nueva política aplica una sola
 * transformación por pase y re-detecta, garantizando que cada conteo corresponde
 * a una transformación real verificada sobre el AST vigente.
 * <p>
 * <strong>Consecuencia:</strong> {@code opportunitiesApplied} es ahora un conteo
 * exacto de transformaciones realmente aplicadas. No hay sobreconteo posible.
 * <p>
 * <strong>NOTA:</strong> los valores de complejidad son estimaciones provisionales
 * del prototipo, no equivalentes a SonarQube/SonarLint.
 */
public class BatchRunner {

    /** Límite de seguridad para iteraciones de punto fijo. */
    static final int MAX_PASSES = 10;

    private final NestedIfDetector detector;
    private final NestedIfTransformer transformer;
    private final CognitiveComplexityCalculator calculator;

    public BatchRunner() {
        this.detector = new NestedIfDetector();
        this.transformer = new NestedIfTransformer();
        this.calculator = new CognitiveComplexityCalculator();
    }

    public BatchRunner(NestedIfDetector detector,
                       NestedIfTransformer transformer,
                       CognitiveComplexityCalculator calculator) {
        this.detector = detector;
        this.transformer = transformer;
        this.calculator = calculator;
    }

    /**
     * Ejecuta el pipeline sobre todos los casos y devuelve resultados.
     *
     * @param cases lista de casos experimentales
     * @return lista de resultados (mismo orden que la entrada)
     */
    public List<ExperimentResult> run(List<ExperimentCase> cases) {
        List<ExperimentResult> results = new ArrayList<>();
        for (ExperimentCase experimentCase : cases) {
            results.add(processCase(experimentCase));
        }
        return results;
    }

    /**
     * Procesa un caso individual: parseo → análisis con motivos → medición before →
     * punto fijo (una transformación por pase) → medición after → resultado.
     */
    ExperimentResult processCase(ExperimentCase experimentCase) {
        // 1. Parseo
        CompilationUnit cu;
        try {
            cu = StaticJavaParser.parse(experimentCase.getSourceCode());
        } catch (Exception e) {
            return buildIneligible(experimentCase,
                    "Error de parseo: " + e.getMessage(),
                    List.of());
        }

        // 2. Buscar primer método
        Optional<MethodDeclaration> methodOpt = cu.findFirst(MethodDeclaration.class);
        if (methodOpt.isEmpty()) {
            return buildIneligible(experimentCase,
                    "No se encontró ningún método",
                    List.of());
        }

        MethodDeclaration original = methodOpt.get();
        String sourceBefore = original.toString();
        String methodSig = original.getDeclarationAsString(false, false, false);

        // 3. Análisis detallado para recopilar motivos de descarte
        List<DetectionResult> detailedResults =
                detector.detectWithReasons(original);
        Set<String> discardCategories = new LinkedHashSet<>();
        for (DetectionResult dr : detailedResults) {
            if (!dr.isAccepted()) {
                for (DiscardReason reason : dr.getDiscardReasons()) {
                    discardCategories.add(reason.name());
                }
            }
        }

        // 4. Medición before (estimación provisional)
        int complexityBefore = calculator.calculate(original);

        // 5. Clonar para preservar el original
        MethodDeclaration working = original.clone();

        // 6. Aplicación iterativa: una transformación por pase hasta punto fijo
        int totalApplied = 0;
        int passes = 0;

        while (passes < MAX_PASSES) {
            List<RefactoringOpportunity> opportunities = detector.detect(working);
            if (opportunities.isEmpty()) {
                break;
            }
            passes++;

            // Aplicar solo la primera oportunidad, luego re-detectar
            RefactoringOpportunity first = opportunities.get(0);
            if (transformer.apply(first)) {
                totalApplied++;
            }
        }

        // 7. Elegibilidad
        boolean eligible = totalApplied > 0;
        String discardReason = eligible
                ? null
                : summarizeDiscardReasons(discardCategories);

        // 8. Medición after (estimación provisional)
        int complexityAfter = calculator.calculate(working);
        int delta = complexityAfter - complexityBefore;

        String sourceAfter = working.toString();

        // 9. Observaciones
        String observations = buildObservations(
                passes, totalApplied, eligible);

        return new ExperimentResult(
                experimentCase.getCaseId(),
                experimentCase.getOrigin(),
                experimentCase.getDescription(),
                eligible,
                discardReason,
                totalApplied, // detected == applied in one-at-a-time policy
                totalApplied,
                passes,
                complexityBefore,
                complexityAfter,
                delta,
                methodSig,
                sourceBefore,
                sourceAfter,
                observations,
                new ArrayList<>(discardCategories)
        );
    }

    private ExperimentResult buildIneligible(ExperimentCase ec, String reason,
                                             List<String> categories) {
        return new ExperimentResult(
                ec.getCaseId(),
                ec.getOrigin(),
                ec.getDescription(),
                false,
                reason,
                0, 0, 0,
                -1, -1, 0,
                null,
                null, null,
                reason,
                categories
        );
    }

    private String summarizeDiscardReasons(Set<String> categories) {
        if (categories.isEmpty()) {
            return "No se detectaron oportunidades de refactorización";
        }
        return "Descartado por: " + String.join(", ", categories);
    }

    private String buildObservations(int passes, int applied, boolean eligible) {
        if (!eligible) {
            return "Sin oportunidades detectadas";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Pases hasta punto fijo: ").append(passes);
        sb.append(". Transformaciones aplicadas: ").append(applied);
        if (passes > 1) {
            sb.append(". [Nota: se requirieron múltiples pases por anidamiento profundo]");
        }
        if (passes >= MAX_PASSES) {
            sb.append(". [ADVERTENCIA: se alcanzó el límite de ")
              .append(MAX_PASSES).append(" pases]");
        }
        return sb.toString();
    }
}
