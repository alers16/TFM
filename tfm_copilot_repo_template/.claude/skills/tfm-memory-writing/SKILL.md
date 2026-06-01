---
name: tfm-memory-writing
description: Utiliza esta skill para redactar o revisar la memoria del TFM en LaTeX (LNCS) con trazabilidad y rigor académico, cumpliendo la guía de la UMA.
---

# Objetivo
Apoyar la redacción de la memoria del TFM sin inventar contenido ni alterar el enfoque aprobado.
El documento se escribe en LaTeX con formato LNCS.

# Reglas
- Mantén las RQ oficiales literalmente.
- No inventes referencias ni resultados.
- Marca todo lo pendiente con [PENDIENTE DE CITA] o [PENDIENTE DE VERIFICACIÓN].
- Escribe con claridad y concisión académica.
- Antes de generar LaTeX consulta la skill `tfm-latex`.

# Contenidos mínimos (guía UMA)
- Resumen / Abstract / Keywords / Tutor(a)
- Introducción (objetivos, problema, situación de partida, relevancia, organización del documento)
- Antecedentes / Estado del arte (con estudio comparativo)
- Descripción del problema
- Detalles de la propuesta (solución, tecnología, comparación y aporte logrado)
- Conclusiones (incl. trabajo futuro)
- Referencias

# Secciones detalladas habituales (mapeables a lo anterior)
Introducción, Estado del arte, Objetivos y preguntas de investigación, Metodología,
Diseño del enfoque, Implementación, Diseño experimental, Resultados, Amenazas a la validez,
Conclusiones.

# Salida esperada
- Texto LaTeX redactado, pendientes, citas requeridas y coherencia con el prototipo real.
