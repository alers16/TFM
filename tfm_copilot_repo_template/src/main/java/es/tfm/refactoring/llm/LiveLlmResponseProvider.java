package es.tfm.refactoring.llm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Proveedor de respuestas LLM que invoca la API real de OpenAI.
 * <p>
 * Soporta dos modelos OpenAI bajo el mismo protocolo (campaña formal
 * gpt-4o vs gpt-4.1). Utiliza exclusivamente {@link java.net.http.HttpClient}
 * (JDK 11+) para evitar dependencias externas de SDK. Lee credenciales
 * desde {@link LlmApiConfig}.
 * <p>
 * Limitaciones deliberadas:
 * <ul>
 *   <li>No implementa reintentos — los gestiona {@link LlmCampaignRunner} vía attempts.</li>
 *   <li>No cachea respuestas — cada llamada es una invocación real.</li>
 *   <li>Timeout de 60 segundos por invocación.</li>
 * </ul>
 * <p>
 * <strong>RQ3:</strong> ¿Pueden los grandes modelos de lenguaje realizar
 * esta refactorización de forma correcta y automática?
 */
public class LiveLlmResponseProvider implements LlmResponseProvider {

    private static final String OPENAI_API_URL =
            "https://api.openai.com/v1/chat/completions";
    private static final Duration TIMEOUT = Duration.ofSeconds(60);

    private final LlmApiConfig config;
    private final HttpClient httpClient;
    private final Gson gson;
    private final double temperature;
    private final int maxTokens;

    /**
     * @param config      configuración con claves de API y modelos
     * @param temperature temperatura del muestreo (0.0 para determinista)
     * @param maxTokens   límite de tokens de salida
     */
    public LiveLlmResponseProvider(LlmApiConfig config,
                                   double temperature, int maxTokens) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(TIMEOUT)
                .build();
        this.gson = new Gson();
        this.temperature = temperature;
        this.maxTokens = maxTokens;
    }

    @Override
    public String getResponse(String model, String caseId, int attempt,
                              String prompt) {
        try {
            if (model.startsWith("gpt") || model.contains("openai")) {
                return callOpenAi(model, prompt);
            } else {
                throw new IllegalArgumentException("Modelo no soportado: " + model);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Invocacion interrumpida", e);
        } catch (IOException e) {
            throw new RuntimeException("Fallo de API: " + e.getMessage(), e);
        }
    }

    /**
     * Invoca la API de OpenAI Chat Completions usando el modelo real
     * mapeado a partir del identificador de protocolo.
     */
    private String callOpenAi(String protocolModel, String prompt)
            throws IOException, InterruptedException {
        JsonObject body = new JsonObject();
        body.addProperty("model", config.getOpenAiApiModelId(protocolModel));
        body.addProperty("temperature", temperature);
        body.addProperty("max_tokens", maxTokens);

        JsonArray messages = new JsonArray();

        // Separar system y user del prompt compuesto
        String[] parts = splitPrompt(prompt);

        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", parts[0]);
        messages.add(systemMsg);

        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", parts[1]);
        messages.add(userMsg);

        body.add("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(OPENAI_API_URL))
                .timeout(TIMEOUT)
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + config.getOpenaiApiKey())
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("OpenAI API error " + response.statusCode()
                    + ": " + response.body());
        }

        JsonObject json = gson.fromJson(response.body(), JsonObject.class);
        return json.getAsJsonArray("choices")
                .get(0).getAsJsonObject()
                .getAsJsonObject("message")
                .get("content").getAsString();
    }

    /**
     * Divide un prompt compuesto en sus partes <em>system</em> y <em>user</em>
     * usando el separador inyectado por {@link LlmPromptBuilder}.
     */
    /**
     * Separa el prompt compuesto (system + user) generado por LlmPromptBuilder.
     * El separador es la línea "---" entre ambas secciones.
     *
     * @return array de 2 elementos: [systemPrompt, userPrompt]
     */
    static String[] splitPrompt(String fullPrompt) {
        // LlmPromptBuilder usa "\n\n---\n\n" como separador
        int sep = fullPrompt.indexOf("\n\n---\n\n");
        if (sep < 0) {
            return new String[]{"", fullPrompt};
        }
        return new String[]{
                fullPrompt.substring(0, sep).strip(),
                fullPrompt.substring(sep + 7).strip()
        };
    }
}
