---
name: tfm-java-conditional-refactoring
description: Utiliza esta skill para detectar, diseñar e implementar refactorizaciones seguras de condicionales anidados en Java.
---

# Objetivo
Apoyar el desarrollo del prototipo de análisis y refactorización.

# Enfoque inicial
Patrón base:

```java
if (A) {
    if (B) {
        S
    }
}
```

Transformación objetivo:

```java
if (A && B) {
    S
}
```

# Reglas
- Conserva el orden de evaluación.
- No reordenes expresiones.
- Rechaza casos con indicios de side effects o null safety dudosa.
- No cubras else/else if complejos en la primera iteración.
- Cada propuesta debe incluir validación.

# Salida esperada
- detector propuesto,
- precondiciones,
- transformador,
- tests básicos,
- límites conocidos.
