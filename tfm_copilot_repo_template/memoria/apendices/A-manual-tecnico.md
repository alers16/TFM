# Apéndice A. Manual técnico

## Objetivo

Permitir a un tercero compilar, ejecutar tests y reproducir las campañas RQ2 y RQ3 sin asistencia adicional.

## Subsecciones previstas

- A.1 Requisitos: Java 17+, Maven 3.8+.
- A.2 Compilación y tests: `mvn clean compile`, `mvn test`.
- A.3 Configuración de credenciales (`OPENAI_API_KEY`, `OPENAI_MODEL`, `OPENAI_SECONDARY_MODEL`).
- A.4 Ejecución de la campaña RQ3 (`CampaignExecutor`, modos `live` y `dry-run`).
- A.5 Ejecución de la batería RQ2.
- A.6 Arranque de SonarQube (Docker) y escaneo.

## Evidencias del repositorio

- [README.md](../../README.md), [README_USO.md](../../README_USO.md)
- [pom.xml](../../pom.xml)
- [docker-compose.sonarqube.yml](../../docker-compose.sonarqube.yml)

## Notas

- Sección redactable ya: contenido extraíble de `README.md` y `README_USO.md`.
