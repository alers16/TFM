# Guía de estilo del autor (voz para la redacción del TFM)

Perfil derivado de una memoria previa del autor (TFG), usada **solo como
referencia de voz y estilo**, no de contenido. Aplíquese al redactar o
revisar cualquier sección de la memoria del TFM (skill `tfm-memory-writing`,
agente `memory-writer`).

## Voz que SÍ imitar

1. **Apertura de capítulo/sección con párrafo-mapa**: anunciar qué se trata
   y en qué orden ("En primer lugar… Posteriormente… Finalmente…").
2. **Conectores secuenciales explícitos** entre ideas (En primer lugar, A
   continuación, Además, Por otro lado, Por último).
3. **Justificar cada decisión técnica**: explicar el *porqué* de cada
   elección, no solo describirla.
4. **Tono didáctico**: definir brevemente cada concepto antes de usarlo;
   lector competente pero no especialista.
5. **Listas "**etiqueta:** explicación"** para componentes, criterios y
   precondiciones.
6. **Cierre de sección con síntesis** que recapitula y enlaza con lo
   siguiente ("En definitiva", "En conjunto").
7. **Orientación al propósito**: conectar cada elemento técnico con su
   utilidad ("lo que permite…", "con el fin de…").
8. **Trazabilidad entre fases**: verbalizar el puente problema → diseño →
   implementación → evaluación.
9. **Voz impersonal SIEMPRE**: usar el "se" impersonal ("se describe", "se
   mide", "se observa"). **NO usar primera persona** —ni plural ("nosotros",
   "planteamos", "medimos", "recorremos") ni singular—. (Rectifica la guía
   anterior, que admitía un "nosotros" puntual.)
10. **Léxico de calidad de ingeniería** con mesura (robusto, modular,
    reproducible, conservador, trazable).

## Defectos que NO reproducir

1. Erratas/typos: revisión ortográfica final impecable.
2. Frases kilométricas con subordinadas encadenadas: partir en oraciones
   cortas y respirables.
3. Muletillas y repeticiones ("permite", "facilita", "en tiempo real");
   variar y eliminar redundancia.
4. Listas de "ventajas" genéricas sin evidencia: toda afirmación anclada a
   evidencia del repo o marcada con `\pendiente…{}`.
5. Placeholders informales ("Foto de…", "Lorem ipsum"): usar etiquetas
   formales del proyecto y figuras con pie.

## Calibración para el TFM (mejor que el TFG)

Más conciso, más maduro y con orientación investigadora: estado del arte,
propuesta con valor añadido y comparación crítica con métodos existentes.
Conservar las marcas personales (párrafo-mapa, justificación, propósito,
síntesis) sin caer en prosa genérica de IA. Respetar siempre las RQ
oficiales y las reglas de no inventar (AGENTS.md, CLAUDE.md).
