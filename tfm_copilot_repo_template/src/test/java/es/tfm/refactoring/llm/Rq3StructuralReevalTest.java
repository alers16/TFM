package es.tfm.refactoring.llm;

import es.tfm.refactoring.analysis.CognitiveComplexityCalculator;
import es.tfm.refactoring.detection.DetectionMode;
import es.tfm.refactoring.detection.NestedIfDetector;
import es.tfm.refactoring.experiment.BatchRunner;
import es.tfm.refactoring.transformation.NestedIfTransformer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validación de la re-evaluación RQ3 con baseline {@link DetectionMode#STRUCTURAL}
 * reutilizando respuestas grabadas (sin llamar a la API).
 * <p>
 * Cubre tres invariantes:
 * <ol>
 *   <li>{@link RecordedEvidenceResponseProvider} sirve la respuesta cruda grabada
 *       por {@code model::caseId::attempt} y devuelve {@code null} para claves
 *       ausentes (no inventa respuestas).</li>
 *   <li>El constructor {@link LlmCampaignRunner#LlmCampaignRunner(BatchRunner)}
 *       enruta el baseline al modo inyectado: en STRUCTURAL, el caso de
 *       method-call interno {@code REAL_COMMONS_COLLECTIONS_GET} pasa a ELEGIBLE.</li>
 *   <li>Alineación oráculo↔prompt: una respuesta que combina ese caso, juzgada
 *       INCORRECT con baseline STRICT, pasa a verdicto positivo (SUCCESS/PARTIAL)
 *       con baseline STRUCTURAL, sin tocar el oráculo.</li>
 * </ol>
 */
@DisplayName("RQ3 — Re-evaluación STRUCTURAL reutilizando respuestas grabadas")
class Rq3StructuralReevalTest {

    private static BatchRunner structuralRunner() {
        return new BatchRunner(
                new NestedIfDetector(DetectionMode.STRUCTURAL),
                new NestedIfTransformer(),
                new CognitiveComplexityCalculator());
    }

    @Nested
    @DisplayName("RecordedEvidenceResponseProvider — solo lectura")
    class ProviderTests {

        @Test
        @DisplayName("Sirve la respuesta cruda grabada por model::caseId::attempt")
        void servesRecordedRaw(@org.junit.jupiter.api.io.TempDir Path tmp)
                throws IOException {
            Path evidence = tmp.resolve("evidence.json");
            Files.writeString(evidence,
                    "[{\"model\":\"gpt-4o\",\"caseId\":\"C1\",\"attemptNumber\":1,"
                            + "\"rawLlmOutput\":\"RAW-A\"},"
                            + "{\"model\":\"gpt-4o\",\"caseId\":\"C1\",\"attemptNumber\":2,"
                            + "\"rawLlmOutput\":\"RAW-B\"}]",
                    StandardCharsets.UTF_8);

            RecordedEvidenceResponseProvider provider =
                    new RecordedEvidenceResponseProvider(evidence);

            assertEquals("RAW-A", provider.getResponse("gpt-4o", "C1", 1, "p"));
            assertEquals("RAW-B", provider.getResponse("gpt-4o", "C1", 2, "p"));
            assertEquals(2, provider.getRecordCount());
        }

        @Test
        @DisplayName("Devuelve null para claves no grabadas (no inventa respuestas)")
        void nullForMissing(@org.junit.jupiter.api.io.TempDir Path tmp)
                throws IOException {
            Path evidence = tmp.resolve("evidence.json");
            Files.writeString(evidence,
                    "[{\"model\":\"gpt-4o\",\"caseId\":\"C1\",\"attemptNumber\":1,"
                            + "\"rawLlmOutput\":\"RAW-A\"}]",
                    StandardCharsets.UTF_8);

            RecordedEvidenceResponseProvider provider =
                    new RecordedEvidenceResponseProvider(evidence);

            // Mismo caso, intento no grabado: salvaguarda con intento 1.
            assertEquals("RAW-A", provider.getResponse("gpt-4o", "C1", 3, "p"));
            // Modelo/caso inexistente: null.
            assertNull(provider.getResponse("gpt-4.1", "C1", 1, "p"));
            assertNull(provider.getResponse("gpt-4o", "C2", 1, "p"));
        }

        @Test
        @DisplayName("Conserva rawLlmOutput null (error técnico grabado)")
        void preservesNullRaw(@org.junit.jupiter.api.io.TempDir Path tmp)
                throws IOException {
            Path evidence = tmp.resolve("evidence.json");
            Files.writeString(evidence,
                    "[{\"model\":\"gpt-4o\",\"caseId\":\"C1\",\"attemptNumber\":1,"
                            + "\"rawLlmOutput\":null}]",
                    StandardCharsets.UTF_8);

            RecordedEvidenceResponseProvider provider =
                    new RecordedEvidenceResponseProvider(evidence);
            assertNull(provider.getResponse("gpt-4o", "C1", 1, "p"));
        }

        @Test
        @DisplayName("Fichero inexistente lanza IOException")
        void missingFileThrows(@org.junit.jupiter.api.io.TempDir Path tmp) {
            Path missing = tmp.resolve("nope.json");
            assertThrows(IOException.class,
                    () -> new RecordedEvidenceResponseProvider(missing));
        }
    }

    @Nested
    @DisplayName("Baseline STRUCTURAL vs STRICT en el runner")
    class BaselineModeTests {

        @Test
        @DisplayName("STRUCTURAL hace ELEGIBLE el caso de method-call interno (COLLECTIONS_GET)")
        void structuralEligibility() throws IOException {
            LlmCampaignRunner strictRunner = new LlmCampaignRunner();
            LlmCampaignRunner structRunner =
                    new LlmCampaignRunner(structuralRunner());

            Map<String, LlmCampaignRunner.CaseWithBaseline> strict =
                    strictRunner.loadCasesWithBaselines();
            Map<String, LlmCampaignRunner.CaseWithBaseline> structural =
                    structRunner.loadCasesWithBaselines();

            // STRICT: inelegible por P5 (containsKey method call).
            assertFalse(strict.get("REAL_COMMONS_COLLECTIONS_GET")
                    .getBaseline().isEligible());
            // STRUCTURAL: elegible (P5 omitida) y con reducción de CC.
            assertTrue(structural.get("REAL_COMMONS_COLLECTIONS_GET")
                    .getBaseline().isEligible());
            assertTrue(structural.get("REAL_COMMONS_COLLECTIONS_GET")
                    .getBaseline().getDelta() < 0);
        }
    }

    @Nested
    @DisplayName("Alineación oráculo↔prompt al re-evaluar")
    class OracleAlignmentTests {

        @Test
        @DisplayName("GPT-4o/COLLECTIONS_GET: INCORRECT con STRICT → positivo con STRUCTURAL")
        void verdictFlipsWithStructuralBaseline() throws IOException {
            Map<String, String> versions = Map.of(
                    "gpt-4o", "gpt-4o", "gpt-4.1", "gpt-4.1");
            LlmExperimentProtocol protocol =
                    LlmExperimentProtocol.defaultProtocol();
            PrerecordedResponseProvider responses =
                    new PrerecordedResponseProvider();

            // STRICT (flujo por defecto): el caso es inelegible y GPT-4o lo
            // transforma → INCORRECT.
            List<LlmInvocationRecord> strict = new LlmCampaignRunner()
                    .executeCampaign(protocol, responses, versions);
            assertTrue(verdictsFor(strict, "gpt-4o", "REAL_COMMONS_COLLECTIONS_GET")
                            .stream().allMatch(v -> v == LlmVerdict.INCORRECT),
                    "Con STRICT, la transformación del caso inelegible debe ser INCORRECT");

            // STRUCTURAL: mismo oráculo, mismas respuestas, baseline P1-P4.
            // El caso es elegible y la transformación reduce CC → verdicto positivo.
            List<LlmInvocationRecord> structural =
                    new LlmCampaignRunner(structuralRunner())
                            .executeCampaign(protocol, responses, versions);
            List<LlmVerdict> structVerdicts = verdictsFor(structural,
                    "gpt-4o", "REAL_COMMONS_COLLECTIONS_GET");
            assertTrue(structVerdicts.stream().allMatch(
                            v -> v == LlmVerdict.SUCCESS || v == LlmVerdict.PARTIAL),
                    "Con STRUCTURAL, la misma respuesta debe puntuar como SUCCESS/PARTIAL: "
                            + structVerdicts);
        }
    }

    private static List<LlmVerdict> verdictsFor(List<LlmInvocationRecord> records,
                                                String model, String caseId) {
        return records.stream()
                .filter(r -> r.getModel().equals(model))
                .filter(r -> r.getCaseId().equals(caseId))
                .map(LlmInvocationRecord::getVerdict)
                .toList();
    }
}
