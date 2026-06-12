// @caseId POI_LINK_TABLE_GET_NAME_X_PTG
// @origin poi
// @project poi
// @file LinkTable.java
// @method getNameXPtg(String, int)
// @license [PENDIENTE]
// @sonarCCBefore 9
// @sonarCCAfter 7   (estimado; CC delta=-2)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class PoiLinkTableGetNameXPtg {
    /**
     * Finds the external name definition for the given name,
     * optionally restricted by externsheet index, and returns
     * (if found) as a NameXPtg.
     *
     * @param sheetRefIndex The Extern Sheet Index to look for, or -1 if any
     */
    public NameXPtg getNameXPtg(String name, int sheetRefIndex) {
        // first find any external book block that contains the name:
        for (int i = 0; i < _externalBookBlocks.length; i++) {
            int definedNameIndex = _externalBookBlocks[i].getIndexOfName(name);
            if (definedNameIndex < 0) {
                continue;
            }
            // Found one
            int thisSheetRefIndex = findRefIndexFromExtBookIndex(i);
            if (thisSheetRefIndex >= 0) {
                // Check for the sheet index match, if requested
                if (sheetRefIndex == -1 || thisSheetRefIndex == sheetRefIndex) {
                    return new NameXPtg(thisSheetRefIndex, definedNameIndex);
                }
            }
        }
        return null;
    }
}
