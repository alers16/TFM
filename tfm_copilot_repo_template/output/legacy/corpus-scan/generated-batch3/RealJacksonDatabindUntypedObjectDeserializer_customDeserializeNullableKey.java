// @caseId JACKSON_DATABIND_UNTYPED_OBJECT_DESERIALIZER_CUSTOM_DESERIALIZE_NULLABLE_KEY
// @origin jackson-databind
// @project jackson-databind
// @file UntypedObjectDeserializer.java
// @method _customDeserializeNullableKey(String, DeserializationContext)
// @license [PENDIENTE]
// @sonarCCBefore 3
// @sonarCCAfter 2   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonDatabindUntypedObjectDeserializerCustomDeserializeNullableKey {
    /**
     * Helper function to allow custom key deserialization with null handling.
     * Similar to {@link #_customDeserializeKey(String, DeserializationContext)}, but instead
     * only returns custom-deserialized key if key is not null.
     *
     * @returns Custom-deserialized key if both custom key deserializer is set and key is not null.
     *          Otherwise the original key.
     */
    private final String _customDeserializeNullableKey(String key, DeserializationContext ctxt) {
        if (_customKeyDeserializer != null) {
            if (key != null) {
                return (String) _customKeyDeserializer.deserializeKey(key, ctxt);
            }
        }
        return key;
    }
}
