// @caseId ANT_ANT_CLASS_LOADER_INITIALIZE_CLASS
// @origin ant-1.10.14
// @project Apache Ant
// @file AntClassLoader.java
// @method initializeClass(Class<?>)
// @license Apache-2.0
// @sonarCCBefore 7
// @sonarCCAfter 4   (estimado; CC delta=-3)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class AntAntClassLoaderInitializeClass {
    /**
     * Forces initialization of a class in a JDK 1.1 compatible, albeit hacky
     * way.
     *
     * @param theClass The class to initialize.
     *                 Must not be <code>null</code>.
     *
     * @deprecated since 1.6.x.
     *             Use Class.forName with initialize=true instead.
     */
    @Deprecated
    public static void initializeClass(final Class<?> theClass) {
        // ***HACK*** We ask the VM to create an instance
        // by voluntarily providing illegal arguments to force
        // the VM to run the class' static initializer, while
        // at the same time not running a valid constructor.
        final Constructor<?>[] cons = theClass.getDeclaredConstructors();
        //At least one constructor is guaranteed to be there, but check anyway.
        if (cons != null) {
            if (cons.length > 0 && cons[0] != null) {
                final String[] strs = new String[NUMBER_OF_STRINGS];
                try {
                    cons[0].newInstance((Object[]) strs);
                    // Expecting an exception to be thrown by this call:
                    // IllegalArgumentException: wrong number of Arguments
                } catch (final Exception e) {
                    // Ignore - we are interested only in the side
                    // effect - that of getting the static initializers
                    // invoked.  As we do not want to call a valid
                    // constructor to get this side effect, an
                    // attempt is made to call a hopefully
                    // invalid constructor - come on, nobody
                    // would have a constructor that takes in
                    // 256 String arguments ;-)
                    // (In fact, they can't - according to JVM spec
                    // section 4.10, the number of method parameters is limited
                    // to 255 by the definition of a method descriptor.
                    // Constructors count as methods here.)
                }
            }
        }
    }
}
