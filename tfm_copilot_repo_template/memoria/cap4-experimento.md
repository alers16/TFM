# 4.5 Diseño experimental

Esta sección documenta el corpus, las métricas, los oráculos y los
protocolos congelados que sostienen los experimentos asociados a RQ2 y
RQ3, así como la validación complementaria con SonarQube.

## 4.5.1 Corpus

El corpus se compone de dos subconjuntos cargados desde recursos del
*classpath* mediante
[`PilotCorpusLoader`](../src/main/java/es/tfm/refactoring/experiment/PilotCorpusLoader.java)
y
[`RealDatasetLoader`](../src/main/java/es/tfm/refactoring/experiment/RealDatasetLoader.java).

**Corpus piloto** (`src/main/resources/pilot-corpus/`). Conjunto de
ficheros sintéticos diseñados para cubrir los casos canónicos del patrón
objetivo y sus rechazos típicos. Incluye casos válidos, inválidos por
violación de cada precondición y un caso mixto con varias oportunidades.
Su finalidad es servir de prueba controlada del pipeline.

**Corpus real** (`src/main/resources/real-corpus/`). Conjunto de
fragmentos extraídos de proyectos Java de código abierto (Apache
Commons Lang, Apache Commons Collections, Apache Commons Math y Apache
Ant), preservando licencia y referencia al método y archivo originales
mediante metadatos en comentarios `@project`, `@file`, `@method`,
`@license` y `@sonarCCBefore`. [PENDIENTE DE REDACCIÓN METODOLÓGICA]
Especificar versiones exactas de cada proyecto y criterio formal de
selección de los fragmentos en el cuerpo de la memoria final, con
trazabilidad al *commit hash* de origen.

**Subset RQ3** (definido en
[`LlmEvaluationSubset`](../src/main/java/es/tfm/refactoring/llm/LlmEvaluationSubset.java)).
Seis casos seleccionados del corpus consolidado:

| Identificador | Origen | Categoría | Justificación |
|---|---|---|---|
| `PILOT_VALID_SIMPLE` | Piloto | Elegible | Caso base mínimo `if + if`. |
| `PILOT_VALID_NESTED_IN_LOOP` | Piloto | Elegible | Anidamiento dentro de `for`; mayor impacto en CC. |
| `REAL_COMMONS_MATH_CONVERGED` | Real | Elegible | Doble condición numérica en código real. |
| `REAL_ANT_MATCH_PATH` | Real | Elegible parcial | Triple `if`, primer par combinable, tercer nivel rechazado por P5. |
| `REAL_COMMONS_MATH_VALIDATE_RANGE` | Real | No elegible (P1) | `outer if` con `else`. |
| `REAL_COMMONS_COLLECTIONS_GET` | Real | No elegible (P5) | Llamada a método (`containsKey`) en condición. |

El subset cubre los ejes de variación relevantes para RQ3
(elegibles, parciales, no elegibles) y los principales motivos de
descarte (P1, P5).

## 4.5.2 Métricas y baseline

**Métrica primaria.** Complejidad cognitiva proxy (CC) calculada por
`CognitiveComplexityCalculator` antes y después de la transformación.
Se reporta el valor *before*, el valor *after* y la diferencia
`Δ = after − before`. Un `Δ` negativo indica reducción de complejidad.
Las limitaciones del proxy están declaradas en §4.4.3.

**Métrica complementaria.** Complejidad cognitiva medida por SonarQube
Community Build sobre un subconjunto representativo (§4.5.4).

**Baseline determinista.** Para cada caso, el resultado producido por
el pipeline determinista (`BatchRunner`) constituye el baseline contra
el que se contrastan las salidas de los LLMs en RQ3. Por construcción
del pipeline (§4.2.5), el baseline declara explícitamente si el caso
es elegible y, en caso afirmativo, el `Δ` aplicable.

## 4.5.3 Protocolo RQ2 — Batch determinista *before/after*

El experimento de RQ2 sigue un esquema *before/after* sobre el corpus
consolidado:

1. Cargar la totalidad del corpus piloto y del corpus real mediante
   los *loaders* correspondientes.
2. Para cada `ExperimentCase`, ejecutar `BatchRunner.processCase` con
   la política *one-at-a-time* descrita en §4.2.5.
3. Recoger métricas *before*, *after* y `Δ`, junto con motivos de
   descarte para los casos no elegibles.
4. Exportar resultados en JSON y CSV bajo `output/rq2-batch/` mediante
   `ResultExporter`.

El procedimiento operativo está documentado en
[docs/sonar-contrast-procedure.md](../docs/sonar-contrast-procedure.md).
La parte **congelada** del experimento es la composición del corpus, el
conjunto de precondiciones P1–P5 y la política *one-at-a-time*. La
implementación de la métrica proxy es estable; cualquier cambio en
`CognitiveComplexityCalculator` invalidaría las métricas reportadas y
requeriría una nueva ejecución completa.

## 4.5.4 Validación complementaria con SonarQube

