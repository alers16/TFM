package es.tfm.refactoring.llm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Ejecutor de la campaña comparativa de prompts para RQ3.
 * <p>
 * Aplica el prompt v2.0 (few-shot con un ejemplo APPLICABLE y uno NOT APPLICABLE)
 * sobre los mismos 6 casos del subset estándar de RQ3, permitiendo comparar
 * si los ejemplos few-shot mejoran la tasa de corrección respecto al
 * prompt zero-shot v1.0.
 * <p>
 * El protocolo mantiene los mismos parámetros que la fase 9
 * (GPT-4o + GPT-4.1, temperatura 0, 3 intentos, 2048 tokens max)
 * salvo la versión del prompt.
 * <p>
 * Salida en {@code output/rq3-promptv2-campaign/}.
 * <p>
 * Uso: {@code java es.tfm.refactoring.llm.Rq3PromptV2Executor}
 */
public class Rq3PromptV2Executor {

    private static final String FILE_PREFIX = "rq3-promptv2-";
    private static final String OUTPUT_DIR = "output/rq3-promptv2-campaign";
    private static final String CAMPAIGN_TYPE = "prompt-v2-comparison-rq3";

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .create();

    public static void main(String[] args) throws IOException {
        Path outputPath = Path.of(OUTPUT_DIR);
        Files.createDirectories(outputPath);
        Instant startedAt = Instant.now();

        System.out.println("=== RQ3 Prompt v2.0 Comparison Executor ===");
        System.out.println("Purpose: compare few-shot (v2.0) vs zero-shot (v1.0) on standard 6-case subset");
        System.out.println("Output directory: " + outputPath.toAbsolutePath());
        System.out.println();

        // 1. Protocol — same as phase 9 but with prompt v2.0
        LlmExperimentProtocol protocol = LlmExperimentProtocol.promptV2Protocol();
        System.out.println("Protocol: " + protocol);
        System.out.println("Expected invocations: "
                + protocol.totalInvocations(LlmEvaluationSubset.size()));
        System.out.println();

        // 2. Prepare provider
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

        // 3. Execute campaign — runner loads standard 6 cases and uses v2.0 prompt
        //    because protocol.getPromptVersion() == "v2.0"
        LlmCampaignRunner runner = new LlmCampaignRunner();
        List<LlmInvocationRecord> results = runner.executeCampaign(
                protocol, provider, modelVersions);

        System.out.println("Invocations completed: " + results.size());
        System.out.println();

        // 4. Generate report
        LlmCampaignReport report = new LlmCampaignReport(results, protocol);
        System.out.println(report.toSummaryString());

        long technicalErrors = results.stream()
                .filter(r -> r.getVerdict() == LlmVerdict.ERROR)
                .count();
        String status = technicalErrors > 0 ? "completed-with-incidents" : "completed";
        String statusDetail = "technicalErrors=" + technicalErrors;

        // 5. Export evidence
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
        sb.append("# RQ3 Prompt v2.0 Technical Incidents\n\n");
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
        metadata.put("campaignPhase", "PromptV2");
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
                "subsetSize", LlmEvaluationSubset.size(),
                "expectedInvocations", protocol.totalInvocations(LlmEvaluationSubset.size())
        ));
        metadata.put("modelVersions", modelVersions);
        metadata.put("completedInvocations", completedInvocations);
        metadata.put("comparisonBaseline", "phase9-real-campaign-2026-05-18");
        Files.writeString(outputPath.resolve(FILE_PREFIX + "run-metadata.json"),
                GSON.toJson(metadata), StandardCharsets.UTF_8);
    }

    private static void exportTechnicalIncidents(Path outputPath,
                                                  List<LlmInvocationRecord> results)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("# RQ3 Prompt v2.0 Technical Incidents\n\n");
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
