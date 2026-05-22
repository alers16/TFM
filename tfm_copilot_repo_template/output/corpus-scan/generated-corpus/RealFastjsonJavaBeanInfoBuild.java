// @caseId FASTJSON_JAVA_BEAN_INFO_BUILD
// @origin fastjson
// @project fastjson
// @file JavaBeanInfo.java
// @method build(Class<?>, Type, PropertyNamingStrategy, boolean, boolean, boolean)
// @license [PENDIENTE]
// @sonarCCBefore 503
// @sonarCCAfter 498   (estimado; CC delta=-5)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class FastjsonJavaBeanInfoBuild {
    public static JavaBeanInfo build(Class<?> clazz, Type type, PropertyNamingStrategy propertyNamingStrategy) {
        return build(clazz, type, propertyNamingStrategy, false, TypeUtils.compatibleWithJavaBean, false);
    }
}
