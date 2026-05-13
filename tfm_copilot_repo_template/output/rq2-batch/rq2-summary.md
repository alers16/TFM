# RQ2 — Resultados consolidados (batch determinista)

**Total casos:** 18 (piloto: 8, real: 10)

**Elegibles:** 10 / 18

**Δ complejidad cognitiva agregado:** -18

> Nota: los valores de complejidad son estimaciones del prototipo (proxy), no equivalentes directos a SonarQube/SonarLint.

## Categorías de descarte

| Categoría | Casos |
|---|---|
| OUTER_HAS_ELSE | 2 |
| SINGLE_STATEMENT_NOT_IF | 8 |
| METHOD_CALL_IN_CONDITION | 4 |
| OUTER_BLOCK_MULTIPLE_STATEMENTS | 4 |

## Resultados — Corpus piloto

| caseId | eligible | applied | passes | CC before | CC after | Δ |
|---|---|---|---|---|---|---|
| PILOT_VALID_SIMPLE | true | 1 | 1 | 3 | 2 | -1 |
| PILOT_VALID_NULL_CHECK | true | 1 | 1 | 3 | 2 | -1 |
| PILOT_VALID_BOUNDS_CHECK | true | 1 | 1 | 4 | 2 | -2 |
| PILOT_VALID_NESTED_IN_LOOP | true | 1 | 1 | 6 | 4 | -2 |
| PILOT_INVALID_ELSE_BRANCH | false | 0 | 0 | 4 | 4 | 0 |
| PILOT_INVALID_METHOD_GUARD | false | 0 | 0 | 3 | 3 | 0 |
| PILOT_INVALID_MULTI_STMT | false | 0 | 0 | 3 | 3 | 0 |
| PILOT_MIXED_OPPORTUNITIES | true | 1 | 1 | 6 | 5 | -1 |

## Resultados — Corpus real

| caseId | eligible | applied | passes | CC before | CC after | Δ |
|---|---|---|---|---|---|---|
| REAL_COMMONS_LANG_CHOMP | false | 0 | 0 | 6 | 6 | 0 |
| REAL_COMMONS_LANG_IS_NUMERIC | false | 0 | 0 | 10 | 10 | 0 |
| REAL_COMMONS_COLLECTIONS_GET | false | 0 | 0 | 3 | 3 | 0 |
| REAL_COMMONS_COLLECTIONS_ISEMPTY | true | 1 | 1 | 3 | 2 | -1 |
| REAL_COMMONS_MATH_CONVERGED | true | 1 | 1 | 3 | 2 | -1 |
| REAL_COMMONS_MATH_VALIDATE_RANGE | false | 0 | 0 | 4 | 4 | 0 |
| REAL_ANT_EXECUTE_TASK | false | 0 | 0 | 3 | 3 | 0 |
| REAL_ANT_MATCH_PATH | true | 1 | 1 | 6 | 4 | -2 |
| REAL_COMMONS_LANG_CONTAINS_NONE | true | 1 | 1 | 15 | 11 | -4 |
| REAL_COMMONS_LANG_MID | true | 1 | 1 | 10 | 7 | -3 |
