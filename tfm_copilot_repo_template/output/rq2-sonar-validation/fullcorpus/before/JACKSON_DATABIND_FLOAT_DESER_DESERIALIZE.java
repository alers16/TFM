public class JACKSON_DATABIND_FLOAT_DESER_DESERIALIZE {
@Override
public T deserialize(JsonParser p, DeserializationContext ctxt, T existing) throws JacksonException {
    T newValue = deserialize(p, ctxt);
    if (existing == null) {
        return newValue;
    }
    int len = Array.getLength(existing);
    if (len == 0) {
        return newValue;
    }
    return _concat(existing, newValue);
}
}

