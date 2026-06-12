# Revisión de comentarios del tutor (162)

> **Nota de contexto importante.** El PDF revisado por el tutor se compiló **antes**
> de los últimos cambios del repositorio (eliminación de la precondición P5 como
> requisito de corrección, cifras actualizadas 104→111 elegibles y Δ −295→−340,
> reescritura de RQ1/RQ2/RQ3, etc.). Por eso varios comentarios —especialmente los
> de P5 y los de cifras— **ya están resueltos**; se marcan con estado **YA ABORDADO**.
> El texto verbatim de cada comentario está en `revision/comentarios-raw.tsv`.

## Leyenda de categorías y estados

- **MECÁNICO** — formato/terminología/LaTeX seguro de corregir. Estado: `PENDIENTE`.
- **FONDO** — requiere criterio o decisión del autor. Estado: `PENDIENTE-REVISIÓN` (con propuesta abajo).
- **NO-IA** — párrafo telegráfico / “huele a LLM”. **No se reescribe** (lo hace el autor). Estado: `NO-IA`.
- **NO-LOCALIZADO** — ambiguo o sin zona clara. Estado: `NO-LOCALIZADO` (preguntar).
- **YA ABORDADO** — resuelto por los cambios recientes (P5/cifras).

## Tabla de clasificación

