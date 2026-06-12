// @caseId FASTJSON_FIELD_SERIALIZER_GET_PROPERTY_VALUE
// @origin fastjson
// @project fastjson
// @file FieldSerializer.java
// @method getPropertyValue(Object)
// @license [PENDIENTE]
// @sonarCCBefore 5
// @sonarCCAfter 3   (estimado; CC delta=-2)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class FastjsonFieldSerializerGetPropertyValue {
    public Object getPropertyValue(Object object) throws InvocationTargetException, IllegalAccessException {
        Object propertyValue = fieldInfo.get(object);
        if (format != null && propertyValue != null) {
            if (fieldInfo.fieldClass == java.util.Date.class || fieldInfo.fieldClass == java.sql.Date.class) {
                SimpleDateFormat dateFormat = new SimpleDateFormat(format, JSON.defaultLocale);
                dateFormat.setTimeZone(JSON.defaultTimeZone);
                return dateFormat.format(propertyValue);
            }
        }
        return propertyValue;
    }
}
