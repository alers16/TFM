// @caseId JACKSON_DATABIND_TYPE_RESOLVER_PROVIDER_FIND_TYPE_RESOLVER
// @origin jackson-databind
// @project jackson-databind
// @file TypeResolverProvider.java
// @method _findTypeResolver(MapperConfig<?>, Annotated, JavaType)
// @license [PENDIENTE]
// @sonarCCBefore 21
// @sonarCCAfter 20   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonDatabindTypeResolverProviderFindTypeResolver {
    /*
        /**********************************************************************
        /* Helper methods
        /**********************************************************************
         */
    protected TypeResolverBuilder<?> _findTypeResolver(MapperConfig<?> config, Annotated ann, JavaType baseType) {
        final AnnotationIntrospector ai = config.getAnnotationIntrospector();
        JsonTypeInfo.Value typeInfo = ai.findPolymorphicTypeInfo(config, ann);
        // First: maybe we have explicit type resolver?
        TypeResolverBuilder<?> b;
        Object customResolverOb = ai.findTypeResolverBuilder(config, ann);
        if (customResolverOb != null) {
            // 08-Mar-2018, tatu: Should `NONE` block custom one? Or not?
            if ((typeInfo != null) && (typeInfo.getIdType() == JsonTypeInfo.Id.NONE)) {
                return null;
            }
            if (customResolverOb instanceof Class<?>) {
                @SuppressWarnings("unchecked")
                Class<TypeResolverBuilder<?>> cls = (Class<TypeResolverBuilder<?>>) customResolverOb;
                b = config.typeResolverBuilderInstance(ann, cls);
            } else {
                b = (TypeResolverBuilder<?>) customResolverOb;
            }
        } else {
            // if not, use standard one, but only if indicated by annotations
            if (typeInfo == null) {
                return null;
            }
            // bit special; must return 'marker' to block use of default typing:
            if (typeInfo.getIdType() == JsonTypeInfo.Id.NONE) {
                return NO_RESOLVER;
            }
            JavaType detectedBaseType;
            // 13-Aug-2011, tatu: One complication; external id
            //   only works for properties; so if declared for a Class, we will need
            //   to map it to "PROPERTY" instead of "EXTERNAL_PROPERTY"
            if (ann instanceof AnnotatedClass annotatedClass) {
                JsonTypeInfo.As inclusion = typeInfo.getInclusionType();
                if (inclusion == JsonTypeInfo.As.EXTERNAL_PROPERTY) {
                    typeInfo = typeInfo.withInclusionType(JsonTypeInfo.As.PROPERTY);
                }
                detectedBaseType = ai.findPolymorphicBaseType(config, annotatedClass, typeInfo, baseType);
            } else {
                // when method/field annotated, declared type MUST be intended base type
                detectedBaseType = null;
            }
            b = _constructStdTypeResolverBuilder(config, typeInfo, baseType, detectedBaseType);
        }
        // Does it define a custom type id resolver?
        Object customIdResolverOb = ai.findTypeIdResolver(config, ann);
        TypeIdResolver idResolver = null;
        if (customIdResolverOb != null) {
            if (customIdResolverOb instanceof Class<?>) {
                @SuppressWarnings("unchecked")
                Class<TypeIdResolver> cls = (Class<TypeIdResolver>) customIdResolverOb;
                idResolver = config.typeIdResolverInstance(ann, cls);
                idResolver.init(baseType);
            }
        }
        b = b.init(typeInfo, idResolver);
        return b;
    }
}
