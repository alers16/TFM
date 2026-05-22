package es.tfm.refactoring.llm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import es.tfm.refactoring.experiment.BatchRunner;
import es.tfm.refactoring.experiment.ExperimentCase;
import es.tfm.refactoring.experiment.ExperimentResult;
import es.tfm.refactoring.experiment.TrapCorpusLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ejecutor de la campaña de casos trampa para RQ3.
 * <p>
 * Evalúa si los LLMs producen <em>falsos positivos</em> ante casos que
 * parecen elegibles para la refactorización if-if → if(&&) pero que en
 * realidad violan exactamente una precondición (P2, P4 o P5).
 * <p>
 * Los 3 casos trampa cubiertos son:
 * <ul>
 *   <li>TRAP_P4_INNER_ELSE — if interno con else (P4)</li>
 *   <li>TRAP_P2_MULTI_STATEMENT — then externo con dos sentencias (P2)</li>
 *   <li>TRAP_P5_ASSIGNMENT — asignación en condición interna (P5, variante no-method-call)</li>
 * </ul>
 * <p>
 * Salida en {@code output/rq3-trap-campaign/}.
 * <p>
 * Uso: {@code java es.tfm.refactoring.llm.Rq3TrapCampaignExecutor}
 */
public class Rq3TrapCampaignExecutor {

    private static final String FILE_PREFIX = "rq3-trap-";
    private static final String OUTPUT_DIR = "output/rq3-trap-campaign";
    private static final String CAMPAIGN_TYPE = "trap-cases-rq3";

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    public static void main(String[] args) throws IOException {
        Path outputPath = Path.of(OUTPUT_DIR);
        Files.createDirectories(outputPath);
        Instant startedAt = Instant.now();

        System.out.println("=== RQ3 Trap Campaign Executor ===");
        System.out.println("Purpose: evaluate false-positive rate on trap (non-eligible) cases");
        System.out.println("Output directory: " + outputPath.toAbsolutePath());
        System.out.println();

        // 1. Protocol — same freeze parameters as phase 9 (v1.0, both models, T=0, 3 attempts)
        LlmExperimentProtocol protocol = LlmExperimentProtocol.defaultProtocol();
        System.out.println("Protocol: " + protocol);
        System.out.println("Expected invocations: "
                + protocol.totalInvocations(LlmTrapSubset.size()));
        System.out.println();

        // 2. Load trap cases and compute baselines
        Map<String, LlmCampaignRunner.CaseWithBaseline> trapCases =
                loadTrapCasesWithBaselines();
        System.out.println("Trap cases loaded: " + trapCases.size());
        trapCases.forEach((id, cwb) ->
                System.out.println("  " + id + " — isEligible="
                        + cwb.getBaseline().isEligible()));
        System.out.println();

        // 3. Prepare provider
        LlmApiConfig apiConfig;
        try {
            apiConfig = new LlmApiConfig();
        } catch (IllegalStateException e) {
            String incident = "Inicio bloqueado: " + e.getMessage();
            System.err.println(incident);
            exportBlockedArtifacts(outputPath, incident, protocol, startedAt);
            return;
        }

        LlmResponseProvider provider = new LiveLlmResponseProvider(
                apiConfig, protocol.getTemperature(), protocol.getMaxOutputTokens());
        Map<String, String> modelVersions = apiConfig.toModelVersions();
        System.out.println("Provider: LiveLlmResponseProvider");
        System.out.println("API config: " + apiConfig.toSafeString());
        System.out.println();

        // 4. Execute campaign with trap cases
        LlmCampaignRunner runner = new LlmCampaignRunner();
        List<LlmInvocationRecord> results = runner.executeCampaign(
                protocol, provider, modelVersions, trapCases);

        System.out.println("Invocations completed: " + results.size());
        System.out.println();

        // 5. Generate report
        LlmCampaignReport report = new LlmCampaignReport(results, protocol);
        System.out.println(report.toSummaryString());

        long technicalErrors = results.stream()
                .filter(r -> r.getVerdict() == LlmVerdict.ERROR)
                .count();
        String status = technicalErrors > 0 ? "completed-with-incidents" : "completed";
        String statusDetail = "technicalErrors=" + technicalErrors;

        // 6. Export evidence
        LlmCampaignExporter exporter = new LlmCampaignExporter();
        exporter.exportFullEvidenceJson(results,
                outputPath.resolve(FILE_PREFIX + "full-evidence.json"));
        exporter.exportSummaryCsv(results,
                outputPath.resolve(FILE_PREFIX + "summary.csv"));
        exporter.exportAggregatedMarkdown(report,
                outputPath.resolve(FILE_PREFIX + "aggregated.md"));
        exportRunMetadata(outputPath, startedAt, protocol, modelVersions,
                results.size(), status, statusDetail);
        exportTechnicalIncidents(outputPath, results);

        System.out.println("\nExported to: " + outputPath.toAbsolutePath());
        System.out.println("  - " + FILE_PREFIX + "full-evidence.json");
        System.out.println("  - " + FILE_PREFIX + "summary.csv");
        System.out.println("  - " + FILE_PREFIX + "aggregated.md");
        System.out.println("  - " + FILE_PREFIX + "run-metadata.json");
        System.out.println("  - " + FILE_PREFIX + "incidents.md");
    }

