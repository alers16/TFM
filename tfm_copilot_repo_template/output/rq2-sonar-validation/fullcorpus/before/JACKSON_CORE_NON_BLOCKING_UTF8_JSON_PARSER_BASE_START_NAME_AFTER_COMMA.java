public class JACKSON_CORE_NON_BLOCKING_UTF8_JSON_PARSER_BASE_START_NAME_AFTER_COMMA {
private final JsonToken _startNameAfterComma(int ch) throws JacksonException {
    // First: any leading white space?
    if (ch <= 0x0020) {
        // will skip through all available ws (and comments)
        ch = _skipWS(ch);
        if (ch <= 0) {
            _minorState = MINOR_PROPERTY_LEADING_COMMA;
            return _currToken;
        }
    }
    if (ch != INT_COMMA) {
        // either comma, separating entries, or closing right curly
        if (ch == INT_RCURLY) {
            return _closeObjectScope();
        }
        if (ch == INT_HASH) {
            return _finishHashComment(MINOR_PROPERTY_LEADING_COMMA);
        }
        if (ch == INT_SLASH) {
            return _startSlashComment(MINOR_PROPERTY_LEADING_COMMA);
        }
        _reportUnexpectedChar(ch, "was expecting comma to separate " + _streamReadContext.typeDesc() + " entries");
    }
    int ptr = _inputPtr;
    if (ptr >= _inputEnd) {
        _minorState = MINOR_PROPERTY_LEADING_WS;
        return _updateTokenToNA();
    }
    ch = getByteFromBuffer(ptr);
    _inputPtr = ptr + 1;
    if (ch <= 0x0020) {
        ch = _skipWS(ch);
        if (ch <= 0) {
            _minorState = MINOR_PROPERTY_LEADING_WS;
            return _currToken;
        }
    }
    _updateTokenLocation();
    if (ch != INT_QUOTE) {
        if (ch == INT_RCURLY) {
            if ((_formatReadFeatures & FEAT_MASK_TRAILING_COMMA) != 0) {
                return _closeObjectScope();
            }
        }
        return _handleOddName(ch);
    }
    // First: can we optimize out bounds checks?
    if ((_inputPtr + 13) <= _inputEnd) {
        // Need up to 12 chars, plus one trailing (quote)
        String n = _fastParseName();
        if (n != null) {
            return _fieldComplete(n);
        }
    }
    return _parseEscapedName(0, 0, 0);
}
}

