# RQ2 — Resultados tras la expansión del corpus real (v2)

**Fecha de ejecución:** 2025-05  
**Estado:** Resultados confirmados. Pipeline BUILD SUCCESS (455 tests passing).  
**Artefactos generados:** `output/rq2-batch/` (CSV, JSON, Markdown, metadata)

---

## 1. Cambios realizados en esta iteración

### 1.1 Descubrimiento dinámico de ficheros del corpus

Los dos cargadores de corpus estaban **hardcodeados** con listas de nombres de fichero. Esto impedía añadir nuevos casos sin modificar código fuente.

**Cambio aplicado:**

| Clase | Método modificado | Antes | Después |
|---|---|---|---|
| `RealDatasetLoader` | `standardRealFiles()` | Lista literal de 33 strings | `Files.list()` dinámico sobre `/real-corpus/` |
| `PilotCorpusLoader` | `standardPilotFiles()` | Lista literal de 8 strings | `Files.list()` dinámico sobre `/pilot-corpus/` |

La implementación usa `Class.getResource(dir)`, convierte a `Path` mediante `URI`, y filtra por extensión `.java` con orden alfabético estable. A partir de ahora, cualquier fichero `.java` añadido a las carpetas de recursos se incluye automáticamente sin ningún cambio en código.

Los tests de ambos cargadores también se actualizaron para usar aserciones flexibles (umbral mínimo) en lugar de conteos fijos.

### 1.2 Expansión del corpus real

El corpus real pasó de **33 a 118 casos** (+85 casos, ×3.6 de crecimiento). Los proyectos OSS añadidos son:

| Proyecto | Tipo | Casos añadidos |
|---|---|---|
| Apache Ant | Build tool (Apache) | ~12 totales |
| Apache Commons (Collections, IO, Lang, Math) | Bibliotecas utilitarias (Apache) | 17 totales |
| Apache POI | Procesamiento de documentos Office | 17 totales |
| Bytecode Viewer | Desensamblador JVM | 4 |
| CyberCaptor | Seguridad de redes | 1 |
| Fastjson (alibaba) | Serialización JSON | 17 |
| IoTBroker | Mensajería IoT | 1 |
| Jackson Core | Parser JSON (FasterXML) | 13 |
| Jackson Databind | Data binding JSON (FasterXML) | 21–22 |
| JMetal | Optimización multiobjetivo | 1 |
| Knowage | Business intelligence (SpagoBI) | 12 |
| MOEA Framework | Algoritmos evolutivos | 1 |

### 1.3 Exclusión de ficheros incompatibles

Se detectaron **5 ficheros de Jackson Databind** con sintaxis **Java 16+ (pattern matching instanceof)**:

```java
// Sintaxis no soportada por el analizador del prototipo:
if (old instanceof ObjectNode objectNode) { ... }
```

El `CognitiveComplexityCalculator` usa `StaticJavaParser` con nivel de lenguaje estándar que no soporta esta variante sintáctica, devolviendo CC = -1. Los 5 ficheros fueron **eliminados físicamente** del corpus:

- `RealJacksonDatabindBaseNodeDeserializerUpdateObject.java`
- `RealJacksonDatabindBeanSerializerBaseResolve.java`
- `RealJacksonDatabindMapLikeTypeWithHandlersFrom.java`
- `RealJacksonDatabindObjectMapperTreeToValue.java`
- `RealJacksonDatabindTypeResolverProvider_findTypeResolver.java`

> **Alcance actual del prototipo:** Java 8–15. Los casos con características de Java 16+ quedan fuera del alcance declarado.

---

## 2. Resultados del pipeline RQ2 (corpus v2)

### 2.1 Visión general

| Métrica | Valor |
|---|---|
| **Total de casos** | 126 (8 pilot + 118 real) |
| **Casos elegibles** | 104 / 126 = **82.5%** |
| **Δ CC total acumulado** | **−278** |
| **Δ CC medio por caso elegible** | −278 / 104 = **−2.67** |

### 2.2 Corpus real (118 casos)

| Métrica | Valor |
|---|---|
| Casos | 118 |
| Elegibles | 99 / 118 = **83.9%** |
| Inelegibles | 19 / 118 = 16.1% |
| Δ CC total | **−271** |
| Δ CC medio por caso elegible | −271 / 99 = **−2.74** |
| CC *antes* (media, elegibles) | **28.3** (rango: 3–228) |
| Δ mínimo (mayor reducción) | **−14** (CYBERCAPTOR_VERTEX_GET_RELATED_MACHINE) |
| Δ máximo (menor reducción) | **−1** (múltiples casos) |

### 2.3 Sub-corpus pilot (8 casos)

| Métrica | Valor |
|---|---|
| Elegibles | 5 / 8 = 62.5% |
| Δ CC total | −7 |
| Δ CC medio por elegible | −7 / 5 = −1.4 |

### 2.4 Resultados por proyecto (corpus real)

