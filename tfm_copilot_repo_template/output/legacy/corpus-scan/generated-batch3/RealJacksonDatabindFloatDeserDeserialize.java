// @caseId JACKSON_DATABIND_FLOAT_DESER_DESERIALIZE
// @origin jackson-databind
// @project jackson-databind
// @file PrimitiveArrayDeserializers.java
// @method deserialize(JsonParser, DeserializationContext)
// @license [PENDIENTE]
// @sonarCCBefore 12
// @sonarCCAfter 10   (estimado; CC delta=-2)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonDatabindFloatDeserDeserialize {
    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt, T existing) throws JacksonException {
        T newValue = deserialize(p, ctxt);
        if (existing == null) {
            return newValue;
        }
        int len = Array.getLength(existing);
        if (len == 0) {
            return newValue;
        }
        return _concat(existing, newValue);
    }
}
