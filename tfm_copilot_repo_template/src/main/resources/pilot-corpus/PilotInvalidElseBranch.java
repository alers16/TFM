// @caseId PILOT_INVALID_ELSE_BRANCH
// @origin synthetic-realistic
// @description If externo con else — no combinable por precondición P1
class PilotInvalidElseBranch {

    /**
     * Método con rama else en el if externo.
     * El detector debe rechazarlo porque la combinación perdería
     * la semántica del else.
     */
    void classifyValue(int x, int y) {
        if (x > 0) {
            if (y > 0) {
                System.out.println("Ambos positivos");
            }
        } else {
            System.out.println("x no es positivo");
        }
    }
}
