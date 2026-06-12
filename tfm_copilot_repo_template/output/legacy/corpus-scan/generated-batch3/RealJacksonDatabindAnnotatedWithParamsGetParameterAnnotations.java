// @caseId JACKSON_DATABIND_ANNOTATED_WITH_PARAMS_GET_PARAMETER_ANNOTATIONS
// @origin jackson-databind
// @project jackson-databind
// @file AnnotatedWithParams.java
// @method getParameterAnnotations(int)
// @license [PENDIENTE]
// @sonarCCBefore 4
// @sonarCCAfter 2   (estimado; CC delta=-2)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonDatabindAnnotatedWithParamsGetParameterAnnotations {
    /*
        /**********************************************************************
        /* Extended API
        /**********************************************************************
         */
    public final AnnotationMap getParameterAnnotations(int index) {
        if (_paramAnnotations != null) {
            if (index >= 0 && index < _paramAnnotations.length) {
                return _paramAnnotations[index];
            }
        }
        return null;
    }
}
