package es.tfm.refactoring.llm;

import java.time.Instant;

/**
 * Registro completo de una invocación individual al LLM durante la campaña
 * experimental de RQ3, incluyendo trazabilidad temporal y de versión.
 * <p>
 * Extiende la información de {@link LlmEvaluationResult} con metadatos
 * de ejecución necesarios para la replicación del experimento.
 */
public class LlmInvocationRecord {

    private final LlmEvaluationResult evaluationResult;
    private final String modelVersion;
    private final Instant executionTimestamp;
    private final String promptSent;
    private final long responseTimeMs;

    public LlmInvocationRecord(LlmEvaluationResult evaluationResult,
                               String modelVersion,
                               Instant executionTimestamp,
                               String promptSent,
                               long responseTimeMs) {
        this.evaluationResult = evaluationResult;
        this.modelVersion = modelVersion;
        this.executionTimestamp = executionTimestamp;
        this.promptSent = promptSent;
        this.responseTimeMs = responseTimeMs;
    }

    public LlmEvaluationResult getEvaluationResult() { return evaluationResult; }
    public String getModelVersion() { return modelVersion; }
    public Instant getExecutionTimestamp() { return executionTimestamp; }
    public String getPromptSent() { return promptSent; }
    public long getResponseTimeMs() { return responseTimeMs; }

    public String getCaseId() { return evaluationResult.getCaseId(); }
    public String getModel() { return evaluationResult.getModel(); }
    public int getAttemptNumber() { return evaluationResult.getAttemptNumber(); }
    public LlmVerdict getVerdict() { return evaluationResult.getVerdict(); }

    @Override
    public String toString() {
        return String.format("Invocation[%s/%s/%s #%d] %s @%s",
                evaluationResult.getCaseId(),
                evaluationResult.getModel(),
                modelVersion,
                evaluationResult.getAttemptNumber(),
                evaluationResult.getVerdict(),
                executionTimestamp);
    }
}
