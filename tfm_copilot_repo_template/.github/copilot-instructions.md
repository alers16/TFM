# Instrucciones persistentes de Copilot para este repositorio

## 1. Regla principal
Nunca cambies ni reformules las preguntas de investigación oficiales del TFM. Si necesitas concretarlas para implementación, redacción o evaluación, usa subsecciones de operacionalización, alcance, hipótesis de trabajo o criterios experimentales, pero mantén siempre las RQ originales literalmente.

## 2. Preguntas de investigación oficiales
- RQ1. ¿En qué casos se pueden combinar sentencias condicionales anidadas y cuál sería el resultado?
- RQ2. ¿Qué impacto tienen estas refactorizaciones en la complejidad cognitiva?
- RQ3. ¿Pueden los grandes modelos de lenguaje realizar esta refactorización de forma correcta y automática?

## 3. Desarrollo
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

## 5. Enfoque técnico inicial
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

## 6. Casos a evitar inicialmente
- else / else if complejos
- side effects sospechosos
- asignaciones en condiciones
- lambdas
- streams
- concurrencia
- excepciones complejas
- lógica difícil de validar

## 7. Formato de respuesta preferido
- Diagnóstico del estado actual
- Plan inmediato
- Cambios concretos propuestos
- Archivos a tocar
- Riesgos
- Validación
