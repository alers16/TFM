// @caseId FASTJSON_J_S_O_N_OBJECT_GET
// @origin fastjson
// @project fastjson
// @file JSONObject.java
// @method get(Object)
// @license [PENDIENTE]
// @sonarCCBefore 4
// @sonarCCAfter 3   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class FastjsonJSONObjectGet {
    public Object get(Object key) {
        Object val = map.get(key);
        if (val == null) {
            if (key instanceof Number || key instanceof Character || key instanceof Boolean || key instanceof UUID) {
                val = map.get(key.toString());
            }
        }
        return val;
    }
}