| Proyecto | Total | Elegibles | Tasa | Δ CC |
|---|---|---|---|---|
| Apache Ant | 12 | 11 | 92% | −31 |
| Apache Commons | 17 | 8 | 47% | −16 |
| Apache POI | 17 | 16 | 94% | −34 |
| Bytecode Viewer | 4 | 4 | 100% | −16 |
| CyberCaptor | 1 | 1 | 100% | −14 |
| Fastjson | 17 | 11 | 65% | −21 |
| IoTBroker | 1 | 1 | 100% | −1 |
| Jackson Core | 13 | 13 | 100% | −36 |
| Jackson Databind | 21 | 19 | 90% | −29 |
| JMetal | 1 | 1 | 100% | −3 |
| Knowage | 12 | 12 | 100% | −64 |
| MOEA Framework | 1 | 1 | 100% | −3 |

> **Nota:** Las métricas de CC son las producidas por el `CognitiveComplexityCalculator` del prototipo, no por SonarQube. Son coherentes con la definición de Cognitive Complexity pero no intercambiables con los valores de Sonar sin calibración.

### 2.5 Top 15 casos por reducción de CC (corpus real)

| Caso | Δ CC |
|---|---|
| CYBERCAPTOR_VERTEX_GET_RELATED_MACHINE | −14 |
| ANT_STRIP_JAVA_COMMENTS_READ | −9 |
| KNOWAGE_DOCUMENT_EXECUTION_RESOURCE_GET_DOCUMENT_EXECUTION_FILTERS | −8 |
| JACKSON_CORE_UTF8_STREAM_JSON_PARSER_SKIP_COLON_FAST | −8 |
| JACKSON_CORE_READER_BASED_JSON_PARSER_SKIP_COLON_FAST | −8 |
| KNOWAGE_DATA_SET_RESOURCE_TRANSFORM_RUNTIME_DRIVERS | −7 |
| KNOWAGE_BUSINESS_MODEL_RESOURCE_GET_DRIVERS_FROM_QBE_DATA_SET | −7 |
| KNOWAGE_DATA_SET_RESOURCE_GET_DRIVERS_FROM_QBE_DATA_SET | −7 |
| KNOWAGE_BUSINESS_MODEL_OPEN_PARAMETERS_GET_BUSINESS_MODEL_EXECUTION_FILTERS | −7 |
| KNOWAGE_META_UTILS_TRANSFORM_RUNTIME_DRIVERS | −7 |
| KNOWAGE_DOCUMENT_EXECUTION_RESOURCE_TRANSFORM_RUNTIME_DRIVERS | −7 |
| KNOWAGE_DOCUMENT_EXECUTION_PARAMETERS_TRANSFORM_RUNTIME_DRIVERS | −7 |
| BYTECODE_VIEWER_REPLACE_STRINGS_SCAN_CLASS_NODE | −6 |
| FASTJSON_JSON_PATH_REMOVE | −5 |
| BYTECODE_VIEWER_SHOW_ALL_STRINGS_EXECUTE | −5 |

### 2.6 Distribución de motivos de descarte (corpus real, inelegibles)

| Categoría de descarte | Ocurrencias |
|---|---|
| `SINGLE_STATEMENT_NOT_IF` | 13 |
| `OUTER_BLOCK_MULTIPLE_STATEMENTS` | 4 |
| `METHOD_CALL_IN_CONDITION` | 4 |
| `OUTER_HAS_ELSE` | 3 |
| `ASSIGNMENT_IN_CONDITION` | 1 |
| `INNER_HAS_ELSE` | 1 |
| `INCREMENT_OR_DECREMENT_IN_CONDITION` | 1 |
| *(sin oportunidad detectada — CC = 0)* | 5 |

> Las ocurrencias suman más de 19 porque cada caso puede acumular múltiples motivos de descarte simultáneos.

---

## 3. Análisis y conclusiones

### 3.1 Tasa de elegibilidad alta y consistente (~84%)

La tasa de elegibilidad del 83.9% en el corpus real es prácticamente idéntica a la observada en iteraciones previas con el corpus reducido (~83%). Esto sugiere que la tasa es **estable al aumentar el tamaño y diversidad del corpus**, lo que refuerza la hipótesis de que la refactorización `if(A){if(B){S}} → if(A&&B){S}` es ampliamente aplicable en código OSS Java de condiciones simples.

Cuatro proyectos alcanzan **100% de elegibilidad** (Bytecode Viewer, CyberCaptor, IoTBroker, Jackson Core, JMetal, Knowage, MOEA Framework), lo que indica que en esos dominios el patrón de if-anidado aparece exclusivamente en forma simple (sin else, sin efectos laterales).

### 3.2 SINGLE_STATEMENT_NOT_IF es el principal motivo de rechazo

La categoría `SINGLE_STATEMENT_NOT_IF` concentra el 48% de los motivos de descarte (13 de 27). Representa casos donde hay un `if` externo que envuelve **una sola sentencia**, pero esa sentencia no es otro `if` — por ejemplo, un return, una llamada a método, o una asignación. En esos casos, no hay anidamiento que combinar: el detector lo identifica correctamente.

Esta es la precondición más selectiva del conjunto: el patrón objetivo requiere que la única sentencia del bloque exterior sea precisamente un `if`.

