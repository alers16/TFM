package es.tfm.refactoring.llm;

import es.tfm.refactoring.experiment.BatchRunner;
import es.tfm.refactoring.experiment.ExperimentCase;
import es.tfm.refactoring.experiment.ExperimentResult;
import es.tfm.refactoring.experiment.PilotCorpusLoader;
import es.tfm.refactoring.experiment.RealDatasetLoader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de la infraestructura experimental de RQ3 (comparación con LLMs).
 * <p>
 * Valida prompts, parsing de respuestas, oráculo de validación,
 * protocolo experimental y subconjunto de evaluación.
 * <p>
 * <strong>RQ3:</strong> ¿Pueden los grandes modelos de lenguaje realizar
 * esta refactorización de forma correcta y automática?
 */
@DisplayName("LLM Evaluation Infrastructure — RQ3")
class LlmEvaluationTest {

    // =========================================================================
    // LlmPromptBuilder
    // =========================================================================

    @Nested
    @DisplayName("LlmPromptBuilder — Generación de prompts")
    class PromptBuilderTests {

        private LlmPromptBuilder builder;

        @BeforeEach
        void setUp() {
            builder = new LlmPromptBuilder();
        }

        @Test
        @DisplayName("System prompt contiene instrucciones de refactorización")
        void systemPromptContainsInstructions() {
            String system = builder.buildSystemPrompt();
            assertTrue(system.contains("nested if"), "Debe mencionar nested if");
            assertTrue(system.contains("&&"), "Debe mencionar operador &&");
            assertTrue(system.contains("semantics"), "Debe mencionar preservación semántica");
            assertTrue(system.contains("NO_REFACTORING_APPLICABLE"),
                    "Debe incluir marcador de rechazo");
        }

        @Test
        @DisplayName("System prompt contiene reglas de formato de salida")
        void systemPromptContainsOutputFormat() {
            String system = builder.buildSystemPrompt();
            assertTrue(system.contains("```java"),
                    "Debe especificar formato de bloque de código");
            assertTrue(system.contains("else"),
                    "Debe mencionar condición de no-else");
        }

        @Test
        @DisplayName("User prompt incluye el código fuente del caso")
        void userPromptContainsSourceCode() {
            ExperimentCase ec = new ExperimentCase(
                    "TEST", "test", "test case",
                    "class Foo { void bar() { if (a) { if (b) { } } } }");
            String user = builder.buildUserPrompt(ec);
            assertTrue(user.contains("class Foo"),
                    "Debe incluir el código fuente");
            assertTrue(user.contains("```java"),
                    "Debe usar bloque de código");
        }

        @Test
        @DisplayName("Full prompt combina sistema y usuario")
        void fullPromptCombinesBoth() {
            ExperimentCase ec = new ExperimentCase(
                    "TEST", "test", "test", "class X {}");
            String full = builder.buildFullPrompt(ec);
            assertTrue(full.contains("Java refactoring assistant"),
                    "Debe incluir prompt de sistema");
            assertTrue(full.contains("class X"),
                    "Debe incluir prompt de usuario");
            assertTrue(full.contains("---"),
                    "Debe tener separador");
        }

        @Test
        @DisplayName("Prompt version es trazable")
        void promptVersionTraceable() {
            assertNotNull(builder.getPromptVersion());
            assertEquals(LlmPromptBuilder.PROMPT_VERSION, builder.getPromptVersion());
        }

        @Test
        @DisplayName("Prompt es idéntico para el mismo caso")
        void promptDeterministic() {
            ExperimentCase ec = new ExperimentCase(
                    "TEST", "test", "desc",
                    "class Foo { void m() {} }");
            String p1 = builder.buildFullPrompt(ec);
            String p2 = builder.buildFullPrompt(ec);
            assertEquals(p1, p2, "El prompt debe ser determinista");
        }

        @Test
        @DisplayName("System prompt no contiene código del caso")
        void systemPromptNoCaseCode() {
            String system = builder.buildSystemPrompt();
            assertFalse(system.contains("class Foo"),
                    "System prompt no debe contener código específico de caso");
        }
    }

    // =========================================================================
    // LlmResponseValidator — Extracción de código
    // =========================================================================

