public class JACKSON_DATABIND_TYPE_FACTORY_BINDINGS_FOR_SUBTYPE {
private TypeBindings _bindingsForSubtype(JavaType baseType, int typeParamCount, Class<?> subclass, boolean relaxedCompatibilityCheck) {
    PlaceholderForType[] placeholders = new PlaceholderForType[typeParamCount];
    for (int i = 0; i < typeParamCount; ++i) {
        placeholders[i] = new PlaceholderForType(i);
    }
    TypeBindings b = TypeBindings.create(subclass, placeholders);
    // First: pseudo-resolve to get placeholders in place:
    JavaType tmpSub = _fromClass(null, subclass, b);
    // Then find super-type
    JavaType baseWithPlaceholders = tmpSub.findSuperType(baseType.getRawClass());
    if (baseWithPlaceholders == null) {
        // should be found but...
        throw new IllegalArgumentException(String.format("Internal error: unable to locate supertype (%s) from resolved subtype %s", baseType.getRawClass().getName(), subclass.getName()));
    }
    // and traverse type hierarchies to both verify and to resolve placeholders
    String error = _resolveTypePlaceholders(baseType, baseWithPlaceholders);
    if (error != null && !relaxedCompatibilityCheck) {
        throw new IllegalArgumentException("Failed to specialize base type " + baseType.toCanonical() + " as " + subclass.getName() + ", problem: " + error);
    }
    final JavaType[] typeParams = new JavaType[typeParamCount];
    for (int i = 0; i < typeParamCount; ++i) {
        JavaType t = placeholders[i].actualType();
        // 18-Oct-2017, tatu: Looks like sometimes we have incomplete bindings (even if not
        //     common, it is possible if subtype is type-erased class with added type
        //     variable -- see test(s) with "bogus" type(s)).
        if (t == null) {
            t = unknownType();
        }
        typeParams[i] = t;
    }
    return TypeBindings.create(subclass, typeParams);
}
}

