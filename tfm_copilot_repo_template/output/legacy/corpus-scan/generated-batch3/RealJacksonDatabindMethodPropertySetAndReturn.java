// @caseId JACKSON_DATABIND_METHOD_PROPERTY_SET_AND_RETURN
// @origin jackson-databind
// @project jackson-databind
// @file MethodProperty.java
// @method setAndReturn(DeserializationContext, Object, Object)
// @license [PENDIENTE]
// @sonarCCBefore 5
// @sonarCCAfter 4   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonDatabindMethodPropertySetAndReturn {
    @Override
    public Object setAndReturn(DeserializationContext ctxt, Object instance, Object value) throws JacksonException {
        if (value == null) {
            if (_skipNulls) {
                return instance;
            }
        }
        try {
            Object result = _setterReturn.get().invokeExact(instance, value);
            return (result == null) ? instance : result;
        } catch (Throwable e) {
            _throwAsJacksonE(ctxt.getParser(), e, value);
            return null;
        }
    }
}
