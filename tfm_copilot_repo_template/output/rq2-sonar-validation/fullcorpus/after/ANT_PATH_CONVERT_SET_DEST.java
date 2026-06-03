public class ANT_PATH_CONVERT_SET_DEST {
/**
 * Set destination resource.
 * @param dest
 * @since Ant 1.10.13
 */
public void setDest(Resource dest) {
    if (dest != null && this.dest != null) {
        throw new BuildException("@dest already set");
    }
    this.dest = dest;
}
}

