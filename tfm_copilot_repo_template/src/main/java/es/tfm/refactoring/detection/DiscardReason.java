package es.tfm.refactoring.detection;

/**
 * Categorías estables de motivo de descarte para un candidato a refactorización.
 * <p>
 * Cada categoría corresponde a una precondición del detector MVP que no se cumple,
 * o a la ausencia total de patrón combinable.
 * <p>
 * Estas categorías permiten trazabilidad experimental reproducible para RQ1:
 * ¿en qué casos se pueden (o no) combinar sentencias condicionales anidadas?
 */
public enum DiscardReason {

    /** El if externo tiene rama else o else-if (precondición P1). */
    OUTER_HAS_ELSE("El if externo tiene else/else-if"),

    /** El bloque then del if externo contiene más de una sentencia (precondición P2). */
    OUTER_BLOCK_MULTIPLE_STATEMENTS("El bloque then tiene más de una sentencia"),

    /** La sentencia única del bloque then no es un if (precondición P3). */
    SINGLE_STATEMENT_NOT_IF("La sentencia única del bloque then no es un if"),

    /** El if interno tiene rama else o else-if (precondición P4). */
    INNER_HAS_ELSE("El if interno tiene else/else-if"),

    /** La condición contiene una llamada a método (precondición P5). */
    METHOD_CALL_IN_CONDITION("Llamada a método en condición (posible side effect)"),

    /** La condición contiene una expresión de asignación (precondición P5). */
    ASSIGNMENT_IN_CONDITION("Asignación en condición (side effect)"),

    /** La condición contiene operadores ++ o -- (precondición P5). */
    INCREMENT_OR_DECREMENT_IN_CONDITION("Operador ++/-- en condición (side effect)"),

    /** La condición contiene una expresión lambda (precondición P5). */
    LAMBDA_IN_CONDITION("Lambda en condición (side effect potencial)"),

    /** La condición contiene una instanciación con new (precondición P5). */
    OBJECT_CREATION_IN_CONDITION("Instanciación con new en condición (side effect)"),

    /** No se encontró un patrón de if anidado combinable. */
    NO_NESTED_IF_PATTERN("No hay patrón de if anidado combinable");

    private final String description;

    DiscardReason(String description) {
        this.description = description;
    }

    /** Descripción legible del motivo. */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return name() + ": " + description;
    }
}
