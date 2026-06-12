// @caseId JACKSON_CORE_U_T_F8_STREAM_JSON_PARSER_NEXT_TOKEN
// @origin jackson-core
// @project jackson-core
// @file UTF8StreamJsonParser.java
// @method nextToken()
// @license [PENDIENTE]
// @sonarCCBefore 20
// @sonarCCAfter 18   (estimado; CC delta=-2)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonCoreUTF8StreamJsonParserNextToken {
    /*
        /**********************************************************************
        /* Public API, traversal, basic
        /**********************************************************************
         */
    /**
     * @return Next token from the stream, if any found, or null
     *   to indicate end-of-input
     */
    @Override
    public JsonToken nextToken() throws JacksonException {
        /* First: property names are special -- we will always tokenize
             * (part of) value along with property name to simplify
             * state handling. If so, can and need to use secondary token:
             */
        if (_currToken == JsonToken.PROPERTY_NAME) {
            return _nextAfterName();
        }
        // But if we didn't already have a name, and (partially?) decode number,
        // need to ensure no numeric information is leaked
        _numTypesValid = NR_UNKNOWN;
        if (_tokenIncomplete) {
            // only strings can be partial
            _skipString();
        }
        int i = _skipWSOrEnd();
        if (i < 0) {
            // end-of-input
            // Close/release things like input source, symbol table and recyclable buffers
            close();
            return _updateTokenToNull();
        }
        // clear any data retained so far
        _binaryValue = null;
        // Closing scope?
        if (i == INT_RBRACKET) {
            _closeArrayScope();
            return _updateToken(JsonToken.END_ARRAY);
        }
        if (i == INT_RCURLY) {
            _closeObjectScope();
            return _updateToken(JsonToken.END_OBJECT);
        }
        // Nope: do we then expect a comma?
        if (_streamReadContext.expectComma()) {
            if (i != INT_COMMA) {
                return _reportUnexpectedChar(i, "was expecting comma to separate " + _streamReadContext.typeDesc() + " entries");
            }
            i = _skipWS();
            // Was that a trailing comma?
            if ((_formatReadFeatures & FEAT_MASK_TRAILING_COMMA) != 0) {
                if ((i == INT_RBRACKET) || (i == INT_RCURLY)) {
                    return _closeScope(i);
                }
            }
        }
        // And should we now have a name? Always true for Object contexts
        // since the intermediate 'expect-value' state is never retained.
        if (!_streamReadContext.inObject()) {
            _updateLocation();
            return _nextTokenNotInObject(i);
        }
        // So first parse the property name itself:
        _updateNameLocation();
        String n = _parseName(i);
        _streamReadContext.setCurrentName(n);
        _updateToken(JsonToken.PROPERTY_NAME);
        i = _skipColon();
        _updateLocation();
        // Ok: we must have a value... what is it? Strings are very common, check first:
        if (i == INT_QUOTE) {
            _tokenIncomplete = true;
            _nextToken = JsonToken.VALUE_STRING;
            return _currToken;
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
            case // [core#611]:
            '.':
                t = _parseFloatThatStartsWithPeriod(false, false);
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
        return _currToken;
    }
}
