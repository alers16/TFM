# Protocolo experimental para RQ3 — Comparación con LLMs

## Pregunta de investigación

**RQ3.** ¿Pueden los grandes modelos de lenguaje realizar esta refactorización
de forma correcta y automática?

> Esta pregunta se mantiene literalmente según el anteproyecto aprobado.

## Objetivo

Evaluar si los LLMs pueden detectar oportunidades de combinación de condicionales
anidados y aplicar la refactorización correctamente, comparando su rendimiento
con el del prototipo determinista bajo las mismas métricas y criterios.

## Subconjunto de evaluación

### Casos seleccionados (6)

| # | caseId | Origen | Elegibilidad | Justificación |
|---|--------|--------|-------------|---------------|
| 1 | PILOT_VALID_SIMPLE | Piloto | Elegible | Caso base mínimo: if+if puro |
| 2 | PILOT_VALID_NESTED_IN_LOOP | Piloto | Elegible | Contexto de loop, mayor impacto CC |
| 3 | REAL_COMMONS_MATH_CONVERGED | Real | Elegible | Condiciones numéricas, código OSS |
| 4 | REAL_ANT_MATCH_PATH | Real | Parcial | Triple if, solo 1 par combinable |
| 5 | REAL_COMMONS_MATH_VALIDATE_RANGE | Real | No elegible (P1) | Outer if con else |
| 6 | REAL_COMMONS_COLLECTIONS_GET | Real | No elegible (P5) | Method call en condición |

### Criterios de selección
- **Representatividad:** cubre elegibles simples, parciales y no elegibles.
- **Variedad estructural:** patrón simple, loop, triple anidamiento, else, method call.
- **Cobertura de motivos de descarte:** P1 (OUTER_HAS_ELSE), P5 (METHOD_CALL_IN_CONDITION).
- **Trazabilidad:** todos provienen del corpus validado en fases previas.
- **Tamaño manejable:** 6 × 2 modelos × 3 intentos = 36 invocaciones.

## Prompt base

### Estructura
- **System prompt:** instrucciones generales del patrón, invariables entre casos.
- **User prompt:** código fuente del caso concreto en bloque ```java.
- **Versión:** `v1.0` (trazable en cada resultado).

### Reglas del prompt
1. Combinar solo nested ifs donde outer no tiene else, inner no tiene else,
   y outer then-block contiene solo el inner if.
2. Preservar orden de evaluación.
3. No alterar semántica.
4. No cambiar firmas ni código fuera de los ifs.
5. Si no es posible: responder `NO_REFACTORING_APPLICABLE`.
6. Devolver código completo en bloque ```java.

### Justificación del diseño
- **Zero-shot:** evaluamos la capacidad base del modelo, sin ejemplos que
  puedan sesgar hacia la respuesta esperada.
- **Prompt idéntico:** garantiza comparación justa entre modelos.
- **Salida estructurada:** facilita parsing automático por el oráculo.
- **Rechazo explícito:** permite distinguir entre "no sé", "no puedo" y
  "no aplica", con un marcador detectable programáticamente.

## Protocolo experimental

### Modelos evaluados
| Modelo | Proveedor | Justificación |
|--------|-----------|---------------|
| GPT-4o | OpenAI | Estado del arte en tareas de código |
| Claude 3.5 Sonnet | Anthropic | Competidor principal, fuerte en razonamiento |

### Parámetros fijos
| Parámetro | Valor | Justificación |
|-----------|-------|---------------|
| Temperatura | 0.0 | Determinismo: misma entrada → misma salida esperada |
| Intentos por caso | 3 | Detectar inconsistencia aun con temperatura 0 |
| Max tokens salida | 2048 | Suficiente para métodos refactorizados |
| Prompt version | v1.0 | Trazabilidad |

### Procedimiento por caso
1. Cargar caso del corpus.
2. Generar prompt con `LlmPromptBuilder`.
3. Invocar modelo con parámetros fijos.
4. Guardar respuesta completa como evidencia.
5. Evaluar con `LlmResponseValidator`.
6. Registrar `LlmEvaluationResult`.
7. Repetir para cada intento (1..3).
8. Repetir para cada modelo.

### Evidencia guardada por invocación
- `caseId`, `model`, `promptVersion`, `attemptNumber`
- Prompt completo enviado
- Respuesta completa del LLM (`rawLlmOutput`)
- Código extraído (`extractedCode`)
- Veredicto (`LlmVerdict`)
- Justificación del veredicto
- CC before / after / delta
- Delta del baseline determinista
- Coincidencia con baseline (sí/no)
- Errores de validación

## Oráculo de validación

### Criterios (en orden de evaluación)

1. **Rechazo explícito:** si la respuesta contiene `NO_REFACTORING_APPLICABLE`:
   - Caso inelegible → `SUCCESS` (rechazo correcto)
   - Caso elegible → `REFUSED` (rechazo incorrecto)

