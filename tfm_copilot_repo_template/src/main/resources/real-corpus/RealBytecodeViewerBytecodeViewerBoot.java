// @caseId BYTECODE_VIEWER_BYTECODE_VIEWER_BOOT
// @origin bytecode-viewer-2.11.2
// @project Bytecode Viewer
// @file BytecodeViewer.java
// @method boot(boolean)
// @license GPL-3.0
// @sonarCCBefore 11
// @sonarCCAfter 9   (estimado; CC delta=-2)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class BytecodeViewerBytecodeViewerBoot {
    /**
     * Boot after all of the libraries have been loaded
     *
     * @param cli is it running CLI mode or not
     */
    public static void boot(boolean cli) {
        //delete files in the temp folder
        cleanupAsync();
        //shutdown hooks
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (Process proc : createdProcesses) proc.destroy();
            SettingsSerializer.saveSettings();
            cleanup();
        }, "Shutdown Hook"));
        //setup the viewer
        viewer.calledAfterLoad();
        //setup the recent files
        Settings.resetRecentFilesMenu();
        //ping back once on first boot to add to global user count
        if (!Configuration.pingback) {
            pingBack.start();
            Configuration.pingback = true;
        }
        //version checking
        if (viewer.updateCheck.isSelected() && !DEV_MODE)
            versionChecker.start();
        //show the main UI
        if (!cli)
            viewer.setVisible(true);
        //print startup time
        System.out.println("Start up took " + ((System.currentTimeMillis() - Configuration.start) / 1000) + " seconds");
        //request focus on GUI for hotkeys on start
        if (!cli)
            viewer.requestFocus();
        //open files from launch args
        if (!cli)
            if (launchArgs.length >= 1)
                for (String s : launchArgs) openFiles(new File[] { new File(s) }, true);
    }
}
