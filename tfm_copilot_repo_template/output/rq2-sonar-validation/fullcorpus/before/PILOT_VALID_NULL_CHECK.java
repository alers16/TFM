public class PILOT_VALID_NULL_CHECK {
/**
 * Comprueba que un array no es null antes de verificar su longitud.
 * PatrÃ³n habitual en cÃ³digo defensivo Java.
 * Nota: la condiciÃ³n no contiene llamadas a mÃ©todos â€” es segura para el MVP.
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