| Nº | Pág | Texto señalado (abrev.) | Comentario (resumen) | Categoría | Archivo / zona | Estado |
|---|---|---|---|---|---|---|
| 1 | 2 | — | "¿qué quiere decir esto?" | NO-LOCALIZADO | 00-preliminares (Resumen) | NO-LOCALIZADO |
| 2 | 3 | "…295 points… Regarding the language models" | idem (claridad del abstract) | FONDO | 00-preliminares (Abstract) | PENDIENTE-REVISIÓN |
| 3 | 6 | — | "métodos" | NO-LOCALIZADO | 01-introduccion | NO-LOCALIZADO |
| 4 | 7 | "…sus tres preguntas de investigación." | línea viuda | MECÁNICO | 01-introduccion §1.1 | PENDIENTE |
| 5 | 7 | "de Java responde a tres motivos…" | "¿seguro? No lo tengo tan claro" (justificación de Java) | FONDO | 01-introduccion §1.2 | PENDIENTE-REVISIÓN |
| 6 | 7 | — | "falta texto para presentar las preguntas de investigación" | NO-IA | 01-introduccion §1.4 | NO-IA |
| 7 | 8 | — | "estilo telegráfico… delata uso de IA…" (contribuciones) | NO-IA | 01-introduccion §1.5 | NO-IA |
| 8 | 8 | — | "el tribunal podría suspender… IA" | NO-IA | 01-introduccion §1.5 | NO-IA |
| 9 | 8 | "cubre carga de corpus…" | "cubre **la** carga" | MECÁNICO | 01-introduccion §1.5 | PENDIENTE |
| 10 | 9 | "2 Antecedentes" | "¿son capítulos? parecen secciones" (cabecera muy pequeña) | MECÁNICO | main.tex (\titleformat chapter) | PENDIENTE |
| 11 | 12 | — | "LLMs" | MECÁNICO | 02-antecedentes | PENDIENTE |
| 12 | 12 | — | "extranjerismos en cursiva" | MECÁNICO | 02-antecedentes | PENDIENTE |
| 13 | 12 | — | "falta el TFM de Andrés Juárez (RIUMA)" | FONDO | 02-antecedentes §2.6 | PENDIENTE-REVISIÓN |
| 14 | 13 | "…frente a un baseline determinista." | línea viuda | MECÁNICO | 02-antecedentes | PENDIENTE |
| 15 | 13 | — | "falta el trabajo de Andrés Juárez" | FONDO | 02-antecedentes | PENDIENTE-REVISIÓN |
| 16 | 15 | — | "ejemplo con dos returns booleanos… quizá no es el mejor" | FONDO | 03-problema §3.2 (fig:cc-desglose) | PENDIENTE-REVISIÓN |
| 17 | 15 | — | "¿podría haber más if anidados? ¿se considera?" | FONDO | 03-problema §3.2/§3.5 | PENDIENTE-REVISIÓN |
| 18 | 15 | "colaterales. No se debe a que la combinación…" | "si tiene efectos laterales ¿no sería igual?" | FONDO (P5) | 03-problema §3.3 | YA ABORDADO |
| 19 | 16 | "(cada nivel un único if…)" | "OK, entonces sí se cubre" (positivo) | — | 03-problema §3.5 | SIN ACCIÓN |
| 20 | 16 | — | "se han mencionado dos veces sin presentarlas" (precondiciones) | FONDO | 03-problema §3.5 | PENDIENTE-REVISIÓN |
| 21 | 17 | "y B sin efectos colate-" | "no creo que sea necesario" | FONDO (P5) | 03-problema §3.5 (tab:alcance) | YA ABORDADO |
| 22 | 17 | "Efectos colaterales o asignaciones en las condiciones" | "no veo por qué" | FONDO (P5) | 03-problema (tab:alcance) | YA ABORDADO |
| 23 | 17 | "streams, concurrencia" | "no veo esta restricción" | FONDO (P5) | 03-problema (tab:alcance) | YA ABORDADO |
| 24 | 19 | — | "ensancha las dos primeras columnas, estrecha la tercera" | MECÁNICO | 04-propuesta (tab:mapa-rq) | PENDIENTE |
| 25 | 19 | — | "¿nuevo nombre para RQ1? repetiría la pregunta original" | FONDO | 03b-metodologia §4.2 | PENDIENTE-REVISIÓN |
| 26 | 19 | — | "demasiado esquemático; falta un párrafo" | NO-IA | 03b-metodologia §4.2 | NO-IA |
| 27 | 19 | — | "te digo lo mismo en las demás" | NO-IA | 03b-metodologia | NO-IA |
| 28 | 22 | "las precondiciones P1–P5…" | "¿con efectos colaterales no la preserva?" | FONDO (P5) | 04a §5.2.2 | YA ABORDADO |
| 29 | 22 | — | "¿qué es esto? referencia o explicar" | NO-LOCALIZADO | 04a §5.2 | NO-LOCALIZADO |
| 30 | 22 | "Un flujo alter-" | "else if no existe en el AST, son else (confirma en JavaParser)" | FONDO | 04a §5.2.3 (P1/P4) | PENDIENTE-REVISIÓN |
| 31 | 22 | "." | idem (else-if en AST) | FONDO | 04a §5.2.3 | PENDIENTE-REVISIÓN |
| 32 | 22 | "A o B) contiene: llamadas a método…" | "no la entiendo, no es necesario" | FONDO (P5) | 04a §5.2.3 | YA ABORDADO |
| 33 | 23 | "cualquiera de esas construc-" | "demasiado conservador, se puede eliminar; el tribunal preguntará" | FONDO (P5) | 04a §5.2.3 | YA ABORDADO |
| 34 | 24 | "interno vacío (P4’) y, en P5…" | "¡claro! ¿por qué no?" | FONDO (P5) | 04a §5.2.3 (RELAXED) | YA ABORDADO |
| 35 | 24 | — | "sigo sin encontrar justificada P5" | FONDO (P5) | 04a §5.2.3 | YA ABORDADO |
| 36 | 24 | — | "¿defensiva? suena raro" | MECÁNICO | 04a §5.2.4 (transformador) | PENDIENTE |
| 37 | 24 | — | "¿qué es el aliasing?" | MECÁNICO (glosa) | 04a §5.2.4 | PENDIENTE |
| 38 | 24 | — | "explícalo" (aliasing) | MECÁNICO (glosa) | 04a §5.2.4 | PENDIENTE |
| 39 | 24 | "Cuando alguna condición es una expresión \|\|…" | "¿solo esto? ¿no hay más casos de paréntesis? ¿JavaParser lo controla?" | FONDO | 04a §5.2.4 | PENDIENTE-REVISIÓN |
| 40 | 24 | — | "¿no hay equivalente en español? 'una por una'" (one-at-a-time) | MECÁNICO | 04a §5.2.5 | PENDIENTE |
| 41 | 24 | ": tras" | "aquí lo tienes" | NO-LOCALIZADO | 04a §5.2.5 | NO-LOCALIZADO |
| 42 | 24 | "…MAX_PASSES en BatchRunner…" | "¿qué es todo esto? explicar" | FONDO | 04a §5.2.5 | PENDIENTE-REVISIÓN |
| 43 | 25 | "(STRICT), los casos con else, lambdas…" | "sigo sin ver esto" | FONDO (P5) | 04a §5.2.5 (compromiso) | YA ABORDADO |
| 44 | 28 | — | "muy pequeño, no se lee bien" (figura) | MECÁNICO | 04c §5.4.5 (fig:secuencia-rq3) | PENDIENTE |
| 45 | 28 | "se organiza en las" | "¿esto qué es? explícalo" | NO-LOCALIZADO | 04c §5.4 | NO-LOCALIZADO |
| 46 | 28 | "El código fuente se parsea mediante" | "este verbo no existe; en español, análisis sintáctico" | MECÁNICO | 04c §5.4 | PENDIENTE |
| 47 | 28 | "…inicial del método antes de aplicar" | "¿previa? ¿antes?" | MECÁNICO | 04c §5.4 | PENDIENTE |
| 48 | 28 | "la política one-at-a-time…" | "español" | MECÁNICO | 04c §5.4 | PENDIENTE |
| 49 | 29 | "after. Una vez finalizado…" | "¿Medición posterior?" | MECÁNICO | 04c §5.4 | PENDIENTE |
| 50 | 29 | "CampaignExecutor y LlmCampaignRunner" | "explicar qué es esto" | MECÁNICO (glosa) | 04c §5.4.5 | PENDIENTE |
| 51 | 30 | — | "falta texto para explicar lo que viene" | NO-IA | 04c §5.4.5 | NO-IA |
| 52 | 31 | "…las cinco categorías descritas en 5.2.3." | "¿los pre/post-incrementos son asignaciones? creo que no" | FONDO (P5) | 04c §5.4.5 | YA ABORDADO |
| 53 | 31 | — | "no hay referencia a la especificación de Java; debería justificar las precondiciones en ella" | FONDO | global (02/03/04a) + referencias.bib | PENDIENTE-REVISIÓN |
| 54 | 31 | "para no dejar el AST inconsistente;" | "¿qué hace y cómo contribuye?" | MECÁNICO (glosa) | 04c §5.4.2 | PENDIENTE |
| 55 | 31 | "; normalización del cuerpo" | "¿único caso? consultar especificación Java" | FONDO | 04c §5.4.2 | PENDIENTE-REVISIÓN |
| 56 | 31 | — | "¿defensiva? mala traducción" | MECÁNICO | 04c §5.4.2 | PENDIENTE |
| 57 | 31 | ", switch" | "cambias estilo de do-while pero no for-each ¿por palabras clave?" | MECÁNICO | 04c §5.4.3 (lista CC) | PENDIENTE |
| 58 | 31 | "o" | "&&/\|\| vs &/\|: cortocircuito; no lo mencionas, llamativo" | FONDO | 04c/04a | PENDIENTE-REVISIÓN |
| 59 | 31 | — | "¿qué hace la CC de SonarSource con &/\|?" | FONDO | 04c §5.4.3 | PENDIENTE-REVISIÓN |
| 60 | 31 | "detección de recursión (que" | "¿por qué no?" | FONDO | 04c §5.4.3 | PENDIENTE-REVISIÓN |
| 61 | 31 | — | "justificar" (recursión) | FONDO | 04c §5.4.3 | PENDIENTE-REVISIÓN |
| 62 | 32 | "/else if/else" | "pones dos veces lo mismo; une else-if con guión" | MECÁNICO | 04c §5.4.3 | PENDIENTE |
| 63 | 32 | "/continue" | "operador ternario" (formato/ítem en la lista) | MECÁNICO | 04c §5.4.3 | PENDIENTE |
| 64 | 32 | "etiquetados, if, clase anónima…" | "expresión lambda" (ítem en la lista) | MECÁNICO | 04c §5.4.3 | PENDIENTE |
| 65 | 32 | "clase anónima y clase local…" | "no entiendo por qué no lo incluyes" | FONDO | 04c §5.4.3 | PENDIENTE-REVISIÓN |
| 66 | 33 | "del prompt: un system…" | "¿por qué no exiges P5 al LLM? limitas tu sistema pero no a los competidores" | FONDO (P5↔LLM) | 04c §5.4.5 | YA ABORDADO |
| 67 | 33 | "y gpt-4.1" | "¿no es poco? ¿podría haber ifs más largos? ¿lo has comprobado?" | FONDO | 04c §5.4.5 / 04d | PENDIENTE-REVISIÓN |
| 68 | 33 | — | "justifica este valor en base a datos" (parámetro) | FONDO | 04c §5.4.5 / 04d | PENDIENTE-REVISIÓN |
| 69 | 33 | "API de OpenAI… dry runs y tests" | "¿qué es esto? no está explicado" | MECÁNICO (glosa) | 04c §5.4.5 (provider) | PENDIENTE |
| 70 | 34 | "…contenga al menos un método…" | "¿a nivel de método o de sentencia? lo segundo sería razonable" | FONDO | 04c §5.4.5 (oráculo) | PENDIENTE-REVISIÓN |
| 71 | 34 | — | "¿firma? ¿signatura?" | MECÁNICO | 04c §5.4.5 | PENDIENTE |
| 72 | 34 | — | "¿no pones la letra griega?" (Δ) | MECÁNICO | 04c §5.4.5 | PENDIENTE |
| 73 | 34 | ": si el baseline declara…" | "al LLM solo le exiges P1–P4, ¿por qué lo frenas? P5 inconsistente" | FONDO (P5↔LLM) | 04c §5.4.5 (guarda v1.1) | YA ABORDADO |
| 74 | 34 | "caso no elegible (P1–P5)… INCORRECT" | "¿qué quiere decir? explicar mejor" | FONDO (P5) | 04c §5.4.5 | YA ABORDADO |
| 75 | 35 | "δ… baseline" | "antes mayúscula, ahora minúscula ¿por qué?" | MECÁNICO | 04c (tab:oraculo) | PENDIENTE |
| 76 | 35 | "∧ δ≠0 → INCORRECT" | "el LLM puede transformar correcto y le dices que mal" | FONDO (P5↔LLM) | 04c (tab:oraculo) | YA ABORDADO |
| 77 | 35 | — | "¿igual o mayor? está poniendo menor" | MECÁNICO | 04c (tab:oraculo) | PENDIENTE |
| 78 | 36 | "--mode=live… --mode=dry-run" | "¿qué es esto? no recuerdo que lo menciones" | MECÁNICO (glosa) | 04c §5.4.5 | PENDIENTE |
| 79 | 36 | — | "en dry-run ¿cómo decides el dato?" | FONDO | 04c §5.4.5 | PENDIENTE-REVISIÓN |
| 80 | 37 | — | "usa el término en español" | MECÁNICO | 04d cap.6 | PENDIENTE |
| 81 | 37 | "protocolos deliberadamente congelados…" | "¿qué quieres decir con 'congelados'?" | MECÁNICO | 04d cap.6 intro | YA ABORDADO (parcial) |
| 82 | 37 | "mediante PilotCorpusLoader" | "¿classpath? debería ejecutarse sobre una carpeta del corpus" | FONDO | 04d §6.1 | PENDIENTE-REVISIÓN |
| 83 | 37 | "src/main/resources/pilot-corpus/)" | "¡esto no debería estar ahí! cutre" | FONDO | 04d §6.1 | YA ABORDADO (ruta retirada) |
| 84 | 37 | "src/main/resources/real-corpus/)" | "idem" | FONDO | 04d §6.1 | YA ABORDADO (ruta retirada) |
| 85 | 37 | "El corpus real está formado por…" | "separa la descripción de la cabecera con dos puntos" | MECÁNICO | 04d §6.1 | YA ABORDADO |
| 86 | 37 | "@project, @file…" | "¿qué son estas anotaciones? coméntalo" | MECÁNICO (glosa) | 04d §6.1 | PENDIENTE |
| 87 | 38 | "El escaneo como censo del patrón." | "¿qué es esto? mala traducción" | MECÁNICO | 04d §6.1 | YA ABORDADO (cabecera retirada) |
| 88 | 38 | — | "separador de miles: punto vs espacio, sé consistente" | MECÁNICO | global (04d/04e) | PENDIENTE |
| 89 | 38 | — | "falta texto introduciendo lo que vas a presentar" | NO-IA | 04d §6.2 | NO-IA |
| 90 | 38 | "Δ negativo… Métrica complementaria…" | "¿no debería ser itemize/enumerate?" | MECÁNICO (estructura) | 04d §6.2 | PENDIENTE |
| 91 | 38 | — | "todo este párrafo: reescritura, telegráfico (huele a LLM)" | NO-IA | 04d §6.2 | NO-IA |
| 92 | 39 | — | "muy corto, telegráfico" | NO-IA | 04d §6.3 | NO-IA |
| 93 | 39 | "es la composi-" | "esto no se entiende" | NO-IA | 04d §6.3 | NO-IA |
| 94 | 39 | — | "ando perdido; la sección necesita coherencia y orden" | NO-IA | 04d cap.6 | NO-IA |
| 95 | 39 | — | "no es necesario PowerShell: línea de comandos" | MECÁNICO | 04d §6.4 | PENDIENTE |
| 96 | 39 | "goal mvn sonar:sonar… docker-compose…" | "primera mención a docker, muy abrupta; explicar" | FONDO | 04d §6.4 | PENDIENTE-REVISIÓN |
| 97 | 39 | — | "¿qué quieres decir con 'congelado'?" | MECÁNICO | 04d §6.5 | PENDIENTE |
| 98 | 39 | "dispone de una única clave de API…" | "tenemos APIs de Mistral, DeepSeek y Claude" (multiproveedor) | FONDO | 04d §6.5 | PENDIENTE-REVISIÓN |
| 99 | 40 | — | "¿por qué señalas la figura aquí? no has terminado la enumeración" | MECÁNICO | 04d §6.5 | PENDIENTE |
| 100 | 40 | "incluye" | "explícalo mejor" | NO-IA | 04d §6.5 | NO-IA |
| 101 | 40 | "modelos, temperatura, intentos…" | "frases crípticas; reescribir" | NO-IA | 04d §6.5 | NO-IA |
| 102 | 40 | "Las garantías se sostienen…" | "¿enumeración? muy telegráfico: reescribir" | NO-IA | 04d §6.6 | NO-IA |
| 103 | 41 | — | "espacio como separador de miles, antes punto: inconsistente" | MECÁNICO | global (04e) | PENDIENTE |
| 104 | 42 | — | "¿qué P* es esta?" | NO-LOCALIZADO | 04e §RQ1 | NO-LOCALIZADO |
| 105 | 42 | "el bloqueo proviene sobre todo de P5…" | "yo pienso que no debería descartarse" | FONDO (P5) | 04e §RQ1 | YA ABORDADO |
| 106 | 42 | "…la decisión de tratar P5 conservadora" | "no creo que lo respalde" | FONDO (P5) | 04e §RQ1 | YA ABORDADO |
| 107 | 43 | "…criterio de Tukey…" | "describe el criterio en nota al pie" | MECÁNICO | 04e §RQ2 (fig:boxplots) | PENDIENTE |
| 108 | 43 | — | "punto" (puntuación) | MECÁNICO | 04e §RQ2 | PENDIENTE |
| 109 | 43 | "1,5·IQR" | "mejor al pie" | MECÁNICO | 04e §RQ2 | PENDIENTE |
| 110 | 43 | — | "esto no debería ir entre paréntesis, es importante" | MECÁNICO | 04e §RQ2 | PENDIENTE |
| 111 | 43 | "…se muestra en la Figura 7.2." | "una figura no debe referenciar otra futura; mejor en el texto" | MECÁNICO | 04e §RQ2 (fig:boxplots caption) | PENDIENTE |
| 112 | 43 | "masa" | "mejor 'la distribución'" | MECÁNICO | 04e §RQ2 (fig:hist-delta) | PENDIENTE |
| 113 | 43 | "que baja de 52 a 38 (Δ=−14)" | "expresión poética, poco científica" | MECÁNICO | 04e §RQ2 | PENDIENTE-REVISIÓN |
| 114 | 44 | "− −" | "¿columna centrada? debería ir a la derecha" | MECÁNICO | 04e (tab:rq2-destacados) | PENDIENTE |
| 115 | 44 | — | "esta tabla se sale del borde" | MECÁNICO | 04e (tab:rq3-completo) | PENDIENTE |
| 116 | 44 | — | "si no tienen CC ¿por qué están entre los elegibles?" | FONDO | 04e §sonar-val | PENDIENTE-REVISIÓN |
| 117 | 45 | — | "no tengo claro por qué hay métodos sin CC" | FONDO | 04e | PENDIENTE-REVISIÓN |
| 118 | 45 | — | "esto hay que repararlo o explicarlo bien" | FONDO | 04e | PENDIENTE-REVISIÓN |
| 119 | 45 | — | "repetición de expresión" | MECÁNICO | 04e §RQ3 | PENDIENTE |
| 120 | 45 | — | "operador ternario" | MECÁNICO | 04e §RQ3 | PENDIENTE |
| 121 | 45 | — | "¿por el límite de tokens?" (INVALID_OUTPUT) | FONDO | 04e §RQ3 | PENDIENTE-REVISIÓN |
| 122 | 45 | — | "te quedaste corto; el nº de tokens no está justificado" | FONDO | 04e §RQ3 / 04d | PENDIENTE-REVISIÓN |
| 123 | 45 | — | "los mismos números que arriba; funde la Tasa de éxito en la tabla anterior" | MECÁNICO | 04e (tab:rq3-modelo) | PENDIENTE |
| 124 | 45 | — | "falta el mensaje, debería empezar aquí" | NO-LOCALIZADO | 04e | NO-LOCALIZADO |
| 125 | 46 | — | "cuidado con la alineación; usa \\phantom abajo" | MECÁNICO | 04e (tab:rq3-elegibilidad) | PENDIENTE |
| 126 | 46 | "gpt-4o es conservador: rechaza…" | "dos puntos dentro de dos puntos" | MECÁNICO | 04e §RQ3 | PENDIENTE |
| 127 | 46 | — | "dale la vuelta a la figura: 3 etiquetas en X (éxito/abstención/errores), 2 columnas" | MECÁNICO | 04e (fig:rq3-verdictos) | PENDIENTE-REVISIÓN |
| 128 | 46 | — | "¿por qué se abstiene? ¿qué cuenta como abstención? explícalo" | FONDO | 04e §RQ3 | PENDIENTE-REVISIÓN |
| 129 | 46 | "…la fracción de casos elegibles que transforma" | "'recall' → 'sensibilidad' en español" | MECÁNICO | 04e §RQ3 | PENDIENTE |
| 130 | 47 | "…s.isEmpty()… que P5 rechaza…" | "incluso si tiene efectos colaterales" | FONDO (P5) | 04e (validación humana) | YA ABORDADO |
| 131 | 48 | — | "mejor 'Discusión' (a secas)" | MECÁNICO | 04e §discusión | PENDIENTE |
| 132 | 48 | "Un modelo peca de…" | "¿has comprobado equivalencia semántica del código del LLM?" | FONDO | 04e §discusión | PENDIENTE-REVISIÓN |
| 133 | 48 | "…0,26 s (del orden de 2 ms)…" | "ponlo en milisegundos para comparar con los LLM" | MECÁNICO | 04e §discusión | PENDIENTE |
| 134 | 49 | — | "podemos ver el coste económico; calcúlalo con los datos devueltos" | FONDO | 04e §discusión | PENDIENTE-REVISIÓN |
| 135 | 50 | — | "no entiendo por qué no se ha implementado" (recursión) | FONDO | 04f / 04c | PENDIENTE-REVISIÓN |
| 136 | 50 | "Determinismo del transformador validado por tests…" | "muy telegráfico, reescribir" | NO-IA | 04f §validez interna | NO-IA |
| 137 | 50 | — | "¿qué es esto?" | NO-LOCALIZADO | 04f | NO-LOCALIZADO |
| 138 | 50 | — | "¿qué significa esto?" | NO-LOCALIZADO | 04f | NO-LOCALIZADO |
| 139 | 51 | "Congelación del protocolo…" | "reescribir: es una enumeración" | NO-IA | 04f §mitigaciones | NO-IA |
| 140 | 52 | "…(precondición P5)…" | "no lo justifica; justifica la NO presencia de P5" | FONDO (P5) | 05-conclusiones §RQ1 | YA ABORDADO |
| 141 | 52 | — | "hablas de coste en tiempo pero no de dinero" | FONDO | 05-conclusiones | PENDIENTE-REVISIÓN |
| 142 | 52 | — | "P5 no lo veo justificado" | FONDO (P5) | 05-conclusiones | YA ABORDADO |
| 143 | 53 | — | "hablabas de una versión relajada de precondiciones; no vi su evaluación" | FONDO | 05 / 04a (RELAXED) | PENDIENTE-REVISIÓN |
| 144 | 53 | "…tesis doctoral…" | "demasiado para algo tan sencillo" | FONDO | 05 §trabajo futuro | PENDIENTE-REVISIÓN |
| 145 | 53 | "anidados, condicionales con" | "¿esto no lo hace ya tu aplicación?" | FONDO | 05 §trabajo futuro | PENDIENTE-REVISIÓN |
| 146 | 53 | "Relajar P5 con análisis semántico…" | "P5 no debería estar siquiera" | FONDO (P5) | 05 §trabajo futuro | YA ABORDADO |
| 147 | 53 | "Completar el modelo con detección de recursión…" | "debería estar hecho aquí, es sencillo" | FONDO | 05 / 04c | PENDIENTE-REVISIÓN |
| 148 | 53 | — | "LLMs" | MECÁNICO | 05-conclusiones | PENDIENTE |
| 149 | 54 | — | "más que 'determinista', yo lo llamaría 'formal'" | FONDO | global (terminología) | PENDIENTE-REVISIÓN |
| 150 | 55 | "…arXiv:2411.02320; ACM TOSEM doi:10.1145/3801158" | "pon la referencia de TOSEM" | MECÁNICO | referencias.bib (cordeiro2024) | PENDIENTE |
| 151 | 55 | — | "106" (formato de página en una ref) | NO-LOCALIZADO | referencias.bib | NO-LOCALIZADO |
| 152 | 55 | — | "¿por qué este salto en la indentación?" | MECÁNICO | referencias.bib | PENDIENTE-REVISIÓN |
| 153 | 55 | "LNCS vol. 6601 … doi …19861-87" | "corrige esto" (DOI partido — ref JPure) | MECÁNICO | referencias.bib | YA ABORDADO (descitada) |
| 154 | 56 | "14" | "corrige esto" (DOI …19861-8_14 — ref Sălcianu) | MECÁNICO | referencias.bib | YA ABORDADO (descitada) |
| 155 | 56 | "https://le…" | "es un libro, no pongas URL, falta editor" | MECÁNICO | referencias.bib (smith2023javaparser) | PENDIENTE |
| 156 | 57 | "Genera el classpath y ejecuta… java -cp…" | "debe darse un directorio con el corpus, no así" | FONDO | 99-apendices (Ap. A) | PENDIENTE-REVISIÓN |
| 157 | 58 | ":" | "¿de dónde toma las respuestas grabadas? no se dice" | MECÁNICO (aclaración) | 99-apendices | PENDIENTE |
| 158 | 59 | — | "describe mejor lo que presentas aquí" | NO-IA | 99-apendices | NO-IA |
| 159 | 63 | "…status; $s }" | "esto puede estar fuera del bucle, ¿no?" | FONDO (código) | 99-apendices (script) | PENDIENTE-REVISIÓN |
| 160 | 64 | — | "ejecución" | MECÁNICO | 99-apendices | PENDIENTE |
| 161 | 64 | "…La limpieza del servidor…" | "esto no es una limitación" | MECÁNICO (reubicar) | 99-apendices / 04f | PENDIENTE-REVISIÓN |
| 162 | 65 | — | "haz la tabla más ancha para evitar tantas líneas" | MECÁNICO | 99-apendices | PENDIENTE |

