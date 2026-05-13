package es.tfm.refactoring.llm;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Exporta los resultados de la campaña experimental de RQ3 en formatos
 * reproducibles: JSON completo con evidencia, CSV resumen y reporte agregado.
 */
public class LlmCampaignExporter {

    private static final String CSV_HEADER = String.join(",",
            "caseId", "model", "modelVersion", "promptVersion",
            "attemptNumber", "verdict", "verdictReason",
            "parseable", "complexityBefore", "complexityAfter",
            "delta", "baselineDelta", "matchesBaseline",
            "executionTimestamp", "responseTimeMs",
            "validationErrors", "observations");

    private final Gson gson;

    public LlmCampaignExporter() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
    }

    /**
     * Exporta evidencia completa a JSON (incluye rawLlmOutput y promptSent).
     */
    public String toFullEvidenceJson(List<LlmInvocationRecord> records) {
        List<Map<String, Object>> entries = records.stream()
                .map(this::recordToMap)
                .toList();
        return gson.toJson(entries);
    }

    /**
     * Exporta resumen a CSV (excluye rawLlmOutput y prompt por tamaño).
     */
    public String toSummaryCsv(List<LlmInvocationRecord> records) {
        StringBuilder sb = new StringBuilder();
        sb.append(CSV_HEADER).append("\n");
        for (LlmInvocationRecord r : records) {
            sb.append(csvLine(r)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Exporta reporte agregado por modelo en formato Markdown.
     */
    public String toAggregatedMarkdown(LlmCampaignReport report) {
        StringBuilder sb = new StringBuilder();
        sb.append("# RQ3 Campaign Results — Aggregated\n\n");
        sb.append(String.format("**Total invocations:** %d%n%n",
                report.getTotalInvocations()));
        sb.append(String.format("**Protocol:** %s%n%n", report.getProtocol()));

        sb.append("## Results by Model\n\n");
        sb.append("| Model | SUCCESS | INCORRECT | INVALID_OUTPUT | REFUSED | PARTIAL | ERROR | Success Rate | Baseline Match | Consistency |\n");
        sb.append("|-------|---------|-----------|----------------|---------|---------|-------|-------------|---------------|-------------|\n");

        for (String model : report.getModels()) {
            Map<LlmVerdict, Integer> vc = report.getVerdictCounts(model);
            sb.append(String.format(Locale.US,
                    "| %s | %d | %d | %d | %d | %d | %d | %.1f%% | %.1f%% | %.1f%% |\n",
                    model,
                    vc.getOrDefault(LlmVerdict.SUCCESS, 0),
                    vc.getOrDefault(LlmVerdict.INCORRECT, 0),
                    vc.getOrDefault(LlmVerdict.INVALID_OUTPUT, 0),
                    vc.getOrDefault(LlmVerdict.REFUSED, 0),
                    vc.getOrDefault(LlmVerdict.PARTIAL, 0),
                    vc.getOrDefault(LlmVerdict.ERROR, 0),
                    report.getSuccessRate(model) * 100,
                    report.getBaselineMatchRate(model) * 100,
                    report.getConsistencyRate(model) * 100));
        }

        sb.append("\n## Results by Case\n\n");
        sb.append("| Case | Success Rate | Detail by Model |\n");
        sb.append("|------|-------------|----------------|\n");

        for (String caseId : report.getCaseIds()) {
            StringBuilder detail = new StringBuilder();
            for (String model : report.getModels()) {
                long success = report.getRecords().stream()
                        .filter(r -> r.getCaseId().equals(caseId))
                        .filter(r -> r.getModel().equals(model))
                        .filter(r -> r.getVerdict() == LlmVerdict.SUCCESS)
                        .count();
                long total = report.getRecords().stream()
                        .filter(r -> r.getCaseId().equals(caseId))
                        .filter(r -> r.getModel().equals(model))
                        .count();
                if (detail.length() > 0) detail.append(", ");
                detail.append(String.format("%s: %d/%d", model, success, total));
            }
            sb.append(String.format(Locale.US, "| %s | %.1f%% | %s |\n",
                    caseId,
                    report.getCaseSuccessRate(caseId) * 100,
                    detail));
        }

        return sb.toString();
    }

    // === File export methods ===

    public void exportFullEvidenceJson(List<LlmInvocationRecord> records,
                                       Path outputPath) throws IOException {
        Files.createDirectories(outputPath.getParent());
        Files.writeString(outputPath, toFullEvidenceJson(records),
                StandardCharsets.UTF_8);
    }

    public void exportSummaryCsv(List<LlmInvocationRecord> records,
                                 Path outputPath) throws IOException {
        Files.createDirectories(outputPath.getParent());
        Files.writeString(outputPath, toSummaryCsv(records),
                StandardCharsets.UTF_8);
    }

    public void exportAggregatedMarkdown(LlmCampaignReport report,
                                         Path outputPath) throws IOException {
        Files.createDirectories(outputPath.getParent());
        Files.writeString(outputPath, toAggregatedMarkdown(report),
                StandardCharsets.UTF_8);
    }

    // === Internal serialization ===

    private Map<String, Object> recordToMap(LlmInvocationRecord r) {
        LlmEvaluationResult e = r.getEvaluationResult();
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("caseId", e.getCaseId());
        map.put("model", e.getModel());
        map.put("modelVersion", r.getModelVersion());
        map.put("promptVersion", e.getPromptVersion());
        map.put("attemptNumber", e.getAttemptNumber());
        map.put("executionTimestamp", r.getExecutionTimestamp().toString());
        map.put("responseTimeMs", r.getResponseTimeMs());
        map.put("verdict", e.getVerdict().name());
        map.put("verdictReason", e.getVerdictReason());
        map.put("parseable", e.isParseable());
        map.put("complexityBefore", e.getComplexityBefore());
        map.put("complexityAfter", e.getComplexityAfter());
        map.put("delta", e.getDelta());
        map.put("baselineDelta", e.getBaselineDelta());
        map.put("matchesBaseline", e.isMatchesBaseline());
        map.put("validationErrors", e.getValidationErrors());
        map.put("observations", e.getObservations());
        map.put("rawLlmOutput", e.getRawLlmOutput());
        map.put("extractedCode", e.getExtractedCode());
        map.put("promptSent", r.getPromptSent());
        return map;
    }

    private String csvLine(LlmInvocationRecord r) {
        LlmEvaluationResult e = r.getEvaluationResult();
        return String.join(",",
                csvField(e.getCaseId()),
                csvField(e.getModel()),
                csvField(r.getModelVersion()),
                csvField(e.getPromptVersion()),
                String.valueOf(e.getAttemptNumber()),
                csvField(e.getVerdict().name()),
                csvField(e.getVerdictReason()),
                String.valueOf(e.isParseable()),
                String.valueOf(e.getComplexityBefore()),
                String.valueOf(e.getComplexityAfter()),
                String.valueOf(e.getDelta()),
                String.valueOf(e.getBaselineDelta()),
                String.valueOf(e.isMatchesBaseline()),
                csvField(r.getExecutionTimestamp().toString()),
                String.valueOf(r.getResponseTimeMs()),
                csvField(String.join("; ", e.getValidationErrors())),
                csvField(e.getObservations()));
    }

    private String csvField(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    String getCsvHeader() { return CSV_HEADER; }
}
