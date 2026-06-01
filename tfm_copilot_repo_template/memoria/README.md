# Memoria del TFM — estructura LaTeX

Memoria del Trabajo de Fin de Máster: *Refactorización de sentencias condicionales anidadas para reducir la complejidad cognitiva en Java*.

> **Formato canónico: LaTeX, clase `report`** (orientada a memoria/tesis: capítulos con `\chapter`, índice nativo), márgenes a página completa con `geometry`. El punto de entrada es [`main.tex`](main.tex).
>
> Nota: `.claude/CLAUDE.md` §4 y la skill `tfm-latex` mencionan `llncs` (LNCS) como formato «recomendado». Tras comprobar que **la guía UMA no exige LNCS** (solo pide márgenes a página completa) y que `llncs` —clase de *paper* de actas— es inadecuada para un documento con capítulos y apéndices, se optó por `report`. **Pendiente:** alinear `CLAUDE.md`/`tfm-latex` con esta decisión.
>
> Los archivos `.md` de esta carpeta (`cap*.md`, `00-*.md`, `apendices/*.md`) son **material fuente legado** que se está migrando a LaTeX. No son la memoria final; se conservan como referencia durante la migración y se eliminarán cuando su contenido esté íntegramente trasladado.

## 1. Estructura LaTeX

```
memoria/
  main.tex                     # clase report + portada + \input de secciones
  secciones/
    00-preliminares.tex        # Resumen / Abstract / Keywords        [andamiaje]
    01-introduccion.tex        # Introducción                          [parcial]
    02-antecedentes.tex        # Antecedentes / estado del arte        [andamiaje]
    03-problema.tex            # Descripción del problema + RQ + oper. [REDACTADO]
    04-propuesta.tex           # 4.1 Visión general (DSR)              [REDACTADO]
    04a-diseno.tex             # 4.2 Diseño del enfoque                [REDACTADO]
    04b-arquitectura.tex       # 4.3 Arquitectura del prototipo        [REDACTADO]
    04c-implementacion.tex     # 4.4 Implementación                    [REDACTADO]
    04d-experimento.tex        # 4.5 Diseño experimental               [REDACTADO]
    04e-resultados.tex         # 4.6 Resultados (cifras de output/)    [REDACTADO]
    04f-amenazas.tex           # 4.7 Amenazas a la validez             [andamiaje]
    05-conclusiones.tex        # Conclusiones                          [andamiaje]
    99-apendices.tex           # Apéndices A–G                         [andamiaje]
  bibliografia/referencias.bib # BibTeX (vacío; sin entradas inventadas)
  figuras/
    src/                       # fuentes Mermaid (.mmd) versionadas
    img/                       # renderizados (SVG/PNG)
  PLAN_VISUAL.md               # catálogo de figuras y tablas
```

## 2. Cobertura de los bloques mínimos UMA

| Bloque UMA | Sección LaTeX | Estado |
|---|---|---|
| Resumen / Abstract / Keywords | `secciones/00-preliminares.tex` | Andamiaje |
| Introducción | `secciones/01-introduccion.tex` | Objetivos/contribuciones redactados; contexto pendiente |
| Antecedentes (estado del arte) | `secciones/02-antecedentes.tex` | Andamiaje (skill `tfm-state-of-the-art`) |
| Descripción del problema | `secciones/03-problema.tex` | **Redactado** |
| Detalles de la propuesta | `secciones/04*.tex` | Diseño/arquitectura/implementación/experimento/**resultados redactados**; amenazas en andamiaje |
| Conclusiones | `secciones/05-conclusiones.tex` | Andamiaje |
| Referencias | `bibliografia/referencias.bib` | Vacío (sin citas inventadas) |

## 3. Compilación

```bash
cd memoria
latexmk -pdf main.tex          # recomendado (resuelve el índice e índices cruzados)
# alternativa manual:
# pdflatex main && bibtex main && pdflatex main && pdflatex main
```

Requisitos: una distribución LaTeX estándar (TeX Live o MiKTeX); la clase
`report` y los paquetes usados (`geometry`, `babel`, `listings`, `hyperref`,
`booktabs`, `titlesec`…) son de base. **El índice se rellena en la segunda
pasada**, por lo que conviene `latexmk` (o ejecutar `pdflatex` dos veces).
No se han ejecutado compilaciones en esta entrega; verificar antes de dar
por válido el PDF.

### Encabezados de capítulo

Los capítulos **están numerados** (`\chapter`, no `\chapter*`); esto es
imprescindible para que las secciones se numeren `N.1, N.2…` y no `0.1,
0.2…`. Con `titlesec` (en `main.tex`) se elimina la palabra «Capítulo» y el
encabezado queda compacto: `N  Título`.

- Para mostrar **solo el título** (sin número visible) conservando la
  numeración de secciones, cambiar en `main.tex` la etiqueta del
  `\titleformat{\chapter}` de `{\thechapter}` a `{}`.
- **Importante:** no usar `\chapter*` en los capítulos del cuerpo; deja el
  contador a 0 y las secciones salen `0.x`. `\chapter*` se reserva para el
  Resumen y el Abstract (front matter sin número).

## 4. Reglas de redacción (heredadas de AGENTS.md / CLAUDE.md)

- **No reformular las RQ oficiales.** Reproducirlas literalmente.
- **No inventar bibliografía.** Usar el comando `\pendientecita{}`.
- **No inventar resultados.** Citar solo evidencia de `output/`.
- Marcadores LaTeX disponibles (definidos en `main.tex`): `\pendientecita{}`,
  `\pendienteverif{}`, `\pendientemetodo{}`, `\pendienteredaccion{}`,
  `\hipotesis{}`.

## 5. Avisos metodológicos abiertos (ver secciones)

- **SonarQube:** es **validación de contraste externa**, no núcleo del
  pipeline. El estado real de la corrida está marcado
  `\pendienteverif{}` en `04d-experimento.tex` (el `metadata.json` declara
  que no se ejecutaron escaneos reales en la entrega).
- **Cifras de resultados:** el borrador Markdown previo citaba `n=18`,
  `Δ=−18`; los artefactos vigentes reportan `126` casos y `Δ=−278`.
  **Re-extraer** todas las cifras de `output/` antes de redactar §4.6.
- **RQ3:** distinguir la campaña real (fase 9, OpenAI) del *dry-run* previo
  (fase 8, respuestas grabadas).
