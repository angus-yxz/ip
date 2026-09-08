package mona.command;

import mona.MonaException;
import mona.storage.Storage;
import mona.task.TaskList;
import mona.ui.Ui;

/**
 * Sorts dated tasks chronologically and places undated tasks after them.
 */
public class SortCommand extends Command {
    /**
     * Creates a command that sorts the current task list chronologically.
     */
    public SortCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MonaException {
        tasks.sortChronologically();
        storage.save(tasks.asList());
        ui.showMessage("✅ The constellations align. Your tasks are now in chronological order.");
    }
}