## Propuestas para los FONDO (estado PENDIENTE-REVISIÓN)

Estado de cada propuesta: 🟢 LISTO (bajo riesgo, alineado con las reglas; aplico al aprobar) ·
🟡 DECISIÓN (elige opción) · 🔵 DATOS/CÓDIGO (requiere inspeccionar artefactos o tocar código,
posible re-ejecución). **Ninguna está aplicada todavía.**

### A. Trabajo antecedente de Andrés Juárez (13, 15) — 🟡/🔵
Añadir en §2 (antecedentes) un párrafo que sitúe su TFM (transformación
relacionada pero distinta) y una entrada bibliográfica. **No tengo sus datos
(título exacto, año, URL RIUMA/arXiv) y no los invento.** Necesito que me pases
la referencia (o autorización para buscarla en RIUMA y que tú la confirmes).

### B. Especificación del lenguaje Java como fuente (53, 55) — 🟢
Añadir la *Java Language Specification* (SE 17) como referencia real y citarla
donde se justifican (1) el cortocircuito de `&&` (§3.3/§5.2.2) y (2) la
normalización del cuerpo a `BlockStmt` (§5.4.2). Entrada propuesta:
`@misc{jls17, author={Gosling, J. and Joy, B. and Steele, G. and Bracha, G. and Buckley, A. and Smith, D. and Bierman, G.}, title={The Java Language Specification, Java SE 17 Edition}, year={2021}, publisher={Oracle}, howpublished={\url{https://docs.oracle.com/javase/specs/}}}`.
Es fuente verificable; la cita refuerza las decisiones sin inventar nada.

