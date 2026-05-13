package es.tfm.refactoring.experiment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import es.tfm.refactoring.analysis.CognitiveComplexityCalculator;
import es.tfm.refactoring.detection.DetectionMode;
import es.tfm.refactoring.detection.NestedIfDetector;
import es.tfm.refactoring.transformation.NestedIfTransformer;

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
 * Ejecutor comparativo RQ2: modo STRICT (P1–P5) vs. modo RELAXED (P1–P4 + P5').
 * <p>
 * Genera artefactos en {@code output/rq2-comparison/} para los tres corpus:
 * piloto, real y tutor. El propósito es cuantificar el impacto de relajar
 * la precondición P5 con el allowlist de métodos presumiblemente puros
 * ({@link es.tfm.refactoring.detection.MethodCallAllowlist}).
 * <p>
 * Uso:
 * <ul>
 *   <li>{@code java es.tfm.refactoring.experiment.Rq2ComparisonExecutor}</li>
 *   <li>{@code java es.tfm.refactoring.experiment.Rq2ComparisonExecutor output/rq2-comparison}</li>
 * </ul>
 */
public class Rq2ComparisonExecutor {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    public static void main(String[] args) throws IOException {
        String outputDir = (args.length > 0 && !args[0].isBlank())
                ? args[0]
                : "output/rq2-comparison";
        Path out = Path.of(outputDir).toAbsolutePath();
        Files.createDirectories(out);

        System.out.println("=== RQ2 Comparison Executor (STRICT vs RELAXED) ===");
        System.out.println("Output directory: " + out);

        Instant startedAt = Instant.now();

        // Cargar los tres corpus
        List<ExperimentCase> pilotCases =
                new PilotCorpusLoader().load(PilotCorpusLoader.standardPilotFiles());
        List<ExperimentCase> realCases =
                new RealDatasetLoader().load(RealDatasetLoader.standardRealFiles());
        List<ExperimentCase> tutorCases =
                new TutorCorpusLoader().load(TutorCorpusLoader.standardTutorFiles());

        System.out.printf("Loaded: pilot=%d  real=%d  tutor=%d%n",
                pilotCases.size(), realCases.size(), tutorCases.size());

        // Runners STRICT y RELAXED
        BatchRunner strictRunner = new BatchRunner(
                new NestedIfDetector(DetectionMode.STRICT),
                new NestedIfTransformer(),
                new CognitiveComplexityCalculator());

        BatchRunner relaxedRunner = new BatchRunner(
                new NestedIfDetector(DetectionMode.RELAXED),
                new NestedIfTransformer(),
                new CognitiveComplexityCalculator());

        // Ejecutar ambos modos sobre cada corpus
        CorpusComparison pilot  = runComparison("pilot",  pilotCases,  strictRunner, relaxedRunner);
        CorpusComparison real   = runComparison("real",   realCases,   strictRunner, relaxedRunner);
        CorpusComparison tutor  = runComparison("tutor",  tutorCases,  strictRunner, relaxedRunner);

        // Exportar CSV por corpus y modo
        ResultExporter exporter = new ResultExporter();
        exporter.exportCsv(pilot.strictResults,  out.resolve("pilot-strict.csv"));
        exporter.exportCsv(pilot.relaxedResults, out.resolve("pilot-relaxed.csv"));
        exporter.exportCsv(real.strictResults,   out.resolve("real-strict.csv"));
        exporter.exportCsv(real.relaxedResults,  out.resolve("real-relaxed.csv"));
        exporter.exportCsv(tutor.strictResults,  out.resolve("tutor-strict.csv"));
        exporter.exportCsv(tutor.relaxedResults, out.resolve("tutor-relaxed.csv"));

        // Resumen comparativo Markdown
        String summary = buildSummary(pilot, real, tutor);
        Files.writeString(out.resolve("rq2-comparison-summary.md"), summary,
                StandardCharsets.UTF_8);

        // Metadata
        Instant finishedAt = Instant.now();
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("artifactType", "rq2-comparison");
        metadata.put("startedAt", startedAt.toString());
        metadata.put("finishedAt", finishedAt.toString());
        metadata.put("pilotCases",  pilotCases.size());
        metadata.put("realCases",   realCases.size());
        metadata.put("tutorCases",  tutorCases.size());
        metadata.put("note",
                "Comparativa STRICT (P1-P5) vs RELAXED (P1-P4 + P5'). "
                + "Valores de complejidad = estimacion proxy del prototipo.");
        Files.writeString(out.resolve("rq2-comparison-metadata.json"),
                GSON.toJson(metadata), StandardCharsets.UTF_8);

        // Resumen de consola
        System.out.println();
        printCorpusSummary("pilot", pilot);
        printCorpusSummary("real",  real);
        printCorpusSummary("tutor", tutor);
        System.out.println();
        System.out.println("Done. Artifacts in: " + out);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static CorpusComparison runComparison(String name,
                                                   List<ExperimentCase> cases,
                                                   BatchRunner strictRunner,
                                                   BatchRunner relaxedRunner) {
        return new CorpusComparison(
                name,
                strictRunner.run(cases),
                relaxedRunner.run(cases));
    }

    private static void printCorpusSummary(String name, CorpusComparison c) {
        System.out.printf("  [%s]%n", name.toUpperCase());
        System.out.printf("    STRICT  : eligible=%d/%d  delta=%+d%n",
                countEligible(c.strictResults),  c.strictResults.size(),  totalDelta(c.strictResults));
        System.out.printf("    RELAXED : eligible=%d/%d  delta=%+d%n",
                countEligible(c.relaxedResults), c.relaxedResults.size(), totalDelta(c.relaxedResults));
        System.out.printf("    Δ nuevos elegibles RELAXED−STRICT: %+d%n",
                countEligible(c.relaxedResults) - countEligible(c.strictResults));
    }

    private static String buildSummary(CorpusComparison pilot,
                                        CorpusComparison real,
                                        CorpusComparison tutor) {
        StringBuilder sb = new StringBuilder();
        sb.append("# RQ2 — Comparativa STRICT (P1–P5) vs RELAXED (P1–P4 + P5')\n\n");
        sb.append("> P5' acepta llamadas a métodos del allowlist ");
        sb.append("(`MethodCallAllowlist`) presumiblemente puros bajo convención JDK.\n\n");

        sb.append("## Tabla resumen por corpus\n\n");
        sb.append("| Corpus | N | STRICT elegibles | STRICT ΔCC | RELAXED elegibles | RELAXED ΔCC | Nuevos elegibles |\n");
        sb.append("|---|---|---|---|---|---|---|\n");
        appendCompRow(sb, pilot);
        appendCompRow(sb, real);
        appendCompRow(sb, tutor);
        sb.append("\n");

        sb.append("## Desglose por corpus\n\n");
        appendCorpusSection(sb, pilot);
        appendCorpusSection(sb, real);
        appendCorpusSection(sb, tutor);

        return sb.toString();
    }

    private static void appendCompRow(StringBuilder sb, CorpusComparison c) {
        long sElig  = countEligible(c.strictResults);
        long rElig  = countEligible(c.relaxedResults);
        int  sDelta = totalDelta(c.strictResults);
        int  rDelta = totalDelta(c.relaxedResults);
        int  n      = c.strictResults.size();

        sb.append("| ").append(c.name)
          .append(" | ").append(n)
          .append(" | ").append(sElig)
          .append(" | ").append(formatDelta(sDelta))
          .append(" | ").append(rElig)
          .append(" | ").append(formatDelta(rDelta))
          .append(" | ").append(formatDelta((int)(rElig - sElig)))
          .append(" |\n");
    }

    private static void appendCorpusSection(StringBuilder sb, CorpusComparison c) {
        sb.append("### Corpus: ").append(c.name).append("\n\n");
        sb.append("#### STRICT\n\n");
        appendResultTable(sb, c.strictResults);
        sb.append("\n#### RELAXED\n\n");
        appendResultTable(sb, c.relaxedResults);
        sb.append("\n");
    }

    private static void appendResultTable(StringBuilder sb,
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
        return results.stream().mapToInt(ExperimentResult::getDelta).sum();
    }

    // -------------------------------------------------------------------------
    // Data holder
    // -------------------------------------------------------------------------

    private static final class CorpusComparison {
        final String name;
        final List<ExperimentResult> strictResults;
        final List<ExperimentResult> relaxedResults;

        CorpusComparison(String name,
                         List<ExperimentResult> strictResults,
                         List<ExperimentResult> relaxedResults) {
            this.name = name;
            this.strictResults = new ArrayList<>(strictResults);
            this.relaxedResults = new ArrayList<>(relaxedResults);
        }
    }
}