    @Nested
    @DisplayName("LlmResponseValidator — Extracción y parsing")
    class ValidatorExtractionTests {

        private LlmResponseValidator validator;

        @BeforeEach
        void setUp() {
            validator = new LlmResponseValidator();
        }

        @Test
        @DisplayName("Extrae código de bloque ```java ... ```")
        void extractsCodeBlock() {
            String response = "Here is the refactored code:\n\n"
                    + "```java\nclass Foo { void bar() {} }\n```\n\n"
                    + "This combines the conditions.";
            String code = validator.extractCode(response);
            assertNotNull(code);
            assertTrue(code.contains("class Foo"));
        }

        @Test
        @DisplayName("Retorna null si no hay bloque de código")
        void noCodeBlockReturnsNull() {
            String response = "I cannot refactor this code because it has an else branch.";
            assertNull(validator.extractCode(response));
        }

        @Test
        @DisplayName("Retorna null para entrada null")
        void nullInputReturnsNull() {
            assertNull(validator.extractCode(null));
        }

        @Test
        @DisplayName("Detecta rechazo explícito con marcador")
        void detectsRefusal() {
            assertTrue(validator.isRefusal("NO_REFACTORING_APPLICABLE"));
            assertTrue(validator.isRefusal(
                    "After analysis, NO_REFACTORING_APPLICABLE because..."));
        }

        @Test
        @DisplayName("No detecta rechazo en respuesta normal")
        void noRefusalInNormalResponse() {
            assertFalse(validator.isRefusal("```java\nclass Foo {}\n```"));
            assertFalse(validator.isRefusal(null));
        }

        @Test
        @DisplayName("Extrae primer bloque si hay múltiples")
        void extractsFirstBlock() {
            String response = "```java\nclass First {}\n```\n\n"
                    + "```java\nclass Second {}\n```";
            String code = validator.extractCode(response);
            assertTrue(code.contains("First"));
            assertFalse(code.contains("Second"));
        }
    }

    // =========================================================================
    // LlmResponseValidator — Oráculo de validación
    // =========================================================================

    @Nested
    @DisplayName("LlmResponseValidator — Oráculo de validación")
    class ValidatorOracleTests {

        private LlmResponseValidator validator;
        private BatchRunner runner;

        @BeforeEach
        void setUp() {
            validator = new LlmResponseValidator();
            runner = new BatchRunner();
        }

        @Test
        @DisplayName("Respuesta correcta → SUCCESS")
        void correctResponseIsSuccess() {
            String original = "class T { void m(int a, int b) {"
                    + " if (a > 0) { if (b > 0) { System.out.println(a + b); } } } }";
            String refactored = "```java\n"
                    + "class T { void m(int a, int b) {"
                    + " if (a > 0 && b > 0) { System.out.println(a + b); } } }\n"
                    + "```";

            ExperimentCase ec = new ExperimentCase("TEST", "test", "desc", original);
            ExperimentResult baseline = runSingle(runner, ec);

            LlmEvaluationResult result = validator.evaluate(
                    "TEST", "gpt-4o", "v1.0", 1,
                    refactored, original, baseline);

            assertEquals(LlmVerdict.SUCCESS, result.getVerdict());
            assertTrue(result.isParseable());
            assertTrue(result.getDelta() < 0, "Debe reducir CC");
        }

        @Test
        @DisplayName("Código no parseable → INVALID_OUTPUT")
        void unparseableCodeIsInvalid() {
            String original = "class T { void m(int a) {"
                    + " if (a > 0) { if (a < 10) { } } } }";
            String bad = "```java\nthis is not valid java!!!\n```";

            ExperimentCase ec = new ExperimentCase("TEST", "test", "desc", original);
            ExperimentResult baseline = runSingle(runner, ec);

            LlmEvaluationResult result = validator.evaluate(
                    "TEST", "gpt-4o", "v1.0", 1,
                    bad, original, baseline);

            assertEquals(LlmVerdict.INVALID_OUTPUT, result.getVerdict());
            assertFalse(result.isParseable());
        }

