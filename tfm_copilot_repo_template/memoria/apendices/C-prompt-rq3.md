# Apéndice C. Prompt RQ3 v1.0 y oráculo

## Objetivo

Reproducir literalmente el prompt usado en la campaña RQ3 y la lógica del oráculo de validación.

## Subsecciones previstas

- C.1 System prompt v1.0 (literal). Fuente: `LlmPromptBuilder`.
- C.2 User prompt v1.0 (literal). Fuente: `LlmPromptBuilder`.
- C.3 Reglas y justificación del diseño zero-shot.
- C.4 Oráculo `LlmResponseValidator` (8 pasos) con guardia de elegibilidad v1.1.

## Evidencias del repositorio

- [src/main/java/es/tfm/refactoring/llm/LlmPromptBuilder.java](../../src/main/java/es/tfm/refactoring/llm/LlmPromptBuilder.java)
- [src/main/java/es/tfm/refactoring/llm/LlmResponseValidator.java](../../src/main/java/es/tfm/refactoring/llm/LlmResponseValidator.java)
- [docs/llm-experiment-protocol.md](../../docs/llm-experiment-protocol.md)

## Notas

- Reproducir el prompt **literalmente** desde el código fuente, sin parafrasear.
