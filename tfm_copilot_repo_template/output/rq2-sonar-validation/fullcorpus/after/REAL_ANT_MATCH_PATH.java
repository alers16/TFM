public class REAL_ANT_MATCH_PATH {
boolean matchPath(String path, String pattern, boolean caseSensitive) {
    if (path != null && pattern != null) {
        if (path.length() > 0) {
            return path.startsWith(pattern);
        }
    }
    return false;
}
}