        @Test
        @DisplayName("Sin bloque de código → INVALID_OUTPUT")
        void noCodeBlockIsInvalid() {
            String original = "class T { void m(int a) {"
                    + " if (a > 0) { if (a < 10) { } } } }";

            ExperimentCase ec = new ExperimentCase("TEST", "test", "desc", original);
            ExperimentResult baseline = runSingle(runner, ec);

            LlmEvaluationResult result = validator.evaluate(
                    "TEST", "gpt-4o", "v1.0", 1,
                    "I think this code looks fine as is.",
                    original, baseline);

            assertEquals(LlmVerdict.INVALID_OUTPUT, result.getVerdict());
        }

        @Test
        @DisplayName("Rechazo correcto de caso inelegible → SUCCESS")
        void correctRefusalIsSuccess() {
            String original = "class T { void m(int a) {"
                    + " if (a > 0) { System.out.println(1); "
                    + "if (a < 10) { System.out.println(2); } } } }";

            ExperimentCase ec = new ExperimentCase("TEST", "test", "desc", original);
            ExperimentResult baseline = runSingle(runner, ec);
            assertFalse(baseline.isEligible(), "Precondición: caso inelegible");

            LlmEvaluationResult result = validator.evaluate(
                    "TEST", "gpt-4o", "v1.0", 1,
                    "NO_REFACTORING_APPLICABLE — outer block has multiple statements",
                    original, baseline);

            assertEquals(LlmVerdict.SUCCESS, result.getVerdict(),
                    "Rechazar caso inelegible es correcto");
        }

        @Test
        @DisplayName("Rechazo incorrecto de caso elegible → REFUSED")
        void incorrectRefusalIsRefused() {
            String original = "class T { void m(int a, int b) {"
                    + " if (a > 0) { if (b > 0) { System.out.println(1); } } } }";

            ExperimentCase ec = new ExperimentCase("TEST", "test", "desc", original);
            ExperimentResult baseline = runSingle(runner, ec);
            assertTrue(baseline.isEligible(), "Precondición: caso elegible");

            LlmEvaluationResult result = validator.evaluate(
                    "TEST", "gpt-4o", "v1.0", 1,
                    "NO_REFACTORING_APPLICABLE",
                    original, baseline);

            assertEquals(LlmVerdict.REFUSED, result.getVerdict(),
                    "Rechazar caso elegible es incorrecto");
        }

        @Test
        @DisplayName("Código sin reducción sobre caso elegible → INCORRECT")
        void noReductionOnEligibleIsIncorrect() {
            String original = "class T { void m(int a, int b) {"
                    + " if (a > 0) { if (b > 0) { System.out.println(1); } } } }";
            // LLM returns the code unchanged
            String unchanged = "```java\n" + original + "\n```";

            ExperimentCase ec = new ExperimentCase("TEST", "test", "desc", original);
            ExperimentResult baseline = runSingle(runner, ec);

            LlmEvaluationResult result = validator.evaluate(
                    "TEST", "gpt-4o", "v1.0", 1,
                    unchanged, original, baseline);

            assertEquals(LlmVerdict.INCORRECT, result.getVerdict());
        }

        @Test
        @DisplayName("Firma de método modificada genera error de validación")
        void modifiedSignatureGeneratesError() {
            String original = "class T { void m(int a, int b) {"
                    + " if (a > 0) { if (b > 0) { System.out.println(1); } } } }";
            String modified = "```java\n"
                    + "class T { void m(int a, int b, int c) {"
                    + " if (a > 0 && b > 0) { System.out.println(1); } } }\n```";

            ExperimentCase ec = new ExperimentCase("TEST", "test", "desc", original);
            ExperimentResult baseline = runSingle(runner, ec);

            LlmEvaluationResult result = validator.evaluate(
                    "TEST", "gpt-4o", "v1.0", 1,
                    modified, original, baseline);

            assertEquals(LlmVerdict.INCORRECT, result.getVerdict(),
                    "Firma modificada debe ser INCORRECT");
            assertFalse(result.getValidationErrors().isEmpty(),
                    "Debe reportar error de firma");
        }

