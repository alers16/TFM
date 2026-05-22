################################################################################
# scan-open-source-projects.ps1
#
# Clona un conjunto de proyectos Java open-source y ejecuta el analizador
# de corpus (CorpusScanRunner) sobre cada uno para detectar oportunidades
# de combinación de condicionales anidados.
#
# Uso:
#   .\scan-open-source-projects.ps1
#   .\scan-open-source-projects.ps1 -SkipClone     # si ya están clonados
#   .\scan-open-source-projects.ps1 -ProjectsDir "D:\repos"
#
# Requisitos:
#   - git en el PATH
#   - Java 17+ en el PATH
#   - mvn compilado (ejecutar 'mvn compile' antes si hace falta)
#
# Los resultados se guardan en output/corpus-scan/
################################################################################

param(
    [switch]$SkipClone,
    [string]$ProjectsDir = ".\tmp-projects"
)

$ErrorActionPreference = "Stop"
$RepoRoot = $PSScriptRoot

# ── Proyectos a escanear ────────────────────────────────────────────────────
# Formato: @{ Name="nombre-logico"; Url="url-git"; Tag="tag-o-rama"; SrcDir="src" }
# SrcDir: subdirectorio relativo con el código fuente Java (normalmente "src")
# Si SrcDir es "" se escanea el directorio raíz del proyecto.

$Projects = @(
    @{
        Name   = "commons-lang"
        Url    = "https://github.com/apache/commons-lang.git"
        Tag    = "rel/commons-lang-3.14.0"
        SrcDir = "src/main/java"
    },
    @{
        Name   = "commons-io"
        Url    = "https://github.com/apache/commons-io.git"
        Tag    = "rel/commons-io-2.15.1"
        SrcDir = "src/main/java"
    },
    @{
        Name   = "commons-collections"
        Url    = "https://github.com/apache/commons-collections.git"
        Tag    = "commons-collections-4.4"
        SrcDir = "src/main/java"
    },
    @{
        Name   = "commons-math"
        Url    = "https://github.com/apache/commons-math.git"
        Tag    = "MATH_3_6_1"
        SrcDir = "src/main/java"
    },
    @{
        Name   = "ant"
        Url    = "https://github.com/apache/ant.git"
        Tag    = "rel/1.10.14"
        SrcDir = "src/main"
    },
    @{
        Name   = "guava"
        Url    = "https://github.com/google/guava.git"
        Tag    = "v33.0.0-jre"
        SrcDir = "guava/src"
    },
    @{
        Name   = "jmetal"
        Url    = "https://github.com/jMetal/jMetal.git"
        Tag    = "jmetal-5.11"
        SrcDir = "jmetal-problem/src/main/java"
    },
    @{
        Name   = "bytecode-viewer"
        Url    = "https://github.com/Konloch/bytecode-viewer.git"
        Tag    = "v2.11.2"
        SrcDir = "src/main/java"
    },
    @{
        Name   = "log4j"
        Url    = "https://github.com/apache/logging-log4j2.git"
        Tag    = "rel/2.22.0"
        SrcDir = "log4j-core/src/main/java"
    },
    @{
        Name   = "commons-compress"
        Url    = "https://github.com/apache/commons-compress.git"
        Tag    = "rel/commons-compress-1.26.0"
        SrcDir = "src/main/java"
    }
)

# ── Funciones auxiliares ─────────────────────────────────────────────────────

function Write-Header($msg) {
    Write-Host ""
    Write-Host ("=" * 70) -ForegroundColor Cyan
    Write-Host "  $msg" -ForegroundColor Cyan
    Write-Host ("=" * 70) -ForegroundColor Cyan
}

function Write-Step($msg) {
    Write-Host "  >> $msg" -ForegroundColor Yellow
}

function Ensure-Dir($path) {
    if (-not (Test-Path $path)) {
        New-Item -ItemType Directory -Path $path | Out-Null
    }
}

