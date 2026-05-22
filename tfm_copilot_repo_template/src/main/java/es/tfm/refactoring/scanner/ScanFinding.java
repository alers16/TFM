package es.tfm.refactoring.scanner;

import es.tfm.refactoring.detection.DiscardReason;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Registro de un candidato detectado durante el análisis masivo de proyectos.
 * <p>
 * Cada instancia corresponde a un if externo encontrado dentro de un método.
 * El candidato puede haber sido aceptado (oportunidad de refactorización) o
 * rechazado (con los motivos de descarte detallados).
 * <p>
 * Contribuye a RQ1 (¿en qué casos se pueden combinar?) y a la ampliación
 * del corpus experimental.
 */
public class ScanFinding {

    private final String project;
    private final String file;           // ruta relativa desde la raíz del proyecto
    private final String className;
    private final String methodName;
    private final String methodSignature; // declaración compacta sin modificadores
    private final int candidateLine;
    private final boolean eligible;
    private final List<DiscardReason> discardReasons;
    private final int ccBefore;          // -1 si no elegible
    private final int ccAfter;           // -1 si no elegible
    private final int delta;             // 0 si no elegible
    private final String outerCondition; // "" si no elegible
    private final String innerCondition; // "" si no elegible

    public ScanFinding(String project,
                       String file,
                       String className,
                       String methodName,
                       String methodSignature,
                       int candidateLine,
                       boolean eligible,
                       List<DiscardReason> discardReasons,
                       int ccBefore,
                       int ccAfter,
                       int delta,
                       String outerCondition,
                       String innerCondition) {
        this.project = project;
        this.file = file;
        this.className = className;
        this.methodName = methodName;
        this.methodSignature = methodSignature;
        this.candidateLine = candidateLine;
        this.eligible = eligible;
        this.discardReasons = List.copyOf(discardReasons);
        this.ccBefore = ccBefore;
        this.ccAfter = ccAfter;
        this.delta = delta;
        this.outerCondition = outerCondition;
        this.innerCondition = innerCondition;
    }

    public String getProject() { return project; }
    public String getFile() { return file; }
    public String getClassName() { return className; }
    public String getMethodName() { return methodName; }
    public String getMethodSignature() { return methodSignature; }
    public int getCandidateLine() { return candidateLine; }
    public boolean isEligible() { return eligible; }
    public List<DiscardReason> getDiscardReasons() { return discardReasons; }
    public int getCcBefore() { return ccBefore; }
    public int getCcAfter() { return ccAfter; }
    public int getDelta() { return delta; }
    public String getOuterCondition() { return outerCondition; }
    public String getInnerCondition() { return innerCondition; }

    /** Motivos de descarte como cadena separada por ';'. */
    public String getDiscardReasonsString() {
        return discardReasons.stream()
                .map(DiscardReason::name)
                .collect(Collectors.joining(";"));
    }

    /** Cabecera CSV. */
    public static String csvHeader() {
        return "project,file,class,method,methodSignature,line,"
                + "eligible,discardReasons,ccBefore,ccAfter,delta,"
                + "outerCondition,innerCondition";
    }

    /** Fila CSV (campos con comas o comillas se encierran entre comillas dobles). */
    public String toCsvRow() {
        return csvQuote(project)
                + "," + csvQuote(file)
                + "," + csvQuote(className)
                + "," + csvQuote(methodName)
                + "," + csvQuote(methodSignature)
                + "," + candidateLine
                + "," + (eligible ? "YES" : "NO")
                + "," + csvQuote(getDiscardReasonsString())
                + "," + (eligible ? ccBefore : "")
                + "," + (eligible ? ccAfter : "")
                + "," + (eligible ? delta : "")
                + "," + csvQuote(truncate(outerCondition, 120))
                + "," + csvQuote(truncate(innerCondition, 120));
    }

    private static String csvQuote(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private static String truncate(String s, int max) {
        if (s == null) return "";
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
