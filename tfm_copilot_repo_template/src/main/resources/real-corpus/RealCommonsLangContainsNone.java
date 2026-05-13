// @caseId REAL_COMMONS_LANG_CONTAINS_NONE
// @origin commons-lang-3.12.0
// @project Apache Commons Lang
// @file StringUtils.java
// @method containsNone(CharSequence, char...)
// @license Apache-2.0
// @sonarCCBefore 15
// @description Método con if-null + if-null + for + for + if — patrón de búsqueda con doble iteración

/**
 * Patrón representativo de Apache Commons Lang 3.12.0 — StringUtils.containsNone.
 * Contiene dos ifs de guarda seguidos de doble bucle con if.
 * Caso ELEGIBLE PARCIAL: los dos ifs externos (cs != null, searchChars != null)
 * son combinables por condiciones puras. Los bucles internos permanecen intactos.
 */
class RealCommonsLangContainsNone {

    boolean containsNone(String cs, char[] searchChars) {
        if (cs != null) {
            if (searchChars != null) {
                int csLen = cs.length();
                int searchLen = searchChars.length;
                for (int i = 0; i < csLen; i++) {
                    char ch = cs.charAt(i);
                    for (int j = 0; j < searchLen; j++) {
                        if (searchChars[j] == ch) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
        return true;
    }
}
