#!/usr/bin/env pwsh
<#
.SYNOPSIS
    Calcula y muestra la complejidad cognitiva de cada método en uno o varios
    ficheros Java usando CcAnalyzeCli.

.EXAMPLE
    # Analizar el fichero adjunto en la raíz del proyecto
    .\cc-analyze.ps1 CognitiveComplexityMethodCheckMax0.java

.EXAMPLE
    # Analizar varios ficheros
    .\cc-analyze.ps1 Fichero1.java Fichero2.java

.EXAMPLE
    # Analizar todos los .java del corpus generado
    .\cc-analyze.ps1 (Get-ChildItem output\corpus-scan\generated-corpus\*.java | % { $_.FullName })
#>
param(
    [Parameter(Mandatory=$false, ValueFromRemainingArguments=$true)]
    [string[]]$Files = @("CognitiveComplexityMethodCheckMax0.java")
)

$ErrorActionPreference = "Stop"
$projectDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $projectDir

# ── 1. Compilar si las clases están desactualizadas ──────────────────────────
$srcTs = (Get-ChildItem src\main\java -Recurse -Filter *.java |
          Measure-Object -Property LastWriteTime -Maximum).Maximum
$classTs = if (Test-Path target\classes) {
    (Get-ChildItem target\classes -Recurse -Filter *.class |
     Measure-Object -Property LastWriteTime -Maximum).Maximum
} else { [datetime]::MinValue }

if ($srcTs -gt $classTs) {
    Write-Host "Compilando..." -ForegroundColor Cyan
    mvn compile -q
    if ($LASTEXITCODE -ne 0) { Write-Error "mvn compile fallo"; exit 1 }
}

# ── 2. Construir classpath ────────────────────────────────────────────────────
$cp = Get-Content target\classpath.txt

# ── 3. Ejecutar el analizador ─────────────────────────────────────────────────
java -cp "target\classes;$cp" es.tfm.refactoring.analysis.CcAnalyzeCli @Files
