// @caseId REAL_COMMONS_LANG_MID
// @origin commons-lang-3.12.0
// @project Apache Commons Lang
// @file StringUtils.java
// @method mid(String, int, int)
// @license Apache-2.0
// @sonarCCBefore 10
// @description Método con if-null + if-límite + if-pos + if-length — patrón elegible parcial de substring seguro

/**
 * Patrón representativo de Apache Commons Lang 3.12.0 — StringUtils.mid.
 * Comprueba que el string no es null y luego valida la posición.
 * Caso ELEGIBLE PARCIAL: primer par (str != null, len >= 0) es combinable.
 * Niveles internos usan str.length() (method call) y son rechazados por P5.
 */
class RealCommonsLangMid {

    String mid(String str, int pos, int len) {
        if (str != null) {
            if (len >= 0) {
                if (pos <= str.length()) {
                    if (str.length() <= pos + len) {
                        return str.substring(pos);
                    }
                    return str.substring(pos, pos + len);
                }
            }
        }
        return null;
    }
}
