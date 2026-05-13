// @caseId PILOT_VALID_BOUNDS_CHECK
// @origin synthetic-realistic
// @description Doble comprobación de límites inferior y superior — validación de índice
class PilotValidBoundsCheck {

    /**
     * Verifica que un índice está dentro de los límites de un array
     * y que el valor en esa posición es positivo.
     * Ambas condiciones son expresiones puras sin side effects.
     */
    void checkBounds(int[] array, int index) {
        if (index >= 0 && index < array.length) {
            if (array[index] > 0) {
                System.out.println("Valor positivo en índice " + index);
            }
        }
    }
}
