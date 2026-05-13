// @caseId PILOT_INVALID_METHOD_GUARD
// @origin synthetic-realistic
// @description Guarda con llamada a método en condición — rechazado por P5 (side effects)
class PilotInvalidMethodCallGuard {

    /**
     * Patrón típico: comprobar isEmpty() antes de acceder.
     * El detector rechaza porque isEmpty() es una llamada a método
     * y la política conservadora del MVP la trata como posible side effect.
     */
    void processIfNotEmpty(String input, int threshold) {
        if (!input.isEmpty()) {
            if (input.length() > threshold) {
                System.out.println("Input largo: " + input);
            }
        }
    }
}
