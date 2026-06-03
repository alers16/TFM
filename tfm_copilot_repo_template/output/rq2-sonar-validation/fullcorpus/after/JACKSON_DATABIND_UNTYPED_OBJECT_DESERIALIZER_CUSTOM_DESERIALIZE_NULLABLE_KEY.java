public class JACKSON_DATABIND_UNTYPED_OBJECT_DESERIALIZER_CUSTOM_DESERIALIZE_NULLABLE_KEY {
/**
 * Helper function to allow custom key deserialization with null handling.
 * Similar to {@link #_customDeserializeKey(String, DeserializationContext)}, but instead
 * only returns custom-deserialized key if key is not null.
 *
 * @returns Custom-deserialized key if both custom key deserializer is set and key is not null.
 *          Otherwise the original key.
 */
private final String _customDeserializeNullableKey(String key, DeserializationContext ctxt) {
    if (_customKeyDeserializer != null && key != null) {
        return (String) _customKeyDeserializer.deserializeKey(key, ctxt);
    }
    return key;
}
}

