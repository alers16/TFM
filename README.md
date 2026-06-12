# Refactorización de condicionales anidados para reducir la complejidad cognitiva en Java

Trabajo Fin de Máster (Máster Universitario en Ingeniería del Software e
Inteligencia Artificial, Universidad de Málaga).

Este repositorio contiene un **prototipo determinista** que detecta y combina de
forma segura sentencias condicionales anidadas en Java
(`if (A) { if (B) { S } }` → `if (A && B) { S }`) para reducir su complejidad
cognitiva, junto con el **paquete de replicación** completo de los experimentos
del estudio y las fuentes de la memoria.

## Preguntas de investigación

- **RQ1.** ¿En qué casos se pueden combinar sentencias condicionales anidadas y cuál sería el resultado?
- **RQ2.** ¿Qué impacto tienen estas refactorizaciones en la complejidad cognitiva?
- **RQ3.** ¿Pueden los grandes modelos de lenguaje realizar esta refactorización de forma correcta y automática?

## Resultados principales

Sobre un corpus de **126 métodos** Java (8 sintéticos y 118 de proyectos de
código abierto):

- **RQ1.** El detector declara **111 casos elegibles** y 15 no elegibles. El
  único obstáculo a la combinación es estructural (un `else` o sentencias
  adicionales), no lo que ocurra dentro de las condiciones.
- **RQ2.** La combinación reduce la complejidad cognitiva en **Δ = −340** puntos
  (todos los casos elegibles mejoran), con **coincidencia exacta del 100 %**
  frente a SonarQube sobre los 122 casos medibles.
- **RQ3.** Los modelos saben aplicar la transformación, pero fallan al decidir
  *cuándo* hacerlo: `gpt-4.1` acierta el 77,4 % de las invocaciones con respuesta
  y `gpt-4o` el 62,5 %. Ninguno iguala la garantía del prototipo determinista.

La combinación es segura bajo **cuatro precondiciones estructurales (P1–P4)**; no
se requiere análisis de pureza, porque el cortocircuito de `&&`
(`A && B ≡ A ? B : false`, JLS §15.24) preserva el orden y la condicionalidad de
evaluación con independencia de los efectos colaterales.

## Estructura del repositorio

```
.
├── src/
│   ├── main/java/es/tfm/refactoring/
│   │   ├── detection/        # Detector de oportunidades (precondiciones P1–P4)
│   │   ├── transformation/   # Transformador sobre el AST de JavaParser
│   │   ├── analysis/         # Complejidad cognitiva (proxy del modelo de SonarSource)
│   │   ├── experiment/       # Pipeline determinista de RQ1/RQ2 (BatchRunner, ejecutores)
│   │   ├── llm/              # Subsistema de RQ3: prompt, proveedor, oráculo y campaña
│   │   ├── scanner/          # Escaneo masivo de proyectos para construir el corpus
│   │   └── ExperimentOrchestrator.java   # Orquestador de todo el experimento
│   ├── main/resources/
│   │   ├── pilot-corpus/     # 8 casos sintéticos (canónicos y rechazos típicos)
│   │   └── real-corpus/      # 118 casos de proyectos de código abierto
│   └── test/java/            # Pruebas unitarias (incl. validación del proxy vs SonarSource)
├── output/                   # Artefactos reproducibles del estudio
│   ├── rq2-structural/            # Resultados de RQ2 (modo STRUCTURAL, P1–P4)
│   ├── rq3-structural/            # Veredictos de RQ3 reportados
│   ├── rq3-campaign-real-phase9/  # Evidencia cruda de la campaña live de RQ3
│   ├── rq2-sonar-validation/      # Contraste del proxy con SonarQube
│   └── legacy/                    # Salidas exploratorias, NO usadas en los resultados finales
├── tmp-projects/             # Proyectos OSS clonados para el escáner (entrada del corpus)
├── tmp-tutor-projects/       # Proyectos del antecedente (Saborido et al.) para el escáner
├── memoria/                  # Fuentes LaTeX de la memoria (compilar con LuaLaTeX)
├── docs/                     # Documentación de protocolos y procedimientos
│   └── material-tfm/         # Material de referencia (anteproyecto, guía, comentarios…)
├── sonar-fullcorpus-validation.ps1   # Validación con SonarQube (apéndice F de la memoria)
├── scan-open-source-projects.ps1     # Clona proyectos OSS y lanza el escáner
├── run-all.ps1               # Pipeline completo de un tirón (compila, prueba, RQ2/escáner/RQ3)
├── cc-analyze.ps1            # Complejidad cognitiva de un fichero Java (ad-hoc)
├── docker-compose.sonarqube.yml      # Servidor SonarQube local para la validación
└── pom.xml
```

