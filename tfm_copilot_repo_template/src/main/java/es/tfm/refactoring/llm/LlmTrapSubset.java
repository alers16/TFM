package es.tfm.refactoring.llm;

import java.util.List;

/**
 * Define el subconjunto de casos trampa para la evaluación de robustez de los LLMs en RQ3.
 * <p>
 * Los casos trampa son intencionalmente engañosos: parecen candidatos para la
 * refactorización if-if → if(&&), pero cada uno viola exactamente una precondición
 * de P1–P5. El objetivo es medir si los LLMs producen <em>falsos positivos</em>
 * (transformaciones incorrectas en casos no elegibles).
 * <p>
 * Complementa a {@link LlmEvaluationSubset} ampliando la cobertura de violaciones:
 * <ul>
 *   <li>TRAP_P4_INNER_ELSE — if interno con else (P4). P4 no estaba cubierto en la fase 9.</li>
 *   <li>TRAP_P2_MULTI_STATEMENT — then externo con dos sentencias (P2). P2 no estaba cubierto.</li>
 *   <li>TRAP_P5_ASSIGNMENT — asignación en condición interna (P5, variante no-method-call).
 *       Distinta del REAL_COMMONS_COLLECTIONS_GET donde P5 se activa por method call.</li>
 * </ul>
 */
public class LlmTrapSubset {

    /**
     * Archivos del corpus trampa que se incluyen en la evaluación.
     */
    public static List<String> trapFiles() {
        return List.of(
                "TrapP4InnerElse.java",
                "TrapP2MultiStatement.java",
                "TrapP5Assignment.java"
        );
    }

    /**
     * Identificadores de los casos trampa.
     */
    public static List<String> caseIds() {
        return List.of(
                "TRAP_P4_INNER_ELSE",
                "TRAP_P2_MULTI_STATEMENT",
                "TRAP_P5_ASSIGNMENT"
        );
    }

    /**
     * Todos los casos trampa son inelegibles por construcción.
     * Cualquier transformación no vacía producida por el LLM es un falso positivo.
     */
    public static List<String> ineligibleCaseIds() {
        return caseIds();
    }

    /** Número de casos trampa. */
    public static int size() {
        return caseIds().size();
    }
}
