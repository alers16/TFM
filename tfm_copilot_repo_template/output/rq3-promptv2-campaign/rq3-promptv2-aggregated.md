# RQ3 Campaign Results — Aggregated

**Total invocations:** 36

**Protocol:** Protocol[models=[gpt-4o, gpt-4.1], temp=0.0, attempts=3, prompt=v2.0, maxTokens=2048]

## Results by Model

| Model | SUCCESS | INCORRECT | INVALID_OUTPUT | REFUSED | PARTIAL | ERROR | Success Rate | Baseline Match | Consistency |
|-------|---------|-----------|----------------|---------|---------|-------|-------------|---------------|-------------|
| gpt-4o | 18 | 0 | 0 | 0 | 0 | 0 | 100.0% | 100.0% | 100.0% |
| gpt-4.1 | 15 | 3 | 0 | 0 | 0 | 0 | 83.3% | 83.3% | 100.0% |

## Results by Case

| Case | Success Rate | Detail by Model |
|------|-------------|----------------|
| PILOT_VALID_SIMPLE | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| PILOT_VALID_NESTED_IN_LOOP | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_MATH_CONVERGED | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_ANT_MATCH_PATH | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_MATH_VALIDATE_RANGE | 100.0% | gpt-4o: 3/3, gpt-4.1: 3/3 |
| REAL_COMMONS_COLLECTIONS_GET | 50.0% | gpt-4o: 3/3, gpt-4.1: 0/3 |
