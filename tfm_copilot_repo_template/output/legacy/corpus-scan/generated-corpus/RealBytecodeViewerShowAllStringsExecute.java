// @caseId BYTECODE_VIEWER_SHOW_ALL_STRINGS_EXECUTE
// @origin bytecode-viewer-2.11.2
// @project Bytecode Viewer
// @file ShowAllStrings.java
// @method execute(List<ClassNode>)
// @license GPL-3.0
// @sonarCCBefore 42
// @sonarCCAfter 37   (estimado; CC delta=-5)
// @eligible YES
// @description [PENDIENTE DE REVISIÓN] Detectado automáticamente por CorpusScanRunner. Verificar semántica antes de incluir en corpus oficial.

class BytecodeViewerShowAllStringsExecute {
    @Override
    public void execute(List<ClassNode> classNodeList) {
        PluginConsole frame = new PluginConsole("Show All Strings");
        StringBuilder sb = new StringBuilder();
        for (ClassNode classNode : classNodeList) {
            for (Object o : classNode.fields.toArray()) {
                FieldNode f = (FieldNode) o;
                Object v = f.value;
                if (v instanceof String) {
                    String s = (String) v;
                    if (!s.isEmpty())
                        sb.append(classNode.name).append(".").append(f.name).append(f.desc).append(" -> \"").append(s.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r")).append("\"").append(nl);
                }
                if (v instanceof String[]) {
                    for (int i = 0; i < ((String[]) v).length; i++) {
                        String s = ((String[]) v)[i];
                        if (!s.isEmpty())
                            sb.append(classNode.name).append(".").append(f.name).append(f.desc).append("[").append(i).append("] -> \"").append(s.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r")).append("\"").append(nl);
                    }
                }
            }
            for (Object o : classNode.methods.toArray()) {
                MethodNode m = (MethodNode) o;
                InsnList iList = m.instructions;
                for (AbstractInsnNode a : iList.toArray()) {
                    if (a instanceof LdcInsnNode) {
                        if (((LdcInsnNode) a).cst instanceof String) {
                            final String s = (String) ((LdcInsnNode) a).cst;
                            if (!s.isEmpty())
                                sb.append(classNode.name).append(".").append(m.name).append(m.desc).append(" -> \"").append(s.replaceAll("\\n", "\\\\n").replaceAll("\\r", "\\\\r")).append("\"").append(nl);
                        }
                    }
                }
            }
        }
        frame.setText(sb.toString());
        frame.setVisible(true);
    }
}
