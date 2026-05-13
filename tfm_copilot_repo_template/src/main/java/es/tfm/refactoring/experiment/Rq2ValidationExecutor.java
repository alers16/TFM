package es.tfm.refactoring.experiment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import es.tfm.refactoring.detection.DetectionMode;
import es.tfm.refactoring.validation.RefactoringValidator;
import es.tfm.refactoring.validation.ValidationResult;

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
 * Ejecutor de validación dinámica (compilación) del código refactorizado.
 * <p>
 * Para cada caso elegible de los tres corpus (pilot, real, tutor) y para
 * ambos modos (STRICT y RELAXED), compila el output del transformador con
 * {@code javac} a través de {@link RefactoringValidator} y reporta PASS / FAIL / SKIPPED.
 * <p>
 * <strong>Propósito académico:</strong> evidencia mínima de corrección sintáctica
 * del output del prototipo. La validación de comportamiento (ejecución de tests del
 * proyecto fuente) queda como trabajo futuro por requerir la instalación de cada
 * proyecto origen.
 * <p>
 * Uso:
 * <ul>
 *   <li>{@code java es.tfm.refactoring.experiment.Rq2ValidationExecutor}</li>
 *   <li>{@code java es.tfm.refactoring.experiment.Rq2ValidationExecutor output/rq2-validation}</li>
 * </ul>
 */
public class Rq2ValidationExecutor {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .disableHtmlEscaping()
            .create();

