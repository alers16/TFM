// @caseId REAL_ANT_EXECUTE_TASK
// @origin ant-1.10.12
// @project Apache Ant
// @file Task.java
// @method execute(Project, boolean)
// @license Apache-2.0
// @sonarCCBefore 3
// @description Método con if anidado y múltiples sentencias en bloque externo — no elegible por P2

/**
 * Patrón representativo de Apache Ant 1.10.12 — ejecución de tarea.
 * Contiene if anidado con múltiples sentencias en el bloque interno.
 * Caso NO ELEGIBLE: bloque externo tiene múltiples sentencias (precondición P2).
 */
class RealAntExecuteTask {

    void executeTask(boolean projectValid, boolean taskEnabled, String taskName) {
        if (projectValid) {
            System.out.println("Preparing: " + taskName);
            if (taskEnabled) {
                System.out.println("Executing: " + taskName);
            }
        }
    }
}
