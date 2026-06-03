public class FASTJSON_J_S_O_N_LEXER_BASE_SCAN_SYMBOL_UN_QUOTED {
public final String scanSymbolUnQuoted(final SymbolTable symbolTable) {
    if (token == JSONToken.ERROR && pos == 0 && bp == 1) {
        // adjust
        bp = 0;
    }
    final boolean[] firstIdentifierFlags = IOUtils.firstIdentifierFlags;
    final char first = ch;
    final boolean firstFlag = ch >= firstIdentifierFlags.length || firstIdentifierFlags[first];
    if (!firstFlag) {
        throw new //
        JSONException("illegal identifier : " + ch + info());
    }
    final boolean[] identifierFlags = IOUtils.identifierFlags;
    int hash = first;
    np = bp;
    sp = 1;
    char chLocal;
    for (; ; ) {
        chLocal = next();
        if (chLocal < identifierFlags.length) {
            if (!identifierFlags[chLocal]) {
                break;
            }
        }
        hash = 31 * hash + chLocal;
        sp++;
        continue;
    }
    this.ch = charAt(bp);
    token = JSONToken.IDENTIFIER;
    final int NULL_HASH = 3392903;
    if (sp == 4 && hash == NULL_HASH && charAt(np) == 'n' && charAt(np + 1) == 'u' && charAt(np + 2) == 'l' && charAt(np + 3) == 'l') {
        return null;
    }
    // return text.substring(np, np + sp).intern();
    if (symbolTable == null) {
        return subString(np, sp);
    }
    return this.addSymbol(np, sp, hash, symbolTable);
    // return symbolTable.addSymbol(buf, np, sp, hash);
}
}

