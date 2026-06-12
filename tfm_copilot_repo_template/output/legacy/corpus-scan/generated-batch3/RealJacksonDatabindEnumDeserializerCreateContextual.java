// @caseId JACKSON_DATABIND_ENUM_DESERIALIZER_CREATE_CONTEXTUAL
// @origin jackson-databind
// @project jackson-databind
// @file EnumDeserializer.java
// @method createContextual(DeserializationContext, BeanProperty)
// @license [PENDIENTE]
// @sonarCCBefore 16
// @sonarCCAfter 13   (estimado; CC delta=-3)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonDatabindEnumDeserializerCreateContextual {
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
            if (shape != null) {
                if (shape != JsonFormat.Shape.ANY && shape != JsonFormat.Shape.SCALAR) {
                    useNumericIndexForNumbers = shape.isNumeric() || shape == JsonFormat.Shape.ARRAY;
                }
            }
        }
        return withResolved(caseInsensitive, useDefaultValueForUnknownEnum, useNullForUnknownEnum, useNumericIndexForNumbers);
    }
}
