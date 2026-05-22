// @caseId JACKSON_DATABIND_METHOD_PROPERTY_SET
// @origin jackson-databind
// @project jackson-databind
// @file MethodProperty.java
// @method set(DeserializationContext, Object, Object)
// @license [PENDIENTE]
// @sonarCCBefore 4
// @sonarCCAfter 3   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonDatabindMethodPropertySet {
    @Override
    public final void set(DeserializationContext ctxt, Object instance, Object value) throws JacksonException {
        if (value == null) {
            if (_skipNulls) {
                return;
            }
        }
        try {
            _setter.get().invokeExact(instance, value);
        } catch (Throwable e) {
            _throwAsJacksonE(ctxt.getParser(), e, value);
        }
    }
}
