// @caseId REAL_COMMONS_MATH_CONVERGED
// @origin commons-math-3.6.1
// @project Apache Commons Math
// @file BaseAbstractUnivariateIntegrator.java
// @method checkConvergence(double, double, double)
// @license Apache-2.0
// @sonarCCBefore 3
// @description Método con doble comprobación de convergencia — patrón elegible de rango numérico

/**
 * Patrón representativo de Apache Commons Math 3.6.1 — convergencia numérica.
 * Comprueba tolerancia absoluta y luego tolerancia relativa.
 * Caso ELEGIBLE: ambas condiciones son comparaciones numéricas puras.
 */
class RealCommonsMathConverged {

    boolean hasConverged(double absoluteError, double relativeError,
                         double absoluteTolerance, double relativeTolerance) {
        if (absoluteError <= absoluteTolerance) {
            if (relativeError <= relativeTolerance) {
                return true;
            }
        }
        return false;
    }
}
