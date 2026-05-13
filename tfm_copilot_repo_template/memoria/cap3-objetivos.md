# Capítulo 3: Objetivos y preguntas de investigación

## 3.1 Objetivo general

Diseñar, implementar y evaluar un enfoque para detectar y refactorizar sentencias condicionales anidadas en Java con el fin de reducir la complejidad cognitiva del código, preservando su semántica cuando corresponda. Además, comparar este enfoque con refactorizaciones generadas por grandes modelos de lenguaje bajo un protocolo experimental controlado.

## 3.2 Objetivos específicos

1. Identificar y formalizar los casos en los que es posible combinar sentencias condicionales anidadas en Java de forma segura, documentando las precondiciones de aplicabilidad y el resultado de la transformación.
2. Diseñar e implementar un prototipo determinista capaz de detectar dichas oportunidades de refactorización y aplicar la transformación correspondiente sobre el árbol de sintaxis abstracta (AST) del código fuente.
3. Medir el impacto de las refactorizaciones aplicadas sobre la complejidad cognitiva del código, utilizando una métrica definida y reproducible.
4. Comparar las refactorizaciones generadas por el prototipo con las producidas por grandes modelos de lenguaje, bajo un protocolo experimental controlado, en términos de corrección, completitud y calidad del resultado.

## 3.3 Preguntas de investigación

- **RQ1.** ¿En qué casos se pueden combinar sentencias condicionales anidadas y cuál sería el resultado?
- **RQ2.** ¿Qué impacto tienen estas refactorizaciones en la complejidad cognitiva?
- **RQ3.** ¿Pueden los grandes modelos de lenguaje realizar esta refactorización de forma correcta y automática?

## 3.4 Operacionalización de las preguntas de investigación

### RQ1 — Alcance operativo
- Se estudian condicionales anidados de tipo `if` en Java.
- Foco inicial: el caso base donde un `if` contiene exactamente otro `if`, sin ramas `else` ni `else if`, y sin sentencias adicionales en el bloque externo.
- El resultado es la transformación `if (A) { if (B) { S } }` → `if (A && B) { S }`.
- Se documentan las precondiciones de aplicabilidad (ausencia de side effects, ausencia de else, unicidad de sentencia interna).
- Extensiones posteriores podrán ampliar el conjunto de patrones cubiertos.

### RQ2 — Criterio de medición
- La complejidad cognitiva se mide siguiendo el modelo propuesto por SonarSource [PENDIENTE DE CITA].
- Se compara el valor de complejidad cognitiva antes y después de aplicar la refactorización sobre cada caso del corpus de evaluación.
- Se reporta la reducción absoluta y relativa por caso y agregada.

### RQ3 — Protocolo de comparación
- Se selecciona un subconjunto del corpus de evaluación.
- Se solicita a uno o más LLMs que realicen la misma refactorización bajo un prompt estandarizado.
- Se comparan los resultados en tres dimensiones: corrección semántica, completitud de detección y calidad del código resultante.
- El protocolo se describe en detalle en el capítulo de diseño experimental [PENDIENTE DE VERIFICACIÓN — depende del diseño experimental].
