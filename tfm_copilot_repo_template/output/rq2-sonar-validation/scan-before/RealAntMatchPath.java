// @caseId REAL_ANT_MATCH_PATH
// @origin ant-1.10.12
// @project Apache Ant
// @file SelectorUtils.java
// @method matchPath(String, String, boolean)
// @license Apache-2.0
// @sonarCCBefore 6
// @description Método con if anidado triple — elegible parcial (P5 rechaza tercer nivel por method call)

/**
 * Patrón representativo de Apache Ant 1.10.12 — coincidencia de rutas.
 * Contiene un triple if anidado para comprobar nulidad y longitud.
 * Caso ELEGIBLE PARCIAL: los dos primeros niveles son combinables (condiciones puras),
 * pero el tercer nivel usa path.length() (method call) y es rechazado por P5.
 * El pipeline debería aplicar una combinación y detenerse en el tercer nivel.
 */
class RealAntMatchPath {

    boolean matchPath(String path, String pattern, boolean caseSensitive) {
        if (path != null) {
            if (pattern != null) {
                if (path.length() > 0) {
                    return path.startsWith(pattern);
                }
            }
        }
        return false;
    }
}
