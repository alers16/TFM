package es.tfm.refactoring.experiment;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Carga casos experimentales desde archivos Java ubicados en el classpath
 * bajo {@code /trap-corpus/}.
 * <p>
 * Los casos trampa son sintéticos y están diseñados para violar exactamente
 * una precondición de forma no obvia, con el fin de evaluar si los LLMs
 * detectan correctamente los casos no elegibles (falsos positivos).
 * <p>
 * Cada archivo debe incluir comentarios de metadatos en formato:
 * <pre>
 * // @caseId TRAP_P4_INNER_ELSE
 * // @origin synthetic-trap
 * // @description ...
 * </pre>
 */
public class TrapCorpusLoader {

    private static final String RESOURCE_PREFIX = "/trap-corpus/";

    /**
     * Carga los archivos especificados del corpus trampa.
     *
     * @param fileNames nombres de los archivos Java dentro de {@code /trap-corpus/}
     * @return lista de casos experimentales cargados
     * @throws IOException si no se puede leer algún archivo
     */
    public List<ExperimentCase> load(List<String> fileNames) throws IOException {
        List<ExperimentCase> cases = new ArrayList<>();
        for (String fileName : fileNames) {
            cases.add(loadFile(fileName));
        }
        return cases;
    }

    /**
     * Carga un archivo individual del corpus trampa.
     *
     * @param fileName nombre del archivo (e.g., "TrapP4InnerElse.java")
     * @return caso experimental
     * @throws IOException si no se puede leer el archivo
     */
    public ExperimentCase loadFile(String fileName) throws IOException {
        String resourcePath = RESOURCE_PREFIX + fileName;
        String source;

        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IOException(
                        "Recurso no encontrado en classpath: " + resourcePath);
            }
            source = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }

        String caseId = extractMetadata(source, "@caseId", deriveId(fileName));
        String origin = extractMetadata(source, "@origin", "synthetic-trap");
        String description = extractMetadata(source, "@description",
                "Caso trampa: " + fileName);

        return new ExperimentCase(caseId, origin, description, source);
    }

    /**
     * Lista estándar de archivos del corpus trampa.
     * Cada uno viola exactamente una precondición P1–P5.
     */
    public static List<String> standardTrapFiles() {
        return List.of(
                "TrapP4InnerElse.java",
                "TrapP2MultiStatement.java",
                "TrapP5Assignment.java"
        );
    }

    private String extractMetadata(String source, String key, String defaultValue) {
        for (String line : source.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("// " + key + " ")) {
                return trimmed.substring(("// " + key + " ").length()).trim();
            }
        }
        return defaultValue;
    }

    private String deriveId(String fileName) {
        return fileName.replace(".java", "")
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toUpperCase();
    }
}
