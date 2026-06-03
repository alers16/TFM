public class JACKSON_CORE_U_T_F8_STREAM_JSON_PARSER_NEXT_NAME {
/*
        /**********************************************************************
        /* Public API, traversal, nextName() variants
        /**********************************************************************
         */
@Override
public String nextName() throws JacksonException {
    // // // Note: this is almost a verbatim copy of nextToken()
    _numTypesValid = NR_UNKNOWN;
    if (_currToken == JsonToken.PROPERTY_NAME) {
        _nextAfterName();
        return null;
    }
    if (_tokenIncomplete) {
        _skipString();
    }
    int i = _skipWSOrEnd();
    if (i < 0) {
        close();
        _updateTokenToNull();
        return null;
    }
    _binaryValue = null;
    if (i == INT_RBRACKET) {
        _closeArrayScope();
        _updateToken(JsonToken.END_ARRAY);
        return null;
    }
    if (i == INT_RCURLY) {
        _closeObjectScope();
        _updateToken(JsonToken.END_OBJECT);
        return null;
    }
    // Nope: do we then expect a comma?
    if (_streamReadContext.expectComma()) {
        if (i != INT_COMMA) {
            return _reportUnexpectedChar(i, "was expecting comma to separate " + _streamReadContext.typeDesc() + " entries");
        }
        i = _skipWS();
        // Was that a trailing comma?
        if ((_formatReadFeatures & FEAT_MASK_TRAILING_COMMA) != 0 && ((i == INT_RBRACKET) || (i == INT_RCURLY))) {
            _closeScope(i);
            return null;
        }
    }
    if (!_streamReadContext.inObject()) {
        _updateLocation();
        _nextTokenNotInObject(i);
        return null;
    }
    _updateNameLocation();
    final String nameStr = _parseName(i);
    _streamReadContext.setCurrentName(nameStr);
    _updateToken(JsonToken.PROPERTY_NAME);
    i = _skipColon();
    _updateLocation();
    if (i == INT_QUOTE) {
        _tokenIncomplete = true;
        _nextToken = JsonToken.VALUE_STRING;
        return nameStr;
    }
    JsonToken t;
    switch(i) {
        case '-':
            t = _parseSignedNumber(true);
            break;
        case '+':
            if (isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS)) {
                t = _parseSignedNumber(false);
            } else {
                t = _handleUnexpectedValue(i);
            }
            break;
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            t = _parseUnsignedNumber(i);
            break;
        case 'f':
            _matchFalse();
            t = JsonToken.VALUE_FALSE;
            break;
        case 'n':
            _matchNull();
            t = JsonToken.VALUE_NULL;
            break;
        case 't':
            _matchTrue();
            t = JsonToken.VALUE_TRUE;
            break;
        case '[':
            t = JsonToken.START_ARRAY;
            break;
        case '{':
            t = JsonToken.START_OBJECT;
            break;
        default:
            t = _handleUnexpectedValue(i);
    }
    _nextToken = t;
    return nameStr;
}
}

