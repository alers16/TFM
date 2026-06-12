// @caseId JACKSON_CORE_READER_BASED_JSON_PARSER_NEXT_NAME
// @origin jackson-core
// @project jackson-core
// @file ReaderBasedJsonParser.java
// @method nextName()
// @license [PENDIENTE]
// @sonarCCBefore 17
// @sonarCCAfter 15   (estimado; CC delta=-2)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonCoreReaderBasedJsonParserNextName {
    /*
        /**********************************************************************
        /* Public API, nextXxx() overrides
        /**********************************************************************
         */
    @Override
    public boolean nextName(SerializableString sstr) throws JacksonException {
        // // // Note: most of code below is copied from nextToken()
        _numTypesValid = NR_UNKNOWN;
        if (_currToken == JsonToken.PROPERTY_NAME) {
            _nextAfterName();
            return false;
        }
        if (_tokenIncomplete) {
            _skipString();
        }
        int i = _skipWSOrEnd();
        if (i < 0) {
            close();
            _updateTokenToNull();
            return false;
        }
        _binaryValue = null;
        // Closing scope?
        if ((i | 0x20) == INT_RCURLY) {
            // ~ '}]'
            _closeScope(i);
            return false;
        }
        if (_streamReadContext.expectComma()) {
            i = _skipComma(i);
            // Was that a trailing comma?
            if ((_formatReadFeatures & FEAT_MASK_TRAILING_COMMA) != 0) {
                if ((i | 0x20) == INT_RCURLY) {
                    // ~ '}]'
                    _closeScope(i);
                    return false;
                }
            }
        }
        if (!_streamReadContext.inObject()) {
            _updateLocation();
            _nextTokenNotInObject(i);
            return false;
        }
        _updateNameLocation();
        if (i == INT_QUOTE) {
            // when doing literal match, must consider escaping:
            char[] nameChars = sstr.asQuotedChars();
            final int len = nameChars.length;
            // Require 4 more bytes for faster skipping of colon that follows name
            if ((_inputPtr + len + 4) < _inputEnd) {
                // maybe...
                // first check length match by
                final int end = _inputPtr + len;
                if (_inputBuffer[end] == '"') {
                    int offset = 0;
                    int ptr = _inputPtr;
                    while (true) {
                        if (ptr == end) {
                            // yes, match!
                            _streamReadContext.setCurrentName(sstr.getValue());
                            _isNextTokenNameYes(_skipColonFast(ptr + 1));
                            return true;
                        }
                        if (nameChars[offset] != _inputBuffer[ptr]) {
                            break;
                        }
                        ++offset;
                        ++ptr;
                    }
                }
            }
        }
        return _isNextTokenNameMaybe(i, sstr.getValue());
    }
}
