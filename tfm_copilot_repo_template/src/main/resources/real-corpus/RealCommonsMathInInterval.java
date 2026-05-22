// @caseId REAL_COMMONS_MATH_IN_INTERVAL
// @origin commons-math-3.6.1
// @project Apache Commons Math
// @file MathUtils.java
// @method inInterval(double, double, double)
// @license Apache-2.0
// @sonarCCBefore 3
// @description Método con doble comprobación de intervalo aritmético puro — elegible

/**
 * Patrón representativo de Apache Commons Math 3.6.1 — MathUtils.
 * Contiene un if anidado: comprobación de límite inferior + límite superior (aritmética pura).
 * Caso ELEGIBLE: ambas condiciones son puramente aritméticas, sin else,
 * bloque externo con única sentencia (el if interno).
 * La herramienta combina en if (x >= low && x <= high) y reduce CC en 1.
 * Variante del patrón de convergencia: aquí el dominio es un intervalo genérico.
 */
class RealCommonsMathInInterval {

    boolean inInterval(double x, double low, double high) {
        if (x >= low) {
            if (x <= high) {
                return true;
            }
        }
        return false;
    }
}
