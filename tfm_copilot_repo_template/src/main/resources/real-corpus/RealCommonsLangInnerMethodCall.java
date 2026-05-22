// @caseId REAL_COMMONS_LANG_INNER_METHOD_CALL
// @origin commons-lang-3.12.0
// @project Apache Commons Lang
// @file StringUtils.java
// @method hasContent(String)
// @license Apache-2.0
// @sonarCCBefore 3
// @description Método con guarda null externa y llamada a método en condición interna — no elegible por P5 (METHOD_CALL_IN_CONDITION en condición interna)

/**
 * Patrón representativo de Apache Commons Lang 3.12.0 — StringUtils.
 * Contiene un if anidado donde la condición INTERNA usa una llamada a método (isEmpty).
 * Caso NO ELEGIBLE: la condición `s.isEmpty()` es una llamada a método (precondición P5).
 * Razón de descarte: METHOD_CALL_IN_CONDITION.
 * Diferencia del caso RealCommonsCollectionsGet: aquí la condición EXTERNA es pura (null check);
 * solo la condición interna incumple P5. Ilustra que P5 aplica a ambas condiciones.
 */
class RealCommonsLangInnerMethodCall {

    boolean hasContent(String s) {
        if (s != null) {
            if (s.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
