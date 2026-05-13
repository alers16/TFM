// @caseId REAL_COMMONS_COLLECTIONS_ISEMPTY
// @origin commons-collections-4.4
// @project Apache Commons Collections
// @file ArrayUtils.java
// @method hasElements(Object[])
// @license Apache-2.0
// @sonarCCBefore 3
// @description Método con if-null + if-length sobre array — patrón elegible clásico de doble guarda

/**
 * Patrón representativo de Apache Commons Collections 4.4 — ArrayUtils.
 * Comprueba que el array no es null y luego su longitud.
 * Caso ELEGIBLE: ambas condiciones son puras (array.length es acceso a campo,
 * no llamada a método). Combinable directamente.
 */
class RealCommonsCollectionsIsEmpty {

    boolean hasElements(Object[] items) {
        if (items != null) {
            if (items.length > 0) {
                return true;
            }
        }
        return false;
    }
}
