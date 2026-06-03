public class JACKSON_DATABIND_ENUM_RESOLVER_FIND_ENUM {
/*
        /**********************************************************************
        /* Public API
        /**********************************************************************
         */
public Enum<?> findEnum(String key) {
    Enum<?> en = _enumsById.get(key);
    if (en == null && _isIgnoreCase) {
        return _findEnumIgnoreCase(key);
    }
    return en;
}
}