### 3.3 Casos con CC = 0 son artefactos del extractor

Cinco casos inelegibles tienen `complexityBefore = 0` y `discardCategories` vacío (cuatro de Fastjson y uno de POI). Esto indica que el `CorpusScanRunner` extrajo el método envolvente pero **el cuerpo del if principal no fue incluido en la clase contenedor** que se grabó como fichero del corpus. JavaParser calcula CC = 0 porque no hay if-statements visibles. Estos casos son válidos en el código original pero no se extrajeron correctamente. Constituyen una limitación conocida del generador de corpus.

### 3.4 Reducción media de −2.67 CC es coherente con la definición teórica

La eliminación de un if anidado reduce CC en 2 unidades: el if externo pierde el penalizador de anidamiento (+1), y el if interno también pierde su penalizador de estructura (+1). La reducción media de −2.74 por caso elegible en el corpus real es consistente con esta expectativa teórica. En casos con múltiples oportunidades aplicadas (como JACKSON_CORE_SKIP_COLON_FAST con 4 aplicaciones → Δ=−8), la reducción escala linealmente.

### 3.5 Knowage concentra la mayor reducción absoluta

Knowage (plataforma BI empresarial) contribuye −64 de los −271 Δ del corpus real (**23.7% del total**) con solo 12 casos (12.1%). Sus métodos tienen CC baseline muy alta (valores reportados de 157–228), probablemente debido a lógica de negocio compleja acumulada históricamente. La refactorización reduce −7/−8 unidades de forma absoluta pero el impacto relativo es pequeño (&lt;5%).

Este contraste ilustra una observación relevante para la RQ2: **la reducción absoluta de CC depende fuertemente del CC baseline del método**. La misma refactorización tiene mayor impacto visible en métodos de CC baja (un método de CC=5 que baja a CC=3 mejora un 40%) que en métodos de CC muy alta.

### 3.6 Apache Commons: menor tasa de elegibilidad (47%)

Con solo 8 de 17 casos elegibles, Apache Commons muestra la menor tasa del corpus. Los motivos son variados:

- `METHOD_CALL_IN_CONDITION` (llamadas como `str.length()`, `CharUtils.isAscii(ch)` en el if)
- `OUTER_BLOCK_MULTIPLE_STATEMENTS` (bloque exterior con sentencias adicionales)
- `OUTER_HAS_ELSE` y `INNER_HAS_ELSE`

Las bibliotecas de Commons tienden a usar condiciones compuestas con llamadas de guardia, lo que las hace inelegibles bajo las precondiciones conservadoras actuales del prototipo.

### 3.7 Comparativa con el corpus previo (v1 → v2)

| Métrica | Corpus v1 (33 casos) | Corpus v2 (118 casos) | Variación |
|---|---|---|---|
| Casos reales | 33 | 118 | +85 (+258%) |
| Elegibles (real) | ~27 | 99 | +72 (+267%) |
| Tasa de elegibilidad | ~82% | 83.9% | +1.9 pp |
| Δ CC total (real) | ~−54 | −271 | ×5.0 |

La tasa de elegibilidad se mantiene estable mientras el corpus crece en diversidad. La variación de +1.9 pp está dentro del margen de fluctuación esperado para muestras de este tamaño.

---

## 4. Limitaciones y amenazas a la validez

- **CC del prototipo ≠ CC de SonarQube:** Los valores de CC calculados por el prototipo son una aproximación local. Para validación definitiva de la RQ2 se requiere la integración con SonarQube (procedimiento en `docs/rq2-sonar-validation-procedure.md`).
- **Sesgo de selección del corpus:** Los métodos del corpus fueron elegidos por contener if-anidados en proyectos OSS. No representan métodos aleatorios del código base — la prevalencia real del patrón en código arbitrario puede ser distinta.
- **Extracción imperfecta:** Los 5 casos con CC=0 muestran que el `CorpusScanRunner` puede generar ficheros que no representan fielmente el método original.
- **Alcance sintáctico:** Java 16+ (pattern matching instanceof) está fuera del alcance del prototipo actual.
- **Corpus pilot reducido (n=8):** Las conclusiones sobre el pilot tienen validez limitada por el tamaño.

---

## 5. Próximos pasos sugeridos

1. **Validación con SonarQube**: Ejecutar el procedimiento de `docs/rq2-sonar-validation-procedure.md` sobre el after-corpus expandido para obtener métricas CC oficiales comparables.
2. **Corrección del extractor**: Investigar por qué los 5 casos Fastjson/POI producen CC=0 y corregir el `CorpusScanRunner` si es posible recuperar el cuerpo completo del método.
3. **Soporte Java 16+**: Ampliar el prototipo para manejar pattern matching instanceof (configurar `LanguageLevel.RAW` en `StaticJavaParser`).
4. **Análisis de Apache Commons**: Los 9 casos inelegibles de Commons son candidatos para extender las precondiciones del prototipo (p. ej., soporte conservador de llamadas a métodos en condición cuando no hay efectos laterales conocidos).
