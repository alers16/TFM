public class FASTJSON_JAVA_BEAN_INFO_BUILD {
public static JavaBeanInfo build(Class<?> clazz, Type type, PropertyNamingStrategy propertyNamingStrategy) {
    return build(clazz, type, propertyNamingStrategy, false, TypeUtils.compatibleWithJavaBean, false);
}
}

