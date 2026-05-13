package es.tfm.refactoring.experiment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Exporta los resultados del experimento en formato JSON y CSV
 * para garantizar la reproducibilidad del protocolo experimental.
 * <p>
 * <strong>JSON:</strong> incluye todos los campos, incluyendo código fuente
 * antes y después de la refactorización. Formato principal para trazabilidad
 * completa y paquete de replicación.
 * <p>
 * <strong>CSV:</strong> excluye código fuente (multi-línea) para facilitar
 * análisis tabular. Incluye métricas, metadatos y observaciones.
 */
public class ResultExporter {

    private static final String CSV_HEADER = String.join(",",
            "caseId", "origin", "description", "eligible", "discardReason",
            "discardCategories",
            "opportunitiesDetected", "opportunitiesApplied", "totalPasses",
            "complexityBefore", "complexityAfter", "delta",
            "methodSignature", "observations");

    private final Gson gson;

    public ResultExporter() {
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping()
                .create();
    }

    /**
     * Serializa los resultados a JSON (incluye código fuente).
     *
     * @param results lista de resultados
     * @return cadena JSON formateada
     */
    public String toJson(List<ExperimentResult> results) {
        return gson.toJson(results);
    }

    /**
     * Serializa los resultados a CSV (excluye código fuente).
     * Formato RFC 4180: campos con comas, comillas o saltos de línea
     * se encierran entre comillas dobles.
     *
     * @param results lista de resultados
     * @return cadena CSV con cabecera
     */
    public String toCsv(List<ExperimentResult> results) {
        StringBuilder sb = new StringBuilder();
        sb.append(CSV_HEADER).append("\n");
        for (ExperimentResult r : results) {
            sb.append(csvLine(r)).append("\n");
        }
        return sb.toString();
    }

    /**
     * Exporta los resultados a un archivo JSON.
     *
     * @param results    lista de resultados
     * @param outputPath ruta del archivo de salida
     * @throws IOException si falla la escritura
     */
    public void exportJson(List<ExperimentResult> results, Path outputPath)
            throws IOException {
        Files.createDirectories(outputPath.getParent());
        Files.writeString(outputPath, toJson(results), StandardCharsets.UTF_8);
    }

    /**
     * Exporta los resultados a un archivo CSV.
     *
     * @param results    lista de resultados
     * @param outputPath ruta del archivo de salida
     * @throws IOException si falla la escritura
     */
    public void exportCsv(List<ExperimentResult> results, Path outputPath)
            throws IOException {
        Files.createDirectories(outputPath.getParent());
        Files.writeString(outputPath, toCsv(results), StandardCharsets.UTF_8);
    }

    // =========================================================================
    // CSV internals
    // =========================================================================

    private String csvLine(ExperimentResult r) {
        return String.join(",",
                csvField(r.getCaseId()),
                csvField(r.getOrigin()),
                csvField(r.getDescription()),
                String.valueOf(r.isEligible()),
                csvField(r.getDiscardReason()),
                csvField(String.join(";", r.getDiscardCategories())),
                String.valueOf(r.getOpportunitiesDetected()),
                String.valueOf(r.getOpportunitiesApplied()),
                String.valueOf(r.getTotalPasses()),
                String.valueOf(r.getComplexityBefore()),
                String.valueOf(r.getComplexityAfter()),
                String.valueOf(r.getDelta()),
                csvField(r.getMethodSignature()),
                csvField(r.getObservations()));
    }

    /**
     * Escapa un campo CSV según RFC 4180.
     * Si el campo contiene coma, comilla doble o salto de línea,
     * se encierra entre comillas dobles y las comillas internas se duplican.
     */
    private String csvField(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /** Devuelve la cabecera CSV (útil para tests). */
    String getCsvHeader() {
        return CSV_HEADER;
    }
}
