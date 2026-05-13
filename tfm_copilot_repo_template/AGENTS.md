# AGENTS.md

## Contexto del proyecto
Este repositorio pertenece a un Trabajo de Fin de Máster sobre **refactorización de sentencias condicionales para reducir la complejidad cognitiva de código**, con foco inicial en Java.

## Preguntas de investigación oficiales
Mantén siempre estas preguntas **literalmente**, sin reformularlas ni sustituirlas:

- **RQ1.** ¿En qué casos se pueden combinar sentencias condicionales anidadas y cuál sería el resultado?
- **RQ2.** ¿Qué impacto tienen estas refactorizaciones en la complejidad cognitiva?
- **RQ3.** ¿Pueden los grandes modelos de lenguaje realizar esta refactorización de forma correcta y automática?

## Regla crítica
Nunca cambies ni reformules las preguntas de investigación oficiales del TFM. Si necesitas concretarlas para implementación, redacción o evaluación, usa subsecciones de operacionalización, alcance, hipótesis de trabajo o criterios experimentales, pero mantén siempre las RQ originales literalmente.

## Objetivo general
Diseñar, implementar y evaluar un enfoque para detectar y refactorizar sentencias condicionales anidadas en Java con el fin de reducir la complejidad cognitiva del código, preservando su semántica cuando corresponda. Además, comparar este enfoque con refactorizaciones generadas por grandes modelos de lenguaje bajo un protocolo experimental controlado.

## Entregables
- Memoria del TFM
- Herramienta o prototipo de análisis/refactorización
- Paquete de replicación
- Manual técnico

## Forma de trabajo esperada
1. Inspecciona primero el repositorio antes de proponer cambios.
2. Resume el estado actual del proyecto.
3. Divide el trabajo en pasos pequeños y verificables.
4. Prioriza primero una versión mínima viable.
5. Prefiere decisiones conservadoras si hay ambigüedad.
6. Documenta siempre:
   - qué cambias,
   - por qué,
   - qué asumes,
   - cómo validarlo.

## Límites de seguridad metodológica
- No inventes bibliografía, experimentos, resultados ni afirmaciones académicas.
- No inventes clases, métodos, APIs ni rutas si no existen en el repositorio.
- No conviertas el TFM en un sistema híbrido enorme salvo instrucción explícita.
- Mejor rechazar casos dudosos que aplicar refactorizaciones inseguras.

## Foco técnico inicial
Transformación base a estudiar:

```java
if (A) {
    if (B) {
        S
    }
}
```

hacia:

```java
if (A && B) {
    S
}
```

### Restricciones iniciales
- Mantener el orden de evaluación original.
- No reordenar expresiones.
- No aplicar inicialmente si hay indicios de:
  - side effects,
  - null safety dudosa,
  - cambios de estado previos,
  - else / else if complejos,
  - lambdas, streams o casos avanzados no soportados.

## Convenciones de salida del agente
Cuando trabajes, usa esta estructura:
- Diagnóstico del estado actual
- Plan inmediato
- Cambios concretos propuestos
- Archivos a tocar
- Riesgos
- Validación

## Redacción de memoria
- Usa estilo académico claro y sobrio.
- No rellenes con texto vacío.
- Marca claramente:
  - [PENDIENTE DE CITA]
  - [PENDIENTE DE VERIFICACIÓN]
  - [HIPÓTESIS DE TRABAJO]
