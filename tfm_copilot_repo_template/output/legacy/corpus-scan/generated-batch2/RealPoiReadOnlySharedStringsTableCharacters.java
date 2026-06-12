// @caseId POI_READ_ONLY_SHARED_STRINGS_TABLE_CHARACTERS
// @origin poi
// @project poi
// @file ReadOnlySharedStringsTable.java
// @method characters(char[], int, int)
// @license [PENDIENTE]
// @sonarCCBefore 11
// @sonarCCAfter 9   (estimado; CC delta=-2)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class PoiReadOnlySharedStringsTableCharacters {
    /**
     * Captures characters only if a t(ext) element is open.
     */
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (tIsOpen) {
            if (inRPh && includePhoneticRuns) {
                if (characters != null) {
                    characters.append(ch, start, length);
                }
            } else if (!inRPh) {
                if (characters != null) {
                    characters.append(ch, start, length);
                }
            }
        }
    }
}
