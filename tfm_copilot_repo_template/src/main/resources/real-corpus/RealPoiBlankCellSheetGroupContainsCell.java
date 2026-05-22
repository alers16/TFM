// @caseId POI_BLANK_CELL_SHEET_GROUP_CONTAINS_CELL
// @origin poi
// @project poi
// @file FormulaUsedBlankCellSet.java
// @method containsCell(int, int)
// @license [PENDIENTE]
// @sonarCCBefore 11
// @sonarCCAfter 8   (estimado; CC delta=-3)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class PoiBlankCellSheetGroupContainsCell {
    public boolean containsCell(int rowIndex, int columnIndex) {
        if (rowIndex > _lastDefinedRow)
            return true;
        for (int i = _rectangleGroups.size() - 1; i >= 0; i--) {
            BlankCellRectangleGroup bcrg = _rectangleGroups.get(i);
            if (bcrg.containsCell(rowIndex, columnIndex)) {
                return true;
            }
        }
        if (_currentRectangleGroup != null && _currentRectangleGroup.containsCell(rowIndex, columnIndex)) {
            return true;
        }
        if (_currentRowIndex != -1 && _currentRowIndex == rowIndex) {
            if (_firstColumnIndex <= columnIndex && columnIndex <= _lastColumnIndex) {
                return true;
            }
        }
        return false;
    }
}
