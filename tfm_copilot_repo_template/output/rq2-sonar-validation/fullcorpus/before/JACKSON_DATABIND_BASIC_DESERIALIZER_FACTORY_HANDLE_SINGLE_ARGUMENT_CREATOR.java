public class JACKSON_DATABIND_BASIC_DESERIALIZER_FACTORY_HANDLE_SINGLE_ARGUMENT_CREATOR {
private boolean _handleSingleArgumentCreator(CreatorCollector creators, AnnotatedWithParams ctor, boolean isCreator, boolean isVisible) {
    // otherwise either 'simple' number, String, or general delegate:
    Class<?> type = ctor.getRawParameterType(0);
    if (type == String.class || type == CLASS_CHAR_SEQUENCE) {
        if (isCreator || isVisible) {
            creators.addStringCreator(ctor, isCreator);
        }
        return true;
    }
    if (type == int.class || type == Integer.class) {
        if (isCreator || isVisible) {
            creators.addIntCreator(ctor, isCreator);
        }
        return true;
    }
    if (type == long.class || type == Long.class) {
        if (isCreator || isVisible) {
            creators.addLongCreator(ctor, isCreator);
        }
        return true;
    }
    if (type == double.class || type == Double.class) {
        if (isCreator || isVisible) {
            creators.addDoubleCreator(ctor, isCreator);
        }
        return true;
    }
    if (type == boolean.class || type == Boolean.class) {
        if (isCreator || isVisible) {
            creators.addBooleanCreator(ctor, isCreator);
        }
        return true;
    }
    if (type == BigInteger.class) {
        if (isCreator || isVisible) {
            creators.addBigIntegerCreator(ctor, isCreator);
        }
    }
    if (type == BigDecimal.class) {
        if (isCreator || isVisible) {
            creators.addBigDecimalCreator(ctor, isCreator);
        }
    }
    // Delegating Creator ok iff it has @JsonCreator (etc)
    if (isCreator) {
        creators.addDelegatingCreator(ctor, isCreator, null, 0);
        return true;
    }
    return false;
}
}

