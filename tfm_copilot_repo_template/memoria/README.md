# Estructura de la memoria del TFM

Memoria del Trabajo de Fin de Máster: *Refactorización de sentencias condicionales para reducir la complejidad cognitiva de código*.

La estructura sigue la guía oficial de la Universidad de Málaga (UMA), respetando los bloques mínimos exigidos: **Resumen, Abstract, Keywords, Introducción, Antecedentes, Descripción del problema, Detalles de la propuesta, Conclusiones, Referencias**.

## 1. Bloques UMA y archivos

| Bloque UMA | Archivo | Estado |
|---|---|---|
| Resumen | [00-resumen.md](00-resumen.md) | Esqueleto |
| Abstract | [00-abstract.md](00-abstract.md) | Esqueleto |
| Keywords | [00-keywords.md](00-keywords.md) | Esqueleto |
| 1. Introducción | [cap1-introduccion.md](cap1-introduccion.md) | Esqueleto |
| 2. Antecedentes | [cap2-antecedentes.md](cap2-antecedentes.md) | Esqueleto |
| 3. Descripción del problema | [cap3-descripcion-problema.md](cap3-descripcion-problema.md) | Esqueleto |
| — (legado, conservado) | [cap3-objetivos.md](cap3-objetivos.md) | Reutilizable: contiene RQ literales y operacionalización |
| 4. Detalles de la propuesta — visión general | [cap4-propuesta.md](cap4-propuesta.md) | Esqueleto |
| 4. Diseño del enfoque | [cap4-diseno.md](cap4-diseno.md) | Esqueleto |
| 4. Arquitectura del prototipo | [cap4-arquitectura.md](cap4-arquitectura.md) | Esqueleto |
| 4. Implementación | [cap4-implementacion.md](cap4-implementacion.md) | Esqueleto |
| 4. Diseño experimental | [cap4-experimento.md](cap4-experimento.md) | Esqueleto |
| 4. Resultados | [cap4-resultados.md](cap4-resultados.md) | Esqueleto |
| 4. Amenazas a la validez | [cap4-amenazas.md](cap4-amenazas.md) | Esqueleto |
| 5. Conclusiones | [cap5-conclusiones.md](cap5-conclusiones.md) | Esqueleto |
| Referencias | [referencias.md](referencias.md) | Vacío — sin citas inventadas |
| Plan visual de figuras | [PLAN_VISUAL.md](PLAN_VISUAL.md) | Definido |

## 2. Apéndices

| Apéndice | Archivo |
|---|---|
| A. Manual técnico | [apendices/A-manual-tecnico.md](apendices/A-manual-tecnico.md) |
| B. Paquete de replicación | [apendices/B-replicacion.md](apendices/B-replicacion.md) |
| C. Prompt RQ3 v1.0 y oráculo | [apendices/C-prompt-rq3.md](apendices/C-prompt-rq3.md) |
| D. Catálogo de `DiscardReason` | [apendices/D-discard-reasons.md](apendices/D-discard-reasons.md) |
| E. Tablas extendidas RQ2 / RQ3 | [apendices/E-tablas-extendidas.md](apendices/E-tablas-extendidas.md) |
| F. Procedimiento SonarQube | [apendices/F-sonarqube.md](apendices/F-sonarqube.md) |
| G. Trazabilidad RQ ↔ artefactos | [apendices/G-trazabilidad.md](apendices/G-trazabilidad.md) |

## 3. Figuras

Las figuras se almacenan en [figuras/](figuras/). Las fuentes Mermaid se versionan en `figuras/src/` y los renderizados en `figuras/img/`. Ver [PLAN_VISUAL.md](PLAN_VISUAL.md).

## 4. Reglas de redacción

- **No reformular las RQ oficiales.** Conservarlas literalmente.
- **No inventar bibliografía.** Marcar como `[PENDIENTE DE CITA]`.
- **No inventar resultados.** Solo referenciar evidencia presente en `output/` o `target/surefire-reports/`.
- Etiquetas reservadas: `[PENDIENTE DE CITA]`, `[PENDIENTE DE VERIFICACIÓN]`, `[PENDIENTE DE REDACCIÓN METODOLÓGICA]`, `[HIPÓTESIS DE TRABAJO]`.

## 5. Checklist global

- [ ] Resumen y abstract redactados
- [ ] Keywords definidas
- [ ] Introducción
- [ ] Antecedentes con bibliografía verificada
- [ ] Descripción del problema completa
- [ ] Diseño del enfoque
- [ ] Arquitectura e implementación documentadas
- [ ] Diseño experimental cerrado
- [ ] Resultados RQ1, RQ2 (incl. validación SonarQube), RQ3
- [ ] Amenazas a la validez
- [ ] Conclusiones y trabajo futuro
- [ ] Referencias completas y verificadas
- [ ] Figuras y tablas finales en `figuras/`
- [ ] Apéndices A–G
