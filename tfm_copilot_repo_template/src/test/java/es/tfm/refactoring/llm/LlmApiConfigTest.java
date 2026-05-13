package es.tfm.refactoring.llm;

import es.tfm.refactoring.experiment.ExperimentCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para la configuración de credenciales y el provider real de RQ3.
 */
@DisplayName("LLM API Config & Live Provider")
class LlmApiConfigTest {

    // =========================================================================
    // LlmApiConfig — Constructor de test
    // =========================================================================

    @Nested
    @DisplayName("LlmApiConfig — Validación de credenciales")
    class ConfigTests {

        @Test
        @DisplayName("Configuración válida con todos los valores")
        void validConfig() {
            LlmApiConfig config = new LlmApiConfig(
                    "sk-test-key-12345678",
                    "gpt-4o-2024-05-13",
                    "gpt-4.1-2025-04-14");

            assertEquals("sk-test-key-12345678", config.getOpenaiApiKey());
            assertEquals("gpt-4o-2024-05-13", config.getOpenaiModel());
            assertEquals("gpt-4.1-2025-04-14", config.getOpenaiSecondaryModel());
            assertTrue(config.isValid());
            assertFalse(config.isOpenAiOnly());
        }

        @Test
        @DisplayName("Modelos por defecto si no se especifican")
        void defaultModels() {
            LlmApiConfig config = new LlmApiConfig(
                    "sk-test-key-12345678", null, null);

            assertEquals(LlmApiConfig.DEFAULT_OPENAI_MODEL, config.getOpenaiModel());
            assertEquals(LlmApiConfig.DEFAULT_OPENAI_SECONDARY_MODEL,
                    config.getOpenaiSecondaryModel());
        }

        @Test
        @DisplayName("Clave OpenAI vacía lanza excepción")
        void emptyOpenaiKeyThrows() {
            assertThrows(IllegalStateException.class, () ->
                    new LlmApiConfig("", null, null));
        }

        @Test
        @DisplayName("Clave null lanza excepción")
        void nullKeyThrows() {
            assertThrows(IllegalStateException.class, () ->
                    new LlmApiConfig(null, null, null));
        }

        @Test
        @DisplayName("toSafeString no expone la clave completa")
        void safeStringMasksKeys() {
            LlmApiConfig config = new LlmApiConfig(
                    "sk-test-key-12345678", null, null);

            String safe = config.toSafeString();
            assertFalse(safe.contains("sk-test-key-12345678"),
                    "No debe exponer clave OpenAI completa");
            assertTrue(safe.contains("sk-t"), "Debe mostrar prefijo");
            assertTrue(safe.contains("..."), "Debe enmascarar");
            assertTrue(safe.contains("dual-openai"),
                    "Debe identificar el modo dual-openai");
        }

        @Test
        @DisplayName("toString usa formato seguro")
        void toStringIsSafe() {
            LlmApiConfig config = new LlmApiConfig(
                    "sk-test-key-12345678", null, null);

            assertEquals(config.toSafeString(), config.toString());
        }

        @Test
        @DisplayName("getApiKeyForModel resuelve la misma clave para ambos OpenAI")
        void apiKeyForModel() {
            LlmApiConfig config = new LlmApiConfig(
                    "openai-key", null, null);

            assertEquals("openai-key", config.getApiKeyForModel("gpt-4o"));
            assertEquals("openai-key", config.getApiKeyForModel("gpt-4.1"));
        }

        @Test
        @DisplayName("getApiKeyForModel rechaza modelos no OpenAI (claude)")
        void rejectsClaudeInDualOpenAi() {
            LlmApiConfig config = new LlmApiConfig(
                    "openai-key", null, null);

            assertThrows(IllegalArgumentException.class,
                    () -> config.getApiKeyForModel("claude-3.5-sonnet"));
        }

        @Test
        @DisplayName("getApiKeyForModel lanza excepción para modelo desconocido")
        void unknownModelThrows() {
            LlmApiConfig config = new LlmApiConfig(
                    "key1", null, null);

            assertThrows(IllegalArgumentException.class,
                    () -> config.getApiKeyForModel("llama-3"));
        }

        @Test
        @DisplayName("toModelVersions contiene los dos modelos OpenAI")
        void modelVersionsMap() {
            LlmApiConfig config = new LlmApiConfig(
                    "key1", "gpt-4o-2024-05-13", "gpt-4.1-2025-04-14");

            var versions = config.toModelVersions();
            assertEquals(2, versions.size());
            assertEquals("gpt-4o-2024-05-13", versions.get("gpt-4o"));
            assertEquals("gpt-4.1-2025-04-14", versions.get("gpt-4.1"));
            assertFalse(versions.containsKey("claude-3.5-sonnet"));
        }

        @Test
        @DisplayName("getOpenAiApiModelId mapea protocolo → modelo real")
        void apiModelIdMapping() {
            LlmApiConfig config = new LlmApiConfig(
                    "key1", "gpt-4o-2024-05-13", "gpt-4.1-2025-04-14");

            assertEquals("gpt-4o-2024-05-13",
                    config.getOpenAiApiModelId("gpt-4o"));
            assertEquals("gpt-4.1-2025-04-14",
                    config.getOpenAiApiModelId("gpt-4.1"));
            assertThrows(IllegalArgumentException.class,
                    () -> config.getOpenAiApiModelId("claude-3.5-sonnet"));
        }

