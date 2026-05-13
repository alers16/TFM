# Capítulo 2. Antecedentes

> **Bloque UMA obligatorio.** Sustituye al "estado del arte" del índice anterior.

## Objetivo del capítulo

Establecer el marco conceptual y técnico necesario para entender la propuesta: complejidad cognitiva, refactorización, análisis estático sobre AST, LLMs aplicados a tareas de código y metodología Design Science Research.

## Subsecciones previstas

- **2.1 Complejidad cognitiva.** Definición, motivación frente a complejidad ciclomática, modelo de incrementos por anidamiento. [PENDIENTE DE CITA]
- **2.2 Refactorización y preservación semántica.** Concepto, criterios de seguridad, refactorizaciones automáticas. [PENDIENTE DE CITA]
- **2.3 Análisis estático sobre AST con JavaParser.** Modelo de AST, manipulación segura, limitaciones. [PENDIENTE DE CITA]
- **2.4 LLMs aplicados a tareas de código.** Capacidades y limitaciones documentadas para refactorización y generación de código. [PENDIENTE DE CITA]
- **2.5 Metodología Design Science Research.** Ciclo build–evaluate–reflect aplicado al TFM. [PENDIENTE DE CITA]
- **2.6 Síntesis crítica orientada al problema.** Hueco que cubre este TFM (sin desplazar el enfoque aprobado).

## Evidencias del repositorio

- [pom.xml](../pom.xml) (versión de JavaParser usada).
- [src/main/java/es/tfm/refactoring/analysis/](../src/main/java/es/tfm/refactoring/analysis/) (proxy de complejidad cognitiva con limitaciones documentadas).
- [docs/llm-experiment-protocol.md](../docs/llm-experiment-protocol.md) (justificación del uso de LLMs).

## Notas

- **No inventar bibliografía.** Cada afirmación con cita debe llevar `[PENDIENTE DE CITA]` hasta verificación.
- [PENDIENTE DE REDACCIÓN METODOLÓGICA] El proxy del prototipo no es equivalente exacto a SonarQube; explicitarlo aquí también.
- Skill aplicable: `tfm-state-of-the-art`.
