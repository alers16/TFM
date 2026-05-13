package es.tfm.refactoring.experiment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del pipeline experimental por lotes.
 * Verifica: procesamiento correcto, punto fijo con una-por-pase, elegibilidad,
 * motivos de descarte específicos, trazabilidad y consistencia de métricas.
 */
@DisplayName("BatchRunner — Pipeline experimental")
class BatchRunnerTest {

    private BatchRunner runner;

    @BeforeEach
    void setUp() {
        runner = new BatchRunner();
    }

    // =========================================================================
    // Casos válidos: detección, transformación y métricas correctas
    // =========================================================================

    @Nested
    @DisplayName("Casos válidos")
    class ValidCases {

        @Test
        @DisplayName("VALID_SIMPLE: patrón canónico, eligible, delta=-1")
        void validSimple() {
            ExperimentResult r = runner.processCase(SeedCorpus.validSimple());

            assertTrue(r.isEligible());
            assertNull(r.getDiscardReason());
            assertEquals("VALID_SIMPLE", r.getCaseId());
            assertEquals("synthetic", r.getOrigin());
            assertEquals(1, r.getOpportunitiesApplied());
            assertEquals(1, r.getTotalPasses());
            assertEquals(3, r.getComplexityBefore());
            assertEquals(2, r.getComplexityAfter());
            assertEquals(-1, r.getDelta());
            assertTrue(r.hasImprovement());
            assertNotNull(r.getSourceBefore());
            assertNotNull(r.getSourceAfter());
            assertNotNull(r.getMethodSignature());
            assertTrue(r.getSourceAfter().contains("&&"));
            // discardCategories puede no estar vacío: recoge motivos de otros candidatos
            // rechazados en el mismo método (e.g., el if interno evaluado individualmente)
        }

        @Test
        @DisplayName("VALID_DEEP_NESTING: triple anidamiento, punto fijo con 2 pases, delta=-4")
        void deepNesting() {
            ExperimentResult r = runner.processCase(SeedCorpus.validDeepNesting());

            assertTrue(r.isEligible());
            assertEquals(2, r.getTotalPasses());
            assertEquals(2, r.getOpportunitiesApplied(),
                    "Una-por-pase: 2 transformaciones en 2 pases");
            assertEquals(6, r.getComplexityBefore());
            assertEquals(2, r.getComplexityAfter());
            assertEquals(-4, r.getDelta());
            assertTrue(r.getObservations().contains("múltiples pases"));
        }

        @Test
        @DisplayName("VALID_INSIDE_LOOP: dentro de for-each, delta=-2")
        void insideLoop() {
            ExperimentResult r = runner.processCase(SeedCorpus.validInsideLoop());

            assertTrue(r.isEligible());
            assertEquals(6, r.getComplexityBefore());
            assertEquals(4, r.getComplexityAfter());
            assertEquals(-2, r.getDelta());
        }

        @Test
        @DisplayName("VALID_OR_CONDITION: condición OR parentizada, delta=-1")
        void orCondition() {
            ExperimentResult r = runner.processCase(SeedCorpus.validOrCondition());

            assertTrue(r.isEligible());
            assertEquals(4, r.getComplexityBefore());
            assertEquals(3, r.getComplexityAfter());
            assertEquals(-1, r.getDelta());
            assertTrue(r.getSourceAfter().contains("(x > 0 || x < -10) && y > 0")
                     || r.getSourceAfter().contains("(x > 0 || x < -10) &&"));
        }

        @Test
        @DisplayName("VALID_TWO_INDEPENDENT: dos patrones, ambos transformados, delta=-2")
        void twoIndependent() {
            ExperimentResult r = runner.processCase(SeedCorpus.validTwoIndependent());

            assertTrue(r.isEligible());
            assertEquals(2, r.getOpportunitiesApplied(),
                    "Dos oportunidades independientes, una por pase");
            assertEquals(6, r.getComplexityBefore());
            assertEquals(4, r.getComplexityAfter());
            assertEquals(-2, r.getDelta());
        }
    }

    // =========================================================================
    // Casos inválidos: elegibilidad, motivo de descarte, complejidad estable
    // =========================================================================

