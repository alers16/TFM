package es.tfm.refactoring.llm;

/**
 * Interfaz funcional para proveer respuestas de LLM durante la campaña.
 * <p>
 * Permite desacoplar la ejecución de la campaña de la fuente de respuestas:
 * <ul>
 *   <li>Pre-grabadas desde archivos (ejecución reproducible).</li>
 *   <li>API real en tiempo de ejecución.</li>
 *   <li>Simuladas para tests.</li>
 * </ul>
 */
@FunctionalInterface
public interface LlmResponseProvider {

    /**
     * Obtiene la respuesta de un LLM para una invocación específica.
     *
     * @param model   identificador del modelo (e.g., "gpt-4o")
     * @param caseId  identificador del caso experimental
     * @param attempt número de intento (1..N)
     * @param prompt  prompt completo enviado al modelo
     * @return respuesta completa del modelo, o null si no disponible
     */
    String getResponse(String model, String caseId, int attempt, String prompt);
}