    /**
     * Carga los casos trampa y computa el baseline determinista de cada uno.
     */
    public static Map<String, LlmCampaignRunner.CaseWithBaseline>
            loadTrapCasesWithBaselines() throws IOException {

        TrapCorpusLoader loader = new TrapCorpusLoader();
        List<ExperimentCase> trapCases = loader.load(LlmTrapSubset.trapFiles());

        BatchRunner batchRunner = new BatchRunner();
        Map<String, LlmCampaignRunner.CaseWithBaseline> result = new LinkedHashMap<>();
        for (ExperimentCase ec : trapCases) {
            ExperimentResult baseline = batchRunner.run(List.of(ec)).get(0);
            result.put(ec.getCaseId(), new LlmCampaignRunner.CaseWithBaseline(ec, baseline));
        }
        return result;
    }

    private static void exportBlockedArtifacts(Path outputPath, String incident,
                                               LlmExperimentProtocol protocol,
                                               Instant startedAt) throws IOException {
        LlmCampaignExporter exporter = new LlmCampaignExporter();
        List<LlmInvocationRecord> empty = List.of();
        exporter.exportFullEvidenceJson(empty,
                outputPath.resolve(FILE_PREFIX + "full-evidence.json"));
        exporter.exportSummaryCsv(empty,
                outputPath.resolve(FILE_PREFIX + "summary.csv"));
        exporter.exportAggregatedMarkdown(
                new LlmCampaignReport(empty, protocol),
                outputPath.resolve(FILE_PREFIX + "aggregated.md"));
        exportRunMetadata(outputPath, startedAt, protocol,
                Map.of(), 0, "blocked", incident);

        StringBuilder sb = new StringBuilder();
        sb.append("# RQ3 Trap Campaign Technical Incidents\n\n");
        sb.append("| stage | timestamp | reason |\n");
        sb.append("|-------|-----------|--------|\n");
        sb.append("| startup | ").append(Instant.now())
                .append(" | ").append(sanitizeMarkdown(incident)).append(" |\n");
        Files.writeString(outputPath.resolve(FILE_PREFIX + "incidents.md"),
                sb.toString(), StandardCharsets.UTF_8);
    }

    private static void exportRunMetadata(Path outputPath, Instant startedAt,
                                          LlmExperimentProtocol protocol,
                                          Map<String, String> modelVersions,
                                          int completedInvocations,
                                          String status, String statusDetail)
            throws IOException {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("campaignPhase", "Trap");
        metadata.put("campaignType", CAMPAIGN_TYPE);
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
                "subsetSize", LlmTrapSubset.size(),
                "expectedInvocations", protocol.totalInvocations(LlmTrapSubset.size())
        ));
        metadata.put("modelVersions", modelVersions);
        metadata.put("completedInvocations", completedInvocations);
        metadata.put("trapCases", LlmTrapSubset.caseIds());
        Files.writeString(outputPath.resolve(FILE_PREFIX + "run-metadata.json"),
                GSON.toJson(metadata), StandardCharsets.UTF_8);
    }

    private static void exportTechnicalIncidents(Path outputPath,
                                                  List<LlmInvocationRecord> results)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("# RQ3 Trap Campaign Technical Incidents\n\n");
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
