package es.tfm.refactoring.detection;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del modo {@link DetectionMode#STRUCTURAL} del detector.
 * <p>
 * STRUCTURAL aplica únicamente las precondiciones estructurales P1–P4 y omite
 * por completo P5 (efectos colaterales en las condiciones). La justificación de
 * corrección es la semántica de cortocircuito de {@code &&}: la transformación
 * es un unfold que preserva orden y condicionalidad de evaluación con
 * independencia de los efectos colaterales.
 * <p>
 * Estos tests verifican:
 * <ul>
 *   <li>(a) condiciones con efectos colaterales (P5) ahora ELEGIBLES;</li>
 *   <li>(b) los descartes estructurales P1–P4 siguen rechazándose;</li>
 *   <li>(c) STRICT y RELAXED conservan su comportamiento previo (no regresión).</li>
 * </ul>
 */
@DisplayName("NestedIfDetector — DetectionMode.STRUCTURAL (solo P1–P4, sin P5)")
class StructuralDetectionModeTest {

    private static final NestedIfDetector STRUCTURAL =
            new NestedIfDetector(DetectionMode.STRUCTURAL);
    private static final NestedIfDetector STRICT =
            new NestedIfDetector(DetectionMode.STRICT);
    private static final NestedIfDetector RELAXED =
            new NestedIfDetector(DetectionMode.RELAXED);

    private MethodDeclaration parseMethod(String code) {
        CompilationUnit cu = StaticJavaParser.parse(code);
        return cu.findFirst(MethodDeclaration.class)
                .orElseThrow(() -> new AssertionError("No method found"));
    }

    // =========================================================================
    // (a) P5 omitida: condiciones con efectos colaterales ahora ELEGIBLES
    // =========================================================================

    @Nested
    @DisplayName("(a) P5 omitida — efectos colaterales aceptados en STRUCTURAL")
    class SideEffectsAccepted {

        @Test
        @DisplayName("Llamada a método fuera del allowlist → ELEGIBLE en STRUCTURAL, rechazada en STRICT")
        void methodCallAcceptedInStructural() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(Service svc) {" +
                    "  if (svc.process()) {" +       // no está en el allowlist
                    "    if (svc.validate()) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            // STRICT descarta por METHOD_CALL_IN_CONDITION
            List<DetectionResult> strict = STRICT.detectWithReasons(m);
            assertTrue(strict.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(DiscardReason.METHOD_CALL_IN_CONDITION)),
                    "STRICT debe descartar por METHOD_CALL_IN_CONDITION");

            // STRUCTURAL acepta: P5 se omite por completo
            assertEquals(1, STRUCTURAL.detect(m).size(),
                    "STRUCTURAL debe detectar 1 oportunidad pese a las llamadas a método");
            List<DetectionResult> structural = STRUCTURAL.detectWithReasons(m);
            assertTrue(structural.stream().anyMatch(DetectionResult::isAccepted),
                    "STRUCTURAL debe aceptar condiciones con llamadas a método (P5 omitida)");
        }

        @Test
        @DisplayName("Asignación en condición → ELEGIBLE en STRUCTURAL (P5 omitida)")
        void assignmentAcceptedInStructural() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int[] arr) {" +
                    "  int x;" +
                    "  if ((x = arr[0]) > 0) {" +
                    "    if (x < 10) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            assertEquals(1, STRUCTURAL.detect(m).size(),
                    "STRUCTURAL debe aceptar asignaciones en condición (P5 omitida)");
            List<DetectionResult> structural = STRUCTURAL.detectWithReasons(m);
            assertTrue(structural.stream().anyMatch(DetectionResult::isAccepted),
                    "STRUCTURAL no debe reportar ASSIGNMENT_IN_CONDITION");
            assertTrue(structural.stream()
                    .noneMatch(r -> r.getDiscardReasons()
                            .contains(DiscardReason.ASSIGNMENT_IN_CONDITION)),
                    "STRUCTURAL no debe emitir el motivo ASSIGNMENT_IN_CONDITION");
        }

        @Test
        @DisplayName("Operador ++ en condición → ELEGIBLE en STRUCTURAL (P5 omitida)")
        void incrementAcceptedInStructural() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int i, int n) {" +
                    "  if (i++ < n) {" +
                    "    if (i < 100) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            assertEquals(1, STRUCTURAL.detect(m).size(),
                    "STRUCTURAL debe aceptar ++/-- en condición (P5 omitida)");
        }

        @Test
        @DisplayName("Instanciación con new en condición → ELEGIBLE en STRUCTURAL (P5 omitida)")
        void objectCreationAcceptedInStructural() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(String s) {" +
                    "  if (new java.io.File(s).exists()) {" +
                    "    if (s.length() > 0) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            assertEquals(1, STRUCTURAL.detect(m).size(),
                    "STRUCTURAL debe aceptar new en condición (P5 omitida)");
        }
    }

    // =========================================================================
    // (b) P1–P4 estructurales: siguen descartándose en STRUCTURAL
    // =========================================================================

    @Nested
    @DisplayName("(b) P1–P4 estructurales — siguen rechazándose en STRUCTURAL")
    class StructuralRejections {

        @Test
        @DisplayName("else externo (P1) → rechazado en STRUCTURAL")
        void outerElseRejected() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(Service svc) {" +
                    "  if (svc.process()) {" +
                    "    if (svc.validate()) { doSomething(); }" +
                    "  } else { doOther(); }" +
                    "} void doSomething() {} void doOther() {} }");

            assertEquals(0, STRUCTURAL.detect(m).size(),
                    "STRUCTURAL debe rechazar else externo (P1)");
            List<DetectionResult> structural = STRUCTURAL.detectWithReasons(m);
            assertTrue(structural.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(DiscardReason.OUTER_HAS_ELSE)),
                    "STRUCTURAL debe reportar OUTER_HAS_ELSE");
        }

        @Test
        @DisplayName("else interno (P4) → rechazado en STRUCTURAL, incluso else vacío")
        void innerElseRejected() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(Service svc) {" +
                    "  if (svc.process()) {" +
                    "    if (svc.validate()) { doSomething(); } else { doOther(); }" +
                    "  }" +
                    "} void doSomething() {} void doOther() {} }");

            assertEquals(0, STRUCTURAL.detect(m).size(),
                    "STRUCTURAL debe rechazar else interno (P4)");
            List<DetectionResult> structural = STRUCTURAL.detectWithReasons(m);
            assertTrue(structural.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(DiscardReason.INNER_HAS_ELSE)),
                    "STRUCTURAL debe reportar INNER_HAS_ELSE");
        }

        @Test
        @DisplayName("else interno vacío → rechazado en STRUCTURAL (P4' es solo de RELAXED)")
        void emptyInnerElseRejected() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(Service svc) {" +
                    "  if (svc.process()) {" +
                    "    if (svc.validate()) { doSomething(); } else {}" +
                    "  }" +
                    "} void doSomething() {} }");

            assertEquals(0, STRUCTURAL.detect(m).size(),
                    "STRUCTURAL debe rechazar else interno vacío (no aplica P4')");
            // RELAXED sí lo aceptaría (P4'): contraste de comportamiento
            assertEquals(1, RELAXED.detect(m).size(),
                    "RELAXED debe aceptar else interno vacío (P4')");
        }

        @Test
        @DisplayName("bloque externo con varias sentencias (P2) → rechazado en STRUCTURAL")
        void outerBlockMultipleStatementsRejected() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(Service svc) {" +
                    "  if (svc.process()) {" +
                    "    log();" +
                    "    if (svc.validate()) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} void log() {} }");

            assertEquals(0, STRUCTURAL.detect(m).size(),
                    "STRUCTURAL debe rechazar bloque externo con varias sentencias (P2)");
            List<DetectionResult> structural = STRUCTURAL.detectWithReasons(m);
            assertTrue(structural.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons()
                                .contains(DiscardReason.OUTER_BLOCK_MULTIPLE_STATEMENTS)),
                    "STRUCTURAL debe reportar OUTER_BLOCK_MULTIPLE_STATEMENTS");
        }

        @Test
        @DisplayName("sentencia única no-if (P3) → rechazado en STRUCTURAL")
        void singleStatementNotIfRejected() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(Service svc) {" +
                    "  if (svc.process()) {" +
                    "    doSomething();" +
                    "  }" +
                    "} void doSomething() {} }");

            assertEquals(0, STRUCTURAL.detect(m).size(),
                    "STRUCTURAL debe rechazar sentencia única no-if (P3)");
            List<DetectionResult> structural = STRUCTURAL.detectWithReasons(m);
            assertTrue(structural.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons()
                                .contains(DiscardReason.SINGLE_STATEMENT_NOT_IF)),
                    "STRUCTURAL debe reportar SINGLE_STATEMENT_NOT_IF");
        }
    }

    // =========================================================================
    // (c) No regresión: STRICT y RELAXED conservan su comportamiento
    // =========================================================================

    @Nested
    @DisplayName("(c) No regresión — STRICT y RELAXED sin cambios")
    class NoRegression {

        @Test
        @DisplayName("STRICT sigue descartando llamadas a método por P5")
        void strictStillRejectsMethodCalls() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(Service svc) {" +
                    "  if (svc.process()) {" +
                    "    if (svc.validate()) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            assertEquals(0, STRICT.detect(m).size(),
                    "STRICT debe seguir rechazando llamadas a método (P5 intacta)");
        }

        @Test
        @DisplayName("RELAXED sigue rechazando métodos fuera del allowlist sin null-guard")
        void relaxedStillRejectsNonAllowlist() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(Service svc, boolean flag) {" +
                    "  if (flag) {" +
                    "    if (svc.process()) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            assertEquals(0, RELAXED.detect(m).size(),
                    "RELAXED debe seguir rechazando process() sin allowlist ni null-guard");
        }

        @Test
        @DisplayName("Condiciones puras → aceptadas idénticamente en los tres modos")
        void pureConditionsAllModes() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int a, int b) {" +
                    "  if (a > 0) {" +
                    "    if (b > 0) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            assertEquals(1, STRICT.detect(m).size(), "STRICT: 1 oportunidad");
            assertEquals(1, RELAXED.detect(m).size(), "RELAXED: 1 oportunidad");
            assertEquals(1, STRUCTURAL.detect(m).size(), "STRUCTURAL: 1 oportunidad");
        }

        @Test
        @DisplayName("else externo con condiciones puras → rechazado en los tres modos (P1)")
        void pureOuterElseAllModes() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int a, int b) {" +
                    "  if (a > 0) {" +
                    "    if (b > 0) { doSomething(); }" +
                    "  } else { doOther(); }" +
                    "} void doSomething() {} void doOther() {} }");

            assertEquals(0, STRICT.detect(m).size(), "STRICT: 0 (P1)");
            assertEquals(0, RELAXED.detect(m).size(), "RELAXED: 0 (P1)");
            assertEquals(0, STRUCTURAL.detect(m).size(), "STRUCTURAL: 0 (P1)");
        }
    }
}
