package es.tfm.refactoring.experiment;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del exportador de resultados experimentales (JSON y CSV).
 * Verifica: formato correcto, campos completos, escaping y escritura a fichero.
 */
@DisplayName("ResultExporter — Exportación JSON/CSV")
class ResultExporterTest {

    private ResultExporter exporter;
    private List<ExperimentResult> sampleResults;

    @BeforeEach
    void setUp() {
        exporter = new ResultExporter();
        BatchRunner runner = new BatchRunner();
        sampleResults = runner.run(SeedCorpus.loadAll());
    }

    // =========================================================================
    // JSON
    // =========================================================================

    @Nested
    @DisplayName("Exportación JSON")
    class JsonExport {

        @Test
        @DisplayName("JSON es parseable y contiene un array de 10 elementos")
        void jsonIsParseableArray() {
            String json = exporter.toJson(sampleResults);

            JsonElement parsed = JsonParser.parseString(json);
            assertTrue(parsed.isJsonArray(), "El JSON debe ser un array");
            assertEquals(10, parsed.getAsJsonArray().size());
        }

        @Test
        @DisplayName("Cada resultado JSON contiene todos los campos requeridos")
        void jsonContainsAllFields() {
            String json = exporter.toJson(sampleResults);
            JsonArray array = JsonParser.parseString(json).getAsJsonArray();

            String[] requiredFields = {
                    "caseId", "origin", "description", "eligible",
                    "discardReason", "discardCategories",
                    "opportunitiesDetected", "opportunitiesApplied",
                    "totalPasses", "complexityBefore", "complexityAfter", "delta",
                    "methodSignature", "sourceBefore", "sourceAfter", "observations"
            };

            for (JsonElement elem : array) {
                JsonObject obj = elem.getAsJsonObject();
                for (String field : requiredFields) {
                    assertTrue(obj.has(field),
                            "Falta campo '" + field + "' en "
                                    + obj.get("caseId").getAsString());
                }
            }
        }

        @Test
        @DisplayName("Caso elegible tiene discardReason null en JSON")
        void eligibleCaseNullDiscardReason() {
            String json = exporter.toJson(sampleResults);
            JsonArray array = JsonParser.parseString(json).getAsJsonArray();

            for (JsonElement elem : array) {
                JsonObject obj = elem.getAsJsonObject();
                if (obj.get("eligible").getAsBoolean()) {
                    assertTrue(obj.get("discardReason").isJsonNull(),
                            obj.get("caseId").getAsString()
                                    + ": discardReason debería ser null");
                }
            }
        }

        @Test
        @DisplayName("Caso inelegible tiene discardReason no null en JSON")
        void ineligibleCaseHasDiscardReason() {
            String json = exporter.toJson(sampleResults);
            JsonArray array = JsonParser.parseString(json).getAsJsonArray();

            for (JsonElement elem : array) {
                JsonObject obj = elem.getAsJsonObject();
                if (!obj.get("eligible").getAsBoolean()
                        && obj.get("complexityBefore").getAsInt() >= 0) {
                    assertFalse(obj.get("discardReason").isJsonNull(),
                            obj.get("caseId").getAsString()
                                    + ": discardReason no debería ser null");
                }
            }
        }

        @Test
        @DisplayName("JSON incluye código fuente con comillas y saltos de línea")
        void jsonHandlesSpecialCharacters() {
            String json = exporter.toJson(sampleResults);

            // Verify it's valid JSON (would throw on invalid escaping)
            JsonArray array = JsonParser.parseString(json).getAsJsonArray();

            // Check that at least one result has sourceBefore with content
            boolean hasSource = false;
            for (JsonElement elem : array) {
                JsonObject obj = elem.getAsJsonObject();
                if (!obj.get("sourceBefore").isJsonNull()) {
                    String source = obj.get("sourceBefore").getAsString();
                    assertTrue(source.length() > 10,
                            "sourceBefore debería contener código real");
                    hasSource = true;
                    break;
                }
            }
            assertTrue(hasSource, "Al menos un resultado debe tener sourceBefore");
        }

        @Test
        @DisplayName("Lista vacía produce array JSON vacío")
        void emptyListProducesEmptyArray() {
            String json = exporter.toJson(List.of());
            assertEquals("[]", json);
        }
    }

    // =========================================================================
    // CSV
    // =========================================================================

    @Nested
    @DisplayName("Exportación CSV")
    class CsvExport {

