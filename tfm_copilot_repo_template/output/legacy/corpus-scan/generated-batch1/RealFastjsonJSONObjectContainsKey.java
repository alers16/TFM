// @caseId FASTJSON_J_S_O_N_OBJECT_CONTAINS_KEY
// @origin fastjson
// @project fastjson
// @file JSONObject.java
// @method containsKey(Object)
// @license [PENDIENTE]
// @sonarCCBefore 4
// @sonarCCAfter 3   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class FastjsonJSONObjectContainsKey {
    public boolean containsKey(Object key) {
        boolean result = map.containsKey(key);
        if (!result) {
            if (key instanceof Number || key instanceof Character || key instanceof Boolean || key instanceof UUID) {
                result = map.containsKey(key.toString());
            }
        }
        return result;
    }
}