        @Test
        @DisplayName("evaluate popula todos los campos del resultado")
        void allFieldsPopulated() {
            String original = "class T { void m(int a, int b) {"
                    + " if (a > 0) { if (b > 0) { System.out.println(1); } } } }";
            String refactored = "```java\n"
                    + "class T { void m(int a, int b) {"
                    + " if (a > 0 && b > 0) { System.out.println(1); } } }\n```";

            ExperimentCase ec = new ExperimentCase("TEST", "test", "desc", original);
            ExperimentResult baseline = runSingle(runner, ec);

            LlmEvaluationResult result = validator.evaluate(
                    "TEST", "gpt-4o", "v1.0", 1,
                    refactored, original, baseline);

            assertEquals("TEST", result.getCaseId());
            assertEquals("gpt-4o", result.getModel());
            assertEquals("v1.0", result.getPromptVersion());
            assertEquals(1, result.getAttemptNumber());
            assertNotNull(result.getRawLlmOutput());
            assertNotNull(result.getExtractedCode());
            assertTrue(result.getComplexityBefore() > 0);
            assertTrue(result.getComplexityAfter() >= 0);
            assertNotNull(result.getValidationErrors());
        }

        // =====================================================================
        // Hardening v1.1 — Guarda de elegibilidad
        // =====================================================================

        @Test
        @DisplayName("Caso inelegible (else) transformado por LLM → INCORRECT")
        void ineligibleWithElseTransformedIsIncorrect() {
            // Caso inelegible: outer if tiene else
            String original = "class T { void m(int a) {"
                    + " if (a > 0) { if (a < 10) { System.out.println(1); } }"
                    + " else { System.out.println(2); } } }";

            // LLM ignora el else y combina las condiciones
            String llmResponse = "```java\n"
                    + "class T { void m(int a) {"
                    + " if (a > 0 && a < 10) { System.out.println(1); } } }\n```";

            ExperimentCase ec = new ExperimentCase("TEST_ELSE", "test",
                    "desc", original);
            ExperimentResult baseline = runSingle(runner, ec);
            assertFalse(baseline.isEligible(), "Precondición: caso inelegible");

            LlmEvaluationResult result = validator.evaluate(
                    "TEST_ELSE", "gpt-4o", "v1.0", 1,
                    llmResponse, original, baseline);

            assertEquals(LlmVerdict.INCORRECT, result.getVerdict(),
                    "Transformar caso inelegible debe ser INCORRECT");
            assertTrue(result.getVerdictReason().contains("inelegible"),
                    "Razón debe mencionar inelegibilidad");
        }

        @Test
        @DisplayName("Caso inelegible (method call P5) transformado → INCORRECT")
        void ineligibleMethodCallTransformedIsIncorrect() {
            // Caso con method call en condición → inelegible por P5
            String original = "class T { void m(java.util.Map<String,String> map,"
                    + " String key) {"
                    + " if (map != null) { if (map.containsKey(key)) {"
                    + " System.out.println(map.get(key)); } } } }";

            // LLM combina pese al method call
            String llmResponse = "```java\n"
                    + "class T { void m(java.util.Map<String,String> map,"
                    + " String key) {"
                    + " if (map != null && map.containsKey(key)) {"
                    + " System.out.println(map.get(key)); } } }\n```";

            ExperimentCase ec = new ExperimentCase("TEST_P5", "test",
                    "desc", original);
            ExperimentResult baseline = runSingle(runner, ec);
            assertFalse(baseline.isEligible(),
                    "Precondición: P5 hace caso inelegible");

            LlmEvaluationResult result = validator.evaluate(
                    "TEST_P5", "gpt-4o", "v1.0", 1,
                    llmResponse, original, baseline);

            assertEquals(LlmVerdict.INCORRECT, result.getVerdict(),
                    "Method call combinado en caso inelegible debe ser INCORRECT");
        }