2. **Extracción de código:** debe existir un bloque ```java ... ```:
   - Sin bloque → `INVALID_OUTPUT`

3. **Parseabilidad:** el código extraído debe ser parseable por JavaParser:
   - No parseable → `INVALID_OUTPUT`

4. **Preservación estructural:** debe contener un método con la misma firma:
   - Firma modificada → error de validación (no bloquea pero se registra)

5. **Medición de CC:** se mide CC del método resultante:
   - CC after < CC before y delta == baseline delta → `SUCCESS`
   - CC after < CC before pero delta distinto → `SUCCESS` (reducción mayor) o `PARTIAL`
   - CC after == CC before y baseline reduce → `INCORRECT`
   - CC after == CC before y baseline no reduce → `SUCCESS` (correcto para inelegibles)
   - CC after > CC before → `INCORRECT`

### Clasificación de veredictos
| Veredicto | Descripción |
|-----------|-------------|
| `SUCCESS` | Transformación correcta o rechazo correcto de caso inelegible |
| `INCORRECT` | Código válido pero transformación incorrecta |
| `INVALID_OUTPUT` | Salida no parseable o sin bloque de código |
| `REFUSED` | Rechazo incorrecto de caso elegible |
| `PARTIAL` | Reducción menor que el baseline |
| `ERROR` | Fallo técnico (timeout, red, etc.) |

## Estructura de resultados

Cada invocación produce un `LlmEvaluationResult` con los campos:

| Campo | Tipo | Descripción |
|-------|------|-------------|
| caseId | String | Identificador del caso |
| model | String | Modelo LLM |
| promptVersion | String | Versión del prompt |
| attemptNumber | int | Número de intento (1..3) |
| rawLlmOutput | String | Respuesta completa del LLM |
| extractedCode | String | Código extraído, o null |
| verdict | LlmVerdict | Clasificación del resultado |
| verdictReason | String | Justificación del veredicto |
| parseable | boolean | ¿Código parseable? |
| complexityBefore | int | CC antes (del caso original) |
| complexityAfter | int | CC después (de la salida LLM) |
| delta | int | Cambio de CC |
| baselineDelta | int | Delta del prototipo determinista |
| matchesBaseline | boolean | ¿Mismo delta que baseline? |
| validationErrors | List | Errores detectados |
| observations | String | Notas adicionales |

### Métricas agregadas por modelo
- **Tasa de éxito:** % de invocaciones con veredicto SUCCESS
- **Tasa de rechazo correcto:** % de rechazos correctos sobre casos inelegibles
- **Tasa de salida inválida:** % de INVALID_OUTPUT
- **Coincidencia con baseline:** % de matchesBaseline == true
- **Consistencia:** % de intentos con el mismo veredicto para un mismo caso
- **Delta medio:** media del delta en casos con SUCCESS

## Amenazas a la validez

### Validez interna
- **Sesgo del prompt:** un prompt diferente podría obtener resultados distintos.
  Mitigación: documentar prompt exacto, versionar, considerar variantes en futuro.
- **CC provisional:** el calculador del prototipo es una aproximación.
  Mitigación: contraste documentado con especificación SonarSource (Fase 6).
- **Oráculo parcial:** el oráculo verifica CC y parseabilidad, no equivalencia
  semántica formal. Mitigación: inspección manual de casos SUCCESS.

### Validez externa
- **Subconjunto pequeño:** 6 casos no representan toda la variedad de código real.
  Mitigación: subconjunto diseñado para cubrir ejes de variación principales.
- **Solo 2 modelos:** el estado del arte cambia rápidamente.
  Mitigación: infraestructura extensible, protocolo documentado para replicar.

### Validez de constructo
- **Refactorización dirigida:** el prompt pide un patrón específico, no
  refactorización genérica. Esto limita la generalización pero aumenta
  la comparabilidad con el prototipo.
- **Temperatura 0:** reduce variabilidad pero no la elimina completamente
  (algunos modelos no son deterministas incluso con temperatura 0).

## Contribución a RQ3

Esta fase aporta:
1. **Infraestructura completa** para ejecutar la campaña experimental.
2. **Subconjunto validado** contra el baseline determinista.
3. **Prompt base reproducible** y versionado.
4. **Oráculo automático** con criterios objetivos y trazables.
5. **Estructura de resultados** directamente comparable con el prototipo.

## Pendiente para ejecución de campaña

- [ ] Ejecutar invocaciones reales contra APIs de GPT-4o y Claude 3.5 Sonnet.
- [ ] Guardar respuestas crudas como archivos de evidencia.
- [ ] Generar informe agregado con métricas por modelo.
- [ ] Inspección manual de casos SUCCESS para validar semántica.
- [ ] Considerar ampliación del subconjunto si los resultados lo justifican.
- [ ] Documentar resultados en la memoria del TFM.
