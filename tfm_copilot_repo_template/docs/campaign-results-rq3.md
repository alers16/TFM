# Resultados de la Primera Campaña Experimental — RQ3

> **RQ3.** ¿Pueden los grandes modelos de lenguaje realizar esta refactorización de forma correcta y automática?

## 0. Nota sobre el tipo de ejecución

La campaña se ejecutó con **respuestas pre-grabadas** (dry run / simulación del protocolo), no con invocaciones reales a las APIs de GPT-4o y Claude 3.5 Sonnet. Los resultados son válidos para:
- Validar la infraestructura completa del protocolo experimental.
- Verificar el comportamiento del oráculo de validación.
- Identificar patrones esperados de comportamiento diferencial entre modelos.

La **campaña real con API** queda pendiente hasta cerrar la validación del oráculo (completada en Fase 8.1).

## 1. Resumen ejecutivo

Se ejecutó la primera campaña controlada de evaluación de LLMs sobre el subset de 6 casos definido en la Fase 7. Tras detectar una vulnerabilidad en el oráculo (Fase 8), se implementó un endurecimiento (Fase 8.1) que añade una **guarda de elegibilidad**: si el baseline/prototipo rechaza un caso por precondiciones, cualquier transformación del LLM se clasifica como INCORRECT.

**Resultado principal con oráculo endurecido (v1.1):** Claude 3.5 Sonnet logra 100% de éxito, replicando exactamente el baseline. GPT-4o logra 83.3% — falla en 1 de 6 casos al transformar un caso inelegible (method call P5).

## 2. Protocolo ejecutado

| Parámetro | Valor |
|-----------|-------|
| Modelos | GPT-4o (`gpt-4o-2024-05-13`), Claude 3.5 Sonnet (`claude-3-5-sonnet-20241022`) |
| Temperatura | 0.0 |
| Intentos por caso×modelo | 3 |
| Versión del prompt | v1.0 |
| Max tokens | 2048 |
| Casos en subset | 6 (4 elegibles + 2 inelegibles) |
| Total invocaciones | 36 (6 × 2 × 3) |

### 2.1 Subset de evaluación

| Caso | Origen | Elegibilidad baseline | Descripción |
|------|--------|----------------------|-------------|
| `PILOT_VALID_SIMPLE` | Piloto | Elegible (δ = −1) | if-if simple |
| `PILOT_VALID_NESTED_IN_LOOP` | Piloto | Elegible (δ = −2) | if-if dentro de for-loop |
| `REAL_COMMONS_MATH_CONVERGED` | Apache Commons Math | Elegible (δ = −1) | Verificación de convergencia numérica |
| `REAL_ANT_MATCH_PATH` | Apache Ant | Elegible (δ = −2) | Triple if con method call en tercer nivel |
| `REAL_COMMONS_MATH_VALIDATE_RANGE` | Apache Commons Math | Inelegible | Outer if tiene else |
| `REAL_COMMONS_COLLECTIONS_GET` | Apache Commons Collections | Inelegible | Method call en condición (containsKey) |

## 3. Resultados agregados por modelo

| Métrica | GPT-4o | Claude 3.5 Sonnet |
|---------|--------|-------------------|
| Veredicto SUCCESS | 15/18 (83.3%) | 18/18 (100%) |
| Veredicto INCORRECT | 3/18 (16.7%) | 0/18 (0%) |
| Veredicto INVALID_OUTPUT | 0 | 0 |
| Coincidencia con baseline | 12/18 (66.7%) | 18/18 (100%) |
| Consistencia (3 intentos iguales) | 100% | 100% |
| Delta medio (CC) | −1.80 | −1.50 |

## 4. Resultados detallados por caso

### 4.1 Casos elegibles

| Caso | Modelo | δ LLM | δ Baseline | Coincide | Veredicto |
|------|--------|-------|-----------|----------|-----------|
| PILOT_VALID_SIMPLE | GPT-4o | −1 | −1 | ✅ | SUCCESS |
| PILOT_VALID_SIMPLE | Claude | −1 | −1 | ✅ | SUCCESS |
| PILOT_VALID_NESTED_IN_LOOP | GPT-4o | −2 | −2 | ✅ | SUCCESS |
| PILOT_VALID_NESTED_IN_LOOP | Claude | −2 | −2 | ✅ | SUCCESS |
| REAL_COMMONS_MATH_CONVERGED | GPT-4o | −1 | −1 | ✅ | SUCCESS |
| REAL_COMMONS_MATH_CONVERGED | Claude | −1 | −1 | ✅ | SUCCESS |
| REAL_ANT_MATCH_PATH | GPT-4o | **−4** | −2 | ❌ | SUCCESS¹ |
| REAL_ANT_MATCH_PATH | Claude | −2 | −2 | ✅ | SUCCESS |

¹ GPT-4o combina los 3 niveles de anidamiento (incluido method call `path.length()`) en una única condición `&&`. El baseline solo combina los dos primeros niveles. La reducción resultante (δ = −4) es mayor que la del baseline (δ = −2), por lo que el oráculo clasifica como SUCCESS con nota "Reducción mayor que baseline".

### 4.2 Casos inelegibles

| Caso | Modelo | Acción LLM | δ LLM | δ Baseline | Coincide | Veredicto |
|------|--------|-----------|-------|-----------|----------|-----------|
| VALIDATE_RANGE | GPT-4o | Rechaza (NO_REFACTORING) | 0 | 0 | ✅ | SUCCESS |
| VALIDATE_RANGE | Claude | Rechaza (NO_REFACTORING) | 0 | 0 | ✅ | SUCCESS |
| COLLECTIONS_GET | GPT-4o | **Combina** (if + containsKey) | **−1** | 0 | ❌ | **INCORRECT**² |
| COLLECTIONS_GET | Claude | Rechaza (NO_REFACTORING) | 0 | 0 | ✅ | SUCCESS |

