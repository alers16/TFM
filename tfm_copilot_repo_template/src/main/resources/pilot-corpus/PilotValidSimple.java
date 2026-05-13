// @caseId PILOT_VALID_SIMPLE
// @origin synthetic-realistic
// @description Validación de rango de entrada — patrón canónico if-if sin else
class PilotValidSimple {

    /**
     * Procesa un valor si está en rango positivo y dentro del límite.
     * Patrón típico de validación de entrada en métodos de negocio.
     */
    void processIfInRange(int value, int limit) {
        if (value > 0) {
            if (value <= limit) {
                System.out.println("Valor aceptado: " + value);
            }
        }
    }
}
