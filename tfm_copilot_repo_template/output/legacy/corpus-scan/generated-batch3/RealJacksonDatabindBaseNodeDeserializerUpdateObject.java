// @caseId JACKSON_DATABIND_BASE_NODE_DESERIALIZER_UPDATE_OBJECT
// @origin jackson-databind
// @project jackson-databind
// @file BaseNodeDeserializer.java
// @method updateObject(JsonParser, DeserializationContext, ObjectNode, ContainerStack)
// @license [PENDIENTE]
// @sonarCCBefore 33
// @sonarCCAfter 29   (estimado; CC delta=-4)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonDatabindBaseNodeDeserializerUpdateObject {
    /**
     * Alternate deserialization method that is to update existing {@link ObjectNode}
     * if possible.
     */
    protected final JsonNode updateObject(JsonParser p, DeserializationContext ctxt, final ObjectNode node, final ContainerStack stack) throws JacksonException {
        String key;
        if (p.isExpectedStartObjectToken()) {
            key = p.nextName();
        } else {
            if (!p.hasToken(JsonToken.PROPERTY_NAME)) {
                return deserialize(p, ctxt);
            }
            key = p.currentName();
        }
        final JsonNodeFactory nodeFactory = ctxt.getNodeFactory();
        for (; key != null; key = p.nextName()) {
            // If not, fall through to regular handling
            JsonToken t = p.nextToken();
            // First: see if we can merge things:
            JsonNode old = node.get(key);
            if (old != null) {
                if (old instanceof ObjectNode objectNode) {
                    // [databind#3056]: merging only if had Object and
                    // getting an Object
                    if ((t == JsonToken.START_OBJECT) && _mergeObjects) {
                        JsonNode newValue = updateObject(p, ctxt, objectNode, stack);
                        if (newValue != old) {
                            node.set(key, newValue);
                        }
                        continue;
                    }
                } else if (old instanceof ArrayNode arrayNode) {
                    // [databind#3056]: related to Object handling, ensure
                    // Array values also match for mergeability
                    if ((t == JsonToken.START_ARRAY) && _mergeArrays) {
                        // 28-Mar-2021, tatu: We'll only append entries so not very different
                        //    from "regular" deserializeArray...
                        _deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, arrayNode);
                        continue;
                    }
                }
            }
            if (t == null) {
                // can this ever occur?
                t = JsonToken.NOT_AVAILABLE;
            }
            JsonNode value;
            switch(t.id()) {
                case JsonTokenId.ID_START_OBJECT:
                    value = _deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, nodeFactory.objectNode());
                    break;
                case JsonTokenId.ID_START_ARRAY:
                    value = _deserializeContainerNoRecursion(p, ctxt, nodeFactory, stack, nodeFactory.arrayNode());
                    break;
                case JsonTokenId.ID_STRING:
                    value = nodeFactory.stringNode(p.getString());
                    break;
                case JsonTokenId.ID_NUMBER_INT:
                    value = _fromInt(p, ctxt, nodeFactory);
                    break;
                case JsonTokenId.ID_TRUE:
                    value = nodeFactory.booleanNode(true);
                    break;
                case JsonTokenId.ID_FALSE:
                    value = nodeFactory.booleanNode(false);
                    break;
                case JsonTokenId.ID_NULL:
                    // 20-Mar-2022, tatu: [databind#3421] Allow skipping `null`s from JSON
                    if (!ctxt.isEnabled(JsonNodeFeature.READ_NULL_PROPERTIES)) {
                        continue;
                    }
                    value = nodeFactory.nullNode();
                    break;
                default:
                    value = _deserializeRareScalar(p, ctxt);
            }
            // 15-Feb-2021, tatu: I don't think this should have been called
            //   on update case (was until 2.12.2) and was simply result of
            //   copy-paste.
            /*
                if (old != null) {
                    _handleDuplicateProperty(p, ctxt, nodeFactory,
                            key, node, old, value);
                }
                */
            node.set(key, value);
        }
        return node;
    }
}