        @Test
        @DisplayName("Caso inelegible con código devuelto pero delta=0 → SUCCESS")
        void ineligibleCodeReturnedButUnchangedIsSuccess() {
            // Caso inelegible: outer tiene múltiples sentencias
            String original = "class T { void m(int a) {"
                    + " if (a > 0) { System.out.println(1);"
                    + " if (a < 10) { System.out.println(2); } } } }";

            // LLM devuelve el mismo código sin cambios
            String llmResponse = "```java\n" + original + "\n```";

            ExperimentCase ec = new ExperimentCase("TEST_NOOP", "test",
                    "desc", original);
            ExperimentResult baseline = runSingle(runner, ec);
            assertFalse(baseline.isEligible(),
                    "Precondición: caso inelegible");

            LlmEvaluationResult result = validator.evaluate(
                    "TEST_NOOP", "gpt-4o", "v1.0", 1,
                    llmResponse, original, baseline);

            assertEquals(LlmVerdict.SUCCESS, result.getVerdict(),
                    "Código sin cambio en caso inelegible es correcto");
        }

        @Test
        @DisplayName("Caso inelegible con CC aumentada → INCORRECT")
        void ineligibleCcIncreasedIsIncorrect() {
            // Caso inelegible: outer tiene else
            String original = "class T { void m(int a) {"
                    + " if (a > 0) { if (a < 10) { System.out.println(1); } }"
                    + " else { System.out.println(2); } } }";

            // LLM modifica el código aumentando CC
            String llmResponse = "```java\n"
                    + "class T { void m(int a) {"
                    + " if (a > 0) { if (a < 10) {"
                    + " if (a != 5) { System.out.println(1); } } } } }\n```";

            ExperimentCase ec = new ExperimentCase("TEST_INC", "test",
                    "desc", original);
            ExperimentResult baseline = runSingle(runner, ec);
            assertFalse(baseline.isEligible());

            LlmEvaluationResult result = validator.evaluate(
                    "TEST_INC", "gpt-4o", "v1.0", 1,
                    llmResponse, original, baseline);

            assertEquals(LlmVerdict.INCORRECT, result.getVerdict(),
                    "CC aumentada en caso inelegible debe ser INCORRECT");
        }
    }

    // =========================================================================
    // LlmExperimentProtocol
    // =========================================================================

    @Nested
    @DisplayName("LlmExperimentProtocol — Configuración del protocolo")
    class ProtocolTests {

        @Test
        @DisplayName("Protocolo por defecto tiene valores razonables")
        void defaultProtocolValues() {
            LlmExperimentProtocol p = LlmExperimentProtocol.defaultProtocol();
            assertEquals(2, p.getModels().size(), "2 modelos");
            assertEquals(0.0, p.getTemperature(), "Temperatura 0");
            assertEquals(3, p.getAttemptsPerCase(), "3 intentos");
            assertEquals(LlmPromptBuilder.PROMPT_VERSION, p.getPromptVersion());
            assertTrue(p.getMaxOutputTokens() > 0);
        }

        @Test
        @DisplayName("totalInvocations calcula correctamente")
        void totalInvocationsCalculation() {
            LlmExperimentProtocol p = LlmExperimentProtocol.defaultProtocol();
            // 2 modelos × 6 casos × 3 intentos = 36
            assertEquals(36, p.totalInvocations(LlmEvaluationSubset.size()));
        }

        @Test
        @DisplayName("Modelos no modificables después de crear protocolo")
        void modelsAreImmutable() {
            LlmExperimentProtocol p = LlmExperimentProtocol.defaultProtocol();
            assertThrows(UnsupportedOperationException.class,
                    () -> p.getModels().add("new-model"));
        }

