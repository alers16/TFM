package es.tfm.refactoring.llm;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de la campaña sobre casos trampa.
 * <p>
 * Usa un proveedor de respuestas controlado que siempre devuelve
 * NO_REFACTORING_APPLICABLE (respuesta correcta para todos los casos trampa).
 * Verifica que el LlmCampaignRunner maneja correctamente el overload con
 * casos externos, y que el oráculo clasifica como CORRECT las respuestas
 * NO_REFACTORING_APPLICABLE sobre casos inelegibles.
 */
@DisplayName("RQ3 Trap Campaign — Casos trampa no elegibles")
class LlmTrapCampaignTest {

    /** Proveedor que devuelve NO_REFACTORING_APPLICABLE para todos los casos. */
    private static final LlmResponseProvider CORRECT_TRAP_PROVIDER =
            (model, caseId, attempt, prompt) -> "NO_REFACTORING_APPLICABLE";

    private static LlmExperimentProtocol protocol;
    private static List<LlmInvocationRecord> campaignResults;

    @BeforeAll
    static void executeCampaign() throws IOException {
        protocol = LlmExperimentProtocol.defaultProtocol();
        LlmCampaignRunner runner = new LlmCampaignRunner();

        Map<String, String> modelVersions = Map.of(
                "gpt-4o", "gpt-4o-test",
                "gpt-4.1", "gpt-4.1-test"
        );

        Map<String, LlmCampaignRunner.CaseWithBaseline> trapCases =
                Rq3TrapCampaignExecutor.loadTrapCasesWithBaselines();

        campaignResults = runner.executeCampaign(
                protocol, CORRECT_TRAP_PROVIDER, modelVersions, trapCases);
    }

    // =========================================================================
    // Ejecución del protocolo
    // =========================================================================

    @Nested
    @DisplayName("Ejecución del protocolo sobre casos trampa")
    class ExecutionTests {

        @Test
        @DisplayName("Se ejecutan 18 invocaciones (3 casos × 2 modelos × 3 intentos)")
        void totalInvocations() {
            assertEquals(18, campaignResults.size());
        }

        @Test
        @DisplayName("Todos los modelos del protocolo fueron evaluados")
        void allModelsEvaluated() {
            long gpt4oCount = campaignResults.stream()
                    .filter(r -> r.getModel().equals("gpt-4o"))
                    .count();
            long gpt41Count = campaignResults.stream()
                    .filter(r -> r.getModel().equals("gpt-4.1"))
                    .count();
            assertEquals(9, gpt4oCount);
            assertEquals(9, gpt41Count);
        }

        @Test
        @DisplayName("Los 3 casos trampa fueron evaluados")
        void allTrapCasesEvaluated() {
            for (String id : LlmTrapSubset.caseIds()) {
                long count = campaignResults.stream()
                        .filter(r -> r.getCaseId().equals(id))
                        .count();
                assertTrue(count > 0, "Falta caso trampa: " + id);
            }
        }

        @Test
        @DisplayName("No hay errores técnicos con el proveedor controlado")
        void noTechnicalErrors() {
            long errors = campaignResults.stream()
                    .filter(r -> r.getVerdict() == LlmVerdict.ERROR)
                    .count();
            assertEquals(0, errors, "No debe haber errores con el proveedor controlado");
        }
    }

    // =========================================================================
    // Corrección del oráculo
    // =========================================================================

    @Nested
    @DisplayName("Validación del oráculo para casos trampa")
    class OracleValidation {

        @Test
        @DisplayName("NO_REFACTORING_APPLICABLE es SUCCESS para todos los casos trampa")
        void correctAnswerIsSuccess() {
            long successCount = campaignResults.stream()
                    .filter(r -> r.getVerdict() == LlmVerdict.SUCCESS)
                    .count();
            assertEquals(18, successCount,
                    "Todos los casos trampa respondidos con NO_REFACTORING_APPLICABLE "
                    + "deben ser SUCCESS (rechazo correcto de caso inelegible)");
        }

        @Test
        @DisplayName("Tasa de verdaderos negativos = 100% con proveedor correcto")
        void trueNegativeRate() {
            long success = campaignResults.stream()
                    .filter(r -> r.getVerdict() == LlmVerdict.SUCCESS)
                    .count();
            double rate = (double) success / campaignResults.size();
            assertEquals(1.0, rate, 1e-9,
                    "Con respuestas correctas, la tasa de verdaderos negativos debe ser 1.0");
        }
    }
}
