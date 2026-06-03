public class JACKSON_DATABIND_AS_ARRAY_SERIALIZER_BASE_ACCEPT_JSON_FORMAT_VISITOR {
@Override
public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JacksonException {
    ValueSerializer<?> valueSer = _elementSerializer;
    if (valueSer == null) {
        // 19-Oct-2016, tatu: Apparently we get null for untyped/raw `EnumSet`s... not 100%
        //   sure what'd be the clean way but let's try this for now:
        if (_elementType != null) {
            valueSer = visitor.getContext().findContentValueSerializer(_elementType, _property);
        }
    }
    visitArrayFormat(visitor, typeHint, valueSer, _elementType);
}
}

