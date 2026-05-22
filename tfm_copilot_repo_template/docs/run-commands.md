# Guía de re-ejecución del experimento

Este documento lista los comandos exactos para reproducir todos los artefactos del TFM desde cero, en orden.

---

## 0. Requisitos previos

```powershell
# Java 17+
java -version

# Maven 3.8+
mvn -version

# Variable de entorno para RQ3 (solo necesaria si se re-ejecuta la campaña live)
$env:OPENAI_API_KEY = "sk-..."
```

---

## 1. Compilar y verificar

```powershell
cd "c:\Users\Ale Roman\Downloads\tfm_copilot_repo_template\tfm_copilot_repo_template"

# Compilar
mvn clean compile -q

# Ejecutar todos los tests (415 tests deben pasar)
mvn test -q

# Generar classpath para ejecuciones manuales
mvn dependency:build-classpath -Dmdep.outputFile=target/classpath.txt -q
$cp = Get-Content target\classpath.txt
```

---

## 2. RQ2 — Impacto en complejidad cognitiva

### 2a. Pipeline completo RQ2 (piloto + real, modo STRICT)

Genera `output/rq2-batch/rq2-summary.md` con 41 casos (8 pilot + 33 real), Δ=-70.

```powershell
java -cp "target\classes;$cp" es.tfm.refactoring.experiment.Rq2BatchExecutor
```

### 2b. Corpus del tutor

Genera `output/rq2-tutor/rq2-tutor-summary.md` con 8 casos del paper de referencia.

```powershell
java -cp "target\classes;$cp" es.tfm.refactoring.experiment.Rq2TutorBatchExecutor
```

### 2c. Comparativa STRICT vs RELAXED

