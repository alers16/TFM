// @caseId FASTJSON_JAVA_BEAN_DESERIALIZER_DESERIALZE
// @origin fastjson
// @project fastjson
// @file JavaBeanDeserializer.java
// @method deserialze(DefaultJSONParser, Type, Object, Object, int, int[])
// @license [PENDIENTE]
// @sonarCCBefore 582
// @sonarCCAfter 579   (estimado; CC delta=-3)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class FastjsonJavaBeanDeserializerDeserialze {
    public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        return deserialze(parser, type, fieldName, 0);
    }
}
