// @caseId REAL_COMMONS_LANG_CHOMP
// @origin commons-lang-3.12.0
// @project Apache Commons Lang
// @file StringUtils.java
// @method chomp(String)
// @license Apache-2.0
// @sonarCCBeforeProxy 6
// @sonarCCAfterProxy 6
// @description Caso NO ELEGIBLE — el método queda idéntico al original (control de no divergencia)

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
