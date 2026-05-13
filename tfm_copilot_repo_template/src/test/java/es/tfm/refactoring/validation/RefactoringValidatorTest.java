package es.tfm.refactoring.validation;

import es.tfm.refactoring.detection.DetectionMode;
import es.tfm.refactoring.experiment.ExperimentCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de {@link RefactoringValidator}.
 * <p>
 * Verifica que el validador compile correctamente el output del transformador
 * y reporte SKIPPED cuando el caso no es elegible.
 */
@DisplayName("RefactoringValidator — compilación dinámica")
class RefactoringValidatorTest {

    private static final RefactoringValidator STRICT_VALIDATOR =
            new RefactoringValidator(DetectionMode.STRICT);
    private static final RefactoringValidator RELAXED_VALIDATOR =
            new RefactoringValidator(DetectionMode.RELAXED);

    private ExperimentCase caseOf(String caseId, String source) {
        return new ExperimentCase(caseId, "test", "test case", source);
    }

    // =========================================================================
    // Casos elegibles: la transformación se aplica y debe compilar
    // =========================================================================

    @Nested
    @DisplayName("Casos elegibles: PASS esperado")
    class EligibleCases {

        @Test
        @DisplayName("if anidado simple con condiciones puras → PASS en STRICT")
        void simpleNestedIfCompilesStrict() {
            ExperimentCase ec = caseOf("SIMPLE", """
                    class SimpleCase {
                        void check(int a, int b) {
                            if (a > 0) {
                                if (b > 0) {
                                    System.out.println("both positive");
                                }
                            }
                        }
                    }
                    """);

            ValidationResult result = STRICT_VALIDATOR.validate(ec);
            assertEquals(ValidationResult.Status.PASS, result.getStatus(),
                    "Caso elegible simple debe compilar: " + result.getDiagnostics());
        }

        @Test
        @DisplayName("if anidado con condiciones de String puras → PASS en RELAXED")
        void nestedIfWithStringMethodCompilesRelaxed() {
            ExperimentCase ec = caseOf("STRING_METHODS", """
                    class StringCase {
                        void check(String s, String prefix) {
                            if (s.isEmpty()) {
                                if (s.equals(prefix)) {
                                    System.out.println("match");
                                }
                            }
                        }
                    }
                    """);

            ValidationResult result = RELAXED_VALIDATOR.validate(ec);
            assertEquals(ValidationResult.Status.PASS, result.getStatus(),
                    "Caso con métodos del allowlist debe compilar en RELAXED: "
                    + result.getDiagnostics());
        }

        @Test
        @DisplayName("if anidado con múltiples sentencias en cuerpo interno → PASS")
        void multipleStatementsInBodyCompiles() {
            ExperimentCase ec = caseOf("MULTI_BODY", """
                    class MultiBody {
                        void check(int a, int b) {
                            if (a > 0) {
                                if (b > 0) {
                                    int x = a + b;
                                    System.out.println(x);
                                }
                            }
                        }
                    }
                    """);

            ValidationResult result = STRICT_VALIDATOR.validate(ec);
            assertEquals(ValidationResult.Status.PASS, result.getStatus(),
                    "Caso con cuerpo interno multi-statement debe compilar: "
                    + result.getDiagnostics());
        }

        @Test
        @DisplayName("Transformación preserva tipo de retorno (método no-void) → PASS")
        void nonVoidMethodCompiles() {
            ExperimentCase ec = caseOf("NON_VOID", """
                    class NonVoid {
                        int compute(int a, int b) {
                            if (a > 0) {
                                if (b > 0) {
                                    return a * b;
                                }
                            }
                            return 0;
                        }
                    }
                    """);

            ValidationResult result = STRICT_VALIDATOR.validate(ec);
            assertEquals(ValidationResult.Status.PASS, result.getStatus(),
                    "Método con retorno debe compilar tras transformación: "
                    + result.getDiagnostics());
        }

