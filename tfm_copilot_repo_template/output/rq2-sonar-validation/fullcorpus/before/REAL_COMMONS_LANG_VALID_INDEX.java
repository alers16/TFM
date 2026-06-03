public class REAL_COMMONS_LANG_VALID_INDEX {
char charAt(String str, int index, char defaultChar) {
    if (str != null) {
        if (index >= 0) {
            return str.charAt(index);
        }
    }
    return defaultChar;
}
}

