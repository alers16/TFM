# Apéndice D. Catálogo de `DiscardReason`

## Objetivo

Enumerar las 10 categorías de descarte con definición formal y ejemplo real.

## Categorías previstas

| Código | Categoría | Definición breve |
|---|---|---|
| P1 | OUTER_HAS_ELSE | El `if` externo tiene `else`/`else if`. |
| — | INNER_HAS_ELSE | El `if` interno tiene `else`/`else if`. |
| P2 | OUTER_BLOCK_MULTIPLE_STATEMENTS | El bloque externo tiene más de una sentencia. |
| P3 | SINGLE_STATEMENT_NOT_IF | La única sentencia del bloque externo no es un `if`. |
| P5 | METHOD_CALL_IN_CONDITION | Llamada a método presente en alguna condición. |
| — | ASSIGNMENT_IN_CONDITION | Asignación dentro de una condición. |
| — | INC_DEC_IN_CONDITION | `++`/`--` dentro de una condición. |
| — | LAMBDA_IN_CONDITION | Lambda dentro de una condición. |
| — | NEW_IN_CONDITION | `new` dentro de una condición. |
| — | OTHER | Reservado para casos futuros. |

## Evidencias del repositorio

- [src/main/java/es/tfm/refactoring/detection/DiscardReason.java](../../src/main/java/es/tfm/refactoring/detection/DiscardReason.java)
- Ejemplos reales: `src/main/resources/pilot-corpus/`, `src/main/resources/real-corpus/`.

## Notas

- [PENDIENTE DE VERIFICACIÓN] Confirmar el listado exacto y nombres consultando `DiscardReason.java` antes de redactar.
