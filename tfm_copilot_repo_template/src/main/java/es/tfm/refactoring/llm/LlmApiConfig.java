package es.tfm.refactoring.llm;

/**
 * Configuración de credenciales y modelos para la campaña real de RQ3.
 * <p>
 * Lee las credenciales exclusivamente desde variables de entorno del sistema.
 * No almacena claves en código fuente, archivos de configuración ni logs.
 * <p>
 * Tras la decisión de usar dos modelos de OpenAI (gpt-4o vs gpt-4.1) en la
 * campaña formal, esta clase solo requiere {@code OPENAI_API_KEY}.
 * Variables disponibles:
 * <ul>
 *   <li>{@code OPENAI_API_KEY} (requerida) — clave de API de OpenAI.</li>
 *   <li>{@code OPENAI_MODEL} (opcional) — modelo primario.
 *       Default: {@value #DEFAULT_OPENAI_MODEL}.</li>
 *   <li>{@code OPENAI_SECONDARY_MODEL} (opcional) — modelo secundario.
 *       Default: {@value #DEFAULT_OPENAI_SECONDARY_MODEL}.</li>
 * </ul>
 * <p>
 * <strong>RQ3:</strong> ¿Pueden los grandes modelos de lenguaje realizar
 * esta refactorización de forma correcta y automática?
 */
public class LlmApiConfig {

    public static final String ENV_OPENAI_KEY = "OPENAI_API_KEY";
    public static final String ENV_OPENAI_MODEL = "OPENAI_MODEL";
    public static final String ENV_OPENAI_SECONDARY_MODEL = "OPENAI_SECONDARY_MODEL";

    public static final String DEFAULT_OPENAI_MODEL = "gpt-4o";
    public static final String DEFAULT_OPENAI_SECONDARY_MODEL = "gpt-4.1";

    /** Identificador de protocolo para el modelo primario. */
    public static final String PROTOCOL_MODEL_PRIMARY = "gpt-4o";
    /** Identificador de protocolo para el modelo secundario. */
    public static final String PROTOCOL_MODEL_SECONDARY = "gpt-4.1";

    private final String openaiApiKey;
    private final String openaiModel;
    private final String openaiSecondaryModel;
    private final boolean openAiOnly;

    /**
     * Carga la configuración desde variables de entorno del sistema para la
     * campaña formal de dos modelos OpenAI.
     *
     * @throws IllegalStateException si falta {@code OPENAI_API_KEY}
     */
    public LlmApiConfig() {
        this.openaiApiKey = requireEnv(ENV_OPENAI_KEY);
        this.openaiModel = envOrDefault(ENV_OPENAI_MODEL, DEFAULT_OPENAI_MODEL);
        this.openaiSecondaryModel = envOrDefault(
                ENV_OPENAI_SECONDARY_MODEL, DEFAULT_OPENAI_SECONDARY_MODEL);
        this.openAiOnly = false;
    }

    /**
     * Constructor para tests del modo formal dual-OpenAI: permite inyectar
     * valores sin depender del entorno.
     */
    LlmApiConfig(String openaiApiKey, String openaiModel,
                 String openaiSecondaryModel) {
        this.openaiApiKey = requireNonEmpty(openaiApiKey, ENV_OPENAI_KEY);
        this.openaiModel = openaiModel != null ? openaiModel : DEFAULT_OPENAI_MODEL;
        this.openaiSecondaryModel = openaiSecondaryModel != null
                ? openaiSecondaryModel : DEFAULT_OPENAI_SECONDARY_MODEL;
        this.openAiOnly = false;
    }

    /**
     * Constructor para modo exploratorio OpenAI-only (un solo modelo).
     * Solo se exige la clave de OpenAI.
     */
    LlmApiConfig(String openaiApiKey, String openaiModel) {
        this.openaiApiKey = requireNonEmpty(openaiApiKey, ENV_OPENAI_KEY);
        this.openaiModel = openaiModel != null ? openaiModel : DEFAULT_OPENAI_MODEL;
        this.openaiSecondaryModel = null;
        this.openAiOnly = true;
    }

