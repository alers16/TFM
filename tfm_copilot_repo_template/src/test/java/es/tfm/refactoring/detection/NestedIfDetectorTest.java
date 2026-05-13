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
 * Tests para el detector de condicionales anidados (MVP).
 * <p>
 * Organización:
 * <ul>
 *   <li>Casos válidos: patrones que deben detectarse.</li>
 *   <li>Casos rechazados por estructura: else, múltiples sentencias, etc.</li>
 *   <li>Casos rechazados por side effects: llamadas a métodos, asignaciones, etc.</li>
 *   <li>Casos sin patrón: código que no contiene el patrón buscado.</li>
 * </ul>
 */
class NestedIfDetectorTest {

    private NestedIfDetector detector;

    @BeforeEach
    void setUp() {
        detector = new NestedIfDetector();
    }

    /**
     * Parsea un cuerpo de método dentro de una clase ficticia y devuelve el MethodDeclaration.
     */
    private MethodDeclaration parseMethod(String methodBody) {
        String code = "class Test { void m(int a, int b, boolean c, boolean d, Object x) { "
                + methodBody + " } }";
        CompilationUnit cu = StaticJavaParser.parse(code);
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    // =========================================================================
    // Casos válidos — deben detectarse
    // =========================================================================

    @Nested
    @DisplayName("Casos válidos")
    class ValidCases {

        @Test
        @DisplayName("Caso básico: if (A) { if (B) { S } }")
        void detectsBasicNestedIf() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { if (b > 0) { System.out.println(a); } }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertEquals(1, results.size());
            assertEquals("a > 0", results.get(0).getOuterCondition());
            assertEquals("b > 0", results.get(0).getInnerCondition());
        }

        @Test
        @DisplayName("Condiciones booleanas simples: if (c) { if (d) { S } }")
        void detectsBooleanConditions() {
            MethodDeclaration method = parseMethod(
                    "if (c) { if (d) { System.out.println(); } }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertEquals(1, results.size());
        }

        @Test
        @DisplayName("Condiciones con operadores lógicos: if (a > 0 && c) { if (b < 10) { S } }")
        void detectsCompoundConditions() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0 && c) { if (b < 10) { System.out.println(); } }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertEquals(1, results.size());
        }

        @Test
        @DisplayName("Condición con instanceof")
        void detectsInstanceof() {
            MethodDeclaration method = parseMethod(
                    "if (x instanceof String) { if (c) { System.out.println(); } }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertEquals(1, results.size());
        }

        @Test
        @DisplayName("Múltiples oportunidades independientes en el mismo método")
        void detectsMultipleOpportunities() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { if (b > 0) { System.out.println(1); } } "
                            + "if (c) { if (d) { System.out.println(2); } }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertEquals(2, results.size());
        }

