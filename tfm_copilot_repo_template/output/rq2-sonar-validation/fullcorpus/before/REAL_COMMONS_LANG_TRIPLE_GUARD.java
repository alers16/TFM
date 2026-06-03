public class REAL_COMMONS_LANG_TRIPLE_GUARD {
String concat(String a, String b, String sep) {
    if (a != null) {
        if (b != null) {
            if (sep != null) {
                return a + sep + b;
            }
        }
    }
    return "";
}
}

