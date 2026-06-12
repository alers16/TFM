package es.tfm.refactoring.llm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Proveedor de respuestas LLM que reutiliza las respuestas crudas ya grabadas
 * en un fichero de evidencia completa de campaña RQ3 (p.ej.
 * {@code output/rq3-campaign-real-phase9/rq3-full-evidence.json}).
 * <p>
 * <strong>Propósito.</strong> Permite RE-EVALUAR una campaña RQ3 ya ejecutada
 * <em>sin volver a llamar a la API del LLM</em>: el campo {@code rawLlmOutput}
 * de cada invocación se sirve tal cual, y el oráculo
 * ({@link LlmResponseValidator}) lo vuelve a comparar contra un baseline que
 * puede haberse recomputado en otro {@link es.tfm.refactoring.detection.DetectionMode}.
 * <p>
 * <strong>Clave de indexación.</strong> {@code model::caseId::attempt}. Esta es
 * la granularidad real del fichero de evidencia (modelo × caso × intento). Con
 * {@code temperature=0} los intentos suelen ser idénticos, pero se respeta el
 * intento para máxima fidelidad. Si para una clave concreta no hay respuesta
 * grabada se intenta el mismo {@code model::caseId} del intento 1 como
 * salvaguarda; si tampoco existe, devuelve {@code null} (el runner lo registra
 * como ERROR técnico, igual que una respuesta ausente en vivo).
 * <p>
 * <strong>Precondiciones.</strong>
 * <ul>
 *   <li>El JSON es un array de objetos con campos {@code model}, {@code caseId},
 *       {@code attemptNumber} y {@code rawLlmOutput} (formato producido por
 *       {@link LlmCampaignExporter#toFullEvidenceJson}).</li>
 *   <li>Una invocación con {@code rawLlmOutput} nulo (error técnico grabado) se
 *       conserva como nula: no se inventa contenido.</li>
 * </ul>
 * <p>
 * Este proveedor es de SOLO LECTURA y no modifica el flujo en vivo ni el
 * proveedor pre-grabado {@link PrerecordedResponseProvider} (que cubre solo el
 * subset de 6 casos del prompt v1.0).
 */
public class RecordedEvidenceResponseProvider implements LlmResponseProvider {

    private static final Gson GSON = new Gson();

    /** model::caseId::attempt -> rawLlmOutput (puede contener valores null). */
    private final Map<String, String> responsesByFullKey;
    /** model::caseId (intento 1) -> rawLlmOutput, salvaguarda. */
    private final Map<String, String> responsesByCaseKey;
    private final int recordCount;

    /**
     * Carga las respuestas crudas desde un fichero de evidencia completa.
     *
     * @param fullEvidenceJson ruta a {@code rq3-full-evidence.json}
     * @throws IOException si el fichero no existe o no es legible
     */
    public RecordedEvidenceResponseProvider(Path fullEvidenceJson) throws IOException {
        if (!Files.exists(fullEvidenceJson)) {
            throw new IOException("Fichero de evidencia no encontrado: "
                    + fullEvidenceJson.toAbsolutePath());
        }
        String json = Files.readString(fullEvidenceJson, StandardCharsets.UTF_8);
        JsonArray array = GSON.fromJson(json, JsonArray.class);
        if (array == null) {
            throw new IOException("Evidencia vacía o no parseable: "
                    + fullEvidenceJson.toAbsolutePath());
        }

        this.responsesByFullKey = new LinkedHashMap<>();
        this.responsesByCaseKey = new LinkedHashMap<>();

        for (JsonElement element : array) {
            JsonObject obj = element.getAsJsonObject();
            String model = asStringOrNull(obj, "model");
            String caseId = asStringOrNull(obj, "caseId");
            int attempt = obj.has("attemptNumber") && !obj.get("attemptNumber").isJsonNull()
                    ? obj.get("attemptNumber").getAsInt()
                    : 1;
            String raw = asStringOrNull(obj, "rawLlmOutput");

            if (model == null || caseId == null) {
                continue;
            }
            responsesByFullKey.put(fullKey(model, caseId, attempt), raw);
            responsesByCaseKey.putIfAbsent(caseKey(model, caseId), raw);
        }
        this.recordCount = responsesByFullKey.size();
    }

    @Override
    public String getResponse(String model, String caseId, int attempt,
                              String prompt) {
        String full = fullKey(model, caseId, attempt);
        if (responsesByFullKey.containsKey(full)) {
            return responsesByFullKey.get(full);
        }
        // Salvaguarda: reutiliza el intento 1 del mismo modelo×caso si existe.
        return responsesByCaseKey.get(caseKey(model, caseId));
    }

    /** Número de invocaciones (model×caso×intento) cargadas desde la evidencia. */
    public int getRecordCount() {
        return recordCount;
    }

    /** Número de pares modelo×caso distintos con respuesta grabada. */
    public int getCaseModelCount() {
        return responsesByCaseKey.size();
    }

    private static String asStringOrNull(JsonObject obj, String field) {
        if (!obj.has(field) || obj.get(field).isJsonNull()) {
            return null;
        }
        return obj.get(field).getAsString();
    }

    private static String fullKey(String model, String caseId, int attempt) {
        return model + "::" + caseId + "::" + attempt;
    }

    private static String caseKey(String model, String caseId) {
        return model + "::" + caseId;
    }
}
