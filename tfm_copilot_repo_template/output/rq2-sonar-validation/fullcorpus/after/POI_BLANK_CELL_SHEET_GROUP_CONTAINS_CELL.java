public class POI_BLANK_CELL_SHEET_GROUP_CONTAINS_CELL {
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
    if (_currentRowIndex != -1 && _currentRowIndex == rowIndex && _firstColumnIndex <= columnIndex && columnIndex <= _lastColumnIndex) {
        return true;
    }
    return false;
}
}

