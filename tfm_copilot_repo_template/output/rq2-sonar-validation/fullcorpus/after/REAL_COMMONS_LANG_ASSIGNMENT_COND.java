public class REAL_COMMONS_LANG_ASSIGNMENT_COND {
String process(char[] chars) {
    if (chars != null) {
        if ((len = chars.length) > 0) {
            return new String(chars, 0, len);
        }
    }
    return null;
}
}