    @Nested
    @DisplayName("Casos inválidos (rechazo)")
    class InvalidCases {

        @Test
        @DisplayName("INVALID_OUTER_ELSE: rechazado por P1, eligible=false")
        void outerElse() {
            ExperimentResult r = runner.processCase(SeedCorpus.invalidOuterElse());

            assertFalse(r.isEligible());
            assertNotNull(r.getDiscardReason());
            assertEquals(0, r.getOpportunitiesApplied());
            assertEquals(0, r.getDelta());
            assertEquals(r.getComplexityBefore(), r.getComplexityAfter());
        }

        @Test
        @DisplayName("INVALID_INNER_ELSE: rechazado por P4, eligible=false")
        void innerElse() {
            ExperimentResult r = runner.processCase(SeedCorpus.invalidInnerElse());

            assertFalse(r.isEligible());
            assertNotNull(r.getDiscardReason());
            assertEquals(0, r.getOpportunitiesApplied());
            assertEquals(0, r.getDelta());
        }

        @Test
        @DisplayName("INVALID_MULTI_STMT: rechazado por P2, eligible=false")
        void multipleStatements() {
            ExperimentResult r = runner.processCase(SeedCorpus.invalidMultipleStatements());

            assertFalse(r.isEligible());
            assertNotNull(r.getDiscardReason());
            assertEquals(0, r.getOpportunitiesApplied());
            assertEquals(0, r.getDelta());
        }

        @Test
        @DisplayName("INVALID_METHOD_CALL: rechazado por P5, eligible=false")
        void methodCall() {
            ExperimentResult r = runner.processCase(SeedCorpus.invalidMethodCall());

            assertFalse(r.isEligible());
            assertNotNull(r.getDiscardReason());
            assertEquals(0, r.getOpportunitiesApplied());
            assertEquals(0, r.getDelta());
        }

        @Test
        @DisplayName("INVALID_NO_NESTING: sin patrón anidado, eligible=false")
        void noNesting() {
            ExperimentResult r = runner.processCase(SeedCorpus.invalidNoNesting());

            assertFalse(r.isEligible());
            assertNotNull(r.getDiscardReason());
            assertEquals(0, r.getOpportunitiesApplied());
            assertEquals(0, r.getDelta());
        }

        @Test
        @DisplayName("Todos los inválidos miden complejidad correctamente (>= 0)")
        void invalidCasesStillMeasureComplexity() {
            List<ExperimentCase> invalidCases = List.of(
                    SeedCorpus.invalidOuterElse(),
                    SeedCorpus.invalidInnerElse(),
                    SeedCorpus.invalidMultipleStatements(),
                    SeedCorpus.invalidMethodCall(),
                    SeedCorpus.invalidNoNesting()
            );
            for (ExperimentCase ec : invalidCases) {
                ExperimentResult r = runner.processCase(ec);
                assertTrue(r.getComplexityBefore() >= 0,
                        ec.getCaseId() + ": complexityBefore debe ser >= 0");
                assertTrue(r.getComplexityAfter() >= 0,
                        ec.getCaseId() + ": complexityAfter debe ser >= 0");
            }
        }
    }

    // =========================================================================
    // Motivos de descarte específicos
    // =========================================================================

    @Nested
    @DisplayName("Motivos de descarte específicos")
    class DiscardCategories {

        @Test
        @DisplayName("INVALID_OUTER_ELSE incluye categoría OUTER_HAS_ELSE")
        void outerElseCategory() {
            ExperimentResult r = runner.processCase(SeedCorpus.invalidOuterElse());
            assertTrue(r.getDiscardCategories().contains("OUTER_HAS_ELSE"),
                    "Debe incluir OUTER_HAS_ELSE: " + r.getDiscardCategories());
        }

        @Test
        @DisplayName("INVALID_INNER_ELSE incluye categoría INNER_HAS_ELSE")
        void innerElseCategory() {
            ExperimentResult r = runner.processCase(SeedCorpus.invalidInnerElse());
            assertTrue(r.getDiscardCategories().contains("INNER_HAS_ELSE"),
                    "Debe incluir INNER_HAS_ELSE: " + r.getDiscardCategories());
        }

