package es.tfm.refactoring.experiment;

/**
 * Representa un caso de entrada para el pipeline experimental de refactorización.
 * <p>
 * Cada caso contiene el código fuente de un método Java envuelto en una clase,
 * junto con metadatos de trazabilidad (identificador, origen, descripción).
 */
public class ExperimentCase {

    private final String caseId;
    private final String origin;
    private final String description;
    private final String sourceCode;

    /**
     * @param caseId      identificador único del caso (e.g., "VALID_SIMPLE")
     * @param origin      procedencia del caso (e.g., "synthetic", "commons-lang")
     * @param description descripción breve del caso y su propósito experimental
     * @param sourceCode  código fuente Java completo (clase con al menos un método)
     */
    public ExperimentCase(String caseId, String origin,
                          String description, String sourceCode) {
        this.caseId = caseId;
        this.origin = origin;
        this.description = description;
        this.sourceCode = sourceCode;
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

    public String getSourceCode() {
        return sourceCode;
    }

    @Override
    public String toString() {
        return "ExperimentCase[" + caseId + "]";
    }
}