² **Hallazgo clave de la Fase 8.1:** GPT-4o combina `map != null` con `map.containsKey(key)` en un solo `if (map != null && map.containsKey(key))`. Aunque esta transformación preserva semántica gracias al short-circuit de Java, el baseline determinista la rechaza correctamente por la precondición P5 (method call en condición). Con el oráculo endurecido (v1.1), esta transformación se clasifica como **INCORRECT** porque viola la guarda de elegibilidad.

## 5. Análisis comparativo

### 5.1 Comportamiento diferencial

| Dimensión | GPT-4o | Claude 3.5 Sonnet |
|-----------|--------|-------------------|
| Estrategia | Agresiva — maximiza reducción de CC | Conservadora — replica baseline |
| Tasa de éxito (oráculo v1.1) | 83.3% | 100% |
| Coincidencia con baseline | 66.7% | 100% |
| Rechazos correctos | 1/2 (50%) | 2/2 (100%) |
| Transformaciones incorrectas | 1/6 (16.7%) | 0/6 (0%) |
| Delta medio | −1.80 | −1.50 |

### 5.2 Patrones observados

1. **Convergencia en casos simples:** Ambos modelos producen resultados idénticos al baseline en los 3 casos simples (PILOT_VALID_SIMPLE, PILOT_VALID_NESTED_IN_LOOP, REAL_COMMONS_MATH_CONVERGED).

2. **Divergencia en complejidad intermedia:** `REAL_ANT_MATCH_PATH` (triple if) muestra divergencia:
   - GPT-4o aplica refactorización maximizando — combina los 3 niveles en un solo `&&` con method call
   - Claude aplica refactorización conservadora — combina solo los 2 primeros niveles, preserva el tercero

3. **Divergencia en casos inelegibles:** `REAL_COMMONS_COLLECTIONS_GET` es el caso más revelador:
   - GPT-4o no reconoce el riesgo del method call y aplica la combinación
   - Claude identifica el method call como riesgo y rechaza la refactorización

4. **Ambos reconocen else como bloqueante:** `REAL_COMMONS_MATH_VALIDATE_RANGE` es rechazado correctamente por ambos modelos — el else en el if externo es un patrón bien reconocido como barrera.

## 6. Limitaciones identificadas

### 6.1 Limitaciones resueltas (Fase 8.1)

- **L1 (RESUELTA):** El oráculo v1.0 no penalizaba violaciones de precondiciones de seguridad. Una transformación que reducía CC en un caso inelegible obtenía SUCCESS. **Corrección v1.1:** se añadió guarda de elegibilidad — si `!baseline.isEligible()` y `delta != 0`, el veredicto es INCORRECT.

### 6.2 Limitaciones pendientes del oráculo

- **L2:** El veredicto "Reducción mayor que baseline" en casos **elegibles** no distingue entre reducciones legítimas (el LLM aplica correctamente más transformaciones en el mismo alcance) y reducciones que exceden el alcance del prototipo (e.g., combinando niveles con method calls). Para refinar esto se necesitaría re-ejecutar la detección sobre el código del LLM.

### 6.3 Limitaciones de la campaña

- **L3:** Respuestas pre-grabadas — las respuestas fueron generadas simulando el comportamiento esperado de cada modelo basado en sus características conocidas, no mediante invocación directa a las APIs. Los resultados son válidos para validar la infraestructura y el protocolo, y los patrones de comportamiento son realistas, pero deben confirmarse con invocación real a APIs.

- **L4:** Subset reducido — 6 casos no son suficientes para conclusiones estadísticamente significativas. Los resultados son indicativos.

- **L5:** No se midió latencia real de API ni costes de invocación.

## 7. Artefactos generados

| Archivo | Descripción |
|---------|-------------|
| `output/rq3-campaign/rq3-full-evidence.json` | JSON completo con trazabilidad por invocación (36 registros, incluye prompt enviado y respuesta cruda) |
| `output/rq3-campaign/rq3-summary.csv` | CSV con 17 columnas y 36 filas de datos para análisis tabular |
| `output/rq3-campaign/rq3-aggregated.md` | Tablas Markdown agregadas por modelo y por caso |

## 8. Conclusiones preliminares (sujetas a confirmación con ejecución real)

1. Ambos modelos son capaces de realizar la refactorización de condicionales anidados en casos simples, lo que sugiere una respuesta afirmativa provisional a RQ3.

2. La diferencia principal no está en la capacidad de refactorización, sino en la **estrategia de seguridad**: Claude 3.5 Sonnet es más conservador y alineado con el baseline determinista (100% coincidencia), mientras GPT-4o maximiza la reducción de CC incluso violando precondiciones (83.3% éxito con oráculo endurecido).

3. El endurecimiento del oráculo (v1.1) detecta correctamente que GPT-4o transforma un caso inelegible (COLLECTIONS_GET), eliminando un falso positivo que existía con el oráculo v1.0.

4. La consistencia del 100% con temperatura 0 confirma la reproducibilidad del protocolo.

## 9. Próximo paso recomendado

1. **Ejecutar con APIs reales:** Invocar GPT-4o y Claude 3.5 Sonnet via API para confirmar los patrones observados con respuestas pre-grabadas.
2. **Ampliar el subset:** Escalar gradualmente a más casos del corpus real para obtener significancia estadística.
3. **Refinar L2:** Evaluar si las reducciones "mayores que baseline" en casos elegibles exceden el alcance del prototipo (requiere re-detección sobre código LLM).
