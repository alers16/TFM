// @caseId REAL_COMMONS_LANG_CONTAINS_NONE
// @origin commons-lang-3.12.0
// @project Apache Commons Lang
// @file StringUtils.java
// @method containsNone(CharSequence, char...)
// @license Apache-2.0
// @sonarCCBeforeProxy 15
// @sonarCCAfterProxy 11
// @description Versión refactorizada por el prototipo — combinación de los dos ifs de guarda externos

class RealCommonsLangContainsNone {

    boolean containsNone(String cs, char[] searchChars) {
        if (cs != null && searchChars != null) {
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
        return true;
    }
}
