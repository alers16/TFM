// @caseId JACKSON_DATABIND_OBJECT_MAPPER_TREE_TO_VALUE
// @origin jackson-databind
// @project jackson-databind
// @file ObjectMapper.java
// @method treeToValue(JsonNode, JavaType)
// @license [PENDIENTE]
// @sonarCCBefore 10
// @sonarCCAfter 8   (estimado; CC delta=-2)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonDatabindObjectMapperTreeToValue {
    /*
        /**********************************************************************
        /* Public API: Additional Tree Model support beyond TreeCodec
        /**********************************************************************
         */
    /**
     *  Convenience conversion method that will bind data given JSON tree
     *  contains into specific value (usually bean) type.
     * <p>
     *  Functionally equivalent to:
     * <pre>
     *    objectMapper.convertValue(n, valueClass);
     * </pre>
     * <p>
     *  Typed overload added in 3.1
     */
    @SuppressWarnings("unchecked")
    public <T> T treeToValue(JsonNode n, Class<T> valueType) throws JacksonException {
        if (n == null) {
            return null;
        }
        // 25-Jan-2019, tatu: [databind#2220] won't prevent existing coercions here
        // Simple cast when we just want to cast to, say, ObjectNode
        if (JsonNode.class.isAssignableFrom(valueType) && valueType.isAssignableFrom(n.getClass())) {
            return (T) n;
        }
        final JsonToken tt = n.asToken();
        // 20-Apr-2016, tatu: Another thing: for VALUE_EMBEDDED_OBJECT, assume similar
        //    short-cut coercion
        if (tt == JsonToken.VALUE_EMBEDDED_OBJECT) {
            if (n instanceof POJONode pNode) {
                Object ob = pNode.getPojo();
                if ((ob == null) || valueType.isInstance(ob)) {
                    return (T) ob;
                }
            }
        }
        // 22-Aug-2019, tatu: [databind#2430] Consider "null node" (minor optimization)
        // 08-Dec-2020, tatu: Alas, lead to [databind#2972], optimization gets complicated
        //    so leave out for now...
        /*if (tt == JsonToken.VALUE_NULL) {
                 return null;
            }*/
        return readValue(treeAsTokens(n), valueType);
    }
}
