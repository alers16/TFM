// @caseId PILOT_INVALID_MULTI_STMT
// @origin synthetic-realistic
// @description Bloque externo con declaración + if anidado — rechazado por P2
class PilotInvalidMultiStatement {

    /**
     * El bloque then del if externo contiene una declaración de variable
     * además del if anidado. No es combinable porque la variable x
     * se usa en el ámbito del if externo.
     */
    void computeWithLocal(int a, int b) {
        if (a > 0) {
            int x = a * 2;
            if (b > x) {
                System.out.println("b supera el doble de a: " + b);
            }
        }
    }
}
