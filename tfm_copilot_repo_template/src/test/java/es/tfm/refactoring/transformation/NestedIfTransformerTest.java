package es.tfm.refactoring.transformation;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.IfStmt;
import es.tfm.refactoring.detection.NestedIfDetector;
import es.tfm.refactoring.detection.RefactoringOpportunity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests para el transformador de condicionales anidados (MVP).
 * <p>
 * Estrategia: detectar con {@link NestedIfDetector}, transformar con
 * {@link NestedIfTransformer}, y verificar el AST resultante.
 */
class NestedIfTransformerTest {

    private NestedIfDetector detector;
    private NestedIfTransformer transformer;

    @BeforeEach
    void setUp() {
        detector = new NestedIfDetector();
        transformer = new NestedIfTransformer();
    }

    /**
     * Parsea código, extrae el primer método, detecta oportunidades y las transforma.
     * Devuelve el código fuente resultante del método (para verificación).
     */
    private String detectAndTransform(String methodBody) {
        String code = "class Test { void m(int a, int b, boolean c, boolean d, Object x) { "
                + methodBody + " } }";
        CompilationUnit cu = StaticJavaParser.parse(code);
        MethodDeclaration method = cu.findFirst(MethodDeclaration.class).orElseThrow();

        List<RefactoringOpportunity> opportunities = detector.detect(method);
        for (RefactoringOpportunity opp : opportunities) {
            assertTrue(transformer.apply(opp), "La transformación debería haberse aplicado");
        }

        return method.toString();
    }

