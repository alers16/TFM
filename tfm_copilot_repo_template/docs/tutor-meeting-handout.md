# Handout — Reunión con tutor
**TFM:** Refactorización de condicionales anidados para reducir complejidad cognitiva en Java  
**Fecha:** mayo 2026

---

## 1. Preguntas de investigación (literales)

| # | Pregunta |
|---|---|
| **RQ1** | ¿En qué casos se pueden combinar sentencias condicionales anidadas y cuál sería el resultado? |
| **RQ2** | ¿Qué impacto tienen estas refactorizaciones en la complejidad cognitiva? |
| **RQ3** | ¿Pueden los grandes modelos de lenguaje realizar esta refactorización de forma correcta y automática? |

---

## 2. Transformación objetivo

```
ANTES                       DESPUÉS
─────────────────────────   ──────────────────────
if (A) {                    if (A && B) {
    if (B) {                    S
        S                   }
    }
}
```

**¿Por qué es equivalente?**  
El operador `&&` en Java aplica cortocircuito: B solo se evalúa si A es verdadero, igual que en el anidado. La equivalencia se preserva si y solo si se cumplen P1–P5.

---

## 3. Precondiciones P1–P5 (condiciones suficientes del MVP)

| Precondición | Descripción | Clase que la verifica |
|---|---|---|
| **P1** | El `if` externo no tiene `else` ni `else-if` | `NestedIfDetector` |
| **P2** | El bloque `then` externo tiene exactamente una sentencia | `NestedIfDetector` |
| **P3** | Esa sentencia única es otro `if` | `NestedIfDetector` |
| **P4** | El `if` interno no tiene `else` ni `else-if` | `NestedIfDetector` |
| **P5** | Ninguna condición contiene side effects detectables | `NestedIfDetector` |

**Side effects cubiertos por P5:** llamadas a métodos, asignaciones, `++`/`--`, lambdas, `new`.

> **Origen de P1–P5:** son la operacionalización de las restricciones MVP declaradas en `AGENTS.md`
> (política conservadora del TFM). No provienen de un paper externo.
> Son *condiciones suficientes*, no necesarias — se pueden relajar incrementalmente
> (ver §7, trabajo futuro).

**Motivos de descarte codificados en `DiscardReason.java`** — permiten trazabilidad experimental completa para RQ1.

---

## 4. Arquitectura del prototipo

```
Corpus (Java fuente)
        │
        ▼
NestedIfDetector          ← detecta candidatos, aplica P1–P5, reporta DiscardReason
        │
        ▼
NestedIfTransformer       ← aplica transformación sobre AST (JavaParser 3.26.4)
        │
        ▼
CognitiveComplexityCalculator  ← calcula CC antes y después (proxy de SonarQube)
        │
        ▼
ResultExporter            ← genera CSV / JSON / Markdown
```

**Stack:** Java 17, Maven, JavaParser 3.26.4, JUnit 5.11.4, Gson 2.11.0.  
**Tests:** 415/415 en verde.

---

## 5. Corpus experimental (3 corpus)

| Corpus | N casos | Origen | Propósito |
|---|---|---|---|
| `pilot-corpus` | 8 | Sintético | Smoke-test del detector |
| `real-corpus` | 33 | Apache Commons Lang/IO/Math/Compress, Ant, Bytecode Viewer (código real open-source) | Evaluación RQ2 sobre código realista |
| `tutor-corpus` | 8 | Saborido et al., IEEE Access 2022 — BCV (GPL-3.0), jMetal (MIT) y FileDrop | Replicación parcial del paper del tutor |

> El `real-corpus` se amplió de 10 a 33 casos mediante un **escáner masivo** que analizó 24.987 candidatos
> en 8 proyectos open-source. Los 13 nuevos casos proceden de Apache Ant 1.10.14 y Bytecode Viewer 2.11.2.

> El `tutor-corpus` usa métodos diana del paper, seleccionados como **outliers de complejidad extrema**,
> no como muestra representativa. Esto es relevante para interpretar sus tasas de elegibilidad (ver §6.3).

---

## 6. Resultados RQ2 — Impacto en complejidad cognitiva

### 6.1 Modo STRICT (P1–P5 completas)

