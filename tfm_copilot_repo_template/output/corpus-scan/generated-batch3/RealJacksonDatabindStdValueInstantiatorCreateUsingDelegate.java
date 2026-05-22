// @caseId JACKSON_DATABIND_STD_VALUE_INSTANTIATOR_CREATE_USING_DELEGATE
// @origin jackson-databind
// @project jackson-databind
// @file StdValueInstantiator.java
// @method createUsingDelegate(DeserializationContext, Object)
// @license [PENDIENTE]
// @sonarCCBefore 3
// @sonarCCAfter 2   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonDatabindStdValueInstantiatorCreateUsingDelegate {
    @Override
    public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) throws JacksonException {
        // 04-Oct-2016, tatu: Need delegation to work around [databind#1392]...
        if (_delegateCreator == null) {
            if (_arrayDelegateCreator != null) {
                return _createUsingDelegate(_arrayDelegateCreator, _arrayDelegateArguments, ctxt, delegate);
            }
        }
        return _createUsingDelegate(_delegateCreator, _delegateArguments, ctxt, delegate);
    }
}
