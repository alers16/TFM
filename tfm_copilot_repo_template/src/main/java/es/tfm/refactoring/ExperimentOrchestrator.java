package es.tfm.refactoring;

import es.tfm.refactoring.experiment.Rq2BatchExecutor;
import es.tfm.refactoring.experiment.Rq2ComparisonExecutor;
import es.tfm.refactoring.experiment.Rq2ScannerBatchExecutor;
import es.tfm.refactoring.experiment.Rq2TutorBatchExecutor;
import es.tfm.refactoring.llm.CampaignExecutor;
import es.tfm.refactoring.llm.Rq3PromptV2Executor;
import es.tfm.refactoring.llm.Rq3TrapCampaignExecutor;
import es.tfm.refactoring.scanner.CorpusFileGenerator;
import es.tfm.refactoring.scanner.CorpusScanRunner;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Orquestador del experimento completo del TFM.
 * <p>
 * Permite ejecutar en orden todos los pasos reproducibles del experimento
 * (RQ2 y RQ3) con un único comando, con control granular por fase.
 * <p>
 * <strong>Uso básico:</strong>
 * <pre>
 *   java es.tfm.refactoring.ExperimentOrchestrator --all
 *   java es.tfm.refactoring.ExperimentOrchestrator --rq2
 *   java es.tfm.refactoring.ExperimentOrchestrator --rq3
 * </pre>
 *
 * <strong>Pasos individuales:</strong>
 * <pre>
 *   java es.tfm.refactoring.ExperimentOrchestrator --rq2-batch
 *   java es.tfm.refactoring.ExperimentOrchestrator --rq2-tutor
 *   java es.tfm.refactoring.ExperimentOrchestrator --rq2-comparison
 *   java es.tfm.refactoring.ExperimentOrchestrator --rq3-campaign
 *   java es.tfm.refactoring.ExperimentOrchestrator --rq3-trap
 *   java es.tfm.refactoring.ExperimentOrchestrator --rq3-promptv2
 * </pre>
 *
 * <strong>Modificadores:</strong>
 * <pre>
 *   --dry-run          Ejecuta los pasos RQ3 en modo dry-run (sin API key)
 *   --output=&lt;dir&gt;     Directorio base para artefactos RQ3 (default: output/)
 * </pre>
 *
 * <strong>Requisitos:</strong>
 * <ul>
 *   <li>RQ2: ninguno — solo classpath del proyecto.</li>
 *   <li>RQ3 en modo live: variable de entorno {@code OPENAI_API_KEY} configurada.</li>
 * </ul>
 */
public class ExperimentOrchestrator {

    // -------------------------------------------------------------------------
    // Constantes
    // -------------------------------------------------------------------------

    private static final String STEP_SCAN           = "--scan";
    private static final String STEP_SCAN_REAL      = "--scan-real";
    private static final String STEP_SCAN_TUTOR     = "--scan-tutor";
    private static final String STEP_RQ2_BATCH      = "--rq2-batch";
    private static final String STEP_RQ2_TUTOR      = "--rq2-tutor";
    private static final String STEP_RQ2_COMPARISON = "--rq2-comparison";
    private static final String STEP_RQ2_SCAN       = "--rq2-scan";
    private static final String STEP_RQ3_CAMPAIGN   = "--rq3-campaign";
    private static final String STEP_RQ3_TRAP       = "--rq3-trap";
    private static final String STEP_RQ3_PROMPTV2   = "--rq3-promptv2";

    // -------------------------------------------------------------------------
    // Modelo de datos interno
    // -------------------------------------------------------------------------

    private record StepResult(String name, boolean ok, Duration elapsed, String error) {}

    // -------------------------------------------------------------------------
    // main
    // -------------------------------------------------------------------------

