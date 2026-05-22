// @caseId REAL_COMMONS_COLLECTIONS_INNER_ELSE
// @origin commons-collections-4.4
// @project Apache Commons Collections
// @file ArrayUtils.java
// @method validate(Object[], int)
// @license Apache-2.0
// @sonarCCBefore 4
// @description Método con if anidado cuyo if interno tiene rama else — no elegible por P4

/**
 * Patrón representativo de Apache Commons Collections 4.4 — ArrayUtils.
 * Contiene un if anidado donde el if INTERNO tiene rama else.
 * Caso NO ELEGIBLE: la condición interna tiene else (precondición P4 no satisfecha).
 * Razón de descarte: INNER_HAS_ELSE.
 * La semántica de la rama else impide combinar sin alterar el flujo de ejecución.
 */
class RealCommonsCollectionsInnerElse {

    boolean validate(Object[] items, int index) {
        if (items != null) {
            if (index >= 0) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }
}
