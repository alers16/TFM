// @caseId FASTJSON_PARSER_CONFIG_CHECK_AUTO_TYPE
// @origin fastjson
// @project fastjson
// @file ParserConfig.java
// @method checkAutoType(String, Class<?>, int)
// @license [PENDIENTE]
// @sonarCCBefore 114
// @sonarCCAfter 113   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class FastjsonParserConfigCheckAutoType {
    public Class<?> checkAutoType(Class type) {
        if (get(type) != null) {
            return type;
        }
        return checkAutoType(type.getName(), null, JSON.DEFAULT_PARSER_FEATURE);
    }
}
