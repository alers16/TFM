// @caseId JACKSON_CORE_PARSER_BASE_DECODE_BASE64_ESCAPE
// @origin jackson-core
// @project jackson-core
// @file ParserBase.java
// @method _decodeBase64Escape(Base64Variant, char, int)
// @license [PENDIENTE]
// @sonarCCBefore 8
// @sonarCCAfter 6   (estimado; CC delta=-2)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class JacksonCoreParserBaseDecodeBase64Escape {
    protected final int _decodeBase64Escape(Base64Variant b64variant, int ch, int index) throws JacksonException {
        // Need to handle escaped chars
        if (ch != '\\') {
            _reportInvalidBase64Char(b64variant, ch, index);
        }
        int unescaped = _decodeEscaped();
        // if white space, skip if first triplet; otherwise errors
        if (unescaped <= INT_SPACE) {
            if (index == 0) {
                // whitespace only allowed to be skipped between triplets
                return -1;
            }
        }
        // otherwise try to find actual triplet value
        int bits = b64variant.decodeBase64Char(unescaped);
        if (bits < 0) {
            if (bits != Base64Variant.BASE64_VALUE_PADDING) {
                _reportInvalidBase64Char(b64variant, unescaped, index);
            }
        }
        return bits;
    }
}
