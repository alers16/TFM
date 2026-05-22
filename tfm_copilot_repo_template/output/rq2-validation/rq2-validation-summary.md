# RQ2 — Validación dinámica: compilación del código refactorizado

> La validación comprueba que el output de `NestedIfTransformer` sea Java compilable (`javac`). SKIPPED = caso no elegible (no se transformó).

## Resumen por modo

| Modo | PASS | FAIL | SKIPPED | ERROR |
|---|---|---|---|---|
| STRICT | 10 | 1 | 13 | 0 |
| RELAXED | 14 | 1 | 9 | 0 |

## Resultados por caso                                                                    

| Corpus | caseId | STRICT | RELAXED | Diagnóstico (si FAIL/ERROR) |
|---|---|---|---|---|
| pilot | PILOT_VALID_SIMPLE | PASS | PASS |  |
| pilot | PILOT_VALID_NULL_CHECK | PASS | PASS |  |
| pilot | PILOT_VALID_BOUNDS_CHECK | PASS | PASS |  |
| pilot | PILOT_VALID_NESTED_IN_LOOP | PASS | PASS |  |
| pilot | PILOT_INVALID_ELSE_BRANCH | SKIPPED | SKIPPED |  |
| pilot | PILOT_INVALID_METHOD_GUARD | SKIPPED | PASS |  |
| pilot | PILOT_INVALID_MULTI_STMT | SKIPPED | SKIPPED |  |
| pilot | PILOT_MIXED_OPPORTUNITIES | PASS | PASS |  |
| real | REAL_COMMONS_LANG_CHOMP | SKIPPED | PASS |  |
| real | REAL_COMMONS_LANG_IS_NUMERIC | SKIPPED | PASS |  |
| real | REAL_COMMONS_COLLECTIONS_GET | SKIPPED | PASS |  |
| real | REAL_COMMONS_COLLECTIONS_ISEMPTY | PASS | PASS |  |
| real | REAL_COMMONS_MATH_CONVERGED | PASS | PASS |  |
| real | REAL_COMMONS_MATH_VALIDATE_RANGE | SKIPPED | SKIPPED |  |
| real | REAL_ANT_EXECUTE_TASK | SKIPPED | SKIPPED |  |
| real | REAL_ANT_MATCH_PATH | PASS | PASS |  |
| real | REAL_COMMONS_LANG_CONTAINS_NONE | PASS | PASS |  |
| real | REAL_COMMONS_LANG_MID | PASS | PASS |  |
| tutor | TUTOR_BCV_EZ_INJECTION_EXECUTE | SKIPPED | SKIPPED |  |
| tutor | TUTOR_BCV_RESOURCE_DECOMPILE_SAVE_ALL | SKIPPED | SKIPPED |  |
| tutor | TUTOR_BCV_RESOURCE_DECOMPILE_SAVE_OPENED | SKIPPED | SKIPPED |  |
| tutor | TUTOR_JMETAL_EBES_READ_DATA_FILE | SKIPPED | SKIPPED |  |
| tutor | TUTOR_JMETAL_EBES_VARIABLE_POSITION | SKIPPED | SKIPPED |  |
| tutor | TUTOR_JMETAL_LZ09_OBJECTIVE | FAIL | FAIL | STRICT: L12: cannot find symbol
  symbol:   class List
  location: class TutorJmetalL... / RELAXED: L12: cannot find symbol
  symbol:   class List
  location: class TutorJmetalL... |

> **Nota metodológica:** esta validación cubre corrección sintáctica y compilabilidad, no equivalencia de comportamiento en tiempo de ejecución. La ejecución de tests del proyecto fuente (Apache Commons, Ant, BCV, jMetal) requiere descargar y configurar cada proyecto y se deja como trabajo futuro.
