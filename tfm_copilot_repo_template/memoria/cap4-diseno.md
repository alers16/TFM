# 4.2 Diseño del enfoque

## 4.2.1 Estrategia general

El enfoque propuesto aborda **RQ1** —*¿En qué casos se pueden combinar
sentencias condicionales anidadas y cuál sería el resultado?*— mediante
un prototipo determinista que opera sobre el árbol de sintaxis abstracta
(AST) de Java. La estrategia es deliberadamente **conservadora**: ante
la menor duda sobre la equivalencia semántica de la transformación, el
caso se rechaza. Esta posición está alineada con las restricciones
declaradas en [AGENTS.md](../AGENTS.md) (§ *Foco técnico inicial* y
*Restricciones iniciales*) y con los objetivos del
[Capítulo 3](cap3-descripcion-problema.md).

El diseño descansa en tres principios:

1. **Aplicabilidad explícita.** Toda transformación queda condicionada
   al cumplimiento de un conjunto cerrado de precondiciones formales,
   evaluadas sobre el AST. Si alguna falla, el caso se descarta con un
   motivo trazable.
2. **Preservación del orden de evaluación.** La condición combinada
   respeta el orden original `A && B`; nunca se reordenan operandos ni
   se simplifica el álgebra booleana.
3. **Determinismo y reproducibilidad.** Para una entrada dada, detector
   y transformador producen siempre la misma salida. Es un requisito
   imprescindible para el contraste con LLMs en RQ3.

## 4.2.2 Patrón objetivo de transformación

El patrón canónico que se busca y se transforma es:

```java
// Antes
if (A) {
    if (B) {
        S
    }
}
```

```java
// Después
if (A && B) {
    S
}
```

donde `A` y `B` son expresiones booleanas y `S` es un bloque de
sentencias arbitrario. La transformación es semánticamente equivalente
al original **bajo las precondiciones P1–P5** definidas a continuación,
ya que en ausencia de side effects en `A` y `B` el operador `&&`
preserva la evaluación cortocircuitada y, por tanto, el comportamiento
observable.

## 4.2.3 Precondiciones de aplicabilidad (P1–P5)

El detector evalúa, para cada nodo `IfStmt` del AST, las siguientes
precondiciones, todas necesarias y conjuntamente suficientes para
considerar el caso elegible:

- **P1 — Ausencia de `else` externo.** El `if` externo no tiene rama
  `else` ni `else if`. La presencia de `else` introduce un flujo
  alternativo que la combinación `A && B` no preserva.
- **P2 — Bloque externo unitario.** El bloque `then` del `if` externo
  contiene **exactamente** una sentencia. Si hubiera más, fundir la
  condición externa con la interna alteraría qué sentencias se ejecutan
  cuando se cumple `A` pero no `B`.
- **P3 — Sentencia única que es un `if`.** Esa sentencia única debe ser
  otro `IfStmt` (el `if` interno candidato a fusión). Si no lo es, el
  patrón objetivo no aplica.
- **P4 — Ausencia de `else` interno.** El `if` interno tampoco tiene
  rama `else` ni `else if`, por la misma razón que P1.
- **P5 — Ausencia de side effects en las condiciones.** Ninguna de las
  dos condiciones (`A` o `B`) contiene:
  - llamadas a método (`MethodCallExpr`),
  - asignaciones (`AssignExpr`),
  - operadores de incremento o decremento (`++`, `--`),
  - expresiones lambda (`LambdaExpr`),
  - instanciaciones con `new` (`ObjectCreationExpr`).

  El criterio es deliberadamente **sintáctico y conservador**:
  cualquiera de estas construcciones se considera un riesgo potencial de
  side effect observable, suficiente para descartar el caso aunque en la
  práctica la semántica fuera idéntica. La justificación es que un
  análisis preciso de pureza requeriría un modelo de efectos que excede
  el alcance del prototipo. [PENDIENTE DE CITA] sobre criterios de
  pureza para refactorización en Java.