        @Test
        @DisplayName("Transformación en RELAXED compila también con getter en condición")
        void getterConditionCompilesRelaxed() {
            // process() debe ser el primer método para que findFirst lo capture.
            ExperimentCase ec = caseOf("GETTER_COND", """
                    class GetterCase {
                        void process(GetterCase obj) {
                            if (obj.isActive()) {
                                if (obj.getValue() > 0) {
                                    System.out.println("ok");
                                }
                            }
                        }
                        boolean isActive() { return true; }
                        int getValue() { return 42; }
                    }
                    """);

            ValidationResult result = RELAXED_VALIDATOR.validate(ec);
            assertEquals(ValidationResult.Status.PASS, result.getStatus(),
                    "Caso con getters en condición debe compilar en RELAXED: "
                    + result.getDiagnostics());
        }
    }

    // =========================================================================
    // Casos no elegibles: SKIPPED esperado
    // =========================================================================

    @Nested
    @DisplayName("Casos no elegibles: SKIPPED esperado")
    class IneligibleCases {

        @Test
        @DisplayName("if con else externo → SKIPPED en STRICT y RELAXED")
        void outerElseSkipped() {
            ExperimentCase ec = caseOf("OUTER_ELSE", """
                    class OuterElse {
                        void check(int a, int b) {
                            if (a > 0) {
                                if (b > 0) {
                                    System.out.println("both");
                                }
                            } else {
                                System.out.println("a not positive");
                            }
                        }
                    }
                    """);

            assertEquals(ValidationResult.Status.SKIPPED,
                    STRICT_VALIDATOR.validate(ec).getStatus(),
                    "STRICT: if con else externo debe ser SKIPPED");
            assertEquals(ValidationResult.Status.SKIPPED,
                    RELAXED_VALIDATOR.validate(ec).getStatus(),
                    "RELAXED: if con else externo debe ser SKIPPED");
        }

        @Test
        @DisplayName("Llamada a método fuera del allowlist → SKIPPED en STRICT, SKIPPED en RELAXED")
        void nonAllowlistMethodSkipped() {
            ExperimentCase ec = caseOf("NON_ALLOWLIST", """
                    class NonAllowlist {
                        void check(Service s) {
                            if (s.process()) {
                                if (s.validate()) {
                                    System.out.println("ok");
                                }
                            }
                        }
                    }
                    class Service {
                        boolean process() { return true; }
                        boolean validate() { return true; }
                    }
                    """);

            assertEquals(ValidationResult.Status.SKIPPED,
                    STRICT_VALIDATOR.validate(ec).getStatus(),
                    "STRICT: método fuera de allowlist debe ser SKIPPED");
            assertEquals(ValidationResult.Status.SKIPPED,
                    RELAXED_VALIDATOR.validate(ec).getStatus(),
                    "RELAXED: método fuera de allowlist debe ser SKIPPED");
        }

        @Test
        @DisplayName("Bloque externo con múltiples sentencias → SKIPPED")
        void multipleStatementsOuterSkipped() {
            ExperimentCase ec = caseOf("MULTI_OUTER", """
                    class MultiOuter {
                        void check(int a, int b) {
                            if (a > 0) {
                                System.out.println("a ok");
                                if (b > 0) {
                                    System.out.println("b ok");
                                }
                            }
                        }
                    }
                    """);

            assertEquals(ValidationResult.Status.SKIPPED,
                    STRICT_VALIDATOR.validate(ec).getStatus());
        }
    }

    // =========================================================================
    // ValidationResult factories
    // =========================================================================

    @Nested
    @DisplayName("ValidationResult — factories y estados")
    class ValidationResultTest {

        @Test
        void passFactory() {
            ValidationResult r = ValidationResult.pass("X");
            assertEquals(ValidationResult.Status.PASS, r.getStatus());
            assertTrue(r.isPassed());
            assertFalse(r.isFailed());
            assertFalse(r.isSkipped());
            assertFalse(r.isError());
        }

        @Test
        void failFactory() {
            ValidationResult r = ValidationResult.fail("X", "error msg");
            assertEquals(ValidationResult.Status.FAIL, r.getStatus());
            assertEquals("error msg", r.getDiagnostics());
            assertTrue(r.isFailed());
        }

        @Test
        void skippedFactory() {
            ValidationResult r = ValidationResult.skipped("X");
            assertEquals(ValidationResult.Status.SKIPPED, r.getStatus());
            assertTrue(r.isSkipped());
        }

        @Test
        void errorFactory() {
            ValidationResult r = ValidationResult.error("X", "infra error");
            assertEquals(ValidationResult.Status.ERROR, r.getStatus());
            assertTrue(r.isError());
        }
    }
}
