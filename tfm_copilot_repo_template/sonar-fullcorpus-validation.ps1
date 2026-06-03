#requires -version 5.1
<#
.SYNOPSIS
  Validación de contraste de complejidad cognitiva (proxy del prototipo vs.
  SonarQube oficial) sobre TODO el corpus RQ2.

.DESCRIPTION
  Para cada caso del corpus (output/rq2-batch/rq2-all-results.json) materializa
  el método 'before' y el 'after' como ficheros Java independientes (envueltos
  en una clase nombrada por caseId), los escanea con SonarQube en dos proyectos
  separados, recupera la métrica oficial cognitive_complexity por fichero y la
  compara con la del proxy (complexityBefore/After). Produce:

    output/rq2-sonar-validation/fullcorpus/
      before/<caseId>.java            (fuentes materializadas)
      after/<caseId>.java
      sonar-before.json               (respuesta API cruda)
      sonar-after.json
      comparison.csv                  (una fila por caso)
      comparison.md                   (informe para la memoria)
      metadata.json                   (versión Sonar, timestamps, operador...)

  La métrica de complejidad cognitiva es SINTÁCTICA: SonarQube la calcula sobre
  el AST aunque no se resuelvan todos los símbolos, por lo que envolver el
  método en una clase mínima es suficiente para el contraste.

.PREREQUISITOS
  1) SonarQube Community en marcha (ver docs/sonarqube-setup.md):
        docker compose -f docker-compose.sonarqube.yml up -d
  2) Token de usuario en $env:SONAR_TOKEN  (o parámetro -SonarToken).
  3) sonar-scanner CLI accesible (en PATH, o vía -ScannerCmd).
  4) Proyecto compilado una vez (genera target/classes, requerido por
     sonar.java.binaries):
        mvn -DskipTests compile

.EJEMPLO
  $env:SONAR_TOKEN = "sqa_xxxxx"
  ./sonar-fullcorpus-validation.ps1

.NOTAS
  No modifica el corpus ni los artefactos de RQ2; solo escribe en WorkDir.
#>
[CmdletBinding()]
param(
  [string]$SonarHostUrl = "http://localhost:9000",
  [string]$SonarToken   = $env:SONAR_TOKEN,
  [string]$ResultsJson  = "output/rq2-batch/rq2-all-results.json",
  [string]$JavaBinaries = "target/classes",
  [string]$WorkDir      = "output/rq2-sonar-validation/fullcorpus",
  [string]$ScannerCmd   = "sonar-scanner",
  [string]$KeyPrefix    = "tfm-rq2-fullcorpus",
  [int]$PollSeconds     = 8,
  [int]$PollRetries     = 40,
  [switch]$SkipScan
)
$ErrorActionPreference = "Stop"

function Get-AuthHeader {
  $pair = "$($SonarToken):"
  $b64  = [Convert]::ToBase64String([Text.Encoding]::ASCII.GetBytes($pair))
  return @{ Authorization = "Basic $b64" }
}

# --- Validaciones de entorno -------------------------------------------------
if ([string]::IsNullOrWhiteSpace($SonarToken)) {
  throw "Falta el token. Define `$env:SONAR_TOKEN o pasa -SonarToken."
}
if (-not (Test-Path $ResultsJson)) { throw "No existe $ResultsJson" }
$beforeDir = Join-Path $WorkDir "before"
$afterDir  = Join-Path $WorkDir "after"
New-Item -ItemType Directory -Force -Path $beforeDir, $afterDir | Out-Null

# --- 1) Materializar ficheros before/after desde el JSON ---------------------
Write-Host "[1/5] Materializando ficheros Java del corpus..." -ForegroundColor Cyan
$cases = Get-Content -Raw -Path $ResultsJson | ConvertFrom-Json
$proxy = @{}
$nWritten = 0
foreach ($c in $cases) {
  $id = $c.caseId
  if ([string]::IsNullOrWhiteSpace($id)) { continue }
  $beforeBody = [string]$c.sourceBefore
  $afterBody  = [string]$c.sourceAfter
  if ([string]::IsNullOrWhiteSpace($beforeBody)) { continue }
  if ([string]::IsNullOrWhiteSpace($afterBody))  { $afterBody = $beforeBody }  # inelegibles: sin cambio
  $cls  = $id   # caseId es un identificador Java válido (mayúsculas, dígitos, '_')
  $wrapB = "public class $cls {`r`n$beforeBody`r`n}`r`n"
  $wrapA = "public class $cls {`r`n$afterBody`r`n}`r`n"
  Set-Content -Path (Join-Path $beforeDir "$cls.java") -Value $wrapB -Encoding UTF8
  Set-Content -Path (Join-Path $afterDir  "$cls.java") -Value $wrapA -Encoding UTF8
  $proxy[$cls] = [pscustomobject]@{
    caseId = $id; eligible = $c.eligible
    proxyBefore = [int]$c.complexityBefore
    proxyAfter  = [int]$c.complexityAfter
    proxyDelta  = [int]$c.delta
  }
  $nWritten++
}
Write-Host "      $nWritten casos materializados en $WorkDir" -ForegroundColor DarkGray

