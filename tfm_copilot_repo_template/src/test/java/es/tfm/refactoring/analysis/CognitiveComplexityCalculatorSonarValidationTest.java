package es.tfm.refactoring.analysis;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Validación de CognitiveComplexityCalculator frente a los casos de prueba
 * oficiales de SonarSource (CognitiveComplexityMethodCheck).
 *
 * <p>Los casos de prueba proceden de:
 * <ul>
 *   <li>SonarQube: CognitiveComplexityMethodCheckMax0.java (repositorio sonar-java)</li>
 *   <li>Herramienta del tutor: SoftwareCognitiveComplexityReducer/src/test/resources/CognitiveComplexityCheck.java</li>
 * </ul>
 * Ambos ficheros son idénticos; se usa el del repositorio del tutor como referencia directa.
 *
 * <h2>Alcance de la validación</h2>
 * Se incluyen los métodos cuya complejidad puede ser calculada por nuestro
 * estimador (JavaParser + AST walk). Se excluyen:
 * <ul>
    *   <li>Recursión (limitación conocida)</li>
 *   <li>Recursión (limitación conocida)</li>
 * </ul>
 *
 * <h2>Coincidencia con SonarSource</h2>
 * El algoritmo usa recorrido en inorden tratando paréntesis como transparentes,
 * lo que replica exactamente el conteo plano de SonarSource.
 * Se obtienen resultados idénticos en todos los casos de prueba oficiales.
 */
@DisplayName("Validación frente a tests oficiales SonarSource / tutor")
class CognitiveComplexityCalculatorSonarValidationTest {

