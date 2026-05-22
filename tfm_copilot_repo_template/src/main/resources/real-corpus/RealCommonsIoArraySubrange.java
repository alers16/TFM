// @caseId REAL_COMMONS_IO_ARRAY_SUBRANGE
// @origin commons-io-2.11.0
// @project Apache Commons IO
// @file IOUtils.java
// @method subarray(byte[], int)
// @license Apache-2.0
// @sonarCCBefore 3
// @description Método con doble guarda null + field access (array.length) — elegible clásico

/**
 * Patrón representativo de Apache Commons IO 2.11.0 — IOUtils.
 * Contiene un if anidado: guarda null exterior + comprobación de longitud por field access.
 * Caso ELEGIBLE: ambas condiciones son puras (null check + field access), sin else,
 * bloque externo con única sentencia (el if interno).
 * La herramienta aplica una combinación y reduce CC en 1.
 */
class RealCommonsIoArraySubrange {

    byte[] subarray(byte[] buf, int len) {
        if (buf != null) {
            if (buf.length >= len) {
                byte[] result = new byte[len];
                System.arraycopy(buf, 0, result, 0, len);
                return result;
            }
        }
        return new byte[0];
    }
}
