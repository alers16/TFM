# TFM — Refactorización de condicionales anidados

Trabajo de Fin de Máster sobre refactorización de sentencias condicionales para reducir la complejidad cognitiva de código Java.

## Preguntas de investigación

- **RQ1.** ¿En qué casos se pueden combinar sentencias condicionales anidadas y cuál sería el resultado?
- **RQ2.** ¿Qué impacto tienen estas refactorizaciones en la complejidad cognitiva?
- **RQ3.** ¿Pueden los grandes modelos de lenguaje realizar esta refactorización de forma correcta y automática?

## Estructura del repositorio

```
├── src/main/java/es/tfm/refactoring/
│   ├── Main.java                                  # Punto de entrada
│   ├── detection/                                 # Detector de oportunidades (P1–P5)
│   │   ├── NestedIfDetector.java                  # Detección con razones de descarte
│   │   ├── DetectionMode.java                     # STRICT / RELAXED
│   │   ├── DiscardReason.java                     # Enum de motivos de descarte
│   │   └── MethodCallAllowlist.java               # Métodos permitidos en modo RELAXED
│   ├── transformation/                            # Transformador AST
│   │   └── NestedIfTransformer.java
│   ├── analysis/                                  # Métricas de complejidad
│   │   └── CognitiveComplexityCalculator.java     # Proxy de SonarQube CC
│   ├── experiment/                                # Pipeline experimental RQ2
│   │   ├── BatchRunner.java
│   │   ├── ResultExporter.java
│   │   ├── Rq2BatchExecutor.java                  # Ejecución corpus piloto + real
│   │   ├── Rq2TutorBatchExecutor.java             # Ejecución corpus tutor
│   │   ├── Rq2ComparisonExecutor.java             # Comparativa STRICT vs RELAXED
│   │   ├── RealDatasetLoader.java                 # Carga 33 casos real-corpus
│   │   ├── PilotCorpusLoader.java                 # Carga 8 casos pilot-corpus
│   │   └── TutorCorpusLoader.java                 # Carga 8 casos tutor-corpus
│   ├── scanner/                                   # Escáner masivo de proyectos OSS
│   │   ├── ProjectCorpusScanner.java              # Escanea un proyecto completo
│   │   ├── ScanFinding.java                       # Resultado por candidato
│   │   ├── CorpusScanRunner.java                  # CLI: escanea N proyectos
│   │   └── CorpusFileGenerator.java               # Genera ficheros corpus desde CSV
│   └── llm/                                       # Protocolo experimental RQ3
│       ├── LlmPromptBuilder.java                  # Prompt v1.0 y v2.0
│       ├── LlmResponseValidator.java              # Oráculo v1.1
│       ├── CampaignExecutor.java                  # CLI: ejecuta campaña RQ3
│       └── ...
├── src/main/resources/
│   ├── pilot-corpus/    (8 casos)
│   ├── real-corpus/     (33 casos — 20 originales + 13 del escáner masivo)
│   ├── tutor-corpus/    (8 casos)
│   └── trap-corpus/     (3 casos trampa para validar falsos positivos)
├── src/test/java/es/tfm/refactoring/      # 415 tests
├── output/                                 # Artefactos generados
│   ├── rq2-batch/                          # Resultados RQ2 (41 casos, Δ=-70)
│   ├── rq2-comparison/                     # STRICT vs RELAXED (49 casos)
│   ├── rq2-tutor/                          # Corpus tutor (8 casos)
│   ├── corpus-scan/                        # Escáner masivo (24.987 candidatos)
│   └── rq3-campaign-real-phase9/           # RQ3 live (36 invocaciones)
├── tmp-projects/                           # Proyectos OSS clonados para el escáner
├── memoria/                                # Memoria del TFM
├── docs/                                   # Documentación del protocolo y resultados
└── pom.xml
```

## Requisitos

- Java 17+
- Maven 3.8+

## Compilación y tests

```bash
mvn clean compile
mvn test
```

## Configuración de credenciales para RQ3

Desde la fase 9.6, la campaña formal de RQ3 compara **dos modelos de OpenAI**
(`gpt-4o` y `gpt-4.1`). Por tanto, **solo se requiere `OPENAI_API_KEY`**.
Las credenciales se leen exclusivamente desde variables de entorno.

> **Importante:** Java usa `System.getenv(...)` y **no carga `.env` automáticamente**. Tener `.env` en la raíz no basta si no exportas esas variables o no configuras integración explícita en el IDE.

### Configuración rápida

1. Copia la plantilla:
   ```bash
   cp .env.example .env
   ```
2. Edita `.env` con tu clave real:
   ```
   OPENAI_API_KEY=sk-...
   ```
