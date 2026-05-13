# Fase 9 - Primera Campana Real de RQ3

> **RQ3.** ¿Pueden los grandes modelos de lenguaje realizar esta refactorización de forma correcta y automática?

## Diagnostico

Esta fase cambia la ejecucion de campana desde respuestas pregrabadas (dry run) a provider real de API, manteniendo el protocolo experimental congelado.

Estado de ejecucion en esta corrida:

- Tipo de campana: primera ejecucion real de RQ3
- Modo de ejecucion: `live`
- Resultado operativo: bloqueada en arranque por falta de `OPENAI_API_KEY`
- Invocaciones completadas: `0/36`

## Condiciones congeladas (sin cambios)

- Subset RQ3: 6 casos
- Modelos del protocolo: `gpt-4o`, `claude-3.5-sonnet`
- Temperatura: `0.0`
- Intentos por caso y modelo: `3`
- Prompt: `v1.0`
- Max tokens: `2048`
- Oraculo: `LlmResponseValidator` v1.1 (sin modificaciones durante la campana)

## Que se ejecuto

1. Se habilito `LiveLlmResponseProvider` como provider por defecto en `CampaignExecutor`.
2. Se mantuvo separacion limpia mediante interfaz `LlmResponseProvider`.
3. Se ejecuto la campana con `LlmExperimentProtocol.defaultProtocol()`.
4. Se exporto evidencia reproducible incluso en caso de bloqueo tecnico de arranque.

## Trazabilidad por invocacion y evidencia

Para esta corrida no hubo invocaciones efectivas porque el bloqueo fue previo a la primera llamada de API.
Aun asi, se conservaron artefactos de evidencia y de incidente tecnico para replicacion.

### Archivos generados

- `output/rq3-campaign-real-phase9/rq3-full-evidence.json`
- `output/rq3-campaign-real-phase9/rq3-summary.csv`
- `output/rq3-campaign-real-phase9/rq3-aggregated.md`
- `output/rq3-campaign-real-phase9/rq3-run-metadata.json`
- `output/rq3-campaign-real-phase9/rq3-incidents.md`

### Incidencia tecnica registrada

- Etapa: `startup`
- Motivo: variable de entorno obligatoria no configurada (`OPENAI_API_KEY`)
- Impacto: campana no iniciada, 0 invocaciones realizadas

## Resultados preliminares

No hay resultados de veredictos por modelo en esta corrida porque no se realizaron invocaciones.

- SUCCESS: 0
- INCORRECT: 0
- INVALID_OUTPUT: 0
- REFUSED: 0
- PARTIAL: 0
- ERROR: 0 (por invocacion)

Nota metodologica:

- La incidencia quedo registrada fuera del flujo de invocacion, en `rq3-incidents.md` y `rq3-run-metadata.json`.

## Limitaciones de esta muestra

1. Esta evidencia documenta una corrida real bloqueada por configuracion, no una corrida completa de 36 invocaciones.
2. No se pueden estimar tasa de exito, baseline match ni comportamiento en elegibles/inelegibles hasta completar la ejecucion live.
3. Coste y latencia de API quedan [PENDIENTE DE VERIFICACION].

## Siguiente paso inmediato

1. Configurar `OPENAI_API_KEY` y `ANTHROPIC_API_KEY` en el entorno de ejecucion.
2. Re-ejecutar exactamente el mismo comando de campana live sobre `output/rq3-campaign-real-phase9` o un subdirectorio de fecha.
3. Conservar todos los artefactos generados, incluyendo incidencias si ocurren durante invocaciones.
4. Actualizar este informe con resultados agregados por modelo y por caso tras la corrida completa.
