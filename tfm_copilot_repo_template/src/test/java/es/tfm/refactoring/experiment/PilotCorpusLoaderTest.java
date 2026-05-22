package es.tfm.refactoring.experiment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del cargador de corpus piloto y de la ejecución del pipeline sobre él.
 */
@DisplayName("PilotCorpusLoader — Ingesta y procesamiento del corpus piloto")
class PilotCorpusLoaderTest {

    private PilotCorpusLoader loader;

    @BeforeEach
    void setUp() {
        loader = new PilotCorpusLoader();
    }

    // =========================================================================
    // Carga de archivos
    // =========================================================================

    @Nested
    @DisplayName("Carga de recursos")
    class Loading {

        @Test
        @DisplayName("standardPilotFiles() descubre todos los archivos del corpus piloto")
        void standardFileCount() {
            int count = PilotCorpusLoader.standardPilotFiles().size();
            assertFalse(PilotCorpusLoader.standardPilotFiles().isEmpty(),
                    "El corpus piloto no debe estar vacío");
            assertTrue(count >= 8,
                    "El corpus piloto debe tener al menos 8 casos, tiene: " + count);
        }

        @Test
        @DisplayName("Carga todos los archivos estándar sin excepción")
        void loadAllStandard() throws IOException {
            List<String> files = PilotCorpusLoader.standardPilotFiles();
            List<ExperimentCase> cases = loader.load(files);
            assertEquals(files.size(), cases.size());
        }

        @Test
        @DisplayName("Cada caso cargado tiene caseId, origin y source no vacíos")
        void casesHaveRequiredFields() throws IOException {
            List<ExperimentCase> cases = loader.load(
                    PilotCorpusLoader.standardPilotFiles());

            for (ExperimentCase ec : cases) {
                assertNotNull(ec.getCaseId(), "caseId no debe ser null");
                assertFalse(ec.getCaseId().isEmpty(), "caseId no debe ser vacío");
                assertNotNull(ec.getOrigin(), "origin no debe ser null");
                assertNotNull(ec.getSourceCode(), "sourceCode no debe ser null");
                assertTrue(ec.getSourceCode().length() > 10,
                        ec.getCaseId() + ": source demasiado corto");
            }
        }

        @Test
        @DisplayName("Metadatos @caseId se extraen correctamente")
        void caseIdExtraction() throws IOException {
            ExperimentCase ec = loader.loadFile("PilotValidSimple.java");
            assertEquals("PILOT_VALID_SIMPLE", ec.getCaseId());
        }

        @Test
        @DisplayName("Metadatos @origin se extraen correctamente")
        void originExtraction() throws IOException {
            ExperimentCase ec = loader.loadFile("PilotValidSimple.java");
            assertEquals("synthetic-realistic", ec.getOrigin());
        }

        @Test
        @DisplayName("Metadatos @description se extraen correctamente")
        void descriptionExtraction() throws IOException {
            ExperimentCase ec = loader.loadFile("PilotValidSimple.java");
            assertTrue(ec.getDescription().contains("Validación de rango"),
                    "La description debe contener lo indicado en el archivo");
        }

        @Test
        @DisplayName("Archivo inexistente lanza IOException")
        void missingFileThrowsIOException() {
            assertThrows(IOException.class,
                    () -> loader.loadFile("NoExiste.java"));
        }

        @Test
        @DisplayName("Lista vacía devuelve lista vacía sin error")
        void emptyListLoadsEmpty() throws IOException {
            List<ExperimentCase> cases = loader.load(List.of());
            assertTrue(cases.isEmpty());
        }
    }

    // =========================================================================
    // Pipeline sobre corpus piloto
    // =========================================================================

    @Nested
    @DisplayName("Pipeline sobre corpus piloto")
    class PipelineExecution {

        private BatchRunner runner;
        private List<ExperimentCase> pilotCases;

        @BeforeEach
        void setUpPipeline() throws IOException {
            runner = new BatchRunner();
            pilotCases = loader.load(PilotCorpusLoader.standardPilotFiles());
        }

        @Test
        @DisplayName("Pipeline procesa todos los casos del corpus piloto y produce un resultado por caso")
        void processesAllCases() {
            List<ExperimentResult> results = runner.run(pilotCases);
            assertEquals(pilotCases.size(), results.size());
        }

        @Test
        @DisplayName("PilotValidSimple: elegible, al menos 1 oportunidad aplicada")
        void validSimple() {
            ExperimentResult r = runner.processCase(findCase("PILOT_VALID_SIMPLE"));
            assertTrue(r.isEligible());
            assertTrue(r.getOpportunitiesApplied() >= 1);
            assertTrue(r.getDelta() < 0, "Debe reducir complejidad");
        }