        @Test
        @DisplayName("CSV tiene cabecera correcta con 14 columnas")
        void csvHasCorrectHeader() {
            String csv = exporter.toCsv(sampleResults);
            String[] lines = csv.split("\n");

            assertTrue(lines.length > 0);
            String header = lines[0];
            assertEquals(exporter.getCsvHeader(), header);
            assertEquals(14, header.split(",").length,
                    "La cabecera debe tener 14 columnas");
        }

        @Test
        @DisplayName("CSV tiene 10 filas de datos + 1 cabecera")
        void csvHasCorrectRowCount() {
            String csv = exporter.toCsv(sampleResults);
            String[] lines = csv.split("\n");

            // Header + 10 data rows
            assertEquals(11, lines.length);
        }

        @Test
        @DisplayName("Campos con comas se encierran entre comillas")
        void csvEscapesCommas() {
            // Create a result with commas in description
            ExperimentResult result = new ExperimentResult(
                    "TEST_COMMA", "test", "Descripción con, coma",
                    false, "reason, with comma",
                    0, 0, 0, 1, 1, 0,
                    "void test()", "before", "after", "obs",
                    List.of("SOME_REASON"));

            String csv = exporter.toCsv(List.of(result));
            String dataLine = csv.split("\n")[1];

            assertTrue(dataLine.contains("\"Descripción con, coma\""),
                    "Campos con comas deben estar entre comillas");
            assertTrue(dataLine.contains("\"reason, with comma\""),
                    "Campos con comas deben estar entre comillas");
        }

        @Test
        @DisplayName("Campos null se exportan como vacío")
        void csvHandlesNullFields() {
            // Eligible case has null discardReason
            String csv = exporter.toCsv(sampleResults);
            // Should not contain "null" as literal text in eligible cases
            String[] lines = csv.split("\n");
            for (int i = 1; i < lines.length; i++) {
                assertFalse(lines[i].contains("null"),
                        "CSV no debe contener 'null' como texto literal: " + lines[i]);
            }
        }

        @Test
        @DisplayName("Lista vacía produce solo cabecera")
        void emptyListProducesHeaderOnly() {
            String csv = exporter.toCsv(List.of());
            String[] lines = csv.split("\n");
            assertEquals(1, lines.length);
            assertEquals(exporter.getCsvHeader(), lines[0]);
        }
    }

    // =========================================================================
    // Escritura a fichero
    // =========================================================================

    @Nested
    @DisplayName("Exportación a fichero")
    class FileExport {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("exportJson crea archivo con contenido JSON válido")
        void exportJsonToFile() throws IOException {
            Path jsonPath = tempDir.resolve("results").resolve("output.json");

            exporter.exportJson(sampleResults, jsonPath);

            assertTrue(Files.exists(jsonPath));
            String content = Files.readString(jsonPath, StandardCharsets.UTF_8);
            JsonArray array = JsonParser.parseString(content).getAsJsonArray();
            assertEquals(10, array.size());
        }

        @Test
        @DisplayName("exportCsv crea archivo con contenido CSV correcto")
        void exportCsvToFile() throws IOException {
            Path csvPath = tempDir.resolve("results").resolve("output.csv");

            exporter.exportCsv(sampleResults, csvPath);

            assertTrue(Files.exists(csvPath));
            String content = Files.readString(csvPath, StandardCharsets.UTF_8);
            assertTrue(content.startsWith(exporter.getCsvHeader()));
            // Header + 10 data rows
            assertEquals(11, content.split("\n").length);
        }

        @Test
        @DisplayName("exportJson crea directorios intermedios")
        void jsonCreatesParentDirectories() throws IOException {
            Path nested = tempDir.resolve("a").resolve("b").resolve("c").resolve("out.json");

            exporter.exportJson(sampleResults, nested);

            assertTrue(Files.exists(nested));
        }
    }

    // =========================================================================
    // Trazabilidad: coherencia JSON ↔ CSV
    // =========================================================================

    @Nested
    @DisplayName("Trazabilidad entre formatos")
    class Traceability {

        @Test
        @DisplayName("Todos los caseId del JSON aparecen en el CSV")
        void caseIdsConsistentAcrossFormats() {
            String json = exporter.toJson(sampleResults);
            String csv = exporter.toCsv(sampleResults);

            JsonArray array = JsonParser.parseString(json).getAsJsonArray();
            for (JsonElement elem : array) {
                String caseId = elem.getAsJsonObject().get("caseId").getAsString();
                assertTrue(csv.contains(caseId),
                        "caseId '" + caseId + "' del JSON debe aparecer en CSV");
            }
        }
    }
}
