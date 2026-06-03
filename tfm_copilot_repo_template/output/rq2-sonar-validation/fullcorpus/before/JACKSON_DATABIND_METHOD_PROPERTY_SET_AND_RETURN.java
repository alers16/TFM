public class JACKSON_DATABIND_METHOD_PROPERTY_SET_AND_RETURN {
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