### C. Operadores `&&`/`||` vs `&`/`|` (58, 59) — 🟢/🔵
(58) Añadir un párrafo breve (§2 o §5.2.2): Java tiene dos parejas de operadores
binarios lógicos; `&&`/`||` evalúan en cortocircuito y `&`/`|` no, y la
transformación se apoya en el cortocircuito. (59) "¿qué hace la CC de SonarSource
con `&`/`|`?": **requiere verificar** nuestro `CognitiveComplexityCalculator` y
el modelo de SonarSource (creo que la regla de operadores lógicos cuenta `&&`/`||`;
hay que confirmar el trato de `&`/`|`). Propongo: redacto el párrafo de (58) y, para
(59), inspecciono el calculador y lo documento con precisión.

### D. Detección de recursión (60, 61, 135, 147) — 🟡 (decisión importante)
El tutor dice que es la única regla de SonarSource no implementada y que "debería
estar hecha aquí". Dos caminos:
  - **(D1, recomendado) Implementarla**: añadir `+1` al detectar que un método se
    llama a sí mismo en `CognitiveComplexityCalculator`, re-ejecutar la validación
    contra SonarQube y RQ2. Si en el corpus no hay métodos recursivos, los números
    no cambian, pero se cierra la limitación. Es código + re-validación.
  - **(D2) Justificar la ausencia**: argumentar que no hay recursión en el corpus
    (sin divergencias) y dejarla como única regla pendiente.