Estas precondiciones están implementadas en
[`NestedIfDetector`](../src/main/java/es/tfm/refactoring/detection/NestedIfDetector.java)
y materializadas como categorías estables del enumerado
[`DiscardReason`](../src/main/java/es/tfm/refactoring/detection/DiscardReason.java),
que el detector emite cuando rechaza un candidato. El catálogo completo
de motivos de descarte (incluyendo subcategorías de P5 y la categoría
auxiliar `NO_NESTED_IF_PATTERN`) se reproduce en el
[Apéndice D](apendices/D-discard-reasons.md).

El detector ofrece dos modos:

- `detect(method)`: devuelve solo las oportunidades aceptadas, suficiente
  para alimentar al transformador.
- `detectWithReasons(method)`: devuelve también los candidatos
  rechazados junto con sus motivos. Este modo es el que se utiliza en el
  pipeline experimental para alimentar la columna `discardCategories`
  del CSV de resultados y dar respuesta empírica a RQ1.

## 4.2.4 Política de transformación y seguridad semántica

Una vez detectada una oportunidad, el
[`NestedIfTransformer`](../src/main/java/es/tfm/refactoring/transformation/NestedIfTransformer.java)
aplica la transformación en cuatro pasos sobre el AST:

1. **Clonación defensiva.** Las condiciones `A` y `B` se clonan antes de
   componerse, para evitar aliasing entre subárboles del AST.
2. **Parentizado de subexpresiones `||`.** Si `A` o `B` son a su vez
   expresiones binarias con operador `||` (de menor precedencia que
   `&&`), se envuelven en `EnclosedExpr` antes de combinarse. Esto
   evita que la composición `A && B` cambie la semántica original
   (p. ej. `a || c` se convierte en `(a || c)` antes de unirse).
3. **Construcción de la condición combinada.** Se crea un nuevo
   `BinaryExpr` con operador `AND`, manteniendo el orden original
   `outer && inner`.
4. **Sustitución *in place*.** Se reemplaza la condición del `if`
   externo, se asigna como nuevo cuerpo el cuerpo del `if` interno
   (clonado y normalizado a `BlockStmt`) y se invoca `removeElseStmt()`
   de forma defensiva, aunque P1 ya garantiza la ausencia de `else`.

Antes de aplicar la transformación, el transformador **re-verifica** que
ni el `if` externo ni el interno tienen rama `else`. Esta comprobación
es redundante con P1 y P4, pero protege frente a futuros usos del
transformador desde clientes que no hayan pasado por el detector.

## 4.2.5 Política iterativa *one-at-a-time*

A nivel de pipeline, las múltiples oportunidades dentro de un mismo
método se procesan **de una en una**. Tras cada transformación se
re-detecta sobre el AST modificado, hasta no encontrar más oportunidades
o alcanzar un límite de seguridad de 10 pasadas (constante `MAX_PASSES`
en [`BatchRunner`](../src/main/java/es/tfm/refactoring/experiment/BatchRunner.java)).

Esta política se adoptó tras observar que aplicar todas las
oportunidades detectadas en un único pase podía producir sobreconteo
cuando los nodos afectados estaban anidados unos dentro de otros, ya
que el transformador modifica el AST en el sitio. Con la política
*one-at-a-time*:

- el conteo `opportunitiesApplied` corresponde a transformaciones
  realmente efectuadas sobre el AST vigente (no hay nodos huérfanos
  contados dos veces),
- la métrica `Δ` antes/después es exacta para cada caso,
- la trazabilidad por pasada se preserva en el campo `totalPasses` del
  resultado.

## 4.2.6 Resumen del compromiso

El diseño descrito sacrifica cobertura a cambio de garantías. Los casos
con `else`, lambdas, llamadas a método o múltiples sentencias quedan
fuera del alcance del prototipo. Esta restricción es coherente con la
estrategia conservadora declarada en el anteproyecto y permite que el
pipeline determinista actúe como **baseline verificable** frente al cual
contrastar las refactorizaciones generadas por LLMs en RQ3.
