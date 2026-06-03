public class JACKSON_CORE_PARSER_BASE_DECODE_BASE64_ESCAPE {
protected final int _decodeBase64Escape(Base64Variant b64variant, int ch, int index) throws JacksonException {
    // Need to handle escaped chars
    if (ch != '\\') {
        _reportInvalidBase64Char(b64variant, ch, index);
    }
    int unescaped = _decodeEscaped();
    // if white space, skip if first triplet; otherwise errors
    if (unescaped <= INT_SPACE && index == 0) {
        // whitespace only allowed to be skipped between triplets
        return -1;
    }
    // otherwise try to find actual triplet value
    int bits = b64variant.decodeBase64Char(unescaped);
    if (bits < 0 && bits != Base64Variant.BASE64_VALUE_PADDING) {
        _reportInvalidBase64Char(b64variant, unescaped, index);
    }
    return bits;
}
}

