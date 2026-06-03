public class PILOT_MIXED_OPPORTUNITIES {
/**
 * Contiene dos bloques if anidados:
 * 1) El primero es combinable (ambas condiciones puras, sin else).
 * 2) El segundo no es combinable (condiciÃ³n con llamada a mÃ©todo).
 *
 * Se espera que el pipeline detecte 1 oportunidad y descarte la otra.
 */
void mixedPatterns(int x, int y, String label) {
    // PatrÃ³n 1: combinable
    if (x > 0 && y > 0) {
        System.out.println("Ambos positivos: " + (x + y));
    }
    // PatrÃ³n 2: no combinable (condiciÃ³n con mÃ©todo)
    if (x > 0) {
        if (label.startsWith("A")) {
            System.out.println("Etiqueta A con x positivo");
        }
    }
}
}