Mi recomendación: D1 (el tutor lo pide y elimina una crítica fácil del tribunal).

### E. Multiproveedor de LLM (98, y relacionados 67, 68, 143) — 🟡 (decisión importante)
El tutor ofrece APIs de Mistral, DeepSeek y Claude. Opciones:
  - **(E1) Reformular el alcance**: quitar el argumento de "única clave financiada"
    y justificar el dúo gpt-4o/gpt-4.1 como decisión de alcance (mismo proveedor,
    comparación capacidad-vs-cautela). Bajo esfuerzo.
  - **(E2) Ampliar el experimento**: integrar ≥1 proveedor más y re-ejecutar RQ3.
    Refuerza mucho RQ3 pero es un experimento nuevo (integración + coste + tiempo).
Relacionado: 67 (¿ifs más largos?), 68 (justificar nº de intentos/tokens con datos),
143 (evaluación de la versión relajada de precondiciones — ahora mismo no aparece).
Necesito tu decisión sobre E1 vs E2 y sobre si se evalúa la variante relajada.

### F. Equivalencia semántica de la salida del LLM (132) — 🟢
Aclarar en la discusión de RQ3 que "correcto" para el LLM significa
*correcto según el oráculo* (cumple P1–P4 + `Δ` esperado + compila), **no**
equivalencia conductual verificada con tests (eso excede el alcance, ya señalado
en el criterio de corrección §3b y en amenazas). Propongo 1–2 frases de matización
+ referencia cruzada a la limitación. ¿Las redacto yo o prefieres escribirlas?

### G. Coste económico de los LLM (134, 141) — 🔵
Calcular el coste a partir del uso de tokens registrado en las evidencias de
`output/rq3-*`. **Requiere** (1) confirmar que los JSON guardan `usage`
(prompt/completion tokens) y (2) precios por token de gpt-4o/gpt-4.1 (dato externo;
lo tomaría de la tarifa oficial y lo marcaría con fecha, sin inventar). Resultado:
una fila/oración en `tab:comparativa-coste`. Si apruebas, inspecciono los artefactos.

### H. Unidad de análisis: método vs sentencia (70) — 🟢
Aclarar en §5.4.5 que la unidad es el **método** (cada `ExperimentCase` = un método)
y que la transformación actúa sobre las sentencias condicionales anidadas dentro de
él. Es una aclaración del diseño existente, no un cambio.

