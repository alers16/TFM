package es.tfm.refactoring.llm;

import es.tfm.refactoring.experiment.ExperimentCase;

/**
 * Construye el prompt para enviar a un LLM solicitando la refactorización
 * de condicionales anidados en un caso experimental.
 * <p>
 * <strong>Principios de diseño:</strong>
 * <ul>
 *   <li>Prompt idéntico entre modelos — garantiza comparación justa.</li>
 *   <li>Instrucción centrada en el patrón del TFM — no pide optimización genérica.</li>
 *   <li>Exige preservación semántica explícitamente.</li>
 *   <li>Solicita salida estructurada (bloque de código) para facilitar parsing.</li>
 *   <li>Incluye opción de rechazo explícito para casos no elegibles.</li>
 * </ul>
 * <p>
 * <strong>RQ3:</strong> ¿Pueden los grandes modelos de lenguaje realizar
 * esta refactorización de forma correcta y automática?
 */
public class LlmPromptBuilder {

    /** Versión del prompt para trazabilidad experimental. */
    public static final String PROMPT_VERSION = "v1.0";

    private static final String SYSTEM_PROMPT =
            "You are a Java refactoring assistant. Your task is to analyze Java code "
            + "and apply a specific refactoring pattern: combining nested if-statements "
            + "into a single if with a compound condition using &&.\n\n"
            + "Rules:\n"
            + "1. Only combine nested ifs where the outer if has NO else branch, "
            + "the inner if has NO else branch, and the outer if's then-block contains "
            + "ONLY the inner if (no other statements).\n"
            + "2. Preserve the original evaluation order of conditions.\n"
            + "3. Do NOT alter the semantics of the code.\n"
            + "4. Do NOT change method signatures, return types, or other code "
            + "outside the if-statements.\n"
            + "5. If the code cannot be safely refactored with this pattern, respond "
            + "with exactly: NO_REFACTORING_APPLICABLE\n"
            + "6. If you apply the refactoring, return ONLY the complete refactored "
            + "method body wrapped in a Java class, inside a ```java code block.\n"
            + "7. Do not add comments, explanations, or annotations outside the code block "
            + "unless you are refusing with NO_REFACTORING_APPLICABLE.";

    private static final String USER_PROMPT_TEMPLATE =
            "Analyze the following Java code and apply the nested-if combination "
            + "refactoring where applicable. Return the complete refactored class "
            + "in a ```java code block, or respond with NO_REFACTORING_APPLICABLE "
            + "if no safe refactoring is possible.\n\n"
            + "```java\n%s\n```";

    /**
     * Construye el prompt de sistema (instrucciones generales, invariable entre casos).
     *
     * @return texto del prompt de sistema
     */
    public String buildSystemPrompt() {
        return SYSTEM_PROMPT;
    }

    /**
     * Construye el prompt de usuario para un caso experimental específico.
     *
     * @param experimentCase caso a refactorizar
     * @return texto del prompt de usuario con el código fuente incrustado
     */
    public String buildUserPrompt(ExperimentCase experimentCase) {
        return String.format(USER_PROMPT_TEMPLATE, experimentCase.getSourceCode());
    }

    /**
     * Construye el prompt completo (sistema + usuario) como texto único,
     * útil para modelos que no distinguen roles de sistema/usuario.
     *
     * @param experimentCase caso a refactorizar
     * @return prompt combinado
     */
    public String buildFullPrompt(ExperimentCase experimentCase) {
        return buildSystemPrompt() + "\n\n---\n\n" + buildUserPrompt(experimentCase);
    }

    /**
     * Devuelve la versión del prompt para trazabilidad.
     *
     * @return identificador de versión (e.g., "v1.0")
     */
    public String getPromptVersion() {
        return PROMPT_VERSION;
    }
}
