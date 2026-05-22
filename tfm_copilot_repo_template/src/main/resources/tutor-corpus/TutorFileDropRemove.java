// @caseId TUTOR_FILEDROP_REMOVE
// @origin tutor-test-corpus
// @project Bytecode Viewer (test resource of Saborido et al. plug-in)
// @file FileDrop.java
// @method remove(PrintStream, Component, boolean)
// @license GPL-3.0-or-later
// @sonarCCBefore 9
// @description FileDrop.remove: if-else exterior + if-else interior con && + for — no elegible por OUTER_HAS_ELSE, OUTER_BLOCK_MULTIPLE_STATEMENTS, INNER_HAS_ELSE, METHOD_CALL_IN_CONDITION
// @upstream Saborido et al. 2022, IEEE Access, doi:10.1109/ACCESS.2022.3144743 (test resource).

class TutorFileDropRemove {

    public static boolean remove(final PrintStream out,
                                 final Component c, final boolean recursive) {
        if (supportsDnD()) {
            log(out, "FileDrop: Removing drag-and-drop hooks.");
            c.setDropTarget(null);
            if (recursive && (c instanceof Container)) {
                final Component[] comps = ((Container) c).getComponents();
                for (Component comp : comps) {
                    remove(out, comp, true);
                }
                return true;
            } else
                return false;
        } else
            return false;
    }

    private static boolean supportsDnD() {
        return true;
    }

    private static void log(PrintStream out, String msg) {
        if (out != null) {
            out.println(msg);
        }
    }
}
