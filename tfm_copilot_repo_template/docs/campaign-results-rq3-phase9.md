# Fase 9 — Primera Campaña Real de RQ3

> **RQ3.** ¿Pueden los grandes modelos de lenguaje realizar esta refactorización de forma correcta y automática?

## Estado de ejecución

- Tipo de campaña: primera ejecución real de RQ3 (live, no dry run)
- Modo de ejecución: `live`
- Resultado operativo: **completada sin incidencias técnicas**
- Invocaciones completadas: `36/36`
- Fecha de ejecución: 2026-05-01

## Protocolo congelado

| Parámetro | Valor |
|-----------|-------|
| Modelos | `gpt-4o`, `gpt-4.1` |
| Temperatura | `0.0` |
| Intentos por caso × modelo | `3` |
| Prompt | `v1.0` (zero-shot) |
| Max tokens | `2048` |
| Subset de casos | `6` (4 elegibles + 2 inelegibles) |
| Total invocaciones | `36` (6 × 2 × 3) |
| Oráculo | `LlmResponseValidator` v1.1 |

## Resultados por modelo

| Modelo | SUCCESS | INCORRECT | Tasa éxito | Baseline match | Consistencia |
|--------|---------|-----------|-----------|---------------|-------------|
| gpt-4o | 18/18 | 0/18 | **100%** | 100% | 100% |
| gpt-4.1 | 15/18 | 3/18 | **83.3%** | 83.3% | 100% |

## Resultados por caso

| Caso | Elegibilidad baseline | gpt-4o | gpt-4.1 | Tasa global |
|------|-----------------------|--------|---------|-------------|
| `PILOT_VALID_SIMPLE` | Elegible (δ = −1) | 3/3 ✅ | 3/3 ✅ | 100% |
| `PILOT_VALID_NESTED_IN_LOOP` | Elegible (δ = −2) | 3/3 ✅ | 3/3 ✅ | 100% |
| `REAL_COMMONS_MATH_CONVERGED` | Elegible (δ = −1) | 3/3 ✅ | 3/3 ✅ | 100% |
| `REAL_ANT_MATCH_PATH` | Elegible (δ = −2) | 3/3 ✅ | 3/3 ✅ | 100% |
| `REAL_COMMONS_MATH_VALIDATE_RANGE` | Inelegible (P1 — outer else) | 3/3 ✅ | 3/3 ✅ | 100% |
| `REAL_COMMONS_COLLECTIONS_GET` | Inelegible (P5 — method call) | 3/3 ✅ | **0/3 ❌** | 50% |

### Caso problemático: `REAL_COMMONS_COLLECTIONS_GET` con gpt-4.1

gpt-4.1 combina consistentemente `map != null && map.containsKey(key)` en una sola condición, violando la precondición P5 (llamada a método). El oráculo v1.1 clasifica esto como INCORRECT porque el baseline determinista rechaza el caso. Aunque la transformación preserva semántica en Java gracias al cortocircuito de `&&`, está fuera del alcance de las precondiciones P1–P5 del prototipo.

**gpt-4o** rechaza correctamente la transformación en todos los intentos.

## Campañas complementarias ejecutadas posteriormente

### Campaña OpenAI-only (rq3-openai-only)

Modo exploratorio con un solo modelo para validación incremental. 18 invocaciones (6 casos × 1 modelo × 3 intentos).

| Modelo | SUCCESS | Tasa éxito |
|--------|---------|-----------|
| gpt-4o | 18/18 | **100%** |

Artefactos en `output/rq3-openai-only/`.

### Campaña trampa — tasa de falsos positivos (rq3-trap-campaign)

3 casos trampa × 2 modelos × 3 intentos = 18 invocaciones. Cada caso trampa viola exactamente una precondición de forma no obvia.

| Caso trampa | Precondición | gpt-4o | gpt-4.1 |
|-------------|-------------|--------|---------|
| `TRAP_P4_INNER_ELSE` | P4 — if interno con `else` | 3/3 ✅ | 3/3 ✅ |
| `TRAP_P2_MULTI_STATEMENT` | P2 — then externo con 2 sentencias | 3/3 ✅ | 3/3 ✅ |
| `TRAP_P5_ASSIGNMENT` | P5 — asignación en condición interna | 3/3 ✅ | 3/3 ✅ |

**Tasa de falsos positivos: 0% (0/18).** Artefactos en `output/rq3-trap-campaign/`.

### Comparativa prompt v2.0 few-shot (rq3-promptv2-campaign)

Mismos 6 casos de la Fase 9, mismo protocolo, únicamente se cambia el prompt a v2.0 (few-shot).
36 invocaciones totales.

| Modelo | Tasa v1.0 (zero-shot) | Tasa v2.0 (few-shot) | Variación |
|--------|----------------------|---------------------|-----------|
| gpt-4o | 100% | **100%** | 0 |
| gpt-4.1 | 83.3% | **83.3%** | 0 |

El few-shot no altera el comportamiento con temperatura 0. `REAL_COMMONS_COLLECTIONS_GET` sigue siendo el único caso que gpt-4.1 falla en ambas versiones del prompt.
Artefactos en `output/rq3-promptv2-campaign/`.

## Archivos generados (Fase 9 principal)

- `output/rq3-campaign-real-phase9/rq3-full-evidence.json`
- `output/rq3-campaign-real-phase9/rq3-summary.csv`
- `output/rq3-campaign-real-phase9/rq3-aggregated.md`
- `output/rq3-campaign-real-phase9/rq3-run-metadata.json`
- `output/rq3-campaign-real-phase9/rq3-incidents.md` (sin incidencias)

## Interpretación (respuesta a RQ3)

- **gpt-4o** realiza correctamente la refactorización en el 100% de los casos evaluados, incluyendo identificación correcta de casos inelegibles y aplicación precisa de la transformación cuando corresponde.
- **gpt-4.1** falla sistemáticamente en un caso inelegible por P5 (method call), transformándolo cuando no debería. El fallo es consistente (3/3 intentos) y robusto al cambio de prompt.
- **Falsos positivos: 0%** sobre el corpus trampa (casos diseñados para confundir). Ambos modelos son más conservadores de lo esperado ante violaciones estructurales.
- El few-shot (prompt v2.0) no aporta mejora sobre zero-shot con temperatura 0: la consistencia ya era máxima y los ejemplos no desbloquean el caso problemático de gpt-4.1.
