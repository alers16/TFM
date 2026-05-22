// @caseId REAL_COMMONS_LANG_TRIPLE_GUARD
// @origin commons-lang-3.12.0
// @project Apache Commons Lang
// @file StringUtils.java
// @method concat(String, String, String)
// @license Apache-2.0
// @sonarCCBefore 6
// @description Método con triple if anidado de guardas null — elegible completo con delta -4

/**
 * Patrón representativo de Apache Commons Lang 3.12.0 — StringUtils.
 * Contiene un triple if anidado: tres guardas null consecutivas, todas con condiciones puras.
 * Caso ELEGIBLE COMPLETO: las tres condiciones son null checks puros, ningún if tiene else,
 * cada bloque externo contiene únicamente el if inmediatamente interior.
 * La herramienta aplica dos pases hasta punto fijo y reduce CC de 6 a 2 (delta = -4).
 * Este caso ilustra el mayor impacto potencial del enfoque en el corpus real.
 */
class RealCommonsLangTripleGuard {

    String concat(String a, String b, String sep) {
        if (a != null) {
            if (b != null) {
                if (sep != null) {
                    return a + sep + b;
                }
            }
        }
        return "";
    }
}
