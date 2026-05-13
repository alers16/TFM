package es.tfm.refactoring.analysis;

/**
 * Registro del impacto de una refactorización sobre la complejidad cognitiva
 * estimada de un método.
 * <p>
 * Diseñado para apoyar la respuesta a RQ2: ¿Qué impacto tienen estas
 * refactorizaciones en la complejidad cognitiva?
 * <p>
 * <strong>NOTA:</strong> los valores de complejidad son estimaciones provisionales
 * calculadas por {@link CognitiveComplexityCalculator}. Los resultados finales del
 * experimento deberán validarse contra SonarQube/SonarLint.
 */
public class RefactoringImpact {

    private final String methodSignature;
    private final int complexityBefore;
    private final int complexityAfter;
    private final int opportunitiesDetected;
    private final int opportunitiesApplied;
    private final String sourceAfter;

    public RefactoringImpact(String methodSignature,
                             int complexityBefore,
                             int complexityAfter,
                             int opportunitiesDetected,
                             int opportunitiesApplied,
                             String sourceAfter) {
        this.methodSignature = methodSignature;
        this.complexityBefore = complexityBefore;
        this.complexityAfter = complexityAfter;
        this.opportunitiesDetected = opportunitiesDetected;
        this.opportunitiesApplied = opportunitiesApplied;
        this.sourceAfter = sourceAfter;
    }

    public String getMethodSignature() {
        return methodSignature;
    }

    public int getComplexityBefore() {
        return complexityBefore;
    }

    public int getComplexityAfter() {
        return complexityAfter;
    }

    /** Diferencia after − before. Negativo indica reducción (mejora). */
    public int getDelta() {
        return complexityAfter - complexityBefore;
    }

    public int getOpportunitiesDetected() {
        return opportunitiesDetected;
    }

    public int getOpportunitiesApplied() {
        return opportunitiesApplied;
    }

    /** Código fuente del método tras la refactorización. */
    public String getSourceAfter() {
        return sourceAfter;
    }

    /** {@code true} si la refactorización redujo la complejidad estimada. */
    public boolean hasImprovement() {
        return getDelta() < 0;
    }

    @Override
    public String toString() {
        return "RefactoringImpact["
                + "method=" + methodSignature
                + ", before=" + complexityBefore
                + ", after=" + complexityAfter
                + ", delta=" + getDelta()
                + ", detected=" + opportunitiesDetected
                + ", applied=" + opportunitiesApplied
                + "]";
    }
}
