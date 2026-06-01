# Plan visual de la memoria

Catálogo de figuras y tablas previstas. Las fuentes editables (Mermaid, dot, etc.) se versionan en `figuras/src/`; los renderizados PNG/SVG en `figuras/img/`.

## 1. Figuras

| ID | Capítulo | Título | Propósito | Tipo recomendado |
|---|---|---|---|---|
| Fig. 2.1 | 2. Antecedentes | Modelo de complejidad cognitiva: incrementos por anidamiento | Didáctico: ilustrar la métrica antes de su uso | Imagen / Mermaid (esquema) |
| Fig. 2.2 | 2. Antecedentes | Ciclo Design Science Research aplicado al TFM | Marco metodológico | Mermaid (`flowchart`) |
| Fig. 4.0 | 4.1 Visión general | Mapa RQ ↔ secciones del capítulo 4 | Trazabilidad RQ | Mermaid (`flowchart LR`) o tabla |
| Fig. 4.1 | 4.3 Arquitectura | Diagrama de paquetes del prototipo | Visión estructural | Mermaid (`classDiagram` o `flowchart`) |
| Fig. 4.2 | 4.3 Arquitectura | Flujo de datos del pipeline determinista | AST → detección → transformación → CC | Mermaid (`flowchart TD`) |
| Fig. 4.3 | 4.4 / 4.5 | Pipeline experimental RQ3 | Caso → prompt → modelo × 3 → oráculo → veredicto | Mermaid (`flowchart LR`) |
| Fig. 4.4 | 4.2 Diseño | Esquema before/after canónico | Patrón objetivo | Mermaid o snippet en código |
| Fig. 4.5 | 4.5 / 4.6 | Caso real `REAL_ANT_MATCH_PATH` (triple anidamiento) | Combinación parcial + rechazo P5 | Imagen / snippets paralelos |
| Fig. 4.6 | 4.5 / 4.6 | Caso negativo `REAL_COMMONS_COLLECTIONS_GET` | Rechazo P5 (method call en condición) | Imagen / snippet |
| Fig. 4.7 | 4.2 Diseño | Árbol de decisión P1–P5 | Lógica del detector | Mermaid (`flowchart TD`) |
| Fig. 4.8 | 4.4.5 | Oráculo `LlmResponseValidator` (8 pasos) | Lógica del oráculo RQ3 | Mermaid (`flowchart TD`) |
| Fig. 4.9 | 4.6.2 | Δ CC por caso (RQ2) | Resultados cuantitativos RQ2 | Gráfico de barras (imagen) |
| Fig. 4.10b | 4.6.3 | Δ proxy vs Δ Sonar (subset) | Validación cruzada | Gráfico de barras agrupadas |
| Fig. 4.11 | 4.6.4 | RQ3: `gpt-4o` vs `gpt-4.1` | Comparativa SUCCESS/INCORRECT por caso | Gráfico de barras apiladas |
| Fig. 4.12 | Apéndice G | Trazabilidad RQ → artefactos → tests | Mapa de trazabilidad global | Mermaid (`flowchart LR`) |

## 2. Tablas clave

| ID | Capítulo | Título | Fuente |
|---|---|---|---|
| Tabla 4.1 | 4.2 Diseño | Catálogo de `DiscardReason` con ejemplos | [src/main/java/es/tfm/refactoring/detection/DiscardReason.java](../src/main/java/es/tfm/refactoring/detection/DiscardReason.java) |
| Tabla 4.2 | 4.5.1 Corpus | Composición del corpus piloto + real | `src/main/resources/pilot-corpus/`, `src/main/resources/real-corpus/` |
| Tabla 4.3 | 4.6.2 Resultados RQ2 | CC before/after y Δ por caso | [output/rq2-batch/rq2-summary.md](../output/rq2-batch/rq2-summary.md) |
| Tabla 4.4 | 4.6.3 SonarQube | Validación cruzada (4 casos) | [output/rq2-sonar-validation/rq2-sonar-validation.md](../output/rq2-sonar-validation/rq2-sonar-validation.md) |
| Tabla 4.5 | 4.6.4 Resultados RQ3 | Tasas SUCCESS / baseline match / consistencia por modelo | [output/rq3-campaign-real-phase9/rq3-aggregated.md](../output/rq3-campaign-real-phase9/rq3-aggregated.md) |
| Tabla 4.6 | 4.6.4 Resultados RQ3 | Resultado por caso × modelo | [output/rq3-campaign-real-phase9/rq3-summary.csv](../output/rq3-campaign-real-phase9/rq3-summary.csv) |

## 3. Convenciones

- Numeración: `Fig. N.M` por capítulo; tablas con `Tabla N.M`.
- Pie de figura siempre con fuente o referencia al archivo de evidencia.
- Renderizar Mermaid a SVG para la versión final; mantener el `.mmd` fuente en `figuras/src/`.

## 3a. Diagramas ya integrados como TikZ nativo (sin imagen externa)

