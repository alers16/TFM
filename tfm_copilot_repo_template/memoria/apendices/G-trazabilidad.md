# Apéndice G. Trazabilidad RQ ↔ artefactos ↔ tests

## Objetivo

Garantizar que cada RQ está respaldada por código, tests y evidencia experimental.

## Mapa previsto

| RQ | Diseño (memoria) | Implementación | Tests | Evidencia |
|---|---|---|---|---|
| RQ1 | [cap4-diseno.md](../cap4-diseno.md) §4.2.1 (P1–P5) | `detection/`, `transformation/` | `surefire-reports/...detection*`, `...transformation*` | [output/rq2-batch/rq2-summary.md](../../output/rq2-batch/rq2-summary.md) (motivos de descarte agregados) |
| RQ2 | [cap4-experimento.md](../cap4-experimento.md) §4.5.3 | `analysis/`, `experiment/BatchRunner` | `surefire-reports/...CognitiveComplexityCalculatorTest*`, `...BatchRunnerTest*` | [output/rq2-batch/](../../output/rq2-batch/), [output/rq2-sonar-validation/](../../output/rq2-sonar-validation/) |
| RQ3 | [cap4-experimento.md](../cap4-experimento.md) §4.5.5 | `llm/*` | tests del paquete `llm` en `surefire-reports/` | [output/rq3-campaign-real-phase9/](../../output/rq3-campaign-real-phase9/), [output/rq3-openai-only/](../../output/rq3-openai-only/), [docs/campaign-results-rq3-phase9.md](../../docs/campaign-results-rq3-phase9.md) |

## Notas

- Mantener este mapa actualizado al añadir nuevos resultados.
- Figura asociada: *Fig. 4.12* (ver [PLAN_VISUAL.md](../PLAN_VISUAL.md)).