        @Test
        @DisplayName("Patrón anidado a tres niveles genera dos oportunidades")
        void detectsTripleNesting() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { if (b > 0) { if (c) { System.out.println(); } } }");
            List<RefactoringOpportunity> results = detector.detect(method);
            // if(a) -> if(b) es oportunidad, if(b) -> if(c) es oportunidad
            assertEquals(2, results.size());
        }

        @Test
        @DisplayName("If anidado sin llaves: if (c) if (d) S;")
        void detectsWithoutBraces() {
            MethodDeclaration method = parseMethod(
                    "if (c) if (d) System.out.println();");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertEquals(1, results.size());
        }
    }

    // =========================================================================
    // Casos rechazados por estructura
    // =========================================================================

    @Nested
    @DisplayName("Rechazados por estructura")
    class RejectedByStructure {

        @Test
        @DisplayName("If externo con else → rechazado")
        void rejectsOuterIfWithElse() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { if (b > 0) { System.out.println(1); } } else { System.out.println(2); }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("If interno con else → rechazado")
        void rejectsInnerIfWithElse() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { if (b > 0) { System.out.println(1); } else { System.out.println(2); } }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("If externo con else-if → rechazado")
        void rejectsOuterIfWithElseIf() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { if (b > 0) { System.out.println(1); } } else if (c) { System.out.println(2); }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Bloque externo con múltiples sentencias → rechazado")
        void rejectsMultipleStatementsInOuterBlock() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { System.out.println(0); if (b > 0) { System.out.println(1); } }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Bloque externo con sentencia posterior al if interno → rechazado")
        void rejectsStatementAfterInnerIf() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { if (b > 0) { System.out.println(1); } System.out.println(2); }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Única sentencia en bloque externo no es if → rechazado")
        void rejectsSingleNonIfStatement() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { System.out.println(1); }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertTrue(results.isEmpty());
        }
    }

    // =========================================================================
    // Casos rechazados por side effects en condiciones
    // =========================================================================

    @Nested
    @DisplayName("Rechazados por side effects")
    class RejectedBySideEffects {

        @Test
        @DisplayName("Condición exterior con llamada a método → rechazado")
        void rejectsOuterConditionWithMethodCall() {
            MethodDeclaration method = parseMethod(
                    "if (x.equals(\"test\")) { if (c) { System.out.println(); } }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Condición interior con llamada a método → rechazado")
        void rejectsInnerConditionWithMethodCall() {
            MethodDeclaration method = parseMethod(
                    "if (c) { if (x.equals(\"test\")) { System.out.println(); } }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Condición con asignación → rechazado")
        void rejectsConditionWithAssignment() {
            MethodDeclaration method = parseMethod(
                    "if ((a = b) > 0) { if (c) { System.out.println(); } }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Condición con incremento prefijo → rechazado")
        void rejectsConditionWithPrefixIncrement() {
            MethodDeclaration method = parseMethod(
                    "if (++a > 0) { if (c) { System.out.println(); } }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Condición con decremento postfijo → rechazado")
        void rejectsConditionWithPostfixDecrement() {
            MethodDeclaration method = parseMethod(
                    "if (a-- > 0) { if (c) { System.out.println(); } }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Condición con instanciación new → rechazado")
        void rejectsConditionWithNewObject() {
            MethodDeclaration method = parseMethod(
                    "if (new Object() != null) { if (c) { System.out.println(); } }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertTrue(results.isEmpty());
        }
    }

    // =========================================================================
    // Casos sin patrón — no hay if anidado
    // =========================================================================

    @Nested
    @DisplayName("Sin patrón")
    class NoPattern {

        @Test
        @DisplayName("Método sin if → lista vacía")
        void noIfStatements() {
            MethodDeclaration method = parseMethod("System.out.println(1);");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("If simple sin anidamiento → lista vacía")
        void singleIfNoNesting() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { System.out.println(1); }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Dos ifs secuenciales (no anidados) → lista vacía")
        void sequentialIfs() {
            MethodDeclaration method = parseMethod(
                    "if (a > 0) { System.out.println(1); } if (b > 0) { System.out.println(2); }");
            List<RefactoringOpportunity> results = detector.detect(method);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Método vacío → lista vacía")
        void emptyMethod() {
            String code = "class Test { void m() {} }";
            CompilationUnit cu = StaticJavaParser.parse(code);
            MethodDeclaration method = cu.findFirst(MethodDeclaration.class).orElseThrow();
            List<RefactoringOpportunity> results = detector.detect(method);
            assertTrue(results.isEmpty());
        }

        @Test
        @DisplayName("Método abstracto (sin body) → lista vacía")
        void abstractMethod() {
            String code = "abstract class Test { abstract void m(); }";
            CompilationUnit cu = StaticJavaParser.parse(code);
            MethodDeclaration method = cu.findFirst(MethodDeclaration.class).orElseThrow();
            List<RefactoringOpportunity> results = detector.detect(method);
            assertTrue(results.isEmpty());
        }
    }
}
