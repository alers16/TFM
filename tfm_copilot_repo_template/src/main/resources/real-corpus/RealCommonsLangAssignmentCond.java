// @caseId REAL_COMMONS_LANG_ASSIGNMENT_COND
// @origin commons-lang-3.12.0
// @project Apache Commons Lang
// @file CharUtils.java
// @method process(char[])
// @license Apache-2.0
// @sonarCCBefore 3
// @description Método con asignación en la condición del if interno — no elegible por P5 (ASSIGNMENT_IN_CONDITION)

/**
 * Patrón representativo de Apache Commons Lang 3.12.0 — CharUtils.
 * Contiene un if anidado donde la condición del if interno incluye una asignación.
 * Caso NO ELEGIBLE: la condición interna es `(len = chars.length) > 0`,
 * que tiene un side effect de asignación (precondición P5 no satisfecha).
 * Razón de descarte: ASSIGNMENT_IN_CONDITION.
 * Combinar con && modificaría el punto en que ocurre la asignación de `len`.
 */
class RealCommonsLangAssignmentCond {

    int len;

    String process(char[] chars) {
        if (chars != null) {
            if ((len = chars.length) > 0) {
                return new String(chars, 0, len);
            }
        }
        return null;
    }
}
