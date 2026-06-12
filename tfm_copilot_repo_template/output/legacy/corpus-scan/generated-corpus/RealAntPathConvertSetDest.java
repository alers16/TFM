// @caseId ANT_PATH_CONVERT_SET_DEST
// @origin ant-1.10.14
// @project Apache Ant
// @file PathConvert.java
// @method setDest(Resource)
// @license Apache-2.0
// @sonarCCBefore 3
// @sonarCCAfter 2   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class AntPathConvertSetDest {
    /**
     * Set destination resource.
     * @param dest
     * @since Ant 1.10.13
     */
    public void setDest(Resource dest) {
        if (dest != null) {
            if (this.dest != null) {
                throw new BuildException("@dest already set");
            }
        }
        this.dest = dest;
    }
}
