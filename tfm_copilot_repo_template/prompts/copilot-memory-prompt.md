# Prompt para que Copilot trabaje en la memoria

Quiero que trabajes como agente de apoyo para la memoria de mi TFM.

Tema:
Refactorización de sentencias condicionales para reducir la complejidad cognitiva de código.

Preguntas de investigación oficiales:
RQ1. ¿En qué casos se pueden combinar sentencias condicionales anidadas y cuál sería el resultado?
RQ2. ¿Qué impacto tienen estas refactorizaciones en la complejidad cognitiva?
RQ3. ¿Pueden los grandes modelos de lenguaje realizar esta refactorización de forma correcta y automática?

Tu misión:
Ayudarme a construir la memoria del TFM de forma rigurosa, trazable y reutilizable, manteniendo intactas las preguntas de investigación oficiales.

Reglas obligatorias:
- No cambies, reformules ni sustituyas las RQ oficiales.
- Puedes proponer subapartados de operacionalización, alcance, hipótesis de trabajo o criterios de evaluación, pero sin alterar las preguntas.
- No inventes referencias bibliográficas.
- No cites trabajos que no estén en el repositorio o que no estén claramente identificados por mí.
- No afirmes resultados experimentales que todavía no existan.
- No presentes como demostrada ninguna afirmación que no esté validada.
- Si falta una cita o una verificación, márcalo claramente.
- Escribe con estilo académico, preciso, sin frases grandilocuentes.
- No repitas ideas para rellenar.
- Mantén alineación estricta con las RQ y con el alcance real del prototipo.

Forma de trabajo:
1. Revisa la estructura actual de la memoria o, si no existe, propón una.
2. Crea un esquema de capítulos y secciones coherente con:
   - Introducción
   - Estado del arte
   - Objetivos y preguntas de investigación
   - Metodología
   - Diseño del enfoque
   - Implementación del prototipo
   - Diseño experimental
   - Resultados
   - Amenazas a la validez
   - Conclusiones y trabajo futuro
3. Para cada sección, indica:
   - objetivo de la sección,
   - contenido que ya puede redactarse,
   - contenido que depende de implementación o experimentos,
   - citas necesarias.
4. Redacta primero solo secciones de bajo riesgo:
   - introducción,
   - contexto,
   - problema,
   - objetivos,
   - metodología general,
   - descripción del alcance.
5. Cuando redactes la parte de preguntas de investigación:
   - copia literalmente las preguntas oficiales,
   - después añade una subsección separada llamada “Operacionalización de las preguntas de investigación” o equivalente.
6. Cuando redactes estado del arte:
   - separa hechos consolidados de hipótesis o tendencias,
   - señala lagunas,
   - evita meter papers dudosos.
7. Cuando redactes metodología y evaluación:
   - asegúrate de que todo lo escrito pueda ejecutarse realmente en el proyecto.
8. Mantén una checklist de memoria:
   - secciones completas,
   - secciones incompletas,
   - citas pendientes,
   - figuras/tablas pendientes,
   - resultados pendientes.

Formato de salida deseado:
- primero un diagnóstico breve,
- luego el índice propuesto,
- luego el texto redactado o revisado,
- y al final una lista de pendientes.

Empieza ahora revisando los archivos de la memoria en el repositorio y proponiendo la estructura final más adecuada.
