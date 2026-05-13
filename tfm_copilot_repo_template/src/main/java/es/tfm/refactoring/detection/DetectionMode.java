package es.tfm.refactoring.detection;

/**
 * Modo de detección del {@link NestedIfDetector}.
 * <p>
 * Permite comparar dos regímenes experimentales para RQ1 y RQ2:
 * <ul>
 *   <li>{@link #STRICT} — MVP original con P1–P5 completas.</li>
 *   <li>{@link #RELAXED} — MVP con P5 parcialmente relajada (P5'),
 *       que acepta llamadas a métodos cuyo nombre figura en
 *       {@link MethodCallAllowlist}.</li>
 * </ul>
 * <p>
 * El modo RELAXED no modifica P1–P4; únicamente amplía el conjunto de
 * expresiones de condición que se consideran libres de side effects.
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
    RELAXED
}
