import java.time.LocalDate;

/**
 * Represents an executable command entered by the user.
 */
public abstract class Command {
    /**
     * Executes this command against the current task list.
     *
     * @param tasks the current task list.
     * @param ui the user interface through which messages are shown.
     * @param storage the storage used to persist task changes.
     * @throws MonaException if the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws MonaException;

    /**
     * Returns whether this command should end the application.
     *
     * @return {@code true} if the application should exit, or {@code false} otherwise.
     */
    public boolean isExit() {
        return false;
    }

    protected static void addTask(TaskList tasks, Task task, Storage storage, Ui ui)
            throws MonaException {
        tasks.add(task);
        // Save before reporting success, so a failed save is reported as an error
        // instead of falsely telling the user the task was added.
        storage.save(tasks.asList());
        ui.showMessage("✅ Your fate is rewritten. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }

    protected static void showMatchingTasks(TaskList tasks, LocalDate date, String header, Ui ui) {
        StringBuilder taskList = new StringBuilder(header);
        int matchCount = 0;
        for (Task task : tasks) {
            if (task.occursOn(date)) {
                matchCount++;
                taskList.append(System.lineSeparator())
                        .append(matchCount)
                        .append(".")
                        .append(task);
            }
        }
        if (matchCount == 0) {
            taskList.append(System.lineSeparator()).append("  (No fates found on this date.)");
        }
        ui.showMessage(taskList.toString());
    }
}
