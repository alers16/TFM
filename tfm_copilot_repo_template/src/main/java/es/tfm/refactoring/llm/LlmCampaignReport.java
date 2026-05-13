package es.tfm.refactoring.llm;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Informe agregado de una campaña experimental de RQ3.
 * <p>
 * Calcula métricas por modelo, por caso y globales a partir de la lista
 * completa de registros de invocación.
 */
public class LlmCampaignReport {

    private final List<LlmInvocationRecord> records;
    private final LlmExperimentProtocol protocol;

    public LlmCampaignReport(List<LlmInvocationRecord> records,
                             LlmExperimentProtocol protocol) {
        this.records = List.copyOf(records);
        this.protocol = protocol;
    }

    public List<LlmInvocationRecord> getRecords() { return records; }
    public LlmExperimentProtocol getProtocol() { return protocol; }
    public int getTotalInvocations() { return records.size(); }

    /** Modelos distintos presentes en los resultados. */
    public List<String> getModels() {
        return records.stream()
                .map(LlmInvocationRecord::getModel)
                .distinct()
                .collect(Collectors.toList());
    }

    /** CaseIds distintos presentes en los resultados. */
    public List<String> getCaseIds() {
        return records.stream()
                .map(LlmInvocationRecord::getCaseId)
                .distinct()
                .collect(Collectors.toList());
    }

    /** Cuenta de cada veredicto para un modelo dado. */
    public Map<LlmVerdict, Integer> getVerdictCounts(String model) {
        Map<LlmVerdict, Integer> counts = new LinkedHashMap<>();
        for (LlmVerdict v : LlmVerdict.values()) {
            counts.put(v, 0);
        }
        for (LlmInvocationRecord r : records) {
            if (r.getModel().equals(model)) {
                counts.merge(r.getVerdict(), 1, Integer::sum);
            }
        }
        return counts;
    }

    /** Tasa de éxito (SUCCESS / total) para un modelo. */
    public double getSuccessRate(String model) {
        long total = records.stream()
                .filter(r -> r.getModel().equals(model)).count();
        if (total == 0) return 0.0;
        long success = records.stream()
                .filter(r -> r.getModel().equals(model))
                .filter(r -> r.getVerdict() == LlmVerdict.SUCCESS)
                .count();
        return (double) success / total;
    }

    /** Tasa de éxito por caso (sobre todos los modelos e intentos). */
    public double getCaseSuccessRate(String caseId) {
        long total = records.stream()
                .filter(r -> r.getCaseId().equals(caseId)).count();
        if (total == 0) return 0.0;
        long success = records.stream()
                .filter(r -> r.getCaseId().equals(caseId))
                .filter(r -> r.getVerdict() == LlmVerdict.SUCCESS)
                .count();
        return (double) success / total;
    }

    /** Tasa de coincidencia con baseline para un modelo. */
    public double getBaselineMatchRate(String model) {
        long total = records.stream()
                .filter(r -> r.getModel().equals(model)).count();
        if (total == 0) return 0.0;
        long match = records.stream()
                .filter(r -> r.getModel().equals(model))
                .filter(r -> r.getEvaluationResult().isMatchesBaseline())
                .count();
        return (double) match / total;
    }

    /** Tasa de consistencia: % de casos donde los 3 intentos dan el mismo veredicto. */
    public double getConsistencyRate(String model) {
        Map<String, List<LlmInvocationRecord>> byCaseId = records.stream()
                .filter(r -> r.getModel().equals(model))
                .collect(Collectors.groupingBy(LlmInvocationRecord::getCaseId));

        if (byCaseId.isEmpty()) return 0.0;

        long consistent = byCaseId.values().stream()
                .filter(attempts -> {
                    if (attempts.isEmpty()) return false;
                    LlmVerdict first = attempts.get(0).getVerdict();
                    return attempts.stream().allMatch(a -> a.getVerdict() == first);
                })
                .count();

        return (double) consistent / byCaseId.size();
    }

    /** Delta medio en invocaciones con código parseable. */
    public double getMeanDelta(String model) {
        List<LlmEvaluationResult> parseable = records.stream()
                .filter(r -> r.getModel().equals(model))
                .map(LlmInvocationRecord::getEvaluationResult)
                .filter(LlmEvaluationResult::isParseable)
                .collect(Collectors.toList());

        if (parseable.isEmpty()) return 0.0;
        double sum = parseable.stream()
                .mapToInt(LlmEvaluationResult::getDelta)
                .sum();
        return sum / parseable.size();
    }

    /**
     * Genera un resumen textual formateado de la campaña.
     */
    public String toSummaryString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== CAMPAIGN REPORT — RQ3 ===\n\n");
        sb.append(String.format("Protocol: %s%n", protocol));
        sb.append(String.format("Total invocations: %d%n%n", records.size()));

        for (String model : getModels()) {
            sb.append(String.format("--- Model: %s ---%n", model));
            Map<LlmVerdict, Integer> counts = getVerdictCounts(model);
            for (Map.Entry<LlmVerdict, Integer> e : counts.entrySet()) {
                if (e.getValue() > 0) {
                    sb.append(String.format("  %-16s: %d%n",
                            e.getKey(), e.getValue()));
                }
            }
            sb.append(String.format(Locale.US,
                    "  Success rate    : %.1f%%%n",
                    getSuccessRate(model) * 100));
            sb.append(String.format(Locale.US,
                    "  Baseline match  : %.1f%%%n",
                    getBaselineMatchRate(model) * 100));
            sb.append(String.format(Locale.US,
                    "  Consistency     : %.1f%%%n",
                    getConsistencyRate(model) * 100));
            sb.append(String.format(Locale.US,
                    "  Mean delta      : %.2f%n%n",
                    getMeanDelta(model)));
        }

        sb.append("--- Per-case success rates ---\n");
        for (String caseId : getCaseIds()) {
            sb.append(String.format(Locale.US, "  %-40s: %.1f%%%n",
                    caseId, getCaseSuccessRate(caseId) * 100));
        }

        return sb.toString();
    }
}
