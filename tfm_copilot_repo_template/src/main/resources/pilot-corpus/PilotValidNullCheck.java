// @caseId PILOT_VALID_NULL_CHECK
// @origin synthetic-realistic
// @description Guarda null seguida de comprobación de propiedad — patrón frecuente en Java
class PilotValidNullCheck {

    /**
     * Comprueba que un array no es null antes de verificar su longitud.
     * Patrón habitual en código defensivo Java.
     * Nota: la condición no contiene llamadas a métodos — es segura para el MVP.
     */
    void processArray(int[] data) {
        if (data != null) {
            if (data.length > 0) {
                int first = data[0];
                System.out.println("Primer elemento: " + first);
            }
        }
    }
}
