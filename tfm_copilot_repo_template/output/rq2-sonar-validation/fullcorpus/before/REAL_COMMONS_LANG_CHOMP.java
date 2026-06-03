public class REAL_COMMONS_LANG_CHOMP {
String chomp(String str) {
    if (str != null) {
        if (str.length() > 0) {
            int lastIdx = str.length() - 1;
            char last = str.charAt(lastIdx);
            if (last == '\n') {
                return str.substring(0, lastIdx);
            }
            return str;
        }
    }
    return str;
}
}

