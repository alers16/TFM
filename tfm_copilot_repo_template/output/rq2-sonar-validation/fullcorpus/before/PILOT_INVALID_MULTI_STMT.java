public class PILOT_INVALID_MULTI_STMT {
/**
 * El bloque then del if externo contiene una declaraciÃ³n de variable
 * ademÃ¡s del if anidado. No es combinable porque la variable x
 * se usa en el Ã¡mbito del if externo.
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

