// @caseId PILOT_MIXED_OPPORTUNITIES
// @origin synthetic-realistic
// @description Método con un patrón combinable y otro no combinable
class PilotMixedOpportunities {

    /**
     * Contiene dos bloques if anidados:
     * 1) El primero es combinable (ambas condiciones puras, sin else).
     * 2) El segundo no es combinable (condición con llamada a método).
     *
     * Se espera que el pipeline detecte 1 oportunidad y descarte la otra.
     */
    void mixedPatterns(int x, int y, String label) {
        // Patrón 1: combinable
        if (x > 0) {
            if (y > 0) {
                System.out.println("Ambos positivos: " + (x + y));
            }
        }

        // Patrón 2: no combinable (condición con método)
        if (x > 0) {
            if (label.startsWith("A")) {
                System.out.println("Etiqueta A con x positivo");
            }
        }
    }
}
