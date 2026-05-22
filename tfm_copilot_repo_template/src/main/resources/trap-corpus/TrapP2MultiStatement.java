// @caseId TRAP_P2_MULTI_STATEMENT
// @origin synthetic-trap
// @description Bloque then externo con dos sentencias — violación P2. El LLM debe rechazar porque
//              el then externo no contiene únicamente el if interno: hay una sentencia extra antes.
class TrapP2MultiStatement {

    void audit(int userId, int resourceId) {
        if (userId > 0) {
            System.out.println("User " + userId + " accessing resource");
            if (resourceId > 0) {
                System.out.println("Granted access to resource " + resourceId);
            }
        }
    }
}
