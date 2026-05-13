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
 * Programa de ejecución de la campaña experimental de RQ3.
 * <p>
 * Ejecuta el protocolo completo sobre el subset de evaluación con
 * proveedor de respuestas configurable (real o pre-grabado) y genera
 * artefactos de evidencia reproducibles:
 * <ul>
 *   <li>JSON completo con trazabilidad por invocación.</li>
 *   <li>CSV resumen para análisis tabular.</li>
 *   <li>Reporte agregado en Markdown.</li>
 *   <li>Metadatos de ejecución para replicación.</li>
 *   <li>Incidencias técnicas por invocación (si las hay).</li>
 *   <li>Resumen textual en consola.</li>
 * </ul>
 * <p>
 * Uso:
 * <ul>
 *   <li>{@code java es.tfm.refactoring.llm.CampaignExecutor}</li>
 *   <li>{@code java es.tfm.refactoring.llm.CampaignExecutor output/rq3-campaign-real}</li>
 *   <li>{@code java es.tfm.refactoring.llm.CampaignExecutor --mode=dry-run output/rq3-campaign-dry}</li>
 * </ul>
 */
public class CampaignExecutor {

        private static final String MODE_LIVE = "live";
        private static final String MODE_DRY_RUN = "dry-run";
        private static final String MODE_OPENAI_ONLY = "openai-only";
        private static final Gson GSON = new GsonBuilder()
                        .setPrettyPrinting()
                        .disableHtmlEscaping()
                        .create();

    public static void main(String[] args) throws IOException {
                String mode = MODE_LIVE;
                String outputDir = null;
                for (String arg : args) {
                        if (arg.startsWith("--mode=")) {
                                mode = arg.substring("--mode=".length()).strip();
                        } else if (!arg.isBlank()) {
                                outputDir = arg;
                        }
                }

                if (!MODE_LIVE.equals(mode) && !MODE_DRY_RUN.equals(mode)
                                && !MODE_OPENAI_ONLY.equals(mode)) {
                        throw new IllegalArgumentException("Modo no soportado: " + mode
                                        + " (usa live, dry-run o openai-only)");
                }

                if (outputDir == null) {
                        outputDir = MODE_OPENAI_ONLY.equals(mode)
                                        ? "output/rq3-openai-only"
                                        : "output/rq3-campaign-real";
                }

                String filePrefix = MODE_OPENAI_ONLY.equals(mode)
                                ? "rq3-openai-only-"
                                : "rq3-";
                String campaignType;
                if (MODE_OPENAI_ONLY.equals(mode)) {
                        campaignType = "exploratory-openai-only";
                } else if (MODE_DRY_RUN.equals(mode)) {
                        campaignType = "dry-run";
                } else {
                        campaignType = "first-real-rq3";
                }

        Path outputPath = Path.of(outputDir);
                Files.createDirectories(outputPath);
                Instant startedAt = Instant.now();

        System.out.println("=== RQ3 Campaign Executor ===");
                System.out.println("Mode: " + mode);
        System.out.println("Output directory: " + outputPath.toAbsolutePath());
        System.out.println();

        // 1. Protocol
        LlmExperimentProtocol protocol = MODE_OPENAI_ONLY.equals(mode)
                        ? LlmExperimentProtocol.openAiOnlyExploratoryProtocol()
                        : LlmExperimentProtocol.defaultProtocol();
        System.out.println("Protocol: " + protocol);
        System.out.println("Expected invocations: "
                + protocol.totalInvocations(LlmEvaluationSubset.size()));
        System.out.println();

        // 2. Execute campaign
        LlmCampaignRunner runner = new LlmCampaignRunner();
        LlmResponseProvider provider;
        Map<String, String> modelVersions;

        if (MODE_LIVE.equals(mode) || MODE_OPENAI_ONLY.equals(mode)) {
                        LlmApiConfig apiConfig;
                        try {
                                apiConfig = MODE_OPENAI_ONLY.equals(mode)
                                                ? LlmApiConfig.openAiOnlyFromEnv()
                                                : new LlmApiConfig();
                        } catch (IllegalStateException e) {
                                String incident = "Inicio bloqueado: " + e.getMessage();
                                LlmCampaignExporter blockedExporter = new LlmCampaignExporter();
                                List<LlmInvocationRecord> emptyResults = List.of();
                                blockedExporter.exportFullEvidenceJson(emptyResults,
                                        outputPath.resolve(filePrefix + "full-evidence.json"));
                                blockedExporter.exportSummaryCsv(emptyResults,
                                        outputPath.resolve(filePrefix + "summary.csv"));
                                blockedExporter.exportAggregatedMarkdown(
                                        new LlmCampaignReport(emptyResults, protocol),
                                        outputPath.resolve(filePrefix + "aggregated.md"));
                                exportRunMetadata(outputPath, filePrefix, campaignType,
                                                startedAt, mode, protocol,
                                                Map.of(), 0, "blocked", incident);
                                exportStartupIncident(outputPath, filePrefix, incident);
                                System.err.println(incident);
                                System.err.println("Se registraron artefactos de incidente en: "
                                                + outputPath.toAbsolutePath());
                                return;
                        }
            provider = new LiveLlmResponseProvider(apiConfig,
                    protocol.getTemperature(), protocol.getMaxOutputTokens());
            modelVersions = apiConfig.toModelVersions();
            System.out.println("Provider: LiveLlmResponseProvider");
            System.out.println("API config: " + apiConfig.toSafeString());
        } else {
            provider = new PrerecordedResponseProvider();
            modelVersions = Map.of(
                    "gpt-4o", "gpt-4o-2024-05-13",
                    "gpt-4.1", "gpt-4.1-2025-04-14"
            );
            System.out.println("Provider: PrerecordedResponseProvider");
        }
        System.out.println();

        List<LlmInvocationRecord> results = runner.executeCampaign(
                protocol, provider, modelVersions);

        System.out.println("Invocations completed: " + results.size());
        System.out.println();

        // 3. Generate report
        LlmCampaignReport report = new LlmCampaignReport(results, protocol);
        System.out.println(report.toSummaryString());

        long technicalErrors = results.stream()
                .filter(r -> r.getVerdict() == LlmVerdict.ERROR)
                .count();
        String status = technicalErrors > 0 ? "completed-with-incidents" : "completed";
        String statusDetail = "technicalErrors=" + technicalErrors;

        // 4. Export evidence
        LlmCampaignExporter exporter = new LlmCampaignExporter();
        exporter.exportFullEvidenceJson(results,
                outputPath.resolve(filePrefix + "full-evidence.json"));
        exporter.exportSummaryCsv(results,
                outputPath.resolve(filePrefix + "summary.csv"));
        exporter.exportAggregatedMarkdown(report,
                outputPath.resolve(filePrefix + "aggregated.md"));
        exportRunMetadata(outputPath, filePrefix, campaignType,
                startedAt, mode, protocol,
                modelVersions, results.size(), status, statusDetail);
        exportTechnicalIncidents(outputPath, filePrefix, results);

        System.out.println("\nExported to: " + outputPath.toAbsolutePath());
        System.out.println("  - " + filePrefix + "full-evidence.json");
        System.out.println("  - " + filePrefix + "summary.csv");
        System.out.println("  - " + filePrefix + "aggregated.md");
        System.out.println("  - " + filePrefix + "run-metadata.json");
        System.out.println("  - " + filePrefix + "incidents.md");
    }