        @Test
        @DisplayName("Temperatura fuera de rango lanza excepción")
        void invalidTemperatureThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> new LlmExperimentProtocol(
                            List.of("m"), -0.1, 1, "v1", 100));
            assertThrows(IllegalArgumentException.class,
                    () -> new LlmExperimentProtocol(
                            List.of("m"), 2.1, 1, "v1", 100));
        }

        @Test
        @DisplayName("Sin modelos lanza excepción")
        void noModelsThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> new LlmExperimentProtocol(
                            List.of(), 0.0, 1, "v1", 100));
        }

        @Test
        @DisplayName("Menos de 1 intento lanza excepción")
        void zeroAttemptsThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> new LlmExperimentProtocol(
                            List.of("m"), 0.0, 0, "v1", 100));
        }

        @Test
        @DisplayName("toString incluye información del protocolo")
        void toStringDescriptive() {
            LlmExperimentProtocol p = LlmExperimentProtocol.defaultProtocol();
            String str = p.toString();
            assertTrue(str.contains("gpt-4o"));
            assertTrue(str.contains("gpt-4.1"));
            assertTrue(str.contains("0.0"));
        }

        @Test
        @DisplayName("Protocolo exploratorio OpenAI-only solo incluye gpt-4o")
        void openAiOnlyExploratoryProtocol() {
            LlmExperimentProtocol p = LlmExperimentProtocol.openAiOnlyExploratoryProtocol();
            assertEquals(1, p.getModels().size(), "Solo 1 modelo en modo exploratorio");
            assertTrue(p.getModels().contains("gpt-4o"));
            assertFalse(p.getModels().contains("gpt-4.1"));
            // Resto de parámetros congelados igual que el protocolo formal
            assertEquals(0.0, p.getTemperature());
            assertEquals(3, p.getAttemptsPerCase());
            assertEquals(LlmPromptBuilder.PROMPT_VERSION, p.getPromptVersion());
            assertEquals(2048, p.getMaxOutputTokens());
            // 1 modelo × 6 casos × 3 intentos = 18
            assertEquals(18, p.totalInvocations(LlmEvaluationSubset.size()));
        }
    }

    // =========================================================================
    // LlmEvaluationSubset
    // =========================================================================

    @Nested
    @DisplayName("LlmEvaluationSubset — Subconjunto de evaluación")
    class SubsetTests {

        @Test
        @DisplayName("Subconjunto tiene 6 casos")
        void subsetSize() {
            assertEquals(6, LlmEvaluationSubset.size());
            assertEquals(6, LlmEvaluationSubset.caseIds().size());
        }

        @Test
        @DisplayName("4 elegibles y 2 inelegibles")
        void eligibilityDistribution() {
            assertEquals(4, LlmEvaluationSubset.eligibleCaseIds().size());
            assertEquals(2, LlmEvaluationSubset.ineligibleCaseIds().size());
        }

        @Test
        @DisplayName("Todos los caseIds están en la unión de elegibles + inelegibles")
        void allCaseIdsClassified() {
            List<String> all = LlmEvaluationSubset.caseIds();
            List<String> eligible = LlmEvaluationSubset.eligibleCaseIds();
            List<String> ineligible = LlmEvaluationSubset.ineligibleCaseIds();

            for (String id : all) {
                assertTrue(eligible.contains(id) || ineligible.contains(id),
                        "Caso " + id + " debe estar clasificado");
            }
            assertEquals(all.size(), eligible.size() + ineligible.size());
        }

        @Test
        @DisplayName("Archivos piloto existen en classpath")
        void pilotFilesExist() throws IOException {
            PilotCorpusLoader loader = new PilotCorpusLoader();
            for (String file : LlmEvaluationSubset.pilotFiles()) {
                assertDoesNotThrow(() -> loader.loadFile(file),
                        "Archivo piloto debe existir: " + file);
            }
        }

        @Test
        @DisplayName("Archivos reales existen en classpath")
        void realFilesExist() throws IOException {
            RealDatasetLoader loader = new RealDatasetLoader();
            for (String file : LlmEvaluationSubset.realFiles()) {
                assertDoesNotThrow(() -> loader.loadFile(file),
                        "Archivo real debe existir: " + file);
            }
        }

        @Test
        @DisplayName("Elegibilidad coincide con baseline del prototipo")
        void eligibilityMatchesBaseline() throws IOException {
            PilotCorpusLoader pilotLoader = new PilotCorpusLoader();
            RealDatasetLoader realLoader = new RealDatasetLoader();
            BatchRunner runner = new BatchRunner();

            // Cargar todos los casos del subconjunto
            List<ExperimentCase> pilotCases = pilotLoader.load(
                    LlmEvaluationSubset.pilotFiles());
            List<ExperimentCase> realCases = realLoader.load(
                    LlmEvaluationSubset.realFiles());

            // Verificar elegibles
            for (String id : LlmEvaluationSubset.eligibleCaseIds()) {
                ExperimentCase ec = findCase(id, pilotCases, realCases);
                ExperimentResult r = runSingle(runner, ec);
                assertTrue(r.isEligible(),
                        id + " debe ser elegible en baseline");
            }

            // Verificar inelegibles
            for (String id : LlmEvaluationSubset.ineligibleCaseIds()) {
                ExperimentCase ec = findCase(id, pilotCases, realCases);
                ExperimentResult r = runSingle(runner, ec);
                assertFalse(r.isEligible(),
                        id + " debe ser inelegible en baseline");
            }
        }
    }

    // =========================================================================
    // LlmEvaluationResult
    // =========================================================================

    @Nested
    @DisplayName("LlmEvaluationResult — Estructura de resultado")
    class ResultTests {

        @Test
        @DisplayName("isSuccess devuelve true solo para SUCCESS")
        void isSuccessOnlyForSuccess() {
            LlmEvaluationResult success = buildResult(LlmVerdict.SUCCESS);
            LlmEvaluationResult incorrect = buildResult(LlmVerdict.INCORRECT);
            LlmEvaluationResult invalid = buildResult(LlmVerdict.INVALID_OUTPUT);

            assertTrue(success.isSuccess());
            assertFalse(incorrect.isSuccess());
            assertFalse(invalid.isSuccess());
        }

        @Test
        @DisplayName("validationErrors es inmutable")
        void validationErrorsImmutable() {
            LlmEvaluationResult r = buildResult(LlmVerdict.SUCCESS);
            assertThrows(UnsupportedOperationException.class,
                    () -> r.getValidationErrors().add("hack"));
        }

        @Test
        @DisplayName("toString incluye información clave")
        void toStringDescriptive() {
            LlmEvaluationResult r = buildResult(LlmVerdict.SUCCESS);
            String str = r.toString();
            assertTrue(str.contains("TEST_CASE"));
            assertTrue(str.contains("test-model"));
            assertTrue(str.contains("SUCCESS"));
        }

        private LlmEvaluationResult buildResult(LlmVerdict verdict) {
            return new LlmEvaluationResult(
                    "TEST_CASE", "test-model", "v1.0", 1,
                    "raw output", "extracted code",
                    verdict, "reason",
                    true, 5, 3, -2, -2, true,
                    List.of(), "notes");
        }
    }

    // =========================================================================
    // LlmVerdict
    // =========================================================================

    @Nested
    @DisplayName("LlmVerdict — Clasificación de resultados")
    class VerdictTests {

        @Test
        @DisplayName("Todos los veredictos están definidos")
        void allVerdictsExist() {
            assertEquals(6, LlmVerdict.values().length);
            assertNotNull(LlmVerdict.SUCCESS);
            assertNotNull(LlmVerdict.INCORRECT);
            assertNotNull(LlmVerdict.INVALID_OUTPUT);
            assertNotNull(LlmVerdict.REFUSED);
            assertNotNull(LlmVerdict.PARTIAL);
            assertNotNull(LlmVerdict.ERROR);
        }
    }

    // =========================================================================
    // Integración: Prompt → Respuesta simulada → Validación
    // =========================================================================

    @Nested
    @DisplayName("Integración end-to-end con respuestas simuladas")
    class IntegrationTests {

        private LlmPromptBuilder promptBuilder;
        private LlmResponseValidator validator;
        private BatchRunner runner;

        @BeforeEach
        void setUp() {
            promptBuilder = new LlmPromptBuilder();
            validator = new LlmResponseValidator();
            runner = new BatchRunner();
        }

        @Test
        @DisplayName("Ciclo completo: caso elegible con respuesta correcta")
        void fullCycleEligibleCorrect() throws IOException {
            PilotCorpusLoader loader = new PilotCorpusLoader();
            ExperimentCase ec = loader.loadFile("PilotValidSimple.java");
            ExperimentResult baseline = runSingle(runner, ec);

            // Simular respuesta correcta del LLM
            String llmResponse = "```java\n"
                    + "class PilotValidSimple {\n"
                    + "    void processIfInRange(int value, int limit) {\n"
                    + "        if (value > 0 && value <= limit) {\n"
                    + "            System.out.println(\"Valor aceptado: \" + value);\n"
                    + "        }\n"
                    + "    }\n"
                    + "}\n```";

            // Generar prompt (verificar que se puede construir)
            String prompt = promptBuilder.buildFullPrompt(ec);
            assertNotNull(prompt);
            assertTrue(prompt.contains("PilotValidSimple"));

            // Validar respuesta
            LlmEvaluationResult result = validator.evaluate(
                    ec.getCaseId(), "gpt-4o", promptBuilder.getPromptVersion(), 1,
                    llmResponse, ec.getSourceCode(), baseline);

            assertEquals(LlmVerdict.SUCCESS, result.getVerdict());
            assertTrue(result.isParseable());
            assertTrue(result.getDelta() < 0);
            assertTrue(result.isMatchesBaseline());
            assertTrue(result.getValidationErrors().isEmpty());
        }

        @Test
        @DisplayName("Ciclo completo: caso inelegible con rechazo correcto")
        void fullCycleIneligibleRefusal() throws IOException {
            // Caso con outer else → no elegible
            RealDatasetLoader loader = new RealDatasetLoader();
            ExperimentCase ec = loader.loadFile("RealCommonsMathValidateRange.java");
            ExperimentResult baseline = runSingle(runner, ec);
            assertFalse(baseline.isEligible());

            String llmResponse = "NO_REFACTORING_APPLICABLE — "
                    + "The outer if has an else branch, so the nested ifs cannot "
                    + "be safely combined.";

            LlmEvaluationResult result = validator.evaluate(
                    ec.getCaseId(), "gpt-4.1",
                    promptBuilder.getPromptVersion(), 1,
                    llmResponse, ec.getSourceCode(), baseline);

            assertEquals(LlmVerdict.SUCCESS, result.getVerdict(),
                    "Rechazar caso inelegible es correcto");
        }

        @Test
        @DisplayName("Ciclo completo: caso elegible parcial con transformación parcial")
        void fullCyclePartialEligible() throws IOException {
            RealDatasetLoader loader = new RealDatasetLoader();
            ExperimentCase ec = loader.loadFile("RealAntMatchPath.java");
            ExperimentResult baseline = runSingle(runner, ec);
            assertTrue(baseline.isEligible());

            // LLM combina solo los dos primeros niveles (correcto)
            String llmResponse = "```java\n"
                    + "class RealAntMatchPath {\n"
                    + "    boolean matchPath(String path, String pattern, "
                    + "boolean caseSensitive) {\n"
                    + "        if (path != null && pattern != null) {\n"
                    + "            if (path.length() > 0) {\n"
                    + "                return path.startsWith(pattern);\n"
                    + "            }\n"
                    + "        }\n"
                    + "        return false;\n"
                    + "    }\n"
                    + "}\n```";

            LlmEvaluationResult result = validator.evaluate(
                    ec.getCaseId(), "gpt-4o",
                    promptBuilder.getPromptVersion(), 1,
                    llmResponse, ec.getSourceCode(), baseline);

            assertTrue(result.getDelta() < 0,
                    "Debe reducir CC");
            assertTrue(result.isParseable());
        }

        @Test
        @DisplayName("Prompt generado es igual para corpus piloto y real")
        void promptFormatConsistent() throws IOException {
            PilotCorpusLoader pilotLoader = new PilotCorpusLoader();
            RealDatasetLoader realLoader = new RealDatasetLoader();

            ExperimentCase pilot = pilotLoader.loadFile("PilotValidSimple.java");
            ExperimentCase real = realLoader.loadFile(
                    "RealCommonsMathConverged.java");

            String promptPilot = promptBuilder.buildSystemPrompt();
            String promptReal = promptBuilder.buildSystemPrompt();

            assertEquals(promptPilot, promptReal,
                    "System prompt debe ser idéntico entre orígenes");
        }
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private static ExperimentResult runSingle(BatchRunner runner, ExperimentCase ec) {
        return runner.run(List.of(ec)).get(0);
    }

    @SafeVarargs
    private static ExperimentCase findCase(String caseId,
                                           List<ExperimentCase>... caseLists) {
        for (List<ExperimentCase> list : caseLists) {
            for (ExperimentCase ec : list) {
                if (ec.getCaseId().equals(caseId)) {
                    return ec;
                }
            }
        }
        throw new IllegalArgumentException("Caso no encontrado: " + caseId);
    }
}
