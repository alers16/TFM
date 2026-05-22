// @caseId REAL_COMMONS_MATH_NO_NESTING
// @origin commons-math-3.6.1
// @project Apache Commons Math
// @file MathUtils.java
// @method signum(double)
// @license Apache-2.0
// @sonarCCBefore 2
// @description Método con dos if secuenciales sin anidamiento — no elegible por SINGLE_STATEMENT_NOT_IF (cuerpo de cada if es return, no otro if)

/**
 * Patrón representativo de Apache Commons Math 3.6.1 — MathUtils.signum.
 * Contiene dos if secuenciales (no anidados) para determinar el signo de un valor.
 * Caso NO ELEGIBLE: no existe el patrón if(A){if(B){S}}; los dos if son independientes
 * y secuenciales. Cada if tiene un único return como cuerpo (no otro if),
 * por lo que la razón de descarte es SINGLE_STATEMENT_NOT_IF (P3).
 * Razones de descarte: SINGLE_STATEMENT_NOT_IF.
 * Ilustra que la herramienta no confunde ifs secuenciales con ifs anidados.
 */
class RealCommonsMathNoNesting {

    int signum(double value) {
        if (value < 0) {
            return -1;
        }
        if (value > 0) {
            return 1;
        }
        return 0;
    }
}
