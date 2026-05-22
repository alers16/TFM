// @caseId TRAP_P5_ASSIGNMENT
// @origin synthetic-trap
// @description Asignación en la condición interna — violación P5 (efecto lateral por asignación,
//              no por llamada a método). El LLM debe rechazar porque combinar la condición
//              modificaría el orden y el momento en que se asigna result.
class TrapP5Assignment {

    int result;

    void compute(int x, int[] data) {
        if (x >= 0) {
            if ((result = data[x]) > 0) {
                System.out.println("Positive result: " + result);
            }
        }
    }
}
