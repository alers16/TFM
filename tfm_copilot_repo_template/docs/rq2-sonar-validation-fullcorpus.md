# Validación de contraste con SonarQube sobre el corpus completo (RQ2)

Este documento describe cómo ejecutar, en **PowerShell**, la validación de
contraste de la complejidad cognitiva del proxy del prototipo frente a la
implementación oficial de SonarQube, **sobre los 126 casos del corpus** (no
solo el subset de 4). El procedimiento es reproducible y deja artefactos
citables en la memoria.

> **Carácter de la validación.** Es *complementaria*: refuerza la validez de
> constructo de la métrica de RQ2 contrastándola con la herramienta oficial.
> No sustituye al cálculo del prototipo ni a los resultados de
> `output/rq2-batch/`.

## 1. Requisitos previos

| Requisito | Comprobación |
|---|---|
| Docker Desktop en ejecución | `docker version` |
| JDK 17 + Maven | `java -version`, `mvn -version` |
| Puertos 9000 libres | — |

> El análisis se realiza con el goal **`mvn sonar:sonar`** (plugin
> `sonar-maven-plugin`); **no se necesita el CLI `sonar-scanner`**.

## 2. Arrancar SonarQube y obtener token

```powershell
# Desde la raíz del repositorio
docker compose -f docker-compose.sonarqube.yml up -d

# Esperar a que esté operativo (status = UP)
do { Start-Sleep 5; $s = (Invoke-RestMethod http://localhost:9000/api/system/status).status; $s }
while ($s -ne 'UP')
```

En <http://localhost:9000> (credenciales iniciales `admin`/`admin`):
*My Account → Security → Generate Tokens* → tipo *User Token*. Copiar y exportar:

```powershell
$env:SONAR_TOKEN = "sqa_xxxxxxxxxxxxxxxx"
```

## 3. Ejecutar la validación de todo el corpus

```powershell
./sonar-fullcorpus-validation.ps1
```

El script (`sonar-fullcorpus-validation.ps1`):

1. Lee `output/rq2-batch/rq2-all-results.json` y materializa, por cada caso, el
   método `before` y el `after` (campos `sourceBefore`/`sourceAfter`) envueltos
   en una clase mínima nombrada por `caseId`.
2. Analiza dos proyectos SonarQube separados (`tfm-rq2-fullcorpus-before` y
   `-after`) ejecutando, para cada uno,
   `mvn -DskipTests compile sonar:sonar` con `sonar.sources` apuntando a la
   carpeta materializada y `sonar.java.binaries=target/classes`. El propio goal
   compila el proyecto, de modo que **no hace falta un paso de compilación
   aparte** ni el CLI `sonar-scanner`.
3. Recupera la métrica oficial `cognitive_complexity` por fichero vía la API
   `api/measures/component_tree`.
4. Cruza la CC oficial con la del proxy (`complexityBefore`/`After`) por caso.

Parámetros útiles: `-SonarHostUrl`, `-SonarToken`, `-MavenCmd` (por defecto
`mvn`), `-JavaBinaries`, `-SkipScan` (rehacer solo la comparación a partir de
los JSON ya descargados).

## 4. Artefactos generados (citables en la memoria)

Se escriben en `output/rq2-sonar-validation/fullcorpus/`:

| Fichero | Contenido |
|---|---|
| `before/`, `after/` | Fuentes Java materializadas (una clase por caso) |
| `sonar-before.json`, `sonar-after.json` | Respuesta cruda de la API (CC por fichero) |
| `comparison.csv` | Una fila por caso: proxy vs SonarQube, `matchBefore/After`, diferencias |
| `comparison.md` | Informe: tasa de coincidencia y tabla de divergencias |
| `metadata.json` | Versión de SonarQube, *timestamps*, operador, host, recuentos |

El `metadata.json` registra **versión, fecha y operador** —los campos que
quedaron sin cumplimentar en la validación previa del subset—, de modo que la
corrida queda trazable.

## 5. Cómo incorporarlo a la memoria

Tras una ejecución real:

1. Abrir `comparison.md` y tomar la tasa de coincidencia (*before*/*after*) y
   las divergencias.
2. En `memoria/secciones/04e-resultados.tex`, sección *Validación de contraste
   con SonarQube*, sustituir el bloque `\pendienteverif{}` por los valores
   observados, citando `output/rq2-sonar-validation/fullcorpus/comparison.md` y
   `metadata.json` como fuente.
3. Registrar cualquier divergencia ≥ 1 punto en el capítulo de amenazas a la
   validez, indicando la regla del modelo SonarSource implicada (p. ej.
   ternario, recursión o lambdas, no cubiertas por el proxy).

## 6. Limitaciones declaradas

- La CC es **sintáctica**: SonarQube la calcula sobre el AST aunque no resuelva
  todos los símbolos, por lo que envolver el método en una clase mínima es
  válido para el contraste. Un caso que no parsee aparecerá como *sin medida*
  en `comparison.md` (recuento `casesMissing`).
- El resultado depende de la **versión del motor** de SonarQube en el momento
  del análisis (registrada en `metadata.json`).
- El proxy no implementa todas las reglas del modelo (ternario, recursión,
  lambdas/clases anónimas como anidamiento); las divergencias se concentrarán,
  previsiblemente, en casos que usen esas construcciones.

## 7. Limpieza

```powershell
docker compose -f docker-compose.sonarqube.yml down       # conserva volúmenes
docker compose -f docker-compose.sonarqube.yml down -v    # borra también datos
```
