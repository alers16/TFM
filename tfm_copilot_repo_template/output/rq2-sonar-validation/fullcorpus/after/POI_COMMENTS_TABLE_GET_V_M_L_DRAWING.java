public class POI_COMMENTS_TABLE_GET_V_M_L_DRAWING {
private XSSFVMLDrawing getVMLDrawing(Sheet sheet, boolean autocreate) {
    if (vmlDrawing == null && sheet instanceof OoxmlSheetExtensions) {
        vmlDrawing = ((OoxmlSheetExtensions) sheet).getVMLDrawing(autocreate);
    }
    return vmlDrawing;
}
}

