// @caseId FASTJSON_DEFAULT_J_S_O_N_PARSER_PARSE_ARRAY
// @origin fastjson
// @project fastjson
// @file DefaultJSONParser.java
// @method parseArray(Type[])
// @license [PENDIENTE]
// @sonarCCBefore 76
// @sonarCCAfter 71   (estimado; CC delta=-5)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class FastjsonDefaultJSONParserParseArray {
    public <T> List<T> parseArray(Class<T> clazz) {
        List<T> array = new ArrayList<T>();
        parseArray(clazz, array);
        return array;
    }
}
