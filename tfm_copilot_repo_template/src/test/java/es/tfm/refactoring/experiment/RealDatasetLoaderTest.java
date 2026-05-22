package es.tfm.refactoring.experiment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del cargador de corpus real y ejecución del pipeline sobre él.
 * <p>
 * Valida la carga de archivos desde /real-corpus/, la extracción de metadatos
 * extendidos y la clasificación correcta de elegibilidad por el pipeline.
 */
@DisplayName("RealDatasetLoader — Ingesta y procesamiento del corpus real")
class RealDatasetLoaderTest {

    private RealDatasetLoader loader;

    @BeforeEach
    void setUp() {
        loader = new RealDatasetLoader();
    }

    // =========================================================================
    // Carga de archivos
    // =========================================================================

    @Nested
    @DisplayName("Carga de recursos")
    class Loading {

        @Test
        @DisplayName("standardRealFiles() descubre al menos 100 archivos del corpus")
        void standardFileCount() {
            int count = RealDatasetLoader.standardRealFiles().size();
            assertTrue(count >= 100,
                    "El corpus real debe tener al menos 100 casos, tiene: " + count);
        }

        @Test
        @DisplayName("Carga todos los archivos estándar sin excepción")
        void loadAllStandard() throws IOException {
            List<String> files = RealDatasetLoader.standardRealFiles();
            List<ExperimentCase> cases = loader.load(files);
            assertEquals(files.size(), cases.size());
        }

