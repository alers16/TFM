# Arranque de SonarQube Community Build para validación complementaria

## Propósito

Documento operativo para arrancar SonarQube en local mediante Docker y
ejecutar SonarScanner for Maven sobre un subset de casos del corpus RQ2.

> **Nota metodológica.** Esta validación es **complementaria**: no
> sustituye el cálculo propio de complejidad cognitiva del prototipo
> (`CognitiveComplexityCalculator`) ni los resultados de RQ2 ya
> consolidados en `output/rq2-batch/`. Su objetivo único es reforzar la
> validez de constructo de RQ2 contrastando un subconjunto representativo
> contra la herramienta oficial de SonarSource.

## Requisitos

- Docker Desktop o Docker Engine instalado y en ejecución.
- Maven 3.8+ y JDK 17 disponibles en `PATH`.
- ~3 GB de RAM libres y los puertos `9000` y `9092` libres.

## Arranque del servidor (SonarQube Community Build)

### Opción 1 — `docker run` directo (PowerShell / Bash)

```bash
docker run -d --name sonarqube-tfm \
  -p 9000:9000 \
  -e SONAR_ES_BOOTSTRAP_CHECKS_DISABLE=true \
  sonarqube:community
```

### Opción 2 — `docker compose` (recomendado para reproducibilidad)

Crear un archivo `docker-compose.sonar.yml` en la raíz del repositorio con:

```yaml
services:
  sonarqube:
    image: sonarqube:community
    container_name: sonarqube-tfm
    ports:
      - "9000:9000"
    environment:
      SONAR_ES_BOOTSTRAP_CHECKS_DISABLE: "true"
    volumes:
      - sonarqube_data:/opt/sonarqube/data
      - sonarqube_logs:/opt/sonarqube/logs
      - sonarqube_extensions:/opt/sonarqube/extensions

volumes:
  sonarqube_data:
  sonarqube_logs:
  sonarqube_extensions:
```

Y arrancar con:

```bash
docker compose -f docker-compose.sonar.yml up -d
```

### Verificación

Esperar 1–2 minutos hasta que SonarQube esté operativo y comprobar:

```bash
curl http://localhost:9000/api/system/status
# {"id":"...","version":"...","status":"UP"}
```

Acceder a <http://localhost:9000> con credenciales por defecto
`admin` / `admin` (forzará cambio de contraseña al primer acceso).

## Generación del token de autenticación

En la UI:

1. *My Account → Security → Generate Tokens*.
2. Tipo: *User Token*. Nombre: `tfm-rq2-validation`.
3. Copiar el token (solo se muestra una vez).
4. Exportarlo como variable de entorno:

```bash
# Linux/macOS
export SONAR_TOKEN=sqa_xxxxxxxx

# PowerShell
$env:SONAR_TOKEN = "sqa_xxxxxxxx"
```

## Comando de análisis con SonarScanner for Maven

El subset de casos se encuentra en `src/main/resources/real-corpus/`
y `src/main/resources/pilot-corpus/`. SonarQube debe analizar únicamente
los archivos seleccionados (ver
[rq2-sonar-validation-procedure.md](rq2-sonar-validation-procedure.md)).

### Comando exacto

```bash
mvn -DskipTests verify sonar:sonar \
  -Dsonar.token=$SONAR_TOKEN \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.projectKey=tfm-rq2-sonar-validation \
  -Dsonar.projectName="TFM RQ2 Sonar Validation" \
  -Dsonar.sources=src/main/resources/real-corpus,src/main/resources/pilot-corpus \
  -Dsonar.inclusions=RealCommonsLangContainsNone.java,RealAntMatchPath.java,RealCommonsLangChomp.java,PilotValidNestedInLoop.java \
  -Dsonar.java.binaries=target/classes \
  -Dsonar.exclusions=src/main/java/**,src/test/**
```

> El parámetro `-Dsonar.java.binaries` apunta a los `.class` del
> proyecto principal aunque no formen parte del análisis; el plugin lo
> requiere para activar el análisis Java.

### Verificación

Tras el éxito del comando, la UI mostrará el proyecto
`tfm-rq2-sonar-validation` con métricas por archivo. La métrica de
complejidad cognitiva por método se consulta vía API:

```bash
curl -u $SONAR_TOKEN: \
  "http://localhost:9000/api/measures/component_tree?component=tfm-rq2-sonar-validation&metricKeys=cognitive_complexity&qualifiers=FIL&ps=100"
```

## Flujo before/after

Para obtener el valor *before* basta con ejecutar el comando con los
archivos originales del corpus.

Para obtener el valor *after* hay dos vías:

1. **Manual:** sustituir el contenido de los archivos del subset por la
   versión refactorizada (extraída de `output/rq2-batch/rq2-real-results.json`,
   campo `sourceAfter`) en una copia de trabajo, relanzar el comando con
   un `sonar.projectKey` diferente (`tfm-rq2-sonar-validation-after`)
   y restaurar los originales con `git checkout`.
2. **Automatizada:** mantener una rama `sonar-after` con los archivos
   refactorizados aplicados y ejecutar el escaneo desde ella.

> Esta operación debe registrarse en el informe
> `output/rq2-sonar-validation/rq2-sonar-validation.md`.

## Limpieza

```bash
docker compose -f docker-compose.sonar.yml down -v   # borra volúmenes
# o
docker rm -f sonarqube-tfm
```

## Limitaciones declaradas

- SonarQube Community Build mide complejidad cognitiva con la
  implementación oficial vigente en el momento del análisis; cambios
  futuros del motor podrían producir valores ligeramente distintos.
- El subset analizado es pequeño por diseño (validación complementaria,
  no exhaustiva).
- La operación *after* requiere intervención manual o ramas auxiliares;
  no está automatizada en el pipeline principal.
