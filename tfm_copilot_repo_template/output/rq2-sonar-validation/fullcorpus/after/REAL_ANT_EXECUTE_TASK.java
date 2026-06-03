public class REAL_ANT_EXECUTE_TASK {
void executeTask(boolean projectValid, boolean taskEnabled, String taskName) {
    if (projectValid) {
        System.out.println("Preparing: " + taskName);
        if (taskEnabled) {
            System.out.println("Executing: " + taskName);
        }
    }
}
}

