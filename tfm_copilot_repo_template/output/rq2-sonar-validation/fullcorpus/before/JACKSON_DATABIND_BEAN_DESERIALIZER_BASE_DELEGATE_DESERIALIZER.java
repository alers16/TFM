public class JACKSON_DATABIND_BEAN_DESERIALIZER_BASE_DELEGATE_DESERIALIZER {
/**
 * Alternate to {@link #_delegateDeserializer()} which will only consider
 * {@code _arrayDelegateDeserializer} if given {@link JsonParser} points to
 * {@link JsonToken#START_ARRAY} token.
 */
protected final ValueDeserializer<Object> _delegateDeserializer(JsonParser p) {
    if (_delegateDeserializer == null) {
        // Note! Will not call `JsonParser.isExpectedArrayToken()` as that could
        // "transform" `JsonToken.START_OBJECT` into `JsonToken.START_ARRAY` and
        // here there is no strong expectation of Array value
        if (_arrayDelegateDeserializer != null) {
            // Alas, need bit elaborate logic: either JSON Array, OR no
            // Properties-based Creator
            if (p.hasToken(JsonToken.START_ARRAY) || (_propertyBasedCreator == null)) {
                return _arrayDelegateDeserializer;
            }
        }
    }
    return _delegateDeserializer;
}
}

