package es.tfm.refactoring;

import es.tfm.refactoring.experiment.Rq2StructuralBatchExecutor;
import es.tfm.refactoring.llm.CampaignExecutor;
import es.tfm.refactoring.llm.Rq3StructuralReevalExecutor;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Orquestador del experimento reportado del TFM.
 * <p>
 * Encadena, con un único comando, los pasos que producen los artefactos
 * reportados en la memoria, todos en modo {@link es.tfm.refactoring.detection.DetectionMode#STRUCTURAL}
 * (precondiciones estructurales P1–P4):
 * <ul>
 *   <li><strong>RQ2:</strong> lote determinista
 *       ({@link Rq2StructuralBatchExecutor}) → {@code output/rq2-structural/}.</li>
 *   <li><strong>RQ3:</strong> campaña con los modelos de lenguaje
 *       ({@link CampaignExecutor}) → {@code output/rq3-campaign-real-phase9/} y,
 *       a continuación, reevaluación del oráculo en modo STRUCTURAL
 *       ({@link Rq3StructuralReevalExecutor}) → {@code output/rq3-structural/}.</li>
 * </ul>
 *
 * <strong>Uso:</strong>
 * <pre>
 *   java es.tfm.refactoring.ExperimentOrchestrator --all
 *   java es.tfm.refactoring.ExperimentOrchestrator --rq2
 *   java es.tfm.refactoring.ExperimentOrchestrator --rq3 [--dry-run]
 * </pre>
 *
 * <strong>Modificadores:</strong>
 * <pre>
 *   --dry-run          Ejecuta RQ3 sin llamar a la API (usa respuestas grabadas).
 *   --output=&lt;dir&gt;     Directorio base de salida (default: output).
 * </pre>
 *
 * <strong>Requisitos:</strong> RQ3 en modo live requiere la variable de entorno
 * {@code OPENAI_API_KEY}. La construcción del corpus desde proyectos externos
 * (escáner) y las variantes exploratorias (corpus del tutor, comparativa
 * STRICT/RELAXED, campaña trampa, prompt v2.0) quedan fuera de este orquestador:
 * se ejecutan con sus propias clases (véase el README).
 */
public class ExperimentOrchestrator {

    private record StepResult(String name, boolean ok, Duration elapsed, String error) {}

    public static void main(String[] args) throws Exception {

        boolean runRq2     = false;
        boolean runRq3     = false;
        boolean dryRun     = false;
        String  outputBase = "output";

        for (String arg : args) {
            switch (arg) {
                case "--all"     -> { runRq2 = true; runRq3 = true; }
                case "--rq2"     -> runRq2 = true;
                case "--rq3"     -> runRq3 = true;
                case "--dry-run" -> dryRun = true;
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

        if (!runRq2 && !runRq3) {
            printHelp();
            System.exit(0);
        }

        // Verificar OPENAI_API_KEY si se necesita RQ3 en modo live.
        boolean needsApiKey = runRq3 && !dryRun;
        if (needsApiKey && isBlank(System.getenv("OPENAI_API_KEY"))) {
            System.err.println();
            System.err.println("ERROR: Se requiere OPENAI_API_KEY para ejecutar RQ3 en modo live.");
            System.err.println("       Configurala con:  $env:OPENAI_API_KEY = \"sk-...\"");
            System.err.println("       O usa --dry-run para ejecutar sin llamadas reales a la API.");
            System.exit(1);
        }

        String rq3Mode = dryRun ? "dry-run" : "live";

        printBanner(dryRun);
        List<StepResult> results = new ArrayList<>();

        if (runRq2) {
            String rq2Dir = outputBase + "/rq2-structural";
            results.add(run("RQ2 - Batch estructural (piloto + real, P1-P4)",
                    () -> Rq2StructuralBatchExecutor.main(new String[]{rq2Dir})));
        }

        if (runRq3) {
            String campaignDir = outputBase + "/rq3-campaign-real-phase9";
            results.add(run("RQ3 - Campana principal (" + rq3Mode + ")",
                    () -> CampaignExecutor.main(
                            new String[]{"--mode=" + rq3Mode, "--cases=all", campaignDir})));

            // Reevaluacion en modo STRUCTURAL (P1-P4) sobre la evidencia grabada:
            // produce los veredictos reportados sin volver a llamar a la API.
            String evidenceJson  = campaignDir + "/rq3-full-evidence.json";
            String structuralDir = outputBase + "/rq3-structural";
            results.add(run("RQ3 - Reevaluacion estructural (P1-P4)",
                    () -> Rq3StructuralReevalExecutor.main(
                            new String[]{evidenceJson, structuralDir})));
        }

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
        System.out.println("  TFM -- Orquestador del experimento (modo STRUCTURAL)");
        System.out.println("  RQ2/RQ3: Refactorizacion de condicionales anidados (Java)");
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
        System.out.println("Pasos:");
        System.out.println("  --all             RQ2 + RQ3 (artefactos reportados, modo STRUCTURAL)");
        System.out.println("  --rq2             RQ2: lote determinista -> output/rq2-structural/");
        System.out.println("  --rq3             RQ3: campana + reevaluacion estructural -> output/rq3-structural/");
        System.out.println();
        System.out.println("Modificadores:");
        System.out.println("  --dry-run         RQ3 sin llamadas reales a la API (respuestas grabadas)");
        System.out.println("  --output=<dir>    Directorio base de salida (default: output)");
        System.out.println();
        System.out.println("Requisitos:");
        System.out.println("  RQ3 modo live: variable de entorno OPENAI_API_KEY configurada.");
        System.out.println();
        System.out.println("Ejemplos:");
        System.out.println("  java es.tfm.refactoring.ExperimentOrchestrator --all");
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
}
