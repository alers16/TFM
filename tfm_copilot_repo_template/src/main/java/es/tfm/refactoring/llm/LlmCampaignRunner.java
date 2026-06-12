package es.tfm.refactoring.llm;

import es.tfm.refactoring.experiment.BatchRunner;
import es.tfm.refactoring.experiment.ExperimentCase;
import es.tfm.refactoring.experiment.ExperimentResult;
import es.tfm.refactoring.experiment.PilotCorpusLoader;
import es.tfm.refactoring.experiment.RealDatasetLoader;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ejecutor de la campaña experimental de RQ3: orquesta la carga de casos,
 * el cómputo de baselines, la invocación (o carga) de respuestas LLM
 * y la validación con el oráculo.
 * <p>
 * No realiza llamadas directas a APIs de LLM. En su lugar, recibe las
 * respuestas a través de un {@link LlmResponseProvider}, lo que permite:
 * <ul>
 *   <li>Ejecución con respuestas pre-grabadas (reproducible).</li>
 *   <li>Ejecución con API real cuando se disponga de claves.</li>
 *   <li>Tests con respuestas simuladas.</li>
 * </ul>
 * <p>
 * <strong>RQ3:</strong> ¿Pueden los grandes modelos de lenguaje realizar
 * esta refactorización de forma correcta y automática?
 */
public class LlmCampaignRunner {

    private final LlmPromptBuilder promptBuilder;
    private final LlmResponseValidator validator;
    private final BatchRunner batchRunner;

    public LlmCampaignRunner() {
        this.promptBuilder = new LlmPromptBuilder();
        this.validator = new LlmResponseValidator();
        this.batchRunner = new BatchRunner();
    }

    /**
     * Constructor con {@link BatchRunner} explícito.
     * <p>
     * Permite re-evaluar la campaña con un baseline determinista calculado en
     * un modo de detección distinto (p.ej. {@link es.tfm.refactoring.detection.DetectionMode#STRUCTURAL})
     * sin alterar el flujo por defecto, que sigue usando STRICT vía el
     * {@code BatchRunner()} sin argumentos.
     * <p>
     * El {@code promptBuilder} y el oráculo {@link LlmResponseValidator} se
     * mantienen idénticos: solo cambia la fuente del baseline (elegibilidad y Δ)
     * con la que el oráculo compara las respuestas del LLM.
     *
     * @param batchRunner pipeline determinista usado para computar baselines
     */
    public LlmCampaignRunner(BatchRunner batchRunner) {
        this.promptBuilder = new LlmPromptBuilder();
        this.validator = new LlmResponseValidator();
        this.batchRunner = batchRunner;
    }

    /**
     * Ejecuta la campaña completa según el protocolo sobre el subset definido.
     *
     * @param protocol         configuración del protocolo experimental
     * @param responseProvider  proveedor de respuestas LLM (pre-grabadas o en vivo)
     * @param modelVersions    mapa modelo → versión exacta utilizada
     * @return lista de registros de invocación con resultados completos
     * @throws IOException si falla la carga del corpus
     */
    public List<LlmInvocationRecord> executeCampaign(
            LlmExperimentProtocol protocol,
            LlmResponseProvider responseProvider,
            Map<String, String> modelVersions) throws IOException {

        Map<String, CaseWithBaseline> cases = loadCasesWithBaselines();
        return executeProtocol(protocol, responseProvider, modelVersions, cases);
    }

    /**
     * Ejecuta la campaña sobre un conjunto de casos pre-cargados.
     * <p>
     * Útil para subcampañas con corpus distintos del subset estándar
     * (p.ej. corpus trampa, evaluación de prompt v2.0).
     *
     * @param protocol         configuración del protocolo experimental
     * @param responseProvider  proveedor de respuestas LLM
     * @param modelVersions    mapa modelo → versión exacta utilizada
     * @param cases            casos ya cargados con sus baselines
     * @return lista de registros de invocación con resultados completos
     */
    public List<LlmInvocationRecord> executeCampaign(
            LlmExperimentProtocol protocol,
            LlmResponseProvider responseProvider,
            Map<String, String> modelVersions,
            Map<String, CaseWithBaseline> cases) {

        return executeProtocol(protocol, responseProvider, modelVersions, cases);
    }

