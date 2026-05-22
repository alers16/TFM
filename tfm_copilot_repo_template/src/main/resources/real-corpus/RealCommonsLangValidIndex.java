// @caseId REAL_COMMONS_LANG_VALID_INDEX
// @origin commons-lang-3.12.0
// @project Apache Commons Lang
// @file StringUtils.java
// @method charAt(String, int, char)
// @license Apache-2.0
// @sonarCCBefore 3
// @description Método con doble guarda null + índice positivo (aritmética pura) — elegible

/**
 * Patrón representativo de Apache Commons Lang 3.12.0 — StringUtils.
 * Contiene un if anidado: guarda null exterior + comprobación aritmética pura del índice.
 * Caso ELEGIBLE: ambas condiciones son puras (null check + comparación aritmética),
 * sin else, bloque externo con única sentencia (el if interno).
 * La herramienta combina en if (str != null && index >= 0) y reduce CC en 1.
 */
class RealCommonsLangValidIndex {

    char charAt(String str, int index, char defaultChar) {
        if (str != null) {
            if (index >= 0) {
                return str.charAt(index);
            }
        }
        return defaultChar;
    }
}