3. Carga las variables antes de ejecutar:
    - **Linux/Mac:**
       ```bash
       set -a
       source .env
       set +a
       ```
    - **PowerShell (sesión actual):**
       ```powershell
       Get-Content .env | ForEach-Object {
          if ($_ -match '^\s*([^#][^=]*)=(.*)$') {
             [System.Environment]::SetEnvironmentVariable($Matches[1].Trim(), $Matches[2].Trim())
          }
       }
       ```
    - **Verificación mínima (PowerShell):**
       ```powershell
       Write-Output ("OPENAI_API_KEY configured: {0}" -f (-not [string]::IsNullOrWhiteSpace($env:OPENAI_API_KEY)))
       ```

### Configuración en IntelliJ IDEA

1. **Run → Edit Configurations** → selecciona la configuración de ejecución.
2. En **Environment variables**, añade:
   ```
   OPENAI_API_KEY=sk-...;OPENAI_MODEL=gpt-4o;OPENAI_SECONDARY_MODEL=gpt-4.1
   ```
3. Alternativa: instala el plugin **EnvFile** y apunta a `.env` para inyectar variables al proceso Java.
4. Verifica que la configuración elegida en IntelliJ sea la misma desde la que ejecutas `CampaignExecutor`.

### Variables disponibles

| Variable | Requerida | Default | Descripción |
|----------|-----------|---------|-------------|
| `OPENAI_API_KEY` | Sí | — | Clave de API de OpenAI (única credencial necesaria) |
| `OPENAI_MODEL` | No | `gpt-4o` | Modelo OpenAI primario |
| `OPENAI_SECONDARY_MODEL` | No | `gpt-4.1` | Modelo OpenAI secundario para la comparativa formal |

> **Nota:** A partir de la fase 9.6 la campaña formal compara `gpt-4o` vs
> `gpt-4.1`. La dependencia de `ANTHROPIC_API_KEY` ha sido eliminada.

> **Importante:** El archivo `.env` está excluido en `.gitignore`. Nunca subas claves reales al repositorio.

## Ejecución de campaña RQ3

La campaña formal de RQ3 se ejecuta con el protocolo congelado por defecto:

- Modelos: `gpt-4o`, `gpt-4.1` (dos modelos OpenAI)
- Temperatura: `0.0`
- Intentos por caso: `3`
- Prompt: `v1.0`
- Max tokens: `2048`
- Subset: `6` casos (`36` invocaciones esperadas)
- Credencial requerida: solo `OPENAI_API_KEY`

Comando de ejecución (provider real por defecto):

```bash
mvn -DskipTests package dependency:build-classpath -Dmdep.outputFile=target/classpath.txt
java -cp "target/classes;$(cat target/classpath.txt)" es.tfm.refactoring.llm.CampaignExecutor output/rq3-campaign-real-phase9
```

Modo alternativo solo para dry run controlado:

```bash
java -cp "target/classes;$(cat target/classpath.txt)" es.tfm.refactoring.llm.CampaignExecutor --mode=dry-run output/rq3-campaign-dry
```

Artefactos generados en el directorio de salida:

- `rq3-full-evidence.json`
- `rq3-summary.csv`
- `rq3-aggregated.md`
- `rq3-run-metadata.json`
- `rq3-incidents.md`

### Ejecución exploratoria OpenAI-only (Phase 9.5)

Modo auxiliar previsto para situaciones en las que solo se dispone de
`OPENAI_API_KEY`. **No sustituye** la comparativa formal de dos modelos
de RQ3: se ejecuta en paralelo con su propio directorio y prefijo de
artefactos.

- Solo requiere `OPENAI_API_KEY` (no exige `ANTHROPIC_API_KEY`).
- Mantiene **congelados** los mismos parámetros del protocolo formal:
  temperatura `0.0`, `3` intentos por caso, prompt `v1.0`, max tokens
  `2048`, subset de `6` casos. Cambia únicamente la lista de modelos
  a `gpt-4o` (1 modelo × 6 casos × 3 intentos = `18` invocaciones).
- Los artefactos se identifican como `campaignType = exploratory-openai-only`
  en `rq3-openai-only-run-metadata.json`.

Comando de ejecución:

```bash
java -cp "target/classes;$(cat target/classpath.txt)" es.tfm.refactoring.llm.CampaignExecutor --mode=openai-only output/rq3-openai-only
```

Artefactos generados (prefijo distinto para no pisar la campaña formal):

- `rq3-openai-only-full-evidence.json`
- `rq3-openai-only-summary.csv`
- `rq3-openai-only-aggregated.md`
- `rq3-openai-only-run-metadata.json`
- `rq3-openai-only-incidents.md`

## Estado actual

Fase 0 completada: andamiaje del proyecto. Esqueletos de clases y tests creados, pendientes de implementación.
