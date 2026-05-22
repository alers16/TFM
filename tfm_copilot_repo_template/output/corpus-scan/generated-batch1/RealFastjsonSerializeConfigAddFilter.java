// @caseId FASTJSON_SERIALIZE_CONFIG_ADD_FILTER
// @origin fastjson
// @project fastjson
// @file SerializeConfig.java
// @method addFilter(Class<?>, SerializeFilter)
// @license [PENDIENTE]
// @sonarCCBefore 6
// @sonarCCAfter 4   (estimado; CC delta=-2)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class FastjsonSerializeConfigAddFilter {
    /**
     * add class level serialize filter
     * @since 1.2.10
     */
    public void addFilter(Class<?> clazz, SerializeFilter filter) {
        ObjectSerializer serializer = getObjectWriter(clazz);
        if (serializer instanceof SerializeFilterable) {
            SerializeFilterable filterable = (SerializeFilterable) serializer;
            if (this != SerializeConfig.globalInstance) {
                if (filterable == MapSerializer.instance) {
                    MapSerializer newMapSer = new MapSerializer();
                    this.put(clazz, newMapSer);
                    newMapSer.addFilter(filter);
                    return;
                }
            }
            filterable.addFilter(filter);
        }
    }
}
