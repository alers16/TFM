public class KNOWAGE_SPAGO_U_R_L_VALIDATOR_IS_VALID_PATH {
/**
 * Controls if the input string represents a valid path.
 *
 * @param path The input string path
 * @return True if the string represents a valid path, else false
 */
@Override
protected boolean isValidPath(String path) {
    if (path == null) {
        return false;
    }
    Perl5Util pathMatcher = new Perl5Util();
    if (!pathMatcher.match(PATH_PATTERN, path)) {
        return false;
    }
    //if (path.endsWith("/")) {
    //    return false;
    //}
    int slash2Count = countToken("//", path);
    //if (this.options.isOff(ALLOW_2_SLASHES) && (slash2Count > 0)) {
    //    return false;
    //}
    int slashCount = countToken("/", path);
    int dot2Count = countToken("..", path);
    if (dot2Count > 0) {
        if ((slashCount - slash2Count - 1) <= dot2Count) {
            return false;
        }
    }
    return true;
}
}

