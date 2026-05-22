package es.tfm.refactoring.llm;

import es.tfm.refactoring.experiment.ExperimentCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del prompt v2.0 (few-shot) de LlmPromptBuilder.
 * <p>
 * Verifica que el sistema de prompt v2.0 contiene los ejemplos few-shot
 * y que el selector de versión en LlmCampaignRunner funciona correctamente.
 */
@DisplayName("LlmPromptBuilder — Prompt v2.0 few-shot")
class LlmPromptBuilderV2Test {

    private LlmPromptBuilder builder;
    private ExperimentCase sampleCase;

    @BeforeEach
    void setUp() {
        builder = new LlmPromptBuilder();
        sampleCase = new ExperimentCase(
                "SAMPLE_01", "test", "Sample case for prompt tests",
                "class Sample { void foo() { if (a) { if (b) { doIt(); } } } }");
    }

    // =========================================================================
    // Constantes de versión
    // =========================================================================

    @Nested
    @DisplayName("Constantes de versión")
    class VersionConstants {

        @Test
        @DisplayName("PROMPT_VERSION es 'v1.0'")
        void v1VersionConstant() {
            assertEquals("v1.0", LlmPromptBuilder.PROMPT_VERSION);
        }

        @Test
        @DisplayName("PROMPT_VERSION_V2 es 'v2.0'")
        void v2VersionConstant() {
            assertEquals("v2.0", LlmPromptBuilder.PROMPT_VERSION_V2);
        }

        @Test
        @DisplayName("v1.0 y v2.0 son distintos")
        void versionsAreDifferent() {
            assertNotEquals(LlmPromptBuilder.PROMPT_VERSION,
                    LlmPromptBuilder.PROMPT_VERSION_V2);
        }
    }

    // =========================================================================
    // Contenido del system prompt v2.0
    // =========================================================================

    @Nested
    @DisplayName("Contenido del system prompt v2.0")
    class SystemPromptV2Content {

        @Test
        @DisplayName("El system prompt v2.0 contiene marcador 'EXAMPLE'")
        void containsExampleMarker() {
            String prompt = builder.buildSystemPromptV2();
            assertTrue(prompt.contains("EXAMPLE"),
                    "El system prompt v2.0 debe contener ejemplos marcados con 'EXAMPLE'");
        }

        @Test
        @DisplayName("El system prompt v2.0 contiene ejemplo APPLICABLE")
        void containsApplicableExample() {
            String prompt = builder.buildSystemPromptV2();
            assertTrue(prompt.contains("APPLICABLE"),
                    "El system prompt v2.0 debe incluir el término APPLICABLE");
        }

        @Test
        @DisplayName("El system prompt v2.0 contiene ejemplo NOT APPLICABLE")
        void containsNotApplicableExample() {
            String prompt = builder.buildSystemPromptV2();
            assertTrue(prompt.contains("NOT APPLICABLE"),
                    "El system prompt v2.0 debe incluir ejemplo NOT APPLICABLE");
        }

        @Test
        @DisplayName("El system prompt v2.0 es más largo que el v1.0 (por los ejemplos)")
        void v2LongerThanV1() {
            int v1Length = builder.buildSystemPrompt().length();
            int v2Length = builder.buildSystemPromptV2().length();
            assertTrue(v2Length > v1Length,
                    "El prompt v2.0 debe ser más largo que v1.0 por incluir ejemplos");
        }

        @Test
        @DisplayName("El system prompt v2.0 contiene las reglas de refactorización")
        void containsRefactoringRules() {
            String prompt = builder.buildSystemPromptV2();
            assertTrue(prompt.contains("&&"),
                    "El system prompt v2.0 debe incluir la regla de combinación con &&");
            assertTrue(prompt.contains("NO_REFACTORING_APPLICABLE"),
                    "El system prompt v2.0 debe incluir la instrucción de rechazo");
        }
    }

    // =========================================================================
    // Prompt completo v2.0
    // =========================================================================

    @Nested
    @DisplayName("Prompt completo v2.0 (sistema + usuario)")
    class FullPromptV2 {

        @Test
        @DisplayName("buildFullPromptV2 incluye el código fuente del caso")
        void includesSourceCode() {
            String prompt = builder.buildFullPromptV2(sampleCase);
            assertTrue(prompt.contains("Sample"),
                    "El prompt completo v2.0 debe contener el código fuente del caso");
        }

        @Test
        @DisplayName("buildFullPromptV2 incluye los ejemplos few-shot")
        void includesFewShotExamples() {
            String prompt = builder.buildFullPromptV2(sampleCase);
            assertTrue(prompt.contains("EXAMPLE"),
                    "El prompt completo v2.0 debe incluir los ejemplos few-shot");
        }

        @Test
        @DisplayName("buildFullPromptV2 produce texto diferente a buildFullPrompt")
        void v2DiffersFromV1() {
            String v1 = builder.buildFullPrompt(sampleCase);
            String v2 = builder.buildFullPromptV2(sampleCase);
            assertNotEquals(v1, v2,
                    "buildFullPromptV2 debe diferir de buildFullPrompt (v1.0)");
        }
    }

    // =========================================================================
    // Integración con LlmExperimentProtocol
    // =========================================================================

    @Nested
    @DisplayName("Integración: protocol selecciona versión correcta")
    class ProtocolIntegration {

        @Test
        @DisplayName("defaultProtocol() usa prompt v1.0")
        void defaultProtocolUsesV1() {
            assertEquals(LlmPromptBuilder.PROMPT_VERSION,
                    LlmExperimentProtocol.defaultProtocol().getPromptVersion());
        }

        @Test
        @DisplayName("promptV2Protocol() usa prompt v2.0")
        void promptV2ProtocolUsesV2() {
            assertEquals(LlmPromptBuilder.PROMPT_VERSION_V2,
                    LlmExperimentProtocol.promptV2Protocol().getPromptVersion());
        }

        @Test
        @DisplayName("promptV2Protocol() conserva los mismos modelos que defaultProtocol()")
        void promptV2SameModels() {
            assertEquals(
                    LlmExperimentProtocol.defaultProtocol().getModels(),
                    LlmExperimentProtocol.promptV2Protocol().getModels());
        }

        @Test
        @DisplayName("promptV2Protocol() conserva temperatura y número de intentos")
        void promptV2SameTemperatureAndAttempts() {
            LlmExperimentProtocol v1 = LlmExperimentProtocol.defaultProtocol();
            LlmExperimentProtocol v2 = LlmExperimentProtocol.promptV2Protocol();
            assertEquals(v1.getTemperature(), v2.getTemperature(), 1e-9);
            assertEquals(v1.getAttemptsPerCase(), v2.getAttemptsPerCase());
        }
    }
}
