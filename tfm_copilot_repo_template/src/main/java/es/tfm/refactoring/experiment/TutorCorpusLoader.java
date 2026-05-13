package es.tfm.refactoring.experiment;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Carga casos experimentales desde {@code /tutor-corpus/}, extraidos de los
 * recursos de prueba del repositorio del tutor del TFM
 * (SoftwareCognitiveComplexityReducer, Saborido et al., IEEE Access 2022).
 * <p>
 * Estos casos corresponden a los metodos diana documentados como entrada del
 * propio enfoque del tutor: metodos reales con complejidad cognitiva alta
 * extraidos de proyectos open-source (Bytecode Viewer, jMetal). Su procedencia
 * y CC esperada estan anotadas como metadatos en cabecera, siguiendo el mismo
 * formato que {@link RealDatasetLoader}.
 * <p>
 * Licencias: los ficheros bajo {@code /tutor-corpus/} mantienen su licencia
 * original (GPL-3.0-or-later o LGPL-3.0-or-later). Su inclusion en el
 * paquete de replicacion del TFM se realiza con fines academicos y se cita
 * la fuente (proyecto, archivo, metodo, version y referencia bibliografica).
 *
 * @see RealDatasetLoader
 */
public class TutorCorpusLoader {

    private static final String RESOURCE_PREFIX = "/tutor-corpus/";

    /**
     * Carga todos los casos del corpus del tutor.
     */
    public List<ExperimentCase> load(List<String> fileNames) throws IOException {
        List<ExperimentCase> cases = new ArrayList<>();
        for (String fileName : fileNames) {
            cases.add(loadFile(fileName));
        }
        return cases;
    }

    /**
     * Carga un archivo individual del corpus del tutor.
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
        String origin = extractMetadata(source, "@origin", "tutor-test-corpus");
        String description = extractMetadata(source, "@description",
                "Caso del tutor: " + fileName);

        return new ExperimentCase(caseId, origin, description, source);
    }

    /**
     * Extrae un valor adicional de metadato del codigo fuente.
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
     * Lista de archivos del corpus del tutor.
     * Corresponden a los seis metodos diana del paper de Saborido et al. 2022.
     */
    public static List<String> standardTutorFiles() {
        return List.of(
                "TutorBcvEzInjectionExecute.java",
                "TutorBcvResourceDecompileSaveAll.java",
                "TutorBcvResourceDecompileSaveOpened.java",
                "TutorJmetalEbesReadDataFile.java",
                "TutorJmetalEbesVariablePosition.java",
                "TutorJmetalLz09Objective.java"
        );
    }
}
