public class JACKSON_DATABIND_OBJECT_ARRAY_SERIALIZER_CREATE_CONTEXTUAL {
/*
        /**********************************************************************
        /* Post-processing
        /**********************************************************************
         */
@Override
public ValueSerializer<?> createContextual(SerializationContext ctxt, BeanProperty property) {
    TypeSerializer vts = _valueTypeSerializer;
    if (vts != null) {
        // need to contextualize
        vts = vts.forProperty(ctxt, property);
    }
    ValueSerializer<?> ser = null;
    Boolean unwrapSingle = null;
    // First: if we have a property, may have property-annotation overrides
    if (property != null) {
        AnnotatedMember m = property.getMember();
        final AnnotationIntrospector intr = ctxt.getAnnotationIntrospector();
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
    // [databind#124]: May have a content converter
    ser = findContextualConvertingSerializer(ctxt, property, ser);
    if (ser == null && _elementType != null) {
        if (_staticTyping && !_elementType.isJavaLangObject() && !_hasDynamicTypingOverride(ctxt, property)) {
            ser = ctxt.findContentValueSerializer(_elementType, property);
        }
    }
    // [databind#5515]: Handle content inclusion for arrays
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
    return _withResolved(property, vts, ser, unwrapSingle, valueToSuppress, suppressNulls);
}
}

