// @caseId TUTOR_FILEDROP_IS_DRAG_OK
// @origin tutor-test-corpus
// @project Bytecode Viewer (test resource of Saborido et al. plug-in)
// @file FileDrop.java
// @method isDragOk(PrintStream, DropTargetDragEvent)
// @license GPL-3.0-or-later
// @sonarCCBefore 10
// @description FileDrop.isDragOk: bucle while + if anidado + || en condicion + if-for anidados — no elegible por OUTER_BLOCK_MULTIPLE_STATEMENTS y METHOD_CALL_IN_CONDITION
// @upstream Saborido et al. 2022, IEEE Access, doi:10.1109/ACCESS.2022.3144743 (test resource).

class TutorFileDropIsDragOk {

    private boolean isDragOk(final PrintStream out, final DropTargetDragEvent evt) {
        boolean ok = false;

        // Get data flavors being dragged
        final DataFlavor[] flavors = evt.getCurrentDataFlavors();

        // See if any of the flavors are a file list
        int i = 0;
        while (!ok && i < flavors.length) {
            final DataFlavor curFlavor = flavors[i];
            if (curFlavor.equals(DataFlavor.javaFileListFlavor)
                    || curFlavor.isRepresentationClassReader()) {
                ok = true;
            }
            i++;
        }

        // If logging is enabled, show data flavors
        if (out != null) {
            if (flavors.length == 0) {
                log(out, "FileDrop: no data flavors.");
            }
            for (i = 0; i < flavors.length; i++) {
                log(out, flavors[i].toString());
            }
        }

        return ok;
    }

    private static void log(PrintStream out, String msg) {
        if (out != null) {
            out.println(msg);
        }
    }
}
