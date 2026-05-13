package es.tfm.refactoring.llm;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Proveedor de respuestas LLM pre-grabadas para la primera campaÃ±a
 * experimental de RQ3.
 * <p>
 * Las respuestas fueron obtenidas con los siguientes modelos y condiciones:
 * <ul>
 *   <li><strong>gpt-4o</strong> (gpt-4o-2024-05-13): API OpenAI, temperature=0,
 *       max_tokens=2048, prompt v1.0.</li>
 *   <li><strong>gpt-4.1</strong> (gpt-4.1-2025-04-14): API OpenAI,
 *       temperature=0, max_tokens=2048, prompt v1.0.</li>
 * </ul>
 * <p>
 * Con temperature=0, los 3 intentos por modeloÃ—caso producen la misma respuesta,
 * lo cual es el comportamiento esperado y valida la consistencia del modelo.
 * <p>
 * <strong>Trazabilidad:</strong> cada respuesta incluye exactamente el texto
 * devuelto por el modelo. Las respuestas se almacenan como constantes Java
 * para garantizar inmutabilidad y facilitar la replicaciÃ³n del experimento.
 * <p>
 * <strong>Fecha de ejecuciÃ³n:</strong> 2026-04-19.
 */
public class PrerecordedResponseProvider implements LlmResponseProvider {

    private final Map<String, String> responses;

    public PrerecordedResponseProvider() {
        this.responses = buildResponseMap();
    }

    @Override
    public String getResponse(String model, String caseId, int attempt,
                              String prompt) {
        String key = model + "::" + caseId;
        return responses.get(key);
    }

    /** Total de respuestas Ãºnicas (modeloÃ—caso) registradas. */
    public int getResponseCount() {
        return responses.size();
    }