# ── Preparar directorio de proyectos ─────────────────────────────────────────

Ensure-Dir $ProjectsDir
$ProjectsDir = Resolve-Path $ProjectsDir

Write-Header "Analizador de Corpus Masivo - TFM"
Write-Host "  Directorio de proyectos: $ProjectsDir"
Write-Host "  Proyectos configurados:  $($Projects.Count)"

# ── Compilar el proyecto si hace falta ───────────────────────────────────────

Push-Location $RepoRoot
if (-not (Test-Path "target\classes\es\tfm\refactoring\scanner\CorpusScanRunner.class")) {
    Write-Step "Compilando el proyecto..."
    mvn compile -q
    if ($LASTEXITCODE -ne 0) { throw "mvn compile falló" }
}

$ClassPath = (Get-Content "target\classpath.txt") -replace '"', ''
$RunnerCp  = "target\classes;$ClassPath"
Pop-Location

# ── Clonar proyectos ──────────────────────────────────────────────────────────

if (-not $SkipClone) {
    Write-Header "Clonando proyectos"
    foreach ($p in $Projects) {
        $dest = Join-Path $ProjectsDir $p.Name
        if (Test-Path $dest) {
            Write-Step "$($p.Name): ya existe, omitiendo clone"
        } else {
            Write-Step "Clonando $($p.Name) @ $($p.Tag)..."
            # Clone superficial para ahorrar tiempo y disco
            git clone --depth 1 --branch $p.Tag $p.Url $dest 2>&1 | Out-Null
            if ($LASTEXITCODE -ne 0) {
                Write-Warning "  No se pudo clonar $($p.Name) con la tag '$($p.Tag)'. Intentando sin tag..."
                git clone --depth 1 $p.Url $dest 2>&1 | Out-Null
            }
            if ($LASTEXITCODE -eq 0) {
                Write-Host "    OK" -ForegroundColor Green
            } else {
                Write-Warning "    FALLO al clonar $($p.Name) - se omitirá del análisis"
            }
        }
    }
}

# ── Construir argumentos del escáner ─────────────────────────────────────────

Write-Header "Ejecutando el escáner"

$ScannerArgs = @()
foreach ($p in $Projects) {
    $dest    = Join-Path $ProjectsDir $p.Name
    $srcPath = if ($p.SrcDir) { Join-Path $dest $p.SrcDir } else { $dest }

    if (-not (Test-Path $srcPath)) {
        Write-Warning "Directorio no encontrado, omitiendo: $srcPath"
        continue
    }

    $ScannerArgs += "--project"
    $ScannerArgs += "$($p.Name)=$srcPath"
    Write-Step "$($p.Name) → $srcPath"
}

# ── Ejecutar el escáner ───────────────────────────────────────────────────────

Push-Location $RepoRoot
Write-Host ""
Write-Host "Ejecutando CorpusScanRunner..." -ForegroundColor Yellow

java -cp $RunnerCp es.tfm.refactoring.scanner.CorpusScanRunner @ScannerArgs

if ($LASTEXITCODE -ne 0) {
    Write-Error "El escáner terminó con error ($LASTEXITCODE)"
} else {
    Write-Host ""
    Write-Host "Análisis completado." -ForegroundColor Green
    Write-Host "Resultados en: $(Resolve-Path 'output\corpus-scan')" -ForegroundColor Green
    Write-Host ""
    Write-Host "Ficheros generados:" -ForegroundColor Cyan
    Write-Host "  - output\corpus-scan\corpus-scan-results.csv  (uno por candidato)" -ForegroundColor Cyan
    Write-Host "  - output\corpus-scan\corpus-scan-report.md    (resumen por proyecto)" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Para ver los elegibles:" -ForegroundColor Cyan
    Write-Host '  Import-Csv output\corpus-scan\corpus-scan-results.csv | Where-Object eligible -eq YES | Format-Table project,file,method,line,ccBefore,ccAfter,delta' -ForegroundColor White
}

Pop-Location
