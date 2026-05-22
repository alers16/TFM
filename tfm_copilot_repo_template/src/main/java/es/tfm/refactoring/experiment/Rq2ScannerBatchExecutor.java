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
 * Ejecutor de RQ2 para el corpus generado automaticamente por
 * {@link es.tfm.refactoring.scanner.CorpusFileGenerator} a partir del CSV
 * producido por {@link es.tfm.refactoring.scanner.CorpusScanRunner}.
 * <p>
 * Lee todos los ficheros {@code .java} del directorio de corpus generado
 * (por defecto {@code output/corpus-scan/generated-corpus/}), ejecuta el
 * mismo analisis que {@link Rq2TutorBatchExecutor} y escribe los resultados
 * en {@code output/rq2-scan/}.
 * <p>
 * Este executor completa el pipeline automatico del orquestador:
 * <pre>
 *   CorpusScanRunner  →  CorpusFileGenerator  →  Rq2ScannerBatchExecutor
 * </pre>
 * <p>
 * Uso:
 * <ul>
 *   <li>{@code java es.tfm.refactoring.experiment.Rq2ScannerBatchExecutor}</li>
 *   <li>{@code java es.tfm.refactoring.experiment.Rq2ScannerBatchExecutor output/rq2-scan}</li>
 *   <li>{@code java es.tfm.refactoring.experiment.Rq2ScannerBatchExecutor output/rq2-scan output/corpus-scan/generated-corpus}</li>
 * </ul>
 */
public class Rq2ScannerBatchExecutor {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    public static void main(String[] args) throws IOException {
        String outputDir  = (args.length > 0 && !args[0].isBlank())
                ? args[0]
                : "output/rq2-scan";
        String corpusDir  = (args.length > 1 && !args[1].isBlank())
                ? args[1]
                : "output/corpus-scan/generated-corpus";

        Path out    = Path.of(outputDir).toAbsolutePath();
        Path corpus = Path.of(corpusDir).toAbsolutePath();

        Files.createDirectories(out);

        System.out.println("=== RQ2 Scanner Batch Executor ===");
        System.out.println("Corpus dir:       " + corpus);
        System.out.println("Output directory: " + out);

        if (!Files.isDirectory(corpus)) {
            System.out.println();
            System.out.println("[SKIP] El directorio de corpus generado no existe: " + corpus);
            System.out.println("       Ejecuta primero CorpusScanRunner + CorpusFileGenerator");
            System.out.println("       o usa el orquestador con --rq2 o --scan.");
            return;
        }

        Instant startedAt = Instant.now();

        TutorCorpusLoader loader = new TutorCorpusLoader();
        List<ExperimentCase> cases = loader.loadAllFromDir(corpus);

        if (cases.isEmpty()) {
            System.out.println();
            System.out.println("[SKIP] No se encontraron ficheros .java en: " + corpus);
            System.out.println("       El corpus puede estar vacio si ningun proyecto estaba clonado.");
            return;
        }

        System.out.println("Scanner-generated cases: " + cases.size());

        BatchRunner runner = new BatchRunner();
        List<ExperimentResult> results = runner.run(cases);

        ResultExporter exporter = new ResultExporter();
        exporter.exportCsv(results, out.resolve("rq2-scan-results.csv"));
        exporter.exportJson(results, out.resolve("rq2-scan-results.json"));

        String summary = buildSummary(results, corpus.toString());
        Files.writeString(out.resolve("rq2-scan-summary.md"), summary,
                StandardCharsets.UTF_8);

        Instant finishedAt = Instant.now();
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("artifactType", "rq2-scanner-batch");
        metadata.put("startedAt", startedAt.toString());
        metadata.put("finishedAt", finishedAt.toString());
        metadata.put("corpusDir", corpus.toString());
        metadata.put("totalCases", results.size());
        metadata.put("eligibleCount", countEligible(results));
        metadata.put("ineligibleCount", results.size() - countEligible(results));
        metadata.put("totalDelta", totalDelta(results));
        metadata.put("note",
                "Corpus generado automaticamente por CorpusScanRunner + CorpusFileGenerator. "
                + "Los valores de CC son estimaciones proxy del prototipo, no equivalentes "
                + "directas a SonarQube. Revisar casos elegibles antes de incluir en corpus oficial.");
        Files.writeString(out.resolve("rq2-scan-run-metadata.json"),
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

    private static String buildSummary(List<ExperimentResult> results, String corpusDir) {
        StringBuilder sb = new StringBuilder();
        sb.append("# RQ2 - Corpus generado por escaner\n\n");
        sb.append("> Fuente: `").append(corpusDir).append("`\n\n");
        sb.append("**Total casos:** ").append(results.size()).append("\n\n");
        sb.append("**Elegibles:** ").append(countEligible(results))
          .append(" / ").append(results.size()).append("\n\n");
        sb.append("**Delta complejidad cognitiva agregado:** ")
          .append(totalDelta(results)).append("\n\n");
        sb.append("> Nota: valores de complejidad = estimacion proxy del prototipo, "
                + "no equivalente directa a SonarQube.\n");
        sb.append("> Los casos marcados como elegibles deben revisarse antes de "
                + "incluirse en el corpus oficial.\n\n");

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
