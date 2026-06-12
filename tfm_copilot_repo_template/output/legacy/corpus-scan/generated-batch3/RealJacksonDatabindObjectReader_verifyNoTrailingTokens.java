// @caseId JACKSON_DATABIND_OBJECT_READER_VERIFY_NO_TRAILING_TOKENS
// @origin jackson-databind
// @project jackson-databind
// @file ObjectReader.java
// @method _verifyNoTrailingTokens(JsonParser, DeserializationContext, JavaType)
// @license [PENDIENTE]
// @sonarCCBefore 6
// @sonarCCAfter 4   (estimado; CC delta=-2)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonDatabindObjectReaderVerifyNoTrailingTokens {
    protected final void _verifyNoTrailingTokens(JsonParser p, DeserializationContext ctxt, JavaType bindType) throws JacksonException {
        JsonToken t = p.nextToken();
        if (t != null) {
            Class<?> bt = ClassUtil.rawClass(bindType);
            if (bt == null) {
                if (_valueToUpdate != null) {
                    bt = _valueToUpdate.getClass();
                }
            }
            ctxt.reportTrailingTokens(bt, p, t);
        }
    }
}
