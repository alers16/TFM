public class JACKSON_DATABIND_STD_VALUE_INSTANTIATOR_CREATE_USING_ARRAY_DELEGATE {
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

