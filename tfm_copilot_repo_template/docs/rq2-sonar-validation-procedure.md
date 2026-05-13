# Procedimiento de validación complementaria con SonarQube — RQ2

## Estado de esta validación

- **Tipo:** validación complementaria de la métrica de complejidad
  cognitiva utilizada en RQ2.
- **No sustituye** el cálculo del prototipo
  ([CognitiveComplexityCalculator.java](../src/main/java/es/tfm/refactoring/analysis/CognitiveComplexityCalculator.java))
  ni los resultados consolidados en
  [output/rq2-batch/](../output/rq2-batch/).
- **No reanaliza el corpus completo:** se ejecuta sobre un subset
  reducido seleccionado para cubrir los ejes de variación relevantes.
- **Ejecución:** ver [sonarqube-setup.md](sonarqube-setup.md).

## Subset elegido (4 casos)

| caseId | Corpus | Estado prototipo | CC before (proxy) | Δ (proxy) | Razón de inclusión |
|---|---|---|---|---|---|
| `PILOT_VALID_NESTED_IN_LOOP`     | piloto | elegible            | 6  | −2 | Mayor delta del corpus piloto; valida reglas de anidamiento dentro de bucle |
| `REAL_COMMONS_LANG_CONTAINS_NONE`| real   | elegible (parcial)  | 15 | −4 | Caso real con mayor CC y mayor delta absoluto del corpus real |
| `REAL_ANT_MATCH_PATH`            | real   | elegible (parcial)  | 6  | −2 | Triple anidamiento con elegibilidad parcial (P5 rechaza nivel 3) |
| `REAL_COMMONS_LANG_CHOMP`        | real   | no elegible (P5)    | 6  | 0  | Caso no elegible: confirma que el rechazo no introduce divergencia |

Justificación del tamaño: cuatro casos permiten cubrir
- corpus piloto vs corpus real,
- elegible total vs elegible parcial vs no elegible,
- rango de CC entre 6 y 15,
- rango de Δ entre 0 y −4,

manteniendo una operación manual *after* (modificar 4 archivos)
asumible en una sola ejecución.

## Procedimiento

1. **Arrancar SonarQube** según [sonarqube-setup.md](sonarqube-setup.md).
2. **Ejecutar el escaneo *before*** sobre los archivos originales:
   ```bash
   mvn -DskipTests verify sonar:sonar \
     -Dsonar.token=$SONAR_TOKEN \
     -Dsonar.projectKey=tfm-rq2-sonar-validation-before \
     -Dsonar.sources=src/main/resources/real-corpus,src/main/resources/pilot-corpus \
     -Dsonar.inclusions=RealCommonsLangContainsNone.java,RealAntMatchPath.java,RealCommonsLangChomp.java,PilotValidNestedInLoop.java \
     -Dsonar.java.binaries=target/classes
   ```
3. **Recuperar las métricas *before*** vía API:
   ```bash
   curl -u $SONAR_TOKEN: \
     "http://localhost:9000/api/measures/component_tree?component=tfm-rq2-sonar-validation-before&metricKeys=cognitive_complexity&qualifiers=FIL&ps=100" \
     -o output/rq2-sonar-validation/sonar-before.json
   ```
4. **Sustituir** temporalmente el contenido de cada archivo del subset
   por la versión refactorizada extraída del campo `sourceAfter` de
   [rq2-real-results.json](../output/rq2-batch/rq2-real-results.json) y
   [rq2-pilot-results.json](../output/rq2-batch/rq2-pilot-results.json).
5. **Ejecutar el escaneo *after*** con un proyectoKey distinto:
   ```bash
   mvn -DskipTests verify sonar:sonar \
     -Dsonar.token=$SONAR_TOKEN \
     -Dsonar.projectKey=tfm-rq2-sonar-validation-after \
     -Dsonar.sources=src/main/resources/real-corpus,src/main/resources/pilot-corpus \
     -Dsonar.inclusions=RealCommonsLangContainsNone.java,RealAntMatchPath.java,RealCommonsLangChomp.java,PilotValidNestedInLoop.java \
     -Dsonar.java.binaries=target/classes
   ```
6. **Recuperar métricas *after*** y guardarlas en
   `output/rq2-sonar-validation/sonar-after.json`.
7. **Restaurar** los archivos originales (`git checkout
   src/main/resources/real-corpus src/main/resources/pilot-corpus`).
8. **Cumplimentar** el informe
   [rq2-sonar-validation.md](../output/rq2-sonar-validation/rq2-sonar-validation.md)
   con los valores observados.

## Métricas a comparar

| Métrica | Fuente prototipo | Fuente SonarQube |
|---|---|---|
| `complexityBefore` | `rq2-{pilot,real}-results.csv` columna `complexityBefore` | métrica `cognitive_complexity` por archivo (run *before*) |
| `complexityAfter`  | `rq2-{pilot,real}-results.csv` columna `complexityAfter`  | métrica `cognitive_complexity` por archivo (run *after*)  |
| `delta`            | `rq2-{pilot,real}-results.csv` columna `delta`            | (after − before) calculado a partir de la API             |

## Criterios de éxito

- **Coincidencia esperada:** valores idénticos en *before* y *after* en los
  cuatro casos del subset, dentro del subconjunto de reglas implementadas
  por `CognitiveComplexityCalculator`.
- **Tolerancia documentada:** divergencia ≥1 punto debe registrarse como
  desviación y discutirse en la sección de amenazas a la validez.

## Limitaciones declaradas

- Subset de 4 casos sobre 18 → no estadísticamente representativo.
- Operación *after* manual → riesgo de error humano al sustituir archivos
  (mitigado al ejecutar inmediatamente `git checkout` tras el escaneo).
- Resultado dependiente de la versión del motor de SonarQube en el
  momento del análisis.
