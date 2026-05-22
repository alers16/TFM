// @caseId FASTJSON_JAVA_BEAN_SERIALIZER_WRITE
// @origin fastjson
// @project fastjson
// @file JavaBeanSerializer.java
// @method write(JSONSerializer, Object, Object, Type, int, boolean)
// @license [PENDIENTE]
// @sonarCCBefore 251
// @sonarCCAfter 247   (estimado; CC delta=-4)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class FastjsonJavaBeanSerializerWrite {
    public //
    void //
    write(//
    JSONSerializer serializer, //
    Object object, //
    Object fieldName, //
    Type fieldType, int features) throws IOException {
        write(serializer, object, fieldName, fieldType, features, false);
    }
}
