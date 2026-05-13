// @caseId REAL_COMMONS_LANG_CHOMP
// @origin commons-lang-3.12.0
// @project Apache Commons Lang
// @file StringUtils.java
// @method chomp(String)
// @license Apache-2.0
// @sonarCCBefore 6
// @description Método con patrón if-null + if-length + if-char — representativo de validación defensiva en StringUtils

/**
 * Patrón representativo de Apache Commons Lang 3.12.0 — StringUtils.chomp.
 * Contiene condicionales anidados para proteger acceso a caracteres.
 * Caso NO ELEGIBLE: condición interna str.length() contiene llamada
 * a método (rechazado por precondición P5 del MVP conservador).
 */
class RealCommonsLangChomp {

    String chomp(String str) {
        if (str != null) {
            if (str.length() > 0) {
                int lastIdx = str.length() - 1;
                char last = str.charAt(lastIdx);
                if (last == '\n') {
                    return str.substring(0, lastIdx);
                }
                return str;
            }
        }
        return str;
    }
}