## Requisitos

- **Java 17+** y **Maven 3.8+**.
- Para construir el corpus desde cero (escáner): **git** y los proyectos fuente.
- Para la validación con SonarQube: **Docker** (servidor local) y el puerto 9000 libre.
- Para ejecutar RQ3 en modo *live*: una clave de la API de OpenAI en la variable
  de entorno `OPENAI_API_KEY`.

Las versiones de las dependencias están fijadas en `pom.xml`
(JavaParser 3.26.4, JUnit 5.11.4, Gson 2.11.0).

## Compilación y pruebas

```bash
mvn clean compile
mvn test
```

La batería de pruebas incluye la validación del cálculo de complejidad cognitiva
frente a los casos oficiales del modelo de SonarSource.

## Credenciales para RQ3

Las credenciales se leen exclusivamente de variables de entorno (Java no carga
`.env` automáticamente). La única obligatoria es `OPENAI_API_KEY`. En PowerShell,
para cargarla desde un fichero `.env`:

```powershell
Get-Content .env | ForEach-Object {
  if ($_ -match '^\s*([^#][^=]*)=(.*)$') {
    [Environment]::SetEnvironmentVariable($Matches[1].Trim(), $Matches[2].Trim())
  }
}
```

> El archivo `.env` está excluido en `.gitignore`. Nunca subas claves reales.

---

## Reproducción de los resultados reportados

Los resultados de la memoria se generan con los **ejecutores en modo
`STRUCTURAL`** (precondiciones P1–P4). Los comandos se muestran para PowerShell;
primero se genera el *classpath de ejecución* (clases compiladas + dependencias,
necesario para lanzar la JVM; no tiene relación con el origen del corpus):

```powershell
mvn -DskipTests compile dependency:build-classpath "-Dmdep.outputFile=target/classpath.txt"
$cp = Get-Content target/classpath.txt
```

### RQ1 / RQ2 — lote determinista

```powershell
java -cp "target/classes;$cp" es.tfm.refactoring.experiment.Rq2StructuralBatchExecutor output/rq2-structural
```

Un segundo argumento opcional indica un **directorio de corpus** (con subcarpetas
`pilot-corpus/` y `real-corpus/`) que sustituye al empaquetado por defecto, de
modo que pueden lanzarse experimentos sobre otro corpus sin recompilar:

```powershell
java -cp "target/classes;$cp" es.tfm.refactoring.experiment.Rq2StructuralBatchExecutor output/rq2-structural ruta/al/corpus
```

### RQ3 — campaña con modelos de lenguaje

La campaña *live* invoca la API (modelos `gpt-4o` y `gpt-4.1`, temperatura `0.0`,
3 intentos por caso, *prompt* `v1.0`, máximo 2048 *tokens*; 126 × 2 × 3 = 756
invocaciones) y graba la evidencia cruda. **Requiere `OPENAI_API_KEY`.**

```powershell
java -cp "target/classes;$cp" es.tfm.refactoring.llm.CampaignExecutor output/rq3-campaign-real-phase9
```

Para una prueba sin coste de API, el modo *dry-run* reproduce respuestas grabadas
en lugar de llamar a la API:

```powershell
java -cp "target/classes;$cp" es.tfm.refactoring.llm.CampaignExecutor --mode=dry-run output/rq3-campaign-dry
```

Los **veredictos reportados** se obtienen reevaluando esa evidencia con el oráculo
y el baseline en modo `STRUCTURAL` (no llama a la API; reutiliza las respuestas ya
grabadas):

```powershell
java -cp "target/classes;$cp" es.tfm.refactoring.llm.Rq3StructuralReevalExecutor
```

Por defecto lee `output/rq3-campaign-real-phase9/rq3-full-evidence.json` y escribe
en `output/rq3-structural/`.

### Validación con SonarQube

