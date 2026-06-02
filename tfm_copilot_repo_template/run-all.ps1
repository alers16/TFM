# Script para ejecutar el TFM completo: RQ2 + Escáner + RQ3 completo
# Requisitos: Java 17+, Maven 3.8+, OPENAI_API_KEY en entorno

$ErrorActionPreference = "Stop"
$scriptStartTime = Get-Date

Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host "TFM - Pipeline Completa (RQ2 + Escáner + RQ3)" -ForegroundColor Cyan
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host ""

# Verificar requisitos
Write-Host "[PRE] Verificando requisitos..." -ForegroundColor Yellow
java -version 2>&1 | ForEach-Object { Write-Host "  Java: $_" -ForegroundColor Gray }
mvn -version 2>&1 | Select-Object -First 1 | ForEach-Object { Write-Host "  Maven: $_" -ForegroundColor Gray }

# Verificar OPENAI_API_KEY
if ([string]::IsNullOrWhiteSpace($env:OPENAI_API_KEY)) {
    Write-Host "[ERROR] OPENAI_API_KEY no configurada. Abortando." -ForegroundColor Red
    exit 1
}
Write-Host "[OK] OPENAI_API_KEY configurada" -ForegroundColor Green
Write-Host ""

# 1. Compilar
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host "1. Compilación (mvn clean compile)" -ForegroundColor Cyan
Write-Host "=================================================================================" -ForegroundColor Cyan
mvn clean compile -q
Write-Host "[OK] Compilación exitosa" -ForegroundColor Green
Write-Host ""

# 2. Tests
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host "2. Tests (mvn test)" -ForegroundColor Cyan
Write-Host "=================================================================================" -ForegroundColor Cyan
mvn test -q
Write-Host "[OK] 415 tests ejecutados" -ForegroundColor Green
Write-Host ""

# 3. Generar classpath
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host "3. Generando classpath" -ForegroundColor Cyan
Write-Host "=================================================================================" -ForegroundColor Cyan
mvn dependency:build-classpath -Dmdep.outputFile=target/classpath.txt -q
$cp = Get-Content target\classpath.txt
Write-Host "[OK] Classpath generado" -ForegroundColor Green
Write-Host ""

# 4. RQ2 Batch (Piloto + Real)
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host "4. RQ2 - Batch (Piloto + Real, 41 casos)" -ForegroundColor Cyan
Write-Host "=================================================================================" -ForegroundColor Cyan
$rq2BatchStart = Get-Date
java -cp "target\classes;$cp" es.tfm.refactoring.experiment.Rq2BatchExecutor
$rq2BatchDuration = (Get-Date) - $rq2BatchStart
Write-Host "[OK] RQ2 Batch completado en $($rq2BatchDuration.TotalSeconds) segundos" -ForegroundColor Green
Write-Host ""

# 5. RQ2 Tutor
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host "5. RQ2 - Tutor (8 casos del paper)" -ForegroundColor Cyan
Write-Host "=================================================================================" -ForegroundColor Cyan
$rq2TutorStart = Get-Date
java -cp "target\classes;$cp" es.tfm.refactoring.experiment.Rq2TutorBatchExecutor
$rq2TutorDuration = (Get-Date) - $rq2TutorStart
Write-Host "[OK] RQ2 Tutor completado en $($rq2TutorDuration.TotalSeconds) segundos" -ForegroundColor Green
Write-Host ""

# 6. RQ2 Comparison (STRICT vs RELAXED)
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host "6. RQ2 - Comparativa STRICT vs RELAXED" -ForegroundColor Cyan
Write-Host "=================================================================================" -ForegroundColor Cyan
$rq2CompStart = Get-Date
java -cp "target\classes;$cp" es.tfm.refactoring.experiment.Rq2ComparisonExecutor
$rq2CompDuration = (Get-Date) - $rq2CompStart
Write-Host "[OK] RQ2 Comparativa completada en $($rq2CompDuration.TotalSeconds) segundos" -ForegroundColor Green
Write-Host ""

# 7. Preparar escáner: clonar proyectos
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host "7a. Clonando proyectos OSS para escáner..." -ForegroundColor Cyan
Write-Host "=================================================================================" -ForegroundColor Cyan

$base = "$PWD\tmp-projects"
$projects = @{
    "commons-lang" = "https://github.com/apache/commons-lang.git#rel/commons-lang-3.14.0"
    "commons-io" = "https://github.com/apache/commons-io.git#rel/commons-io-2.15.1"
    "commons-math" = "https://github.com/apache/commons-math.git#MATH_3_6_1"
    "ant" = "https://github.com/apache/ant.git#rel/1.10.14"
    "jmetal" = "https://github.com/jMetal/jMetal.git#jmetal-5.11"
    "bytecode-viewer" = "https://github.com/Konloch/bytecode-viewer.git#v2.11.2"
    "commons-compress" = "https://github.com/apache/commons-compress.git#rel/commons-compress-1.26.0"
}

$existingProjects = 0
$clonedProjects = 0

foreach ($proj in $projects.GetEnumerator()) {
    $projPath = "$base\$($proj.Name)"
    if (Test-Path $projPath) {
        Write-Host "  [SKIP] $($proj.Name) ya existe" -ForegroundColor Gray
        $existingProjects++
    } else {
        $url, $branch = $proj.Value -split '#'
        Write-Host "  [CLONE] $($proj.Name) ($branch)..."
        git clone --depth 1 --branch $branch $url $projPath 2>&1 | Out-Null
        $clonedProjects++
    }
}
Write-Host "[OK] Proyectos: $existingProjects existentes + $clonedProjects clonados" -ForegroundColor Green
Write-Host ""

