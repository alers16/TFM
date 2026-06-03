public class JACKSON_DATABIND_MERGING_SETTABLE_BEAN_PROPERTY_DESERIALIZE_SET_AND_RETURN {
@Override
public Object deserializeSetAndReturn(JsonParser p, DeserializationContext ctxt, Object instance) throws JacksonException {
    Object oldValue = _accessor.getValue(instance);
    Object newValue;
    // 20-Oct-2016, tatu: Couple of possibilities of how to proceed; for
    //    now, default to "normal" handling without merging
    if (oldValue == null) {
        newValue = delegate.deserialize(p, ctxt);
    } else {
        newValue = delegate.deserializeWith(p, ctxt, oldValue);
    }
    // 23-Oct-2016, tatu: One possible complication here; should we always
    //    try calling setter on builder? Presumably should not be required,
    //    but may need to revise
    if (newValue != oldValue && newValue != null) {
        return delegate.setAndReturn(ctxt, instance, newValue);
    }
    return instance;
}
}

