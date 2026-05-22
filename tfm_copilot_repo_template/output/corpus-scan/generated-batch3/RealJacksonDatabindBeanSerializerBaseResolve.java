// @caseId JACKSON_DATABIND_BEAN_SERIALIZER_BASE_RESOLVE
// @origin jackson-databind
// @project jackson-databind
// @file BeanSerializerBase.java
// @method resolve(SerializationContext)
// @license [PENDIENTE]
// @sonarCCBefore 48
// @sonarCCAfter 44   (estimado; CC delta=-4)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonDatabindBeanSerializerBaseResolve {
    /*
        /**********************************************************************
        /* Post-construction processing: resolvable, contextual
        /**********************************************************************
         */
    /**
     * We need to resolve dependant serializers here
     * to be able to properly handle cyclic type references.
     */
    @Override
    public void resolve(SerializationContext ctxt) {
        int filteredCount = (_filteredProps == null) ? 0 : _filteredProps.length;
        for (int i = 0, len = _props.length; i < len; ++i) {
            BeanPropertyWriter prop = _props[i];
            // let's start with null serializer resolution actually
            if (!prop.willSuppressNulls() && !prop.hasNullSerializer()) {
                ValueSerializer<Object> nullSer = ctxt.findNullValueSerializer(prop);
                if (nullSer != null) {
                    prop.assignNullSerializer(nullSer);
                    // also: remember to replace filtered property too?
                    if (i < filteredCount) {
                        BeanPropertyWriter w2 = _filteredProps[i];
                        if (w2 != null) {
                            w2.assignNullSerializer(nullSer);
                        }
                    }
                }
            }
            if (prop.hasSerializer()) {
                continue;
            }
            // [databind#124]: allow use of converters
            ValueSerializer<Object> ser = findConvertingSerializer(ctxt, prop);
            if (ser == null) {
                // Was the serialization type hard-coded? If so, use it
                JavaType type = prop.getSerializationType();
                // It not, we can use declared return type if and only if declared type is final:
                // if not, we don't really know the actual type until we get the instance.
                if (type == null) {
                    type = prop.getType();
                    // [databind#5615]: _nonTrivialBaseType now set in BeanPropertyWriter
                    // constructor to avoid race condition
                    if (!type.isFinal()) {
                        continue;
                    }
                }
                ser = ctxt.findPrimaryPropertySerializer(type, prop);
                // 04-Feb-2010, tatu: We may have stashed type serializer for content types
                //   too, earlier; if so, it's time to connect the dots here:
                if (type.isContainerType()) {
                    TypeSerializer typeSer = (TypeSerializer) type.getContentType().getTypeHandler();
                    if (typeSer != null) {
                        // for now, can do this only for standard containers...
                        if (ser instanceof StdContainerSerializer<?> serializer) {
                            // ugly casts... but necessary
                            @SuppressWarnings("unchecked")
                            ValueSerializer<Object> ser2 = (ValueSerializer<Object>) serializer.withValueTypeSerializer(typeSer);
                            ser = ser2;
                        }
                    }
                }
            }
            // and maybe replace filtered property too?
            if (i < filteredCount) {
                BeanPropertyWriter w2 = _filteredProps[i];
                if (w2 != null) {
                    w2.assignSerializer(ser);
                    // 17-Mar-2017, tatu: Typically will lead to chained call to original property,
                    //    which would lead to double set. Not a problem itself, except... unwrapping
                    //    may require work to be done, which does lead to an actual issue.
                    continue;
                }
            }
            prop.assignSerializer(ser);
        }
        // also, any-getter may need to be resolved
        for (int i = 0; i < _props.length; i++) {
            BeanPropertyWriter prop = _props[i];
            if (prop instanceof AnyGetterWriter anyGetterWriter) {
                anyGetterWriter.resolve(ctxt);
            }
        }
        // [databind#2883]: now that inner serializers (and their post-transformation
        // property names) are known, verify no unwrapped property clashes with any
        // other property
        _verifyNoUnwrappedPropertyConflict(ctxt);
    }
}
