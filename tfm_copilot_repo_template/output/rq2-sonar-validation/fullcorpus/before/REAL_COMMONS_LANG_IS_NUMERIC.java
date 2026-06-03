public class REAL_COMMONS_LANG_IS_NUMERIC {
boolean isNumeric(String cs) {
    if (cs != null) {
        if (cs.length() > 0) {
            for (int i = 0; i < cs.length(); i++) {
                if (!Character.isDigit(cs.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
    }
    return false;
}
}

