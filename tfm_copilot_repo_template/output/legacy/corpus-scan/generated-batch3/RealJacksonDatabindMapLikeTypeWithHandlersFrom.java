// @caseId JACKSON_DATABIND_MAP_LIKE_TYPE_WITH_HANDLERS_FROM
// @origin jackson-databind
// @project jackson-databind
// @file MapLikeType.java
// @method withHandlersFrom(JavaType)
// @license [PENDIENTE]
// @sonarCCBefore 9
// @sonarCCAfter 7   (estimado; CC delta=-2)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonDatabindMapLikeTypeWithHandlersFrom {
    @Override
    public JavaType withHandlersFrom(JavaType src) {
        JavaType type = super.withHandlersFrom(src);
        JavaType srcKeyType = src.getKeyType();
        // "withKeyType()" not part of JavaType, hence must verify:
        if (type instanceof MapLikeType mapLikeType) {
            if (srcKeyType != null) {
                JavaType ct = _keyType.withHandlersFrom(srcKeyType);
                if (ct != _keyType) {
                    type = mapLikeType.withKeyType(ct);
                }
            }
        }
        JavaType srcCt = src.getContentType();
        if (srcCt != null) {
            JavaType ct = _valueType.withHandlersFrom(srcCt);
            if (ct != _valueType) {
                type = type.withContentType(ct);
            }
        }
        return type;
    }
}
