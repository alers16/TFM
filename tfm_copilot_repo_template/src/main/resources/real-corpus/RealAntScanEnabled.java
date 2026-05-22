// @caseId REAL_ANT_SCAN_ENABLED
// @origin ant-1.10.12
// @project Apache Ant
// @file DirectoryScanner.java
// @method scan(File, boolean, String[])
// @license Apache-2.0
// @sonarCCBefore 3
// @description Método con doble guarda null + flag booleano — elegible, patrón de activación condicional

/**
 * Patrón representativo de Apache Ant 1.10.12 — DirectoryScanner.
 * Contiene un if anidado: guarda null exterior + flag booleano como condición interna.
 * Caso ELEGIBLE: ambas condiciones son puras (null check + variable booleana),
 * sin else, bloque externo con única sentencia (el if interno).
 * La herramienta combina en if (dir != null && enabled) y reduce CC en 1.
 */
class RealAntScanEnabled {

    String[] includes;

    void scan(java.io.File dir, boolean enabled, String[] patterns) {
        if (dir != null) {
            if (enabled) {
                includes = patterns;
            }
        }
    }
}
