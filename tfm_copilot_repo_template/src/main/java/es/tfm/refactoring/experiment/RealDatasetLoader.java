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
 * bajo {@code /real-corpus/}, extraÃ­dos de proyectos open-source reales
 * referenciados por el estudio de Saborido et al. (IEEE Access, 2022).
 * <p>
 * Cada archivo contiene un mÃ©todo Java envuelto en una clase, con metadatos
 * de trazabilidad que incluyen el proyecto de origen, la ruta original del
 * archivo, la licencia y un valor de CC Sonar manualmente verificado
 * (cuando estÃ© disponible).
 * <p>
 * Formato de metadatos esperado:
 * <pre>
 * // @caseId REAL_001
 * // @origin commons-lang-3.12.0
 * // @project Apache Commons Lang
 * // @file StringUtils.java
 * // @method chomp(String)
 * // @license Apache-2.0
 * // @sonarCCBefore 3
 * // @description MÃ©todo con condicional anidado â€” patrÃ³n null-check + longitud
 * </pre>
 * <p>
 * Los metadatos {@code @project}, {@code @file}, {@code @method}, {@code @license}
 * y {@code @sonarCCBefore} son adicionales respecto al corpus piloto y permiten
 * rastrear el caso hasta su fuente original.
 * <p>
 * <strong>Referencia del dataset base:</strong> R. Saborido, J. Ferrer, F. Chicano
 * y E. Alba, "Automatizing Software Cognitive Complexity Reduction," IEEE Access,
 * vol. 10, 2022, doi:10.1109/ACCESS.2022.3144743.
 */
public class RealDatasetLoader {

    private static final String RESOURCE_PREFIX = "/real-corpus/";

    /**
     * Carga todos los casos del corpus real.
     *
     * @param fileNames nombres de los archivos Java dentro de {@code /real-corpus/}
     * @return lista de casos experimentales cargados
     * @throws IOException si no se puede leer algÃºn archivo
     */
    public List<ExperimentCase> load(List<String> fileNames) throws IOException {
        List<ExperimentCase> cases = new ArrayList<>();
        for (String fileName : fileNames) {
            cases.add(loadFile(fileName));
        }
        return cases;
    }

    /**
     * Carga un archivo individual del corpus real.
     *
     * @param fileName nombre del archivo (e.g., "RealCommonsLangChomp.java")
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
        String origin = extractMetadata(source, "@origin", "real-oss");
        String description = extractMetadata(source, "@description",
                "Caso real: " + fileName);

        return new ExperimentCase(caseId, origin, description, source);
    }

    /**
     * Carga todos los archivos {@code .java} de un directorio del sistema de
     * archivos (no del classpath), de modo que el experimento pueda apuntarse a
     * una carpeta de corpus arbitraria y cambiarla sin recompilar.
     *
     * @param dir directorio con los archivos del corpus real
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
        String origin = extractMetadata(source, "@origin", "real-oss");
        String description = extractMetadata(source, "@description",
                "Caso real: " + fileName);
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
     * Extrae un valor adicional de metadato del cÃ³digo fuente.
     * Ãštil para obtener @project, @file, @method, @license, @sonarCCBefore.
     *
     * @param source cÃ³digo fuente del caso
     * @param key    clave del metadato (e.g., "@project")
     * @return valor extraÃ­do, o null si no se encuentra
     */
    public String extractExtra(String source, String key) {
        return extractMetadata(source, key, null);
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

    /**
     * Descubre dinÃ¡micamente todos los archivos {@code .java} presentes en
     * {@code /real-corpus/} del classpath y los devuelve ordenados.
     * <p>
     * No requiere mantenimiento manual: cualquier archivo aÃ±adido al directorio
     * de recursos se incluye automÃ¡ticamente.
     */
    public static List<String> standardRealFiles() {
        return discoverDir(RESOURCE_PREFIX);
    }

    private static List<String> discoverDir(String resourceDir) {
        URL dirUrl = RealDatasetLoader.class.getResource(resourceDir);
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
