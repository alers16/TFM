public class PILOT_INVALID_ELSE_BRANCH {
/**
 * MÃ©todo con rama else en el if externo.
 * El detector debe rechazarlo porque la combinaciÃ³n perderÃ­a
 * la semÃ¡ntica del else.
 */
void classifyValue(int x, int y) {
    if (x > 0) {
        if (y > 0) {
            System.out.println("Ambos positivos");
        }
    } else {
        System.out.println("x no es positivo");
    }
}
}

