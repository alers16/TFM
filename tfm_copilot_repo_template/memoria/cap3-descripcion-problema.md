# Capítulo 3. Descripción del problema

> **Bloque UMA obligatorio.**

## Objetivo del capítulo

Formular con precisión el problema abordado, fijar el patrón objetivo, operacionalizar RQ1–RQ3 sin reformularlas, y delimitar el alcance.

## Subsecciones previstas

- **3.1 Formulación del problema.** Condicionales anidados como contribuyentes a la complejidad cognitiva.
- **3.2 Patrón objetivo.** Transformación canónica:

  ```java
  if (A) {
      if (B) {
          S
      }
  }
  ```

  ```java
  if (A && B) {
      S
  }
  ```

- **3.3 Preguntas de investigación (literales).**
  - **RQ1.** ¿En qué casos se pueden combinar sentencias condicionales anidadas y cuál sería el resultado?
  - **RQ2.** ¿Qué impacto tienen estas refactorizaciones en la complejidad cognitiva?
  - **RQ3.** ¿Pueden los grandes modelos de lenguaje realizar esta refactorización de forma correcta y automática?
- **3.4 Operacionalización.** Reutilizar §3.4 de [cap3-objetivos.md](cap3-objetivos.md).
- **3.5 Hipótesis de trabajo.** [HIPÓTESIS DE TRABAJO] Marcar cada una explícitamente.
- **3.6 Alcance y exclusiones iniciales.** else/else-if complejos, lambdas, streams, concurrencia, side effects, asignaciones en condiciones.
- **3.7 Riesgos metodológicos.** Limitaciones del proxy CC, tamaño del corpus, dependencia de proveedor LLM.

## Evidencias del repositorio

- [memoria/cap3-objetivos.md](cap3-objetivos.md) (RQ literales y operacionalización ya redactadas).
- [AGENTS.md](../AGENTS.md) §"Foco técnico inicial" y §"Restricciones iniciales".
- [src/main/java/es/tfm/refactoring/detection/](../src/main/java/es/tfm/refactoring/detection/) (precondiciones P1–P5 implementadas).

## Notas

- **No reformular las RQ.** Reproducirlas literalmente.
- Capítulo en gran parte redactable ya a partir del legado.
