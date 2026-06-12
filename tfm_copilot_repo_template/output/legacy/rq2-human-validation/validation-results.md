# Resultados de la validación humana del pipeline automático (RQ1/RQ2)

- **Fecha:** 2026-06-03
- **Muestra:** N = 30 casos (20 elegibles + 10 no elegibles), muestreo reproducible
  (semilla 42) sobre el corpus consolidado de 126 casos
  (`output/rq2-batch/rq2-summary.md` / `rq2-all-results.csv`).
- **Instrumento:** [`validation-sample.csv`](validation-sample.csv) (columnas `humano_*` cumplimentadas).
- **Protocolo:** [`docs/human-validation-protocol.md`](../../docs/human-validation-protocol.md).

## Método de validación (qué se hizo)

Para cada caso se contrastó el veredicto automático contra una revisión manual,
en las cuatro etapas del protocolo:

1. **Detección `if`-`if`** (`humano_deteccion_ok`): presencia/ausencia del patrón.
2. **Precondiciones P1–P5** (`humano_precondiciones_ok`): corrección de la decisión de
   elegibilidad y, si aplica, del `DiscardReason`.
3. **CC before** (`humano_cc_before_ok`): recálculo manual de la complejidad cognitiva
   (modelo SonarSource: +1 estructural por `if`/`else`/`for`/`while`/`catch`/ternario,
   +nivel de anidamiento, +1 por secuencia de operadores `&&`/`||`) sobre el método original.
4. **CC after** (`humano_cc_after_ok`): ídem sobre el método refactorizado.

Marcado `1` = veredicto automático correcto, `0` = incorrecto.

> **Trazabilidad de la verificación de CC.** Todas las CC ≤ 57 se recalcularon
> íntegramente a mano y **coincidieron exactamente** con `auto_cc_before`
> (p. ej. 19, 57, 36, 42, 19, 11, 10, 7, 6, 3…). Para los **2 métodos KNOWAGE**
> con CC > 180 (casos 1 y 15) no se recomputó el valor absoluto a mano por
> inviabilidad; se validó la **estructura del patrón** y la **consistencia del
> delta** (−9, coherente con el desanidado de un bloque que contiene varias
> estructuras anidadas). Esta limitación queda registrada como nota en el CSV.

## Resultado por etapa

| Etapa | Aciertos | N | % acierto | IC 95 % (Wilson) |
|---|---|---|---|---|
| Detección `if`-`if` | 30 | 30 | 100,0 % | [88,6 % – 100 %] |
| Precondiciones P1–P5 | 30 | 30 | 100,0 % | [88,6 % – 100 %] |
| CC before | 30 | 30 | 100,0 % | [88,6 % – 100 %] |
| CC after | 30 | 30 | 100,0 % | [88,6 % – 100 %] |

IC de Wilson al 95 % (z = 1,96) para p̂ = 30/30 = 1,0:
centro = 0,9432; margen = 0,0568 ⇒ **[0,8865 – 1,0000]**.
El límite inferior 88,6 % es el umbral de confianza conservador del pipeline para
esta muestra.

## Verdicto por caso (30/30 = 100 % de acuerdo)

### Grupo elegible (20 casos) — todos `1/1/1/1`

