public class PILOT_VALID_SIMPLE {
/**
 * Procesa un valor si estÃ¡ en rango positivo y dentro del lÃ­mite.
 * PatrÃ³n tÃ­pico de validaciÃ³n de entrada en mÃ©todos de negocio.
 */
void processIfInRange(int value, int limit) {
    if (value > 0) {
        if (value <= limit) {
            System.out.println("Valor aceptado: " + value);
        }
    }
}
}

