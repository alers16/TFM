public class ANT_U_R_L_RESOURCE_GET_U_R_L {
/**
 * Get the URL used by this URLResource.
 * @return a URL object.
 */
public synchronized URL getURL() {
    if (isReference()) {
        return getRef().getURL();
    }
    if (url == null && baseURL != null) {
        if (relPath == null) {
            throw new BuildException("must provide relativePath" + " attribute when using baseURL.");
        }
        try {
            url = new URL(baseURL, relPath);
        } catch (MalformedURLException e) {
            throw new BuildException(e);
        }
    }
    return url;
}
}

