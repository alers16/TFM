// @caseId JACKSON_CORE_READER_BASED_JSON_PARSER_SKIP_COLON_FAST
// @origin jackson-core
// @project jackson-core
// @file ReaderBasedJsonParser.java
// @method _skipColonFast(int)
// @license [PENDIENTE]
// @sonarCCBefore 36
// @sonarCCAfter 28   (estimado; CC delta=-8)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonCoreReaderBasedJsonParserSkipColonFast {
    // Variant called when we know there's at least 4 more bytes available
    private final int _skipColonFast(int ptr) throws JacksonException {
        int i = _inputBuffer[ptr++];
        if (i == INT_COLON) {
            // common case, no leading space
            i = _inputBuffer[ptr++];
            if (i > INT_SPACE) {
                // nor trailing
                if (i != INT_SLASH && i != INT_HASH) {
                    _inputPtr = ptr;
                    return i;
                }
            } else if (i == INT_SPACE || i == INT_TAB) {
                i = _inputBuffer[ptr++];
                if (i > INT_SPACE) {
                    if (i != INT_SLASH && i != INT_HASH) {
                        _inputPtr = ptr;
                        return i;
                    }
                }
            }
            _inputPtr = ptr - 1;
            // true -> skipped colon
            return _skipColon2(true);
        }
        if (i == INT_SPACE || i == INT_TAB) {
            i = _inputBuffer[ptr++];
        }
        boolean gotColon = (i == INT_COLON);
        if (gotColon) {
            i = _inputBuffer[ptr++];
            if (i > INT_SPACE) {
                if (i != INT_SLASH && i != INT_HASH) {
                    _inputPtr = ptr;
                    return i;
                }
            } else if (i == INT_SPACE || i == INT_TAB) {
                i = _inputBuffer[ptr++];
                if (i > INT_SPACE) {
                    if (i != INT_SLASH && i != INT_HASH) {
                        _inputPtr = ptr;
                        return i;
                    }
                }
            }
        }
        _inputPtr = ptr - 1;
        return _skipColon2(gotColon);
    }
}