    public static void main(String[] args) throws Exception {

        // --- Parseo de argumentos --------------------------------------------
        boolean runScanReal      = false;
        boolean runScanTutor     = false;
        boolean runRq2Batch      = false;
        boolean runRq2Tutor      = false;
        boolean runRq2Comparison = false;
        boolean runRq2Scan       = false;
        boolean runRq3Campaign   = false;
        boolean runRq3Trap       = false;
        boolean runRq3Promptv2   = false;
        boolean dryRun           = false;
        String  outputBase       = "output";

        for (String arg : args) {
            switch (arg) {
                case "--all"            -> { runScanReal = runScanTutor = true;
                                             runRq2Batch = runRq2Tutor = runRq2Comparison = runRq2Scan = true;
                                             runRq3Campaign = runRq3Trap = runRq3Promptv2 = true; }
                case STEP_SCAN         -> { runScanReal = runScanTutor = true; }
                case STEP_SCAN_REAL    -> runScanReal      = true;
                case STEP_SCAN_TUTOR   -> runScanTutor     = true;
                case "--rq2"            -> { runScanReal = runScanTutor = true;
                                             runRq2Batch = runRq2Tutor = runRq2Comparison = runRq2Scan = true; }
                case "--rq3"            -> { runRq3Campaign = runRq3Trap = runRq3Promptv2 = true; }
                case STEP_RQ2_BATCH     -> runRq2Batch      = true;
                case STEP_RQ2_TUTOR     -> runRq2Tutor      = true;
                case STEP_RQ2_COMPARISON-> runRq2Comparison = true;
                case STEP_RQ2_SCAN      -> runRq2Scan       = true;
                case STEP_RQ3_CAMPAIGN  -> runRq3Campaign   = true;
                case STEP_RQ3_TRAP      -> runRq3Trap       = true;
                case STEP_RQ3_PROMPTV2  -> runRq3Promptv2   = true;
                case "--dry-run"        -> dryRun            = true;
                default -> {
                    if (arg.startsWith("--output=")) {
                        outputBase = arg.substring("--output=".length()).strip();
                    } else {
                        System.err.println("Argumento desconocido: " + arg);
                        printHelp();
                        System.exit(1);
                    }
                }
            }
        }

        if (!runScanReal && !runScanTutor
                && !runRq2Batch && !runRq2Tutor && !runRq2Comparison && !runRq2Scan
                && !runRq3Campaign && !runRq3Trap && !runRq3Promptv2) {
            printHelp();
            System.exit(0);
        }

        // --- Verificar OPENAI_API_KEY si se necesita RQ3 live ----------------
        boolean needsApiKey = (runRq3Campaign || runRq3Trap || runRq3Promptv2) && !dryRun;
        if (needsApiKey && isBlank(System.getenv("OPENAI_API_KEY"))) {
            System.err.println();
            System.err.println("ERROR: Se requiere OPENAI_API_KEY para ejecutar pasos RQ3 en modo live.");
            System.err.println("       Configúrala con:  $env:OPENAI_API_KEY = \"sk-...\"");
            System.err.println("       O usa --dry-run para ejecutar sin llamadas reales a la API.");
            System.exit(1);
        }

        // --- Modo RQ3 --------------------------------------------------------
        String rq3Mode = dryRun ? "dry-run" : "live";

        // --- Ejecución -------------------------------------------------------
        printBanner(dryRun);
        List<StepResult> results = new ArrayList<>();

        // Escaner masivo (antes de RQ2 para que el CSV este actualizado)
        if (runScanReal || runScanTutor) {
            Path cwd = Paths.get("").toAbsolutePath();
            List<String> scanArgs = new ArrayList<>();
            if (runScanReal)  scanArgs.addAll(buildRealProjectArgs(cwd));
            if (runScanTutor) scanArgs.addAll(buildTutorProjectArgs(cwd));

            if (scanArgs.isEmpty()) {
                System.out.println();
                System.out.println("  [SKIP] Escaner: no se encontraron directorios de proyectos.");
                System.out.println("         Clona los proyectos en tmp-projects/ y/o tmp-tutor-projects/");
                System.out.println("         (ver docs/run-commands.md secciones 3a y 3a-bis)");
            } else {
                String[] argsArray = scanArgs.toArray(new String[0]);
                results.add(run("Scan - Escaner masivo de proyectos OSS",
                        () -> CorpusScanRunner.main(argsArray)));
            }
        }

        // Generacion de corpus desde el CSV (si hubo scan o si se pide rq2-scan standalone)
        boolean generateCorpus = (runRq2Scan || runRq2Batch || runRq2Tutor || runRq2Comparison)
                && (runScanReal || runScanTutor);
        if (generateCorpus) {
            Path cwd = Paths.get("").toAbsolutePath();
            String capturedOutput = outputBase;  // effectively final para la lambda
            results.add(run("Scan - Generacion de corpus (CorpusFileGenerator)",
                    () -> generateCorpusFiles(cwd, capturedOutput)));
        }

        if (runRq2Batch) {
            results.add(run("RQ2 - Batch (piloto + real, STRICT)",
                    () -> Rq2BatchExecutor.main(new String[]{})));
        }
        if (runRq2Tutor) {
            results.add(run("RQ2 - Tutor corpus",
                    () -> Rq2TutorBatchExecutor.main(new String[]{})));
        }
        if (runRq2Comparison) {
            results.add(run("RQ2 - Comparativa STRICT vs RELAXED",
                    () -> Rq2ComparisonExecutor.main(new String[]{})));
        }
        if (runRq2Scan) {
            String scanCorpusDir = outputBase + "/corpus-scan/generated-corpus";
            String rq2ScanDir    = outputBase + "/rq2-scan";
            results.add(run("RQ2 - Corpus generado por escaner (scanner-generated)",
                    () -> Rq2ScannerBatchExecutor.main(
                            new String[]{rq2ScanDir, scanCorpusDir})));
        }
        if (runRq3Campaign) {
            String outDir = outputBase + "/rq3-campaign-real-phase9";
            results.add(run("RQ3 - Campana principal (" + rq3Mode + ")",
                    () -> CampaignExecutor.main(new String[]{"--mode=" + rq3Mode, outDir})));
        }
        if (runRq3Trap) {
            results.add(run("RQ3 - Campana trampa / falsos positivos",
                    () -> Rq3TrapCampaignExecutor.main(new String[]{})));
        }
        if (runRq3Promptv2) {
            results.add(run("RQ3 - Comparativa prompt v2.0 (few-shot)",
                    () -> Rq3PromptV2Executor.main(new String[]{})));
        }

        // --- Resumen final ---------------------------------------------------
        printSummary(results);

        boolean anyFailed = results.stream().anyMatch(r -> !r.ok());
        System.exit(anyFailed ? 1 : 0);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    @FunctionalInterface
    private interface ThrowingRunnable {
        void run() throws Exception;
    }

    private static StepResult run(String name, ThrowingRunnable step) {
        System.out.println();
        System.out.println("----------------------------------------------------------------");
        System.out.printf ("  %s%n", name);
        System.out.println("----------------------------------------------------------------");
        Instant start = Instant.now();
        try {
            step.run();
            Duration elapsed = Duration.between(start, Instant.now());
            System.out.println("-> OK  [" + formatDuration(elapsed) + "]");
            return new StepResult(name, true, elapsed, null);
        } catch (Exception e) {
            Duration elapsed = Duration.between(start, Instant.now());
            System.err.println("-> FAILED  [" + formatDuration(elapsed) + "]: " + e.getMessage());
            e.printStackTrace(System.err);
            return new StepResult(name, false, elapsed, e.getMessage());
        }
    }

    private static void printBanner(boolean dryRun) {
        System.out.println();
        System.out.println("================================================================");
        System.out.println("  TFM -- Orquestador del Experimento");
        System.out.println("  RQ1/RQ2/RQ3: Refactorizacion de condicionales anidados (Java)");
        if (dryRun) {
            System.out.println("  Modo: DRY-RUN (sin llamadas reales a la API)");
        }
        System.out.println("================================================================");
    }

    private static void printSummary(List<StepResult> results) {
        System.out.println();
        System.out.println("================================================================");
        System.out.println("  RESUMEN FINAL");
        System.out.println("================================================================");
        long passed = results.stream().filter(StepResult::ok).count();
        long failed = results.size() - passed;
        for (StepResult r : results) {
            String status = r.ok() ? "[OK]  " : "[FAIL]";
            System.out.printf("  %s  %-50s  %s%n",
                    status, r.name(), formatDuration(r.elapsed()));
            if (!r.ok() && r.error() != null) {
                System.out.printf("         Error: %s%n", r.error());
            }
        }
        System.out.println();
        System.out.printf("  Resultado: %d/%d pasos completados%n", passed, results.size());
        if (failed > 0) {
            System.out.printf("  %d paso(s) fallaron - revisa los mensajes de error anteriores%n", failed);
        }
        System.out.println("================================================================");
    }

    private static void printHelp() {
        System.out.println();
        System.out.println("Uso: java es.tfm.refactoring.ExperimentOrchestrator [opciones]");
        System.out.println();
        System.out.println("Grupos de pasos:");
        System.out.println("  --all             Scan + RQ2 completo + RQ3 completo");
        System.out.println("  --scan            Escanear todos los proyectos disponibles (real + tutor)");
        System.out.println("  --rq2             Scan + genera corpus + RQ2 (batch + tutor + comparison + scan)");
        System.out.println("  --rq3             Ejecuta los 3 pasos de RQ3 (campaign + trap + promptv2)");
        System.out.println();
        System.out.println("Pasos individuales:");
        System.out.println("  --scan-real       Escanear proyectos en tmp-projects/ (corpus real)");
        System.out.println("  --scan-tutor      Escanear proyectos en tmp-tutor-projects/ (paper tutor)");
        System.out.println("  --rq2-batch       RQ2: pipeline piloto + real (modo STRICT)");
        System.out.println("  --rq2-tutor       RQ2: corpus del tutor");
        System.out.println("  --rq2-comparison  RQ2: comparativa STRICT vs RELAXED");
        System.out.println("  --rq2-scan        RQ2: corpus generado por escaner");
        System.out.println("  --rq3-campaign    RQ3: campana principal (gpt-4o + gpt-4.1)");
        System.out.println("  --rq3-trap        RQ3: campana trampa / falsos positivos");
        System.out.println("  --rq3-promptv2    RQ3: comparativa prompt v2.0 few-shot");
        System.out.println();
        System.out.println("Modificadores:");
        System.out.println("  --dry-run         Pasos RQ3 sin llamadas reales a la API");
        System.out.println("  --output=<dir>    Directorio base de salida (default: output)");
        System.out.println();
        System.out.println("Requisitos:");
        System.out.println("  Scan: proyectos clonados en tmp-projects/ y/o tmp-tutor-projects/");
        System.out.println("        (ver docs/run-commands.md secciones 3a y 3a-bis)");
        System.out.println("  RQ3 modo live: variable de entorno OPENAI_API_KEY configurada.");
        System.out.println();
        System.out.println("Pipeline RQ2 completo (--rq2):");
        System.out.println("  1. Escanea tmp-projects/ y tmp-tutor-projects/ (si existen)");
        System.out.println("  2. Genera corpus en output/corpus-scan/generated-corpus/");
        System.out.println("  3. Rq2BatchExecutor     -> output/rq2-batch/");
        System.out.println("  4. Rq2TutorBatchExecutor -> output/rq2-tutor/");
        System.out.println("  5. Rq2ComparisonExecutor -> output/rq2-comparison/");
        System.out.println("  6. Rq2ScannerBatchExecutor -> output/rq2-scan/");
        System.out.println();
        System.out.println("Ejemplos:");
        System.out.println("  # RQ2 completo (scan + genera corpus + analisis):");
        System.out.println("  java es.tfm.refactoring.ExperimentOrchestrator --rq2");
        System.out.println();
        System.out.println("  # Experimento completo (scan + RQ2 + RQ3):");
        System.out.println("  java es.tfm.refactoring.ExperimentOrchestrator --all");
        System.out.println();
        System.out.println("  # Solo escanear proyectos:");
        System.out.println("  java es.tfm.refactoring.ExperimentOrchestrator --scan");
        System.out.println();
        System.out.println("  # RQ3 sin consumir creditos (dry-run):");
        System.out.println("  java es.tfm.refactoring.ExperimentOrchestrator --rq3 --dry-run");
    }

    private static String formatDuration(Duration d) {
        long s = d.getSeconds();
        long ms = d.toMillisPart();
        if (s == 0) return ms + "ms";
        if (s < 60) return s + "s " + ms + "ms";
        return (s / 60) + "m " + (s % 60) + "s";
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    /** Genera ficheros corpus en {@code output/corpus-scan/generated-corpus/} desde el CSV. */
    private static void generateCorpusFiles(Path cwd, String outputBase) throws Exception {
        Path csvPath = cwd.resolve(outputBase).resolve("corpus-scan/corpus-scan-results.csv");
        if (!Files.exists(csvPath)) {
            System.out.println("  [SKIP] CSV no encontrado: " + csvPath);
            return;
        }
        Path genDir = cwd.resolve(outputBase).resolve("corpus-scan/generated-corpus");
        Files.createDirectories(genDir);

        // Ejecutar CorpusFileGenerator pasando todas las raices disponibles en una
        // sola llamada, de modo que la busqueda cubra ambas carpetas de proyectos.
        Path realRoot  = cwd.resolve("tmp-projects");
        Path tutorRoot = cwd.resolve("tmp-tutor-projects");

        List<String> cfgArgs = new ArrayList<>();
        cfgArgs.add(csvPath.toString());
        if (Files.isDirectory(realRoot))  cfgArgs.add(realRoot.toString());
        if (Files.isDirectory(tutorRoot)) cfgArgs.add(tutorRoot.toString());
        cfgArgs.add(genDir.toString());

        if (cfgArgs.size() < 3) {
            System.out.println("  [SKIP] No hay directorios tmp-projects/ ni tmp-tutor-projects/");
        } else {
            CorpusFileGenerator.main(cfgArgs.toArray(new String[0]));
        }
    }

    // -------------------------------------------------------------------------
    // Proyectos conocidos para el escaner
    // -------------------------------------------------------------------------

    /**
     * Construye los argumentos {@code --project} para los proyectos del corpus real
     * en {@code tmp-projects/}. Solo incluye los que existen en disco.
     */
    private static List<String> buildRealProjectArgs(Path cwd) {
        Path base = cwd.resolve("tmp-projects");
        if (!Files.isDirectory(base)) return List.of();

        String[][] entries = {
            {"commons-lang",     "commons-lang/src/main/java"},
            {"commons-io",       "commons-io/src/main/java"},
            {"commons-math",     "commons-math/src/main/java"},
            {"ant",              "ant/src/main"},
            {"jmetal",           "jmetal/jmetal-problem/src/main/java"},
            {"bytecode-viewer",  "bytecode-viewer/src/main/java"},
            {"commons-compress", "commons-compress/src/main/java"},
        };
        return projectArgs(base, entries);
    }

    /**
     * Construye los argumentos {@code --project} para los 10 proyectos del paper
     * de Saborido et al. 2022 en {@code tmp-tutor-projects/}.
     * Solo incluye los que existen en disco.
     */
    private static List<String> buildTutorProjectArgs(Path cwd) {
        Path base = cwd.resolve("tmp-tutor-projects");
        if (!Files.isDirectory(base)) return List.of();

        String[][] entries = {
            {"cybercaptor",    "cybercaptor-server/src/main/java"},
            {"moea",           "MOEAFramework/src"},
            {"iotbroker",      "iotbroker/src/main/java"},
            {"knowage",        "knowage-server/knowage-core/src/main/java"},
            {"fastjson",       "fastjson/src/main/java"},
            {"fiware-commons", "fiware-commons/src/main/java"},
            {"jedis",          "jedis/src/main/java"},
            {"aioteslake",     "aioteslake/src/main/java"},
        };
        return projectArgs(base, entries);
    }

    /** Filtra las entradas cuyo directorio existe y devuelve los args {@code --project}. */
    private static List<String> projectArgs(Path base, String[][] entries) {
        List<String> args = new ArrayList<>();
        for (String[] e : entries) {
            Path src = base.resolve(e[1].replace('/', java.io.File.separatorChar));
            if (Files.isDirectory(src)) {
                args.add("--project");
                args.add(e[0] + "=" + src.toAbsolutePath());
            }
        }
        return args;
    }
}
