# RQ3 Campaign Results — Aggregated

**Total invocations:** 300

**Protocol:** Protocol[models=[gpt-4o, gpt-4.1], temp=0.0, attempts=3, prompt=v1.0, maxTokens=2048]

## Results by Model

| Model | SUCCESS | INCORRECT | INVALID_OUTPUT | REFUSED | PARTIAL | ERROR | Success Rate | Baseline Match | Consistency |
|-------|---------|-----------|----------------|---------|---------|-------|-------------|---------------|-------------|
| gpt-4o | 122 | 0 | 0 | 28 | 0 | 0 | 81.3% | 78.7% | 96.0% |
| gpt-4.1 | 145 | 5 | 0 | 0 | 0 | 0 | 96.7% | 94.0% | 98.0% |

## Results by Case

| Case | Success Rate | Detail by Model |
|------|-------------|----------------|
| PILOT_INVALID_ELSE_BRANCH | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| PILOT_INVALID_METHOD_GUARD | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| PILOT_INVALID_MULTI_STMT | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| PILOT_MIXED_OPPORTUNITIES | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| PILOT_VALID_BOUNDS_CHECK | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| PILOT_VALID_NESTED_IN_LOOP | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| PILOT_VALID_NULL_CHECK | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| PILOT_VALID_SIMPLE | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| ANT_ANT_CLASS_LOADER_INITIALIZE_CLASS | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| ANT_AVAILABLE_EVAL | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| ANT_CONCAT_FILTER_READ | 50.0% | gpt-4o: 0/3, gpt-4.1: 3/3 |
| REAL_ANT_EXECUTE_TASK | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| ANT_HEAD_FILTER_HEAD_FILTER | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| ANT_J_UNIT_TASK_CREATE_CLASS_LOADER | 50.0% | gpt-4o: 0/3, gpt-4.1: 3/3 |
| REAL_ANT_MATCH_PATH | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| ANT_PATH_CONVERT_SET_DEST | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_ANT_SCAN_ENABLED | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| ANT_STRIP_JAVA_COMMENTS_READ | 50.0% | gpt-4o: 0/3, gpt-4.1: 3/3 |
| ANT_TAR_BUFFER_READ_BLOCK | 50.0% | gpt-4o: 0/3, gpt-4.1: 3/3 |
| ANT_U_R_L_RESOURCE_GET_U_R_L | 0.0% | gpt-4o: 0/3, gpt-4.1: 0/3 |
| BYTECODE_VIEWER_BYTECODE_VIEWER_BOOT | 66.7% | gpt-4o: 1/3, gpt-4.1: 3/3 |
| BYTECODE_VIEWER_MALWARE_CODE_SCANNER_SCAN_METHODS | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| BYTECODE_VIEWER_REPLACE_STRINGS_SCAN_CLASS_NODE | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| BYTECODE_VIEWER_SHOW_ALL_STRINGS_EXECUTE | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_COLLECTIONS_GET | 66.7% | gpt-4o: 3/3, gpt-4.1: 1/3 |
| REAL_COMMONS_COLLECTIONS_INNER_ELSE | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_COLLECTIONS_ISEMPTY | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| COMMONS_COLLECTIONS_PERMUTATION_ITERATOR_NEXT | 50.0% | gpt-4o: 0/3, gpt-4.1: 3/3 |
| REAL_COMMONS_IO_ARRAY_SUBRANGE | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_LANG_ASSIGNMENT_COND | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_LANG_CHOMP | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_LANG_CONTAINS_NONE | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_LANG_DECREMENT_COND | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_LANG_INNER_METHOD_CALL | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_LANG_IS_NUMERIC | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_LANG_MID | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_LANG_TRIPLE_GUARD | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_LANG_VALID_INDEX | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_MATH_CONVERGED | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_MATH_IN_INTERVAL | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_MATH_NO_NESTING | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_MATH_VALIDATE_RANGE | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| CYBERCAPTOR_VERTEX_GET_RELATED_MACHINE | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| FASTJSON_DEFAULT_J_S_O_N_PARSER_PARSE_ARRAY | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| FASTJSON_FIELD_SERIALIZER_GET_PROPERTY_VALUE | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| FASTJSON_J_S_O_N_LEXER_BASE_SCAN_FIELD_INT | 50.0% | gpt-4o: 0/3, gpt-4.1: 3/3 |
| FASTJSON_J_S_O_N_LEXER_BASE_SCAN_SYMBOL_UN_QUOTED | 50.0% | gpt-4o: 0/3, gpt-4.1: 3/3 |
| FASTJSON_J_S_O_N_OBJECT_CONTAINS_KEY | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| FASTJSON_J_S_O_N_OBJECT_GET | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| FASTJSON_J_S_O_N_PATH_EQ_NOT_NULL | 66.7% | gpt-4o: 1/3, gpt-4.1: 3/3 |
