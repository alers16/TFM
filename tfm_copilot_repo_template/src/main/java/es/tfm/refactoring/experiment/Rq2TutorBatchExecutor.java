package es.tfm.refactoring.experiment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Ejecutor de RQ2 para el corpus extraido del repositorio del tutor
 * (SoftwareCognitiveComplexityReducer). Es analogo a {@link Rq2BatchExecutor}
 * pero opera unicamente sobre los seis metodos diana del paper de
 * Saborido et al. 2022, cargados via {@link TutorCorpusLoader}.
 * <p>
 * Uso:
 * <ul>
 *   <li>{@code java es.tfm.refactoring.experiment.Rq2TutorBatchExecutor}</li>
 *   <li>{@code java es.tfm.refactoring.experiment.Rq2TutorBatchExecutor output/rq2-tutor}</li>
 * </ul>
 */
public class Rq2TutorBatchExecutor {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    public static void main(String[] args) throws IOException {
        String outputDir = (args.length > 0 && !args[0].isBlank())
                ? args[0]
                : "output/rq2-tutor";
        Path out = Path.of(outputDir).toAbsolutePath();
        Files.createDirectories(out);

        System.out.println("=== RQ2 Tutor Batch Executor ===");
        System.out.println("Output directory: " + out);

        Instant startedAt = Instant.now();

        TutorCorpusLoader loader = new TutorCorpusLoader();
        List<ExperimentCase> tutorCases =
                loader.load(TutorCorpusLoader.standardTutorFiles());

        System.out.println("Tutor cases: " + tutorCases.size());

        BatchRunner runner = new BatchRunner();
        List<ExperimentResult> results = runner.run(tutorCases);

        ResultExporter exporter = new ResultExporter();
        exporter.exportCsv(results, out.resolve("rq2-tutor-results.csv"));
        exporter.exportJson(results, out.resolve("rq2-tutor-results.json"));

        String summary = buildSummary(results);
        Files.writeString(out.resolve("rq2-tutor-summary.md"), summary,
                StandardCharsets.UTF_8);

        Instant finishedAt = Instant.now();
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("artifactType", "rq2-tutor-batch");
        metadata.put("startedAt", startedAt.toString());
        metadata.put("finishedAt", finishedAt.toString());
        metadata.put("tutorCases", results.size());
        metadata.put("eligibleCount", countEligible(results));
        metadata.put("ineligibleCount", results.size() - countEligible(results));
        metadata.put("totalDelta", totalDelta(results));
        metadata.put("note",
                "Corpus extraido del repositorio del tutor "
                + "(SoftwareCognitiveComplexityReducer, Saborido et al. 2022). "
                + "Valores de complejidad = estimacion proxy del prototipo.");
        Files.writeString(out.resolve("rq2-tutor-run-metadata.json"),
                GSON.toJson(metadata), StandardCharsets.UTF_8);

        System.out.println();
        for (ExperimentResult r : results) {
            System.out.printf(Locale.US,
                    "  %-50s eligible=%-5s applied=%-2d passes=%-2d CC %d->%d (delta=%+d)%n",
                    r.getCaseId(), r.isEligible(),
                    r.getOpportunitiesApplied(), r.getTotalPasses(),
                    r.getComplexityBefore(), r.getComplexityAfter(),
                    r.getDelta());
        }
        System.out.println();
        System.out.println("Eligible: " + countEligible(results) + "/" + results.size());
        System.out.println("Total delta: " + totalDelta(results));
        System.out.println();
        System.out.println("Done.");
    }

    private static String buildSummary(List<ExperimentResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("# RQ2 - Corpus del tutor (Saborido et al. 2022)\n\n");
        sb.append("**Total casos:** ").append(results.size()).append("\n\n");
        sb.append("**Elegibles:** ").append(countEligible(results))
          .append(" / ").append(results.size()).append("\n\n");
        sb.append("**Delta complejidad cognitiva agregado:** ")
          .append(totalDelta(results)).append("\n\n");
        sb.append("> Nota: valores de complejidad = estimacion proxy del "
                + "prototipo, no equivalente directa a SonarQube.\n\n");

        sb.append("## Categorias de descarte\n\n");
        Map<String, Integer> discardCounts = new LinkedHashMap<>();
        for (ExperimentResult r : results) {
            if (!r.isEligible()) {
                if (r.getDiscardCategories().isEmpty()) {
                    discardCounts.merge("(no opportunity detected)", 1, Integer::sum);
                } else {
                    for (String cat : r.getDiscardCategories()) {
                        discardCounts.merge(cat, 1, Integer::sum);
                    }
                }
            }
        }
        if (discardCounts.isEmpty()) {
            sb.append("Todos los casos elegibles.\n\n");
        } else {
            sb.append("| Categoria | Casos |\n|---|---|\n");
            for (Map.Entry<String, Integer> e : discardCounts.entrySet()) {
                sb.append("| ").append(e.getKey()).append(" | ")
                  .append(e.getValue()).append(" |\n");
            }
            sb.append("\n");
        }

        sb.append("## Resultados\n\n");
        sb.append("| caseId | eligible | applied | passes | CC before | CC after | delta |\n");
        sb.append("|---|---|---|---|---|---|---|\n");
        for (ExperimentResult r : results) {
            sb.append("| ").append(r.getCaseId())
              .append(" | ").append(r.isEligible())
              .append(" | ").append(r.getOpportunitiesApplied())
              .append(" | ").append(r.getTotalPasses())
              .append(" | ").append(r.getComplexityBefore())
              .append(" | ").append(r.getComplexityAfter())
              .append(" | ").append(formatDelta(r.getDelta()))
              .append(" |\n");
        }
        return sb.toString();
    }

    private static String formatDelta(int delta) {
        if (delta == 0) return "0";
        return String.format(Locale.US, "%+d", delta);
    }

    private static long countEligible(List<ExperimentResult> results) {
        return results.stream().filter(ExperimentResult::isEligible).count();
    }

    private static int totalDelta(List<ExperimentResult> results) {
        return results.stream().mapToInt(ExperimentResult::getDelta).sum();
    }
}
