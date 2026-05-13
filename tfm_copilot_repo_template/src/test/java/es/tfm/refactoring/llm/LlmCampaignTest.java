package es.tfm.refactoring.llm;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Ejecución controlada de la primera campaña experimental de RQ3.
 * <p>
 * Este test ejecuta el protocolo completo sobre el subset de 6 casos
 * con respuestas pre-grabadas de GPT-4o y GPT-4.1, valida
 * los resultados con el oráculo, genera reportes agregados y exporta
 * evidencia reproducible.
 * <p>
 * <strong>RQ3:</strong> ¿Pueden los grandes modelos de lenguaje realizar
 * esta refactorización de forma correcta y automática?
 */
@DisplayName("RQ3 Campaign — Primera ejecución controlada")
class LlmCampaignTest {

    private static LlmExperimentProtocol protocol;
    private static List<LlmInvocationRecord> campaignResults;
    private static LlmCampaignReport report;

    @BeforeAll
    static void executeCampaign() throws IOException {
        protocol = LlmExperimentProtocol.defaultProtocol();
        LlmCampaignRunner runner = new LlmCampaignRunner();
        PrerecordedResponseProvider provider = new PrerecordedResponseProvider();

        Map<String, String> modelVersions = Map.of(
                "gpt-4o", "gpt-4o-2024-05-13",
                "gpt-4.1", "gpt-4.1-2025-04-14"
        );

        campaignResults = runner.executeCampaign(protocol, provider, modelVersions);
        report = new LlmCampaignReport(campaignResults, protocol);
    }

    // =========================================================================
    // Validación de la ejecución completa
    // =========================================================================

    @Nested
    @DisplayName("Ejecución completa del protocolo")
    class ExecutionTests {

        @Test
        @DisplayName("Se ejecutan exactamente 36 invocaciones (6 casos × 2 modelos × 3 intentos)")
        void totalInvocations() {
            assertEquals(36, campaignResults.size());
        }

        @Test
        @DisplayName("Todos los modelos del protocolo fueron evaluados")
        void allModelsEvaluated() {
            List<String> models = report.getModels();
            assertTrue(models.contains("gpt-4o"));
            assertTrue(models.contains("gpt-4.1"));
            assertEquals(2, models.size());
        }

        @Test
        @DisplayName("Todos los casos del subset fueron evaluados")
        void allCasesEvaluated() {
            List<String> cases = report.getCaseIds();
            assertEquals(6, cases.size());
            for (String id : LlmEvaluationSubset.caseIds()) {
                assertTrue(cases.contains(id), "Falta caso: " + id);
            }
        }

        @Test
        @DisplayName("Cada caso tiene 3 intentos por modelo")
        void threeAttemptsPerModelCase() {
            for (String model : protocol.getModels()) {
                for (String caseId : LlmEvaluationSubset.caseIds()) {
                    long count = campaignResults.stream()
                            .filter(r -> r.getModel().equals(model))
                            .filter(r -> r.getCaseId().equals(caseId))
                            .count();
                    assertEquals(3, count,
                            model + "/" + caseId + " debe tener 3 intentos");
                }
            }
        }

        @Test
        @DisplayName("Ninguna invocación tiene veredicto ERROR")
        void noErrors() {
            long errors = campaignResults.stream()
                    .filter(r -> r.getVerdict() == LlmVerdict.ERROR)
                    .count();
            assertEquals(0, errors, "No debe haber errores técnicos");
        }

        @Test
        @DisplayName("Todas las invocaciones tienen timestamp")
        void allHaveTimestamps() {
            assertTrue(campaignResults.stream()
                    .allMatch(r -> r.getExecutionTimestamp() != null));
        }

        @Test
        @DisplayName("Todas las invocaciones tienen modelo y versión")
        void allHaveModelVersion() {
            assertTrue(campaignResults.stream()
                    .allMatch(r -> r.getModelVersion() != null
                            && !r.getModelVersion().isEmpty()));
        }

        @Test
        @DisplayName("Prompt version es v1.0 en todos los resultados")
        void promptVersionConsistent() {
            assertTrue(campaignResults.stream()
                    .allMatch(r -> "v1.0".equals(
                            r.getEvaluationResult().getPromptVersion())));
        }
    }

    // =========================================================================
    // Validación de resultados por caso — GPT-4o
    // =========================================================================

    @Nested
    @DisplayName("Resultados GPT-4o — Casos elegibles")
    class GptEligibleTests {

        @Test
        @DisplayName("PILOT_VALID_SIMPLE → SUCCESS (refactorización correcta)")
        void pilotValidSimple() {
            assertVerdictConsistent("gpt-4o", "PILOT_VALID_SIMPLE",
                    LlmVerdict.SUCCESS);
        }

