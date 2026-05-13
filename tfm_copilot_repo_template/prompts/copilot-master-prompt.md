# Prompt maestro para arrancar el TFM

Estás trabajando en mi Trabajo de Fin de Máster.

Tema del TFM:
Refactorización de sentencias condicionales para reducir la complejidad cognitiva de código.

Objetivo general:
Diseñar, implementar y evaluar un enfoque para detectar y refactorizar sentencias condicionales anidadas en Java con el fin de reducir la complejidad cognitiva del código, preservando su semántica cuando corresponda. Además, comparar este enfoque con refactorizaciones generadas por grandes modelos de lenguaje bajo un protocolo experimental controlado.

Preguntas de investigación oficiales del TFM:
RQ1. ¿En qué casos se pueden combinar sentencias condicionales anidadas y cuál sería el resultado?
RQ2. ¿Qué impacto tienen estas refactorizaciones en la complejidad cognitiva?
RQ3. ¿Pueden los grandes modelos de lenguaje realizar esta refactorización de forma correcta y automática?

Entregables esperados:
- Memoria del TFM
- Herramienta o prototipo de análisis/refactorización
- Paquete de replicación
- Manual técnico

Forma de trabajo obligatoria:
- Trabaja de forma autónoma, pero siempre por fases.
- Antes de modificar nada, inspecciona el repositorio y resume:
  1) estructura actual,
  2) tecnologías detectadas,
  3) archivos clave,
  4) riesgos o huecos.
- Después crea un plan de trabajo dividido en tareas pequeñas, priorizadas y verificables.
- No inventes bibliografía, resultados, métricas, experimentos ni afirmaciones académicas.
- No inventes APIs, clases, métodos ni rutas si no existen; primero busca en el repositorio.
- No cambies las preguntas de investigación oficiales.
- Puedes concretar su operacionalización, su alcance práctico y sus criterios de evaluación, pero sin reformularlas ni sustituirlas.
- Cuando propongas texto de memoria, distingue claramente entre:
  - contenido confirmado por el repositorio o por fuentes ya validadas,
  - contenido pendiente de verificar.
- Cuando implementes código, explica:
  - qué cambias,
  - por qué lo cambias,
  - qué precondiciones asumes,
  - cómo validar que no has roto el comportamiento.
- Mantén el alcance controlado: primero casos simples y seguros, luego extensiones.
- No conviertas el TFM en un sistema híbrido enorme salvo que el repositorio o mis instrucciones lo pidan explícitamente.

Criterios técnicos iniciales de la refactorización:
- Lenguaje principal: Java
- Foco inicial:
  if (A) {
      if (B) {
          S
      }
  }
  -> if (A && B) { S }
- Mantener el orden de evaluación original.
- No aplicar la transformación cuando haya indicios de:
  - side effects,
  - dependencia de null safety rota,
  - cambios de estado previos,
  - estructuras else/else if complejas,
  - lambdas/streams/casos avanzados no soportados aún.
- Priorizar una implementación determinista basada en análisis estructural.
- La comparación con LLMs responde a RQ3 y es una fase posterior al núcleo inicial del prototipo.

Sobre la memoria:
- Ayúdame a redactarla, pero no escribas afirmaciones dudosas como si fueran hechos.
- Cuando redactes, usa estilo académico claro, preciso y sobrio.
- No rellenes con texto vacío.
- Marca explícitamente [PENDIENTE DE CITA], [PENDIENTE DE VERIFICACIÓN] o [HIPÓTESIS DE TRABAJO] cuando corresponda.

Tu primera tarea ahora es:
1. Inspeccionar el repositorio completo.
2. Resumir el estado actual del proyecto.
3. Proponer un plan maestro realista con fases para desarrollo + memoria.
4. Identificar el siguiente paso más útil y ejecutarlo.
