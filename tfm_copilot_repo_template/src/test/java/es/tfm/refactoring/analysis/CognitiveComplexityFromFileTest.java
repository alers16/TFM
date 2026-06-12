package es.tfm.refactoring.analysis;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Valida {@link CognitiveComplexityCalculator} parseando el fichero real de test
 * de SonarSource: {@code CognitiveComplexityMethodCheckMax0.java}.
 *
 * <p>A diferencia de {@link CognitiveComplexityCalculatorSonarValidationTest}, que
 * construye snippets inline, esta clase lee el fichero completo desde
 * {@code src/test/resources/sonar-validation/} tal como lo haría el escaner
 * sobre código real. Esto verifica que el calculador produce resultados consistentes
 * sobre un fichero con tipos no resueltos, lambdas, bucles sin llaves, etc.
 *
 * <h2>Métodos excluidos (limitaciones conocidas del calculador)</h2>
 * <ul>
 *   <li>{@code ternaryOp} – operadores ternarios no contabilizados</li>
 *   <li>{@code boolean extraConditions()} – operadores lógicos en {@code return}</li>
 *   <li>{@code extraConditions2} – operadores lógicos en {@code return}</li>
 *   <li>{@code enforceLimits} – lambda como nivel de anidamiento</li>
 *   <li>{@code isPalindrome} – recursión</li>
 *   <li>{@code main} – lambda con {@code if} interno</li>
 *   <li>{@code localClasses} – clase local dentro de método</li>
 * </ul>
 *
 * <h2>Discrepancias conocidas con SonarSource</h2>
 * SonarSource cuenta secuencias de operadores lógicos en lectura plana del código;
 * nuestro algoritmo usa el AST con precedencia de operadores. Esto produce una
 * diferencia de −1 a −2 en expresiones con cuatro o más cambios de tipo.
 * Cada caso está marcado con {@code // SONAR=N  nuestro=M}.
 */
@DisplayName("CC fichero real SonarSource – CognitiveComplexityMethodCheckMax0")
class CognitiveComplexityFromFileTest {

    private CognitiveComplexityCalculator calculator;
    private CompilationUnit cu;

    @BeforeEach
    void setUp() throws Exception {
        calculator = new CognitiveComplexityCalculator();

        // Usar un parser local con RAW para tolerar tipos no declarados en el
        // fichero de validación sin afectar al estado global de StaticJavaParser.
        JavaParser parser = new JavaParser(
                new ParserConfiguration()
                        .setLanguageLevel(ParserConfiguration.LanguageLevel.RAW));

        try (InputStream is = getClass().getResourceAsStream(
                "/sonar-validation/CognitiveComplexityMethodCheckMax0.java")) {
            assertNotNull(is, "Fichero de validación no encontrado en resources/sonar-validation/");
            String src = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            ParseResult<CompilationUnit> result = parser.parse(src);
            assertTrue(result.getResult().isPresent(), "Error al parsear el fichero: " + result.getProblems());
            cu = result.getResult().get();
        }
    }

    /** Devuelve el primer método con ese nombre. */
    private MethodDeclaration method(String name) {
        return cu.findFirst(MethodDeclaration.class,
                        md -> md.getNameAsString().equals(name))
                .orElseThrow(() -> new NoSuchElementException("Método no encontrado: " + name));
    }

    /** Para métodos con nombre duplicado: devuelve el primero cuyo tipo de retorno sea void. */
    private MethodDeclaration voidMethod(String name) {
        return cu.findAll(MethodDeclaration.class).stream()
                .filter(md -> md.getNameAsString().equals(name) && md.getType().isVoidType())
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Método void no encontrado: " + name));
    }

    // =========================================================================
    // BLOQUE 1: Sin control de flujo / estructura simple
    // =========================================================================

    @Nested
    @DisplayName("Sin control de flujo")
    class SinControlDeFlujo {

        @Test
        @DisplayName("getSpecifiedByKeysAsCommaList – solo return → CC=0 [SONAR=0]")
        void getSpecifiedByKeysAsCommaList() {
            assertEquals(0, calculator.calculate(method("getSpecifiedByKeysAsCommaList")));
        }

        @Test
        @DisplayName("toProtocolType – solo switch → CC=1 [SONAR=1]")
        void toProtocolType() {
            assertEquals(1, calculator.calculate(method("toProtocolType")));
        }
    }

    // =========================================================================
    // BLOQUE 2: Operadores lógicos en condiciones de control de flujo
    // =========================================================================

    @Nested
    @DisplayName("Operadores lógicos en condiciones (extraConditions)")
    class OperadoresLogicos {

        @Test
        @DisplayName("extraConditions7 – if simple → CC=1 [SONAR=1]")
        void extraConditions7() {
            assertEquals(1, calculator.calculate(method("extraConditions7")));
        }

        @Test
        @DisplayName("extraConditions8 – if con secuencia && → CC=2 [SONAR=2]")
        void extraConditions8() {
            assertEquals(2, calculator.calculate(method("extraConditions8")));
        }

        @Test
        @DisplayName("extraConditions9 – if con secuencia || → CC=2 [SONAR=2]")
        void extraConditions9() {
            assertEquals(2, calculator.calculate(method("extraConditions9")));
        }

        @Test
        @DisplayName("extraCondition11 – paréntesis ignorados en || → CC=2 [SONAR=2]")
        void extraCondition11() {
            assertEquals(2, calculator.calculate(method("extraCondition11")));
        }

        @Test
        @DisplayName("extraConditions3 – &&...|| → CC=3 [SONAR=3]")
        void extraConditions3() {
            assertEquals(3, calculator.calculate(method("extraConditions3")));
        }

        @Test
        @DisplayName("extraConditions6 – &&&&...||... → CC=3 [SONAR=3]")
        void extraConditions6() {
            assertEquals(3, calculator.calculate(method("extraConditions6")));
        }

        @Test
        @DisplayName("extraCondition10 – &&&&||...||&& → CC=4 [SONAR=4]")
        void extraCondition10() {
            assertEquals(4, calculator.calculate(method("extraCondition10")));
        }

        @Test
        @DisplayName("extraConditions4 – &&||&&|| entrelazado → CC=5 [SONAR=5]")
        void extraConditions4() {
            assertEquals(5, calculator.calculate(method("extraConditions4")));
        }

        @Test
        @DisplayName("extraConditions5 – ||&&||&& entrelazado → CC=5 [SONAR=5]")
        void extraConditions5() {
            assertEquals(5, calculator.calculate(method("extraConditions5")));
        }

        @Test
        @DisplayName("extraConditions12 – condición multilínea compleja → CC=7 [SONAR=7]")
        void extraConditions12() {
            assertEquals(7, calculator.calculate(method("extraConditions12")));
        }
    }

    // =========================================================================
    // BLOQUE 3: Estructuras de control complejas
    // =========================================================================

    @Nested
    @DisplayName("Estructuras de control complejas")
    class EstructurasComplejas {

        @Test
        @DisplayName("extraConditions7 (control basic) – CC=1 ya cubierto arriba")
        void extraConditions7Alias() {
            assertEquals(1, calculator.calculate(method("extraConditions7")));
        }

        @Test
        @DisplayName("getWeight – 4 if secuenciales → CC=4 [SONAR=4]")
        void getWeight() {
            assertEquals(4, calculator.calculate(method("getWeight")));
        }

        @Test
        @DisplayName("breakWithLabel – for-each con break etiquetado → CC=2 [SONAR=2]")
        void breakWithLabel() {
            assertEquals(2, calculator.calculate(method("breakWithLabel")));
        }

        @Test
        @DisplayName("bulkActivate – try/while/catch/finally → CC=6 [SONAR=6]")
        void bulkActivate() {
            assertEquals(6, calculator.calculate(method("bulkActivate")));
        }

        @Test
        @DisplayName("getValueToEval – if/else-if/else + while anidado → CC=6 [SONAR=6]")
        void getValueToEval() {
            assertEquals(6, calculator.calculate(method("getValueToEval")));
        }

        @Test
        @DisplayName("to – for anidados y if sin llaves → CC=7 [SONAR=7]")
        void to() {
            // for(ctr): +1(nesting=0)
            //   if(args): +2(nesting=1)
            // for(i): +1(nesting=0)
            // if(foo): +1(nesting=0)
            //   for(i2): +2(nesting=1)
            // Total: 1+2+1+1+2 = 7
            assertEquals(7, calculator.calculate(method("to")));
        }

        @Test
        @DisplayName("switch2 – switch con if anidados y operadores → CC=12 [SONAR=12]")
        void switch2() {
            assertEquals(12, calculator.calculate(method("switch2")));
        }

        @Test
        @DisplayName("void extraConditions – bucles mixtos con operadores → CC=10 [SONAR=10]")
        void extraConditionsVoid() {
            // Hay dos métodos llamados extraConditions: el boolean (SKIP – retorno lógico)
            // y este (void), que es el que tiene bucles mixtos.
            assertEquals(10, calculator.calculate(voidMethod("extraConditions")));
        }

        @Test
        @DisplayName("sumOfNonPrimes – for etiquetado con continue etiquetado → CC=9 [SONAR=9]")
        void sumOfNonPrimes() {
            assertEquals(9, calculator.calculate(method("sumOfNonPrimes")));
        }

        @Test
        @DisplayName("doFilter – cadena compleja if/catch/else-if → CC=13 [SONAR=13]")
        void doFilter() {
            assertEquals(13, calculator.calculate(method("doFilter")));
        }

        @Test
        @DisplayName("noNestingForIfElseIf – anidamiento profundo 4 niveles → CC=21 [SONAR=21]")
        void noNestingForIfElseIf() {
            assertEquals(21, calculator.calculate(method("noNestingForIfElseIf")));
        }
    }

    // =========================================================================
    // BLOQUE 4: Métodos antes no cubiertos – ahora implementados
    // =========================================================================

    @Nested
    @DisplayName("Características antes limitadas – ahora implementadas (fichero real)")
    class LimitacionesImplementadas {

        @Test
        @DisplayName("ternaryOp – dos ternarios → CC=2 [SONAR=2]")
        void ternaryOp() {
            // int a = (i < 0) ? -i : i; return (a < lowerBound) ? lowerBound : a;
            // +1 + +1 = 2
            assertEquals(2, calculator.calculate(method("ternaryOp")));
        }

        @Test
        @DisplayName("extraConditions (boolean) – return con lógica → CC=3 [SONAR=3]")
        void extraConditionsBoolReturn() {
            // return a && b || foo(b && c);
            // &&-seq + ||-seq + &&-seq-en-arg = 3
            // Usamos method() porque el método boolean es el primero con ese nombre.
            assertEquals(3, calculator.calculate(method("extraConditions")));
        }

        @Test
        @DisplayName("extraConditions2 – return con subexpresión parentizada → CC=2 [SONAR=2]")
        void extraConditions2() {
            // return a && (b || c) || d;
            // Recorrido plano (paréntesis ignorados): &&, ||, || → 2 secuencias
            // SonarSource=2 ✓ (ahora coincide)
            assertEquals(2, calculator.calculate(method("extraConditions2")));
        }

        @Test
        @DisplayName("enforceLimits – lambda con || en argumento → CC=1 [SONAR=1]")
        void enforceLimits() {
            assertEquals(1, calculator.calculate(method("enforceLimits")));
        }

        @Test
        @DisplayName("isPalindrome – else + && en return + recursión → CC=4 [SONAR=3]")
        void isPalindrome() {
            // if: +1; else: +1; return ... && ...: +1; recursive call: +1 → 4
            assertEquals(4, calculator.calculate(method("isPalindrome")));
        }

        @Test
        @DisplayName("main – lambda + clase anónima con if interno → CC=4 [SONAR=4]")
        void main() {
            assertEquals(4, calculator.calculate(method("main")));
        }

        @Test
        @DisplayName("localClasses – clase local con return lógico → CC=3 [SONAR=3]")
        void localClasses() {
            assertEquals(3, calculator.calculate(method("localClasses")));
        }
    }
}
