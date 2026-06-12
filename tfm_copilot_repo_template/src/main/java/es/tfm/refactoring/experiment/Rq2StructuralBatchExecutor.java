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
 * Variante de {@link Rq2BatchExecutor} que ejecuta el pipeline RQ2 con el
 * detector en modo {@link DetectionMode#STRUCTURAL} (solo precondiciones
 * estructurales P1–P4; P5 omitida).
 * <p>
 * <strong>Motivación.</strong> Tras la decisión metodológica confirmada con el
 * tutor, P5 (ausencia de efectos colaterales en las condiciones) no es necesaria
 * para la corrección: la transformación es un unfold de la semántica de
 * cortocircuito de {@code &&} ({@code A && B ≡ A ? B : false}), que preserva el
 * orden y la condicionalidad de evaluación con independencia de los efectos
 * colaterales. Este executor permite re-ejecutar RQ2 sin P5 para medir el
 * impacto en complejidad cognitiva sobre el conjunto ampliado de casos elegibles.
 * <p>
 * <strong>Aislamiento.</strong> Reutiliza los mismos componentes
 * ({@link PilotCorpusLoader}, {@link RealDatasetLoader}, {@link BatchRunner},
 * {@link ResultExporter}) y NO modifica el flujo STRICT de {@link Rq2BatchExecutor}.
 * La única diferencia es el {@link NestedIfDetector} inyectado en el
 * {@link BatchRunner} y la carpeta de salida por defecto ({@code output/rq2-structural}).
 * <p>
 * Uso:
 * <ul>
 *   <li>{@code java es.tfm.refactoring.experiment.Rq2StructuralBatchExecutor}</li>
 *   <li>{@code java es.tfm.refactoring.experiment.Rq2StructuralBatchExecutor output/rq2-structural}</li>
 *   <li>{@code java es.tfm.refactoring.experiment.Rq2StructuralBatchExecutor output/rq2-structural <dir-corpus>}
 *       — carga el corpus desde {@code <dir-corpus>/pilot-corpus} y
 *       {@code <dir-corpus>/real-corpus} en lugar del classpath.</li>
 * </ul>
 */
public class Rq2StructuralBatchExecutor {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    public static void main(String[] args) throws IOException {
        String outputDir = (args.length > 0 && !args[0].isBlank())
                ? args[0]
                : "output/rq2-structural";
        Path out = Path.of(outputDir).toAbsolutePath();
        Files.createDirectories(out);

        System.out.println("=== RQ2 Structural Batch Executor (P1-P4, sin P5) ===");
        System.out.println("Detection mode: " + DetectionMode.STRUCTURAL);
        System.out.println("Output directory: " + out);

        Instant startedAt = Instant.now();

        // 1. Cargar corpus: desde un directorio si se indica (args[1]), o del
        //    classpath (comportamiento por defecto).
        PilotCorpusLoader pilotLoader = new PilotCorpusLoader();
        RealDatasetLoader realLoader = new RealDatasetLoader();

        String corpusDir = (args.length > 1 && !args[1].isBlank()) ? args[1] : null;
        List<ExperimentCase> pilotCases;
        List<ExperimentCase> realCases;
        if (corpusDir != null) {
            Path base = Path.of(corpusDir).toAbsolutePath();
            pilotCases = pilotLoader.loadFromDirectory(base.resolve("pilot-corpus"));
            realCases = realLoader.loadFromDirectory(base.resolve("real-corpus"));
            System.out.println("Corpus directory: " + base);
        } else {
            pilotCases = pilotLoader.load(PilotCorpusLoader.standardPilotFiles());
            realCases = realLoader.load(RealDatasetLoader.standardRealFiles());
            System.out.println("Corpus: classpath (src/main/resources)");
        }

        List<ExperimentCase> allCases = new ArrayList<>();
        allCases.addAll(pilotCases);
        allCases.addAll(realCases);

        System.out.println("Pilot cases: " + pilotCases.size());
        System.out.println("Real cases:  " + realCases.size());
        System.out.println("Total:       " + allCases.size());

