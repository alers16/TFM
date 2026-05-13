package es.tfm.refactoring.experiment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Programa de ejecución del pipeline determinista para consolidar los
 * resultados de RQ2 (impacto en complejidad cognitiva).
 * <p>
 * <strong>Alcance:</strong> orquesta los componentes ya existentes
 * ({@link PilotCorpusLoader}, {@link RealDatasetLoader}, {@link BatchRunner},
 * {@link ResultExporter}) sobre el corpus piloto y el corpus real, y exporta
 * los artefactos en una carpeta estable para uso en la memoria del TFM.
 * <p>
 * <strong>No modifica</strong> la lógica del pipeline; únicamente añade
 * un punto de entrada de archivado y un resumen Markdown agregado.
 * <p>
 * Uso:
 * <ul>
 *   <li>{@code java es.tfm.refactoring.experiment.Rq2BatchExecutor}</li>
 *   <li>{@code java es.tfm.refactoring.experiment.Rq2BatchExecutor output/rq2-batch}</li>
 * </ul>
 */
public class Rq2BatchExecutor {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    public static void main(String[] args) throws IOException {
        String outputDir = (args.length > 0 && !args[0].isBlank())
                ? args[0]
                : "output/rq2-batch";
        Path out = Path.of(outputDir).toAbsolutePath();
        Files.createDirectories(out);

        System.out.println("=== RQ2 Batch Executor ===");
        System.out.println("Output directory: " + out);

        Instant startedAt = Instant.now();

        // 1. Cargar corpus
        PilotCorpusLoader pilotLoader = new PilotCorpusLoader();
        RealDatasetLoader realLoader = new RealDatasetLoader();

        List<ExperimentCase> pilotCases =
                pilotLoader.load(PilotCorpusLoader.standardPilotFiles());
        List<ExperimentCase> realCases =
                realLoader.load(RealDatasetLoader.standardRealFiles());

        List<ExperimentCase> allCases = new ArrayList<>();
        allCases.addAll(pilotCases);
        allCases.addAll(realCases);

        System.out.println("Pilot cases: " + pilotCases.size());
        System.out.println("Real cases:  " + realCases.size());
        System.out.println("Total:       " + allCases.size());

        // 2. Ejecutar pipeline
        BatchRunner runner = new BatchRunner();
        List<ExperimentResult> pilotResults = runner.run(pilotCases);
        List<ExperimentResult> realResults = runner.run(realCases);

        List<ExperimentResult> allResults = new ArrayList<>();
        allResults.addAll(pilotResults);
        allResults.addAll(realResults);

        // 3. Exportar artefactos
        ResultExporter exporter = new ResultExporter();

        exporter.exportCsv(pilotResults, out.resolve("rq2-pilot-results.csv"));
        exporter.exportJson(pilotResults, out.resolve("rq2-pilot-results.json"));

        exporter.exportCsv(realResults, out.resolve("rq2-real-results.csv"));
        exporter.exportJson(realResults, out.resolve("rq2-real-results.json"));

        exporter.exportCsv(allResults, out.resolve("rq2-all-results.csv"));
        exporter.exportJson(allResults, out.resolve("rq2-all-results.json"));

        // 4. Resumen Markdown
        String summary = buildSummary(pilotResults, realResults, allResults);
        Files.writeString(out.resolve("rq2-summary.md"), summary,
                StandardCharsets.UTF_8);

        // 5. Metadata
        Instant finishedAt = Instant.now();
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("artifactType", "rq2-batch");
        metadata.put("startedAt", startedAt.toString());
        metadata.put("finishedAt", finishedAt.toString());
        metadata.put("pilotCases", pilotResults.size());
        metadata.put("realCases", realResults.size());
        metadata.put("totalCases", allResults.size());
        metadata.put("eligibleCount", countEligible(allResults));
        metadata.put("ineligibleCount", allResults.size() - countEligible(allResults));
        metadata.put("totalDelta", totalDelta(allResults));
        metadata.put("note", "Valores de complejidad cognitiva = estimación "
                + "provisional del prototipo (proxy), no equivalente directa "
                + "a SonarQube/SonarLint.");
        Files.writeString(out.resolve("rq2-run-metadata.json"),
                GSON.toJson(metadata), StandardCharsets.UTF_8);

        System.out.println();
        System.out.println("Artifacts written:");
        System.out.println("  - rq2-pilot-results.csv / .json");
        System.out.println("  - rq2-real-results.csv  / .json");
        System.out.println("  - rq2-all-results.csv   / .json");
        System.out.println("  - rq2-summary.md");
        System.out.println("  - rq2-run-metadata.json");
        System.out.println();
        System.out.println("Done.");
    }

    private static String buildSummary(List<ExperimentResult> pilot,
                                       List<ExperimentResult> real,
                                       List<ExperimentResult> all) {
        StringBuilder sb = new StringBuilder();
        sb.append("# RQ2 — Resultados consolidados (batch determinista)\n\n");
        sb.append("**Total casos:** ").append(all.size())
          .append(" (piloto: ").append(pilot.size())
          .append(", real: ").append(real.size()).append(")\n\n");
        sb.append("**Elegibles:** ").append(countEligible(all))
          .append(" / ").append(all.size()).append("\n\n");
        sb.append("**Δ complejidad cognitiva agregado:** ")
          .append(totalDelta(all)).append("\n\n");
        sb.append("> Nota: los valores de complejidad son estimaciones del "
                + "prototipo (proxy), no equivalentes directos a "
                + "SonarQube/SonarLint.\n\n");

        sb.append("## Categorías de descarte\n\n");
        Map<String, Integer> discardCounts = new LinkedHashMap<>();
        for (ExperimentResult r : all) {
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
            sb.append("| Categoría | Casos |\n|---|---|\n");
            for (Map.Entry<String, Integer> e : discardCounts.entrySet()) {
                sb.append("| ").append(e.getKey()).append(" | ")
                  .append(e.getValue()).append(" |\n");
            }
            sb.append("\n");
        }

        sb.append("## Resultados — Corpus piloto\n\n");
        appendCorpusTable(sb, pilot);

        sb.append("\n## Resultados — Corpus real\n\n");
        appendCorpusTable(sb, real);

        return sb.toString();
    }

    private static void appendCorpusTable(StringBuilder sb,
                                          List<ExperimentResult> results) {
        sb.append("| caseId | eligible | applied | passes | CC before | CC after | Δ |\n");
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
    }

    private static String formatDelta(int delta) {
        if (delta == 0) return "0";
        return String.format(Locale.US, "%+d", delta);
    }

    private static long countEligible(List<ExperimentResult> results) {
        return results.stream().filter(ExperimentResult::isEligible).count();
    }

    private static int totalDelta(List<ExperimentResult> results) {
        int sum = 0;
        for (ExperimentResult r : results) {
            if (r.isEligible()) sum += r.getDelta();
        }
        return sum;
    }
}
