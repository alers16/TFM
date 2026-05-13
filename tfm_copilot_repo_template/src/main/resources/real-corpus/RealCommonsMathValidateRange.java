// @caseId REAL_COMMONS_MATH_VALIDATE_RANGE
// @origin commons-math-3.6.1
// @project Apache Commons Math
// @file MathUtils.java
// @method validateRange(double, double, double)
// @license Apache-2.0
// @sonarCCBefore 4
// @description Método con if-else en outer — no elegible por P1 (rama else)

/**
 * Patrón representativo de Apache Commons Math 3.6.1 — validación de rango.
 * Contiene if con else en el nivel externo.
 * Caso NO ELEGIBLE: rama else impide la combinación (precondición P1).
 */
class RealCommonsMathValidateRange {

    String validateRange(double value, double lower, double upper) {
        if (value >= lower) {
            if (value <= upper) {
                return "in range";
            }
        } else {
            return "below minimum: " + lower;
        }
        return "above maximum: " + upper;
    }
}