        // 2. Ejecutar pipeline con detector en modo STRUCTURAL
        BatchRunner runner = new BatchRunner(
                new NestedIfDetector(DetectionMode.STRUCTURAL),
                new NestedIfTransformer(),
                new CognitiveComplexityCalculator());
        List<ExperimentResult> pilotResults = runner.run(pilotCases);
        List<ExperimentResult> realResults = runner.run(realCases);

        List<ExperimentResult> allResults = new ArrayList<>();
        allResults.addAll(pilotResults);
        allResults.addAll(realResults);

        // 3. Exportar artefactos
        ResultExporter exporter = new ResultExporter();

        exporter.exportCsv(pilotResults, out.resolve("rq2-structural-pilot-results.csv"));
        exporter.exportJson(pilotResults, out.resolve("rq2-structural-pilot-results.json"));

        exporter.exportCsv(realResults, out.resolve("rq2-structural-real-results.csv"));
        exporter.exportJson(realResults, out.resolve("rq2-structural-real-results.json"));

        exporter.exportCsv(allResults, out.resolve("rq2-structural-all-results.csv"));
        exporter.exportJson(allResults, out.resolve("rq2-structural-all-results.json"));

        // 4. Resumen Markdown
        String summary = buildSummary(pilotResults, realResults, allResults);
        Files.writeString(out.resolve("rq2-structural-summary.md"), summary,
                StandardCharsets.UTF_8);

        // 5. Metadata
        Instant finishedAt = Instant.now();
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("artifactType", "rq2-structural-batch");
        metadata.put("detectionMode", DetectionMode.STRUCTURAL.name());
        metadata.put("startedAt", startedAt.toString());
        metadata.put("finishedAt", finishedAt.toString());
        metadata.put("pilotCases", pilotResults.size());
        metadata.put("realCases", realResults.size());
        metadata.put("totalCases", allResults.size());
        metadata.put("eligibleCount", countEligible(allResults));
        metadata.put("ineligibleCount", allResults.size() - countEligible(allResults));
        metadata.put("totalDelta", totalDelta(allResults));
        metadata.put("note", "Modo STRUCTURAL: solo precondiciones estructurales "
                + "P1-P4, P5 omitida (cortocircuito de && preserva el "
                + "comportamiento). Valores de complejidad cognitiva = estimacion "
                + "provisional del prototipo (proxy), no equivalente directa a "
                + "SonarQube/SonarLint.");
        Files.writeString(out.resolve("rq2-structural-run-metadata.json"),
                GSON.toJson(metadata), StandardCharsets.UTF_8);

        System.out.println();
        System.out.println("Artifacts written:");
        System.out.println("  - rq2-structural-pilot-results.csv / .json");
        System.out.println("  - rq2-structural-real-results.csv  / .json");
        System.out.println("  - rq2-structural-all-results.csv   / .json");
        System.out.println("  - rq2-structural-summary.md");
        System.out.println("  - rq2-structural-run-metadata.json");
        System.out.println();
        System.out.println("Done.");
    }

    private static String buildSummary(List<ExperimentResult> pilot,
                                       List<ExperimentResult> real,
                                       List<ExperimentResult> all) {
        StringBuilder sb = new StringBuilder();
        sb.append("# RQ2 — Resultados consolidados (modo STRUCTURAL, sin P5)\n\n");
        sb.append("**Modo de deteccion:** STRUCTURAL (solo P1-P4)\n\n");
        sb.append("**Total casos:** ").append(all.size())
          .append(" (piloto: ").append(pilot.size())
          .append(", real: ").append(real.size()).append(")\n\n");
        sb.append("**Elegibles:** ").append(countEligible(all))
          .append(" / ").append(all.size()).append("\n\n");
        sb.append("**Δ complejidad cognitiva agregado:** ")
          .append(totalDelta(all)).append("\n\n");
        sb.append("> Nota: P5 omitida; la correccion se apoya en el cortocircuito "
                + "de &&. Los valores de complejidad son estimaciones del "
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
