public class REAL_COMMONS_LANG_MID {
String mid(String str, int pos, int len) {
    if (str != null && len >= 0) {
        if (pos <= str.length()) {
            if (str.length() <= pos + len) {
                return str.substring(pos);
            }
            return str.substring(pos, pos + len);
        }
    }
    return null;
}
}