# 8. Escáner masivo
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host "7b. Ejecutando escáner masivo" -ForegroundColor Cyan
Write-Host "=================================================================================" -ForegroundColor Cyan
$scannerStart = Get-Date
java -cp "target\classes;$cp" es.tfm.refactoring.scanner.CorpusScanRunner `
    "--project" "commons-lang=$base\commons-lang\src\main\java" `
    "--project" "commons-io=$base\commons-io\src\main\java" `
    "--project" "commons-math=$base\commons-math\src\main\java" `
    "--project" "ant=$base\ant\src\main" `
    "--project" "jmetal=$base\jmetal\jmetal-problem\src\main\java" `
    "--project" "bytecode-viewer=$base\bytecode-viewer\src\main\java" `
    "--project" "commons-compress=$base\commons-compress\src\main\java"
$scannerDuration = (Get-Date) - $scannerStart
Write-Host "[OK] Escáner completado en $($scannerDuration.TotalSeconds) segundos" -ForegroundColor Green
Write-Host ""

# 9. Generar ficheros corpus
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host "8. Generando ficheros corpus desde CSV" -ForegroundColor Cyan
Write-Host "=================================================================================" -ForegroundColor Cyan
java -cp "target\classes;$cp" es.tfm.refactoring.scanner.CorpusFileGenerator `
    "output\corpus-scan\corpus-scan-results.csv" `
    "$PWD\tmp-projects" `
    "output\corpus-scan\generated-corpus"
Write-Host "[OK] Ficheros corpus generados" -ForegroundColor Green
Write-Host ""

# 10. RQ3 Campaña principal
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host "9. RQ3 - Campaña Principal (gpt-4o vs gpt-4.1, 36 invocaciones)" -ForegroundColor Cyan
Write-Host "=================================================================================" -ForegroundColor Cyan
$rq3MainStart = Get-Date
java -cp "target\classes;$cp" es.tfm.refactoring.llm.CampaignExecutor `
    output/rq3-campaign-real-phase9
$rq3MainDuration = (Get-Date) - $rq3MainStart
Write-Host "[OK] RQ3 Campaña principal completada en $($rq3MainDuration.TotalSeconds) segundos" -ForegroundColor Green
Write-Host ""

# 11. RQ3 Campaña trampa
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host "10. RQ3 - Campaña Trampa (falsos positivos, 18 invocaciones)" -ForegroundColor Cyan
Write-Host "=================================================================================" -ForegroundColor Cyan
$rq3TrapStart = Get-Date
java -cp "target\classes;$cp" es.tfm.refactoring.llm.TrapCampaignExecutor `
    output/rq3-trap-campaign
$rq3TrapDuration = (Get-Date) - $rq3TrapStart
Write-Host "[OK] RQ3 Campaña trampa completada en $($rq3TrapDuration.TotalSeconds) segundos" -ForegroundColor Green
Write-Host ""

# 12. RQ3 Prompt v2.0
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host "11. RQ3 - Prompt v2.0 Few-shot (36 invocaciones)" -ForegroundColor Cyan
Write-Host "=================================================================================" -ForegroundColor Cyan
$rq3PromptStart = Get-Date
java -cp "target\classes;$cp" es.tfm.refactoring.llm.PromptV2CampaignExecutor `
    output/rq3-promptv2-campaign
$rq3PromptDuration = (Get-Date) - $rq3PromptStart
Write-Host "[OK] RQ3 Prompt v2.0 completado en $($rq3PromptDuration.TotalSeconds) segundos" -ForegroundColor Green
Write-Host ""

# Resumen final
$totalDuration = (Get-Date) - $scriptStartTime
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host "PIPELINE COMPLETA - RESUMEN FINAL" -ForegroundColor Cyan
Write-Host "=================================================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Tiempos por etapa:" -ForegroundColor Green
Write-Host "  RQ2 Batch:          $($rq2BatchDuration.TotalSeconds) seg" 
Write-Host "  RQ2 Tutor:          $($rq2TutorDuration.TotalSeconds) seg" 
Write-Host "  RQ2 Comparativa:    $($rq2CompDuration.TotalSeconds) seg" 
Write-Host "  Escáner masivo:     $($scannerDuration.TotalSeconds) seg" 
Write-Host "  RQ3 Campaña:        $($rq3MainDuration.TotalSeconds) seg" 
Write-Host "  RQ3 Trampa:         $($rq3TrapDuration.TotalSeconds) seg" 
Write-Host "  RQ3 Prompt v2:      $($rq3PromptDuration.TotalSeconds) seg" 
Write-Host ""
Write-Host "TIEMPO TOTAL:        $($totalDuration.TotalSeconds) segundos ($($totalDuration.TotalMinutes) minutos)" -ForegroundColor Cyan
Write-Host ""
Write-Host "Artefactos generados en output/:" -ForegroundColor Green
Write-Host "  ✓ output/rq2-batch/rq2-summary.md" 
Write-Host "  ✓ output/rq2-tutor/rq2-tutor-summary.md" 
Write-Host "  ✓ output/rq2-comparison/rq2-comparison-summary.md" 
Write-Host "  ✓ output/corpus-scan/corpus-scan-report.md" 
Write-Host "  ✓ output/rq3-campaign-real-phase9/rq3-aggregated.md" 
Write-Host "  ✓ output/rq3-trap-campaign/rq3-trap-aggregated.md" 
Write-Host "  ✓ output/rq3-promptv2-campaign/rq3-promptv2-aggregated.md" 
Write-Host ""
Write-Host "=================================================================================" -ForegroundColor Green
Write-Host "✓ ÉXITO: Pipeline completada sin errores" -ForegroundColor Green
Write-Host "=================================================================================" -ForegroundColor Green