| Corpus | N | Elegibles | % Elegible | ΔCC total | ΔCC medio/caso |
|---|---|---|---|---|---|
| pilot | 8 | 5 | 63% | −7 | −1.4 |
| real | 33 | 23 | 70% | −63 | −2.7 |
| tutor | 8 | 1 | 13% | −3 | −3.0 |
| **Total** | **49** | **29** | **59%** | **−73** | **−2.5** |

### 6.2 Modo RELAXED (P1–P4 + P5' con allowlist de pureza)

P5' acepta llamadas a métodos presumiblemente puros: `isEmpty`, `size`, `contains`, `length`, `equals`,
`startsWith`, `endsWith`, `isPresent`, `compareTo`, getters `getX`/`isX`/`hasX`.

| Corpus | N | Elegibles | % Elegible | ΔCC total | Nuevos vs STRICT |
|---|---|---|---|---|---|
| pilot | 8 | 6 | 75% | −9 | **+1** |
| real | 33 | 27 | 82% | −75 | **+4** |
| tutor | 8 | 1 | 13% | −3 | **0** |
| **Total** | **49** | **34** | **69%** | **−87** | **+5** |

### 6.3 Interpretación

- **RELAXED amplía eligibilidad en real-corpus** (23 → 27, +4 casos) con un ΔCC total de −75 vs −63 en STRICT.
- **RELAXED no cambia el tutor-corpus (1/8):** los descartes allí son por P1/P4 (ramas `else`), no por P5.
  - **Hallazgo RQ1:** en código industrial de complejidad extrema la barrera dominante es la presencia de
    ramas `else`, no los side effects en condiciones. Relajar P5 no es suficiente para ese tipo de código.
- Los 10 casos `real-corpus` que ningún modo acepta fallan por P1, P2 o P5 (distintas barreras).
- El escáner masivo (24.987 candidatos en 8 proyectos OSS) confirma que las precondiciones P1–P5 en modo
  STRICT son restrictivas: tasa de elegibilidad natural ≈ 0.06% (15 elegibles / 24.987 candidatos).

> **Nota metodológica:** los valores de CC son estimaciones del prototipo (`CognitiveComplexityCalculator`),
> no equivalentes directos a SonarQube. La validación con SonarQube sobre un subconjunto de 4 casos está
> planificada (ver `docs/rq2-sonar-validation-procedure.md`).

---

## 7. Resultados RQ3 — LLMs como refactorizadores

### 7.1 Fase 9 — Campaña principal (prompt v1.0 zero-shot)

**36 invocaciones:** 6 casos × 2 modelos × 3 intentos.

| Modelo | Éxito | Incorrecto | Tasa de éxito | Consistencia |
|---|---|---|---|---|
| gpt-4o | 18/18 | 0 | **100%** | 100% |
| gpt-4.1 | 15/18 | 3 | **83.3%** | 100% |

**Caso donde gpt-4.1 falla consistentemente:** `REAL_COMMONS_COLLECTIONS_GET` (50% global).

---

### 7.2 Campaña trampa — Tasa de falsos positivos (prompt v1.0)

**18 invocaciones:** 3 casos trampa × 2 modelos × 3 intentos.  
Cada caso trampa viola exactamente una precondición de forma no obvia:

| Caso trampa | Precondición violada | Modelo | Rechaza correctamente |
|---|---|---|---|
| `TRAP_P4_INNER_ELSE` | P4 — if interno con `else` | gpt-4o | 3/3 ✓ |
| `TRAP_P4_INNER_ELSE` | P4 — if interno con `else` | gpt-4.1 | 3/3 ✓ |
| `TRAP_P2_MULTI_STATEMENT` | P2 — then externo con 2 sentencias | gpt-4o | 3/3 ✓ |
| `TRAP_P2_MULTI_STATEMENT` | P2 — then externo con 2 sentencias | gpt-4.1 | 3/3 ✓ |
| `TRAP_P5_ASSIGNMENT` | P5 — asignación en condición interna | gpt-4o | 3/3 ✓ |
| `TRAP_P5_ASSIGNMENT` | P5 — asignación en condición interna | gpt-4.1 | 3/3 ✓ |

**Tasa de falsos positivos: 0% (0/18 invocaciones).** Ambos modelos reconocen en todos los casos que la transformación no es aplicable.

---

### 7.3 Comparativa prompt v2.0 few-shot vs v1.0 zero-shot

**36 invocaciones:** mismos 6 casos de la fase 9, mismos modelos, mismo protocolo excepto el prompt.