        @Test
        @DisplayName("PILOT_VALID_NESTED_IN_LOOP → SUCCESS (refactorización en loop)")
        void pilotValidNestedInLoop() {
            assertVerdictConsistent("gpt-4o", "PILOT_VALID_NESTED_IN_LOOP",
                    LlmVerdict.SUCCESS);
        }

        @Test
        @DisplayName("REAL_COMMONS_MATH_CONVERGED → SUCCESS (convergencia numérica)")
        void realCommonsMathConverged() {
            assertVerdictConsistent("gpt-4o", "REAL_COMMONS_MATH_CONVERGED",
                    LlmVerdict.SUCCESS);
        }

        @Test
        @DisplayName("REAL_ANT_MATCH_PATH → GPT-4o combina los tres niveles (incluye method call)")
        void realAntMatchPath() {
            // GPT-4o combina los 3 niveles incluyendo path.length() — method call
            // El oráculo evalúa la reducción de CC resultante
            List<LlmInvocationRecord> records = filterRecords("gpt-4o",
                    "REAL_ANT_MATCH_PATH");
            assertTrue(records.stream()
                            .allMatch(r -> r.getEvaluationResult().isParseable()),
                    "Todas las respuestas deben ser parseables");
            // GPT-4o reduces more aggressively by combining all three
            assertTrue(records.stream()
                            .allMatch(r -> r.getEvaluationResult().getDelta() < 0),
                    "Debe reducir CC");
        }
    }

    @Nested
    @DisplayName("Resultados GPT-4o — Casos inelegibles")
    class GptIneligibleTests {

        @Test
        @DisplayName("REAL_COMMONS_MATH_VALIDATE_RANGE → SUCCESS (rechazo correcto)")
        void validateRange() {
            assertVerdictConsistent("gpt-4o", "REAL_COMMONS_MATH_VALIDATE_RANGE",
                    LlmVerdict.SUCCESS);
        }

        @Test
        @DisplayName("REAL_COMMONS_COLLECTIONS_GET → INCORRECT (transformó caso inelegible)")
        void collectionsGet() {
            // GPT-4o combina map!=null && map.containsKey() — no reconoce P5
            // Oráculo v1.1: caso inelegible transformado → INCORRECT
            assertVerdictConsistent("gpt-4o", "REAL_COMMONS_COLLECTIONS_GET",
                    LlmVerdict.INCORRECT);
        }
    }

    // =========================================================================
    // Validación de resultados por caso — GPT-4.1
    // =========================================================================

    @Nested
    @DisplayName("Resultados GPT-4.1 — Casos elegibles")
    class Gpt41EligibleTests {

        @Test
        @DisplayName("PILOT_VALID_SIMPLE → SUCCESS")
        void pilotValidSimple() {
            assertVerdictConsistent("gpt-4.1", "PILOT_VALID_SIMPLE",
                    LlmVerdict.SUCCESS);
        }

        @Test
        @DisplayName("PILOT_VALID_NESTED_IN_LOOP → SUCCESS")
        void pilotValidNestedInLoop() {
            assertVerdictConsistent("gpt-4.1",
                    "PILOT_VALID_NESTED_IN_LOOP", LlmVerdict.SUCCESS);
        }

        @Test
        @DisplayName("REAL_COMMONS_MATH_CONVERGED → SUCCESS")
        void realCommonsMathConverged() {
            assertVerdictConsistent("gpt-4.1",
                    "REAL_COMMONS_MATH_CONVERGED", LlmVerdict.SUCCESS);
        }

        @Test
        @DisplayName("REAL_ANT_MATCH_PATH → GPT-4.1 combina solo el primer par (prudente)")
        void realAntMatchPath() {
            // GPT-4.1 preserva el tercer nivel como nested if separado
            List<LlmInvocationRecord> records = filterRecords("gpt-4.1",
                    "REAL_ANT_MATCH_PATH");
            assertTrue(records.stream()
                    .allMatch(r -> r.getEvaluationResult().isParseable()));
        }
    }

    @Nested
    @DisplayName("Resultados GPT-4.1 — Casos inelegibles")
    class Gpt41IneligibleTests {

        @Test
        @DisplayName("REAL_COMMONS_MATH_VALIDATE_RANGE → SUCCESS (rechazo correcto)")
        void validateRange() {
            assertVerdictConsistent("gpt-4.1",
                    "REAL_COMMONS_MATH_VALIDATE_RANGE", LlmVerdict.SUCCESS);
        }

        @Test
        @DisplayName("REAL_COMMONS_COLLECTIONS_GET → SUCCESS (rechazo correcto)")
        void collectionsGet() {
            // GPT-4.1 rechaza correctamente por method call en condición
            assertVerdictConsistent("gpt-4.1",
                    "REAL_COMMONS_COLLECTIONS_GET", LlmVerdict.SUCCESS);
        }
    }

