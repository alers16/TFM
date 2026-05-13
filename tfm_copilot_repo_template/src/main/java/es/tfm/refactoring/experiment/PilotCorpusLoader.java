package es.tfm.refactoring.experiment;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Carga casos experimentales desde archivos Java ubicados en el classpath
 * bajo {@code /pilot-corpus/}.
 * <p>
 * Cada archivo debe contener una clase Java completa con al menos un método.
 * Los metadatos del caso (caseId, origin, description) se extraen del nombre
 * del archivo y de comentarios especiales en el formato:
 * <pre>
 * // @caseId PILOT_001
 * // @origin commons-lang
 * // @description Método con condicional anidado simple
 * </pre>
 * <p>
 * Si los comentarios no están presentes, se usan valores por defecto
 * derivados del nombre del archivo.
 */
public class PilotCorpusLoader {

    private static final String RESOURCE_PREFIX = "/pilot-corpus/";

    /**
     * Carga todos los casos del corpus piloto.
     *
     * @param fileNames nombres de los archivos Java dentro de {@code /pilot-corpus/}
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
     * Carga un archivo individual del corpus piloto.
     *
     * @param fileName nombre del archivo (e.g., "PilotValidSimple.java")
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

        String caseId = extractMetadata(source, "@caseId",
                deriveId(fileName));
        String origin = extractMetadata(source, "@origin", "pilot");
        String description = extractMetadata(source, "@description",
                "Caso piloto: " + fileName);

        return new ExperimentCase(caseId, origin, description, source);
    }

    /**
     * Extrae un valor de metadato de un comentario especial en el código fuente.
     * Busca la primera línea que contenga {@code // @key valor...}
     */
    private String extractMetadata(String source, String key,
                                   String defaultValue) {
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

    /**
     * Lista de archivos del corpus piloto estándar.
     * Los archivos deben existir como recursos en {@code /pilot-corpus/}.
     */
    public static List<String> standardPilotFiles() {
        return List.of(
                "PilotValidSimple.java",
                "PilotValidNullCheck.java",
                "PilotValidBoundsCheck.java",
                "PilotValidNestedInLoop.java",
                "PilotInvalidElseBranch.java",
                "PilotInvalidMethodCallGuard.java",
                "PilotInvalidMultiStatement.java",
                "PilotMixedOpportunities.java"
        );
    }
}
