// @caseId PILOT_VALID_NESTED_IN_LOOP
// @origin synthetic-realistic
// @description Filtrado condicional dentro de bucle — patrón de procesamiento de colecciones
class PilotValidNestedInLoop {

    /**
     * Filtra elementos de un array aplicando dos condiciones encadenadas.
     * El anidamiento dentro de un bucle produce mayor impacto en complejidad
     * cognitiva al combinarse.
     */
    void filterAndProcess(int[] values, int threshold) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] > 0) {
                if (values[i] < threshold) {
                    System.out.println("Aceptado: " + values[i]);
                }
            }
        }
    }
}
