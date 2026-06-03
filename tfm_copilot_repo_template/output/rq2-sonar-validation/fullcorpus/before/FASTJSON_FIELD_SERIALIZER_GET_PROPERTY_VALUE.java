public class FASTJSON_FIELD_SERIALIZER_GET_PROPERTY_VALUE {
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

