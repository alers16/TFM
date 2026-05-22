// @caseId TUTOR_MOEA_COMMAND_LINE_GET_CONSOLE_WIDTH
// @origin tutor-paper-corpus
// @project MOEAFramework/MOEAFramework (Saborido et al. 2022, 10 proyectos)
// @file org/moeaframework/util/cli/CommandLineUtility.java
// @method getConsoleWidth()
// @license MIT
// @sonarCCBefore 12
// @description Detecta el ancho de consola en Linux/Mac con stty; patron elegible:
//              if (width <= 0) { if (SystemUtils.IS_OS_LINUX || SystemUtils.IS_OS_MAC) { ... } }
// @upstream Saborido et al. 2022, IEEE Access, doi:10.1109/ACCESS.2022.3144743 (proyecto de evaluacion).
// @sourceCommit 223393fd

class TutorMoeaCommandLineGetConsoleWidth {

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
