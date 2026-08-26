package mona;

/**
 * Deletes a numbered task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that deletes the given one-based task number.
     *
     * @param taskNumber the one-based number of the task to delete.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MonaException {
        if (tasks.isEmpty()) {
            throw MonaException.withHint(
                    "❌ The constellations remain still. There are no tasks to be deleted.",
                    "todo read book");
        }

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw MonaException.withHint(
                    "❌ No such fate is written in the constellations. Please enter a valid task number.",
                    "list");
        }

        Task deletedTask = tasks.delete(taskNumber - 1);
        storage.save(tasks.asList());
        ui.showMessage("✅ A fate fades from the constellations. I've removed this task:\n  " + deletedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }
}
