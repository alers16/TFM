public class JACKSON_DATABIND_ENUM_DESERIALIZER_CREATE_CONTEXTUAL {
@Override
public ValueDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
    Boolean caseInsensitive = _caseInsensitive;
    Boolean useDefaultValueForUnknownEnum = _useDefaultValueForUnknownEnum;
    Boolean useNullForUnknownEnum = _useNullForUnknownEnum;
    boolean useNumericIndexForNumbers = _useNumericIndexForNumbers;
    JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
    if (format != null) {
        // [databind#5814]: check both ACCEPT_CASE_INSENSITIVE_VALUES (primary) and
        //   ACCEPT_CASE_INSENSITIVE_PROPERTIES (legacy, for backwards-compatibility)
        Boolean ci = format.getFeature(JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_VALUES);
        if (ci == null) {
            ci = format.getFeature(JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        }
        if (ci != null) {
            caseInsensitive = ci;
        }
        Boolean b = format.getFeature(JsonFormat.Feature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE);
        if (b != null) {
            useDefaultValueForUnknownEnum = b;
        }
        b = format.getFeature(JsonFormat.Feature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
        if (b != null) {
            useNullForUnknownEnum = b;
        }
        JsonFormat.Shape shape = format.getShape();
        if (shape != null && shape != JsonFormat.Shape.ANY && shape != JsonFormat.Shape.SCALAR) {
            useNumericIndexForNumbers = shape.isNumeric() || shape == JsonFormat.Shape.ARRAY;
        }
    }
    return withResolved(caseInsensitive, useDefaultValueForUnknownEnum, useNullForUnknownEnum, useNumericIndexForNumbers);
}
}

