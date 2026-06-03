public class JACKSON_DATABIND_AS_ARRAY_SERIALIZER_BASE_ACCEPT_JSON_FORMAT_VISITOR {
@Override
public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JacksonException {
    ValueSerializer<?> valueSer = _elementSerializer;
    if (valueSer == null && _elementType != null) {
        valueSer = visitor.getContext().findContentValueSerializer(_elementType, _property);
    }
    visitArrayFormat(visitor, typeHint, valueSer, _elementType);
}
}