Genera `output/rq2-comparison/rq2-comparison-summary.md` (STRICT P1–P5 vs RELAXED P5' con allowlist).

```powershell
java -cp "target\classes;$cp" es.tfm.refactoring.experiment.Rq2ComparisonExecutor
```

---

## 3. Escáner masivo de corpus (ProjectCorpusScanner)

Escanea proyectos OSS clonados en `tmp-projects/` y detecta nuevos candidatos elegibles.

### 3a. Clonar proyectos corpus real (si no están en tmp-projects/)

```powershell
cd tmp-projects

git clone --depth 1 --branch rel/commons-lang-3.14.0 https://github.com/apache/commons-lang.git commons-lang
git clone --depth 1 --branch rel/commons-io-2.15.1    https://github.com/apache/commons-io.git commons-io
git clone --depth 1 --branch MATH_3_6_1               https://github.com/apache/commons-math.git commons-math
git clone --depth 1 --branch rel/1.10.14               https://github.com/apache/ant.git ant
git clone --depth 1 --branch jmetal-5.11               https://github.com/jMetal/jMetal.git jmetal
git clone --depth 1 --branch v2.11.2                   https://github.com/Konloch/bytecode-viewer.git bytecode-viewer
git clone --depth 1 --branch rel/commons-compress-1.26.0 https://github.com/apache/commons-compress.git commons-compress

cd ..
```

### 3b. Ejecutar el escáner (corpus real)

Genera `output/corpus-scan/corpus-scan-results.csv` y `corpus-scan-report.md`.

```powershell
$base = "$PWD\tmp-projects"
java -cp "target\classes;$cp" es.tfm.refactoring.scanner.CorpusScanRunner `
    "--project" "commons-lang=$base\commons-lang\src\main\java" `
    "--project" "commons-io=$base\commons-io\src\main\java" `
    "--project" "commons-math=$base\commons-math\src\main\java" `
    "--project" "ant=$base\ant\src\main" `
    "--project" "jmetal=$base\jmetal\jmetal-problem\src\main\java" `
    "--project" "bytecode-viewer=$base\bytecode-viewer\src\main\java" `
    "--project" "commons-compress=$base\commons-compress\src\main\java"
```

### 3b-bis. Ejecutar el escáner (10 proyectos del paper del tutor)

Escanea los 10 proyectos OSS de Saborido et al. 2022. Los proyectos deben estar clonados en
`tmp-tutor-projects/` (ver 3a-bis). Produce 35 candidatos elegibles en los proyectos con código Java.
Nota: `fiware-commons`, `jedis` y `aioteslake` devuelven 0 casos elegibles.

```powershell
$base = "$PWD\tmp-tutor-projects"
java -cp "target\classes;$cp" es.tfm.refactoring.scanner.CorpusScanRunner `
    "--project" "cybercaptor=$base\cybercaptor-server\src\main\java" `
    "--project" "moea=$base\MOEAFramework\src" `
    "--project" "iotbroker=$base\iotbroker\src\main\java" `
    "--project" "knowage=$base\knowage-server\knowage-core\src\main\java" `
    "--project" "fastjson=$base\fastjson\src\main\java" `
    "--project" "fiware-commons=$base\fiware-commons\src\main\java" `
    "--project" "jedis=$base\jedis\src\main\java" `
    "--project" "aioteslake=$base\aioteslake\src\main\java"
```

### 3a-bis. Clonar proyectos del paper del tutor (si no están en tmp-tutor-projects/)

Estos son los 10 proyectos evaluados en Saborido et al. 2022 (IEEE Access).

```powershell
mkdir tmp-tutor-projects -Force; cd tmp-tutor-projects

git clone --depth 1 https://github.com/fiware-cybercaptor/cybercaptor-server.git cybercaptor-server
git clone --depth 1 https://github.com/MOEAFramework/MOEAFramework.git MOEAFramework
git clone --depth 1 https://github.com/mobius-software-ltd/iotbroker.cloud-java-client.git iotbroker
git clone --depth 1 https://github.com/KnowageLabs/Knowage-Server.git knowage-server
git clone --depth 1 https://github.com/alibaba/fastjson.git fastjson
git clone --depth 1 https://github.com/telefonicaid/fiware-commons.git fiware-commons
git clone --depth 1 https://github.com/redis/jedis.git jedis
git clone --depth 1 https://github.com/AIoTES/DataLayer-AiotesLake.git aioteslake
# jMetal y bytecode-viewer ya están en src/main/resources/tutor-corpus como recursos

cd ..
```

### 3c. Generar ficheros corpus desde el CSV

Extrae los métodos elegibles y genera ficheros `.java` con metadatos para revisión.
**Importante**: el escáner detecta candidatos, pero el experimento (`Rq2TutorBatchExecutor`)
only procesa los ficheros registrados en `TutorCorpusLoader`. La ruta entre ambos es la
curación manual: revisar los ficheros generados y mover los interesantes a
`src/main/resources/tutor-corpus/`, registrándolos en `TutorCorpusLoader.standardTutorFiles()`.

#### Corpus real (tmp-projects/)

```powershell
java -cp "target\classes;$cp" es.tfm.refactoring.scanner.CorpusFileGenerator `
    "output\corpus-scan\corpus-scan-results.csv" `
    "$PWD\tmp-projects" `
    "output\corpus-scan\generated-corpus"
```

#### Proyectos del paper del tutor (tmp-tutor-projects/)

Nota: el CSV usa nombres cortos (`cybercaptor`, `moea`, `knowage`) que no coinciden con
las carpetas clonadas (`cybercaptor-server`, `MOEAFramework`, `knowage-server`). El
generador encontrará automáticamente `fastjson` e `iotbroker` (nombres idénticos); para
los demás, la resolución de rutas funciona igualmente por búsqueda recursiva.

```powershell
java -cp "target\classes;$cp" es.tfm.refactoring.scanner.CorpusFileGenerator `
    "output\corpus-scan\corpus-scan-results.csv" `
    "$PWD\tmp-tutor-projects" `
    "output\corpus-scan\generated-tutor-corpus"
```

De los 35 elegibles encontrados, 4 ya están en `src/main/resources/tutor-corpus/`
(cybercaptor, MOEAFramework, iotbroker, knowage). Los 31 restantes son mayoritariamente
métodos de **fastjson** con δ pequeño (−1 a −5), añadibles según necesidad del TFM.

---

## 4. RQ3 — Campaña LLM

> **Requiere `OPENAI_API_KEY` configurada en el entorno.**

### 4a. Campaña principal (gpt-4o vs gpt-4.1, 36 invocaciones)

```powershell
$env:OPENAI_API_KEY = "sk-..."   # solo si no está ya configurada

java -cp "target\classes;$cp" es.tfm.refactoring.llm.CampaignExecutor `
    output/rq3-campaign-real-phase9
```

Genera artefactos en `output/rq3-campaign-real-phase9/`:
- `rq3-aggregated.md` — resultados por modelo y caso
- `rq3-full-evidence.json` — evidencia reproducible invocación a invocación
- `rq3-summary.csv` — tabla compacta
- `rq3-run-metadata.json` — metadatos del protocolo
- `rq3-incidents.md` — incidencias técnicas (si las hay)

### 4b. Campaña trampa (falsos positivos, 18 invocaciones)

```powershell
java -cp "target\classes;$cp" es.tfm.refactoring.llm.TrapCampaignExecutor `
    output/rq3-trap-campaign
```

### 4c. Comparativa prompt v2.0 few-shot (36 invocaciones)

```powershell
java -cp "target\classes;$cp" es.tfm.refactoring.llm.PromptV2CampaignExecutor `
    output/rq3-promptv2-campaign
```

### 4d. Modo dry run (sin API, respuestas pre-grabadas)

Para validar infraestructura sin consumir créditos:

```powershell
java -cp "target\classes;$cp" es.tfm.refactoring.llm.CampaignExecutor `
    --mode=dry-run output/rq3-campaign-dry
```

---

## 5. Orden completo de re-ejecución

```
1.  mvn clean compile -q
2.  mvn test -q                          # verificar 415 tests OK
3.  mvn dependency:build-classpath ...   # generar classpath
4.  Rq2BatchExecutor                     # RQ2 piloto + real
5.  Rq2TutorBatchExecutor                # RQ2 tutor
6.  Rq2ComparisonExecutor                # STRICT vs RELAXED
7.  CorpusScanRunner   (si hay nuevos proyectos que escanear)
8.  CorpusFileGenerator (si se añaden casos al corpus)
9.  CampaignExecutor                     # RQ3 campaña principal
10. TrapCampaignExecutor                 # RQ3 trampa
11. PromptV2CampaignExecutor             # RQ3 prompt v2.0
```

Los pasos 7–8 son opcionales si el corpus no cambia. Los pasos 9–11 requieren `OPENAI_API_KEY`.

---

## 6. Artefactos de salida

| Ruta | Contenido |
|------|-----------|
| `output/rq2-batch/rq2-summary.md` | RQ2: 41 casos, 28 elegibles, Δ=-70 |
| `output/rq2-tutor/rq2-tutor-summary.md` | RQ2 tutor: 12 casos, 5 elegibles, Δ=-24 |
| `output/rq2-comparison/rq2-comparison-summary.md` | STRICT vs RELAXED, 3 corpus |
| `output/corpus-scan/corpus-scan-report.md` | Escáner: 24.987 candidatos, 15 elegibles |
| `output/rq3-campaign-real-phase9/rq3-aggregated.md` | RQ3 fase 9: gpt-4o 100%, gpt-4.1 83.3% |
| `output/rq3-trap-campaign/rq3-trap-aggregated.md` | Falsos positivos: 0% (18/18 SUCCESS) |
| `output/rq3-promptv2-campaign/rq3-promptv2-aggregated.md` | Prompt v2.0: mismos resultados que v1.0 |
