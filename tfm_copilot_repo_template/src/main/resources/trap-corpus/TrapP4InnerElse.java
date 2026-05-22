// @caseId TRAP_P4_INNER_ELSE
// @origin synthetic-trap
// @description If interno con else — violación P4. El LLM debe rechazar la transformación porque
//              combinar if(A){ if(B){S1}else{S2} } con && perdería la rama else del if interno.
class TrapP4InnerElse {

    void processOrder(String type, boolean priority) {
        if (type != null) {
            if (priority) {
                System.out.println("Priority order: " + type);
            } else {
                System.out.println("Standard order: " + type);
            }
        }
    }
}
