public class ANT_J_UNIT_TASK_CREATE_CLASS_LOADER {
/**
 * Creates and configures an AntClassLoader instance from the
 * nested classpath element.
 *
 * @since Ant 1.6
 */
private void createClassLoader() {
    final Path userClasspath = getCommandline().getClasspath();
    final Path userModulepath = getCommandline().getModulepath();
    if ((userClasspath != null || userModulepath != null) && (reloading || classLoader == null)) {
        deleteClassLoader();
        final Path path = new Path(getProject());
        if (userClasspath != null) {
            path.add((Path) userClasspath.clone());
        }
        if (userModulepath != null && !hasJunit(path)) {
            path.add(expandModulePath(userModulepath));
        }
        if (includeAntRuntime) {
            log("Implicitly adding " + antRuntimeClasses + " to CLASSPATH", Project.MSG_VERBOSE);
            path.append(antRuntimeClasses);
        }
        classLoader = getProject().createClassLoader(path);
        if (getClass().getClassLoader() != null && getClass().getClassLoader() != Project.class.getClassLoader()) {
            classLoader.setParent(getClass().getClassLoader());
        }
        classLoader.setParentFirst(false);
        classLoader.addJavaLibraries();
        log("Using CLASSPATH " + classLoader.getClasspath(), Project.MSG_VERBOSE);
        // make sure the test will be accepted as a TestCase
        classLoader.addSystemPackageRoot("junit");
        // make sure the test annotation are accepted
        classLoader.addSystemPackageRoot("org.junit");
        // will cause trouble in JDK 1.1 if omitted
        classLoader.addSystemPackageRoot(MagicNames.ANT_CORE_PACKAGE);
    }
}
}

