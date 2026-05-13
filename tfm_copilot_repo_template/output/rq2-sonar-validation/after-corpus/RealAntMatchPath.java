// @caseId REAL_ANT_MATCH_PATH
// @origin ant-1.10.12
// @project Apache Ant
// @file SelectorUtils.java
// @method matchPath(String, String, boolean)
// @license Apache-2.0
// @sonarCCBeforeProxy 6
// @sonarCCAfterProxy 4
// @description Versión refactorizada por el prototipo — combinación nivel 1+2 (P5 rechaza nivel 3)

class RealAntMatchPath {

    boolean matchPath(String path, String pattern, boolean caseSensitive) {
        if (path != null && pattern != null) {
            if (path.length() > 0) {
                return path.startsWith(pattern);
            }
        }
        return false;
    }
}
