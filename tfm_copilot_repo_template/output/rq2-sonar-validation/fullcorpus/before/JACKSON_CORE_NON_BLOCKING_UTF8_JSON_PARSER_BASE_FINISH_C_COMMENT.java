public class JACKSON_CORE_NON_BLOCKING_UTF8_JSON_PARSER_BASE_FINISH_C_COMMENT {
private final JsonToken _finishCComment(int fromMinorState, boolean gotStar) throws JacksonException {
    while (true) {
        if (_inputPtr >= _inputEnd) {
            _minorState = gotStar ? MINOR_COMMENT_CLOSING_ASTERISK : MINOR_COMMENT_C;
            _pending32 = fromMinorState;
            return _updateTokenToNA();
        }
        int ch = getNextUnsignedByteFromBuffer();
        if (ch < 0x020) {
            if (ch == INT_LF) {
                ++_currInputRow;
                _currInputRowStart = _inputPtr;
            } else if (ch == INT_CR) {
                ++_currInputRowAlt;
                _currInputRowStart = _inputPtr;
            } else if (ch != INT_TAB) {
                _reportInvalidSpace(ch);
            }
        } else if (ch == INT_ASTERISK) {
            gotStar = true;
            continue;
        } else if (ch == INT_SLASH) {
            if (gotStar) {
                break;
            }
        }
        gotStar = false;
    }
    return _startAfterComment(fromMinorState);
}
}