    private static void exportRunMetadata(Path outputPath,
                                          String filePrefix,
                                          String campaignType,
                                          Instant startedAt,
                                          String mode,
                                          LlmExperimentProtocol protocol,
                                          Map<String, String> modelVersions,
                                                                                  int completedInvocations,
                                                                                  String status,
                                                                                  String statusDetail)
            throws IOException {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("campaignPhase", "Phase 9");
        metadata.put("campaignType", campaignType);
        metadata.put("executionMode", mode);
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
        Files.writeString(outputPath.resolve(filePrefix + "run-metadata.json"),
                GSON.toJson(metadata), StandardCharsets.UTF_8);
    }

    private static void exportStartupIncident(Path outputPath,
                                              String filePrefix,
                                              String incident)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("# RQ3 Technical Incidents\n\n");
        sb.append("| stage | timestamp | reason |\n");
        sb.append("|-------|-----------|--------|\n");
        sb.append("| startup | ")
                .append(Instant.now())
                .append(" | ")
                .append(sanitizeMarkdown(incident))
                .append(" |\n");

        Files.writeString(outputPath.resolve(filePrefix + "incidents.md"),
                sb.toString(), StandardCharsets.UTF_8);
    }

    private static void exportTechnicalIncidents(Path outputPath,
                                                 String filePrefix,
                                                 List<LlmInvocationRecord> results)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("# RQ3 Technical Incidents\n\n");
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

        Files.writeString(outputPath.resolve(filePrefix + "incidents.md"),
                sb.toString(), StandardCharsets.UTF_8);
    }

    private static String sanitizeMarkdown(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("|", "\\|").replace("\n", " ");
    }
}
