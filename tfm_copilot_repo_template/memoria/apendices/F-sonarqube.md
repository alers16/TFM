# Apéndice F. Procedimiento SonarQube

## Objetivo

Documentar el procedimiento operativo para reproducir la validación cruzada con SonarQube Community Build.

## Subsecciones previstas

- F.1 Requisitos (Docker, puertos).
- F.2 Arranque del servidor: [docs/sonarqube-setup.md](../../docs/sonarqube-setup.md), [docker-compose.sonarqube.yml](../../docker-compose.sonarqube.yml).
- F.3 Procedimiento de contraste: [docs/sonar-contrast-procedure.md](../../docs/sonar-contrast-procedure.md).
- F.4 Procedimiento de validación RQ2: [docs/rq2-sonar-validation-procedure.md](../../docs/rq2-sonar-validation-procedure.md).
- F.5 Resultados consolidados: [output/rq2-sonar-validation/rq2-sonar-validation.md](../../output/rq2-sonar-validation/rq2-sonar-validation.md).

## Notas

- [PENDIENTE DE VERIFICACIÓN] Confirmar si los `sonar-before.json` / `sonar-after.json` actuales corresponden a una corrida real o son placeholders del proxy. La cabecera del propio `rq2-sonar-validation.md` declara estado de plantilla.
