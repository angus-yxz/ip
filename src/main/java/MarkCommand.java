/**
 * Marks a numbered task as complete.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that marks the given one-based task number as complete.
     *
     * @param taskNumber the one-based number of the task to mark.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MonaException {
        if (tasks.isEmpty()) {
            throw MonaException.withHint(
                    "❌ There are no tasks yet whose fate can be altered.",
                    "todo read book");
        }

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw MonaException.withHint(
                    "❌ No such fate is written in the constellations. Please enter a valid task number.",
                    "list");
        }

        Task task = tasks.get(taskNumber - 1);
        task.markAsDone();
        storage.save(tasks.asList());
        ui.showMessage("✅ The stars align. I've marked this task as done:\n  " + task);
    }
}