### I. Métodos "elegibles" con CC = 0 (116, 117, 118) — 🔵
El tutor no entiende por qué hay métodos con CC = 0 entre los elegibles.
**Requiere inspeccionar los datos** (qué casos muestran CC = 0 y por qué se marcan
elegibles; sospecho relación con la medición del proxy en ciertos casos de fastjson).
Propongo investigarlo y, según el hallazgo, corregir el dato o explicarlo en el texto.

### J. Corpus como carpeta, no en el classpath (82, 156, 157) — 🟡/🔵
El tutor objeta cargar el corpus desde `src/main/resources` vía classpath.
Opciones: **(J1)** documentar cómo apuntar a una carpeta configurable; **(J2)**
refactorizar `PilotCorpusLoader`/`BatchRunner` para aceptar un directorio como
argumento (más trabajo, más limpio). 157: además, explicar de dónde toma las
respuestas grabadas el modo dry-run. Necesito decisión J1 vs J2.

### K. Repetir el enunciado literal de las RQ (25) — 🟢
En la operacionalización (§4.2) sustituir las etiquetas "RQ1 — Alcance operativo"
por el **enunciado literal** de cada RQ (alineado con la regla de no reformular las
RQ), manteniendo debajo los puntos de operacionalización. Listo para aplicar.

### L. `else if` en el AST (30, 31) — 🟢/🔵
Aclarar en §5.2.3 que una rama `else if` es, en el AST, un `else` cuyo cuerpo es un
`if`, y que la precondición descarta cualquier `else` (incluido el `else if`).
**Verifico** en el detector que ese caso se trata así y ajusto la redacción.

### M. Parentesización al combinar (39) — 🟢/🔵
Aclarar la regla: se envuelve una condición en `EnclosedExpr` cuando es un `||`
para preservar la precedencia bajo `&&`; los demás casos no lo necesitan.
**Verifico** la lógica del transformador y lo documento con precisión.

### N. Ejemplo de la descripción del problema (16, 17) — 🟡
(16) El tutor sugiere cambiar el ejemplo de `cc-desglose` (usa dos `return`
booleanos, eliminables) por uno que **realice una acción** dentro de los `if`
anidados. Puedo proponerte código Java nuevo para el ejemplo (lo apruebas tú).
(17) Añadir una frase aclarando que las cadenas de N `if` sí se contemplan
(referencia a §3.5/RQ1).

### O. Abstención: definición (128) — 🟢
Aclarar qué cuenta como "abstención" (el modelo declina transformar / responde que
no es elegible) frente a "incorrecto". 1 frase en RQ3.

### P. Claridad del resumen y de menciones tempranas (2, 20) — 🟢
(2) Reescribir/aclarar la frase del abstract sobre los perfiles de los LLM.
(20) Las precondiciones se mencionan antes de presentarse: añadir una referencia
cruzada a §5.2.3 en la primera mención. (Parte de esto es prosa: dime si la redacto.)

### Q. Tono "tesis doctoral" y trabajo futuro (144, 145, 149) — 🟡
(144) Atenuar la frase que enmarca el trabajo futuro como tesis doctoral.
(145) Aclarar que las líneas futuras no duplican el alcance actual.
(149) Terminología: el tutor prefiere "formal" antes que "determinista" para el
prototipo. Es una decisión de marco: ¿cambio "determinista"→"formal" en todo el
documento, lo combino ("determinista y formal"), o lo dejo? Necesito tu criterio.

### R. Justificaciones de RQ3 con datos (67, 68, 121, 122) y docker (96, 79) — 🔵
(121, 122) Las salidas `INVALID_OUTPUT`: confirmar si se deben al límite de tokens
y, si es así, justificar el valor de `max_tokens` con datos (o ampliarlo y re-ejecutar).
(67) ¿`if` más largos? (68) justificar nº de intentos/tokens. (96) explicar el uso de
docker (SonarQube) antes de mencionarlo; (79) cómo se elige el dato en dry-run.
**Requiere inspeccionar artefactos** y, en algún caso, decisión de re-ejecutar.

### Comentario positivo sin acción
19 (el tutor confirma que las cadenas sí se cubren).

## NO-IA (los reescribes tú; no los toco)

6, 7, 8, 26, 27, 51, 89, 91, 92, 93, 94, 100, 101, 102, 136, 139, 158.

## NO-LOCALIZADO (necesito que me indiques a qué se refieren)

1, 3, 29, 41, 45, 104, 124, 137, 138, 151.

---

# Registro de aplicación — lotes mecánicos (sesión 2026-06-12)

> Sin commits (a petición del autor): solo ediciones en el árbol de trabajo.
> PowerShell **no** se ha tocado (95). `LLM`→`LLMs` aplicado solo en usos
> plurales; se mantiene singular "el/un LLM" y la definición "(LLM)".

## APLICADOS

- **Lote 1 — léxico/anglicismos.**
  - `parse*` (verbo inexistente) → análisis sintáctico: 04b, 04c (×4), 04e, 99 (com. 46).
  - `recall` → `sensibilidad`: 04e (129).
  - `one-at-a-time` → "una por una" en prosa (se mantiene solo en `\label`/`\autoref`): 04d (×2), 04f (40, 48).
  - "Discusión integrada" → "Discusión": 04e (131).
  - `LLM` → `LLMs` (plurales): 02, 03b, 04b, 04e, 04f, 05, 99 (11, 148).
- **Lote 2 — separadores de miles.** `1.000` → `1\,000` en 04d (×2), unificado al espacio fino del resto (88, 103).
- **Lote 3 — estilo de la lista de CC (04c).** `for-each` siempre en `\lstinline` (como `do-while`); `else if` → `else-if` (57, 62, 64-parcial).
- **Lote 4 — figuras y notas al pie (04e).** Criterio de Tukey/`1,5·IQR` movido a **nota al pie** anclada en el texto; referencia cruzada a la figura futura retirada de la leyenda y puesta en el texto con `\autoref`; "La masa" → "La mayoría de los casos" (107, 109, 111, 112).
- **Lote 5 — cabecera / unidades / bibliografía.**
  - Cabecera de capítulo `\Large` → `\huge` en `main.tex` (10).
  - Latencia del prototipo en ms (`0,26 s` → `260 ms`) en 04e (×2) (133).
  - `smith2023javaparser`: eliminada la `url` del libro (155).
  - `cordeiro2024`: `@misc`(arXiv) → `@article`(ACM TOSEM) con DOI `10.1145/3801158` (150). **Verifica volumen/páginas/año al consultar la publicación.**

## YA ABORDADO (verificado, sin acción)

- "Medición previa/posterior" ya en español (47, 49); "defensiva" ya eliminada (36, 56); "censo" ya en prosa (87); "cubre la carga" ya correcto (9); 04b ya usa "transforma" no "parsea" (46-parcial); frase con dos puntos anidados ya reescrita (126); y todos los de P5/cifras (ver tabla).

## DIFERIDOS — requieren verlo al compilar (te dejo instrucción)