        @Test
        @DisplayName("Constructor desde entorno solo requiere OPENAI_API_KEY")
        void envConstructorOnlyRequiresOpenAi() {
            // Solo ejecutar si OPENAI no está configurada → debe fallar
            // explícitamente sin pedir ANTHROPIC.
            if (System.getenv("OPENAI_API_KEY") == null) {
                IllegalStateException ex = assertThrows(
                        IllegalStateException.class, LlmApiConfig::new);
                assertTrue(ex.getMessage().contains("OPENAI_API_KEY"));
                assertFalse(ex.getMessage().contains("ANTHROPIC_API_KEY"),
                        "La campaña formal ya no exige ANTHROPIC_API_KEY");
            }
        }
    }

    // =========================================================================
    // LiveLlmResponseProvider — splitPrompt
    // =========================================================================

    @Nested
    @DisplayName("LiveLlmResponseProvider — Separación de prompt")
    class SplitPromptTests {

        @Test
        @DisplayName("Separa correctamente system y user con separador ---")
        void splitWithSeparator() {
            String full = "System instructions here\n\n---\n\nUser code here";
            String[] parts = LiveLlmResponseProvider.splitPrompt(full);

            assertEquals("System instructions here", parts[0]);
            assertEquals("User code here", parts[1]);
        }

        @Test
        @DisplayName("Sin separador, todo es user prompt")
        void noSeparator() {
            String full = "Just a plain prompt without separator";
            String[] parts = LiveLlmResponseProvider.splitPrompt(full);

            assertEquals("", parts[0]);
            assertEquals(full, parts[1]);
        }

        @Test
        @DisplayName("Compatible con LlmPromptBuilder.buildFullPrompt")
        void compatibleWithPromptBuilder() {
            LlmPromptBuilder builder = new LlmPromptBuilder();
            ExperimentCase ec = new ExperimentCase(
                    "TEST", "test", "desc",
                    "class Foo { void bar() {} }");

            String full = builder.buildFullPrompt(ec);
            String[] parts = LiveLlmResponseProvider.splitPrompt(full);

            assertEquals(builder.buildSystemPrompt(), parts[0]);
            assertEquals(builder.buildUserPrompt(ec), parts[1]);
        }
    }

    // =========================================================================
    // LlmApiConfig — Modo exploratorio OpenAI-only (Phase 9.5)
    // =========================================================================

    @Nested
    @DisplayName("LlmApiConfig — Modo OpenAI-only (exploratorio)")
    class OpenAiOnlyTests {

        @Test
        @DisplayName("Constructor single-model crea config válida sin secundario")
        void singleModelConstructor() {
            LlmApiConfig config = new LlmApiConfig(
                    "sk-test-key-12345678", "gpt-4o-2024-05-13");

            assertTrue(config.isOpenAiOnly());
            assertTrue(config.isValid());
            assertEquals("sk-test-key-12345678", config.getOpenaiApiKey());
            assertEquals("gpt-4o-2024-05-13", config.getOpenaiModel());
            assertNull(config.getOpenaiSecondaryModel());
        }

        @Test
        @DisplayName("toModelVersions solo expone gpt-4o")
        void modelVersionsOnlyOpenAi() {
            LlmApiConfig config = new LlmApiConfig(
                    "sk-test-key-12345678", "gpt-4o-2024-05-13");

            java.util.Map<String, String> versions = config.toModelVersions();
            assertEquals(1, versions.size());
            assertTrue(versions.containsKey("gpt-4o"));
            assertFalse(versions.containsKey("gpt-4.1"));
            assertFalse(versions.containsKey("claude-3.5-sonnet"));
        }

        @Test
        @DisplayName("getApiKeyForModel rechaza claude en modo OpenAI-only")
        void rejectsClaudeKey() {
            LlmApiConfig config = new LlmApiConfig(
                    "sk-test-key-12345678", "gpt-4o-2024-05-13");

            assertThrows(IllegalArgumentException.class,
                    () -> config.getApiKeyForModel("claude-3.5-sonnet"));
            assertEquals("sk-test-key-12345678",
                    config.getApiKeyForModel("gpt-4o"));
        }

        @Test
        @DisplayName("getOpenAiApiModelId rechaza el secundario en openai-only")
        void rejectsSecondaryInOpenAiOnly() {
            LlmApiConfig config = new LlmApiConfig(
                    "sk-test-key-12345678", "gpt-4o-2024-05-13");

            assertEquals("gpt-4o-2024-05-13",
                    config.getOpenAiApiModelId("gpt-4o"));
            assertThrows(IllegalArgumentException.class,
                    () -> config.getOpenAiApiModelId("gpt-4.1"));
        }

        @Test
        @DisplayName("toSafeString indica mode=openai-only")
        void safeStringIncludesMode() {
            LlmApiConfig config = new LlmApiConfig(
                    "sk-test-key-12345678", "gpt-4o-2024-05-13");

            String safe = config.toSafeString();
            assertTrue(safe.contains("mode=openai-only"), safe);
            assertFalse(safe.contains("anthropic="), safe);
        }

        @Test
        @DisplayName("Clave OpenAI vacía sigue lanzando IllegalStateException")
        void rejectsEmptyKey() {
            assertThrows(IllegalStateException.class,
                    () -> new LlmApiConfig("", "gpt-4o-2024-05-13"));
        }

        @Test
        @DisplayName("Modelo por defecto si se pasa null")
        void defaultModelOnNull() {
            LlmApiConfig config = new LlmApiConfig(
                    "sk-test-key-12345678", null);

            assertEquals(LlmApiConfig.DEFAULT_OPENAI_MODEL, config.getOpenaiModel());
        }
    }
}
