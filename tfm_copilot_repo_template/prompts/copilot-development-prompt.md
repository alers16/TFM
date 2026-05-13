# Prompt para que Copilot trabaje en el desarrollo

Quiero que trabajes como agente de desarrollo para el prototipo de mi TFM.

Tema:
Refactorización de sentencias condicionales para reducir la complejidad cognitiva de código.

Preguntas de investigación oficiales:
RQ1. ¿En qué casos se pueden combinar sentencias condicionales anidadas y cuál sería el resultado?
RQ2. ¿Qué impacto tienen estas refactorizaciones en la complejidad cognitiva?
RQ3. ¿Pueden los grandes modelos de lenguaje realizar esta refactorización de forma correcta y automática?

Objetivo del prototipo:
Construir una herramienta o prototipo capaz de detectar oportunidades de combinación de sentencias condicionales anidadas en Java y aplicar la refactorización cuando sea segura según el alcance definido del proyecto.

Transformación objetivo inicial:
if (A) {
    if (B) {
        S
    }
}
-> if (A && B) {
    S
}

Restricciones obligatorias:
- No cambies ni reformules las preguntas de investigación.
- El desarrollo debe servir para responder a las RQ oficiales, no para redefinirlas.
- No inventes clases, APIs ni dependencias si no existen; primero inspecciona el proyecto.
- Antes de programar, identifica la arquitectura actual y el punto de integración correcto.
- Empieza por una versión mínima viable.
- No intentes cubrir todos los casos de golpe.
- Mantén una estrategia conservadora: mejor rechazar casos dudosos que transformar código inseguro.
- Documenta explícitamente las precondiciones de aplicabilidad.
- Conserva el orden de evaluación.
- No reordenes expresiones.
- Evita inicialmente casos con:
  - else / else if,
  - side effects sospechosos,
  - asignaciones en condiciones,
  - lambdas,
  - streams,
  - concurrencia,
  - excepciones complejas,
  - lógica difícil de validar.
- Cada cambio debe ir acompañado de tests o una propuesta clara de validación.

Flujo de trabajo obligatorio:
1. Inspecciona el repositorio y localiza:
   - parser/AST usado,
   - módulos relevantes,
   - tests existentes,
   - utilidades de análisis ya implementadas.
2. Resume dónde encaja mejor la nueva funcionalidad.
3. Diseña una primera versión del detector:
   - patrón buscado,
   - representación interna,
   - precondiciones.
4. Diseña luego el transformador.
5. Añade o adapta tests para:
   - caso válido básico,
   - caso inválido por else,
   - caso inválido por side effect sospechoso,
   - caso inválido por null safety dudosa,
   - verificación de compilación si aplica.
6. Documenta cómo cada parte contribuye a responder:
   - RQ1: casos en que la combinación es aplicable y resultado de la transformación,
   - RQ2: medición del impacto en complejidad cognitiva,
   - RQ3: futura comparación con LLMs bajo el mismo criterio.
7. Mantén una lista de extensiones futuras, pero no las implementes aún.

Quiero que tu salida tenga siempre este formato:
- Diagnóstico del estado actual
- Plan inmediato
- Cambios concretos propuestos
- Archivos a tocar
- Riesgos
- Validación

Empieza ahora inspeccionando el repositorio y proponiendo la implementación mínima viable.
