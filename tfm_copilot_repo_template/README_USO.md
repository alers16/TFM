# Cómo usar esta plantilla con Copilot

## 1. Copia estos archivos al repositorio del TFM
Mantén la estructura tal como está para que Copilot pueda encontrar:
- `AGENTS.md`
- `.github/copilot-instructions.md`
- `.github/instructions/*.instructions.md`
- `.github/skills/**/SKILL.md`
- `prompts/*.md`

## 2. Arranque recomendado
Primero abre Copilot Agent dentro del repositorio y pega el contenido de:
- `prompts/copilot-master-prompt.md`

Eso hará que inspeccione el repo, proponga plan y arranque con sentido.

## 3. Cómo trabajar de forma autónoma pero con control
No le digas solo “hazme el TFM”. Dale sesiones cerradas con un objetivo concreto.

### Buenas sesiones
- “deja redactada la introducción y la sección de objetivos sin inventar citas”
- “implementa el detector MVP del patrón if anidado simple y añade tests básicos”
- “prepara el diseño experimental que responderá a RQ1 y RQ2 sin asumir resultados”
- “revisa la carpeta memoria y crea checklist de citas pendientes”

### Malas sesiones
- “haz todo el trabajo”
- “investiga y escribe lo que veas”
- “implementa ya toda la herramienta completa”

## 4. Secuencia recomendada de prompts
### Fase 1: arranque
Usa `prompts/copilot-master-prompt.md`

### Fase 2: trabajo específico
Según toque, usa uno de estos:
- `prompts/copilot-memory-prompt.md`
- `prompts/copilot-development-prompt.md`

### Fase 3: sesiones autónomas controladas
Usa `prompts/copilot-autonomous-session-prompt.md` y rellena el objetivo.

Ejemplos de objetivo:
- “dejar cerrada la estructura del capítulo de metodología y redactar un borrador verificable”
- “implementar detector y tests del caso válido básico”
- “preparar tabla de amenazas a la validez sin inventar contenido”

## 5. Regla de oro
Autonomía sí, pero por iteraciones pequeñas. Cada sesión debe terminar con:
- lo hecho,
- lo pendiente,
- riesgos,
- siguiente paso.
