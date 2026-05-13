# 4.3 Arquitectura del prototipo

## 4.3.1 Visión general

El prototipo se organiza en seis paquetes Java. La separación responde a las
responsabilidades clásicas de una herramienta de análisis y
refactorización (detección, transformación, medición), ampliadas con un
pipeline experimental y un subsistema específico para la evaluación de
LLMs requerida por RQ3. Todas las operaciones sobre código se realizan
sobre el AST proporcionado por la biblioteca **JavaParser**, declarada
como dependencia en [`pom.xml`](../pom.xml).

## 4.3.2 Paquetes y responsabilidades

| Paquete | Responsabilidad | Clases principales |
|---|---|---|
| `es.tfm.refactoring` | Punto de entrada de línea de órdenes. | `Main` |
| `es.tfm.refactoring.detection` | Análisis del AST y evaluación de precondiciones P1–P5. | `NestedIfDetector`, `RefactoringOpportunity`, `DetectionResult`, `DiscardReason` |
| `es.tfm.refactoring.transformation` | Aplicación de la transformación sobre el AST. | `NestedIfTransformer` |
| `es.tfm.refactoring.analysis` | Cálculo proxy de complejidad cognitiva e impacto de la refactorización. | `CognitiveComplexityCalculator`, `RefactoringImpact`, `RefactoringImpactAnalyzer` |
| `es.tfm.refactoring.experiment` | Carga de corpus, ejecución por lotes, exportación de resultados y contraste con SonarQube. | `BatchRunner`, `Rq2BatchExecutor`, `ExperimentCase`, `ExperimentResult`, `ResultExporter`, `SeedCorpus`, `PilotCorpusLoader`, `RealDatasetLoader`, `SonarContrastEntry`, `SonarContrastReport` |
| `es.tfm.refactoring.llm` | Protocolo experimental, prompt, oráculo y campaña de evaluación de modelos para RQ3. | `LlmExperimentProtocol`, `LlmPromptBuilder`, `LlmEvaluationSubset`, `LlmResponseValidator`, `LlmCampaignRunner`, `LlmCampaignReport`, `LlmCampaignExporter`, `LlmResponseProvider`, `LiveLlmResponseProvider`, `PrerecordedResponseProvider`, `LlmInvocationRecord`, `LlmEvaluationResult`, `LlmVerdict`, `LlmApiConfig`, `CampaignExecutor` |

La lista de clases coincide con la estructura presente en
[src/main/java/es/tfm/refactoring/](../src/main/java/es/tfm/refactoring/).

## 4.3.3 Flujo de datos del pipeline determinista (RQ1, RQ2)

El flujo extremo a extremo para los experimentos RQ1 y RQ2 sigue las
siguientes etapas:

1. **Carga de corpus.** `PilotCorpusLoader` y `RealDatasetLoader` leen
   recursos del *classpath* (`/pilot-corpus/` y `/real-corpus/`) y
   construyen instancias de `ExperimentCase` con el código fuente y
   metadatos asociados (origen, descripción, licencia, identificador del
   método, valor `sonarCCBefore` cuando está disponible).
2. **Parseo a AST.** `BatchRunner` invoca `StaticJavaParser.parse(...)`
   y selecciona el primer `MethodDeclaration` del archivo. Si el parseo
   falla, el caso se marca como inelegible con motivo explícito.
3. **Detección con motivos.** Se invoca `detectWithReasons(method)` para
   recopilar todos los candidatos —aceptados y rechazados— junto con sus
   `DiscardReason`. Los motivos se agregan en el conjunto
   `discardCategories` del resultado.
4. **Medición *before*.** `CognitiveComplexityCalculator` calcula la
   complejidad cognitiva proxy del método original.
5. **Aplicación iterativa.** Se clona el método y se aplica la política
   *one-at-a-time* descrita en §4.2.5: detectar, aplicar la primera
   oportunidad, re-detectar, hasta punto fijo o `MAX_PASSES`.
6. **Medición *after*.** Se calcula nuevamente la complejidad cognitiva
   sobre el AST transformado y se obtiene `Δ = after − before`.
7. **Construcción del resultado.** Se ensambla un `ExperimentResult`
   inmutable con métricas, código fuente antes/después, número de
   pasadas y motivos de descarte.
8. **Exportación.** `ResultExporter` produce JSON (con código fuente
   completo) y CSV (sin código, apto para análisis tabular) en la
   ruta de salida correspondiente bajo `output/rq2-batch/`.

Este flujo está implementado por
[`BatchRunner.processCase`](../src/main/java/es/tfm/refactoring/experiment/BatchRunner.java).

## 4.3.4 Flujo del subsistema LLM (RQ3)

Para RQ3 se añade un flujo paralelo coordinado por
`CampaignExecutor` y `LlmCampaignRunner`:

1. **Carga del subset RQ3** (`LlmEvaluationSubset`) y de sus baselines
   deterministas, calculados por `BatchRunner` sobre los mismos casos.
2. **Construcción del prompt.** `LlmPromptBuilder` produce un par
   *system + user* con el código del caso, en una versión congelada
   (`PROMPT_VERSION = "v1.0"`).
3. **Invocación al modelo.** El componente concreto se inyecta a través
   de la interfaz `LlmResponseProvider`. Existen dos implementaciones:
   `LiveLlmResponseProvider` (API real de OpenAI) y
   `PrerecordedResponseProvider` (respuestas grabadas para *dry runs*).
4. **Validación con oráculo.** `LlmResponseValidator` aplica el oráculo
   descrito en §4.4.5 y emite un `LlmEvaluationResult` con veredicto
   (`LlmVerdict`).
5. **Agregación.** `LlmCampaignReport` calcula tasas por modelo y por
   caso; `LlmCampaignExporter` emite los artefactos `rq3-summary.csv`,
   `rq3-full-evidence.json`, `rq3-aggregated.md`,
   `rq3-run-metadata.json` e `rq3-incidents.md` en la ruta de salida
   indicada por línea de órdenes.

## 4.3.5 Decisiones estructurales

- **Inmutabilidad de los resultados.** Tanto `ExperimentResult` como
  `LlmEvaluationResult` se construyen una sola vez y se exportan tal
  cual; no hay mutación posterior. Esto facilita la trazabilidad.
- **Provider intercambiable para LLM.** La interfaz
  `LlmResponseProvider` permite ejecutar la misma campaña con
  respuestas reales o grabadas, sin tocar el código del runner ni del
  oráculo. Esto desacopla la lógica experimental del coste y la
  variabilidad de las llamadas remotas.
- **Exportadores múltiples.** El prototipo emite simultáneamente CSV,
  JSON y Markdown para cada campaña; cada formato cubre un uso
  diferente (tabla agregada, evidencia completa, lectura humana).
- **Sin acoplamiento a SonarQube en el pipeline determinista.** El
  prototipo **no invoca** SonarQube como parte del flujo principal. La
  validación cruzada con SonarQube se realiza fuera de banda según el
  procedimiento de §4.5.4.
