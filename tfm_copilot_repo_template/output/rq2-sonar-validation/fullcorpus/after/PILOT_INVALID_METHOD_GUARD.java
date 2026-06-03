public class PILOT_INVALID_METHOD_GUARD {
/**
 * PatrÃ³n tÃ­pico: comprobar isEmpty() antes de acceder.
 * El detector rechaza porque isEmpty() es una llamada a mÃ©todo
 * y la polÃ­tica conservadora del MVP la trata como posible side effect.
 */
void processIfNotEmpty(String input, int threshold) {
    if (!input.isEmpty()) {
        if (input.length() > threshold) {
            System.out.println("Input largo: " + input);
        }
    }
}
}

