// @caseId JACKSON_DATABIND_STD_VALUE_INSTANTIATOR_CREATE_USING_ARRAY_DELEGATE
// @origin jackson-databind
// @project jackson-databind
// @file StdValueInstantiator.java
// @method createUsingArrayDelegate(DeserializationContext, Object)
// @license [PENDIENTE]
// @sonarCCBefore 3
// @sonarCCAfter 2   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonDatabindStdValueInstantiatorCreateUsingArrayDelegate {
    @Override
    public Object createUsingArrayDelegate(DeserializationContext ctxt, Object delegate) throws JacksonException {
        if (_arrayDelegateCreator == null) {
            if (_delegateCreator != null) {
                // sanity-check; caller should check
                // fallback to the classic delegate creator
                return createUsingDelegate(ctxt, delegate);
            }
        }
        return _createUsingDelegate(_arrayDelegateCreator, _arrayDelegateArguments, ctxt, delegate);
    }
}
