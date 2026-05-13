# 4.6 Resultados

## Objetivo de la sección

Presentar los resultados experimentales por RQ, citando exclusivamente artefactos presentes en `output/`.

## Subsecciones previstas

- **4.6.1 Resultados RQ1 — Taxonomía de aplicabilidad.**
  - Las precondiciones P1–P5 cubren el patrón canónico.
  - Distribución de motivos de descarte en el corpus consolidado (n=18). Datos: [output/rq2-batch/rq2-summary.md](../output/rq2-batch/rq2-summary.md).
- **4.6.2 Resultados RQ2 — Impacto en complejidad cognitiva.**
  - n=18 (piloto 8 + real 10). Elegibles: 10/18.
  - Δ complejidad cognitiva agregado (proxy): **−18**.
  - Tabla por caso (incluir desde [output/rq2-batch/rq2-summary.md](../output/rq2-batch/rq2-summary.md)).
- **4.6.3 Validación cruzada con SonarQube.**
  - Subset de 4 casos. Coincidencia 8/8 en mediciones individuales (4 *before* + 4 *after*). Ver [output/rq2-sonar-validation/rq2-sonar-validation.md](../output/rq2-sonar-validation/rq2-sonar-validation.md).
  - [PENDIENTE DE VERIFICACIÓN] Confirmar si `sonar-before.json` / `sonar-after.json` provienen de una corrida real de SonarQube o son valores pre-rellenos del proxy. La cabecera del propio archivo declara estado de plantilla.
- **4.6.4 Resultados RQ3 — Campaña fase 9.**
  - 36 invocaciones (6 casos × 2 modelos × 3 intentos), protocolo congelado, T=0, prompt v1.0.
  - `gpt-4o`: 18/18 SUCCESS (100%), baseline match 100%, consistencia 100%.
  - `gpt-4.1`: 15/18 SUCCESS (83.3%), 3 INCORRECT en `REAL_COMMONS_COLLECTIONS_GET`.
  - Evidencia: [output/rq3-campaign-real-phase9/rq3-aggregated.md](../output/rq3-campaign-real-phase9/rq3-aggregated.md), [rq3-summary.csv](../output/rq3-campaign-real-phase9/rq3-summary.csv), [rq3-full-evidence.json](../output/rq3-campaign-real-phase9/rq3-full-evidence.json).
  - Incidentes: [output/rq3-campaign-real-phase9/rq3-incidents.md](../output/rq3-campaign-real-phase9/rq3-incidents.md).
- **4.6.5 Discusión integrada.** Cómo se interrelacionan los resultados de RQ1, RQ2 y RQ3.

## Evidencias del repositorio

- [output/rq2-batch/rq2-summary.md](../output/rq2-batch/rq2-summary.md)
- [output/rq2-batch/rq2-all-results.csv](../output/rq2-batch/rq2-all-results.csv)
- [output/rq2-sonar-validation/rq2-sonar-validation.md](../output/rq2-sonar-validation/rq2-sonar-validation.md)
- [output/rq3-campaign-real-phase9/](../output/rq3-campaign-real-phase9/)
- [output/rq3-openai-only/](../output/rq3-openai-only/)
- [docs/campaign-results-rq3-phase9.md](../docs/campaign-results-rq3-phase9.md)

## Figuras asociadas

- *Fig. 4.9* — Δ CC por caso (RQ2).
- *Fig. 4.10b* — Proxy vs Sonar (subset).
- *Fig. 4.11* — Comparativa `gpt-4o` vs `gpt-4.1`.

## Notas

- **No inventar resultados.** Citar únicamente cifras presentes en archivos del repositorio.
- [PENDIENTE DE VERIFICACIÓN] Estado real de la corrida SonarQube (§4.6.3).