```powershell
docker compose -f docker-compose.sonarqube.yml up -d
./sonar-fullcorpus-validation.ps1
```

El procedimiento completo (token, ejecución y artefactos) se documenta en el
apéndice F de la memoria.

---

## Construcción del corpus (escáner masivo)

El corpus real se obtiene escaneando proyectos Java de código abierto en busca
del patrón objetivo. El proceso tiene tres pasos.

**1. Clonar los proyectos y escanear.** El script clona los proyectos en
`tmp-projects/` y lanza el escáner:

```powershell
./scan-open-source-projects.ps1            # clona y escanea
./scan-open-source-projects.ps1 -SkipClone # si ya están clonados
```

Como alternativa, el escáner se invoca directamente sobre directorios ya
disponibles (admite `--root <dir>`, `--project nombre=<dir>` repetible, o
`--dir <dir>`):

```powershell
java -cp "target/classes;$cp" es.tfm.refactoring.scanner.CorpusScanRunner `
    --project commons-lang=tmp-projects/commons-lang/src/main/java `
    --project ant=tmp-projects/ant/src/main
```

Produce `output/corpus-scan/corpus-scan-results.csv` (un registro por candidato)
y `corpus-scan-report.md` (resumen por proyecto).

**2. Generar los ficheros de corpus** a partir del CSV. Por cada caso elegible
extrae el método y lo envuelve en una clase con sus metadatos
(`@project`, `@file`, `@method`, `@license`, `@sonarCCBefore`):

```powershell
java -cp "target/classes;$cp" es.tfm.refactoring.scanner.CorpusFileGenerator `
    output/corpus-scan/corpus-scan-results.csv `
    tmp-projects tmp-tutor-projects `
    output/corpus-scan/generated-corpus
```

**3. Revisar e incorporar.** Los ficheros generados deben revisarse (que la
envoltura compile, que los metadatos sean correctos y que el CC estimado coincida
con SonarQube) antes de moverse a `src/main/resources/real-corpus/`.

---

## Pipeline completo (orquestador)

`ExperimentOrchestrator` encadena el pipeline reportado (RQ2 estructural → RQ3:
campaña + reevaluación estructural) con un único comando:

```powershell
java -cp "target/classes;$cp" es.tfm.refactoring.ExperimentOrchestrator --all
java -cp "target/classes;$cp" es.tfm.refactoring.ExperimentOrchestrator --rq2
java -cp "target/classes;$cp" es.tfm.refactoring.ExperimentOrchestrator --rq3 --dry-run
```

`run-all.ps1` es un envoltorio que compila, ejecuta las pruebas y lanza el
pipeline completo (requiere `OPENAI_API_KEY`).

> **Nota.** El paso principal de RQ2 del orquestador se ejecuta en modo
> `STRUCTURAL` (escribe en `output/rq2-structural/`) y, tras la campaña de RQ3,
> lanza la reevaluación estructural (`output/rq3-structural/`): es decir,
> **reproduce los artefactos reportados** en la memoria. La construcción del
> corpus (escáner) y las variantes exploratorias (corpus del tutor, comparativa
> STRICT/RELAXED, campaña trampa, *prompt* v2.0) quedan fuera del orquestador y se
> ejecutan aparte, con sus propias clases. 

---

## Utilidad: complejidad cognitiva de un fichero

Para calcular la CC de los métodos de un fichero Java concreto:

```powershell
./cc-analyze.ps1 ruta/al/Fichero.java
# equivalente:
java -cp "target/classes;$cp" es.tfm.refactoring.analysis.CcAnalyzeCli ruta/al/Fichero.java
```

## Artefactos reproducibles (`output/`)

| Directorio | Contenido |
|---|---|
| `rq2-structural/` | Resultados por caso de RQ2 (JSON/CSV), resumen y metadatos. |
| `rq3-structural/` | Veredictos del oráculo de RQ3 reportados (agregado y por invocación). |
| `rq3-campaign-real-phase9/` | Evidencia cruda de la campaña *live* (una entrada por invocación). |
| `rq2-sonar-validation/fullcorpus/` | Contraste de la complejidad oficial de SonarQube con el proxy. |


## Autoría

Trabajo Fin de Máster de Alejandro Román Sánchez (Universidad de Málaga).
Tutores: Francisco Chicano y Rubén Saborido.