    private CognitiveComplexityCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new CognitiveComplexityCalculator();
    }

    /**
     * Parsea un cuerpo de método envolviéndolo en una clase con variables booleanas
     * a-m (para cubrir todos los identificadores usados en los tests de SonarSource).
     */
    private MethodDeclaration parseMethod(String body) {
        String code = "class T { "
                + "boolean a,b,c,d,e,f,g,h,i,j,k,l,m; "
                + "int[] arr; "
                + "void m() { " + body + " } }";
        CompilationUnit cu = StaticJavaParser.parse(code);
        return cu.findFirst(MethodDeclaration.class).orElseThrow();
    }

    // =========================================================================
    // BLOQUE 1: Operadores lógicos en condiciones de control de flujo
    // Fuente: métodos extraConditions3-12 de CognitiveComplexityCheck.java
    // =========================================================================

    @Nested
    @DisplayName("Operadores lógicos en condiciones (extraConditions)")
    class LogicalOperatorsInConditions {

        @Test
        @DisplayName("extraConditions7 – if simple sin operadores → CC=1 [SONAR=1]")
        void extraConditions7() {
            // if(a) {}
            // +1 (if, nesting=0) = 1
            assertEquals(1, calculator.calculate(parseMethod("if (a) {}")));
        }

        @Test
        @DisplayName("extraConditions8 – if con secuencia plana de && → CC=2 [SONAR=2]")
        void extraConditions8() {
            // if(a && b && c && d && e) {}
            // +1(if) + 1(una secuencia &&) = 2
            assertEquals(2, calculator.calculate(parseMethod(
                    "if (a && b && c && d && e) {}")));
        }

        @Test
        @DisplayName("extraConditions9 – if con secuencia plana de || → CC=2 [SONAR=2]")
        void extraConditions9() {
            // if(a || b || c || d || e) {}
            // +1(if) + 1(una secuencia ||) = 2
            assertEquals(2, calculator.calculate(parseMethod(
                    "if (a || b || c || d || e) {}")));
        }

        @Test
        @DisplayName("extraCondition11 – paréntesis ignorados en || → CC=2 [SONAR=2]")
        void extraCondition11() {
            // if(a || (b || c)) {}
            // OR(a, OR(b,c)): OR raíz → nueva seq; OR(b,c): padre=OR raíz, mismo → no nueva
            // +1(if) + 1(una secuencia ||) = 2
            assertEquals(2, calculator.calculate(parseMethod(
                    "if (a || (b || c)) {}")));
        }

        @Test
        @DisplayName("extraConditions3 – && seguido de || → CC=3 [SONAR=3]")
        void extraConditions3() {
            // if(a && b || c || d) {}
            // Árbol: OR(OR(AND(a,b),c),d)
            // AND(a,b): padre=OR → nueva seq; OR raíz: padre=if → nueva seq → 2 secuencias
            // +1(if) + 2(secuencias: &&, ||) = 3
            assertEquals(3, calculator.calculate(parseMethod(
                    "if (a && b || c || d) {}")));
        }

        @Test
        @DisplayName("extraConditions6 – &&&&… || ||… → CC=3 [SONAR=3]")
        void extraConditions6() {
            // if(a && b && c || d || e) {}
            // Árbol: OR(OR(AND(AND(a,b),c),d),e)
            // AND(AND,c): padre=OR → nueva; AND(a,b): padre=AND, mismo → no; OR raíz: padre=if → nueva
            // +1(if) + 2(&&-seq, ||-seq) = 3
            assertEquals(3, calculator.calculate(parseMethod(
                    "if (a && b && c || d || e) {}")));
        }

        @Test
        @DisplayName("extraCondition10 – &&&&…||…||&&… → CC=4 [SONAR=4]")
        void extraCondition10() {
            // if(a && b && c || d || e && f) {}
            // Árbol: OR(OR(AND(AND(a,b),c),d),AND(e,f))
            // 3 secuencias (OR raíz, AND-seq1, AND-seq2)
            // +1(if) + 3(secuencias) = 4
            assertEquals(4, calculator.calculate(parseMethod(
                    "if (a && b && c || d || e && f) {}")));
        }

        // ------------------------------------------------------------------
        // Casos con DISCREPANCIA CONOCIDA respecto a SonarSource
        // Causa: SonarSource cuenta la secuencia plana; nosotros usamos el AST
        //        con precedencia de operadores, que agrupa las subcadenas &&.
        // ------------------------------------------------------------------

        @Test
        @DisplayName("extraConditions4 – &&||&&|| entrelazado → CC=5 [SONAR=5]")
        void extraConditions4() {
            // if(a && b || c && d || e) {}
            // Recorrido plano en inorden: &&, ||, &&, || → 4 secuencias
            // +1(if) + 4(secuencias) = 5
            assertEquals(5, calculator.calculate(parseMethod(
                    "if (a && b || c && d || e) {}")));
        }

        @Test
        @DisplayName("extraConditions5 – ||&&||&& entrelazado → CC=5 [SONAR=5]")
        void extraConditions5() {
            // if(a || b && c || d && e) {}
            // Recorrido plano en inorden: ||, &&, ||, && → 4 secuencias
            // +1(if) + 4(secuencias) = 5
            assertEquals(5, calculator.calculate(parseMethod(
                    "if (a || b && c || d && e) {}")));
        }

        @Test
        @DisplayName("extraConditions12 – condición multilínea compleja → CC=7 [SONAR=7]")
        void extraConditions12() {
            // if(a && b && c || d || e && f && g || (h || (i && j || k)) || l || m) {}
            // Recorrido plano en inorden (paréntesis ignorados): &&, ||, &&, ||, &&, || → 6 secuencias
            // +1(if) + 6(secuencias) = 7
            assertEquals(7, calculator.calculate(parseMethod(
                    "if (a && b && c || d || e && f && g "
                    + "|| (h || (i && j || k)) || l || m) {}")));
        }
    }

    // =========================================================================
    // BLOQUE 2: Estructuras de control complejas (casos estructurales)
    // =========================================================================

    @Nested
    @DisplayName("Estructuras de control complejas")
    class ComplexControlFlow {

        @Test
        @DisplayName("getSpecifiedByKeysAsCommaList – sin control de flujo → CC=0 [SONAR=0]")
        void getSpecifiedByKeysAsCommaList() {
            // Solo una expresión de retorno, sin control de flujo
            assertEquals(0, calculator.calculate(parseMethod("return a;")));
        }

        @Test
        @DisplayName("toProtocolType – solo switch → CC=1 [SONAR=1]")
        void toProtocolType() {
            // switch: +1(nesting=0) = 1
            assertEquals(1, calculator.calculate(parseMethod(
                    "switch (a) { case 1: break; case 2: break; default: break; }")));
        }

        @Test
        @DisplayName("getWeight – 4 if secuenciales → CC=4 [SONAR=4]")
        void getWeight() {
            // Cuatro if sin anidamiento: 4 × (+1) = 4
            assertEquals(4, calculator.calculate(parseMethod(
                    "if (a) { return; } "
                    + "if (b) { return; } "
                    + "if (c) { return; } "
                    + "if (d) { return; }")));
        }

        @Test
        @DisplayName("breakWithLabel – for-each con break etiquetado → CC=2 [SONAR=2]")
        void breakWithLabel() {
            // LabeledStmt pasa al for-each sin añadir complejidad.
            // for-each: +1(nesting=0); break etiquetado: +1 = 2
            assertEquals(2, calculator.calculate(parseMethod(
                    "label1: for (int o : arr) { break label1; }")));
        }

        @Test
        @DisplayName("bulkActivate – try/while/try/catch/if/finally → CC=6 [SONAR=6]")
        void bulkActivate() {
            // try externo: 0 (sin incremento)
            //   while(a): +1(nesting=0)
            //     try interno: 0
            //       if(b): +2(nesting=1)
            //     catch: +2(nesting=1)
            // finally: 0 (sin incremento)
            //   if(c): +1(nesting=0)
            // Total: 1+2+2+1 = 6
            assertEquals(6, calculator.calculate(parseMethod(
                    "try { "
                    + "  while (a) { "
                    + "    try { "
                    + "      if (b) {} "
                    + "    } catch (Exception e) {} "
                    + "  } "
                    + "} finally { "
                    + "  if (c) {} "
                    + "}")));
        }

        @Test
        @DisplayName("getValueToEval – if/else-if/else + while anidado → CC=6 [SONAR=6]")
        void getValueToEval() {
            // if(a && b): +1(if) + 1(&&) = 2
            // else if(c): +1 (sin penalización de anidamiento)
            // else: +1
            //   while(a): +2(nesting=1)
            // Total: 2+1+1+2 = 6
            assertEquals(6, calculator.calculate(parseMethod(
                    "if (a && b) { return; } "
                    + "else if (c) { return; } "
                    + "else { while (a) {} }")));
        }

        @Test
        @DisplayName("sumOfNonPrimes – for etiquetado + continue etiquetado → CC=9 [SONAR=9]")
        void sumOfNonPrimes() {
            // OUTER: for: +1(nesting=0)
            //   if(a): +2(nesting=1)
            //   for: +2(nesting=1)
            //     if(b): +3(nesting=2)
            //       continue OUTER: +1 (etiquetado)
            // Total: 1+2+2+3+1 = 9
            assertEquals(9, calculator.calculate(parseMethod(
                    "OUTER: for (int i = 0; i < 10; ++i) { "
                    + "  if (a) { continue; } "
                    + "  for (int j = 0; j < 1; ++j) { "
                    + "    if (b) { continue OUTER; } "
                    + "  } "
                    + "}")));
        }

        @Test
        @DisplayName("doFilter – cadena de if/catch/else-if → CC=13 [SONAR=13]")
        void doFilter() {
            // if(a): +1
            // catch: +1, catch: +1
            // if(a&&b): +2; if(a&&c): +2; if(a) dentro: +2(nesting=1)
            // if(b&&d): +2; if(c): +1; else if(d): +1
            // Total: 1+1+1+2+2+2+2+1+1 = 13
            assertEquals(13, calculator.calculate(parseMethod(
                    "if (a) { return; } "
                    + "try {} catch (RuntimeException e1) {} catch (Exception e2) {} "
                    + "if (a && b) {} "
                    + "if (a && c) { if (a) { return; } } "
                    + "if (b && d) {} "
                    + "if (c) {} else if (d) {}")));
        }

        @Test
        @DisplayName("noNestingForIfElseIf – anidamiento profundo con else-if → CC=21 [SONAR=21]")
        void noNestingForIfElseIf() {
            // while: +1(nesting=0)
            //   if: +2(nesting=1)
            //     for(;;): +3(nesting=2) — bucle infinito sin condición
            //       if: +4(nesting=3)
            //       else if: +1 (sin penalización de anidamiento)
            //       else → if: +1(else) + +5(nesting=4)
            //       if: +4(nesting=3)
            // Total: 1+2+3+4+1+1+5+4 = 21
            assertEquals(21, calculator.calculate(parseMethod(
                    "while (a) { "
                    + "  if (b) { "
                    + "    for (;;) { "
                    + "      if (c) { "
                    + "      } else if (d) { "
                    + "      } else { "
                    + "        if (a) {} "
                    + "      } "
                    + "      if (b) {} "
                    + "    } "
                    + "  } "
                    + "}")));
        }

        @Test
        @DisplayName("switch2 – switch con if anidados y operadores → CC=12 [SONAR=12]")
        void switch2() {
            // switch: +1(nesting=0)
            //   if(b): +2(nesting=1)
            //     if(a&&b&&c||d): +3(nesting=2) + 2ops(&&-seq, ||-seq) = 5
            //     if(b): +3(nesting=2); else: +1
            // Total: 1+2+5+3+1 = 12
            assertEquals(12, calculator.calculate(parseMethod(
                    "switch (a) { "
                    + "  case 1: break; "
                    + "  default: "
                    + "    if (b) { "
                    + "      if (a && b && c || d) {} "
                    + "      if (b) {} else {} "
                    + "    } "
                    + "    break; "
                    + "}")));
        }

        @Test
        @DisplayName("extraConditions – bucles mixtos con operadores → CC=10 [SONAR=10]")
        void extraConditionsMixedLoops() {
            // if(a): +1
            // if(a||b||c): +1(if) + 1(||-seq) = 2
            //   while(a&&b): +2(nesting=1) + 1(&&) = 3
            // do{} while(a||b): +1(do) + 1(||-seq) = 2
            // for(…a&&b…): +1(for) + 1(&&) = 2
            // Total: 1+2+3+2+2 = 10
            assertEquals(10, calculator.calculate(parseMethod(
                    "if (a) {} "
                    + "if (a || b || c) { while (a && b) {} } "
                    + "do {} while (a || b); "
                    + "for (int i = 0; a && b; i++) {}")));
        }
    }

    // =========================================================================
    // BLOQUE 3: Características antes limitadas – ahora implementadas
    // =========================================================================

    @Nested
    @DisplayName("Características antes limitadas – ahora implementadas")
    class LimitacionesImplementadas {

        @Test
        @DisplayName("Ternario simple – un operador ternario → CC=1")
        void ternarioSimple() {
            // return a ? b : c;
            // +1 (ternary)
            assertEquals(1, calculator.calculate(parseMethod("return a ? b : c;")));
        }

        @Test
        @DisplayName("Dos ternarios seguidos – → CC=2")
        void dosTernarios() {
            // boolean x = a ? b : c;  boolean y = d ? e : f;
            // +1 + +1 = 2
            assertEquals(2, calculator.calculate(parseMethod(
                    "boolean x = a ? b : c; boolean y = d ? e : f;")));
        }

        @Test
        @DisplayName("Ternario anidado en if – incremento de anidamiento → CC=3 [SONAR=3]")
        void ternarioAnidadoEnIf() {
            // if (a) { return b ? c : d; }
            // if: +1 (nesting=0)
            // ternario a nesting=1: +1 estructural + 1 anidamiento = +2
            // Total: 3
            assertEquals(3, calculator.calculate(parseMethod(
                    "if (a) { return b ? c : d; }")));
        }

        @Test
        @DisplayName("Ternario anidado en ternario → CC=3 [SONAR=3]")
        void ternarioAnidadoEnTernario() {
            // return a ? b : (c ? d : e);
            // ternario externo: +1 (nesting=0)
            // ternario interno a nesting=1: +1 estructural + 1 anidamiento = +2
            // Total: 3
            assertEquals(3, calculator.calculate(parseMethod(
                    "return a ? b : (c ? d : e);")));
        }

        @Test
        @DisplayName("Lógica en return – return a && b → CC=1")
        void logicaEnReturn() {
            assertEquals(1, calculator.calculate(parseMethod("return a && b;")));
        }

        @Test
        @DisplayName("Lógica en return – return a && b || c → CC=2")
        void logicaEnReturn2() {
            // &&-seq + ||-seq = 2
            assertEquals(2, calculator.calculate(parseMethod("return a && b || c;")));
        }

        @Test
        @DisplayName("Lógica en asignación – boolean x = a || b → CC=1")
        void logicaEnAsignacion() {
            assertEquals(1, calculator.calculate(parseMethod("boolean x = a || b;")));
        }

        @Test
        @DisplayName("If + lógica en return → CC=3")
        void ifConReturnLogico() {
            // if(a): +1 (nesting=0)
            // else: +1
            // return b && c: +1 (&&-seq)
            // Total: 3
            assertEquals(3, calculator.calculate(parseMethod(
                    "if (a) { return b && c; } else { return a; }")));
        }

        @Test
        @DisplayName("Lambda con lógica – lambda body con || → CC=1")
        void lambdaConLogica() {
            // Runnable r = () -> a || b;
            // lambda body at nesting=1: ExprStmt(OR(a,b))
            // OR: padre = lambda body (no BinaryExpr) → +1
            // Total: 1
            assertEquals(1, calculator.calculate(parseMethod(
                    "Runnable r = () -> a || b;")));
        }

        @Test
        @DisplayName("Lambda con if – lambda body con if anidado → CC=2")
        void lambdaConIf() {
            // Runnable r = () -> { if (a) {} };
            // lambda body at nesting=1: if(a) → +1(structural) + +1(nesting=1) = 2
            assertEquals(2, calculator.calculate(parseMethod(
                    "Runnable r = () -> { if (a) {} };")));
        }

        @Test
        @DisplayName("Clase anónima con if – métodos al nesting+1 → CC=2")
        void claseAnonimaConIf() {
            // new Runnable() { void run() { if (a) {} } }
            // anonymous body, run() at nesting=1: if(a) → 1+1 = 2
            assertEquals(2, calculator.calculate(parseMethod(
                    "doThing(new Runnable() { public void run() { if (a) {} } });")));
        }

        @Test
        @DisplayName("Clase local con if – métodos al nesting+1 → CC=2")
        void claseLocalConIf() {
            // class Local { void inner() { if (a) {} } }
            // local class, inner() at nesting=1: if(a) → 1+1 = 2
            assertEquals(2, calculator.calculate(parseMethod(
                    "class Local { void inner() { if (a) {} } }")));
        }
    }
}