    /**
     * Carga la configuración para una corrida exploratoria OpenAI-only:
     * solo requiere {@code OPENAI_API_KEY}.
     *
     * @throws IllegalStateException si falta {@code OPENAI_API_KEY}
     */
    public static LlmApiConfig openAiOnlyFromEnv() {
        String openai = requireEnv(ENV_OPENAI_KEY);
        String openaiModel = envOrDefault(ENV_OPENAI_MODEL, DEFAULT_OPENAI_MODEL);
        return new LlmApiConfig(openai, openaiModel);
    }

    /** Indica si esta configuración está en modo OpenAI-only (un solo modelo). */
    public boolean isOpenAiOnly() {
        return openAiOnly;
    }

    public String getOpenaiApiKey() { return openaiApiKey; }
    public String getOpenaiModel() { return openaiModel; }
    public String getOpenaiSecondaryModel() { return openaiSecondaryModel; }

    /**
     * Devuelve el mapa {modelo-de-protocolo → versión real del modelo}.
     * Útil para alimentar {@link LlmCampaignRunner#executeCampaign}.
     */
    public java.util.Map<String, String> toModelVersions() {
        if (openAiOnly) {
            return java.util.Map.of(PROTOCOL_MODEL_PRIMARY, openaiModel);
        }
        return java.util.Map.of(
                PROTOCOL_MODEL_PRIMARY, openaiModel,
                PROTOCOL_MODEL_SECONDARY, openaiSecondaryModel
        );
    }

    /**
     * Devuelve la clave de API correspondiente al modelo de protocolo.
     * En la campaña formal de dos modelos OpenAI, todos los modelos
     * comparten la misma clave {@code OPENAI_API_KEY}.
     *
     * @throws IllegalArgumentException si el modelo no es reconocido
     */
    public String getApiKeyForModel(String protocolModel) {
        if (protocolModel != null
                && (protocolModel.startsWith("gpt") || protocolModel.contains("openai"))) {
            return openaiApiKey;
        }
        throw new IllegalArgumentException("Modelo no reconocido: " + protocolModel);
    }

    /**
     * Devuelve el identificador real del modelo en la API de OpenAI para un
     * modelo del protocolo dado. En modo OpenAI-only el secundario no aplica.
     *
     * @throws IllegalArgumentException si el modelo no es reconocido o no
     *                                  está habilitado en este modo
     */
    public String getOpenAiApiModelId(String protocolModel) {
        if (PROTOCOL_MODEL_PRIMARY.equals(protocolModel)) {
            return openaiModel;
        }
        if (PROTOCOL_MODEL_SECONDARY.equals(protocolModel)) {
            if (openAiOnly) {
                throw new IllegalArgumentException(
                        "Modelo secundario no disponible en modo openai-only: "
                                + protocolModel);
            }
            return openaiSecondaryModel;
        }
        throw new IllegalArgumentException("Modelo no reconocido: " + protocolModel);
    }

    /**
     * Verifica que la clave de OpenAI está configurada y no está vacía.
     */
    public boolean isValid() {
        return isNonEmpty(openaiApiKey);
    }

    /**
     * Devuelve la configuración sin exponer las claves. Para diagnóstico.
     */
    public String toSafeString() {
        if (openAiOnly) {
            return String.format("LlmApiConfig[openai=%s(%s), mode=openai-only]",
                    mask(openaiApiKey), openaiModel);
        }
        return String.format("LlmApiConfig[openai=%s, primary=%s, secondary=%s, mode=dual-openai]",
                mask(openaiApiKey), openaiModel, openaiSecondaryModel);
    }

    @Override
    public String toString() {
        return toSafeString();
    }

    // =========================================================================
    // Helpers
    // =========================================================================

    private static String requireEnv(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Variable de entorno requerida no configurada: " + name
                            + ". Consulta .env.example para la plantilla de configuración. "
                            + "Nota: Java/System.getenv no carga el archivo .env automáticamente; "
                            + "debes exportar variables en terminal o configurarlas en el IDE.");
        }
        return value.strip();
    }

    private static String envOrDefault(String name, String defaultValue) {
        String value = System.getenv(name);
        return (value != null && !value.isBlank()) ? value.strip() : defaultValue;
    }

    private static String requireNonEmpty(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Clave de API vacía para: " + name);
        }
        return value.strip();
    }

    private static boolean isNonEmpty(String s) {
        return s != null && !s.isBlank();
    }

    private static String mask(String key) {
        if (key == null || key.length() < 8) return "***";
        return key.substring(0, 4) + "..." + key.substring(key.length() - 4);
    }
}