    private MethodDeclaration parseMethod(String methodBody) {
        String code = "class Test { void m(int a, int b, boolean c, boolean d, Object x) { "
                + methodBody + " } }";
        CompilationUnit cu = StaticJavaParser.parse(code);
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    // =========================================================================
    // Transformaciones válidas
    // =========================================================================

    @Nested
    @DisplayName("Transformaciones válidas")
    class ValidTransformations {

        @Test
        @DisplayName("Caso básico: if(a>0) { if(b>0) { S } } → if(a>0 && b>0) { S }")
        void transformsBasicNestedIf() {
            String result = detectAndTransform(
                    "if (a > 0) { if (b > 0) { System.out.println(a); } }");

            // La condición resultante debe ser A && B
            assertTrue(result.contains("a > 0 && b > 0"), "Condición combinada esperada: " + result);
            // El cuerpo S se preserva
            assertTrue(result.contains("System.out.println(a)"), "Cuerpo S preservado: " + result);
            // No debe haber ifs anidados residuales (patrón if.*if dentro del bloque)
            assertFalse(result.contains("if (b > 0)"), "No debe quedar if anidado: " + result);
        }

        @Test
        @DisplayName("Condiciones booleanas simples: if(c) { if(d) { S } }")
        void transformsBooleanConditions() {
            String result = detectAndTransform(
                    "if (c) { if (d) { System.out.println(); } }");
            assertTrue(result.contains("c && d"), "Condición combinada: " + result);
        }

        @Test
        @DisplayName("Preserva orden de evaluación: A && B, no B && A")
        void preservesEvaluationOrder() {
            String result = detectAndTransform(
                    "if (a > 0) { if (b < 10) { System.out.println(); } }");

            // Verificar que A aparece antes que B en la condición
            int posA = result.indexOf("a > 0");
            int posB = result.indexOf("b < 10");
            assertTrue(posA < posB, "A debe evalarse antes que B: " + result);
            assertTrue(result.contains("a > 0 && b < 10"), "Orden A && B: " + result);
        }

        @Test
        @DisplayName("Preserva el cuerpo S intacto con múltiples sentencias")
        void preservesMultiStatementBody() {
            String result = detectAndTransform(
                    "if (a > 0) { if (b > 0) { int x = a + b; System.out.println(x); } }");
            assertTrue(result.contains("int x = a + b"), "Primera sentencia del cuerpo: " + result);
            assertTrue(result.contains("System.out.println(x)"), "Segunda sentencia del cuerpo: " + result);
        }

        @Test
        @DisplayName("Condición con OR se envuelve en paréntesis: if(a||c) { if(d) { S } }")
        void parenthesizesOrCondition() {
            String result = detectAndTransform(
                    "if (a > 0 || c) { if (d) { System.out.println(); } }");
            // a > 0 || c debe quedar entre paréntesis para preservar precedencia
            assertTrue(result.contains("(a > 0 || c) && d"), "OR debe tener paréntesis: " + result);
        }

        @Test
        @DisplayName("Condición interna con OR se envuelve en paréntesis")
        void parenthesizesInnerOrCondition() {
            String result = detectAndTransform(
                    "if (c) { if (a > 0 || d) { System.out.println(); } }");
            assertTrue(result.contains("c && (a > 0 || d)"), "OR interno con paréntesis: " + result);
        }

        @Test
        @DisplayName("Ambas condiciones con OR: (A||B) && (C||D)")
        void parenthesizesBothOrConditions() {
            String result = detectAndTransform(
                    "if (a > 0 || c) { if (b > 0 || d) { System.out.println(); } }");
            assertTrue(result.contains("(a > 0 || c) && (b > 0 || d)"),
                    "Ambos OR con paréntesis: " + result);
        }

        @Test
        @DisplayName("Condición con AND existente no añade paréntesis extra")
        void doesNotParenthesizeAndCondition() {
            String result = detectAndTransform(
                    "if (a > 0 && c) { if (d) { System.out.println(); } }");
            // a > 0 && c no necesita paréntesis extra porque && es asociativo
            assertTrue(result.contains("a > 0 && c && d"), "AND sin paréntesis extra: " + result);
        }

        @Test
        @DisplayName("If sin llaves: if(c) if(d) S;")
        void transformsWithoutBraces() {
            String result = detectAndTransform(
                    "if (c) if (d) System.out.println();");
            assertTrue(result.contains("c && d"), "Condición combinada: " + result);
            // El cuerpo debe quedar en bloque
            assertTrue(result.contains("System.out.println()"), "Cuerpo preservado: " + result);
        }

        @Test
        @DisplayName("Múltiples oportunidades independientes se transforman")
        void transformsMultipleOpportunities() {
            String result = detectAndTransform(
                    "if (a > 0) { if (b > 0) { System.out.println(1); } } "
                            + "if (c) { if (d) { System.out.println(2); } }");
            assertTrue(result.contains("a > 0 && b > 0"), "Primera transformación: " + result);
            assertTrue(result.contains("c && d"), "Segunda transformación: " + result);
        }
    }

    // =========================================================================
    // Resultado parseable
    // =========================================================================

    @Nested
    @DisplayName("Validez sintáctica del resultado")
    class SyntacticValidity {

        @Test
        @DisplayName("El resultado es parseable por JavaParser")
        void resultIsParseableByJavaParser() {
            String code = "class Test { void m(int a, int b) { "
                    + "if (a > 0) { if (b > 0) { System.out.println(a + b); } } } }";
            CompilationUnit cu = StaticJavaParser.parse(code);
            MethodDeclaration method = cu.findFirst(MethodDeclaration.class).orElseThrow();

            List<RefactoringOpportunity> opps = detector.detect(method);
            assertEquals(1, opps.size());
            assertTrue(transformer.apply(opps.get(0)));

            // Verificar que el CU completo se puede serializar y re-parsear
            String resultSource = cu.toString();
            assertDoesNotThrow(() -> StaticJavaParser.parse(resultSource),
                    "El resultado debe ser parseable: " + resultSource);
        }

        @Test
        @DisplayName("El resultado no contiene ifs anidados residuales del patrón")
        void noResidualNestedIfs() {
            String code = "class Test { void m(int a, int b) { "
                    + "if (a > 0) { if (b > 0) { System.out.println(); } } } }";
            CompilationUnit cu = StaticJavaParser.parse(code);
            MethodDeclaration method = cu.findFirst(MethodDeclaration.class).orElseThrow();

            List<RefactoringOpportunity> opps = detector.detect(method);
            for (RefactoringOpportunity opp : opps) {
                transformer.apply(opp);
            }

            // Re-detección sobre el resultado: no debe encontrar nuevas oportunidades del mismo tipo
            List<RefactoringOpportunity> remaining = detector.detect(method);
            assertTrue(remaining.isEmpty(),
                    "No deben quedar oportunidades residuales tras la transformación");
        }

        @Test
        @DisplayName("El resultado del if combinado no tiene rama else")
        void resultHasNoElse() {
            String code = "class Test { void m(boolean c, boolean d) { "
                    + "if (c) { if (d) { System.out.println(); } } } }";
            CompilationUnit cu = StaticJavaParser.parse(code);
            MethodDeclaration method = cu.findFirst(MethodDeclaration.class).orElseThrow();

            detector.detect(method).forEach(transformer::apply);

            IfStmt resultIf = method.findFirst(IfStmt.class).orElseThrow();
            assertFalse(resultIf.hasElseBranch(), "El if combinado no debe tener else");
        }
    }

    // =========================================================================
    // Casos de protección defensiva
    // =========================================================================

    @Nested
    @DisplayName("Protección defensiva")
    class DefensiveProtection {

        @Test
        @DisplayName("apply() retorna false si el outerIf tiene else (manipulación externa)")
        void returnsFalseIfOuterHasElse() {
            // Simular una oportunidad con else añadido después de la detección
            String code = "class Test { void m(int a, int b) { "
                    + "if (a > 0) { if (b > 0) { System.out.println(); } } } }";
            CompilationUnit cu = StaticJavaParser.parse(code);
            MethodDeclaration method = cu.findFirst(MethodDeclaration.class).orElseThrow();

            List<RefactoringOpportunity> opps = detector.detect(method);
            assertEquals(1, opps.size());

            // Manipular el AST después de la detección: añadir else al outer
            IfStmt outerIf = opps.get(0).getOuterIf();
            outerIf.setElseStmt(StaticJavaParser.parseStatement("{ System.out.println(\"else\"); }"));

            // El transformador debe rechazar
            assertFalse(transformer.apply(opps.get(0)),
                    "No debe transformar si el outerIf tiene else");
        }

        @Test
        @DisplayName("apply() retorna false si el innerIf tiene else (manipulación externa)")
        void returnsFalseIfInnerHasElse() {
            String code = "class Test { void m(int a, int b) { "
                    + "if (a > 0) { if (b > 0) { System.out.println(); } } } }";
            CompilationUnit cu = StaticJavaParser.parse(code);
            MethodDeclaration method = cu.findFirst(MethodDeclaration.class).orElseThrow();

            List<RefactoringOpportunity> opps = detector.detect(method);
            assertEquals(1, opps.size());

            // Manipular: añadir else al inner
            IfStmt innerIf = opps.get(0).getInnerIf();
            innerIf.setElseStmt(StaticJavaParser.parseStatement("{ System.out.println(\"else\"); }"));

            assertFalse(transformer.apply(opps.get(0)),
                    "No debe transformar si el innerIf tiene else");
        }
    }
}