    /**
     * Núcleo de la campaña: itera modelos × casos × intentos respetando el protocolo.
     * Selecciona el prompt según la versión indicada en el protocolo.
     */
    private List<LlmInvocationRecord> executeProtocol(
            LlmExperimentProtocol protocol,
            LlmResponseProvider responseProvider,
            Map<String, String> modelVersions,
            Map<String, CaseWithBaseline> cases) {

        List<LlmInvocationRecord> records = new ArrayList<>();
        boolean useV2 = LlmPromptBuilder.PROMPT_VERSION_V2.equals(
                protocol.getPromptVersion());

        for (String model : protocol.getModels()) {
            String modelVersion = modelVersions.getOrDefault(model, model);

            for (Map.Entry<String, CaseWithBaseline> entry : cases.entrySet()) {
                String caseId = entry.getKey();
                CaseWithBaseline cwb = entry.getValue();

                String prompt = useV2
                        ? promptBuilder.buildFullPromptV2(cwb.experimentCase)
                        : promptBuilder.buildFullPrompt(cwb.experimentCase);

                for (int attempt = 1; attempt <= protocol.getAttemptsPerCase(); attempt++) {
                    Instant before = Instant.now();

                    String rawResponse;
                    String technicalError = null;
                    try {
                        rawResponse = responseProvider.getResponse(
                                model, caseId, attempt, prompt);
                    } catch (Exception e) {
                        rawResponse = null;
                        technicalError = buildTechnicalErrorMessage(e);
                    }

                    long elapsed = java.time.Duration.between(before, Instant.now())
                            .toMillis();

                    LlmEvaluationResult evalResult;
                    if (rawResponse == null) {
                        evalResult = buildErrorResult(caseId, model,
                                protocol.getPromptVersion(), attempt,
                                cwb.baseline, technicalError);
                    } else {
                        evalResult = validator.evaluate(
                                caseId, model,
                                protocol.getPromptVersion(), attempt,
                                rawResponse,
                                cwb.experimentCase.getSourceCode(),
                                cwb.baseline);
                    }

                    records.add(new LlmInvocationRecord(
                            evalResult, modelVersion, before,
                            prompt, elapsed));
                }
            }
        }

        return records;
    }

    /**
     * Carga los 6 casos del subset y computa el baseline determinista de cada uno.
     */
    public Map<String, CaseWithBaseline> loadCasesWithBaselines() throws IOException {
        PilotCorpusLoader pilotLoader = new PilotCorpusLoader();
        RealDatasetLoader realLoader = new RealDatasetLoader();

        List<ExperimentCase> pilotCases = pilotLoader.load(
                LlmEvaluationSubset.pilotFiles());
        List<ExperimentCase> realCases = realLoader.load(
                LlmEvaluationSubset.realFiles());

        List<ExperimentCase> allCases = new ArrayList<>();
        allCases.addAll(pilotCases);
        allCases.addAll(realCases);

        Map<String, CaseWithBaseline> result = new LinkedHashMap<>();
        for (ExperimentCase ec : allCases) {
            ExperimentResult baseline = batchRunner.run(List.of(ec)).get(0);
            result.put(ec.getCaseId(), new CaseWithBaseline(ec, baseline));
        }
        return result;
    }

    public Map<String, CaseWithBaseline> loadAllCasesWithBaselines() throws IOException {
        PilotCorpusLoader pilotLoader = new PilotCorpusLoader();
        RealDatasetLoader realLoader = new RealDatasetLoader();

        List<ExperimentCase> pilotCases = pilotLoader.load(PilotCorpusLoader.standardPilotFiles());
        List<ExperimentCase> realCases = realLoader.load(RealDatasetLoader.standardRealFiles());

        List<ExperimentCase> allCases = new ArrayList<>();
        allCases.addAll(pilotCases);
        allCases.addAll(realCases);

        Map<String, CaseWithBaseline> result = new LinkedHashMap<>();
        for (ExperimentCase ec : allCases) {
            ExperimentResult baseline = batchRunner.run(List.of(ec)).get(0);
            result.put(ec.getCaseId(), new CaseWithBaseline(ec, baseline));
        }
        return result;
    }

    private LlmEvaluationResult buildErrorResult(String caseId, String model,
                                                  String promptVersion,
                                                  int attemptNumber,
                                                  ExperimentResult baseline,
                                                  String technicalError) {
        String detail = technicalError == null || technicalError.isBlank()
                ? "No response from model"
                : technicalError;
        return new LlmEvaluationResult(
                caseId, model, promptVersion, attemptNumber,
                null, null, LlmVerdict.ERROR,
                "Error tecnico: no se obtuvo respuesta del modelo. Detalle: " + detail,
                false,
                baseline.getComplexityBefore(), -1, 0,
                baseline.getDelta(), false,
                List.of(detail), null);
    }

    private String buildTechnicalErrorMessage(Exception e) {
        Throwable root = e;
        while (root.getCause() != null) {
            root = root.getCause();
        }
        String rootMessage = root.getMessage();
        if (rootMessage == null || rootMessage.isBlank()) {
            rootMessage = root.getClass().getSimpleName();
        }
        return e.getClass().getSimpleName() + ": " + rootMessage;
    }

    /**
     * Par caso+baseline para uso interno durante la campaña.
     */
    public static class CaseWithBaseline {
        private final ExperimentCase experimentCase;
        private final ExperimentResult baseline;

        public CaseWithBaseline(ExperimentCase experimentCase,
                                ExperimentResult baseline) {
            this.experimentCase = experimentCase;
            this.baseline = baseline;
        }

        public ExperimentCase getExperimentCase() { return experimentCase; }
        public ExperimentResult getBaseline() { return baseline; }
    }
}
