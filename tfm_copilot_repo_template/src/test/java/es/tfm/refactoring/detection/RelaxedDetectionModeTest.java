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
 * Tests del modo {@link DetectionMode#RELAXED} del detector.
 * <p>
 * Verifica que la precondición P5' acepte condiciones con llamadas a métodos
 * del allowlist ({@link MethodCallAllowlist}) y rechace las que no aparecen.
 * P1–P4 se comportan igual que en modo STRICT.
 */
@DisplayName("NestedIfDetector — DetectionMode.RELAXED (P5')")
class RelaxedDetectionModeTest {

    private static final NestedIfDetector RELAXED =
            new NestedIfDetector(DetectionMode.RELAXED);
    private static final NestedIfDetector STRICT =
            new NestedIfDetector(DetectionMode.STRICT);

    private MethodDeclaration parseMethod(String code) {
        CompilationUnit cu = StaticJavaParser.parse(code);
        return cu.findFirst(MethodDeclaration.class)
                .orElseThrow(() -> new AssertionError("No method found"));
    }

    // =========================================================================
    // Casos que STRICT rechaza y RELAXED acepta
    // =========================================================================

    @Nested
    @DisplayName("Allowlist: aceptados en RELAXED, rechazados en STRICT")
    class AllowlistAccepted {

        @Test
        @DisplayName("isEmpty() en condición externa → aceptado en RELAXED")
        void isEmptyAccepted() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(java.util.List<String> list) {" +
                    "  if (list.isEmpty()) {" +
                    "    if (list.size() > 0) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            // STRICT rechaza por METHOD_CALL_IN_CONDITION
            List<DetectionResult> strictResults = STRICT.detectWithReasons(m);
            assertTrue(strictResults.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(DiscardReason.METHOD_CALL_IN_CONDITION)),
                    "STRICT debe rechazar por METHOD_CALL_IN_CONDITION");

            // RELAXED acepta: isEmpty() y size() están en el allowlist
            List<DetectionResult> relaxedResults = RELAXED.detectWithReasons(m);
            assertTrue(relaxedResults.stream().anyMatch(DetectionResult::isAccepted),
                    "RELAXED debe aceptar cuando solo hay llamadas del allowlist");
        }

        @Test
        @DisplayName("getter convencional (getX) en condición → aceptado en RELAXED")
        void getterAccepted() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(Object obj) {" +
                    "  if (obj.getStatus() != null) {" +
                    "    if (obj.getValue() > 0) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            List<DetectionResult> strictResults = STRICT.detectWithReasons(m);
            assertTrue(strictResults.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(DiscardReason.METHOD_CALL_IN_CONDITION)),
                    "STRICT debe rechazar por METHOD_CALL_IN_CONDITION");

            List<DetectionResult> relaxedResults = RELAXED.detectWithReasons(m);
            assertTrue(relaxedResults.stream().anyMatch(DetectionResult::isAccepted),
                    "RELAXED debe aceptar getters convencionales");
        }

        @Test
        @DisplayName("isPresent() en condición → aceptado en RELAXED")
        void isPresentAccepted() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(java.util.Optional<String> opt) {" +
                    "  if (opt.isPresent()) {" +
                    "    if (opt.isPresent()) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            List<DetectionResult> relaxedResults = RELAXED.detectWithReasons(m);
            assertTrue(relaxedResults.stream().anyMatch(DetectionResult::isAccepted),
                    "RELAXED debe aceptar isPresent()");
        }

        @Test
        @DisplayName("equals() en condición → aceptado en RELAXED")
        void equalsAccepted() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(String a, String b) {" +
                    "  if (a.equals(b)) {" +
                    "    if (a.startsWith(\"x\")) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            List<DetectionResult> relaxedResults = RELAXED.detectWithReasons(m);
            assertTrue(relaxedResults.stream().anyMatch(DetectionResult::isAccepted),
                    "RELAXED debe aceptar equals() y startsWith()");
        }
    }

    // =========================================================================
    // Casos que RELAXED también debe rechazar
    // =========================================================================

    @Nested
    @DisplayName("Siempre rechazados: fuera del allowlist o P1–P4 violadas")
    class AlwaysRejected {

        @Test
        @DisplayName("Método fuera del allowlist → rechazado también en RELAXED")
        void nonAllowlistMethodRejected() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(Service svc) {" +
                    "  if (svc.process()) {" +      // process() no está en el allowlist
                    "    if (svc.validate()) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            List<DetectionResult> relaxedResults = RELAXED.detectWithReasons(m);
            assertTrue(relaxedResults.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(DiscardReason.METHOD_CALL_IN_CONDITION)),
                    "RELAXED debe rechazar métodos fuera del allowlist");
        }

        @Test
        @DisplayName("Asignación en condición → rechazado en RELAXED (P5 no relajada para asignaciones)")
        void assignmentRejectedInRelaxed() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int[] arr) {" +
                    "  int x;" +
                    "  if ((x = arr[0]) > 0) {" +
                    "    if (x < 10) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            List<DetectionResult> relaxedResults = RELAXED.detectWithReasons(m);
            assertTrue(relaxedResults.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(DiscardReason.ASSIGNMENT_IN_CONDITION)),
                    "RELAXED debe rechazar asignaciones en condición");
        }

        @Test
        @DisplayName("else externo → rechazado en RELAXED (P1 intacta)")
        void outerElseRejectedInRelaxed() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(String s) {" +
                    "  if (s.isEmpty()) {" +
                    "    if (s.length() == 0) { doSomething(); }" +
                    "  } else {" +
                    "    doOther();" +
                    "  }" +
                    "} void doSomething() {} void doOther() {} }");

            List<DetectionResult> relaxedResults = RELAXED.detectWithReasons(m);
            assertTrue(relaxedResults.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(DiscardReason.OUTER_HAS_ELSE)),
                    "RELAXED debe seguir rechazando else externo (P1 intacta)");
        }

        @Test
        @DisplayName("else interno no vacío → rechazado en RELAXED (P4 intacta para else con cuerpo)")
        void innerElseRejectedInRelaxed() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(String s) {" +
                    "  if (s.isEmpty()) {" +
                    "    if (s.length() == 0) { doSomething(); }" +
                    "    else { doOther(); }" +
                    "  }" +
                    "} void doSomething() {} void doOther() {} }");

            List<DetectionResult> relaxedResults = RELAXED.detectWithReasons(m);
            assertTrue(relaxedResults.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(DiscardReason.INNER_HAS_ELSE)),
                    "RELAXED debe seguir rechazando else interno con cuerpo (P4 intacta)");
        }
    }

    // =========================================================================
    // P4' — else vacío en if interno aceptado en RELAXED
    // =========================================================================

    @Nested
    @DisplayName("P4' — else vacío en if interno (RELAXED acepta, STRICT rechaza)")
    class EmptyElseRelaxation {

        @Test
        @DisplayName("else {} vacío en if interno → aceptado en RELAXED")
        void emptyInnerElseAcceptedInRelaxed() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int a, int b) {" +
                    "  if (a > 0) {" +
                    "    if (b > 0) { doSomething(); } else {}" +
                    "  }" +
                    "} void doSomething() {} }");

            List<DetectionResult> relaxedResults = RELAXED.detectWithReasons(m);
            assertTrue(relaxedResults.stream().anyMatch(DetectionResult::isAccepted),
                    "RELAXED debe aceptar if interno con else vacío (P4')");
        }

        @Test
        @DisplayName("else {} vacío en if interno → rechazado en STRICT")
        void emptyInnerElseRejectedInStrict() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int a, int b) {" +
                    "  if (a > 0) {" +
                    "    if (b > 0) { doSomething(); } else {}" +
                    "  }" +
                    "} void doSomething() {} }");

            List<DetectionResult> strictResults = STRICT.detectWithReasons(m);
            assertTrue(strictResults.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(DiscardReason.INNER_HAS_ELSE)),
                    "STRICT debe rechazar else vacío igual que cualquier otro else (P4 sin relajar)");
        }

        @Test
        @DisplayName("else { stmt; } no vacío → rechazado en RELAXED (P4' no aplica)")
        void nonEmptyInnerElseStillRejectedInRelaxed() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int a, int b) {" +
                    "  if (a > 0) {" +
                    "    if (b > 0) { doSomething(); } else { doOther(); }" +
                    "  }" +
                    "} void doSomething() {} void doOther() {} }");

            List<DetectionResult> relaxedResults = RELAXED.detectWithReasons(m);
            assertTrue(relaxedResults.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(DiscardReason.INNER_HAS_ELSE)),
                    "RELAXED debe seguir rechazando else con sentencias");
        }
    }

    // =========================================================================
    // P5''' — patrón null-guard: outer 'x != null', inner 'x.method()'
    // =========================================================================

    @Nested
    @DisplayName("P5''' — patrón null-guard (outer: x != null, inner: x.method())")
    class NullGuardRelaxation {

        @Test
        @DisplayName("null-guard clásico: outer 'x != null', inner 'x.process()' → aceptado en RELAXED")
        void nullGuardAcceptedInRelaxed() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(Service x) {" +
                    "  if (x != null) {" +
                    "    if (x.process()) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            List<DetectionResult> relaxedResults = RELAXED.detectWithReasons(m);
            assertTrue(relaxedResults.stream().anyMatch(DetectionResult::isAccepted),
                    "RELAXED debe aceptar null-guard (x != null / x.method()) aunque el método no esté en el allowlist");
        }

        @Test
        @DisplayName("null-guard invertido: outer 'null != x', inner 'x.validate()' → aceptado en RELAXED")
        void nullGuardInvertedAcceptedInRelaxed() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(Service x) {" +
                    "  if (null != x) {" +
                    "    if (x.validate()) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            List<DetectionResult> relaxedResults = RELAXED.detectWithReasons(m);
            assertTrue(relaxedResults.stream().anyMatch(DetectionResult::isAccepted),
                    "RELAXED debe aceptar null-guard en forma 'null != x'");
        }

        @Test
        @DisplayName("null-guard: outer 'x != null', inner 'x.process()' → rechazado en STRICT")
        void nullGuardRejectedInStrict() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(Service x) {" +
                    "  if (x != null) {" +
                    "    if (x.process()) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            List<DetectionResult> strictResults = STRICT.detectWithReasons(m);
            assertTrue(strictResults.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(DiscardReason.METHOD_CALL_IN_CONDITION)),
                    "STRICT debe rechazar null-guard: cualquier llamada a método es descarte");
        }

        @Test
        @DisplayName("outer no es null-check → inner 'x.process()' rechazado en RELAXED (no null-guard)")
        void nonNullGuardOuterRejectedInRelaxed() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(Service x, boolean flag) {" +
                    "  if (flag) {" +
                    "    if (x.process()) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            List<DetectionResult> relaxedResults = RELAXED.detectWithReasons(m);
            assertTrue(relaxedResults.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(DiscardReason.METHOD_CALL_IN_CONDITION)),
                    "RELAXED debe rechazar si la condición externa no es null-check y el método no está en el allowlist");
        }

        @Test
        @DisplayName("null-guard: inner llama a método de DIFERENTE variable → rechazado en RELAXED")
        void nullGuardDifferentVarRejectedInRelaxed() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(Service x, Service y) {" +
                    "  if (x != null) {" +
                    "    if (y.process()) { doSomething(); }" +  // y, not x
                    "  }" +
                    "} void doSomething() {} }");

            List<DetectionResult> relaxedResults = RELAXED.detectWithReasons(m);
            assertTrue(relaxedResults.stream()
                    .anyMatch(r -> !r.isAccepted()
                            && r.getDiscardReasons().contains(DiscardReason.METHOD_CALL_IN_CONDITION)),
                    "RELAXED debe rechazar si la llamada es sobre una variable distinta a la null-guardada");
        }
    }

    // =========================================================================
    // Compatibilidad: casos STRICT que siguen pasando en RELAXED
    // =========================================================================

    @Nested
    @DisplayName("Compatibilidad: casos sin llamadas a métodos (idénticos en ambos modos)")
    class StrictCompatibility {

        @Test
        @DisplayName("Condiciones puras (sin llamadas) → aceptado en STRICT y RELAXED")
        void pureConditionsBothModes() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int a, int b) {" +
                    "  if (a > 0) {" +
                    "    if (b > 0) { doSomething(); }" +
                    "  }" +
                    "} void doSomething() {} }");

            assertTrue(STRICT.detect(m).size() == 1,
                    "STRICT debe detectar 1 oportunidad");
            assertTrue(RELAXED.detect(m).size() == 1,
                    "RELAXED debe detectar 1 oportunidad");
        }

        @Test
        @DisplayName("Condiciones puras rechazadas por P1 → rechazado en STRICT y RELAXED")
        void pureElseBothModes() {
            MethodDeclaration m = parseMethod(
                    "class X { void f(int a, int b) {" +
                    "  if (a > 0) {" +
                    "    if (b > 0) { doSomething(); }" +
                    "  } else { doOther(); }" +
                    "} void doSomething() {} void doOther() {} }");

            assertEquals(0, STRICT.detect(m).size(),
                    "STRICT debe detectar 0 oportunidades");
            assertEquals(0, RELAXED.detect(m).size(),
                    "RELAXED debe detectar 0 oportunidades");
        }
    }

    // =========================================================================
    // MethodCallAllowlist unitaria
    // =========================================================================

    @Nested
    @DisplayName("MethodCallAllowlist.isPresumablyPure()")
    class AllowlistUnit {

        @Test
        @DisplayName("Métodos del allowlist devuelven true")
        void allowlistMethodsReturnTrue() {
            assertTrue(MethodCallAllowlist.isPresumablyPure("isEmpty"));
            assertTrue(MethodCallAllowlist.isPresumablyPure("size"));
            assertTrue(MethodCallAllowlist.isPresumablyPure("contains"));
            assertTrue(MethodCallAllowlist.isPresumablyPure("length"));
            assertTrue(MethodCallAllowlist.isPresumablyPure("isPresent"));
            assertTrue(MethodCallAllowlist.isPresumablyPure("equals"));
            assertTrue(MethodCallAllowlist.isPresumablyPure("startsWith"));
            assertTrue(MethodCallAllowlist.isPresumablyPure("endsWith"));
        }

        @Test
        @DisplayName("Getters convencionales devuelven true")
        void getterPrefixesReturnTrue() {
            assertTrue(MethodCallAllowlist.isPresumablyPure("getName"));
            assertTrue(MethodCallAllowlist.isPresumablyPure("getStatus"));
            assertTrue(MethodCallAllowlist.isPresumablyPure("getValue"));
            assertTrue(MethodCallAllowlist.isPresumablyPure("isActive"));
            assertTrue(MethodCallAllowlist.isPresumablyPure("hasNext"));
            assertTrue(MethodCallAllowlist.isPresumablyPure("hasChildren"));
        }

        @Test
        @DisplayName("Métodos fuera del allowlist devuelven false")
        void nonAllowlistReturnFalse() {
            assertFalse(MethodCallAllowlist.isPresumablyPure("process"));
            assertFalse(MethodCallAllowlist.isPresumablyPure("execute"));
            assertFalse(MethodCallAllowlist.isPresumablyPure("save"));
            assertFalse(MethodCallAllowlist.isPresumablyPure("delete"));
            assertFalse(MethodCallAllowlist.isPresumablyPure("update"));
            assertFalse(MethodCallAllowlist.isPresumablyPure("get")); // prefijo solo, sin sufijo
            assertFalse(MethodCallAllowlist.isPresumablyPure("is"));  // prefijo solo, sin sufijo
        }
    }
}
