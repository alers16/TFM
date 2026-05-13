# 4.4 Implementación del prototipo

Esta sección detalla la implementación de los componentes descritos en
la §4.3. La descripción se ciñe a lo presente en el repositorio; las
limitaciones conocidas se declaran explícitamente.

## 4.4.1 Detector

La clase
[`NestedIfDetector`](../src/main/java/es/tfm/refactoring/detection/NestedIfDetector.java)
recorre el cuerpo de un `MethodDeclaration` mediante un
`VoidVisitorAdapter` que visita todos los `IfStmt` del AST, incluidos
los anidados a profundidad arbitraria. Para cada `IfStmt` candidato
actúa como sigue:

- Comprueba P1 (`hasElseBranch`).
- Llama a `extractSingleInnerIf` sobre el bloque `then`. Acepta tanto
  el caso con llaves (`BlockStmt` de tamaño 1) como el caso sin llaves
  (sentencia directa que es un `IfStmt`). En cualquier otro caso
  devuelve `null`, lo que produce un descarte por P2 o P3.
- Comprueba P4 sobre el `IfStmt` interno extraído.
- Comprueba P5 mediante `containsSideEffects`, que examina las cinco
  categorías declaradas en §4.2.3 con `findAll(...)` sobre subnodos del
  AST.

El método paralelo `detectWithReasons(method)` devuelve un
`DetectionResult` por candidato (aceptado o rechazado). Los motivos de
rechazo se construyen en `evaluateCandidateWithReasons` distinguiendo
entre `OUTER_HAS_ELSE`, `OUTER_BLOCK_MULTIPLE_STATEMENTS`,
`SINGLE_STATEMENT_NOT_IF`, `INNER_HAS_ELSE`,
`NO_NESTED_IF_PATTERN` y, para P5, una o varias de
`METHOD_CALL_IN_CONDITION`, `ASSIGNMENT_IN_CONDITION`,
`INCREMENT_OR_DECREMENT_IN_CONDITION`, `LAMBDA_IN_CONDITION` y
`OBJECT_CREATION_IN_CONDITION`.

Dado que el modo `detectWithReasons` recopila motivos por candidato y
el pipeline los agrega a nivel de método, un mismo método puede
contribuir varias categorías a la columna `discardCategories` del CSV
final. Esto es deliberado y proporciona evidencia agregada para RQ1.

## 4.4.2 Transformador

La clase
[`NestedIfTransformer`](../src/main/java/es/tfm/refactoring/transformation/NestedIfTransformer.java)
implementa el algoritmo descrito en §4.2.4. Detalles relevantes de la
implementación:

- **Clonación.** Se invoca `Expression.clone()` sobre `outerCondition` y
  `innerCondition` antes de combinarlas. Sin esto, la asignación de la
  nueva condición al `outerIf` movería los nodos originales y dejaría
  partes del AST inconsistentes.
- **Parentizado selectivo.** El método auxiliar `parenthesizeIfNeeded`
  envuelve la expresión en `EnclosedExpr` solo si es un `BinaryExpr`
  con operador `OR`. Otros operadores tienen precedencia mayor o igual
  que `&&`, por lo que no requieren paréntesis.
- **Normalización del cuerpo.** Si el cuerpo del `if` interno no es un
  `BlockStmt`, se envuelve en uno antes de asignarse al `if` resultante.
  Esto produce siempre código con llaves, alineado con guías de estilo
  habituales en Java.
- **Eliminación defensiva del `else`.** Tras asignar la nueva condición
  y el nuevo cuerpo se llama a `outerIf.removeElseStmt()`. La operación
  es no-op cuando ya no hay rama `else`, pero protege ante usos
  incorrectos del transformador.
- **Verificación previa.** El método `apply` retorna `false` y aborta si
  alguno de los dos `if` tiene rama `else`, replicando como salvaguarda
  las precondiciones P1 y P4 que ya cubrió el detector.

## 4.4.3 Cálculo proxy de complejidad cognitiva

La clase
[`CognitiveComplexityCalculator`](../src/main/java/es/tfm/refactoring/analysis/CognitiveComplexityCalculator.java)
calcula una **estimación provisional** de la complejidad cognitiva
basada en el modelo publicado por SonarSource [PENDIENTE DE CITA].
Cubre las siguientes reglas:

