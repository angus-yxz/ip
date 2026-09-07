package mona.command;

import mona.MonaException;
import mona.storage.Storage;
import mona.task.Task;
import mona.task.TaskList;
import mona.ui.Ui;

/**
 * Marks a numbered task as incomplete.
 */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that marks the given one-based task number as incomplete.
     *
     * @param taskNumber the one-based number of the task to unmark.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MonaException {
        int taskIndex = toTaskIndex(tasks, taskNumber,
                "❌ There are no tasks yet whose fate can be altered.");
        Task task = tasks.get(taskIndex);
        task.markAsNotDone();
        storage.save(tasks.asList());
        ui.showMessage("❌ The constellation fades. I've marked this task as not done yet:\n  " + task);
    }
}
