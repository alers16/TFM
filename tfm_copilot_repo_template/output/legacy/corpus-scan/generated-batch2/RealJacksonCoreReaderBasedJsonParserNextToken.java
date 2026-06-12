// @caseId JACKSON_CORE_READER_BASED_JSON_PARSER_NEXT_TOKEN
// @origin jackson-core
// @project jackson-core
// @file ReaderBasedJsonParser.java
// @method nextToken()
// @license [PENDIENTE]
// @sonarCCBefore 21
// @sonarCCAfter 19   (estimado; CC delta=-2)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonCoreReaderBasedJsonParserNextToken {
    /*
        /**********************************************************************
        /* Public API, traversal
        /**********************************************************************
         */
    /**
     * @return Next token from the stream, if any found, or null
     *   to indicate end-of-input
     */
    @Override
    public final JsonToken nextToken() throws JacksonException {
        /* First: Object Property names are special -- we will always tokenize
             * (part of) value along with the property name to simplify
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
            // Should actually close/release things
            // like input source, symbol table and recyclable buffers now.
            close();
            return _updateTokenToNull();
        }
        // clear any data retained so far
        _binaryValue = null;
        // Closing scope?
        if ((i | 0x20) == INT_RCURLY) {
            // ~ '}]'
            _closeScope(i);
            return _currToken;
        }
        // Nope: do we then expect a comma?
        if (_streamReadContext.expectComma()) {
            i = _skipComma(i);
            // Was that a trailing comma?
            if ((_formatReadFeatures & FEAT_MASK_TRAILING_COMMA) != 0) {
                if ((i | 0x20) == INT_RCURLY) {
                    // ~ '}]'
                    _closeScope(i);
                    return _currToken;
                }
            }
        }
        /* And should we now have a name? Always true for Object contexts, since
             * the intermediate 'expect-value' state is never retained.
             */
        boolean inObject = _streamReadContext.inObject();
        if (inObject) {
            // First, the property name itself:
            _updateNameLocation();
            String name = (i == INT_QUOTE) ? _parseName() : _handleOddName(i);
            _streamReadContext.setCurrentName(name);
            _updateToken(JsonToken.PROPERTY_NAME);
            i = _skipColon();
        }
        _updateLocation();
        // Ok: we must have a value... what is it?
        JsonToken t;
        switch(i) {
            case '"':
                _tokenIncomplete = true;
                t = JsonToken.VALUE_STRING;
                break;
            case '[':
                if (!inObject) {
                    createChildArrayContext(_tokenInputRow, _tokenInputCol);
                }
                t = JsonToken.START_ARRAY;
                break;
            case '{':
                if (!inObject) {
                    createChildObjectContext(_tokenInputRow, _tokenInputCol);
                }
                t = JsonToken.START_OBJECT;
                break;
            case '}':
                // Error: } is not valid at this point; valid closers have
                // been handled earlier
                _reportUnexpectedChar(i, "expected a value");
            case 't':
                _matchTrue();
                t = JsonToken.VALUE_TRUE;
                break;
            case 'f':
                _matchFalse();
                t = JsonToken.VALUE_FALSE;
                break;
            case 'n':
                _matchNull();
                t = JsonToken.VALUE_NULL;
                break;
            case '-':
                t = _parseSignedNumber(true);
                break;
            case '+':
                if (isEnabled(JsonReadFeature.ALLOW_LEADING_PLUS_SIGN_FOR_NUMBERS)) {
                    t = _parseSignedNumber(false);
                } else {
                    t = _handleOddValue(i);
                }
                break;
            case // [core#61]]
            '.':
                t = _parseFloatThatStartsWithPeriod();
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
            default:
                t = _handleOddValue(i);
                break;
        }
        if (inObject) {
            _nextToken = t;
            return _currToken;
        }
        return _updateToken(t);
    }
}
