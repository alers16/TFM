public class JACKSON_DATABIND_AS_ARRAY_SERIALIZER_BASE_CREATE_CONTEXTUAL {
/*
        /**********************************************************************
        /* Post-processing
        /**********************************************************************
         */
/**
 * This method is needed to resolve contextual annotations like
 * per-property overrides, as well as do recursive call
 * to <code>createContextual</code> of content serializer, if
 * known statically.
 */
@Override
public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty property) {
    TypeSerializer typeSer = _valueTypeSerializer;
    if (typeSer != null) {
        typeSer = typeSer.forProperty(ctxt, property);
    }
    ValueSerializer<?> ser = null;
    Boolean unwrapSingle = null;
    // First: if we have a property, may have property-annotation overrides
    if (property != null) {
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
        AnnotatedMember m = property.getMember();
        if (m != null) {
            ser = ctxt.serializerInstance(m, intr.findContentSerializer(ctxt.getConfig(), m));
        }
    }
    JsonFormat.Value format = findFormatOverrides(ctxt, property, handledType());
    if (format != null) {
        unwrapSingle = format.getFeature(JsonFormat.Feature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED);
    }
    if (ser == null) {
        ser = _elementSerializer;
    }
    // 18-Feb-2013, tatu: May have a content converter:
    ser = findContextualConvertingSerializer(ctxt, property, ser);
    if (ser == null) {
        // 30-Sep-2012, tatu: One more thing -- if explicit content type is annotated,
        //   we can consider it a static case as well.
        // [databind#1515]: but allow per-property or per-content-type DYNAMIC override
        if (_elementType != null) {
            if (_staticTyping && !_elementType.isJavaLangObject() && !_hasDynamicTypingOverride(ctxt, property)) {
                ser = ctxt.findContentValueSerializer(_elementType, property);
            }
        }
    }
    // Handle content inclusion (similar to MapSerializer lines 560-609)
    JsonInclude.Value inclV = findIncludeOverrides(ctxt, property, handledType());
    Object valueToSuppress = _suppressableValue;
    boolean suppressNulls = _suppressNulls;
    if (inclV != null) {
        JsonInclude.Include incl = inclV.getContentInclusion();
        if (incl != JsonInclude.Include.USE_DEFAULTS) {
            switch(incl) {
                case NON_DEFAULT:
                    valueToSuppress = BeanUtil.propertyDefaultValue(ctxt, _elementType);
                    suppressNulls = true;
                    if (valueToSuppress != null) {
                        if (valueToSuppress.getClass().isArray()) {
                            valueToSuppress = ArrayBuilders.getArrayComparator(valueToSuppress);
                        }
                    }
                    break;
                case NON_ABSENT:
                    suppressNulls = true;
                    valueToSuppress = MARKER_FOR_EMPTY;
                    break;
                case NON_EMPTY:
                    suppressNulls = true;
                    valueToSuppress = MARKER_FOR_EMPTY;
                    break;
                case CUSTOM:
                    valueToSuppress = ctxt.includeFilterInstance(null, inclV.getContentFilter());
                    if (valueToSuppress == null) {
                        suppressNulls = true;
                    } else {
                        suppressNulls = ctxt.includeFilterSuppressNulls(valueToSuppress);
                    }
                    break;
                case NON_NULL:
                    valueToSuppress = null;
                    suppressNulls = true;
                    break;
                case ALWAYS:
                default:
                    valueToSuppress = null;
                    suppressNulls = false;
                    break;
            }
        }
    }
    if ((ser != _elementSerializer) || (property != _property) || (_valueTypeSerializer != typeSer) || (!Objects.equals(_unwrapSingle, unwrapSingle)) || (!Objects.equals(valueToSuppress, _suppressableValue)) || (suppressNulls != _suppressNulls)) {
        return withResolved(property, typeSer, ser, unwrapSingle, valueToSuppress, suppressNulls);
    }
    return this;
}
}

