package es.tfm.refactoring.llm;

import java.util.Collections;
import java.util.List;

/**
 * Resultado de evaluar una respuesta de LLM para un caso experimental,
 * con todos los datos necesarios para comparar con el baseline determinista.
 * <p>
 * Estructura diseñada para responder a RQ3 de forma trazable y comparable.
 * Cada instancia documenta un intento individual del LLM sobre un caso.
 * <p>
 * <strong>RQ3:</strong> ¿Pueden los grandes modelos de lenguaje realizar
 * esta refactorización de forma correcta y automática?
 */
public class LlmEvaluationResult {

    private final String caseId;
    private final String model;
    private final String promptVersion;
    private final int attemptNumber;
    private final String rawLlmOutput;
    private final String extractedCode;
    private final LlmVerdict verdict;
    private final String verdictReason;
    private final boolean parseable;
    private final int complexityBefore;
    private final int complexityAfter;
    private final int delta;
    private final int baselineDelta;
    private final boolean matchesBaseline;
    private final List<String> validationErrors;
    private final String observations;

    /**
     * @param caseId           identificador del caso experimental
     * @param model            modelo LLM evaluado (e.g., "gpt-4o")
     * @param promptVersion    versión del prompt utilizado (e.g., "v1.0")
     * @param attemptNumber    número de intento (1..N)
     * @param rawLlmOutput     salida completa del LLM (sin procesar)
     * @param extractedCode    código Java extraído de la respuesta, o null
     * @param verdict          clasificación del resultado
     * @param verdictReason    justificación del veredicto
     * @param parseable        true si el código extraído es parseable por JavaParser
     * @param complexityBefore CC estimada antes (del caso original)
     * @param complexityAfter  CC estimada después (de la salida del LLM), o -1
     * @param delta            cambio de CC (after - before), o 0 si no aplica
     * @param baselineDelta    delta del prototipo determinista para comparación
     * @param matchesBaseline  true si el LLM produce el mismo delta que el baseline
     * @param validationErrors lista de errores detectados por el oráculo
     * @param observations     notas adicionales sobre el resultado
     */
    public LlmEvaluationResult(String caseId, String model, String promptVersion,
                               int attemptNumber,
                               String rawLlmOutput, String extractedCode,
                               LlmVerdict verdict, String verdictReason,
                               boolean parseable,
                               int complexityBefore, int complexityAfter, int delta,
                               int baselineDelta, boolean matchesBaseline,
                               List<String> validationErrors,
                               String observations) {
        this.caseId = caseId;
        this.model = model;
        this.promptVersion = promptVersion;
        this.attemptNumber = attemptNumber;
        this.rawLlmOutput = rawLlmOutput;
        this.extractedCode = extractedCode;
        this.verdict = verdict;
        this.verdictReason = verdictReason;
        this.parseable = parseable;
        this.complexityBefore = complexityBefore;
        this.complexityAfter = complexityAfter;
        this.delta = delta;
        this.baselineDelta = baselineDelta;
        this.matchesBaseline = matchesBaseline;
        this.validationErrors = validationErrors != null
                ? List.copyOf(validationErrors) : Collections.emptyList();
        this.observations = observations;
    }

    public String getCaseId() { return caseId; }
    public String getModel() { return model; }
    public String getPromptVersion() { return promptVersion; }
    public int getAttemptNumber() { return attemptNumber; }
    public String getRawLlmOutput() { return rawLlmOutput; }
    public String getExtractedCode() { return extractedCode; }
    public LlmVerdict getVerdict() { return verdict; }
    public String getVerdictReason() { return verdictReason; }
    public boolean isParseable() { return parseable; }
    public int getComplexityBefore() { return complexityBefore; }
    public int getComplexityAfter() { return complexityAfter; }
    public int getDelta() { return delta; }
    public int getBaselineDelta() { return baselineDelta; }
    public boolean isMatchesBaseline() { return matchesBaseline; }
    public List<String> getValidationErrors() { return validationErrors; }
    public String getObservations() { return observations; }

    /** True si el LLM produjo una transformación válida y exitosa. */
    public boolean isSuccess() {
        return verdict == LlmVerdict.SUCCESS;
    }

    @Override
    public String toString() {
        return String.format("LlmEval[%s/%s #%d] %s delta=%d baseline=%d match=%s",
                caseId, model, attemptNumber, verdict,
                delta, baselineDelta, matchesBaseline);
    }
}
