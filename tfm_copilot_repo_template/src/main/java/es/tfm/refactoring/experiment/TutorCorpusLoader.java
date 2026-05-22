package es.tfm.refactoring.experiment;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
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
     * Carga todos los ficheros {@code .java} de un directorio del sistema de
     * ficheros (no classpath). Usado por {@code Rq2ScannerBatchExecutor} para
     * procesar el corpus generado automaticamente por {@code CorpusFileGenerator}.
     * <p>
     * Los ficheros deben seguir el mismo formato de cabecera que los recursos
     * de {@code /tutor-corpus/}: {@code // @caseId}, {@code // @origin}, etc.
     *
     * @param dir directorio que contiene los ficheros {@code .java}
     * @return lista de casos experimentales (vacia si el dir no existe o esta vacio)
     */
    public List<ExperimentCase> loadAllFromDir(Path dir) throws IOException {
        if (!Files.isDirectory(dir)) return List.of();
        List<ExperimentCase> cases = new ArrayList<>();
        try (var stream = Files.newDirectoryStream(dir, "*.java")) {
            for (Path javaFile : stream) {
                String source   = Files.readString(javaFile, StandardCharsets.UTF_8);
                String fileName = javaFile.getFileName().toString();
                String caseId   = extractMetadata(source, "@caseId", deriveId(fileName));
                String origin   = extractMetadata(source, "@origin", "scanner-generated");
                String description = extractMetadata(source, "@description",
                        "Caso generado por CorpusScanRunner: " + fileName);
                cases.add(new ExperimentCase(caseId, origin, description, source));
            }
        }
        return cases;
    }

    /**
     * Carga un archivo individual del corpus del tutor.
     */
    public ExperimentCase loadFile(String fileName) throws IOException {        String resourcePath = RESOURCE_PREFIX + fileName;
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
     * Cubre los 10 proyectos open-source evaluados en Saborido et al. 2022 (IEEE Access).
     * <ul>
     *   <li>Lote 1 (8 casos): metodos de los 2 proyectos incluidos como recursos de prueba
     *       en el propio repositorio del tutor (bytecode-viewer, jMetal, FileDrop).</li>
     *   <li>Lote 2 (4 casos): metodos elegibles extraidos de 4 de los 8 proyectos adicionales
     *       del paper mediante el escaner masivo (cybercaptor-server, MOEAFramework,
     *       iotbroker.cloud-java-client, Knowage-Server).</li>
     * </ul>
     * Los 4 proyectos restantes (AIoTES, fastjson batch-eligible, jedis, fiware-commons)
     * no produjeron casos elegibles para la refactorizacion if-if en sus versiones HEAD actuales.
     */
    public static List<String> standardTutorFiles() {
        return List.of(
                // Lote 1 — recursos de prueba del repositorio del tutor (bytecode-viewer, jMetal, FileDrop)
                "TutorBcvEzInjectionExecute.java",
                "TutorBcvResourceDecompileSaveAll.java",
                "TutorBcvResourceDecompileSaveOpened.java",
                "TutorJmetalEbesReadDataFile.java",
                "TutorJmetalEbesVariablePosition.java",
                "TutorJmetalLz09Objective.java",
                "TutorFileDropIsDragOk.java",
                "TutorFileDropRemove.java",
                // Lote 2 — proyectos adicionales del paper (escaneados en HEAD actual)
                "TutorCybercaptorVertexGetRelatedMachine.java",  // CC 52->38, delta=-14
                "TutorMoeaCommandLineGetConsoleWidth.java",       // CC 12->9,  delta=-3
                "TutorIotbrokerAmqpProcessSaslOutcome.java",      // CC 3->2,   delta=-1
                "TutorKnowageGeoSpatialCheckValue.java"           // CC 20->17, delta=-3
        );
    }
}
