// @caseId FASTJSON_J_S_O_N_SERIALIZER_WRITE_REFERENCE
// @origin fastjson
// @project fastjson
// @file JSONSerializer.java
// @method writeReference(Object)
// @license [PENDIENTE]
// @sonarCCBefore 9
// @sonarCCAfter 8   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class FastjsonJSONSerializerWriteReference {
    public void writeReference(Object object) {
        SerialContext context = this.context;
        Object current = context.object;
        if (object == current) {
            out.write("{\"$ref\":\"@\"}");
            return;
        }
        SerialContext parentContext = context.parent;
        if (parentContext != null) {
            if (object == parentContext.object) {
                out.write("{\"$ref\":\"..\"}");
                return;
            }
        }
        SerialContext rootContext = context;
        for (; ; ) {
            if (rootContext.parent == null) {
                break;
            }
            rootContext = rootContext.parent;
        }
        if (object == rootContext.object) {
            out.write("{\"$ref\":\"$\"}");
        } else {
            out.write("{\"$ref\":\"");
            String path = references.get(object).toString();
            out.write(path);
            out.write("\"}");
        }
    }
}
