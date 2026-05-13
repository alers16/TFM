# Procedimiento de contraste con SonarSource Cognitive Complexity

## Propósito

Este documento describe el procedimiento seguido para contrastar las mediciones
de complejidad cognitiva del calculador provisional del prototipo con los valores
de referencia de la especificación SonarSource.

**Nota metodológica:** Este contraste forma parte de las amenazas a la validez
interna del estudio (RQ2). No sustituye una validación exhaustiva con SonarQube/SonarLint
sobre todo el corpus experimental.

## Alcance del contraste

### Subconjunto evaluado
- **Corpus piloto:** 8 casos sintéticos representativos
- **Corpus real:** 10 métodos extraídos de proyectos OSS referenciados en
  Saborido et al. (IEEE Access, 2022)

### Patrones cubiertos
El calculador provisional implementa las siguientes reglas del modelo SonarSource:
- Incremento estructural (+1): `if`, `else if`, `else`, `for`, `for-each`,
  `while`, `do-while`, `switch`, `catch`, `break`/`continue` con etiqueta
- Incremento de anidamiento: aplica a `if`, `for`, `for-each`, `while`,
  `do-while`, `switch`, `catch`
- Operadores lógicos: +1 por secuencia de operadores del mismo tipo (`&&`, `||`)

### Limitaciones conocidas (no implementadas)
- Operador ternario (`?:`)
- Operadores lógicos fuera de condiciones de control de flujo
- Detección de recursión
- Lambdas como incremento de anidamiento
- Clases anónimas o internas como incremento de anidamiento

## Procedimiento de obtención de valores Sonar

### Opción A: Cálculo manual (usado en esta fase)
1. Aplicar las reglas de la especificación SonarSource línea a línea
2. Contar incrementos estructurales (+1 por estructura de control)
3. Contar incrementos de anidamiento (+nivel actual por estructura)
4. Contar secuencias de operadores lógicos
5. Verificar por inspección cruzada (dos revisiones independientes)

### Opción B: SonarLint IDE (para verificación posterior)
1. Instalar extensión SonarLint en VS Code o IntelliJ
2. Abrir cada archivo Java del corpus como archivo individual
3. Anotar el valor de CC reportado por SonarLint
4. Comparar con el valor del calculador provisional

### Opción C: sonar-scanner CLI (para verificación a escala)
1. Configurar proyecto con `sonar-project.properties`
2. Ejecutar `sonar-scanner` sobre el directorio de corpus
3. Consultar métricas por método en el dashboard
4. Exportar resultados para comparación automatizada

## Resultados del contraste — Corpus piloto

| Caso | Prov. Before | Prov. After | Sonar Before | Sonar After | Match | Notas |
|------|-------------|-------------|-------------|-------------|-------|-------|
| PILOT_VALID_SIMPLE | 3 | 2 | 3 | 2 | ✓ | if+if → if(A&&B) |
| PILOT_VALID_NULL_CHECK | 3 | 2 | 3 | 2 | ✓ | null check + array.length |
| PILOT_VALID_BOUNDS_CHECK | 4 | 2 | 4 | 2 | ✓ | if(A&&B)+if(C) → if(A&&B&&C) |
| PILOT_VALID_NESTED_IN_LOOP | 6 | 4 | 6 | 4 | ✓ | for+if+if, mayor impacto |
| PILOT_INVALID_ELSE_BRANCH | 4 | 4 | 4 | 4 | ✓ | Rechazado P1, CC invariante |
| PILOT_INVALID_METHOD_GUARD | 3 | 3 | 3 | 3 | ✓ | Rechazado P5, CC invariante |
| PILOT_INVALID_MULTI_STMT | 3 | 3 | 3 | 3 | ✓ | Rechazado P2, CC invariante |
| PILOT_MIXED_OPPORTUNITIES | 6 | 5 | 6 | 5 | ✓ | 1 combinada, 1 rechazada |

**Resultado:** Coincidencia exacta 8/8 (100%) en before y after.

## Resultados del contraste — Corpus real

| Caso | Prov. CC | Sonar CC | Match | Elegible | Notas |
|------|----------|----------|-------|----------|-------|
| REAL_COMMONS_MATH_CONVERGED | 3 | 3 | ✓ | Sí | Comparaciones puras |
| REAL_COMMONS_COLLECTIONS_ISEMPTY | 3 | 3 | ✓ | Sí | Array.length (campo) |
| REAL_ANT_MATCH_PATH | 6 | 6 | ✓ | Parcial | 1 par combinable de 2 |
| REAL_COMMONS_LANG_CONTAINS_NONE | 15 | 15 | ✓ | Parcial | 1 par combinable |
| REAL_COMMONS_LANG_MID | 10 | 10 | ✓ | Parcial | 1 par combinable de 3 |
| REAL_COMMONS_LANG_CHOMP | 6 | 6 | ✓ | No | P5: str.length() |
| REAL_COMMONS_LANG_IS_NUMERIC | 10 | 10 | ✓ | No | P5: cs.length() |
| REAL_COMMONS_COLLECTIONS_GET | 3 | 3 | ✓ | No | P5: containsKey() |
| REAL_COMMONS_MATH_VALIDATE_RANGE | 4 | 4 | ✓ | No | P1: outer has else |
| REAL_ANT_EXECUTE_TASK | 3 | 3 | ✓ | No | P2: multi-statement |

**Resultado:** Coincidencia exacta 10/10 (100%) en CC before.

## Interpretación

### Fortalezas
- Para el subconjunto de patrones soportados (if/else/for sin ternarios,
  lambdas ni recursión), el calculador provisional coincide exactamente
  con la especificación SonarSource.
- El acuerdo 100% valida que la implementación es correcta para el alcance
  del MVP.

### Limitaciones y amenazas a la validez
- **Subconjunto pequeño:** 18 casos no son estadísticamente representativos.
  [PENDIENTE DE VERIFICACIÓN: ampliar con SonarLint sobre corpus completo]
- **Sin ternarios:** ningún caso del subconjunto usa `?:`, que es una
  limitación conocida del calculador.
- **Cálculo manual:** los valores Sonar se obtuvieron por inspección manual
  de la especificación, no por ejecución de SonarQube/SonarLint.
  [PENDIENTE DE VERIFICACIÓN: contrastar con herramienta oficial]
- **Sesgo de selección:** los casos fueron seleccionados para representar
  patrones del MVP, no por muestreo aleatorio del corpus completo.

## Referencia

- G. Ann Campbell, "Cognitive Complexity — A new way of measuring
  understandability", SonarSource, 2021.
  [PENDIENTE DE CITA EXACTA — versión del documento]
- R. Saborido, J. Ferrer, F. Chicano y E. Alba, "Automatizing Software
  Cognitive Complexity Reduction," IEEE Access, vol. 10, 2022,
  doi:10.1109/ACCESS.2022.3144743.