    // =========================================================================
    // Métricas agregadas
    // =========================================================================

    @Nested
    @DisplayName("Métricas agregadas de la campaña")
    class AggregateTests {

        @Test
        @DisplayName("Reporte contiene resumen textual no vacío")
        void summaryNotEmpty() {
            String summary = report.toSummaryString();
            assertNotNull(summary);
            assertTrue(summary.contains("CAMPAIGN REPORT"));
            assertTrue(summary.contains("gpt-4o"));
            assertTrue(summary.contains("gpt-4.1"));
        }

        @Test
        @DisplayName("Tasa de éxito de ambos modelos es mayor que 0")
        void successRatePositive() {
            assertTrue(report.getSuccessRate("gpt-4o") > 0,
                    "GPT-4o debe tener algún éxito");
            assertTrue(report.getSuccessRate("gpt-4.1") > 0,
                    "GPT-4.1 debe tener algún éxito");
        }

        @Test
        @DisplayName("Consistencia es 100% con temperatura 0")
        void consistencyWithTempZero() {
            // Con temperatura 0 y respuestas pre-grabadas idénticas,
            // los 3 intentos dan el mismo veredicto → 100% consistencia
            assertEquals(1.0, report.getConsistencyRate("gpt-4o"), 0.01);
            assertEquals(1.0, report.getConsistencyRate("gpt-4.1"), 0.01);
        }

        @Test
        @DisplayName("Conteo exacto de veredictos por modelo")
        void exactVerdictCountsPerModel() {
            Map<LlmVerdict, Integer> gptCounts = report.getVerdictCounts("gpt-4o");
            Map<LlmVerdict, Integer> gpt41Counts = report.getVerdictCounts(
                    "gpt-4.1");

            // GPT-4o: 5 casos SUCCESS × 3 = 15, 1 caso INCORRECT × 3 = 3
            assertEquals(15, gptCounts.getOrDefault(LlmVerdict.SUCCESS, 0),
                    "GPT-4o: 5 casos×3 intentos SUCCESS");
            assertEquals(3, gptCounts.getOrDefault(LlmVerdict.INCORRECT, 0),
                    "GPT-4o: COLLECTIONS_GET×3 intentos INCORRECT");
            // GPT-4.1: 6 casos SUCCESS × 3 = 18
            assertEquals(18, gpt41Counts.getOrDefault(LlmVerdict.SUCCESS, 0),
                    "GPT-4.1: 6 casos×3 intentos SUCCESS");
            assertEquals(0, gpt41Counts.getOrDefault(LlmVerdict.INCORRECT, 0),
                    "GPT-4.1: 0 INCORRECT");
        }

        @Test
        @DisplayName("Veredicto counts por modelo suman 18 (6 casos × 3 intentos)")
        void verdictCountsSumCorrectly() {
            for (String model : protocol.getModels()) {
                Map<LlmVerdict, Integer> counts = report.getVerdictCounts(model);
                int total = counts.values().stream()
                        .mapToInt(Integer::intValue).sum();
                assertEquals(18, total,
                        model + ": veredictos deben sumar 18");
            }
        }
    }

    // =========================================================================
    // Exportación
    // =========================================================================

    @Nested
    @DisplayName("Exportación de evidencia y reportes")
    class ExportTests {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("JSON de evidencia completa es generado y no vacío")
        void fullEvidenceJson() {
            LlmCampaignExporter exporter = new LlmCampaignExporter();
            String json = exporter.toFullEvidenceJson(campaignResults);
            assertNotNull(json);
            assertTrue(json.contains("caseId"));
            assertTrue(json.contains("rawLlmOutput"));
            assertTrue(json.contains("verdict"));
            assertTrue(json.contains("promptSent"));
        }

        @Test
        @DisplayName("CSV resumen tiene cabecera y 36 líneas de datos")
        void summaryCsv() {
            LlmCampaignExporter exporter = new LlmCampaignExporter();
            String csv = exporter.toSummaryCsv(campaignResults);
            String[] lines = csv.strip().split("\n");
            assertEquals(37, lines.length, "1 cabecera + 36 datos");
            assertTrue(lines[0].contains("caseId"));
            assertTrue(lines[0].contains("verdict"));
        }

        @Test
        @DisplayName("Markdown agregado contiene tablas por modelo y por caso")
        void aggregatedMarkdown() {
            LlmCampaignExporter exporter = new LlmCampaignExporter();
            String md = exporter.toAggregatedMarkdown(report);
            assertTrue(md.contains("gpt-4o"));
            assertTrue(md.contains("gpt-4.1"));
            assertTrue(md.contains("PILOT_VALID_SIMPLE"));
            assertTrue(md.contains("Results by Model"));
            assertTrue(md.contains("Results by Case"));
        }

