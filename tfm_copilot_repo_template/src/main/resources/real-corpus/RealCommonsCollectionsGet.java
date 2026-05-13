// @caseId REAL_COMMONS_COLLECTIONS_GET
// @origin commons-collections-4.4
// @project Apache Commons Collections
// @file MapUtils.java
// @method getObject(Map, Object)
// @license Apache-2.0
// @sonarCCBefore 3
// @description Método con if-null sobre mapa + if-containsKey — patrón defensivo en MapUtils

/**
 * Patrón representativo de Apache Commons Collections 4.4 — MapUtils.getObject.
 * Comprueba que el mapa no es null y que contiene la clave antes de acceder.
 * Caso NO ELEGIBLE: condición interna contiene llamada a método (containsKey).
 */
class RealCommonsCollectionsGet {

    Object getObject(java.util.Map<String, Object> map, String key) {
        if (map != null) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }
        return null;
    }
}