- **4, 14** líneas viudas (dependen de paginación).
- **24** anchos de columna; **114** alinear columna numérica a la derecha; **115, 162** tablas que se salen del margen; **125** `\phantom`; **123** fundir las dos tablas RQ3; **127** rediseñar la figura de veredictos (3 etiquetas en X); **44** figura demasiado pequeña; **152** salto de indentación en una referencia.

## DIFERIDOS — requieren prosa tuya (no la redacto por el criterio anti-IA)

Glosas explicativas: ~~37, 38~~ aliasing (04a §5.2.4) · ~~42~~ `MAX_PASSES` (04a §5.2.5) ·
~~50/45~~ `CampaignExecutor`/`LlmCampaignRunner` (04b) · ~~54~~ clonación (04c §5.4.2) ·
~~86~~ anotaciones `@project…` (04d §6.1) — **todas HECHAS**.
Pendientes solo las de **dry-run** (por tu indicación): **69** provider/dry-run y
**78** `--mode live`/`dry-run`. (90 itemize y 157/158 ya hechos antes.)

## DIFERIDOS — ambiguos / ubicación imprecisa (dime y los hago)

- **72** letra griega Δ; ~~**75** mayúscula/minúscula `δ`/`Δ`~~ (HECHO: Δ=prototipo/baseline, δ=LLM, en tabla+leyenda+prosa); ~~**77** oráculo "igual o mayor/menor"~~ (HECHO: 04c tab:oraculo → "reduce al menos tanto como el baseline"); **80** término en español (p37); **108, 110, 119, 120, 160, 161**; **71** `firma`/`signatura` (dejado: "firma" es válido).

---

# Registro de aplicación — bloque verde FONDO (sesión 2026-06-12)

## APLICADOS

- **B** (53, 55) — añadida entrada `@misc{jls17}` (Java Language Specification SE 17) y citada en el cortocircuito de `&&` (04a §5.2.1).
- **K** (25) — enunciado **literal** de RQ1/RQ2/RQ3 en los `\paragraph` de operacionalización (03b §4.2).
- **L** (30, 31) — aclarado, verificado contra el código, que un `else if` es en el AST un `else` cuyo cuerpo es un `IfStmt` y que `hasElseBranch()` cubre ambos (04a §5.2.3).
- **C-58/59** (58, 59) — nota al pie: Java distingue `&&`/`||` (cortocircuito) de `&`/`|` (sin cortocircuito); el modelo de CC y el proxy solo cuentan los primeros (verificado en `CognitiveComplexityCalculator`) (04c §5.4.3).
- **H** (70) — nota al pie: la unidad de análisis es el método; detección y transformación operan sobre las sentencias anidadas que contiene (04c §5.4.5).
- **O** (128) — nota al pie que define "abstención" (δ=0 en caso elegible) frente a "incorrecta" (04e §RQ3).
- **F** (132) — nota al pie: "correcta" para el LLM = firma + Δ esperado + compila según el oráculo; **no** equivalencia conductual por tests (excede el alcance) (04e §Discusión).
- **P-20** (20) — referencia cruzada `\autoref{sec:precondiciones}` en la primera mención (leyenda fig:before-after, 03-problema).

## HALLAZGO (no era simple aclaración)

- **M** (39) — el transformador solo parentiza condiciones `||` (`NestedIfTransformer.parenthesizeIfNeeded`). El tutor tiene razón: una condición ternaria `a?b:c` o una asignación (menor precedencia que `&&`, no rechazadas en STRUCTURAL) se imprimirían mal parentizadas. **Decisión pendiente:** arreglar el código (parentizar siempre que la precedencia sea menor que `&&`) o documentarlo como limitación. → pasa a 🟡.

---

# Registro de aplicación — decisiones 🟡 aprobadas (sesión 2026-06-12)

> Decisiones del autor: **2** justificar recursión · **3** multiproveedor olvidado
> (no da tiempo) · **4** refactorizar corpus · **5** ejemplo sí · **6** sí.
> La verificación de compilación del refactor la hace el autor en otro equipo.

## APLICADOS

- **D — recursión (60, 61, 135, 147).** Justificada su ausencia en §5.4.3 (04c): el
  100\,% de coincidencia con SonarQube sobre el corpus completo confirma que ningún
  método activa esa regla; implementarla sería trivial pero no altera los resultados.
  (No implementada, por decisión.)
- **J — corpus desde directorio (82, 156).** Refactor **aditivo y compatible**:
  - `PilotCorpusLoader` y `RealDatasetLoader`: nuevos `loadFromDirectory(Path)` /
    `loadFromPath(Path)` + `listJavaFiles` (lectura del sistema de archivos).
  - `Rq2StructuralBatchExecutor`: 2.º argumento opcional = directorio de corpus
    (`<dir>/pilot-corpus`, `<dir>/real-corpus`); sin él, classpath como antes.
  - Memoria: §6.1 (04d) y comando del apéndice A.4 (99) actualizados (al ejecutor
    estructural + opción de directorio).
  - **Pendiente: que el autor compile/verifique en su equipo.**
- **N — ejemplo (16, 17).** `fig:cc-desglose` (03-problema): ejemplo cambiado a uno
  no booleano que ejecuta una acción (`procesarPedido`/`enviar(p)`), CC 3→2 intactos;
  + frase sobre cadenas de N `if` con `\autoref{sec:alcance}`.
- **Q — marco (144, 145, 149).**
  - 144: atenuado el "programa de investigación / tesis doctoral" → "líneas de
    investigación futuras" (05).
  - 145: corregido el trabajo futuro: las cadenas de N `if` **ya las combina** el
    prototipo; lo pendiente es `else if` y otras consolidaciones (05).
  - 149: "un procedimiento determinista" → "un procedimiento **formal** y
    determinista" en la conclusión (05). *No se hizo rename global: "determinista"
    es exacto y contrasta con el no-determinismo de los LLM; dime si quieres más.*

## OLVIDADO (por decisión)

- **E — multiproveedor (98).** No se toca. Nota: el texto de §6.5 aún justifica el
  único proveedor con "clave de API financiada"; si quieres, lo reformulo a un
  argumento de alcance/tiempo (1 edición) para no dejar abierta la crítica del tutor.

---

# Registro de aplicación — bloque azul 🔵 (sesión 2026-06-12)

> Todo verificado contra los artefactos de `output/`. Sin inventar cifras.

## APLICADOS

