public class JACKSON_DATABIND_STD_VALUE_INSTANTIATOR_CREATE_USING_DELEGATE {
@Override
public Object createUsingDelegate(DeserializationContext ctxt, Object delegate) throws JacksonException {
    // 04-Oct-2016, tatu: Need delegation to work around [databind#1392]...
    if (_delegateCreator == null && _arrayDelegateCreator != null) {
        return _createUsingDelegate(_arrayDelegateCreator, _arrayDelegateArguments, ctxt, delegate);
    }
    return _createUsingDelegate(_delegateCreator, _delegateArguments, ctxt, delegate);
}
}

