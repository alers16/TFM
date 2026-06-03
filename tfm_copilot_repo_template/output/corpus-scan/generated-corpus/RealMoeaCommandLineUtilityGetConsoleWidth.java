// @caseId MOEA_COMMAND_LINE_UTILITY_GET_CONSOLE_WIDTH
// @origin moea
// @project moea
// @file CommandLineUtility.java
// @method getConsoleWidth()
// @license [PENDIENTE]
// @sonarCCBefore 15
// @sonarCCAfter 11   (estimado; CC delta=-4)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class MoeaCommandLineUtilityGetConsoleWidth {
    /**
     * Returns the console width by:
     * <ol>
     *   <li>The value stored in {@link Settings#KEY_HELP_WIDTH}
     *   <li>Invoking {@code stty size} on supported platforms
     *   <li>Defaulting to {@value HelpFormatter#DEFAULT_WIDTH}
     * </ol>
     *
     * @return the console width
     */
    int getConsoleWidth() {
        int width = Settings.PROPERTIES.getInt(Settings.KEY_HELP_WIDTH, -1);
        if (width <= 0) {
            if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) {
                try {
                    ProcessBuilder processBuilder = new ProcessBuilder("stty", "size");
                    processBuilder.redirectInput(Redirect.INHERIT);
                    processBuilder.redirectError(Settings.isVerbose() ? Redirect.INHERIT : Redirect.DISCARD);
                    String output = RedirectStream.capture(processBuilder);
                    Tokenizer tokenizer = new Tokenizer();
                    width = Integer.parseInt(tokenizer.decodeToArray(output.trim())[1]);
                } catch (Exception e) {
                    if (Settings.isVerbose()) {
                        System.err.println("Unable to detect console width, using default!");
                        e.printStackTrace();
                    }
                }
            }
        }
        if (width <= 0) {
            width = HelpFormatter.DEFAULT_WIDTH;
        }
        return width;
    }
}