- **R — INVALID_OUTPUT por límite de tokens (121, 122).** Verificado en
  `output/rq3-structural/rq3-structural-full-evidence.json`: las 16
  `INVALID_OUTPUT` son **todas de gpt-4.1**, en **6 métodos extensos de Knowage**
  (respuestas de ~10.900–11.300 caracteres que no cierran el bloque ` ``` `): es
  **truncamiento por el límite de 2048 tokens**, no código defectuoso. Reescrito
  en 04e (Síntesis de RQ3) y añadido como limitación honesta en 04f (validez de
  conclusión: el límite resultó insuficiente; reejecutar con más tokens = futuro).

## VERIFICADO — ya estaba bien (sin edición)

- **I — métodos con CC=0 (116, 117, 118).** En los datos
  (`rq2-structural-all-results.csv`), los 4 métodos `fastjson` con CC=0 (sobrecargas
  que solo delegan) están como **`eligible=false`**. La §sonar-val (04e, líneas
  ~279-289) ya lo explica correctamente y es consistente con los datos. El tutor se
  confundió con el PDF antiguo; no requiere cambio.

## BLOQUEADO — falta el dato (no se puede sin inventar)

- **G — coste económico (134, 141).** Las evidencias **no registran el uso de
  tokens** (solo `responseTimeMs`); el protocolo solo fija `maxOutputTokens=2048`.
  No se puede calcular el coste exacto desde los artefactos. Estimación
  **basada en datos** (longitud de prompts/respuestas, tokens ≈ caracteres/4):
  - gpt-4o (378 inv.): ~365k tokens de entrada, ~40,5k de salida.
  - gpt-4.1 (378 inv.): ~365k tokens de entrada, ~119k de salida.
  - Total ≈ 730k entrada + 160k salida. El coste = aplicar la tarifa vigente a esas
    cifras (del orden de unos pocos dólares). **Decisión:** o se añade una cifra a
    la memoria con la tarifa que tú confirmes, o se re-ejecuta capturando `usage`.

## PENDIENTE (prosa/menor, del grupo R)

- **96** explicar docker antes de mencionarlo; **79** cómo se elige el dato en
  dry-run; **67** ¿`if` más largos? (los de Knowage lo son: justo los que truncaron).

---

# Registro — conversiones a `enumerate` (sesión 2026-06-12)

> Reestructuración del texto **existente** en listas (no es prosa nueva).

## APLICADOS

- **90** (04d §Métricas y línea base) — los tres elementos (métrica primaria /
  complementaria / baseline) pasan a `itemize`.
- **102** (04d §Reproducibilidad) — la enumeración de garantías (API key, versionado
  del prompt, versiones de `pom.xml`, artefactos auditables) pasa a `itemize`.
- **139** (04f §Mitigaciones implementadas) — las tres mitigaciones pasan a `itemize`.

## NO TOCADO — reescritura de prosa (NO-IA, lo haces tú)

Párrafos telegráficos que **no** reescribo (riesgo de IA): **6, 7, 8, 26, 27, 51,
89, 91, 92, 93, 94, 100, 101, 136, 158**. Candidato extra a `enumerate` si quieres
(es semicolon-list): el "procedimiento por caso es: cargar…; construir…; invocar…"
en 04d §6.5 (líneas ~207-210) — dímelo y lo convierto.

---

# Registro — reescritura de prosa NO-IA + coherencia (sesión 2026-06-12)

> El autor asume el riesgo y hará una **declaración de uso de IA**. Redactado en
> la voz del autor (impersonal "se", conectores, justificación, síntesis).

## APLICADOS — prosa reescrita (ya no telegráfica)

- **6** (01-intro §1.4) — entradilla que presenta las tres RQ antes de enunciarlas.
- **7** (01-intro §1.5) — entradilla de contribuciones + los 4 ítems pasan de
  sintagmas a frases completas.
- **26, 27** (03b §4.2) — tras cada RQ, una frase que explica *cómo* se aborda
  (constructiva / empírica / comparativa) antes de los criterios.
- **136** (04f §validez interna) — párrafo telegráfico → prosa con hilo.
- **158** (99 A.5) — descrito el modo dry-run y de dónde salen las respuestas
  grabadas (cubre también 157).

## APLICADOS — coherencia del cap. 6 (experimento) + de-telegrafiado

- **cabecera-mapa** del capítulo reescrita (+ typo "la linea"→"línea base").
- **89/91** entradilla antes de la lista de métricas.
- **92/93** "parte congelada" reescrita y aclarada ("se congelan… cuatro elementos").
- **96** Docker explicado (servidor local de SonarQube en contenedor) + frase rota
  arreglada (se mantiene "PowerShell" por tu indicación).
- **100/101** procedimiento por caso de RQ3 → `enumerate`; "parte congelada incluye…"
  → prosa que explica el provider intercambiable.
- **94** el capítulo gana hilo (mapa + entradillas + transiciones).

## VERIFICADO — ya estaba bien (sin tocar)

- **51** (04c §5.4 Implementación) ya tiene entradilla-mapa.
- **8** es la advertencia del tutor sobre IA (no es texto a reescribir).
- Cap. 7 (Resultados, 04e) ya tenía cabecera-mapa (Fase B).

## PENDIENTE (menor, si quieres)

- **67, 79** (prosa menor de RQ3); convertir a `enumerate` el "procedimiento por
  caso" — ya hecho como parte de 100/101.

---

# Registro — implementación de la detección de recursión (sesión 2026-06-12)

> Cambio de decisión del autor (antes D2-justificar → ahora **D1-implementar**),
> comentarios 60, 61, 135, 147. Regla SonarSource: recursión = `+1` estructural
> (sin anidamiento) por cada llamada del método a sí mismo.

## CÓDIGO

- `CognitiveComplexityCalculator`: nueva rama `MethodCallExpr` + método
  `isRecursiveCall` (criterio sintáctico conservador: nombre coincidente, misma
  aridad y receptor implícito o `this`). Captura `currentMethodName` /
  `currentMethodParamCount` en `calculate`. Cubre recursión **directa**; la
  **indirecta** queda fuera (documentado).
- Tests nuevos en `CognitiveComplexityCalculatorTest` (`@Nested Recursión`):
  directa simple (→2), Fibonacci dos llamadas (→3), recursión plana bajo
  anidamiento (→4), vía `this` (→1), aridad distinta no cuenta (→0), otro
  receptor no cuenta (→0).

## MEMORIA (revertido el marco de "no implementada")

- 04c §5.4.3: lista de incrementos +recursión; "Limitaciones" → "Cobertura del
  modelo" (completo, incluida recursión directa); fixture aclarado.
- 04e §sonar-val: el proxy implementa el modelo completo.
- 04f §validez de constructo: descartada esa fuente de divergencia.
- 05: limitación de recursión retirada; ítem de trabajo futuro "completar con
  recursión" **eliminado** (ya hecho).

## PENDIENTE DEL AUTOR (otro equipo)

- **Compilar** y ejecutar `mvn test`: confirmar que pasan los tests nuevos y,
  sobre todo, el `CognitiveComplexityCalculatorSonarValidationTest` (si el
  *fixture* incluyera algún caso recursivo, ahora debería alinear con SonarSource).
- Opcional: re-ejecutar la validación con SonarQube; la coincidencia 100\,% debe
  mantenerse (el corpus no tiene recursión).