        @Test
        @DisplayName("PilotValidNullCheck: elegible")
        void validNullCheck() {
            ExperimentResult r = runner.processCase(findCase("PILOT_VALID_NULL_CHECK"));
            assertTrue(r.isEligible());
            assertTrue(r.getDelta() < 0);
        }

        @Test
        @DisplayName("PilotValidBoundsCheck: elegible")
        void validBoundsCheck() {
            ExperimentResult r = runner.processCase(findCase("PILOT_VALID_BOUNDS_CHECK"));
            assertTrue(r.isEligible());
            assertTrue(r.getDelta() < 0);
        }

        @Test
        @DisplayName("PilotValidNestedInLoop: elegible, mayor impacto por nesting")
        void validNestedInLoop() {
            ExperimentResult r = runner.processCase(findCase("PILOT_VALID_NESTED_IN_LOOP"));
            assertTrue(r.isEligible());
            assertTrue(r.getDelta() < 0);
            // Inside loop → higher complexity reduction
            assertTrue(r.getComplexityBefore() > r.getComplexityAfter());
        }

        @Test
        @DisplayName("PilotInvalidElseBranch: inelegible, categoría OUTER_HAS_ELSE")
        void invalidElseBranch() {
            ExperimentResult r = runner.processCase(findCase("PILOT_INVALID_ELSE_BRANCH"));
            assertFalse(r.isEligible());
            assertTrue(r.getDiscardCategories().contains("OUTER_HAS_ELSE"),
                    "Debe incluir OUTER_HAS_ELSE: " + r.getDiscardCategories());
        }

        @Test
        @DisplayName("PilotInvalidMethodCallGuard: inelegible, categoría METHOD_CALL_IN_CONDITION")
        void invalidMethodCall() {
            ExperimentResult r = runner.processCase(findCase("PILOT_INVALID_METHOD_GUARD"));
            assertFalse(r.isEligible());
            assertTrue(r.getDiscardCategories().contains("METHOD_CALL_IN_CONDITION"),
                    "Debe incluir METHOD_CALL_IN_CONDITION: " + r.getDiscardCategories());
        }

        @Test
        @DisplayName("PilotInvalidMultiStatement: inelegible, categoría OUTER_BLOCK_MULTIPLE_STATEMENTS")
        void invalidMultiStatement() {
            ExperimentResult r = runner.processCase(findCase("PILOT_INVALID_MULTI_STMT"));
            assertFalse(r.isEligible());
            assertTrue(r.getDiscardCategories().contains("OUTER_BLOCK_MULTIPLE_STATEMENTS"),
                    "Debe incluir OUTER_BLOCK_MULTIPLE_STATEMENTS: " + r.getDiscardCategories());
        }

        @Test
        @DisplayName("PilotMixedOpportunities: elegible con al menos 1 oportunidad aplicada")
        void mixedOpportunities() {
            ExperimentResult r = runner.processCase(findCase("PILOT_MIXED_OPPORTUNITIES"));
            // Mixed case: has at least one valid pattern, so pipeline should be eligible
            assertTrue(r.isEligible(),
                    "Mixed case should be eligible because it has at least one valid pattern");
            assertTrue(r.getOpportunitiesApplied() >= 1);
        }

        @Test
        @DisplayName("Todos los resultados tienen complejidad medida (>= 0)")
        void allHaveComplexity() {
            List<ExperimentResult> results = runner.run(pilotCases);
            for (ExperimentResult r : results) {
                assertTrue(r.getComplexityBefore() >= 0,
                        r.getCaseId() + ": complexityBefore >= 0");
            }
        }

        @Test
        @DisplayName("Todos los inelegibles tienen discardCategories no vacío")
        void ineligibleHaveCategories() {
            List<ExperimentResult> results = runner.run(pilotCases);
            for (ExperimentResult r : results) {
                if (!r.isEligible() && r.getComplexityBefore() >= 0) {
                    assertFalse(r.getDiscardCategories().isEmpty(),
                            r.getCaseId() + " inelegible debe tener categorías de descarte");
                }
            }
        }

        private ExperimentCase findCase(String caseId) {
            return pilotCases.stream()
                    .filter(c -> c.getCaseId().equals(caseId))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("Case not found: " + caseId));
        }
    }

    private static class AssertionError extends RuntimeException {
        AssertionError(String msg) { super(msg); }
    }
}
