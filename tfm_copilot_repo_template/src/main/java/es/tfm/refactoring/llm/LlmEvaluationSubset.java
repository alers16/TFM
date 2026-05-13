package es.tfm.refactoring.llm;

import java.util.List;

/**
 * Define el subconjunto de evaluación para RQ3: los casos del corpus
 * existente que se usarán para evaluar la capacidad de los LLMs.
 * <p>
 * <strong>Criterios de selección:</strong>
 * <ul>
 *   <li>Representatividad: incluye casos elegibles, parciales y no elegibles.</li>
 *   <li>Variedad estructural: patrón simple, null-check, loop, triple anidamiento,
 *       else, method call guard.</li>
 *   <li>Trazabilidad: todos los casos provienen del corpus ya validado.</li>
 *   <li>Cobertura de motivos de descarte: P1 (else), P2 (multi-statement), P5 (method call).</li>
 * </ul>
 * <p>
 * El subconjunto incluye 6 casos:
 * <ol>
 *   <li><strong>PILOT_VALID_SIMPLE</strong> — Elegible simple: if+if sin else,
 *       condiciones puras. Caso base mínimo.</li>
 *   <li><strong>PILOT_VALID_NESTED_IN_LOOP</strong> — Elegible en contexto de loop:
 *       anidamiento dentro de for. Mayor impacto en CC.</li>
 *   <li><strong>REAL_COMMONS_MATH_CONVERGED</strong> — Elegible real: doble condición
 *       numérica. Representativo de código OSS real.</li>
 *   <li><strong>REAL_ANT_MATCH_PATH</strong> — Elegible parcial: triple if,
 *       primer par combinable, tercer nivel rechazado por P5.</li>
 *   <li><strong>REAL_COMMONS_MATH_VALIDATE_RANGE</strong> — No elegible (P1):
 *       outer if tiene rama else. El LLM debe rechazar.</li>
 *   <li><strong>REAL_COMMONS_COLLECTIONS_GET</strong> — No elegible (P5):
 *       condición interna con containsKey (method call). El LLM debe rechazar
 *       o al menos preservar semántica.</li>
 * </ol>
 * <p>
 * <strong>Justificación del tamaño:</strong> 6 casos permiten una evaluación
 * piloto manejable (6 × 2 modelos × 3 intentos = 36 invocaciones) mientras
 * cubren todos los ejes de variación relevantes para RQ3.
 */
public class LlmEvaluationSubset {

    /**
     * Archivos del corpus piloto incluidos en el subconjunto RQ3.
     */
    public static List<String> pilotFiles() {
        return List.of(
                "PilotValidSimple.java",
                "PilotValidNestedInLoop.java"
        );
    }

    /**
     * Archivos del corpus real incluidos en el subconjunto RQ3.
     */
    public static List<String> realFiles() {
        return List.of(
                "RealCommonsMathConverged.java",
                "RealAntMatchPath.java",
                "RealCommonsMathValidateRange.java",
                "RealCommonsCollectionsGet.java"
        );
    }

    /**
     * Identificadores de los casos del subconjunto.
     */
    public static List<String> caseIds() {
        return List.of(
                "PILOT_VALID_SIMPLE",
                "PILOT_VALID_NESTED_IN_LOOP",
                "REAL_COMMONS_MATH_CONVERGED",
                "REAL_ANT_MATCH_PATH",
                "REAL_COMMONS_MATH_VALIDATE_RANGE",
                "REAL_COMMONS_COLLECTIONS_GET"
        );
    }

    /**
     * Identificadores de los casos que el prototipo determinista clasifica
     * como elegibles (incluyendo elegibles parciales).
     */
    public static List<String> eligibleCaseIds() {
        return List.of(
                "PILOT_VALID_SIMPLE",
                "PILOT_VALID_NESTED_IN_LOOP",
                "REAL_COMMONS_MATH_CONVERGED",
                "REAL_ANT_MATCH_PATH"
        );
    }

    /**
     * Identificadores de los casos que el prototipo determinista clasifica
     * como no elegibles.
     */
    public static List<String> ineligibleCaseIds() {
        return List.of(
                "REAL_COMMONS_MATH_VALIDATE_RANGE",
                "REAL_COMMONS_COLLECTIONS_GET"
        );
    }

    /** Número total de casos en el subconjunto. */
    public static int size() {
        return caseIds().size();
    }
}
