package es.tfm.refactoring.detection;

/**
 * Modo de detección del {@link NestedIfDetector}.
 * <p>
 * Permite comparar tres regímenes experimentales para RQ1 y RQ2:
 * <ul>
 *   <li>{@link #STRICT} — MVP original con P1–P5 completas.</li>
 *   <li>{@link #RELAXED} — MVP con P5 parcialmente relajada (P5'),
 *       que acepta llamadas a métodos cuyo nombre figura en
 *       {@link MethodCallAllowlist}.</li>
 *   <li>{@link #STRUCTURAL} — solo las precondiciones estructurales P1–P4;
 *       omite por completo P5 (efectos colaterales en las condiciones).</li>
 * </ul>
 * <p>
 * Ni RELAXED ni STRUCTURAL modifican P1–P4: RELAXED únicamente amplía el
 * conjunto de expresiones de condición que se consideran libres de side
 * effects, y STRUCTURAL prescinde de esa comprobación por completo.
 */
public enum DetectionMode {

    /**
     * Modo estricto: P1–P5 originales.
     * Toda llamada a método en una condición se considera potencialmente
     * insegura y descarta el candidato.
     */
    STRICT,

    /**
     * Modo relajado (P5'): P1–P4 intactas + P5 con allowlist.
     * Las llamadas a métodos cuyo nombre pertenece a
     * {@link MethodCallAllowlist#PURE_METHOD_NAMES} se aceptan como
     * presumiblemente puras bajo la convención JDK estándar.
     * <p>
     * Precaución: la pureza se asume por nombre de método, no por análisis
     * interprocedural. Esta es una heurística conservadora, documentada como
     * tal en la memoria del TFM.
     */
    RELAXED,

    /**
     * Modo estructural: aplica únicamente las precondiciones estructurales
     * P1–P4 y OMITE por completo P5 (ausencia de efectos colaterales en las
     * condiciones).
     * <p>
     * Las cinco categorías de P5 (llamadas a método, asignaciones, ++/--,
     * lambdas e instanciaciones con {@code new}) NO se evalúan en este modo:
     * un par de {@code if} anidados que cumpla P1–P4 se considera ELEGIBLE
     * aunque sus condiciones contengan dichas construcciones.
     * <p>
     * <strong>Justificación de corrección.</strong> La transformación
     * {@code if (A) { if (B) { S } } -> if (A && B) { S }} es un <em>unfold</em>
     * de la semántica de cortocircuito de {@code &&}: dado que
     * {@code A && B ≡ A ? B : false}, B solo se evalúa cuando A es verdadera,
     * exactamente igual que en la forma anidada. Por tanto el orden de
     * evaluación y la condicionalidad de evaluación de A y B se preservan con
     * independencia de los efectos colaterales que A o B puedan producir. De
     * ahí que P5 NO sea necesaria para la corrección y baste con las
     * precondiciones estructurales P1–P4 (decisión metodológica confirmada con
     * el tutor).
     * <p>
     * P4 se evalúa aquí de forma idéntica a {@link #STRICT}: cualquier rama
     * else del if interno (incluso vacía) descarta el candidato. La relajación
     * P4' (else vacío) es exclusiva de {@link #RELAXED}.
     */
    STRUCTURAL
}
