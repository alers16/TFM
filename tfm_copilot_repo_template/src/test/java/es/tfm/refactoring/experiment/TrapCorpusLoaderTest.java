package es.tfm.refactoring.experiment;

import es.tfm.refactoring.llm.LlmTrapSubset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del cargador de corpus trampa (casos sintéticos no elegibles).
 */
@DisplayName("TrapCorpusLoader — Carga y clasificación de casos trampa")
class TrapCorpusLoaderTest {

    private TrapCorpusLoader loader;

    @BeforeEach
    void setUp() {
        loader = new TrapCorpusLoader();
    }

    // =========================================================================
    // Definición del subset
    // =========================================================================

    @Nested
    @DisplayName("Definición de LlmTrapSubset")
    class SubsetDefinition {

        @Test
        @DisplayName("standardTrapFiles() devuelve 3 archivos")
        void standardFileCount() {
            assertEquals(3, TrapCorpusLoader.standardTrapFiles().size());
        }

        @Test
        @DisplayName("Los 3 case IDs esperados están definidos")
        void caseIdsPresent() {
            List<String> ids = LlmTrapSubset.caseIds();
            assertTrue(ids.contains("TRAP_P4_INNER_ELSE"));
            assertTrue(ids.contains("TRAP_P2_MULTI_STATEMENT"));
            assertTrue(ids.contains("TRAP_P5_ASSIGNMENT"));
        }
    }

    // =========================================================================
    // Carga de recursos
    // =========================================================================

    @Nested
    @DisplayName("Carga de recursos desde classpath")
    class Loading {

        @Test
        @DisplayName("Carga todos los archivos trampa estándar sin excepción")
        void loadAllTrapFiles() throws IOException {
            List<ExperimentCase> cases = loader.load(
                    TrapCorpusLoader.standardTrapFiles());
            assertEquals(3, cases.size());
        }

        @Test
        @DisplayName("Cada caso trampa tiene caseId, origin y source no vacíos")
        void casesHaveRequiredFields() throws IOException {
            List<ExperimentCase> cases = loader.load(
                    TrapCorpusLoader.standardTrapFiles());
            for (ExperimentCase ec : cases) {
                assertNotNull(ec.getCaseId(), "caseId no debe ser null");
                assertFalse(ec.getCaseId().isEmpty(), "caseId no debe ser vacío");
                assertEquals("synthetic-trap", ec.getOrigin(),
                        ec.getCaseId() + ": origin debe ser synthetic-trap");
                assertNotNull(ec.getSourceCode(), "sourceCode no debe ser null");
                assertTrue(ec.getSourceCode().length() > 10,
                        ec.getCaseId() + ": source demasiado corto");
            }
        }

        @Test
        @DisplayName("TrapP4InnerElse carga con caseId correcto")
        void trapP4InnerElseId() throws IOException {
            ExperimentCase ec = loader.loadFile("TrapP4InnerElse.java");
            assertEquals("TRAP_P4_INNER_ELSE", ec.getCaseId());
        }

        @Test
        @DisplayName("TrapP2MultiStatement carga con caseId correcto")
        void trapP2MultiStatementId() throws IOException {
            ExperimentCase ec = loader.loadFile("TrapP2MultiStatement.java");
            assertEquals("TRAP_P2_MULTI_STATEMENT", ec.getCaseId());
        }

        @Test
        @DisplayName("TrapP5Assignment carga con caseId correcto")
        void trapP5AssignmentId() throws IOException {
            ExperimentCase ec = loader.loadFile("TrapP5Assignment.java");
            assertEquals("TRAP_P5_ASSIGNMENT", ec.getCaseId());
        }

        @Test
        @DisplayName("Lanzar IOException si el archivo no existe")
        void throwsOnMissingFile() {
            assertThrows(IOException.class,
                    () -> loader.loadFile("DoesNotExist.java"));
        }
    }

    // =========================================================================
    // Clasificación como no elegibles
    // =========================================================================

    @Nested
    @DisplayName("Clasificación determinista como no elegibles")
    class EligibilityClassification {

        @Test
        @DisplayName("Los 3 casos trampa son clasificados como NO elegibles por BatchRunner")
        void allTrapCasesAreIneligible() throws IOException {
            List<ExperimentCase> cases = loader.load(
                    TrapCorpusLoader.standardTrapFiles());
            BatchRunner batchRunner = new BatchRunner();

            for (ExperimentCase ec : cases) {
                ExperimentResult result = batchRunner.run(List.of(ec)).get(0);
                assertFalse(result.isEligible(),
                        ec.getCaseId()
                        + ": debería ser inelegible pero BatchRunner dice isEligible=true");
            }
        }
    }
}
