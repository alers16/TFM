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
**Tests:** 399/399 en verde.

---

## 5. Corpus experimental (3 corpus)

| Corpus | N casos | Origen | Propósito |
|---|---|---|---|
| `pilot-corpus` | 8 | Sintético | Smoke-test del detector |
| `real-corpus` | 10 | Apache Commons, Ant (código real open-source) | Evaluación RQ2 sobre código realista |
| `tutor-corpus` | 6 | Saborido et al., IEEE Access 2022 — BCV (GPL-3.0) y jMetal (LGPL-3.0) | Replicación parcial del paper del tutor |

> El `tutor-corpus` usa los 6 métodos diana del paper, seleccionados como **outliers de complejidad extrema**,
> no como muestra representativa. Esto es relevante para interpretar sus tasas de elegibilidad (ver §6.3).

---

## 6. Resultados RQ2 — Impacto en complejidad cognitiva

### 6.1 Modo STRICT (P1–P5 completas)

| Corpus | N | Elegibles | % Elegible | ΔCC total | ΔCC medio/caso |
|---|---|---|---|---|---|
| pilot | 8 | 5 | 63% | −7 | −1.4 |
| real | 10 | 5 | 50% | −11 | −2.2 |
| tutor | 6 | 1 | 17% | −3 | −3.0 |
| **Total** | **24** | **11** | **46%** | **−21** | **−1.9** |

### 6.2 Modo RELAXED (P1–P4 + P5' con allowlist de pureza)

P5' acepta llamadas a métodos presumiblemente puros: `isEmpty`, `size`, `contains`, `length`, `equals`,
`startsWith`, `endsWith`, `isPresent`, `compareTo`, getters `getX`/`isX`/`hasX`.

| Corpus | N | Elegibles | % Elegible | ΔCC total | Nuevos vs STRICT |
|---|---|---|---|---|---|
| pilot | 8 | 6 | 75% | −9 | **+1** |
| real | 10 | 8 | 80% | −22 | **+3** |
| tutor | 6 | 1 | 17% | −3 | **0** |
| **Total** | **24** | **15** | **63%** | **−34** | **+4** |

### 6.3 Interpretación

- **RELAXED amplía eligibilidad +36% en real-corpus** (5 → 8) y duplica el ΔCC total (−11 → −22).
- **RELAXED no cambia el tutor-corpus (1/6):** los descartes allí son por P1/P4 (ramas `else`), no por P5.
  - **Hallazgo RQ1:** en código industrial de complejidad extrema la barrera dominante es la presencia de
    ramas `else`, no los side effects en condiciones. Relajar P5 no es suficiente para ese tipo de código.
- Los 2 casos `real-corpus` que ningún modo acepta (`REAL_COMMONS_MATH_VALIDATE_RANGE`, `REAL_ANT_EXECUTE_TASK`)
  fallan por P1 o P2 — barrera estructural, no P5.

> **Nota metodológica:** los valores de CC son estimaciones del prototipo (`CognitiveComplexityCalculator`),
> no equivalentes directos a SonarQube. La validación con SonarQube sobre un subconjunto de 4 casos está
> planificada (ver `docs/rq2-sonar-validation-procedure.md`).

---

## 7. Resultados RQ3 — LLMs como refactorizadores

**Campaña ejecutada:** fase 9 — 6 casos × 2 modelos × 3 intentos = 36 invocaciones.

| Modelo | Éxito | Incorrecto | Tasa de éxito | Consistencia |
|---|---|---|---|---|
| gpt-4o | 18/18 | 0 | **100%** | 100% |
| gpt-4.1 | 15/18 | 3 | **83.3%** | 100% |

**Caso donde gpt-4.1 falla consistentemente:** `REAL_COMMONS_COLLECTIONS_GET` (50% global).

**Interpretación:**
- Los LLMs **no aplican P1–P5** explícitamente: operan heurísticamente.
- gpt-4o produce transformaciones correctas en todos los casos de la campaña.
- gpt-4.1 falla en un caso real-corpus donde la condición original incluye una llamada de método —
  posiblemente porque no detecta el riesgo de side effect que P5 evita por construcción.
- Esto ilustra la diferencia clave: **el detector es conservador y formal; los LLMs son permisivos y heurísticos**.

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
| `output/rq3-campaign-real-phase9/rq3-aggregated.md` | Resultados RQ3 |

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
| 🟡 Media | Campaña RQ3 con casos trampa (side effects en condición) | 2 sesiones |
| 🟢 Baja | Documentar tutor-corpus en §4.5 de la memoria | 30 min |
| 🟢 Baja | Estudio Likert de legibilidad (triangulación de CC) | 1 semana |

---

## 12. Preguntas propuestas al tutor

1. ¿Prefiere que el modo RELAXED se presente como condición experimental paralela o como extensión del MVP?
2. ¿El corpus tutor se trata como *replicación* o como *caso de estudio complementario* en la memoria?
3. ¿La validación con SonarQube es necesaria para la entrega o es suficiente con la nota metodológica?
4. ¿Qué capítulos tienen prioridad de revisión antes de la entrega?