| Modelo | Tasa (v1.0 zero-shot) | Tasa (v2.0 few-shot) | Variación |
|---|---|---|---|
| gpt-4o | 100% | **100%** | 0 |
| gpt-4.1 | 83.3% | **83.3%** | 0 |

`REAL_COMMONS_COLLECTIONS_GET` sigue siendo el único caso que gpt-4.1 falla (3/3 INCORRECT en ambas versiones del prompt). Los ejemplos few-shot no alteran el comportamiento con temperatura 0.

---

### 7.4 Interpretación consolidada

- Los LLMs **no aplican P1–P5** explícitamente: operan heurísticamente, pero identifican correctamente los casos no elegibles cuando la violación es estructural (P2, P4) o de efecto lateral evidente (P5-asignación).
- **Falsos positivos: 0%** sobre el corpus trampa actual. Los modelos son más conservadores de lo esperado.
- **El few-shot no aporta ganancia** con temperatura 0: la consistencia del modelo ya es máxima y los ejemplos no desbloquean el caso que falla (`REAL_COMMONS_COLLECTIONS_GET`). Este caso requiere razonamiento sobre pureza de método, no más ejemplos de sintaxis.
- Diferencia clave entre enfoques: **el detector es conservador y formal (P1–P5 verificadas en AST); los LLMs son heurísticos pero sorprendentemente robustos** en los casos evaluados.

---

## 8. Componentes de código clave

| Fichero | Rol |
|---|---|
| `src/.../detection/NestedIfDetector.java` | Implementa P1–P5 y P5'; constructor acepta `DetectionMode` |
| `src/.../detection/DetectionMode.java` | Enum `STRICT` / `RELAXED` |
| `src/.../detection/MethodCallAllowlist.java` | Allowlist de métodos presumiblemente puros |
| `src/.../detection/DiscardReason.java` | Enum de motivos de descarte (trazabilidad RQ1) |
| `src/.../experiment/BatchRunner.java` | Pipeline: parseo → detección → transformación → medición |
| `src/.../experiment/Rq2ComparisonExecutor.java` | Comparativa STRICT vs RELAXED, 3 corpus |
| `src/.../experiment/Rq2TutorBatchExecutor.java` | Ejecución sobre tutor-corpus |
| `src/.../analysis/CognitiveComplexityCalculator.java` | Proxy de complejidad cognitiva |
| `output/rq2-comparison/rq2-comparison-summary.md` | Tabla comparativa con todos los datos |
| `output/rq3-campaign-real-phase9/rq3-aggregated.md` | Resultados RQ3 fase 9 |
| `src/.../llm/LlmTrapSubset.java` | Define los 3 casos trampa (todos inelegibles) |
| `src/.../experiment/TrapCorpusLoader.java` | Carga casos desde `/trap-corpus/` |
| `output/rq3-trap-campaign/rq3-trap-aggregated.md` | Resultados campaña trampa (falsos positivos) |
| `src/.../llm/LlmPromptBuilder.java` | Prompt v1.0 (zero-shot) y v2.0 (few-shot) |
| `output/rq3-promptv2-campaign/rq3-promptv2-aggregated.md` | Resultados comparativa prompt v2.0 |

---

## 9. Limitaciones reconocidas

| Tipo | Limitación | Mitigación |
|---|---|---|
| Validez interna | P1–P5 son condiciones *suficientes*, no necesarias — puede haber casos seguros que se descartan | Modo RELAXED como segunda condición; trabajo futuro: análisis de pureza interprocedural |
| Validez interna | `CognitiveComplexityCalculator` es proxy, no SonarQube exacto | Validación complementaria con SonarQube planificada |
| Validez externa | Solo Java, solo una transformación, N pequeño | Alcance explícito del MVP; framework extensible |
| Validez de constructo | CC como proxy de legibilidad humana, no legibilidad directa | Mitigación futura: estudio Likert / revisión humana |
| tutor-corpus | 6 métodos son outliers seleccionados por CC extrema, no muestra aleatoria | Se documenta como tal; el desglose de descartes es en sí un hallazgo |

---

## 10. Estado de la memoria