        @Test
        @DisplayName("Cada caso cargado tiene caseId, origin y source no vacíos")
        void casesHaveRequiredFields() throws IOException {
            List<ExperimentCase> cases = loader.load(
                    RealDatasetLoader.standardRealFiles());

            for (ExperimentCase ec : cases) {
                assertNotNull(ec.getCaseId(), "caseId no debe ser null");
                assertFalse(ec.getCaseId().isEmpty(),
                        ec + ": caseId no debe ser vacío");
                assertNotNull(ec.getOrigin(), "origin no debe ser null");
                assertNotNull(ec.getSourceCode(), "sourceCode no debe ser null");
                assertTrue(ec.getSourceCode().length() > 10,
                        ec.getCaseId() + ": source demasiado corto");
            }
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
    // Extracción de metadatos extendidos
    // =========================================================================

    @Nested
    @DisplayName("Metadatos extendidos")
    class Metadata {

        @Test
        @DisplayName("@caseId extraído correctamente")
        void caseIdExtraction() throws IOException {
            ExperimentCase ec = loader.loadFile("RealCommonsLangChomp.java");
            assertEquals("REAL_COMMONS_LANG_CHOMP", ec.getCaseId());
        }

        @Test
        @DisplayName("@origin extraído correctamente")
        void originExtraction() throws IOException {
            ExperimentCase ec = loader.loadFile("RealCommonsLangChomp.java");
            assertEquals("commons-lang-3.12.0", ec.getOrigin());
        }

        @Test
        @DisplayName("@project extraído como metadato extra")
        void projectExtraction() throws IOException {
            ExperimentCase ec = loader.loadFile("RealCommonsLangChomp.java");
            String project = loader.extractExtra(ec.getSourceCode(), "@project");
            assertEquals("Apache Commons Lang", project);
        }

        @Test
        @DisplayName("@license extraído como metadato extra")
        void licenseExtraction() throws IOException {
            ExperimentCase ec = loader.loadFile("RealCommonsMathConverged.java");
            String license = loader.extractExtra(ec.getSourceCode(), "@license");
            assertEquals("Apache-2.0", license);
        }

        @Test
        @DisplayName("@sonarCCBefore extraído como metadato extra")
        void sonarCCBeforeExtraction() throws IOException {
            ExperimentCase ec = loader.loadFile("RealCommonsMathConverged.java");
            String ccBefore = loader.extractExtra(ec.getSourceCode(), "@sonarCCBefore");
            assertEquals("3", ccBefore);
        }

        @Test
        @DisplayName("Metadato inexistente devuelve null")
        void missingMetadataReturnsNull() throws IOException {
            ExperimentCase ec = loader.loadFile("RealCommonsLangChomp.java");
            assertNull(loader.extractExtra(ec.getSourceCode(), "@nonexistent"));
        }

        @Test
        @DisplayName("Todos los archivos reales tienen @project, @license y @sonarCCBefore")
        void allFilesHaveExtendedMetadata() throws IOException {
            List<ExperimentCase> cases = loader.load(
                    RealDatasetLoader.standardRealFiles());

            for (ExperimentCase ec : cases) {
                String source = ec.getSourceCode();
                assertNotNull(loader.extractExtra(source, "@project"),
                        ec.getCaseId() + ": falta @project");
                assertNotNull(loader.extractExtra(source, "@license"),
                        ec.getCaseId() + ": falta @license");
                assertNotNull(loader.extractExtra(source, "@sonarCCBefore"),
                        ec.getCaseId() + ": falta @sonarCCBefore");
            }
        }
    }

    // =========================================================================
    // Pipeline sobre corpus real
    // =========================================================================

    @Nested
    @DisplayName("Pipeline sobre corpus real")
    class PipelineExecution {

        private BatchRunner runner;
        private List<ExperimentCase> realCases;

        @BeforeEach
        void setUpPipeline() throws IOException {
            runner = new BatchRunner();
            realCases = loader.load(RealDatasetLoader.standardRealFiles());
        }

        @Test
        @DisplayName("Pipeline procesa todos los casos del corpus y produce un resultado por caso")
        void processesAllCases() {
            List<ExperimentResult> results = runner.run(realCases);
            assertEquals(realCases.size(), results.size());
        }

        @Test
        @DisplayName("Todos los resultados tienen complejidad medida (>= 0)")
        void allHaveComplexity() {
            List<ExperimentResult> results = runner.run(realCases);
            for (ExperimentResult r : results) {
                assertTrue(r.getComplexityBefore() >= 0,
                        r.getCaseId() + ": complexityBefore debe ser >= 0");
                assertTrue(r.getComplexityAfter() >= 0,
                        r.getCaseId() + ": complexityAfter debe ser >= 0");
            }
        }

        // --- Casos elegibles ---

        @Test
        @DisplayName("RealCommonsMathConverged: elegible, CC 3→2, delta -1")
        void commonsMathConverged() {
            ExperimentResult r = runCase("REAL_COMMONS_MATH_CONVERGED");
            assertTrue(r.isEligible());
            assertEquals(3, r.getComplexityBefore());
            assertEquals(2, r.getComplexityAfter());
            assertEquals(-1, r.getDelta());
        }

        @Test
        @DisplayName("RealCommonsCollectionsIsEmpty: elegible, CC 3→2, delta -1")
        void commonsCollectionsIsEmpty() {
            ExperimentResult r = runCase("REAL_COMMONS_COLLECTIONS_ISEMPTY");
            assertTrue(r.isEligible());
            assertEquals(3, r.getComplexityBefore());
            assertEquals(2, r.getComplexityAfter());
            assertEquals(-1, r.getDelta());
        }

        @Test
        @DisplayName("RealAntMatchPath: elegible parcial, CC 6→4, delta -2")
        void antMatchPath() {
            ExperimentResult r = runCase("REAL_ANT_MATCH_PATH");
            assertTrue(r.isEligible(),
                    "Primer par (null checks) es combinable");
            assertEquals(6, r.getComplexityBefore());
            assertEquals(4, r.getComplexityAfter());
            assertEquals(-2, r.getDelta());
        }

        @Test
        @DisplayName("RealCommonsLangContainsNone: elegible parcial, CC 15→11, delta -4")
        void commonsLangContainsNone() {
            ExperimentResult r = runCase("REAL_COMMONS_LANG_CONTAINS_NONE");
            assertTrue(r.isEligible(),
                    "Primer par (null checks) es combinable");
            assertEquals(15, r.getComplexityBefore());
            assertEquals(11, r.getComplexityAfter());
            assertEquals(-4, r.getDelta());
        }

        @Test
        @DisplayName("RealCommonsLangMid: elegible parcial, CC 10→7, delta -3")
        void commonsLangMid() {
            ExperimentResult r = runCase("REAL_COMMONS_LANG_MID");
            assertTrue(r.isEligible(),
                    "Primer par (str!=null, len>=0) es combinable");
            assertEquals(10, r.getComplexityBefore());
            assertEquals(7, r.getComplexityAfter());
            assertEquals(-3, r.getDelta());
        }

        // --- Casos no elegibles ---

        @Test
        @DisplayName("RealCommonsLangChomp: no elegible (P5 — str.length() method call)")
        void commonsLangChomp() {
            ExperimentResult r = runCase("REAL_COMMONS_LANG_CHOMP");
            assertFalse(r.isEligible());
            assertEquals(6, r.getComplexityBefore());
            assertEquals(r.getComplexityBefore(), r.getComplexityAfter(),
                    "CC invariante: no se aplica refactorización");
        }

        @Test
        @DisplayName("RealCommonsLangIsNumeric: no elegible (P5 — cs.length() method call)")
        void commonsLangIsNumeric() {
            ExperimentResult r = runCase("REAL_COMMONS_LANG_IS_NUMERIC");
            assertFalse(r.isEligible());
            assertEquals(10, r.getComplexityBefore());
        }

        @Test
        @DisplayName("RealCommonsCollectionsGet: no elegible (P5 — containsKey method call)")
        void commonsCollectionsGet() {
            ExperimentResult r = runCase("REAL_COMMONS_COLLECTIONS_GET");
            assertFalse(r.isEligible());
            assertEquals(3, r.getComplexityBefore());
        }

        @Test
        @DisplayName("RealCommonsMathValidateRange: no elegible (P1 — outer has else)")
        void commonsMathValidateRange() {
            ExperimentResult r = runCase("REAL_COMMONS_MATH_VALIDATE_RANGE");
            assertFalse(r.isEligible());
            assertTrue(r.getDiscardCategories().contains("OUTER_HAS_ELSE"),
                    "Debe incluir OUTER_HAS_ELSE: " + r.getDiscardCategories());
        }

        @Test
        @DisplayName("RealAntExecuteTask: no elegible (P2 — multiple statements)")
        void antExecuteTask() {
            ExperimentResult r = runCase("REAL_ANT_EXECUTE_TASK");
            assertFalse(r.isEligible());
            assertTrue(r.getDiscardCategories().contains(
                    "OUTER_BLOCK_MULTIPLE_STATEMENTS"),
                    "Debe incluir OUTER_BLOCK_MULTIPLE_STATEMENTS: "
                            + r.getDiscardCategories());
        }

        // --- Nuevos casos elegibles (lote ampliado) ---

        @Test
        @DisplayName("RealCommonsIoArraySubrange: elegible, null + field access, CC 3→2")
        void commonsIoArraySubrange() {
            ExperimentResult r = runCase("REAL_COMMONS_IO_ARRAY_SUBRANGE");
            assertTrue(r.isEligible(), "null+field access debe ser elegible");
            assertEquals(3, r.getComplexityBefore());
            assertEquals(2, r.getComplexityAfter());
            assertEquals(-1, r.getDelta());
        }

        @Test
        @DisplayName("RealCommonsLangValidIndex: elegible, null + aritmética pura, CC 3→2")
        void commonsLangValidIndex() {
            ExperimentResult r = runCase("REAL_COMMONS_LANG_VALID_INDEX");
            assertTrue(r.isEligible(), "null+aritmética debe ser elegible");
            assertEquals(3, r.getComplexityBefore());
            assertEquals(2, r.getComplexityAfter());
            assertEquals(-1, r.getDelta());
        }

        @Test
        @DisplayName("RealAntScanEnabled: elegible, null + booleano, CC 3→2")
        void antScanEnabled() {
            ExperimentResult r = runCase("REAL_ANT_SCAN_ENABLED");
            assertTrue(r.isEligible(), "null+boolean debe ser elegible");
            assertEquals(3, r.getComplexityBefore());
            assertEquals(2, r.getComplexityAfter());
            assertEquals(-1, r.getDelta());
        }

        @Test
        @DisplayName("RealCommonsMathInInterval: elegible, dos comparaciones aritméticas, CC 3→2")
        void commonsMathInInterval() {
            ExperimentResult r = runCase("REAL_COMMONS_MATH_IN_INTERVAL");
            assertTrue(r.isEligible(), "dos comparaciones puras deben ser elegibles");
            assertEquals(3, r.getComplexityBefore());
            assertEquals(2, r.getComplexityAfter());
            assertEquals(-1, r.getDelta());
        }

        @Test
        @DisplayName("RealCommonsLangTripleGuard: elegible, triple null check, CC 6→2, delta -4")
        void commonsLangTripleGuard() {
            ExperimentResult r = runCase("REAL_COMMONS_LANG_TRIPLE_GUARD");
            assertTrue(r.isEligible(), "triple null check debe ser elegible");
            assertEquals(6, r.getComplexityBefore());
            assertEquals(2, r.getComplexityAfter());
            assertEquals(-4, r.getDelta());
        }

        // --- Nuevos casos no elegibles (lote ampliado, razones nuevas) ---

        @Test
        @DisplayName("RealCommonsCollectionsInnerElse: no elegible (P4 — inner has else)")
        void commonsCollectionsInnerElse() {
            ExperimentResult r = runCase("REAL_COMMONS_COLLECTIONS_INNER_ELSE");
            assertFalse(r.isEligible());
            assertTrue(r.getDiscardCategories().contains("INNER_HAS_ELSE"),
                    "Debe incluir INNER_HAS_ELSE: " + r.getDiscardCategories());
        }

        @Test
        @DisplayName("RealCommonsLangAssignmentCond: no elegible (P5 — assignment in condition)")
        void commonsLangAssignmentCond() {
            ExperimentResult r = runCase("REAL_COMMONS_LANG_ASSIGNMENT_COND");
            assertFalse(r.isEligible());
            assertTrue(r.getDiscardCategories().contains("ASSIGNMENT_IN_CONDITION"),
                    "Debe incluir ASSIGNMENT_IN_CONDITION: " + r.getDiscardCategories());
        }

        @Test
        @DisplayName("RealCommonsMathNoNesting: no elegible (SINGLE_STATEMENT_NOT_IF — ifs secuenciales con return)")
        void commonsMathNoNesting() {
            ExperimentResult r = runCase("REAL_COMMONS_MATH_NO_NESTING");
            assertFalse(r.isEligible());
            assertTrue(r.getDiscardCategories().contains("SINGLE_STATEMENT_NOT_IF"),
                    "Debe incluir SINGLE_STATEMENT_NOT_IF: " + r.getDiscardCategories());
        }

        @Test
        @DisplayName("RealCommonsLangInnerMethodCall: no elegible (P5 — method call en condición interna)")
        void commonsLangInnerMethodCall() {
            ExperimentResult r = runCase("REAL_COMMONS_LANG_INNER_METHOD_CALL");
            assertFalse(r.isEligible());
            assertTrue(r.getDiscardCategories().contains("METHOD_CALL_IN_CONDITION"),
                    "Debe incluir METHOD_CALL_IN_CONDITION: " + r.getDiscardCategories());
        }

        @Test
        @DisplayName("RealCommonsLangDecrementCond: no elegible (P5 — decremento en condición)")
        void commonsLangDecrementCond() {
            ExperimentResult r = runCase("REAL_COMMONS_LANG_DECREMENT_COND");
            assertFalse(r.isEligible());
            assertTrue(r.getDiscardCategories().contains("INCREMENT_OR_DECREMENT_IN_CONDITION"),
                    "Debe incluir INCREMENT_OR_DECREMENT_IN_CONDITION: "
                            + r.getDiscardCategories());
        }

        // --- Estadísticas globales ---

        @Test
        @DisplayName("Al menos el 75% de los casos del corpus son elegibles")
        void eligibilityDistribution() {
            List<ExperimentResult> results = runner.run(realCases);
            long eligible = results.stream()
                    .filter(ExperimentResult::isEligible).count();
            long ineligible = results.stream()
                    .filter(r -> !r.isEligible()).count();
            assertEquals(realCases.size(), eligible + ineligible,
                    "Total debe coincidir con casos cargados");
            assertTrue(eligible >= realCases.size() * 0.75,
                    "Al menos el 75% deben ser elegibles, elegibles=" + eligible
                            + " de " + realCases.size());
        }

        @Test
        @DisplayName("Todos los elegibles tienen delta negativo (reducen CC)")
        void allEligibleReduceComplexity() {
            List<ExperimentResult> results = runner.run(realCases);
            for (ExperimentResult r : results) {
                if (r.isEligible()) {
                    assertTrue(r.getDelta() < 0,
                            r.getCaseId() + ": delta debe ser negativo, got "
                                    + r.getDelta());
                }
            }
        }

        @Test
        @DisplayName("Ningún no elegible cambia CC")
        void noIneligibleChangesComplexity() {
            List<ExperimentResult> results = runner.run(realCases);
            for (ExperimentResult r : results) {
                if (!r.isEligible()) {
                    assertEquals(0, r.getDelta(),
                            r.getCaseId() + ": delta debe ser 0 para no elegibles");
                }
            }
        }

        private ExperimentResult runCase(String caseId) {
            return realCases.stream()
                    .filter(c -> c.getCaseId().equals(caseId))
                    .findFirst()
                    .map(runner::processCase)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Caso no encontrado: " + caseId));
        }
    }
}
