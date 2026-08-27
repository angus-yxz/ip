package mona.command;

import java.util.ArrayList;

import mona.storage.Storage;
import mona.task.Task;
import mona.task.TaskList;
import mona.ui.Ui;

/**
 * Shows tasks whose descriptions contain a given keyword.
 */
public class FindCommand extends Command {
    private final String keyword;

    /**
     * Creates a command that finds tasks containing the given keyword.
     *
     * @param keyword the keyword to find in task descriptions.
     */
    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ArrayList<Task> matchingTasks = tasks.find(keyword);
        if (matchingTasks.isEmpty()) {
            ui.showMessage("✨ No matching tasks are written in the constellations.");
            return;
        }

        StringBuilder taskList = new StringBuilder("✨ Here are the matching tasks in your list:");
        for (int index = 0; index < matchingTasks.size(); index++) {
            taskList.append(System.lineSeparator())
                    .append(index + 1)
                    .append(".")
                    .append(matchingTasks.get(index));
        }
        ui.showMessage(taskList.toString());
    }
}