| Capítulo | Estado |
|---|---|
| Cap. 1 — Introducción | [PENDIENTE / EN BORRADOR] |
| Cap. 2 — Estado del arte | [PENDIENTE / EN BORRADOR] |
| Cap. 3 — Objetivos | Esqueleto disponible (`memoria/cap3-objetivos.md`) |
| Cap. 4 — Diseño experimental | Esqueleto disponible (`memoria/cap4-experimento.md`) |
| Cap. 5 — Resultados | **Datos generados; redacción pendiente** |
| Cap. 6 — Discusión y conclusiones | Pendiente |

---

## 11. Pendientes priorizados

| Prioridad | Tarea | Esfuerzo estimado |
|---|---|---|
| 🔴 Alta | Validación dinámica: ejecutar tests del proyecto fuente sobre código refactorizado | 1–2 sesiones |
| 🔴 Alta | Redactar §5 (resultados) con tablas de §6 de este handout | 1–2 sesiones |
| 🟡 Media | Validación con SonarQube sobre subset de 4 casos | 1 sesión |
| 🟡 Media | Reporte de tasas de descarte por `DiscardReason` × corpus (RQ1 cuantitativa) | 1 sesión |
| ✅ Hecho | Campaña RQ3 con casos trampa — 0% falsos positivos (0/18), ambos modelos | Completado |
| ✅ Hecho | Comparativa prompt v2.0 few-shot — sin mejora sobre v1.0 (temperatura 0) | Completado |
| 🟢 Baja | Documentar tutor-corpus en §4.5 de la memoria | 30 min |
| 🟢 Baja | Estudio Likert de legibilidad (triangulación de CC) | 1 semana |

---

## 12. Preguntas propuestas al tutor

1. ¿Prefiere que el modo RELAXED se presente como condición experimental paralela o como extensión del MVP?
2. ¿El corpus tutor se trata como *replicación* o como *caso de estudio complementario* en la memoria?
3. ¿La validación con SonarQube es necesaria para la entrega o es suficiente con la nota metodológica?
4. ¿Qué capítulos tienen prioridad de revisión antes de la entrega?

## 13. Marco investigador / aprendizajes provisionales

Más allá de la implementación del prototipo, el trabajo me está permitiendo situar el problema en un punto intermedio entre dos enfoques distintos de refactorización automática:

- **Enfoque formal y conservador**, basado en reglas explícitas de aplicabilidad sobre AST (P1–P5).
- **Enfoque heurístico**, representado por los LLMs, que no aplican esas precondiciones de forma explícita sino que deciden a partir de patrones aprendidos de simplificación.

Desde ese punto de vista, el interés del TFM no está solo en “hacer la transformación”, sino en responder a tres cuestiones:

1. **Cuándo puede hacerse con seguridad**  
   El detector no intenta maximizar cobertura a toda costa, sino delimitar condiciones suficientes para preservar la equivalencia observacional en el patrón MVP.

2. **Qué se gana realmente al hacerla**  
   La complejidad cognitiva se ha elegido porque el problema estudiado afecta directamente al flujo de control y al anidamiento, que es precisamente donde esta métrica resulta más informativa.

3. **Cómo se comportan los LLMs frente a ese criterio conservador**  
   La comparación con los modelos no se plantea como una competición abstracta, sino como una forma de estudiar hasta qué punto un enfoque heurístico respeta o no las restricciones que el baseline determinista considera necesarias.

### Aprendizajes provisionales

- En los corpus más realistas, **no siempre domina el problema de los efectos secundarios**; en varios casos la barrera principal es más bien **estructural**, por ejemplo la presencia de ramas `else` o de bloques con varias sentencias.
- La relajación parcial de P5 mejora claramente la cobertura en código real, pero **no cambia apenas el comportamiento en el `tutor-corpus`**, lo que sugiere que en métodos de complejidad extrema el cuello de botella puede estar más en la estructura de control que en la pureza de las condiciones.
- En RQ3, la divergencia observada entre **gpt-4o** y **gpt-4.1** en un caso no elegible sugiere que los LLMs pueden comportarse muy bien en casos compatibles con el baseline, pero no necesariamente respetan de forma robusta las precondiciones del detector.

### Preguntas que me abre el trabajo

- ¿Hasta qué punto conviene refinar P5 mediante análisis de pureza más preciso en lugar de una política conservadora por patrones?
- ¿Es suficiente trabajar sobre condiciones, o en código industrial complejo el problema principal pasa antes por normalizar estructuras con `else`?
- ¿La complejidad cognitiva reducida por la transformación se traduce siempre en una mejora real de legibilidad, o hay casos donde solo baja la métrica?
