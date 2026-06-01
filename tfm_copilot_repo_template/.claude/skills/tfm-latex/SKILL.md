---
name: tfm-latex
description: Utiliza esta skill al crear, estructurar o compilar la memoria del TFM en LaTeX con formato LNCS (clase llncs), bibliografía y estructura de archivos.
---

# Objetivo
Establecer el andamiaje LaTeX de la memoria del TFM con el formato LNCS recomendado por la guía
de la UMA, y compilarla de forma reproducible.

# Estilo y paquetes
- Clase recomendada: `llncs` (Springer LNCS). Alternativa de estilo libre: `article` + `fullpage`
  para márgenes a página completa.
- Bibliografía: BibTeX con estilo `splncs04` (LNCS) o `biblatex`. No inventes claves ni entradas.
- Paquetes habituales: `inputenc`/`fontenc` (o usar `lualatex`/`xelatex`), `babel` (spanish),
  `graphicx`, `listings` o `minted` (para código Java), `amsmath`, `hyperref`, `booktabs`.

# Estructura de archivos sugerida
```
memoria/
  main.tex            % preámbulo + \input de secciones
  secciones/
    00-resumen.tex
    01-introduccion.tex
    02-antecedentes.tex
    03-problema.tex
    04-propuesta.tex
    05-conclusiones.tex
  bibliografia/referencias.bib
  figuras/
```

# Reglas
- Cubre los contenidos mínimos de la guía (ver CLAUDE.md).
- Inserta `\input{}` por sección para mantener archivos manejables.
- Para fragmentos de código Java usa `listings`/`minted` con `language=Java`.
- Marca pendientes con `% [PENDIENTE ...]` o el comando `\todo{}` (paquete `todonotes`).

# Compilación
- Preferido: `latexmk -pdf main.tex` (o `-lualatex` si se usa `minted`/fuentes).
- Alternativa manual: `pdflatex main` -> `bibtex main` -> `pdflatex main` x2.
- Si falta una herramienta (latexmk, pdflatex, bibtex), indícalo y propón instalación; no
  asumas resultados de compilación que no se hayan ejecutado.

# Salida esperada
- Esqueleto LaTeX compilable, archivos por sección y `.bib` con entradas reales o marcadas como
  pendientes.
