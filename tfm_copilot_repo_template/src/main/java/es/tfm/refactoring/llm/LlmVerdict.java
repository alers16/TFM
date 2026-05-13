package es.tfm.refactoring.llm;

/**
 * Clasificación del resultado de evaluar una respuesta de LLM
 * en el contexto del protocolo experimental de RQ3.
 * <p>
 * Cada veredicto se asigna tras aplicar el oráculo de validación
 * ({@link LlmResponseValidator}) sobre la salida del modelo.
 * <p>
 * <strong>RQ3:</strong> ¿Pueden los grandes modelos de lenguaje realizar
 * esta refactorización de forma correcta y automática?
 */
public enum LlmVerdict {

    /**
     * La respuesta contiene código Java válido que aplica correctamente
     * la refactorización de condicionales anidados, reduce la complejidad
     * cognitiva y preserva la semántica.
     */
    SUCCESS,

    /**
     * La respuesta contiene código Java válido (parseable), pero la
     * transformación es incorrecta: no preserva la semántica, no reduce
     * la complejidad, o aplica una transformación distinta al patrón esperado.
     */
    INCORRECT,

    /**
     * La respuesta del LLM no contiene código Java parseable o no
     * sigue el formato de salida solicitado.
     */
    INVALID_OUTPUT,

    /**
     * El LLM se negó a realizar la refactorización o indicó que el caso
     * no es refactorizable. Este veredicto distingue entre rechazos
     * correctos (caso inelegible) y rechazos incorrectos (caso elegible).
     */
    REFUSED,

    /**
     * La respuesta aplica parcialmente la refactorización: algunos
     * condicionales se combinan correctamente pero otros no.
     * Solo aplicable a casos con múltiples niveles de anidamiento.
     */
    PARTIAL,

    /**
     * Error técnico en la evaluación: timeout, error de red, respuesta
     * vacía, u otro fallo no atribuible al contenido de la respuesta.
     */
    ERROR
}
