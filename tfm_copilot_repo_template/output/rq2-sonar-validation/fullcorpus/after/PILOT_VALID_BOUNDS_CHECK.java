public class PILOT_VALID_BOUNDS_CHECK {
/**
 * Verifica que un Ã­ndice estÃ¡ dentro de los lÃ­mites de un array
 * y que el valor en esa posiciÃ³n es positivo.
 * Ambas condiciones son expresiones puras sin side effects.
 */
void checkBounds(int[] array, int index) {
    if (index >= 0 && index < array.length && array[index] > 0) {
        System.out.println("Valor positivo en Ã­ndice " + index);
    }
}
}

