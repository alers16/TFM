public class JACKSON_DATABIND_ANNOTATED_WITH_PARAMS_GET_PARAMETER_ANNOTATIONS {
/*
        /**********************************************************************
        /* Extended API
        /**********************************************************************
         */
public final AnnotationMap getParameterAnnotations(int index) {
    if (_paramAnnotations != null && index >= 0 && index < _paramAnnotations.length) {
        return _paramAnnotations[index];
    }
    return null;
}
}