        @Test
        @DisplayName("Exportación a ficheros funciona correctamente")
        void fileExport() throws IOException {
            LlmCampaignExporter exporter = new LlmCampaignExporter();

            Path jsonPath = tempDir.resolve("evidence.json");
            Path csvPath = tempDir.resolve("summary.csv");
            Path mdPath = tempDir.resolve("aggregated.md");

            exporter.exportFullEvidenceJson(campaignResults, jsonPath);
            exporter.exportSummaryCsv(campaignResults, csvPath);
            exporter.exportAggregatedMarkdown(report, mdPath);

            assertTrue(Files.exists(jsonPath));
            assertTrue(Files.exists(csvPath));
            assertTrue(Files.exists(mdPath));
            assertTrue(Files.size(jsonPath) > 100);
            assertTrue(Files.size(csvPath) > 100);
            assertTrue(Files.size(mdPath) > 100);
        }
    }

    // =========================================================================
    // PrerecordedResponseProvider
    // =========================================================================

    @Nested
    @DisplayName("PrerecordedResponseProvider — Cobertura de respuestas")
    class ProviderTests {

        @Test
        @DisplayName("Tiene 12 respuestas únicas (6 casos × 2 modelos)")
        void responseCount() {
            PrerecordedResponseProvider provider = new PrerecordedResponseProvider();
            assertEquals(12, provider.getResponseCount());
        }

        @Test
        @DisplayName("Cada modelo×caso tiene respuesta no nula")
        void allResponsesPresent() {
            PrerecordedResponseProvider provider = new PrerecordedResponseProvider();
            for (String model : protocol.getModels()) {
                for (String caseId : LlmEvaluationSubset.caseIds()) {
                    String response = provider.getResponse(
                            model, caseId, 1, "prompt");
                    assertNotNull(response,
                            "Falta respuesta: " + model + "/" + caseId);
                }
            }
        }

        @Test
        @DisplayName("Respuestas son idénticas para los 3 intentos (temperatura 0)")
        void identicalAcrossAttempts() {
            PrerecordedResponseProvider provider = new PrerecordedResponseProvider();
            for (String model : protocol.getModels()) {
                for (String caseId : LlmEvaluationSubset.caseIds()) {
                    String r1 = provider.getResponse(model, caseId, 1, "p");
                    String r2 = provider.getResponse(model, caseId, 2, "p");
                    String r3 = provider.getResponse(model, caseId, 3, "p");
                    assertEquals(r1, r2);
                    assertEquals(r2, r3);
                }
            }
        }
    }

    // =========================================================================
    // LlmCampaignRunner — Infrastructure tests
    // =========================================================================

    @Nested
    @DisplayName("LlmCampaignRunner — Carga de baselines")
    class RunnerTests {

        @Test
        @DisplayName("loadCasesWithBaselines carga 6 casos con baselines correctos")
        void loadBaselines() throws IOException {
            LlmCampaignRunner runner = new LlmCampaignRunner();
            var cases = runner.loadCasesWithBaselines();
            assertEquals(6, cases.size());

            for (String eligible : LlmEvaluationSubset.eligibleCaseIds()) {
                assertTrue(cases.containsKey(eligible),
                        "Falta caso elegible: " + eligible);
                assertTrue(cases.get(eligible).getBaseline().isEligible(),
                        eligible + " debe ser elegible");
            }
            for (String ineligible : LlmEvaluationSubset.ineligibleCaseIds()) {
                assertTrue(cases.containsKey(ineligible),
                        "Falta caso inelegible: " + ineligible);
                assertFalse(cases.get(ineligible).getBaseline().isEligible(),
                        ineligible + " debe ser inelegible");
            }
        }
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private static List<LlmInvocationRecord> filterRecords(String model,
                                                            String caseId) {
        return campaignResults.stream()
                .filter(r -> r.getModel().equals(model))
                .filter(r -> r.getCaseId().equals(caseId))
                .toList();
    }

    private static void assertVerdictConsistent(String model, String caseId,
                                                LlmVerdict expectedVerdict) {
        List<LlmInvocationRecord> records = filterRecords(model, caseId);
        assertEquals(3, records.size(),
                "Debe haber 3 intentos para " + model + "/" + caseId);
        for (LlmInvocationRecord r : records) {
            assertEquals(expectedVerdict, r.getVerdict(),
                    String.format("%s/%s #%d: esperado %s, obtenido %s — %s",
                            model, caseId, r.getAttemptNumber(),
                            expectedVerdict, r.getVerdict(),
                            r.getEvaluationResult().getVerdictReason()));
        }
    }
}