# --- 2) Escaneos SonarQube (before y after) ----------------------------------
function Invoke-SonarScan([string]$key, [string]$name, [string]$src) {
  Write-Host "      sonar-scanner -> $key ($src)" -ForegroundColor DarkGray
  & $ScannerCmd `
    "-Dsonar.host.url=$SonarHostUrl" `
    "-Dsonar.token=$SonarToken" `
    "-Dsonar.projectKey=$key" `
    "-Dsonar.projectName=$name" `
    "-Dsonar.sources=$src" `
    "-Dsonar.java.binaries=$JavaBinaries" `
    "-Dsonar.scm.disabled=true"
  if ($LASTEXITCODE -ne 0) { throw "sonar-scanner devolvió código $LASTEXITCODE para $key" }
}

$keyBefore = "$KeyPrefix-before"
$keyAfter  = "$KeyPrefix-after"
if (-not $SkipScan) {
  Write-Host "[2/5] Lanzando escaneos SonarQube..." -ForegroundColor Cyan
  Invoke-SonarScan $keyBefore "TFM RQ2 Full Corpus (before)" $beforeDir
  Invoke-SonarScan $keyAfter  "TFM RQ2 Full Corpus (after)"  $afterDir
} else {
  Write-Host "[2/5] -SkipScan: se omiten los escaneos." -ForegroundColor Yellow
}

# --- 3) Recuperar cognitive_complexity por fichero (con reintentos) ----------
function Get-FileCC([string]$key, [string]$outFile) {
  $headers = Get-AuthHeader
  $url = "$SonarHostUrl/api/measures/component_tree?component=$key&metricKeys=cognitive_complexity&qualifiers=FIL&ps=500"
  for ($i = 0; $i -lt $PollRetries; $i++) {
    try {
      $resp = Invoke-RestMethod -Uri $url -Headers $headers -Method Get
      if ($resp.components -and $resp.components.Count -gt 0) {
        $resp | ConvertTo-Json -Depth 12 | Set-Content -Path $outFile -Encoding UTF8
        return $resp
      }
    } catch { }
    Start-Sleep -Seconds $PollSeconds
  }
  throw "No se obtuvieron medidas de $key tras $($PollRetries*$PollSeconds)s (¿análisis en cola?)."
}

Write-Host "[3/5] Recuperando métricas oficiales..." -ForegroundColor Cyan
$respBefore = Get-FileCC $keyBefore (Join-Path $WorkDir "sonar-before.json")
$respAfter  = Get-FileCC $keyAfter  (Join-Path $WorkDir "sonar-after.json")

function To-CCMap($resp) {
  $m = @{}
  foreach ($comp in $resp.components) {
    $cls = [IO.Path]::GetFileNameWithoutExtension($comp.name)
    $val = ($comp.measures | Where-Object { $_.metric -eq "cognitive_complexity" }).value
    if ($null -ne $val) { $m[$cls] = [int]$val }
  }
  return $m
}
$ccBefore = To-CCMap $respBefore
$ccAfter  = To-CCMap $respAfter

# --- 4) Cruce proxy vs SonarQube ---------------------------------------------
Write-Host "[4/5] Cruzando proxy vs SonarQube..." -ForegroundColor Cyan
$rows = foreach ($cls in ($proxy.Keys | Sort-Object)) {
  $p = $proxy[$cls]
  $sb = if ($ccBefore.ContainsKey($cls)) { $ccBefore[$cls] } else { $null }
  $sa = if ($ccAfter.ContainsKey($cls))  { $ccAfter[$cls] }  else { $null }
  [pscustomobject]@{
    caseId       = $p.caseId
    eligible     = $p.eligible
    proxyBefore  = $p.proxyBefore
    sonarBefore  = $sb
    matchBefore  = ($null -ne $sb -and $sb -eq $p.proxyBefore)
    proxyAfter   = $p.proxyAfter
    sonarAfter   = $sa
    matchAfter   = ($null -ne $sa -and $sa -eq $p.proxyAfter)
    diffBefore   = if ($null -ne $sb) { $sb - $p.proxyBefore } else { $null }
    diffAfter    = if ($null -ne $sa) { $sa - $p.proxyAfter }  else { $null }
  }
}
$rows | Export-Csv -Path (Join-Path $WorkDir "comparison.csv") -NoTypeInformation -Encoding UTF8

$measured   = ($rows | Where-Object { $null -ne $_.sonarBefore }).Count
$missing    = ($rows | Where-Object { $null -eq $_.sonarBefore }).Count
$okBefore   = ($rows | Where-Object { $_.matchBefore }).Count
$okAfter    = ($rows | Where-Object { $_.matchAfter }).Count
$divergence = $rows | Where-Object { ($null -ne $_.sonarBefore -and -not $_.matchBefore) -or ($null -ne $_.sonarAfter -and -not $_.matchAfter) }

# --- 5) Informe + metadata ----------------------------------------------------
Write-Host "[5/5] Escribiendo informe y metadata..." -ForegroundColor Cyan
$sonarVersion = try { (Invoke-RestMethod -Uri "$SonarHostUrl/api/server/version" -Headers (Get-AuthHeader)) } catch { "desconocida" }

$meta = [pscustomobject]@{
  artifactType   = "rq2-sonar-validation-fullcorpus"
  generatedAt    = (Get-Date).ToUniversalTime().ToString("o")
  operator       = $env:USERNAME
  host           = $env:COMPUTERNAME
  sonarHostUrl   = $SonarHostUrl
  sonarVersion   = "$sonarVersion"
  projectBefore  = $keyBefore
  projectAfter   = $keyAfter
  casesTotal     = $proxy.Count
  casesMeasured  = $measured
  casesMissing   = $missing
  matchBefore    = $okBefore
  matchAfter     = $okAfter
}
$meta | ConvertTo-Json -Depth 6 | Set-Content -Path (Join-Path $WorkDir "metadata.json") -Encoding UTF8

$pctB = if ($measured) { [math]::Round(100.0*$okBefore/$measured,1) } else { 0 }
$pctA = if ($measured) { [math]::Round(100.0*$okAfter/$measured,1) } else { 0 }
$md = @"
# Validación de contraste proxy vs SonarQube — corpus completo

- **Generado:** $($meta.generatedAt)  ·  **Operador:** $($meta.operator)  ·  **Host:** $($meta.host)
- **SonarQube:** versión $($meta.sonarVersion) en $SonarHostUrl
- **Proyectos:** ``$keyBefore`` / ``$keyAfter``
- **Casos:** $($proxy.Count) materializados, $measured medidos por SonarQube, $missing sin medida (no parseables).

## Coincidencia proxy ↔ SonarQube

| Medida | Coincidencias | Total medidos | % |
|---|---|---|---|
| Complejidad *before* | $okBefore | $measured | $pctB |
| Complejidad *after*  | $okAfter  | $measured | $pctA |

## Divergencias (|Δ| ≥ 1 punto)

| caseId | proxyBefore | sonarBefore | proxyAfter | sonarAfter |
|---|---|---|---|---|
$([string]::Join("`n", ($divergence | ForEach-Object { "| $($_.caseId) | $($_.proxyBefore) | $($_.sonarBefore) | $($_.proxyAfter) | $($_.sonarAfter) |" })))

> Detalle completo por caso en ``comparison.csv``. Las medidas oficiales crudas
> están en ``sonar-before.json`` y ``sonar-after.json``.
"@
$md | Set-Content -Path (Join-Path $WorkDir "comparison.md") -Encoding UTF8

Write-Host ""
Write-Host "Listo. Resultados en $WorkDir" -ForegroundColor Green
Write-Host ("  before: {0}/{1} coinciden ({2}%)" -f $okBefore,$measured,$pctB)
Write-Host ("  after : {0}/{1} coinciden ({2}%)" -f $okAfter,$measured,$pctA)
if ($missing -gt 0) { Write-Host "  $missing casos sin medida (revisar parseo)." -ForegroundColor Yellow }