- Incremento estructural (+1) en `if`, `else if`, `else`, `for`,
  `for-each`, `while`, `do-while`, `switch`, `catch` y `break`/`continue`
  con etiqueta.
- Incremento de anidamiento (+nivel) en `if`, `for`, `for-each`,
  `while`, `do-while`, `switch` y `catch`. No aplica a `else if` ni a
  `else`, alineado con el modelo de referencia.
- Operadores lógicos: +1 por cada secuencia de operadores del mismo
  tipo (`&&` o `||`) en condiciones de control de flujo.

**Limitaciones conocidas y declaradas en el código.** El cálculo es una
aproximación parcial y no reemplaza la medición oficial de SonarQube o
SonarLint. Concretamente, no cubre:

- el operador ternario `?:` en expresiones,
- los operadores lógicos fuera de condiciones de control de flujo,
- la detección de recursión,
- las expresiones lambda como incremento de anidamiento,
- las clases anónimas o internas como incremento de anidamiento.

Además, el conteo basado en AST de operadores lógicos alternantes
puede subcontar secuencias del tipo `||` `&&` `||`, contabilizándolas
como dos grupos en lugar de tres en algunos arreglos. Por estas
razones, la métrica se denomina **proxy** a lo largo de la memoria y
cualquier resultado se contrasta con SonarQube cuando el contraste es
aplicable (§4.5.4).

## 4.4.4 Pipeline experimental

La clase
[`BatchRunner`](../src/main/java/es/tfm/refactoring/experiment/BatchRunner.java)
implementa la política *one-at-a-time* sobre cada `ExperimentCase`.
Características relevantes:

- Trabaja sobre **un clon** del `MethodDeclaration` original para no
  modificar el AST de partida; las dos mediciones se hacen sobre el
  método original (clon "before") y sobre el clon transformado.
- Calcula `discardCategories` mediante `detectWithReasons`,
  independientemente de cuántas oportunidades acabe aplicando el
  pipeline. Esto permite responder a RQ1 con evidencia agregada.
- Emite siempre `opportunitiesDetected == opportunitiesApplied` por la
  política *one-at-a-time*, junto con `totalPasses` (≤ 10) y `Δ` exacto.
- Cuando un caso falla a parsear o no contiene métodos, se emite un
  `ExperimentResult` inelegible con `complexityBefore = -1` y motivo
  explícito en `discardReason`/`observations`.

Los corpus se cargan mediante `PilotCorpusLoader` y `RealDatasetLoader`,
que leen recursos del *classpath* desde `pilot-corpus/` y
`real-corpus/`. Cada archivo de corpus declara metadatos en comentarios
`@project`, `@file`, `@method`, `@license` y, en el corpus real,
`@sonarCCBefore`. La ejecución coordinada de RQ2 se realiza desde
`Rq2BatchExecutor`.

La exportación corre a cargo de
[`ResultExporter`](../src/main/java/es/tfm/refactoring/experiment/ResultExporter.java),
que produce:

- **JSON** completo con código fuente antes y después por caso
  (formato principal para reproducibilidad y trazabilidad),
- **CSV** sin código fuente (cabecera con campos
  `caseId, origin, description, eligible, discardReason,
  discardCategories, opportunitiesDetected, opportunitiesApplied,
  totalPasses, complexityBefore, complexityAfter, delta,
  methodSignature, observations`).

## 4.4.5 Subsistema de evaluación de LLMs (RQ3)

El paquete
[`es.tfm.refactoring.llm`](../src/main/java/es/tfm/refactoring/llm/)
implementa el protocolo experimental para RQ3.

