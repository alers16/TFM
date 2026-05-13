// @caseId REAL_COMMONS_LANG_IS_NUMERIC
// @origin commons-lang-3.12.0
// @project Apache Commons Lang
// @file StringUtils.java
// @method isNumeric(CharSequence)
// @license Apache-2.0
// @sonarCCBefore 10
// @description Método con if-null + if-longitud + for + if-dígito — patrón de recorrido con guarda

/**
 * Patrón representativo de Apache Commons Lang 3.12.0 — StringUtils.isNumeric.
 * Contiene if anidado para validar que la secuencia no es vacía antes de iterar.
 * Caso NO ELEGIBLE: condición interna cs.length() contiene llamada
 * a método (rechazado por precondición P5 del MVP conservador).
 */
class RealCommonsLangIsNumeric {

    boolean isNumeric(String cs) {
        if (cs != null) {
            if (cs.length() > 0) {
                for (int i = 0; i < cs.length(); i++) {
                    if (!Character.isDigit(cs.charAt(i))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
}
