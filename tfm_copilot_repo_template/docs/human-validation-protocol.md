# Protocolo de validación humana del pipeline automático (RQ1/RQ2)

## Motivación
El pipeline se apoya en herramientas de IA y en etapas automáticas
(detección de `if` anidados, comprobación de precondiciones P1–P5 y cálculo
de la complejidad cognitiva). Esta validación obtiene un **umbral de
confianza** de esos resultados: se comprueba manualmente, sobre una muestra
aleatoria, si el veredicto automático es correcto, y se reporta el **% de
acierto por etapa**.

## Muestra
- Tamaño: **N = 30** casos (20 elegibles + 10 no elegibles).
- Selección: muestreo aleatorio **reproducible**, semilla fija `42`, sobre
  el corpus consolidado de 126 casos (`output/rq2-batch/rq2-summary.md`).
- Instrumento: [`output/rq2-human-validation/validation-sample.csv`](../output/rq2-human-validation/validation-sample.csv).
  Cada fila trae el resultado automático (`auto_*`) y columnas vacías
  `humano_*` para el veredicto del revisor.

## Etapas a validar (por caso)
Para cada caso, abrir el fichero fuente en `src/main/resources/*-corpus/`
y contrastar con el resultado automático:

1. **Detección de `if` anidados** (`humano_deteccion_ok`): ¿el detector
   identificó correctamente la presencia o ausencia del patrón `if`-`if`?
2. **Precondiciones P1–P5** (`humano_precondiciones_ok`): ¿la decisión de
   elegibilidad y, si aplica, el motivo de descarte (`DiscardReason`) son
   correctos según las precondiciones?
3. **CC before** (`humano_cc_before_ok`): ¿el valor `auto_cc_before`
   coincide con un cálculo manual (o con SonarQube) sobre el método original?
4. **CC after** (`humano_cc_after_ok`): ídem sobre el método refactorizado.

Marcar cada columna con `1` (correcto) o `0` (incorrecto); usar
`humano_notas` para discrepancias.

## Cálculo del resultado
Para cada etapa: `% acierto = aciertos / N`.

Intervalo de confianza de **Wilson** al 95 % (recomendado para
proporciones con N moderado), con `z = 1,96`:

```
p̂ = aciertos / N
centro = (p̂ + z²/(2N)) / (1 + z²/N)
margen = (z / (1 + z²/N)) · sqrt( p̂(1−p̂)/N + z²/(4N²) )
IC95% = centro ± margen
```

## Reporte
Volcar los resultados en la Tabla "Validación humana del pipeline" de
`memoria/secciones/04e-resultados.tex` (§ Validación humana). Hasta
completarla, la tabla y el texto llevan `\pendienteverif{}`.

## Reproducibilidad
La muestra se regenera de forma determinista con la semilla 42 a partir de
`rq2-summary.md`. Si se reejecuta el lote RQ2 y cambian los casos, debe
regenerarse la muestra y repetirse la validación.
