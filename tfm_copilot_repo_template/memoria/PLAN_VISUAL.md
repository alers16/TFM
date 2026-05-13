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

## 4. Estado

- [PENDIENTE DE REDACCIÓN] Aún no se ha generado ninguna figura.
- Próximo paso: crear los esqueletos `.mmd` para Fig. 4.1, Fig. 4.2, Fig. 4.3, Fig. 4.7 y Fig. 4.8 cuando se inicie el capítulo 4.