- **Fig. 4.7 — Árbol de decisión P1–P5** (`fig:arbol-p1-p5`): integrada en
  `secciones/04a-diseno.tex` (§4.2.3) como `tikzpicture`. Sustituye al
  borrador Mermaid `figuras/src/fig-4.7-arbol-decision-p1-p5.mmd`, que se
  conserva solo como referencia. Compila sin `mmdc` ni PNG.
- **Fig. 4.4 — Esquema before/after canónico** (`fig:before-after`):
  integrada en `secciones/04a-diseno.tex` (§4.2.2) como dos snippets
  `lstlisting` lado a lado (Antes $\Longrightarrow$ Después). Nativo, sin
  imagen externa.
- **Fig. 4.1 — Diagrama de paquetes** (`fig:paquetes`): integrada en
  `secciones/04b-arquitectura.tex` (§4.3) como `tikzpicture` por capas
  (entrada → orquestación → núcleo + validation). Sustituye al borrador
  `figuras/src/fig-4.1-paquetes.mmd`.
- **Fig. — Secuencia de evaluación RQ3** (`fig:secuencia-rq3`): diagrama de
  secuencia UML en `secciones/04c-implementacion.tex` (§4.4.5). Fuente en
  `figuras/src/fig-secuencia-rq3.mmd`; **pendiente de renderizar** a
  `figuras/img/fig-secuencia-rq3.pdf` (mmdc o mermaid.live). El `figure` ya
  está colocado con `\IfFileExists` (muestra placeholder hasta que exista el PDF).
- **Tabla — Composición del corpus** (`tab:corpus`): en
  `secciones/04d-experimento.tex` (§4.5.1), piloto 8 + real 118 = 126
  (104 elegibles). Datos de `output/rq2-batch/rq2-run-metadata.json`.
- **Listado 4.x — Cálculo de CC** (`lst:cc-ejemplo`): en
  `secciones/04c-implementacion.tex` (§4.4.3), ejemplo anotado de cálculo de
  complejidad cognitiva sobre un caso validado contra SonarSource (CC=9).
- **Fig. 4.8 — Oráculo de RQ3** (`fig:oraculo`): integrada en
  `secciones/04c-implementacion.tex` (§4.4.5) como diagrama de flujo TikZ de
  la cadena de validación (rechazo / salida inválida / asignación de
  veredicto), acompañada de la **tabla de decisión** `tab:oraculo` con las
  reglas exactas de veredicto (fiel a `LlmResponseValidator.evaluate`).
  Sustituye al borrador `figuras/src/fig-4.8-oraculo-rq3.mmd`.
- **Fig. 4.2 — Flujo del pipeline determinista** (`fig:pipeline-det`):
  integrada en `secciones/04b-arquitectura.tex` (§4.3) como **diagrama de
  flujo** TikZ (rombos de decisión) que hace explícitas la rama de
  inelegibilidad y el bucle \emph{one-at-a-time}. Sustituye al borrador
  `figuras/src/fig-4.2-pipeline-determinista.mmd`.

## 3b. Figuras y tablas ya integradas (datos reales)

Generadas a partir de `output/rq2-batch/rq2-summary.md` y embebidas en
`secciones/04e-resultados.tex` (no requieren imagen externa):

- **Fig. boxplots** (`fig:boxplots`, pgfplots): CC antes vs. después (escala
  log, bigotes de Tukey + outliers) y distribución de Δ.
- **Tabla cinco números** (`tab:rq2-cinco`): mín/Q1/mediana/Q3/máx/media de
  CC antes, después y Δ (n=104 elegibles).
- **Tabla issues SonarQube** (`tab:issues-sonar`): cruce del umbral CC=15
  (41 con issue → 6 eliminados, 35 persisten; 63 sin issue).
- **Tabla validación humana** (`tab:validacion-humana`): plantilla N=30,
  pendiente de rellenar (ver `docs/human-validation-protocol.md`).

## 4. Estado

- **Fuentes Mermaid creadas** en `figuras/src/` (pendientes de renderizar a SVG en `figuras/img/`):
  - `fig-4.1-paquetes.mmd` — diagrama de paquetes del prototipo.
  - `fig-4.2-pipeline-determinista.mmd` — flujo RQ1/RQ2.
  - `fig-4.3-pipeline-rq3.mmd` — pipeline experimental RQ3.
  - `fig-4.7-arbol-decision-p1-p5.mmd` — árbol de decisión del detector.
  - `fig-4.8-oraculo-rq3.mmd` — oráculo `LlmResponseValidator` (8 pasos).
- Renderizado sugerido: `mmdc -i figuras/src/<fichero>.mmd -o figuras/img/<fig>.svg`.
- **Pendientes de fuente:** Fig. 2.1, 2.2, 4.0, 4.4, 4.5, 4.6, 4.9, 4.10b, 4.11, 4.12.
- ⚠️ **Aviso de datos (Fig. 4.9 / Tabla 4.3):** las cifras de RQ2 del borrador
  Markdown previo (`n=18`, `Δ=−18`) están **obsoletas**. Los artefactos vigentes
  (`output/rq2-batch/rq2-run-metadata.json`) reportan `126` casos y `Δ=−278`.
  Re-extraer antes de generar cualquier gráfico de resultados.
