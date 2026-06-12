package es.tfm.refactoring.llm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import es.tfm.refactoring.analysis.CognitiveComplexityCalculator;
import es.tfm.refactoring.detection.DetectionMode;
import es.tfm.refactoring.detection.NestedIfDetector;
import es.tfm.refactoring.experiment.BatchRunner;
import es.tfm.refactoring.transformation.NestedIfTransformer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Re-evaluador AISLADO de la campaña RQ3 con baseline en modo
 * {@link DetectionMode#STRUCTURAL} (P1–P4, omite P5), REUTILIZANDO las
 * respuestas de LLM ya grabadas en una campaña previa.
 * <p>
 * <strong>Motivación.</strong> El oráculo de RQ3 ({@link LlmResponseValidator})
 * compara cada respuesta del LLM contra un baseline determinista por caso
 * (elegibilidad y Δ de complejidad cognitiva) producido por el pipeline
 * determinista ({@link BatchRunner}). La campaña original (fase 9) calculó ese
 * baseline en modo {@link DetectionMode#STRICT} (P1–P5). Sin embargo, el prompt
 * de RQ3 codifica únicamente las precondiciones estructurales P1–P4 (omite P5,
 * porque el cortocircuito de {@code &&} preserva el comportamiento). Para
 * alinear oráculo y prompt, este executor recomputa el baseline en modo
 * STRUCTURAL y vuelve a aplicar el oráculo sobre las MISMAS respuestas grabadas,
 * sin llamar a la API.
 * <p>
 * <strong>Aislamiento.</strong> No toca el flujo STRICT/live de
 * {@link CampaignExecutor}. Reutiliza:
 * <ul>
 *   <li>{@link RecordedEvidenceResponseProvider} para servir las respuestas
 *       crudas grabadas (solo lectura).</li>
 *   <li>{@link LlmCampaignRunner#LlmCampaignRunner(BatchRunner)} para inyectar un
 *       {@link BatchRunner} con detector STRUCTURAL (única diferencia respecto al
 *       flujo por defecto, que usa STRICT).</li>
 *   <li>{@link LlmCampaignExporter} para emitir los MISMOS artefactos que la
 *       campaña (full-evidence.json, summary.csv, aggregated.md, run-metadata).</li>
 * </ul>
 * <p>
 * <strong>Precondición de fidelidad.</strong> El corpus cargado por
 * {@link LlmCampaignRunner#loadAllCasesWithBaselines()} debe ser el mismo que
 * generó la evidencia (mismos {@code caseId}). De lo contrario, los casos sin
 * respuesta grabada se registran como ERROR técnico (no se inventan respuestas).
 * <p>
 * Uso:
 * <ul>
 *   <li>{@code java es.tfm.refactoring.llm.Rq3StructuralReevalExecutor}</li>
 *   <li>{@code java es.tfm.refactoring.llm.Rq3StructuralReevalExecutor <evidenceJson> <outputDir>}</li>
 * </ul>
 * Valores por defecto:
 * {@code output/rq3-campaign-real-phase9/rq3-full-evidence.json} → {@code output/rq3-structural/}.
 */
public class Rq3StructuralReevalExecutor {

    private static final String DEFAULT_EVIDENCE =
            "output/rq3-campaign-real-phase9/rq3-full-evidence.json";
    private static final String DEFAULT_OUTPUT = "output/rq3-structural";
    private static final String FILE_PREFIX = "rq3-structural-";
    private static final String CAMPAIGN_TYPE = "structural-reeval-rq3";

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    public static void main(String[] args) throws IOException {
        String evidenceArg = (args.length > 0 && !args[0].isBlank())
                ? args[0] : DEFAULT_EVIDENCE;
        String outputArg = (args.length > 1 && !args[1].isBlank())
                ? args[1] : DEFAULT_OUTPUT;

        Path evidencePath = Path.of(evidenceArg);
        Path outputPath = Path.of(outputArg);
        Files.createDirectories(outputPath);
        Instant startedAt = Instant.now();

        System.out.println("=== RQ3 Structural Re-evaluation Executor (baseline P1-P4) ===");
        System.out.println("Baseline mode: " + DetectionMode.STRUCTURAL);
        System.out.println("Recorded evidence: " + evidencePath.toAbsolutePath());
        System.out.println("Output directory:  " + outputPath.toAbsolutePath());
        System.out.println();

        // 1. Protocolo: el mismo de la campaña fase 9 (prompt v1.0, 2 modelos,
        //    temp 0, 3 intentos). No se llama a la API: el provider es de lectura.
        LlmExperimentProtocol protocol = LlmExperimentProtocol.defaultProtocol();
        System.out.println("Protocol: " + protocol);

        // 2. Proveedor de respuestas grabadas (solo lectura, sin API).
        RecordedEvidenceResponseProvider provider;
        try {
            provider = new RecordedEvidenceResponseProvider(evidencePath);
        } catch (IOException e) {
            System.err.println("No se pudo cargar la evidencia grabada: " + e.getMessage());
            throw e;
        }
        System.out.println("Provider: RecordedEvidenceResponseProvider");
        System.out.println("Recorded invocations: " + provider.getRecordCount()
                + " (case×model pairs: " + provider.getCaseModelCount() + ")");
        System.out.println();

        // 3. Runner con baseline determinista en modo STRUCTURAL (P1-P4).
        //    El oráculo (LlmResponseValidator) es idéntico al del flujo por
        //    defecto; solo cambia el modo del detector usado para el baseline.
        BatchRunner structuralBatchRunner = new BatchRunner(
                new NestedIfDetector(DetectionMode.STRUCTURAL),
                new NestedIfTransformer(),
                new CognitiveComplexityCalculator());
        LlmCampaignRunner runner = new LlmCampaignRunner(structuralBatchRunner);

        // 4. Reconstruir el baseline sobre el corpus completo (mismos casos que
        //    generaron la evidencia) y re-evaluar reutilizando las respuestas.
        Map<String, LlmCampaignRunner.CaseWithBaseline> cases =
                runner.loadAllCasesWithBaselines();
        System.out.println("Cases with STRUCTURAL baseline: " + cases.size());
        Map<String, String> modelVersions = Map.of(
                "gpt-4o", "gpt-4o",
                "gpt-4.1", "gpt-4.1");

        List<LlmInvocationRecord> results = runner.executeCampaign(
                protocol, provider, modelVersions, cases);

        System.out.println("Invocations re-evaluated: " + results.size());
        System.out.println();

        // 5. Reporte agregado.
        LlmCampaignReport report = new LlmCampaignReport(results, protocol);
        System.out.println(report.toSummaryString());

        long technicalErrors = results.stream()
                .filter(r -> r.getVerdict() == LlmVerdict.ERROR)
                .count();
        long missingResponses = results.stream()
                .filter(r -> r.getEvaluationResult().getRawLlmOutput() == null)
                .count();
        String status = technicalErrors > 0 ? "completed-with-incidents" : "completed";
        String statusDetail = "technicalErrors=" + technicalErrors
                + "; missingRecordedResponses=" + missingResponses;

        // 6. Exportar los mismos artefactos que la campaña.
        LlmCampaignExporter exporter = new LlmCampaignExporter();
        exporter.exportFullEvidenceJson(results,
                outputPath.resolve(FILE_PREFIX + "full-evidence.json"));
        exporter.exportSummaryCsv(results,
                outputPath.resolve(FILE_PREFIX + "summary.csv"));
        exporter.exportAggregatedMarkdown(report,
                outputPath.resolve(FILE_PREFIX + "aggregated.md"));
        exportRunMetadata(outputPath, startedAt, protocol, modelVersions,
                results.size(), status, statusDetail, cases.size(),
                evidencePath, provider.getRecordCount());
        exportTechnicalIncidents(outputPath, results);

        System.out.println("\nExported to: " + outputPath.toAbsolutePath());
        System.out.println("  - " + FILE_PREFIX + "full-evidence.json");
        System.out.println("  - " + FILE_PREFIX + "summary.csv");
        System.out.println("  - " + FILE_PREFIX + "aggregated.md");
        System.out.println("  - " + FILE_PREFIX + "run-metadata.json");
        System.out.println("  - " + FILE_PREFIX + "incidents.md");
    }

    private static void exportRunMetadata(Path outputPath, Instant startedAt,
                                          LlmExperimentProtocol protocol,
                                          Map<String, String> modelVersions,
                                          int completedInvocations,
                                          String status, String statusDetail,
                                          int caseCount, Path evidencePath,
                                          int recordedInvocations)
            throws IOException {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("campaignPhase", "Phase 9 re-eval");
        metadata.put("campaignType", CAMPAIGN_TYPE);
        metadata.put("executionMode", "reeval-recorded");
        metadata.put("baselineMode", DetectionMode.STRUCTURAL.name());
        metadata.put("oracleUnchanged", true);
        metadata.put("apiCalled", false);
        metadata.put("sourceEvidence", evidencePath.toAbsolutePath().toString());
        metadata.put("recordedInvocationsLoaded", recordedInvocations);
        metadata.put("status", status);
        metadata.put("statusDetail", statusDetail);
        metadata.put("startedAt", startedAt.toString());
        metadata.put("protocolFrozen", true);
        metadata.put("protocol", Map.of(
                "models", protocol.getModels(),
                "temperature", protocol.getTemperature(),
                "attemptsPerCase", protocol.getAttemptsPerCase(),
                "promptVersion", protocol.getPromptVersion(),
                "maxOutputTokens", protocol.getMaxOutputTokens(),
                "caseCount", caseCount,
                "expectedInvocations", protocol.totalInvocations(caseCount)
        ));
        metadata.put("modelVersions", modelVersions);
        metadata.put("completedInvocations", completedInvocations);
        metadata.put("note", "Re-evaluacion del oraculo RQ3 con baseline STRUCTURAL "
                + "(P1-P4, P5 omitida; el cortocircuito de && preserva el "
                + "comportamiento). Reutiliza las respuestas crudas grabadas; "
                + "NO se llamo a la API. El oraculo (LlmResponseValidator) es "
                + "identico al del flujo por defecto: solo cambia el modo de "
                + "deteccion del baseline.");
        Files.writeString(outputPath.resolve(FILE_PREFIX + "run-metadata.json"),
                GSON.toJson(metadata), StandardCharsets.UTF_8);
    }

    private static void exportTechnicalIncidents(Path outputPath,
                                                 List<LlmInvocationRecord> results)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("# RQ3 Structural Re-eval Technical Incidents\n\n");
        List<LlmInvocationRecord> incidents = results.stream()
                .filter(r -> r.getVerdict() == LlmVerdict.ERROR)
                .toList();
        if (incidents.isEmpty()) {
            sb.append("No technical incidents recorded.\n");
        } else {
            sb.append("| caseId | model | attempt | timestamp | reason |\n");
            sb.append("|--------|-------|---------|-----------|--------|\n");
            for (LlmInvocationRecord incident : incidents) {
                sb.append("| ")
                        .append(incident.getCaseId()).append(" | ")
                        .append(incident.getModel()).append(" | ")
                        .append(incident.getAttemptNumber()).append(" | ")
                        .append(incident.getExecutionTimestamp()).append(" | ")
                        .append(sanitizeMarkdown(
                                incident.getEvaluationResult().getVerdictReason()))
                        .append(" |\n");
            }
        }
        Files.writeString(outputPath.resolve(FILE_PREFIX + "incidents.md"),
                sb.toString(), StandardCharsets.UTF_8);
    }

    private static String sanitizeMarkdown(String value) {
        if (value == null) return "";
        return value.replace("|", "\\|").replace("\n", " ");
    }
}