La validación complementaria contrasta los valores del proxy con los
producidos por la implementación oficial de SonarQube Community Build
sobre un subset de cuatro casos representativos:
`PILOT_VALID_NESTED_IN_LOOP`, `REAL_COMMONS_LANG_CONTAINS_NONE`,
`REAL_ANT_MATCH_PATH` y `REAL_COMMONS_LANG_CHOMP`. El subset cubre el
caso de mayor `Δ` del corpus piloto, el de mayor `Δ` absoluto del
corpus real, un caso de triple anidamiento con elegibilidad parcial y
un caso no elegible por P5 (control de no divergencia).

El procedimiento operativo está documentado en
[docs/rq2-sonar-validation-procedure.md](../docs/rq2-sonar-validation-procedure.md)
y el arranque del servidor (vía `docker-compose`) en
[docs/sonarqube-setup.md](../docs/sonarqube-setup.md). Los artefactos
asociados se conservan en
[output/rq2-sonar-validation/](../output/rq2-sonar-validation/),
incluyendo `metadata.json`, `sonar-before.json`, `sonar-after.json`,
el corpus *after* (`after-corpus/`) y la tabla comparativa
`rq2-sonar-validation.md`.

[PENDIENTE DE VERIFICACIÓN] Estado de consolidación de la validación.
El archivo
[output/rq2-sonar-validation/metadata.json](../output/rq2-sonar-validation/metadata.json)
documenta que la versión exacta de SonarQube, los timestamps de
ejecución y el operador están sin registrar, y la nota interna del
fichero declara que en esta entrega **no se han ejecutado escaneos
reales de SonarQube**. La tabla comparativa
[rq2-sonar-validation.md](../output/rq2-sonar-validation/rq2-sonar-validation.md)
se encuentra en estado de plantilla, con valores prerellenos a partir
del proxy. Hasta que se ejecute la corrida real y se completen
`metadata.json` y los JSON de Sonar con valores independientes, los
resultados de §4.6.3 deben presentarse como provisionales. Esta
incertidumbre afecta exclusivamente a la validación complementaria; los
resultados de RQ2 reportados en §4.6.2 se basan en el proxy y son
independientes de esta validación.

## 4.5.5 Protocolo RQ3 — Evaluación de LLMs

El protocolo experimental para RQ3 está congelado en
[`LlmExperimentProtocol.defaultProtocol()`](../src/main/java/es/tfm/refactoring/llm/LlmExperimentProtocol.java)
y consta de los siguientes parámetros:

| Parámetro | Valor | Justificación |
|---|---|---|
| Modelos | `gpt-4o`, `gpt-4.1` | Comparativa intra-proveedor (OpenAI). [PENDIENTE DE REDACCIÓN METODOLÓGICA] sobre la decisión adoptada en fase 9.6 de restringir la comparación a un único proveedor. |
| Temperatura | `0.0` | Determinismo: misma entrada produce misma salida esperada. |
| Intentos por caso | `3` | Detección de inconsistencia residual aun a temperatura 0. |
| Prompt | versión `v1.0` | Trazabilidad. Texto literal en `LlmPromptBuilder`. |
| *Max tokens* salida | `2048` | Suficiente para métodos refactorizados completos. |
| Subset | 6 casos | Cobertura de elegibles/parciales/no elegibles. |
| Total invocaciones | 36 | 6 × 2 × 3. |

El procedimiento por caso es:

1. Cargar el caso del corpus y el baseline determinista correspondiente.
2. Construir el prompt `v1.0` mediante `LlmPromptBuilder`.
3. Invocar al modelo a través del `LlmResponseProvider` configurado.
4. Almacenar la respuesta cruda como evidencia
   (`rq3-full-evidence.json`).
5. Aplicar el oráculo `LlmResponseValidator` (§4.4.5).
6. Registrar `LlmEvaluationResult` con veredicto, métricas y comparación
   con baseline.
7. Repetir para cada intento (1..3) y cada modelo.

La **parte congelada** del experimento incluye: la lista de modelos,
la temperatura, el número de intentos, la versión del prompt, el
oráculo y la composición del subset. El proveedor concreto
(`LiveLlmResponseProvider` vs `PrerecordedResponseProvider`) es
intercambiable, pero el modo `live` es el oficial; el modo `dry-run` se
utiliza para pruebas reproducibles del pipeline sin coste de API. El
protocolo descansa documentalmente en
[docs/llm-experiment-protocol.md](../docs/llm-experiment-protocol.md).

## 4.5.6 Reproducibilidad

Las garantías de reproducibilidad se sostienen sobre los siguientes
elementos verificables en el repositorio:

- **Configuración congelada.** `LlmExperimentProtocol.defaultProtocol()`
  está fijada en código y se imprime en `rq3-run-metadata.json` de cada
  campaña.
- **Variables de entorno explícitas.** El modo `live` exige
  `OPENAI_API_KEY`; su ausencia no produce un fallo silencioso, sino
  un incidente registrado en `rq3-incidents.md` con el protocolo
  serializado intacto.
- **Versionado del prompt.** `LlmPromptBuilder.PROMPT_VERSION = "v1.0"`
  se propaga a cada `LlmEvaluationResult` y a cada artefacto exportado.
- **Build reproducible.** Versiones fijadas en
  [pom.xml](../pom.xml): Java 17, JavaParser 3.26.4, JUnit 5.11.4,
  Gson 2.11.0.
- **Artefactos persistentes.** Todas las salidas se escriben bajo
  `output/` y son auditables sin ejecutar el código.
