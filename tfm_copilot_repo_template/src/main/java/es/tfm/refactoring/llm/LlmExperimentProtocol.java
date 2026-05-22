package es.tfm.refactoring.llm;

import java.util.Collections;
import java.util.List;

/**
 * Configuración del protocolo experimental para la evaluación de LLMs
 * en el contexto de RQ3.
 * <p>
 * Define los parámetros que deben mantenerse fijos durante una campaña
 * de evaluación para garantizar reproducibilidad y comparabilidad.
 * <p>
 * <strong>RQ3:</strong> ¿Pueden los grandes modelos de lenguaje realizar
 * esta refactorización de forma correcta y automática?
 */
public class LlmExperimentProtocol {

    private final List<String> models;
    private final double temperature;
    private final int attemptsPerCase;
    private final String promptVersion;
    private final int maxOutputTokens;

    /**
     * @param models          lista de identificadores de modelos a evaluar
     * @param temperature     temperatura del muestreo (0 = determinista)
     * @param attemptsPerCase intentos por caso y modelo
     * @param promptVersion   versión del prompt (para trazabilidad)
     * @param maxOutputTokens límite de tokens de salida
     */
    public LlmExperimentProtocol(List<String> models, double temperature,
                                 int attemptsPerCase, String promptVersion,
                                 int maxOutputTokens) {
        if (models == null || models.isEmpty()) {
            throw new IllegalArgumentException("Debe haber al menos un modelo");
        }
        if (temperature < 0 || temperature > 2) {
            throw new IllegalArgumentException(
                    "Temperatura debe estar entre 0 y 2: " + temperature);
        }
        if (attemptsPerCase < 1) {
            throw new IllegalArgumentException(
                    "Al menos 1 intento por caso: " + attemptsPerCase);
        }
        this.models = List.copyOf(models);
        this.temperature = temperature;
        this.attemptsPerCase = attemptsPerCase;
        this.promptVersion = promptVersion;
        this.maxOutputTokens = maxOutputTokens;
    }

    public List<String> getModels() { return models; }
    public double getTemperature() { return temperature; }
    public int getAttemptsPerCase() { return attemptsPerCase; }
    public String getPromptVersion() { return promptVersion; }
    public int getMaxOutputTokens() { return maxOutputTokens; }

    /** Número total de invocaciones: modelos × casos × intentos. */
    public int totalInvocations(int caseCount) {
        return models.size() * caseCount * attemptsPerCase;
    }

    /**
     * Protocolo por defecto para la evaluación formal de RQ3.
     * <ul>
     *   <li>Modelos: GPT-4o, GPT-4.1 (dos modelos OpenAI)</li>
     *   <li>Temperatura: 0 (determinista)</li>
     *   <li>3 intentos por caso</li>
     *   <li>Prompt v1.0</li>
     *   <li>Máximo 2048 tokens de salida</li>
     * </ul>
     */
    public static LlmExperimentProtocol defaultProtocol() {
        return new LlmExperimentProtocol(
                List.of("gpt-4o", "gpt-4.1"),
                0.0,
                3,
                LlmPromptBuilder.PROMPT_VERSION,
                2048
        );
    }

    /**
     * Protocolo exploratorio OpenAI-only.
     * <p>
     * Mantiene <strong>congelados</strong> los mismos parámetros que
     * {@link #defaultProtocol()} (temperatura, intentos, prompt, max tokens),
     * pero restringe la lista de modelos a {@code gpt-4o}. Se usa cuando
     * solo hay credenciales de OpenAI disponibles. <strong>No sustituye</strong>
     * el protocolo formal de dos modelos.
     */
    public static LlmExperimentProtocol openAiOnlyExploratoryProtocol() {
        return new LlmExperimentProtocol(
                List.of("gpt-4o"),
                0.0,
                3,
                LlmPromptBuilder.PROMPT_VERSION,
                2048
        );
    }

    /**
     * Protocolo con prompt v2.0 (few-shot) para la evaluación comparativa de prompts.
     * <p>
     * Mantiene los mismos parámetros que {@link #defaultProtocol()} salvo la versión
     * del prompt, que pasa a {@code v2.0} con ejemplos few-shot incrustados en el
     * system prompt. El objetivo es comparar si los ejemplos mejoran la tasa de
     * corrección respecto al prompt zero-shot v1.0.
     */
    public static LlmExperimentProtocol promptV2Protocol() {
        return new LlmExperimentProtocol(
                List.of("gpt-4o", "gpt-4.1"),
                0.0,
                3,
                LlmPromptBuilder.PROMPT_VERSION_V2,
                2048
        );
    }

    @Override
    public String toString() {
        return String.format(java.util.Locale.US,
                "Protocol[models=%s, temp=%.1f, attempts=%d, prompt=%s, maxTokens=%d]",
                models, temperature, attemptsPerCase, promptVersion, maxOutputTokens);
    }
}
