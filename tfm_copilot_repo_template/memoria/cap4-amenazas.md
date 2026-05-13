# 4.7 Amenazas a la validez

## Objetivo de la sección

Discutir limitaciones que pueden afectar la interpretación de los resultados, organizadas por tipo de validez.

## Subsecciones previstas

- **4.7.1 Validez de constructo.**
  - Proxy de complejidad cognitiva ≠ SonarQube oficial; validación cruzada limitada a un subset.
  - Oráculo RQ3 basado en CC y comparación con baseline determinista, no en revisión humana ni en ejecución de tests del proyecto original.
- **4.7.2 Validez interna.**
  - Determinismo del transformador validado por tests; pero los LLMs presentan no-determinismo residual aun con T=0 (mitigado con 3 intentos).
  - Política *one-at-a-time* limita interacciones inesperadas pero podría ocultar efectos compuestos.
- **4.7.3 Validez externa.**
  - Corpus pequeño (n=18 total; subset RQ3 n=6).
  - Comparativa RQ3 actual restringida a dos modelos del mismo proveedor (OpenAI). [HIPÓTESIS DE TRABAJO] Generalización limitada a otros proveedores/familias.
  - Patrón objetivo restringido al caso base sin else/else-if; resultados no extrapolables a refactorizaciones más complejas.
- **4.7.4 Validez de conclusión.**
  - Tamaño muestral reducido impide afirmaciones estadísticas fuertes.
  - Métrica única (CC) puede sesgar la valoración global de calidad.
- **4.7.5 Mitigaciones implementadas.**
  - Congelación del protocolo (`LlmExperimentProtocol`).
  - Trazabilidad completa (evidencias JSON, CSV, MD por invocación).
  - Guardia de elegibilidad v1.1 en oráculo (Phase 8.1).

## Evidencias del repositorio

- [output/rq3-campaign-real-phase9/rq3-incidents.md](../output/rq3-campaign-real-phase9/rq3-incidents.md)
- [docs/campaign-results-rq3-phase9.md](../docs/campaign-results-rq3-phase9.md)
- [src/main/java/es/tfm/refactoring/llm/LlmResponseValidator.java](../src/main/java/es/tfm/refactoring/llm/LlmResponseValidator.java)

## Notas

- [PENDIENTE DE REDACCIÓN METODOLÓGICA] Decisión sobre ampliar a otro proveedor de LLM o documentar la restricción definitivamente.
- Sección sensible: redactar tras cerrar §4.6.
