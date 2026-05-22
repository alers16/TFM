@echo off
setlocal

:: Directorio del proyecto (donde está este .bat)
set "PROJ=%~dp0"
cd /d "%PROJ%"

:: ── Compilar si target\classes no existe ─────────────────────────────────────
if not exist "target\classes" (
    echo Compilando...
    mvn compile -q
    if errorlevel 1 ( echo ERROR: mvn compile fallo & exit /b 1 )
)

:: ── Classpath ─────────────────────────────────────────────────────────────────
set /p CP=<target\classpath.txt

:: ── Fichero(s) a analizar ─────────────────────────────────────────────────────
set "FILES=%*"
if "%FILES%"=="" set "FILES=CognitiveComplexityMethodCheckMax0.java"

:: ── Ejecutar ──────────────────────────────────────────────────────────────────
java -cp "target\classes;%CP%" es.tfm.refactoring.analysis.CcAnalyzeCli %FILES%
endlocal
