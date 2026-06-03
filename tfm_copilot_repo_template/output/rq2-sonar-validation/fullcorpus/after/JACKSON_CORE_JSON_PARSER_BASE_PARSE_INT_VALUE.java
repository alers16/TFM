public class JACKSON_CORE_JSON_PARSER_BASE_PARSE_INT_VALUE {
@Override
protected int _parseIntValue() throws JacksonException {
    // Inlined variant of: _parseNumericValue(NR_INT)
    if (_currToken == JsonToken.VALUE_NUMBER_INT && _intLength <= 9) {
        int i = _textBuffer.contentsAsInt(_numberNegative);
        _numberInt = i;
        _numTypesValid = NR_INT;
        return i;
    }
    // if not optimizable, use more generic
    _parseNumericValue(NR_INT);
    if ((_numTypesValid & NR_INT) == 0) {
        convertNumberToInt();
    }
    return _numberInt;
}
}

