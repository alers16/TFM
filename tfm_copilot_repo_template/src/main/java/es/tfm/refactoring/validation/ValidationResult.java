package es.tfm.refactoring.validation;

/**
 * Resultado de compilar el código refactorizado generado por el prototipo.
 * <p>
 * La validación dinámica comprueba que la salida del transformador sea
 * Java sintácticamente correcto y compilable por {@code javac}. Es la
 * evidencia mínima de que la transformación no produce código roto.
 * <p>
 * Estados posibles:
 * <ul>
 *   <li>{@link Status#PASS} — compilación sin errores.</li>
 *   <li>{@link Status#FAIL} — javac reportó errores.</li>
 *   <li>{@link Status#SKIPPED} — el caso no fue elegible (no se transformó).</li>
 *   <li>{@link Status#ERROR} — fallo de infraestructura (compilador no disponible,
 *       I/O, etc.). No implica que el código sea incorrecto.</li>
 * </ul>
 */
public final class ValidationResult {

    public enum Status { PASS, FAIL, SKIPPED, ERROR }

    private final String caseId;
    private final Status status;
    private final String diagnostics;   // errores de javac, o descripción del error de infraestructura

    private ValidationResult(String caseId, Status status, String diagnostics) {
        this.caseId      = caseId;
        this.status      = status;
        this.diagnostics = diagnostics;
    }

    // -------------------------------------------------------------------------
    // Factories
    // -------------------------------------------------------------------------

    public static ValidationResult pass(String caseId) {
        return new ValidationResult(caseId, Status.PASS, "");
    }

    public static ValidationResult fail(String caseId, String diagnostics) {
        return new ValidationResult(caseId, Status.FAIL, diagnostics);
    }

    public static ValidationResult skipped(String caseId) {
        return new ValidationResult(caseId, Status.SKIPPED, "caso no elegible");
    }

    public static ValidationResult error(String caseId, String reason) {
        return new ValidationResult(caseId, Status.ERROR, reason);
    }

    // -------------------------------------------------------------------------
    // Accessors
    // -------------------------------------------------------------------------

    public String getCaseId()      { return caseId;      }
    public Status getStatus()      { return status;       }
    public String getDiagnostics() { return diagnostics;  }

    public boolean isPassed()  { return status == Status.PASS;    }
    public boolean isFailed()  { return status == Status.FAIL;    }
    public boolean isSkipped() { return status == Status.SKIPPED; }
    public boolean isError()   { return status == Status.ERROR;   }

    @Override
    public String toString() {
        return "ValidationResult[" + caseId + " → " + status
                + (diagnostics.isBlank() ? "" : ": " + diagnostics) + "]";
    }
}
