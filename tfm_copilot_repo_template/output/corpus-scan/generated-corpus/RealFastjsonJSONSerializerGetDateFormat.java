// @caseId FASTJSON_J_S_O_N_SERIALIZER_GET_DATE_FORMAT
// @origin fastjson
// @project fastjson
// @file JSONSerializer.java
// @method getDateFormat()
// @license [PENDIENTE]
// @sonarCCBefore 3
// @sonarCCAfter 2   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class FastjsonJSONSerializerGetDateFormat {
    public DateFormat getDateFormat() {
        if (dateFormat == null) {
            if (dateFormatPattern != null) {
                dateFormat = this.generateDateFormat(dateFormatPattern);
            }
        }
        return dateFormat;
    }
}
