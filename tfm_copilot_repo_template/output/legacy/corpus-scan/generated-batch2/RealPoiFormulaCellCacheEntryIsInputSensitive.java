// @caseId POI_FORMULA_CELL_CACHE_ENTRY_IS_INPUT_SENSITIVE
// @origin poi
// @project poi
// @file FormulaCellCacheEntry.java
// @method isInputSensitive()
// @license [PENDIENTE]
// @sonarCCBefore 4
// @sonarCCAfter 3   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class PoiFormulaCellCacheEntryIsInputSensitive {
    public boolean isInputSensitive() {
        if (_sensitiveInputCells != null) {
            if (_sensitiveInputCells.length > 0) {
                return true;
            }
        }
        return _usedBlankCellGroup == null ? false : !_usedBlankCellGroup.isEmpty();
    }
}