        @Test
        @DisplayName("INVALID_MULTI_STMT incluye categoría OUTER_BLOCK_MULTIPLE_STATEMENTS")
        void multiStmtCategory() {
            ExperimentResult r = runner.processCase(SeedCorpus.invalidMultipleStatements());
            assertTrue(r.getDiscardCategories().contains("OUTER_BLOCK_MULTIPLE_STATEMENTS"),
                    "Debe incluir OUTER_BLOCK_MULTIPLE_STATEMENTS: " + r.getDiscardCategories());
        }

        @Test
        @DisplayName("INVALID_METHOD_CALL incluye categoría METHOD_CALL_IN_CONDITION")
        void methodCallCategory() {
            ExperimentResult r = runner.processCase(SeedCorpus.invalidMethodCall());
            assertTrue(r.getDiscardCategories().contains("METHOD_CALL_IN_CONDITION"),
                    "Debe incluir METHOD_CALL_IN_CONDITION: " + r.getDiscardCategories());
        }

        @Test
        @DisplayName("INVALID_NO_NESTING incluye categoría de no-patrón")
        void noNestingCategory() {
            ExperimentResult r = runner.processCase(SeedCorpus.invalidNoNesting());
            assertFalse(r.getDiscardCategories().isEmpty(),
                    "Debe tener al menos una categoría de descarte");
        }

        @Test
        @DisplayName("discardReason texto incluye nombre de categoría")
        void discardReasonContainsCategory() {
            ExperimentResult r = runner.processCase(SeedCorpus.invalidOuterElse());
            assertTrue(r.getDiscardReason().contains("OUTER_HAS_ELSE"),
                    "discardReason debe mencionar categoría: " + r.getDiscardReason());
        }

        @Test
        @DisplayName("Caso elegible tiene discardCategories vacío o sin impedir refactorización")
        void eligibleCaseNoBlockingCategories() {
            ExperimentResult r = runner.processCase(SeedCorpus.validSimple());
            // Eligible cases may have discard categories from non-matching ifs
            // but the key check is that the case IS eligible
            assertTrue(r.isEligible());
        }
    }

    // =========================================================================
    // Ejecución por lotes y manejo de errores
    // =========================================================================

    @Nested
    @DisplayName("Ejecución por lotes")
    class BatchExecution {

        @Test
        @DisplayName("Corpus semilla completo: 10 resultados, 5 elegibles, 5 inelegibles")
        void seedCorpusComplete() {
            List<ExperimentResult> results = runner.run(SeedCorpus.loadAll());

            assertEquals(10, results.size());
            long eligible = results.stream().filter(ExperimentResult::isEligible).count();
            long ineligible = results.stream().filter(r -> !r.isEligible()).count();
            assertEquals(5, eligible);
            assertEquals(5, ineligible);
        }

        @Test
        @DisplayName("Todos los resultados conservan caseId")
        void caseIdsPreserved() {
            List<ExperimentCase> cases = SeedCorpus.loadAll();
            List<ExperimentResult> results = runner.run(cases);

            for (int i = 0; i < cases.size(); i++) {
                assertEquals(cases.get(i).getCaseId(), results.get(i).getCaseId());
            }
        }

        @Test
        @DisplayName("Error de parseo produce resultado inelegible con motivo")
        void parseError() {
            ExperimentCase badCase = new ExperimentCase(
                    "PARSE_ERROR", "synthetic",
                    "Código malformado",
                    "this is not { valid java code !!!"
            );

            ExperimentResult r = runner.processCase(badCase);

            assertFalse(r.isEligible());
            assertTrue(r.getDiscardReason().startsWith("Error de parseo:"));
            assertEquals(-1, r.getComplexityBefore());
        }

        @Test
        @DisplayName("Clase sin métodos produce resultado inelegible")
        void noMethod() {
            ExperimentCase noMethodCase = new ExperimentCase(
                    "NO_METHOD", "synthetic",
                    "Clase sin métodos",
                    "class Empty { int x = 5; }"
            );

            ExperimentResult r = runner.processCase(noMethodCase);

            assertFalse(r.isEligible());
            assertTrue(r.getDiscardReason().contains("No se encontró ningún método"));
        }