    public static void main(String[] args) throws IOException {
        String outputDir = (args.length > 0 && !args[0].isBlank())
                ? args[0]
                : "output/rq2-validation";
        Path out = Path.of(outputDir).toAbsolutePath();
        Files.createDirectories(out);

        System.out.println("=== RQ2 Validation Executor (compilación dinámica) ===");
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

        RefactoringValidator strictValidator  = new RefactoringValidator(DetectionMode.STRICT);
        RefactoringValidator relaxedValidator = new RefactoringValidator(DetectionMode.RELAXED);

        // Validar cada corpus en ambos modos
        List<ValidationRow> rows = new ArrayList<>();
        rows.addAll(validateCorpus("pilot",  pilotCases,  strictValidator, relaxedValidator));
        rows.addAll(validateCorpus("real",   realCases,   strictValidator, relaxedValidator));
        rows.addAll(validateCorpus("tutor",  tutorCases,  strictValidator, relaxedValidator));

        // Exportar
        String summary = buildSummary(rows);
        Files.writeString(out.resolve("rq2-validation-summary.md"), summary,
                StandardCharsets.UTF_8);

        String json = GSON.toJson(rows);
        Files.writeString(out.resolve("rq2-validation-results.json"), json,
                StandardCharsets.UTF_8);

        Instant finishedAt = Instant.now();
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("artifactType", "rq2-validation");
        metadata.put("startedAt", startedAt.toString());
        metadata.put("finishedAt", finishedAt.toString());
        metadata.put("totalRows", rows.size());
        metadata.put("passStrict",
                rows.stream().filter(r -> r.statusStrict.equals("PASS")).count());
        metadata.put("passRelaxed",
                rows.stream().filter(r -> r.statusRelaxed.equals("PASS")).count());
        metadata.put("note",
                "Validacion dinamica: compilacion con javac del output del transformador. "
                + "No incluye ejecucion de tests del proyecto fuente (trabajo futuro).");
        Files.writeString(out.resolve("rq2-validation-metadata.json"),
                GSON.toJson(metadata), StandardCharsets.UTF_8);

        // Consola
        System.out.println();
        printConsole(rows);
        System.out.println();
        System.out.println("Done. Artifacts in: " + out);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static List<ValidationRow> validateCorpus(String corpus,
                                                       List<ExperimentCase> cases,
                                                       RefactoringValidator strict,
                                                       RefactoringValidator relaxed) {
        List<ValidationRow> rows = new ArrayList<>();
        for (ExperimentCase ec : cases) {
            ValidationResult sr = strict.validate(ec);
            ValidationResult rr = relaxed.validate(ec);
            rows.add(new ValidationRow(corpus, ec.getCaseId(), sr, rr));
        }
        return rows;
    }

    private static String buildSummary(List<ValidationRow> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("# RQ2 — Validación dinámica: compilación del código refactorizado\n\n");
        sb.append("> La validación comprueba que el output de `NestedIfTransformer` ");
        sb.append("sea Java compilable (`javac`). SKIPPED = caso no elegible (no se transformó).\n\n");

        // Tabla de totales
        sb.append("## Resumen por modo\n\n");
        sb.append("| Modo | PASS | FAIL | SKIPPED | ERROR |\n|---|---|---|---|---|\n");
        appendModeSummary(sb, "STRICT",  rows, true);
        appendModeSummary(sb, "RELAXED", rows, false);
        sb.append("\n");

        // Tabla detallada
        sb.append("## Resultados por caso\n\n");
        sb.append("| Corpus | caseId | STRICT | RELAXED | Diagnóstico (si FAIL/ERROR) |\n");
        sb.append("|---|---|---|---|---|\n");
        for (ValidationRow row : rows) {
            String diag = "";
            if (row.statusStrict.equals("FAIL") || row.statusStrict.equals("ERROR")) {
                diag = "STRICT: " + truncate(row.diagnosticsStrict, 80);
            }
            if (row.statusRelaxed.equals("FAIL") || row.statusRelaxed.equals("ERROR")) {
                if (!diag.isEmpty()) diag += " / ";
                diag += "RELAXED: " + truncate(row.diagnosticsRelaxed, 80);
            }
            sb.append("| ").append(row.corpus)
              .append(" | ").append(row.caseId)
              .append(" | ").append(row.statusStrict)
              .append(" | ").append(row.statusRelaxed)
              .append(" | ").append(diag)
              .append(" |\n");
        }

        sb.append("\n> **Nota metodológica:** esta validación cubre corrección sintáctica ");
        sb.append("y compilabilidad, no equivalencia de comportamiento en tiempo de ejecución. ");
        sb.append("La ejecución de tests del proyecto fuente (Apache Commons, Ant, BCV, jMetal) ");
        sb.append("requiere descargar y configurar cada proyecto y se deja como trabajo futuro.\n");

        return sb.toString();
    }

    private static void appendModeSummary(StringBuilder sb, String modeName,
                                           List<ValidationRow> rows, boolean strict) {
        long pass    = rows.stream().filter(r -> status(r, strict).equals("PASS")).count();
        long fail    = rows.stream().filter(r -> status(r, strict).equals("FAIL")).count();
        long skipped = rows.stream().filter(r -> status(r, strict).equals("SKIPPED")).count();
        long error   = rows.stream().filter(r -> status(r, strict).equals("ERROR")).count();
        sb.append("| ").append(modeName)
          .append(" | ").append(pass)
          .append(" | ").append(fail)
          .append(" | ").append(skipped)
          .append(" | ").append(error)
          .append(" |\n");
    }

    private static String status(ValidationRow r, boolean strict) {
        return strict ? r.statusStrict : r.statusRelaxed;
    }

    private static void printConsole(List<ValidationRow> rows) {
        System.out.printf(Locale.US, "  %-12s %-50s %-10s %-10s%n",
                "CORPUS", "CASE", "STRICT", "RELAXED");
        System.out.println("  " + "-".repeat(84));
        for (ValidationRow row : rows) {
            System.out.printf(Locale.US, "  %-12s %-50s %-10s %-10s%n",
                    row.corpus, row.caseId, row.statusStrict, row.statusRelaxed);
        }
    }

    private static String truncate(String s, int max) {
        if (s == null || s.length() <= max) return s == null ? "" : s;
        return s.substring(0, max - 3) + "...";
    }

    // -------------------------------------------------------------------------
    // Data holder (serializable a JSON)
    // -------------------------------------------------------------------------

    @SuppressWarnings("unused")
    static final class ValidationRow {
        final String corpus;
        final String caseId;
        final String statusStrict;
        final String diagnosticsStrict;
        final String statusRelaxed;
        final String diagnosticsRelaxed;

        ValidationRow(String corpus, String caseId,
                      ValidationResult strict, ValidationResult relaxed) {
            this.corpus             = corpus;
            this.caseId             = caseId;
            this.statusStrict       = strict.getStatus().name();
            this.diagnosticsStrict  = strict.getDiagnostics();
            this.statusRelaxed      = relaxed.getStatus().name();
            this.diagnosticsRelaxed = relaxed.getDiagnostics();
        }
    }
}
