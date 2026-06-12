package es.tfm.refactoring.experiment;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
     * Carga todos los archivos {@code .java} de un directorio del sistema de
     * archivos (no del classpath), de modo que el experimento pueda apuntarse a
     * una carpeta de corpus arbitraria y cambiarla sin recompilar.
     *
     * @param dir directorio con los archivos del corpus piloto
     * @return casos cargados, ordenados por nombre de archivo
     * @throws IOException si el directorio no existe o no puede leerse
     */
    public List<ExperimentCase> loadFromDirectory(Path dir) throws IOException {
        List<ExperimentCase> cases = new ArrayList<>();
        for (Path file : listJavaFiles(dir)) {
            cases.add(loadFromPath(file));
        }
        return cases;
    }

    /**
     * Carga un caso individual desde un archivo del sistema de archivos.
     */
    public ExperimentCase loadFromPath(Path file) throws IOException {
        String source = Files.readString(file, StandardCharsets.UTF_8);
        String fileName = file.getFileName().toString();
        String caseId = extractMetadata(source, "@caseId", deriveId(fileName));
        String origin = extractMetadata(source, "@origin", "pilot");
        String description = extractMetadata(source, "@description",
                "Caso piloto: " + fileName);
        return new ExperimentCase(caseId, origin, description, source);
    }

    /**
     * Lista, ordenados, los archivos {@code .java} de un directorio del sistema
     * de archivos.
     */
    private static List<Path> listJavaFiles(Path dir) throws IOException {
        if (!Files.isDirectory(dir)) {
            throw new IOException("Directorio de corpus no encontrado: " + dir);
        }
        try (Stream<Path> files = Files.list(dir)) {
            return files
                    .filter(p -> p.getFileName().toString().endsWith(".java"))
                    .sorted()
                    .collect(Collectors.toList());
        }
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
     * Descubre dinámicamente todos los archivos {@code .java} presentes en
     * {@code /pilot-corpus/} del classpath y los devuelve ordenados.
     */
    public static List<String> standardPilotFiles() {
        return discoverDir(RESOURCE_PREFIX);
    }

    private static List<String> discoverDir(String resourceDir) {
        URL dirUrl = PilotCorpusLoader.class.getResource(resourceDir);
        if (dirUrl == null) {
            throw new IllegalStateException(
                    "Directorio no encontrado en classpath: " + resourceDir);
        }
        try {
            Path dirPath = Paths.get(dirUrl.toURI());
            try (Stream<Path> files = Files.list(dirPath)) {
                return files
                        .map(p -> p.getFileName().toString())
                        .filter(name -> name.endsWith(".java"))
                        .sorted()
                        .collect(Collectors.toList());
            }
        } catch (URISyntaxException | IOException e) {
            throw new IllegalStateException(
                    "Error al descubrir archivos en " + resourceDir, e);
        }
    }
}
