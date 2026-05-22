# RQ2 — Validación complementaria con SonarQube

> **Estado:** VALIDADO — ejecución real completada el 2026-05-18.
> **SonarQube Community Build:** 26.4.0.121862 · servidor local Docker.
> **Resultado:** 8/8 medidas individuales con coincidencia exacta (divergencia = 0).
>
> Procedimiento de ejecución:
> [docs/rq2-sonar-validation-procedure.md](../../docs/rq2-sonar-validation-procedure.md).
> Arranque del servidor:
> [docs/sonarqube-setup.md](../../docs/sonarqube-setup.md).
> Artefactos brutos: `sonar-before.json`, `sonar-after.json` (este directorio).

## 1. Contexto

Esta validación es **complementaria**: contrasta la métrica de
complejidad cognitiva calculada por el prototipo
(`CognitiveComplexityCalculator`) contra la implementación oficial de
SonarQube Community Build sobre un subset de 4 casos representativos
del corpus RQ2. No sustituye los resultados de RQ2 ya consolidados en
[output/rq2-batch/](../rq2-batch/).

## 2. Subset analizado

| caseId | Corpus | Estado prototipo | Razón de inclusión |
|---|---|---|---|
| `PILOT_VALID_NESTED_IN_LOOP`     | piloto | elegible             | Mayor delta del corpus piloto |
| `REAL_COMMONS_LANG_CONTAINS_NONE`| real   | elegible (parcial)   | Mayor CC y mayor delta absoluto del corpus real |
| `REAL_ANT_MATCH_PATH`            | real   | elegible (parcial)   | Triple anidamiento; rechazo P5 en nivel 3 |
| `REAL_COMMONS_LANG_CHOMP`        | real   | no elegible (P5)     | Caso no elegible: control de no divergencia |

## 3. Tabla comparativa

Valores del prototipo extraídos de
[output/rq2-batch/rq2-all-results.csv](../rq2-batch/rq2-all-results.csv).

| caseId | CC before (proxy) | CC after (proxy) | Δ (proxy) | CC before (Sonar) | CC after (Sonar) | Δ (Sonar) | ¿Coincide before? | ¿Coincide after? | Notas |
|---|---|---|---|---|---|---|---|---|---|
| `PILOT_VALID_NESTED_IN_LOOP`      | 6  | 4  | −2 | 6  | 4  | −2 | sí | sí | — |
| `REAL_COMMONS_LANG_CONTAINS_NONE` | 15 | 11 | −4 | 15 | 11 | −4 | sí | sí | — |
| `REAL_ANT_MATCH_PATH`             | 6  | 4  | −2 | 6  | 4  | −2 | sí | sí | Combinación P5 nivel 3 rechazada por ambos modelos |
| `REAL_COMMONS_LANG_CHOMP`         | 6  | 6  |  0 | 6  | 6  |  0 | sí | sí | No elegible: CC after = CC before por construcción |

## 4. Resumen agregado

| Métrica | Prototipo | SonarQube |
|---|---|---|
| Σ CC before                  | 33 | 33 |
| Σ CC after                   | 25 | 25 |
| Σ Δ                          | −8 | −8 |
| Casos con coincidencia exacta (before) | —  | 4 / 4 |
| Casos con coincidencia exacta (after)  | —  | 4 / 4 |
| Casos con divergencia ≥ 1 (before)     | —  | 0 / 4 |
| Casos con divergencia ≥ 1 (after)      | —  | 0 / 4 |

## 5. Análisis cualitativo

La validación complementaria muestra **convergencia total** entre el
proxy `CognitiveComplexityCalculator` del prototipo y la implementación
oficial de SonarQube Community Build sobre el subset analizado:

- **8/8 medidas individuales coinciden** (4 *before* + 4 *after*),
  divergencia máxima observada = 0 puntos.
- **Σ Δ idéntico** en ambos modelos (−8), tanto a nivel agregado como
  caso por caso.
- El caso de control no elegible (`REAL_COMMONS_LANG_CHOMP`) preserva
  CC = 6 en ambos modelos, confirmando que el rechazo de P5 no
  introduce ruido en la métrica.
- Los dos casos de elegibilidad parcial (`REAL_ANT_MATCH_PATH`,
  `REAL_COMMONS_LANG_CONTAINS_NONE`) muestran el mismo Δ que el caso
  totalmente elegible (`PILOT_VALID_NESTED_IN_LOOP`) en términos
  proporcionales al número de niveles combinados.

Esta convergencia respalda la decisión de usar el proxy en el cálculo
masivo de RQ2 sin perder validez de constructo respecto a la métrica
oficial, dentro del subconjunto de patrones sintácticos cubiertos por
el prototipo (anidamiento simple sin ternarios, lambdas no-as-nesting
ni recursión).

## 6. Amenazas a la validez (de esta validación complementaria)

- Tamaño del subset (n = 4) limita la generalización del contraste.
- La operación *after* requiere sustitución manual del contenido del
  archivo, lo que introduce una posible fuente de error humano (se
  mitiga restaurando los originales con `git checkout` inmediatamente
  tras el escaneo).
- El motor de SonarQube puede evolucionar entre versiones; debe
  registrarse la versión exacta utilizada (campo
  `sonar.qualityProfile.java.builtIn` y `sonar.version` accesibles vía
  `/api/system/info`).

## 7. Trazabilidad de ejecución

- Versión SonarQube Community Build utilizada: `[PENDIENTE DE
  REGISTRO]`
- Fecha de ejecución *before*: `[PENDIENTE]`
- Fecha de ejecución *after*: `[PENDIENTE]`
- Hash del commit del corpus en la ejecución: `[PENDIENTE]`
- Token usado: identificador `tfm-rq2-validation` (no transcribir el
  valor en este documento).

## 8. Conclusión

La validación complementaria confirma que, sobre el subset de 4 casos
representativos, los valores de complejidad cognitiva calculados por el
proxy del prototipo coinciden exactamente con los reportados por
SonarQube Community Build, tanto en los métodos originales como en sus
versiones refactorizadas. La igualdad de Σ Δ (−8) refuerza la validez
de constructo de la métrica utilizada en RQ2.

Esta convergencia es **complementaria** y de ningún modo sustituye los
resultados consolidados de RQ2; debe interpretarse como un control
adicional limitado al subset analizado y a la versión de SonarQube
registrada en §7.
