// @caseId ANT_AVAILABLE_EVAL
// @origin ant-1.10.14
// @project Apache Ant
// @file Available.java
// @method eval()
// @license Apache-2.0
// @sonarCCBefore 18
// @sonarCCAfter 17   (estimado; CC delta=-1)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class AntAvailableEval {
    /**
     * Evaluate the availability of a resource.
     *
     * @return boolean is the resource is available.
     * @exception BuildException if the condition is not configured correctly
     */
    @Override
    public boolean eval() throws BuildException {
        try {
            if (classname == null && file == null && resource == null) {
                throw new BuildException("At least one of (classname|file|resource) is required", getLocation());
            }
            if (type != null) {
                if (file == null) {
                    throw new BuildException("The type attribute is only valid when specifying the file attribute.", getLocation());
                }
            }
            if (classpath != null) {
                classpath.setProject(getProject());
                this.loader = getProject().createClassLoader(classpath);
            }
            String appendix = "";
            if (isTask) {
                appendix = " to set property " + property;
            } else {
                setTaskName("available");
            }
            if (classname != null && !checkClass(classname)) {
                log("Unable to load class " + classname + appendix, Project.MSG_VERBOSE);
                return false;
            }
            if (file != null && !checkFile()) {
                StringBuilder buf = new StringBuilder("Unable to find ");
                if (type != null) {
                    buf.append(type).append(' ');
                }
                buf.append(filename).append(appendix);
                log(buf.toString(), Project.MSG_VERBOSE);
                return false;
            }
            if (resource != null && !checkResource(resource)) {
                log("Unable to load resource " + resource + appendix, Project.MSG_VERBOSE);
                return false;
            }
        } finally {
            if (loader != null) {
                loader.cleanup();
                loader = null;
            }
            if (!isTask) {
                setTaskName(null);
            }
        }
        return true;
    }
}