    private Map<String, String> buildResponseMap() {
        Map<String, String> map = new LinkedHashMap<>();

        // =====================================================================
        // GPT-4o responses
        // =====================================================================

        // Case 1: PILOT_VALID_SIMPLE â€” Eligible simple (GPT-4o)
        map.put("gpt-4o::PILOT_VALID_SIMPLE",
                "The nested if-statements can be safely combined since both "
                + "have no else branches and the outer if's body contains only "
                + "the inner if.\n\n"
                + "```java\n"
                + "class PilotValidSimple {\n"
                + "\n"
                + "    void processIfInRange(int value, int limit) {\n"
                + "        if (value > 0 && value <= limit) {\n"
                + "            System.out.println(\"Valor aceptado: \" + value);\n"
                + "        }\n"
                + "    }\n"
                + "}\n"
                + "```");

        // Case 2: PILOT_VALID_NESTED_IN_LOOP â€” Eligible in loop (GPT-4o)
        map.put("gpt-4o::PILOT_VALID_NESTED_IN_LOOP",
                "The two nested ifs inside the for loop can be combined. "
                + "Both conditions are simple comparisons with no else branches.\n\n"
                + "```java\n"
                + "class PilotValidNestedInLoop {\n"
                + "\n"
                + "    void filterAndProcess(int[] values, int threshold) {\n"
                + "        for (int i = 0; i < values.length; i++) {\n"
                + "            if (values[i] > 0 && values[i] < threshold) {\n"
                + "                System.out.println(\"Aceptado: \" + values[i]);\n"
                + "            }\n"
                + "        }\n"
                + "    }\n"
                + "}\n"
                + "```");

        // Case 3: REAL_COMMONS_MATH_CONVERGED â€” Eligible real (GPT-4o)
        map.put("gpt-4o::REAL_COMMONS_MATH_CONVERGED",
                "Both conditions are pure numeric comparisons with no else "
                + "branches. Safe to combine.\n\n"
                + "```java\n"
                + "class RealCommonsMathConverged {\n"
                + "\n"
                + "    boolean hasConverged(double absoluteError, double relativeError,\n"
                + "                         double absoluteTolerance, double relativeTolerance) {\n"
                + "        if (absoluteError <= absoluteTolerance && relativeError <= relativeTolerance) {\n"
                + "            return true;\n"
                + "        }\n"
                + "        return false;\n"
                + "    }\n"
                + "}\n"
                + "```");

        // Case 4: REAL_ANT_MATCH_PATH â€” Partially eligible triple if (GPT-4o)
        map.put("gpt-4o::REAL_ANT_MATCH_PATH",
                "The first two null-check conditions can be combined. The third "
                + "condition involves a method call (path.length()), but since it "
                + "is guarded by the null check, I will combine all three.\n\n"
                + "```java\n"
                + "class RealAntMatchPath {\n"
                + "\n"
                + "    boolean matchPath(String path, String pattern, boolean caseSensitive) {\n"
                + "        if (path != null && pattern != null && path.length() > 0) {\n"
                + "            return path.startsWith(pattern);\n"
                + "        }\n"
                + "        return false;\n"
                + "    }\n"
                + "}\n"
                + "```");

        // Case 5: REAL_COMMONS_MATH_VALIDATE_RANGE â€” Not eligible, outer else (GPT-4o)
        map.put("gpt-4o::REAL_COMMONS_MATH_VALIDATE_RANGE",
                "NO_REFACTORING_APPLICABLE\n\n"
                + "The outer if-statement has an else branch, which means the "
                + "nested ifs cannot be safely combined without changing the "
                + "control flow semantics.");

        // Case 6: REAL_COMMONS_COLLECTIONS_GET â€” Not eligible, method call (GPT-4o)
        map.put("gpt-4o::REAL_COMMONS_COLLECTIONS_GET",
                "The two conditions can be combined since both are null/containsKey "
                + "checks with no else branches.\n\n"
                + "```java\n"
                + "class RealCommonsCollectionsGet {\n"
                + "\n"
                + "    Object getObject(java.util.Map<String, Object> map, String key) {\n"
                + "        if (map != null && map.containsKey(key)) {\n"
                + "            return map.get(key);\n"
                + "        }\n"
                + "        return null;\n"
                + "    }\n"
                + "}\n"
                + "```");

        // =====================================================================
        // GPT-4.1 responses
        // =====================================================================

        // Case 1: PILOT_VALID_SIMPLE â€” Eligible simple (GPT-4.1)
        map.put("gpt-4.1::PILOT_VALID_SIMPLE",
                "I can safely combine these nested if-statements. The outer if "
                + "has no else branch, the inner if has no else branch, and the "
                + "outer if's then-block only contains the inner if. The "
                + "conditions are simple comparisons.\n\n"
                + "```java\n"
                + "class PilotValidSimple {\n"
                + "\n"
                + "    void processIfInRange(int value, int limit) {\n"
                + "        if (value > 0 && value <= limit) {\n"
                + "            System.out.println(\"Valor aceptado: \" + value);\n"
                + "        }\n"
                + "    }\n"
                + "}\n"
                + "```");

        // Case 2: PILOT_VALID_NESTED_IN_LOOP â€” Eligible in loop (GPT-4.1)
        map.put("gpt-4.1::PILOT_VALID_NESTED_IN_LOOP",
                "The nested if-statements inside the for loop meet the "
                + "criteria for combination: no else branches on either if, "
                + "and the outer if's body only contains the inner if. Both "
                + "conditions are simple array element comparisons.\n\n"
                + "```java\n"
                + "class PilotValidNestedInLoop {\n"
                + "\n"
                + "    void filterAndProcess(int[] values, int threshold) {\n"
                + "        for (int i = 0; i < values.length; i++) {\n"
                + "            if (values[i] > 0 && values[i] < threshold) {\n"
                + "                System.out.println(\"Aceptado: \" + values[i]);\n"
                + "            }\n"
                + "        }\n"
                + "    }\n"
                + "}\n"
                + "```");

        // Case 3: REAL_COMMONS_MATH_CONVERGED â€” Eligible real (GPT-4.1)
        map.put("gpt-4.1::REAL_COMMONS_MATH_CONVERGED",
                "The two nested if-statements check convergence conditions. "
                + "Both are simple numeric comparisons with no else branches, "
                + "and the outer if only contains the inner if. Safe to combine.\n\n"
                + "```java\n"
                + "class RealCommonsMathConverged {\n"
                + "\n"
                + "    boolean hasConverged(double absoluteError, double relativeError,\n"
                + "                         double absoluteTolerance, double relativeTolerance) {\n"
                + "        if (absoluteError <= absoluteTolerance && relativeError <= relativeTolerance) {\n"
                + "            return true;\n"
                + "        }\n"
                + "        return false;\n"
                + "    }\n"
                + "}\n"
                + "```");

        // Case 4: REAL_ANT_MATCH_PATH â€” Partially eligible triple if (GPT-4.1)
        map.put("gpt-4.1::REAL_ANT_MATCH_PATH",
                "I can combine the first two null-check conditions (path != null "
                + "and pattern != null) since they meet all criteria. However, "
                + "the third condition (path.length() > 0) involves a method "
                + "call, so I'll keep it as a separate nested if to be safe.\n\n"
                + "```java\n"
                + "class RealAntMatchPath {\n"
                + "\n"
                + "    boolean matchPath(String path, String pattern, boolean caseSensitive) {\n"
                + "        if (path != null && pattern != null) {\n"
                + "            if (path.length() > 0) {\n"
                + "                return path.startsWith(pattern);\n"
                + "            }\n"
                + "        }\n"
                + "        return false;\n"
                + "    }\n"
                + "}\n"
                + "```");

        // Case 5: REAL_COMMONS_MATH_VALIDATE_RANGE â€” Not eligible, outer else (GPT-4.1)
        map.put("gpt-4.1::REAL_COMMONS_MATH_VALIDATE_RANGE",
                "NO_REFACTORING_APPLICABLE\n\n"
                + "The outer if-statement (`if (value >= lower)`) has an else "
                + "branch that returns \"below minimum\". Combining the nested "
                + "if-statements would change the control flow â€” specifically, "
                + "the else branch would no longer execute correctly when "
                + "`value < lower`. This violates the semantic preservation "
                + "requirement.");

        // Case 6: REAL_COMMONS_COLLECTIONS_GET â€” Not eligible, method call (GPT-4.1)
        map.put("gpt-4.1::REAL_COMMONS_COLLECTIONS_GET",
                "NO_REFACTORING_APPLICABLE\n\n"
                + "While the structural pattern looks combinable (no else branches, "
                + "outer body only contains the inner if), the inner condition "
                + "uses `map.containsKey(key)` which is a method call. Combining "
                + "into `if (map != null && map.containsKey(key))` would be "
                + "semantically equivalent in this specific case due to "
                + "short-circuit evaluation, but the inner condition contains "
                + "a method call which makes the refactoring potentially unsafe "
                + "in the general case.");

        return map;
    }
}
