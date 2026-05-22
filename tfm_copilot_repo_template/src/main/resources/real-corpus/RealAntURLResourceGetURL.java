// @caseId ANT_U_R_L_RESOURCE_GET_U_R_L
// @origin ant-1.10.14
// @project Apache Ant
// @file URLResource.java
// @method getURL()
// @license Apache-2.0
// @sonarCCBefore 10
// @sonarCCAfter 7   (estimado; CC delta=-3)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class AntURLResourceGetURL {
    /**
     * Get the URL used by this URLResource.
     * @return a URL object.
     */
    public synchronized URL getURL() {
        if (isReference()) {
            return getRef().getURL();
        }
        if (url == null) {
            if (baseURL != null) {
                if (relPath == null) {
                    throw new BuildException("must provide relativePath" + " attribute when using baseURL.");
                }
                try {
                    url = new URL(baseURL, relPath);
                } catch (MalformedURLException e) {
                    throw new BuildException(e);
                }
            }
        }
        return url;
    }
}
