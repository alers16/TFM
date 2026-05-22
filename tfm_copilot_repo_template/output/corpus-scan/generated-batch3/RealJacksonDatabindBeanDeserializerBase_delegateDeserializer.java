// @caseId JACKSON_DATABIND_BEAN_DESERIALIZER_BASE_DELEGATE_DESERIALIZER
// @origin jackson-databind
// @project jackson-databind
// @file BeanDeserializerBase.java
// @method _delegateDeserializer(JsonParser)
// @license [PENDIENTE]
// @sonarCCBefore 7
// @sonarCCAfter 5   (estimado; CC delta=-2)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonDatabindBeanDeserializerBaseDelegateDeserializer {
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