**Prompt.** [`LlmPromptBuilder`](../src/main/java/es/tfm/refactoring/llm/LlmPromptBuilder.java)
contiene literalmente el prompt versión `v1.0`: un *system prompt* con
las reglas de combinación (P1–P4 reescritas como restricciones para el
modelo), exigencia de preservación de orden de evaluación y de
semántica, prohibición de modificar firmas o código fuera de los `if`,
y opción de rechazo explícito mediante el marcador
`NO_REFACTORING_APPLICABLE`. El *user prompt* envuelve el código del
caso en un bloque ` ```java `.

**Protocolo.** [`LlmExperimentProtocol`](../src/main/java/es/tfm/refactoring/llm/LlmExperimentProtocol.java)
encapsula los parámetros como configuración inmutable. El método
estático `defaultProtocol()` declara la configuración formal:

- modelos: `gpt-4o`, `gpt-4.1`,
- temperatura: `0.0`,
- intentos por caso: `3`,
- versión de prompt: `v1.0`,
- *max tokens* de salida: `2048`.

Un protocolo auxiliar `openAiOnlyExploratoryProtocol()` mantiene los
mismos parámetros pero restringe la lista a `gpt-4o`. Su comentario
Javadoc establece explícitamente que **no sustituye** al protocolo
formal de dos modelos.

**Provider.** La interfaz `LlmResponseProvider` admite dos
implementaciones intercambiables:
[`LiveLlmResponseProvider`](../src/main/java/es/tfm/refactoring/llm/LiveLlmResponseProvider.java)
(invocación real a la API OpenAI con credencial leída desde
`OPENAI_API_KEY`) y
[`PrerecordedResponseProvider`](../src/main/java/es/tfm/refactoring/llm/PrerecordedResponseProvider.java)
(respuestas grabadas para *dry runs* y tests).

**Oráculo.** [`LlmResponseValidator`](../src/main/java/es/tfm/refactoring/llm/LlmResponseValidator.java)
implementa una cadena de comprobaciones:

1. Detección de rechazo explícito (`NO_REFACTORING_APPLICABLE`).
2. Extracción del bloque ` ```java ` con expresión regular.
3. Comprobación de parseabilidad con JavaParser.
4. Existencia de al menos un método.
5. Preservación de la firma del método (registrada como error de
   validación si difiere).
6. Medición de la complejidad cognitiva *after* sobre el método
   resultante.
7. Cálculo del delta y comparación con el delta del baseline
   determinista.
8. Asignación de veredicto.

El conjunto de veredictos posibles está definido en
[`LlmVerdict`](../src/main/java/es/tfm/refactoring/llm/LlmVerdict.java)
con las categorías `SUCCESS`, `INCORRECT`, `INVALID_OUTPUT`, `REFUSED`,
`PARTIAL` y `ERROR`.

El oráculo incluye la **guarda de elegibilidad v1.1**: si el baseline
declara el caso como no elegible (alguna precondición P1–P5 no se
cumple) y el LLM aplica una transformación que altera la complejidad
cognitiva (`delta != 0`), el veredicto es `INCORRECT`, aun cuando la
complejidad disminuya. Esta política evita clasificar como acierto
transformaciones que violan precondiciones del baseline.

**Campaña.** [`LlmCampaignRunner`](../src/main/java/es/tfm/refactoring/llm/LlmCampaignRunner.java)
orquesta la ejecución sobre el subset definido en
[`LlmEvaluationSubset`](../src/main/java/es/tfm/refactoring/llm/LlmEvaluationSubset.java),
genera un `LlmInvocationRecord` por cada llamada con metadatos de
ejecución (timestamp, versión de modelo, prompt enviado, tiempo de
respuesta) y delega en `LlmResponseValidator` para producir el
`LlmEvaluationResult` correspondiente. Los `Throwable` capturados en
el provider se propagan como veredicto `ERROR` con detalle técnico.

**Exportación.** [`LlmCampaignExporter`](../src/main/java/es/tfm/refactoring/llm/LlmCampaignExporter.java)
emite cinco artefactos por campaña:

- `rq3-full-evidence.json` — evidencia completa por invocación,
- `rq3-summary.csv` — resumen tabular,
- `rq3-aggregated.md` — agregación legible por humanos,
- `rq3-run-metadata.json` — metadatos de ejecución y protocolo
  congelado,
- `rq3-incidents.md` — registro de incidentes (p. ej. credenciales
  faltantes, errores de API).

El punto de entrada
[`CampaignExecutor`](../src/main/java/es/tfm/refactoring/llm/CampaignExecutor.java)
ofrece dos modos de ejecución: `--mode=live` (defecto, provider real,
validación de credenciales obligatoria) y `--mode=dry-run` (provider
grabado, sin red).
