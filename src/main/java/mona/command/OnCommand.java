package mona.command;

import mona.storage.Storage;
import mona.task.TaskDateTime;
import mona.task.TaskList;
import mona.ui.Ui;

/**
 * Shows tasks that occur on a specified date.
 */
public class OnCommand extends Command {
    private final TaskDateTime date;

    /**
     * Creates a command that shows tasks occurring on the given date.
     *
     * @param date the date whose tasks should be shown.
     */
    public OnCommand(TaskDateTime date) {
        this.date = date;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        showMatchingTasks(tasks, date.toLocalDate(),
                "✨ On " + date + ", the stars reveal:", ui);
    }
}
