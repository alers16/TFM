package es.tfm.refactoring.experiment;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Carga casos experimentales desde archivos Java ubicados en el classpath
 * bajo {@code /real-corpus/}, extraídos de proyectos open-source reales
 * referenciados por el estudio de Saborido et al. (IEEE Access, 2022).
 * <p>
 * Cada archivo contiene un método Java envuelto en una clase, con metadatos
 * de trazabilidad que incluyen el proyecto de origen, la ruta original del
 * archivo, la licencia y un valor de CC Sonar manualmente verificado
 * (cuando esté disponible).
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
 * // @description Método con condicional anidado — patrón null-check + longitud
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
     * Extrae un valor adicional de metadato del código fuente.
     * Útil para obtener @project, @file, @method, @license, @sonarCCBefore.
     *
     * @param source código fuente del caso
     * @param key    clave del metadato (e.g., "@project")
     * @return valor extraído, o null si no se encuentra
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
     * Lista de archivos del subconjunto real estándar.
     * Los archivos deben existir como recursos en {@code /real-corpus/}.
     * <p>
     * Subconjunto inicial: métodos representativos de proyectos referenciados
     * en Saborido et al. (2022), seleccionados para cubrir:
     * <ul>
     *   <li>Distintos proyectos de origen</li>
     *   <li>Casos elegibles e inelegibles</li>
     *   <li>Distintas categorías de descarte</li>
     *   <li>Distintos niveles de CC</li>
     * </ul>
     */
    public static List<String> standardRealFiles() {
        return List.of(
                "RealCommonsLangChomp.java",
                "RealCommonsLangIsNumeric.java",
                "RealCommonsCollectionsGet.java",
                "RealCommonsCollectionsIsEmpty.java",
                "RealCommonsMathConverged.java",
                "RealCommonsMathValidateRange.java",
                "RealAntExecuteTask.java",
                "RealAntMatchPath.java",
                "RealCommonsLangContainsNone.java",
                "RealCommonsLangMid.java"
        );
    }
}
