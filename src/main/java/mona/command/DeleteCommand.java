package mona.command;

import mona.MonaException;
import mona.storage.Storage;
import mona.task.Task;
import mona.task.TaskList;
import mona.ui.Ui;

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
        int taskIndex = toTaskIndex(tasks, taskNumber,
                "❌ The constellations remain still. There are no tasks to be deleted.");
        Task deletedTask = tasks.delete(taskIndex);
        storage.save(tasks.asList());
        ui.showMessage("✅ A fate fades from the constellations. I've removed this task:\n  " + deletedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }
}
