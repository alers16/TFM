// @caseId JACKSON_DATABIND_ENUM_RESOLVER_FIND_ENUM
// @origin jackson-databind
// @project jackson-databind
// @file EnumResolver.java
// @method findEnum(String)
// @license [PENDIENTE]
// @sonarCCBefore 3
// @sonarCCAfter 2   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonDatabindEnumResolverFindEnum {
    /*
        /**********************************************************************
        /* Public API
        /**********************************************************************
         */
    public Enum<?> findEnum(String key) {
        Enum<?> en = _enumsById.get(key);
        if (en == null) {
            if (_isIgnoreCase) {
                return _findEnumIgnoreCase(key);
            }
        }
        return en;
    }
}
