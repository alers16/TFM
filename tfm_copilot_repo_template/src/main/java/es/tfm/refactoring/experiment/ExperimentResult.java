package es.tfm.refactoring.experiment;

import java.util.Collections;
import java.util.List;

/**
 * Resultado de ejecutar el pipeline de refactorización sobre un caso experimental.
 * <p>
 * Contiene todos los datos necesarios para responder a RQ1 (¿en qué casos se pueden
 * combinar condicionales anidados?) y RQ2 (¿qué impacto tienen en la complejidad
 * cognitiva?).
 * <p>
 * <strong>NOTA:</strong> los valores de complejidad cognitiva son estimaciones
 * provisionales calculadas por el prototipo. Los resultados finales del experimento
 * deberán validarse contra SonarQube/SonarLint.
 */
public class ExperimentResult {

    private final String caseId;
    private final String origin;
    private final String description;
    private final boolean eligible;
    private final String discardReason;
    private final int opportunitiesDetected;
    private final int opportunitiesApplied;
    private final int totalPasses;
    private final int complexityBefore;
    private final int complexityAfter;
    private final int delta;
    private final String methodSignature;
    private final String sourceBefore;
    private final String sourceAfter;
    private final String observations;
    private final List<String> discardCategories;

    public ExperimentResult(String caseId, String origin, String description,
                            boolean eligible, String discardReason,
                            int opportunitiesDetected, int opportunitiesApplied,
                            int totalPasses,
                            int complexityBefore, int complexityAfter, int delta,
                            String methodSignature,
                            String sourceBefore, String sourceAfter,
                            String observations) {
        this(caseId, origin, description, eligible, discardReason,
                opportunitiesDetected, opportunitiesApplied, totalPasses,
                complexityBefore, complexityAfter, delta,
                methodSignature, sourceBefore, sourceAfter,
                observations, Collections.emptyList());
    }

    public ExperimentResult(String caseId, String origin, String description,
                            boolean eligible, String discardReason,
                            int opportunitiesDetected, int opportunitiesApplied,
                            int totalPasses,
                            int complexityBefore, int complexityAfter, int delta,
                            String methodSignature,
                            String sourceBefore, String sourceAfter,
                            String observations,
                            List<String> discardCategories) {
        this.caseId = caseId;
        this.origin = origin;
        this.description = description;
        this.eligible = eligible;
        this.discardReason = discardReason;
        this.opportunitiesDetected = opportunitiesDetected;
        this.opportunitiesApplied = opportunitiesApplied;
        this.totalPasses = totalPasses;
        this.complexityBefore = complexityBefore;
        this.complexityAfter = complexityAfter;
        this.delta = delta;
        this.methodSignature = methodSignature;
        this.sourceBefore = sourceBefore;
        this.sourceAfter = sourceAfter;
        this.observations = observations;
        this.discardCategories = discardCategories != null
                ? List.copyOf(discardCategories)
                : Collections.emptyList();
    }

    public String getCaseId() {
        return caseId;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDescription() {
        return description;
    }

    /** {@code true} si se detectó al menos una oportunidad de refactorización. */
    public boolean isEligible() {
        return eligible;
    }

    /** Motivo del descarte, o {@code null} si el caso es elegible. */
    public String getDiscardReason() {
        return discardReason;
    }

    /** Total acumulado de oportunidades detectadas en todos los pases. */
    public int getOpportunitiesDetected() {
        return opportunitiesDetected;
    }

    /** Total acumulado de transformaciones aplicadas en todos los pases. */
    public int getOpportunitiesApplied() {
        return opportunitiesApplied;
    }

    /** Número de pases de punto fijo ejecutados. */
    public int getTotalPasses() {
        return totalPasses;
    }

    /** Complejidad cognitiva estimada (aproximación provisional) antes de refactorizar. */
    public int getComplexityBefore() {
        return complexityBefore;
    }

    /** Complejidad cognitiva estimada (aproximación provisional) después de refactorizar. */
    public int getComplexityAfter() {
        return complexityAfter;
    }

    /** Diferencia after − before. Negativo indica reducción (mejora). */
    public int getDelta() {
        return delta;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    /** Código fuente del método antes de la refactorización. */
    public String getSourceBefore() {
        return sourceBefore;
    }

    /** Código fuente del método después de la refactorización. */
    public String getSourceAfter() {
        return sourceAfter;
    }

    public String getObservations() {
        return observations;
    }

    /**
     * Categorías de descarte específicas encontradas durante el análisis.
     * Vacía si el caso es elegible.
     * <p>
     * Los valores corresponden a nombres de {@code DiscardReason} del detector.
     */
    public List<String> getDiscardCategories() {
        return discardCategories;
    }

    /** {@code true} si la refactorización redujo la complejidad estimada. */
    public boolean hasImprovement() {
        return delta < 0;
    }

    @Override
    public String toString() {
        return "ExperimentResult[case=" + caseId
                + ", eligible=" + eligible
                + ", before=" + complexityBefore
                + ", after=" + complexityAfter
                + ", delta=" + delta
                + ", detected=" + opportunitiesDetected
                + ", applied=" + opportunitiesApplied
                + ", passes=" + totalPasses
                + "]";
    }
}
