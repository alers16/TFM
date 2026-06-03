public class JACKSON_DATABIND_OBJECT_READER_VERIFY_NO_TRAILING_TOKENS {
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

