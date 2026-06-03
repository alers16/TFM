public class POI_FORMULA_CELL_CACHE_ENTRY_IS_INPUT_SENSITIVE {
public boolean isInputSensitive() {
    if (_sensitiveInputCells != null && _sensitiveInputCells.length > 0) {
        return true;
    }
    return _usedBlankCellGroup == null ? false : !_usedBlankCellGroup.isEmpty();
}
}

