# ValidaciÃ³n de contraste proxy vs SonarQube â€” corpus completo

- **Generado:** 2026-06-03T15:25:48.7561933Z  Â·  **Operador:** Ale Roman  Â·  **Host:** DESKTOP-7E8R1FA
- **SonarQube:** versiÃ³n 26.4.0.121862 en http://localhost:9000
- **Proyectos:** `tfm-rq2-fullcorpus-before` / `tfm-rq2-fullcorpus-after`
- **Casos:** 126 materializados, 122 medidos por SonarQube, 4 sin medida (no parseables).

## Coincidencia proxy â†” SonarQube

| Medida | Coincidencias | Total medidos | % |
|---|---|---|---|
| Complejidad *before* | 109 | 122 | 89.3 |
| Complejidad *after*  | 109  | 122 | 89.3 |

## Divergencias (|Î”| â‰¥ 1 punto)

| caseId | proxyBefore | sonarBefore | proxyAfter | sonarAfter |
|---|---|---|---|---|
| FASTJSON_J_S_O_N_LEXER_BASE_SCAN_FIELD_INT | 31 | 32 | 29 | 30 |
| JACKSON_CORE_NON_BLOCKING_UTF8_JSON_PARSER_BASE_FINISH_C_COMMENT | 16 | 18 | 14 | 16 |
| JACKSON_CORE_READER_BASED_JSON_PARSER_NEXT_TOKEN | 21 | 22 | 19 | 20 |
| KNOWAGE_BUSINESS_MODEL_OPEN_PARAMETERS_GET_BUSINESS_MODEL_EXECUTION_FILTERS | 157 | 180 | 150 | 171 |
| KNOWAGE_BUSINESS_MODEL_RESOURCE_GET_DRIVERS_FROM_QBE_DATA_SET | 183 | 205 | 176 | 196 |
| KNOWAGE_DATA_SET_RESOURCE_GET_DRIVERS_FROM_QBE_DATA_SET | 183 | 205 | 176 | 196 |
| KNOWAGE_DATA_SET_RESOURCE_TRANSFORM_RUNTIME_DRIVERS | 167 | 190 | 160 | 181 |
| KNOWAGE_DOCUMENT_EXECUTION_PARAMETERS_TRANSFORM_RUNTIME_DRIVERS | 166 | 189 | 159 | 180 |
| KNOWAGE_DOCUMENT_EXECUTION_RESOURCE_GET_DOCUMENT_EXECUTION_FILTERS | 228 | 257 | 220 | 247 |
| KNOWAGE_DOCUMENT_EXECUTION_RESOURCE_TRANSFORM_RUNTIME_DRIVERS | 165 | 187 | 158 | 178 |
| KNOWAGE_META_UTILS_TRANSFORM_RUNTIME_DRIVERS | 166 | 189 | 159 | 180 |
| MOEA_COMMAND_LINE_UTILITY_GET_CONSOLE_WIDTH | 13 | 15 | 10 | 11 |
| POI_RELATIONAL_OPERATION_EVAL_DO_COMPARE | 19 | 21 | 18 | 20 |

> Detalle completo por caso en `comparison.csv`. Las medidas oficiales crudas
> estÃ¡n en `sonar-before.json` y `sonar-after.json`.