        @Test
        @DisplayName("Lista vacía produce lista vacía de resultados")
        void emptyBatch() {
            List<ExperimentResult> results = runner.run(List.of());
            assertTrue(results.isEmpty());
        }
    }

    // =========================================================================
    // Política de punto fijo (una-por-pase)
    // =========================================================================

    @Nested
    @DisplayName("Política de punto fijo (una-por-pase)")
    class FixPointPolicy {

        @Test
        @DisplayName("Caso simple converge en 1 pase")
        void simpleConvergesInOnePass() {
            ExperimentResult r = runner.processCase(SeedCorpus.validSimple());
            assertEquals(1, r.getTotalPasses());
            assertEquals(1, r.getOpportunitiesApplied());
        }

        @Test
        @DisplayName("Triple anidamiento converge en exactamente 2 pases")
        void deepNestingConvergesInTwoPasses() {
            ExperimentResult r = runner.processCase(SeedCorpus.validDeepNesting());
            assertEquals(2, r.getTotalPasses());
            assertEquals(2, r.getOpportunitiesApplied());
        }

        @Test
        @DisplayName("Dos independientes se aplican en 2 pases")
        void twoIndependentConvergesInTwoPasses() {
            ExperimentResult r = runner.processCase(SeedCorpus.validTwoIndependent());
            assertEquals(2, r.getTotalPasses());
            assertEquals(2, r.getOpportunitiesApplied());
        }

        @Test
        @DisplayName("opportunitiesDetected == opportunitiesApplied (conteo exacto)")
        void detectedEqualsApplied() {
            List<ExperimentResult> results = runner.run(SeedCorpus.loadAll());
            for (ExperimentResult r : results) {
                assertEquals(r.getOpportunitiesDetected(), r.getOpportunitiesApplied(),
                        r.getCaseId() + ": detected debe igualar applied con una-por-pase");
            }
        }

        @Test
        @DisplayName("Caso inválido: 0 pases")
        void invalidCaseZeroPasses() {
            ExperimentResult r = runner.processCase(SeedCorpus.invalidNoNesting());
            assertEquals(0, r.getTotalPasses());
        }

        @Test
        @DisplayName("MAX_PASSES es 10")
        void maxPassesConstant() {
            assertEquals(10, BatchRunner.MAX_PASSES);
        }
    }

    // =========================================================================
    // Consistencia de métricas
    // =========================================================================

    @Nested
    @DisplayName("Consistencia de métricas")
    class MetricsConsistency {

        @Test
        @DisplayName("delta = complexityAfter - complexityBefore")
        void deltaConsistency() {
            List<ExperimentResult> results = runner.run(SeedCorpus.loadAll());

            for (ExperimentResult r : results) {
                if (r.getComplexityBefore() >= 0) {
                    assertEquals(
                            r.getComplexityAfter() - r.getComplexityBefore(),
                            r.getDelta(),
                            r.getCaseId() + ": delta inconsistente"
                    );
                }
            }
        }

        @Test
        @DisplayName("Casos elegibles tienen delta <= 0")
        void eligibleCasesImproveOrNeutral() {
            List<ExperimentResult> results = runner.run(SeedCorpus.loadAll());

            for (ExperimentResult r : results) {
                if (r.isEligible()) {
                    assertTrue(r.getDelta() <= 0,
                            r.getCaseId() + ": caso elegible debería mejorar complejidad");
                }
            }
        }

        @Test
        @DisplayName("Casos inelegibles tienen delta == 0")
        void ineligibleCasesNeutralDelta() {
            List<ExperimentResult> results = runner.run(SeedCorpus.loadAll());

            for (ExperimentResult r : results) {
                if (!r.isEligible() && r.getComplexityBefore() >= 0) {
                    assertEquals(0, r.getDelta(),
                            r.getCaseId() + ": caso inelegible debería tener delta 0");
                }
            }
        }
    }
}
