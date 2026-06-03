public class REAL_COMMONS_LANG_CONTAINS_NONE {
boolean containsNone(String cs, char[] searchChars) {
    if (cs != null && searchChars != null) {
        int csLen = cs.length();
        int searchLen = searchChars.length;
        for (int i = 0; i < csLen; i++) {
            char ch = cs.charAt(i);
            for (int j = 0; j < searchLen; j++) {
                if (searchChars[j] == ch) {
                    return false;
                }
            }
        }
        return true;
    }
    return true;
}
}