| # | Caso | CC b→a (Δ) | Verificación |
|---|---|---|---|
| 1 | KNOWAGE getDriversFromQbeDataSet | 205→196 (−9) | patrón `oVals!=null`/`instanceof List` combinable; CC absoluta no recomputada, delta consistente |
| 2 | FASTJSON patchAdd | 19→16 (−3) | CC recomputada a mano ✓ |
| 3 | ANT stripJavaComments read | 57→48 (−9) | CC recomputada a mano ✓ (desanidado de loops) |
| 4 | JACKSON _decodeBase64Escape | 7→5 (−2) | CC ✓; 2 merges |
| 5 | REAL_ANT scan | 3→2 (−1) | CC ✓; guarda null + flag |
| 6 | POI setConditionType | 4→3 (−1) | CC ✓ |
| 7 | JACKSON _skipColonFast | 36→28 (−8) | CC ✓; 2 merges simétricos |
| 8 | JACKSON _releaseBuffers | 6→4 (−2) | CC ✓ |
| 9 | JMETAL objective | 42→39 (−3) | CC ✓; `nobj==3 && (ltype...)` |
| 10 | POI containsCell | 11→8 (−3) | CC ✓ |
| 11 | ANT concat read | 11→9 (−2) | CC ✓ |
| 12 | JACKSON setAndReturn | 5→4 (−1) | CC ✓ |
| 13 | JACKSON findEnum | 3→2 (−1) | CC ✓ |
| 14 | ANT getURL | 10→7 (−3) | CC ✓; métodos en `if` no fusionado |
| 15 | KNOWAGE transformRuntimeDrivers | 187→178 (−9) | misma forma que #1; CC absoluta no recomputada, delta consistente |
| 16 | JACKSON assignIndex | 3→2 (−1) | CC ✓ |
| 17 | POI evaluateInternal | 19→17 (−2) | CC ✓ |
| 18 | JACKSON _delegateDeserializer | 7→5 (−2) | CC ✓; par interno con `hasToken()` correctamente descartado |
| 19 | ANT setDest | 3→2 (−1) | CC ✓ |
| 20 | PILOT_VALID_SIMPLE | 3→2 (−1) | CC ✓; caso canónico |

### Grupo no elegible (10 casos) — todos `1/1/1/1`

| # | Caso | CC | Motivo de descarte (correcto) |
|---|---|---|---|
| 21 | hasContent | 3 | METHOD_CALL_IN_CONDITION (`s.isEmpty()`) — *falso negativo conservador* |
| 22 | checkBound | 3 | INCREMENT_OR_DECREMENT (`--count`) — side effect real |
| 23 | FloatDeser deserialize | 2 | SINGLE_STATEMENT_NOT_IF (sin patrón if-if) |
| 24 | getObject | 3 | METHOD_CALL_IN_CONDITION (`map.containsKey()`) — *falso negativo conservador* |
| 25 | DoubleDeser deserialize | 2 | SINGLE_STATEMENT_NOT_IF (sin patrón if-if) |
| 26 | POI dateFunc evaluate | 1 | Sin oportunidades (solo try/catch) |
| 27 | REAL_ANT executeTask | 3 | OUTER_BLOCK_MULTIPLE_STATEMENTS |
| 28 | isNumeric | 10 | METHOD_CALL_IN_CONDITION + bloque externo múltiple |
| 29 | chomp | 6 | METHOD_CALL_IN_CONDITION + bloque externo múltiple |
| 30 | processIfNotEmpty | 3 | METHOD_CALL_IN_CONDITION — *falso negativo conservador* |

## Observaciones relevantes para amenazas a la validez

1. **Falsos negativos conservadores (casos 21, 24, 30; y parcialmente 28, 29).**
   El detector rechaza condiciones con llamadas a método (`s.isEmpty()`,
   `map.containsKey()`, `input.isEmpty()`) que en la práctica **no tienen side
   effects** y serían combinables. La decisión es **correcta según P5** (política
   conservadora del MVP), por lo que la etapa de precondiciones puntúa `1`; pero
   el acierto del 100 % mide la **fidelidad de la herramienta a sus propias reglas**,
   no la optimalidad de esas reglas. La precisión es alta a costa de la exhaustividad
   (recall) — discútase en RQ1 / amenazas a la validez.

2. **CC de métodos grandes (casos 1 y 15).** El valor absoluto (>180) no se
   reverificó a mano; solo el patrón y el delta. Para un control de validez total
   convendría contrastar estos dos con SonarQube.

3. **Acuerdo del 100 % sobre una muestra curada.** El instrumento se generó a
   partir de los propios resultados del pipeline; la coincidencia total es
   esperable en casos limpios y conservadores, pero el IC de Wilson sitúa el
   suelo de confianza en **88,6 %**, que es el valor a reportar.

## Siguiente paso

Trasladar la tabla "Resultado por etapa" a la sección §Validación humana de
[`memoria/secciones/04e-resultados.tex`](../../memoria/secciones/04e-resultados.tex)
y retirar los marcadores `\pendienteverif{}` una vez incorporada.
