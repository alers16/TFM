package es.tfm.refactoring.detection;

import java.util.Collections;
import java.util.List;

/**
 * Resultado del análisis de un candidato a refactorización.
 * <p>
 * Si el candidato cumple todas las precondiciones, contiene una
 * {@link RefactoringOpportunity}. Si no, contiene la lista de motivos
 * de descarte específicos ({@link DiscardReason}).
 * <p>
 * Permite trazabilidad experimental: cada if analizado produce un registro
 * que explica por qué fue aceptado o rechazado.
 */
public class DetectionResult {

    private final RefactoringOpportunity opportunity;
    private final List<DiscardReason> discardReasons;
    private final int candidateLine;

    private DetectionResult(RefactoringOpportunity opportunity,
                            List<DiscardReason> discardReasons,
                            int candidateLine) {
        this.opportunity = opportunity;
        this.discardReasons = discardReasons;
        this.candidateLine = candidateLine;
    }

    /** Crea un resultado aceptado (todas las precondiciones se cumplen). */
    static DetectionResult accepted(RefactoringOpportunity opportunity) {
        return new DetectionResult(
                opportunity,
                Collections.emptyList(),
                opportunity.getStartLine());
    }

    /** Crea un resultado rechazado con motivos específicos. */
    static DetectionResult rejected(List<DiscardReason> reasons, int candidateLine) {
        return new DetectionResult(null, List.copyOf(reasons), candidateLine);
    }

    /** {@code true} si el candidato fue aceptado como oportunidad válida. */
    public boolean isAccepted() {
        return opportunity != null;
    }

    /** La oportunidad detectada, o {@code null} si fue rechazado. */
    public RefactoringOpportunity getOpportunity() {
        return opportunity;
    }

    /** Motivos de descarte (vacía si fue aceptado). */
    public List<DiscardReason> getDiscardReasons() {
        return discardReasons;
    }

    /** Línea del if candidato analizado. */
    public int getCandidateLine() {
        return candidateLine;
    }

    @Override
    public String toString() {
        if (isAccepted()) {
            return "DetectionResult[ACCEPTED, line=" + candidateLine + "]";
        }
        return "DetectionResult[REJECTED, line=" + candidateLine
                + ", reasons=" + discardReasons + "]";
    }
}
