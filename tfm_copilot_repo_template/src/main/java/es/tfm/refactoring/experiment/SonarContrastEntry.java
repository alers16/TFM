package es.tfm.refactoring.experiment;

/**
 * Entrada de contraste entre la complejidad cognitiva estimada por el prototipo
 * y la medida oficial de SonarQube/SonarLint para un caso experimental.
 * <p>
 * Este registro permite documentar y analizar la desviación entre la estimación
 * provisional del {@link es.tfm.refactoring.analysis.CognitiveComplexityCalculator}
 * y el valor de referencia oficial.
 * <p>
 * <strong>Valores Sonar:</strong> se obtienen mediante SonarLint (IDE) o
 * sonar-scanner (CLI) siguiendo el procedimiento documentado en
 * {@code docs/sonar-contrast-procedure.md}. Un valor {@code -1} indica
 * que la verificación Sonar aún no se ha realizado.
 */
public class SonarContrastEntry {

    /** Valor que indica que la medición Sonar no se ha realizado aún. */
    public static final int NOT_VERIFIED = -1;

    private final String caseId;
    private final int provisionalBefore;
    private final int provisionalAfter;
    private final int sonarBefore;
    private final int sonarAfter;
    private final String notes;

    /**
     * @param caseId             identificador del caso experimental
     * @param provisionalBefore  CC estimada antes de refactorizar (prototipo)
     * @param provisionalAfter   CC estimada después de refactorizar (prototipo)
     * @param sonarBefore        CC oficial Sonar antes de refactorizar (-1 si no verificado)
     * @param sonarAfter         CC oficial Sonar después de refactorizar (-1 si no verificado)
     * @param notes              observaciones sobre la discrepancia o procedimiento
     */
    public SonarContrastEntry(String caseId,
                              int provisionalBefore, int provisionalAfter,
                              int sonarBefore, int sonarAfter,
                              String notes) {
        this.caseId = caseId;
        this.provisionalBefore = provisionalBefore;
        this.provisionalAfter = provisionalAfter;
        this.sonarBefore = sonarBefore;
        this.sonarAfter = sonarAfter;
        this.notes = notes;
    }

    public String getCaseId() { return caseId; }
    public int getProvisionalBefore() { return provisionalBefore; }
    public int getProvisionalAfter() { return provisionalAfter; }
    public int getSonarBefore() { return sonarBefore; }
    public int getSonarAfter() { return sonarAfter; }
    public String getNotes() { return notes; }

    /** {@code true} si se han proporcionado ambos valores Sonar (before y after). */
    public boolean isFullyVerified() {
        return sonarBefore != NOT_VERIFIED && sonarAfter != NOT_VERIFIED;
    }

    /** Desviación absoluta en el valor before, o -1 si no verificado. */
    public int deviationBefore() {
        if (sonarBefore == NOT_VERIFIED) return NOT_VERIFIED;
        return Math.abs(provisionalBefore - sonarBefore);
    }

    /** Desviación absoluta en el valor after, o -1 si no verificado. */
    public int deviationAfter() {
        if (sonarAfter == NOT_VERIFIED) return NOT_VERIFIED;
        return Math.abs(provisionalAfter - sonarAfter);
    }

    /** Delta provisional (after - before). */
    public int provisionalDelta() {
        return provisionalAfter - provisionalBefore;
    }

    /** Delta Sonar (after - before), o {@link #NOT_VERIFIED} si alguno no verificado. */
    public int sonarDelta() {
        if (!isFullyVerified()) return NOT_VERIFIED;
        return sonarAfter - sonarBefore;
    }

    /** {@code true} si el delta provisional coincide con el delta Sonar. */
    public boolean deltaAgrees() {
        if (!isFullyVerified()) return false;
        return provisionalDelta() == sonarDelta();
    }

    @Override
    public String toString() {
        return String.format(
                "SonarContrast[%s] prov=%d→%d sonar=%s→%s",
                caseId, provisionalBefore, provisionalAfter,
                sonarBefore == NOT_VERIFIED ? "?" : String.valueOf(sonarBefore),
                sonarAfter == NOT_VERIFIED ? "?" : String.valueOf(sonarAfter));
    }
}
