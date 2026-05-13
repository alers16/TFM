package es.tfm.refactoring.detection;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de detectWithReasons — detección con motivos de descarte específicos.
 */
@DisplayName("NestedIfDetector — detectWithReasons")
class DetectWithReasonsTest {

    private NestedIfDetector detector;

    @BeforeEach
    void setUp() {
        detector = new NestedIfDetector();
    }

    private MethodDeclaration parseMethod(String code) {
        CompilationUnit cu = StaticJavaParser.parse(code);
        return cu.findFirst(MethodDeclaration.class)
                .orElseThrow(() -> new AssertionError("No method found"));
    }

    // =========================================================================
    // Motivos de descarte individuales
    // =========================================================================

    @Nested
    @DisplayName("Motivos de descarte específicos")
    class SpecificReasons {

        @Test
        @DisplayName("if con else externo → OUTER_HAS_ELSE")
        void outerElse() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int a) { if (a > 0) { if (a < 10) { } } else { } } }");
            List<DetectionResult> results = detector.detectWithReasons(m);

            assertFalse(results.isEmpty());
            DetectionResult first = results.get(0);
            assertFalse(first.isAccepted());
            assertTrue(first.getDiscardReasons().contains(DiscardReason.OUTER_HAS_ELSE));
        }

        @Test
        @DisplayName("if con else interno → INNER_HAS_ELSE")
        void innerElse() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int a) { if (a > 0) { if (a < 10) { } else { } } } }");
            List<DetectionResult> results = detector.detectWithReasons(m);

            // The outer if produces a result: it passes P1 but inner has else → INNER_HAS_ELSE
            boolean hasInnerElse = results.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(DiscardReason.INNER_HAS_ELSE));
            assertTrue(hasInnerElse, "Debe detectar INNER_HAS_ELSE");
        }

        @Test
        @DisplayName("Bloque con múltiples sentencias → OUTER_BLOCK_MULTIPLE_STATEMENTS")
        void multipleStatements() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int a, int b) { if (a > 0) { int x = 1; if (b > 0) { } } } }");
            List<DetectionResult> results = detector.detectWithReasons(m);

            boolean hasMulti = results.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(
                                    DiscardReason.OUTER_BLOCK_MULTIPLE_STATEMENTS));
            assertTrue(hasMulti, "Debe detectar OUTER_BLOCK_MULTIPLE_STATEMENTS");
        }

        @Test
        @DisplayName("Sentencia única no-if → SINGLE_STATEMENT_NOT_IF")
        void singleNonIf() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int a) { if (a > 0) { System.out.println(a); } } }");
            List<DetectionResult> results = detector.detectWithReasons(m);

            boolean hasSingleNotIf = results.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(
                                    DiscardReason.SINGLE_STATEMENT_NOT_IF));
            assertTrue(hasSingleNotIf, "Debe detectar SINGLE_STATEMENT_NOT_IF");
        }

        @Test
        @DisplayName("Llamada a método en condición → METHOD_CALL_IN_CONDITION")
        void methodCallInCondition() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(String s) { if (s.isEmpty()) { if (s.length() > 0) { } } } }");
            List<DetectionResult> results = detector.detectWithReasons(m);

            boolean hasMethodCall = results.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(
                                    DiscardReason.METHOD_CALL_IN_CONDITION));
            assertTrue(hasMethodCall, "Debe detectar METHOD_CALL_IN_CONDITION");
        }

        @Test
        @DisplayName("Asignación en condición → ASSIGNMENT_IN_CONDITION")
        void assignmentInCondition() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int a, int b) { if ((a = 5) > 0) { if (b > 0) { } } } }");
            List<DetectionResult> results = detector.detectWithReasons(m);

            boolean hasAssign = results.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(
                                    DiscardReason.ASSIGNMENT_IN_CONDITION));
            assertTrue(hasAssign, "Debe detectar ASSIGNMENT_IN_CONDITION");
        }

        @Test
        @DisplayName("Incremento en condición → INCREMENT_OR_DECREMENT_IN_CONDITION")
        void incrementInCondition() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int a, int b) { if (++a > 0) { if (b > 0) { } } } }");
            List<DetectionResult> results = detector.detectWithReasons(m);

            boolean hasIncr = results.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(
                                    DiscardReason.INCREMENT_OR_DECREMENT_IN_CONDITION));
            assertTrue(hasIncr, "Debe detectar INCREMENT_OR_DECREMENT_IN_CONDITION");
        }
    }

    // =========================================================================
    // Casos aceptados
    // =========================================================================

    @Nested
    @DisplayName("Casos aceptados")
    class AcceptedCases {

        @Test
        @DisplayName("Patrón canónico if-if produce resultado aceptado")
        void canonicalPattern() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int a, int b) { if (a > 0) { if (b > 0) { System.out.println(1); } } } }");
            List<DetectionResult> results = detector.detectWithReasons(m);

            // Should have at least one accepted result
            boolean hasAccepted = results.stream().anyMatch(DetectionResult::isAccepted);
            assertTrue(hasAccepted, "Patrón canónico debe ser aceptado");
        }

        @Test
        @DisplayName("Resultado aceptado contiene oportunidad válida")
        void acceptedHasOpportunity() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int a, int b) { if (a > 0) { if (b > 0) { } } } }");
            List<DetectionResult> results = detector.detectWithReasons(m);

            DetectionResult accepted = results.stream()
                    .filter(DetectionResult::isAccepted)
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("No accepted result"));
            assertNotNull(accepted.getOpportunity());
            assertTrue(accepted.getDiscardReasons().isEmpty());
        }
    }

    // =========================================================================
    // Coherencia con detect()
    // =========================================================================

    @Nested
    @DisplayName("Coherencia detect() ↔ detectWithReasons()")
    class Coherence {

        @Test
        @DisplayName("Número de aceptados coincide con detect().size()")
        void acceptedCountMatchesDetect() {
            String code = "class X { void f(int a, int b, int c) { "
                    + "if (a > 0) { if (b > 0) { } } "
                    + "if (c > 0) { if (a > 0) { } } "
                    + "} }";
            MethodDeclaration m = parseMethod(code);

            List<RefactoringOpportunity> opps = detector.detect(m);
            List<DetectionResult> results = detector.detectWithReasons(m);
            long accepted = results.stream().filter(DetectionResult::isAccepted).count();

            assertEquals(opps.size(), accepted,
                    "Número de aceptados debe coincidir con detect()");
        }

        @Test
        @DisplayName("Método vacío produce resultados vacíos en ambos")
        void emptyMethodConsistency() {
            MethodDeclaration m = parseMethod(
                    "class X { void f() { } }");

            assertTrue(detector.detect(m).isEmpty());
            assertTrue(detector.detectWithReasons(m).isEmpty());
        }

        @Test
        @DisplayName("Método sin cuerpo produce resultados vacíos en ambos")
        void abstractMethodConsistency() {
            CompilationUnit cu = StaticJavaParser.parse(
                    "abstract class X { abstract void f(); }");
            MethodDeclaration m = cu.findFirst(MethodDeclaration.class)
                    .orElseThrow();

            assertTrue(detector.detect(m).isEmpty());
            assertTrue(detector.detectWithReasons(m).isEmpty());
        }
    }

    /**
     * AssertionError alias used for inline orElseThrow lambdas.
     */
    private static class AssertionError extends RuntimeException {
        AssertionError(String msg) { super(msg); }
    }
}
