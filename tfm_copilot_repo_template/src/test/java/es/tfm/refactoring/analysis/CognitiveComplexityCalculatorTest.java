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
 * Tests para la estimación provisional de complejidad cognitiva.
 * <p>
 * Los valores esperados siguen las reglas del modelo SonarSource:
 * incremento estructural (+1), incremento de anidamiento (+nivel),
 * secuencias de operadores lógicos (+1 por grupo).
 */
class CognitiveComplexityCalculatorTest {

    private CognitiveComplexityCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new CognitiveComplexityCalculator();
    }

    private MethodDeclaration parseMethod(String body) {
        String code = "class T { void m(int a, int b, boolean c, boolean d, int[] arr) { "
                + body + " } }";
        CompilationUnit cu = StaticJavaParser.parse(code);
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    // =========================================================================
    // Casos base
    // =========================================================================

    @Nested
    @DisplayName("Casos base")
    class BaseCases {

        @Test
        @DisplayName("Método vacío → 0")
        void emptyMethod() {
            assertEquals(0, calculator.calculate(parseMethod("")));
        }

        @Test
        @DisplayName("Método sin control de flujo → 0")
        void noControlFlow() {
            assertEquals(0, calculator.calculate(parseMethod(
                    "int x = a + b; System.out.println(x);")));
        }

        @Test
        @DisplayName("Método abstracto → 0")
        void abstractMethod() {
            String code = "abstract class T { abstract void m(); }";
            CompilationUnit cu = StaticJavaParser.parse(code);
            MethodDeclaration method = cu.findFirst(MethodDeclaration.class).orElseThrow();
            assertEquals(0, calculator.calculate(method));
        }
    }

    // =========================================================================
    // if / else if / else
    // =========================================================================

    @Nested
    @DisplayName("if / else if / else")
    class IfStatements {

        @Test
        @DisplayName("if simple → 1")
        void singleIf() {
            // if: +1 structural + 0 nesting = 1
            assertEquals(1, calculator.calculate(parseMethod(
                    "if (a > 0) { System.out.println(a); }")));
        }

        @Test
        @DisplayName("if-else → 2")
        void ifElse() {
            // if: +1, else: +1 = 2
            assertEquals(2, calculator.calculate(parseMethod(
                    "if (a > 0) { System.out.println(1); } else { System.out.println(2); }")));
        }

        @Test
        @DisplayName("if / else-if / else → 3")
        void ifElseIfElse() {
            // if: +1, else-if: +1, else: +1 = 3
            assertEquals(3, calculator.calculate(parseMethod(
                    "if (a > 0) { System.out.println(1); } "
                            + "else if (a < 0) { System.out.println(2); } "
                            + "else { System.out.println(3); }")));
        }

        @Test
        @DisplayName("Nested if (nuestro patrón MVP) → 3")
        void nestedIf() {
            // if(a): +1+0=1, if(b): +1+1=2 → total 3
            assertEquals(3, calculator.calculate(parseMethod(
                    "if (a > 0) { if (b > 0) { System.out.println(a); } }")));
        }

        @Test
        @DisplayName("if combinado con && (resultado de nuestra transformación) → 2")
        void combinedIfWithAnd() {
            // if: +1+0=1, &&: +1 → total 2
            assertEquals(2, calculator.calculate(parseMethod(
                    "if (a > 0 && b > 0) { System.out.println(a); }")));
        }

        @Test
        @DisplayName("Triple nesting → 6")
        void tripleNesting() {
            // if(a): +1+0=1, if(b): +1+1=2, if(c): +1+2=3 → total 6
            assertEquals(6, calculator.calculate(parseMethod(
                    "if (a > 0) { if (b > 0) { if (c) { System.out.println(); } } }")));
        }

        @Test
        @DisplayName("Nested if inside else → correcto anidamiento")
        void nestedIfInsideElse() {
            // if(a): +1+0=1, else: +1, if(b) inside else at nesting 1: +1+1=2 → total 4
            assertEquals(4, calculator.calculate(parseMethod(
                    "if (a > 0) { System.out.println(1); } "
                            + "else { if (b > 0) { System.out.println(2); } }")));
        }
    }

    // =========================================================================
    // Operadores lógicos
    // =========================================================================

    @Nested
    @DisplayName("Operadores lógicos")
    class LogicalOperators {

        @Test
        @DisplayName("if con && simple → 2 (if:1 + &&:1)")
        void singleAnd() {
            assertEquals(2, calculator.calculate(parseMethod(
                    "if (a > 0 && b > 0) { System.out.println(); }")));
        }

        @Test
        @DisplayName("if con && && → 2 (una sola secuencia de &&)")
        void chainedAnd() {
            // if: +1, one && sequence: +1 → 2
            assertEquals(2, calculator.calculate(parseMethod(
                    "if (a > 0 && b > 0 && c) { System.out.println(); }")));
        }

        @Test
        @DisplayName("if con && || → 3 (dos secuencias distintas)")
        void mixedAndOr() {
            // if: +1, &&: +1, ||: +1 → 3
            assertEquals(3, calculator.calculate(parseMethod(
                    "if (a > 0 && b > 0 || c) { System.out.println(); }")));
        }

        @Test
        @DisplayName("if con || simple → 2")
        void singleOr() {
            assertEquals(2, calculator.calculate(parseMethod(
                    "if (a > 0 || b > 0) { System.out.println(); }")));
        }

        @Test
        @DisplayName("if con || && || (lectura plana) → 4")
        void threeSequences() {
            // a > 0 || b > 0 && c || d se parsea como OR(OR(a>0, AND(b>0,c)), d)
            // Recorrido en inorden: ||, &&, || → 3 secuencias distintas
            // if: +1, ||: +1, &&: +1, ||: +1 → 4
            assertEquals(4, calculator.calculate(parseMethod(
                    "if (a > 0 || b > 0 && c || d) { System.out.println(); }")));
        }
    }

    // =========================================================================
    // Bucles y switch
    // =========================================================================

    @Nested
    @DisplayName("Bucles y switch")
    class LoopsAndSwitch {

        @Test
        @DisplayName("for simple → 1")
        void simpleFor() {
            assertEquals(1, calculator.calculate(parseMethod(
                    "for (int i = 0; i < 10; i++) { System.out.println(i); }")));
        }

        @Test
        @DisplayName("for-each simple → 1")
        void simpleForEach() {
            assertEquals(1, calculator.calculate(parseMethod(
                    "for (int x : arr) { System.out.println(x); }")));
        }

        @Test
        @DisplayName("while simple → 1")
        void simpleWhile() {
            assertEquals(1, calculator.calculate(parseMethod(
                    "while (a > 0) { a--; }")));
        }

        @Test
        @DisplayName("do-while simple → 1")
        void simpleDoWhile() {
            assertEquals(1, calculator.calculate(parseMethod(
                    "do { a--; } while (a > 0);")));
        }

        @Test
        @DisplayName("switch simple → 1")
        void simpleSwitch() {
            assertEquals(1, calculator.calculate(parseMethod(
                    "switch (a) { case 1: System.out.println(1); break; "
                            + "case 2: System.out.println(2); break; }")));
        }

        @Test
        @DisplayName("if dentro de for-each → 3 (for:1, if:1+1)")
        void ifInsideForEach() {
            assertEquals(3, calculator.calculate(parseMethod(
                    "for (int x : arr) { if (x > 0) { System.out.println(x); } }")));
        }
    }

    // =========================================================================
    // try-catch
    // =========================================================================

    @Nested
    @DisplayName("try-catch")
    class TryCatch {

        @Test
        @DisplayName("try-catch simple → 1")
        void simpleTryCatch() {
            assertEquals(1, calculator.calculate(parseMethod(
                    "try { System.out.println(); } catch (Exception e) { e.printStackTrace(); }")));
        }

        @Test
        @DisplayName("if dentro de catch → 3 (catch:1, if:1+1)")
        void ifInsideCatch() {
            assertEquals(3, calculator.calculate(parseMethod(
                    "try { System.out.println(); } "
                            + "catch (Exception e) { if (a > 0) { e.printStackTrace(); } }")));
        }
    }

    // =========================================================================
    // break/continue con etiqueta
    // =========================================================================

    @Nested
    @DisplayName("break/continue con etiqueta")
    class LabeledBreakContinue {

        @Test
        @DisplayName("break con etiqueta → +1")
        void labeledBreak() {
            // for: +1, if: +1+1=2, break label: +1 → total 4
            assertEquals(4, calculator.calculate(parseMethod(
                    "outer: for (int x : arr) { if (x > 0) { break outer; } }")));
        }

        @Test
        @DisplayName("break sin etiqueta → +0")
        void unlabeledBreak() {
            // for: +1, if: +1+1=2 → total 3 (break normal no suma)
            assertEquals(3, calculator.calculate(parseMethod(
                    "for (int x : arr) { if (x > 0) { break; } }")));
        }
    }

    // =========================================================================
    // Patrón MVP: before vs after
    // =========================================================================

    @Nested
    @DisplayName("Verificación before/after del patrón MVP")
    class BeforeAfterPattern {

        @Test
        @DisplayName("Patrón MVP: before=3, after=2, delta=-1")
        void basicPatternDelta() {
            int before = calculator.calculate(parseMethod(
                    "if (a > 0) { if (b > 0) { System.out.println(); } }"));
            int after = calculator.calculate(parseMethod(
                    "if (a > 0 && b > 0) { System.out.println(); }"));
            assertEquals(3, before);
            assertEquals(2, after);
            assertEquals(-1, after - before);
        }

        @Test
        @DisplayName("Patrón MVP dentro de for: before=6, after=4, delta=-2")
        void patternInsideForDelta() {
            int before = calculator.calculate(parseMethod(
                    "for (int x : arr) { if (a > 0) { if (b > 0) { System.out.println(); } } }"));
            int after = calculator.calculate(parseMethod(
                    "for (int x : arr) { if (a > 0 && b > 0) { System.out.println(); } }"));
            assertEquals(6, before);
            assertEquals(4, after);
            assertEquals(-2, after - before);
        }

        @Test
        @DisplayName("Triple nesting: before=6, after completamente aplanado=2")
        void tripleNestingFullyFlattened() {
            int before = calculator.calculate(parseMethod(
                    "if (a > 0) { if (b > 0) { if (c) { System.out.println(); } } }"));
            int after = calculator.calculate(parseMethod(
                    "if (a > 0 && b > 0 && c) { System.out.println(); }"));
            assertEquals(6, before);
            assertEquals(2, after);
            assertEquals(-4, after - before);
        }
    }

    // =========================================================================
    // Conveniencia: calculate(String)
    // =========================================================================

    @Nested
    @DisplayName("Método de conveniencia calculate(String)")
    class StringOverload {

        @Test
        @DisplayName("Parsea clase completa y calcula primer método")
        void parsesFullClass() {
            int result = calculator.calculate(
                    "class T { void m(int a, int b) { if (a > 0) { if (b > 0) { System.out.println(); } } } }");
            assertEquals(3, result);
        }

        @Test
        @DisplayName("Clase sin método → 0")
        void noMethod() {
            assertEquals(0, calculator.calculate("class T { int x = 5; }"));
        }
    }

    // =========================================================================
    // Recursión (regla SonarSource: +1 estructural por llamada del método a sí mismo)
    // =========================================================================

    @Nested
    @DisplayName("Recursión")
    class Recursion {

        @Test
        @DisplayName("Recursión directa simple → 2 (if:1 + llamada recursiva:1)")
        void directRecursion() {
            // if (n<=1): +1 ; fact(n-1): +1 (recursión, sin anidamiento) → 2
            assertEquals(2, calculator.calculate(
                    "class T { int fact(int n) { if (n <= 1) { return 1; } "
                            + "return n * fact(n - 1); } }"));
        }

        @Test
        @DisplayName("Dos llamadas recursivas (Fibonacci) → 3 (if:1 + 2 llamadas)")
        void twoRecursiveCalls() {
            // if (n<2): +1 ; fib(n-1): +1 ; fib(n-2): +1 → 3
            assertEquals(3, calculator.calculate(
                    "class T { int fib(int n) { if (n < 2) { return n; } "
                            + "return fib(n - 1) + fib(n - 2); } }"));
        }

        @Test
        @DisplayName("La recursión suma +1 plano, sin multiplicar por anidamiento → 4")
        void recursionIsFlatRegardlessOfNesting() {
            // if(n>0):+1 ; if(n>1):+1+1=2 ; f(n-1) en nivel 2: +1 (plano) → 4
            assertEquals(4, calculator.calculate(
                    "class T { int f(int n) { if (n > 0) { if (n > 1) { "
                            + "return f(n - 1); } } return 0; } }"));
        }

        @Test
        @DisplayName("Recursión a través de this → 1")
        void recursionViaThis() {
            assertEquals(1, calculator.calculate(
                    "class T { int f(int n) { return this.f(n - 1); } }"));
        }

        @Test
        @DisplayName("Llamada con aridad distinta no es recursión → 0")
        void differentArityIsNotRecursion() {
            // m(a, a) tiene 2 argumentos; el método tiene 1 parámetro → no cuenta
            assertEquals(0, calculator.calculate(
                    "class T { void m(int a) { m(a, a); } }"));
        }

        @Test
        @DisplayName("Llamada sobre otro receptor no es recursión → 0")
        void callOnOtherReceiverIsNotRecursion() {
            // o.f(...) tiene un receptor distinto de this → no cuenta
            assertEquals(0, calculator.calculate(
                    "class T { int f(int n) { T o = null; return o.f(n - 1); } }"));
        }
    }
}
