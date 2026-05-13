package es.tfm.refactoring.analysis;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests del pipeline before/after de medición de impacto.
 * <p>
 * Verifica que el analizador orquesta correctamente
 * detección → medición → transformación → medición.
 */
class RefactoringImpactAnalyzerTest {

    private RefactoringImpactAnalyzer analyzer;

    @BeforeEach
    void setUp() {
        analyzer = new RefactoringImpactAnalyzer();
    }

    private MethodDeclaration parseMethod(String body) {
        String code = "class T { void m(int a, int b, boolean c, boolean d, int[] arr) { "
                + body + " } }";
        CompilationUnit cu = StaticJavaParser.parse(code);
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    // =========================================================================
    // Pipeline completo con patrón MVP
    // =========================================================================

    @Nested
    @DisplayName("Pipeline con patrón MVP")
    class MvpPattern {

        @Test
        @DisplayName("Patrón básico: detecta, transforma, mide before=3, after=2, delta=-1")
        void basicPattern() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { if (b > 0) { System.out.println(a); } }");
            RefactoringImpact impact = analyzer.analyze(method);

            assertEquals(3, impact.getComplexityBefore());
            assertEquals(2, impact.getComplexityAfter());
            assertEquals(-1, impact.getDelta());
            assertEquals(1, impact.getOpportunitiesDetected());
            assertEquals(1, impact.getOpportunitiesApplied());
            assertTrue(impact.hasImprovement());
        }

        @Test
        @DisplayName("Patrón dentro de for: before=6, after=4, delta=-2")
        void patternInsideFor() {
            MethodDeclaration method = parseMethod(
                    "for (int x : arr) { if (a > 0) { if (b > 0) { System.out.println(); } } }");
            RefactoringImpact impact = analyzer.analyze(method);

            assertEquals(6, impact.getComplexityBefore());
            assertEquals(4, impact.getComplexityAfter());
            assertEquals(-2, impact.getDelta());
            assertTrue(impact.hasImprovement());
        }

        @Test
        @DisplayName("Dos patrones independientes: ambos detectados y transformados")
        void twoIndependentPatterns() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { if (b > 0) { System.out.println(1); } } "
                            + "if (c) { if (d) { System.out.println(2); } }");
            RefactoringImpact impact = analyzer.analyze(method);

            assertEquals(2, impact.getOpportunitiesDetected());
            assertEquals(2, impact.getOpportunitiesApplied());
            assertTrue(impact.hasImprovement());
            assertTrue(impact.getDelta() < 0);
        }

        @Test
        @DisplayName("El método original no se modifica tras analyze()")
        void originalMethodPreserved() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { if (b > 0) { System.out.println(); } }");
            String originalSource = method.toString();

            analyzer.analyze(method);

            assertEquals(originalSource, method.toString(),
                    "El método original no debe modificarse");
        }
    }

    // =========================================================================
    // Sin patrón: delta = 0
    // =========================================================================

    @Nested
    @DisplayName("Métodos sin patrón aplicable")
    class NoPattern {

        @Test
        @DisplayName("Método sin if anidado → delta=0, 0 oportunidades")
        void noNestedIf() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { System.out.println(1); }");
            RefactoringImpact impact = analyzer.analyze(method);

            assertEquals(1, impact.getComplexityBefore());
            assertEquals(1, impact.getComplexityAfter());
            assertEquals(0, impact.getDelta());
            assertEquals(0, impact.getOpportunitiesDetected());
            assertEquals(0, impact.getOpportunitiesApplied());
            assertFalse(impact.hasImprovement());
        }

        @Test
        @DisplayName("Método vacío → before=0, after=0, delta=0")
        void emptyMethod() {
            MethodDeclaration method = parseMethod("");
            RefactoringImpact impact = analyzer.analyze(method);

            assertEquals(0, impact.getComplexityBefore());
            assertEquals(0, impact.getComplexityAfter());
            assertEquals(0, impact.getDelta());
        }

        @Test
        @DisplayName("if con else (rechazado por detector) → delta=0")
        void ifWithElse() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { if (b > 0) { System.out.println(1); } } "
                            + "else { System.out.println(2); }");
            RefactoringImpact impact = analyzer.analyze(method);

            assertEquals(0, impact.getOpportunitiesDetected());
            assertEquals(0, impact.getDelta());
        }
    }

    // =========================================================================
    // Metadatos y trazabilidad
    // =========================================================================

    @Nested
    @DisplayName("Metadatos del resultado")
    class Metadata {

        @Test
        @DisplayName("Contiene firma del método")
        void containsMethodSignature() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { if (b > 0) { System.out.println(); } }");
            RefactoringImpact impact = analyzer.analyze(method);

            assertNotNull(impact.getMethodSignature());
            assertFalse(impact.getMethodSignature().isEmpty());
        }

        @Test
        @DisplayName("Contiene código fuente transformado")
        void containsTransformedSource() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { if (b > 0) { System.out.println(); } }");
            RefactoringImpact impact = analyzer.analyze(method);

            assertNotNull(impact.getSourceAfter());
            // El código transformado debe contener la condición combinada
            assertTrue(impact.getSourceAfter().contains("&&"),
                    "El código after debe contener && : " + impact.getSourceAfter());
        }

        @Test
        @DisplayName("toString() incluye campos clave")
        void toStringContainsFields() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { if (b > 0) { System.out.println(); } }");
            RefactoringImpact impact = analyzer.analyze(method);
            String str = impact.toString();

            assertTrue(str.contains("before=3"));
            assertTrue(str.contains("after=2"));
            assertTrue(str.contains("delta=-1"));
        }
    }

    // =========================================================================
    // Conveniencia: analyze(String)
    // =========================================================================

    @Nested
    @DisplayName("Método de conveniencia analyze(String)")
    class StringOverload {

        @Test
        @DisplayName("Parsea y analiza clase completa")
        void analyzesFullClass() {
            RefactoringImpact impact = analyzer.analyze(
                    "class T { void m(int a, int b) { "
                            + "if (a > 0) { if (b > 0) { System.out.println(); } } } }");
            assertNotNull(impact);
            assertEquals(3, impact.getComplexityBefore());
            assertEquals(2, impact.getComplexityAfter());
        }

        @Test
        @DisplayName("Clase sin método → null")
        void noMethod() {
            assertNull(analyzer.analyze("class T { int x = 5; }"));
        }
    }
}
