// @caseId REAL_COMMONS_LANG_DECREMENT_COND
// @origin commons-lang-3.12.0
// @project Apache Commons Lang
// @file NumberUtils.java
// @method checkBound(int, int)
// @license Apache-2.0
// @sonarCCBefore 3
// @description Método con operador de decremento en condición interna — no elegible por P5 (INCREMENT_OR_DECREMENT_IN_CONDITION)

/**
 * Patrón representativo de Apache Commons Lang 3.12.0 — NumberUtils.
 * Contiene un if anidado donde la condición interna usa el operador de decremento prefijo.
 * Caso NO ELEGIBLE: la condición `--count < max` muta la variable `count` como side effect
 * (precondición P5 no satisfecha).
 * Razón de descarte: INCREMENT_OR_DECREMENT_IN_CONDITION.
 * Combinar con && adelantaría la evaluación del decremento, alterando la semántica.
 */
class RealCommonsLangDecrementCond {

    boolean checkBound(int count, int max) {
        if (count > 0) {
            if (--count < max) {
                return true;
            }
        }
        return false;
    }
}
