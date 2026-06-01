# Instrucciones persistentes del proyecto (TFM) para Claude Code

Este repositorio contiene el Trabajo Fin de Máster (TFM) del Máster Universitario en
Ingeniería del Software e Inteligencia Artificial (UMA). El TFM tiene una orientación
investigadora: estudio del estado del arte, propuesta novedosa, comparación crítica con
métodos existentes y resaltado de contribuciones.

## 1. Regla principal
Nunca cambies ni reformules las preguntas de investigación oficiales del TFM. Si necesitas
concretarlas para implementación, redacción o evaluación, usa subsecciones de
operacionalización, alcance, hipótesis de trabajo o criterios experimentales, pero mantén
siempre las RQ originales literalmente.

## 2. Preguntas de investigación oficiales
- **RQ1.** ¿En qué casos se pueden combinar sentencias condicionales anidadas y cuál sería el resultado?
- **RQ2.** ¿Qué impacto tienen estas refactorizaciones en la complejidad cognitiva?
- **RQ3.** ¿Pueden los grandes modelos de lenguaje realizar esta refactorización de forma correcta y automática?

## 3. Desarrollo (código)
- Inspecciona siempre el repositorio antes de codificar.
- No inventes APIs ni clases.
- Empieza por una implementación mínima viable.
- Mantén una estrategia conservadora.
- Documenta precondiciones de aplicabilidad.
- Cada cambio debe tener validación o propuesta de validación.

## 4. Memoria
- No inventes referencias.
- No presentes resultados no obtenidos.
- Distingue claramente entre contenido confirmado y contenido pendiente.
- Mantén alineación estricta con las RQ oficiales y con el alcance real del prototipo.
- La memoria se redacta en **LaTeX** siguiendo el formato **LNCS** recomendado por la guía de
  la UMA (márgenes a página completa con `fullpage` o equivalente).

## 5. Estructura mínima exigida por la guía del TFM (UMA)
La memoria debe cubrir, como mínimo, estos contenidos (el orden y la organización final
quedan a criterio del autor, pero estos contenidos deben tratarse):

1. **Resumen / Abstract / Keywords / Tutor(a)** (resumen en español e inglés).
2. **Introducción**: definición de objetivos, descripción del problema a resolver, situación
   de partida, relevancia de la solución propuesta y organización del resto del trabajo.
3. **Antecedentes (estado del arte)**: situar los antecedentes con referencias a la
   bibliografía más relevante; estudio comparativo que muestre aportaciones y relevancia
   frente a soluciones existentes.
4. **Descripción del problema**: descripción clara y precisa del problema o familia de
   problemas que se aborda.
5. **Detalles de la propuesta**: solución propuesta, tecnología utilizada y comparación con
   otras soluciones, resaltando el aporte logrado.
6. **Conclusiones**: resumen del trabajo, contribuciones y trabajo futuro (orientado a una
   posible tesis doctoral o al enfoque investigador).
7. **Referencias**.

Nota: la guía permite mapear estos contenidos a una estructura más detallada (Introducción,
Estado del arte, Objetivos y RQ, Metodología, Diseño del enfoque, Implementación, Diseño
experimental, Resultados, Amenazas a la validez, Conclusiones), siempre que se cubran los
contenidos mínimos anteriores.

## 6. Enfoque técnico inicial
Transformación objetivo inicial:

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

## 7. Casos a evitar inicialmente
- else / else if complejos
- side effects sospechosos
- asignaciones en condiciones
- lambdas
- streams
- concurrencia
- excepciones complejas
- lógica difícil de validar

## 8. Formato de respuesta preferido (tareas de código)
- Diagnóstico del estado actual
- Plan inmediato
- Cambios concretos propuestos
- Archivos a tocar
- Riesgos
- Validación

## 9. Subagentes y skills disponibles
Agentes en `.claude/agents/`:
- `memory-writer` — redacta/revisa la memoria en LaTeX con trazabilidad y rigor.
- `state-of-the-art` — construye/revisa el estado del arte sin inventar bibliografía.
- `experiment-design` — diseña el protocolo experimental para RQ1–RQ3.
- `java-refactoring` — detecta e implementa refactorizaciones seguras de condicionales.

Skills en `.claude/skills/`: `tfm-memory-writing`, `tfm-state-of-the-art`,
`tfm-experiment-design`, `tfm-java-conditional-refactoring`, `tfm-latex`.
