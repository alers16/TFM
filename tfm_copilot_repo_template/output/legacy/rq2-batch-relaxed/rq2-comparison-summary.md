# RQ2 — Comparativa STRICT (P1–P5) vs RELAXED (P1–P4 + P5')

> P5' acepta llamadas a métodos del allowlist (`MethodCallAllowlist`) presumiblemente puros bajo convención JDK.

## Tabla resumen por corpus

| Corpus | N | STRICT elegibles | STRICT ΔCC | RELAXED elegibles | RELAXED ΔCC | Nuevos elegibles |
|---|---|---|---|---|---|---|
| pilot | 8 | 5 | -7 | 6 | -9 | +1 |
| real | 10 | 5 | -11 | 8 | -22 | +3 |
| tutor | 6 | 1 | -3 | 1 | -3 | 0 |

## Desglose por corpus

### Corpus: pilot

#### STRICT

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

#### RELAXED

| caseId | eligible | applied | passes | CC before | CC after | Δ |
|---|---|---|---|---|---|---|
| PILOT_VALID_SIMPLE | true | 1 | 1 | 3 | 2 | -1 |
| PILOT_VALID_NULL_CHECK | true | 1 | 1 | 3 | 2 | -1 |
| PILOT_VALID_BOUNDS_CHECK | true | 1 | 1 | 4 | 2 | -2 |
| PILOT_VALID_NESTED_IN_LOOP | true | 1 | 1 | 6 | 4 | -2 |
| PILOT_INVALID_ELSE_BRANCH | false | 0 | 0 | 4 | 4 | 0 |
| PILOT_INVALID_METHOD_GUARD | true | 1 | 1 | 3 | 2 | -1 |
| PILOT_INVALID_MULTI_STMT | false | 0 | 0 | 3 | 3 | 0 |
| PILOT_MIXED_OPPORTUNITIES | true | 2 | 2 | 6 | 4 | -2 |

### Corpus: real

#### STRICT

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

#### RELAXED

| caseId | eligible | applied | passes | CC before | CC after | Δ |
|---|---|---|---|---|---|---|
| REAL_COMMONS_LANG_CHOMP | true | 1 | 1 | 6 | 4 | -2 |
| REAL_COMMONS_LANG_IS_NUMERIC | true | 1 | 1 | 10 | 7 | -3 |
| REAL_COMMONS_COLLECTIONS_GET | true | 1 | 1 | 3 | 2 | -1 |
| REAL_COMMONS_COLLECTIONS_ISEMPTY | true | 1 | 1 | 3 | 2 | -1 |
| REAL_COMMONS_MATH_CONVERGED | true | 1 | 1 | 3 | 2 | -1 |
| REAL_COMMONS_MATH_VALIDATE_RANGE | false | 0 | 0 | 4 | 4 | 0 |
| REAL_ANT_EXECUTE_TASK | false | 0 | 0 | 3 | 3 | 0 |
| REAL_ANT_MATCH_PATH | true | 2 | 2 | 6 | 2 | -4 |
| REAL_COMMONS_LANG_CONTAINS_NONE | true | 1 | 1 | 15 | 11 | -4 |
| REAL_COMMONS_LANG_MID | true | 2 | 2 | 10 | 4 | -6 |

### Corpus: tutor

#### STRICT

| caseId | eligible | applied | passes | CC before | CC after | Δ |
|---|---|---|---|---|---|---|
| TUTOR_BCV_EZ_INJECTION_EXECUTE | false | 0 | 0 | 124 | 124 | 0 |
| TUTOR_BCV_RESOURCE_DECOMPILE_SAVE_ALL | false | 0 | 0 | 1 | 1 | 0 |
| TUTOR_BCV_RESOURCE_DECOMPILE_SAVE_OPENED | false | 0 | 0 | 2 | 2 | 0 |
| TUTOR_JMETAL_EBES_READ_DATA_FILE | false | 0 | 0 | 126 | 126 | 0 |
| TUTOR_JMETAL_EBES_VARIABLE_POSITION | false | 0 | 0 | 16 | 16 | 0 |
| TUTOR_JMETAL_LZ09_OBJECTIVE | true | 1 | 1 | 42 | 39 | -3 |

#### RELAXED

| caseId | eligible | applied | passes | CC before | CC after | Δ |
|---|---|---|---|---|---|---|
| TUTOR_BCV_EZ_INJECTION_EXECUTE | false | 0 | 0 | 124 | 124 | 0 |
| TUTOR_BCV_RESOURCE_DECOMPILE_SAVE_ALL | false | 0 | 0 | 1 | 1 | 0 |
| TUTOR_BCV_RESOURCE_DECOMPILE_SAVE_OPENED | false | 0 | 0 | 2 | 2 | 0 |
| TUTOR_JMETAL_EBES_READ_DATA_FILE | false | 0 | 0 | 126 | 126 | 0 |
| TUTOR_JMETAL_EBES_VARIABLE_POSITION | false | 0 | 0 | 16 | 16 | 0 |
| TUTOR_JMETAL_LZ09_OBJECTIVE | true | 1 | 1 | 42 | 39 | -3 |

