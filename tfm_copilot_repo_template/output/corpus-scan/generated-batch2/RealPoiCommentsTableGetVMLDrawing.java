// @caseId POI_COMMENTS_TABLE_GET_V_M_L_DRAWING
// @origin poi
// @project poi
// @file CommentsTable.java
// @method getVMLDrawing(Sheet, boolean)
// @license [PENDIENTE]
// @sonarCCBefore 3
// @sonarCCAfter 2   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class PoiCommentsTableGetVMLDrawing {
    private XSSFVMLDrawing getVMLDrawing(Sheet sheet, boolean autocreate) {
        if (vmlDrawing == null) {
            if (sheet instanceof OoxmlSheetExtensions) {
                vmlDrawing = ((OoxmlSheetExtensions) sheet).getVMLDrawing(autocreate);
            }
        }
        return vmlDrawing;
    }
}
