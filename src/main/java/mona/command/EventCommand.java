package mona.command;

import mona.MonaException;
import mona.storage.Storage;
import mona.task.Event;
import mona.task.TaskDateTime;
import mona.task.TaskList;
import mona.ui.Ui;

/**
 * Adds an event to the task list.
 */
public class EventCommand extends Command {
    private final String description;
    private final TaskDateTime start;
    private final TaskDateTime end;

    /**
     * Creates a command that adds an event with the given description and date range.
     *
     * @param description the event description.
     * @param start the date and optional time when the event starts.
     * @param end the date and optional time when the event ends.
     */
    public EventCommand(String description, TaskDateTime start, TaskDateTime end) {
        this.description = description;
        this.start = start;
        this.end = end;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MonaException {
        addTask(tasks, new Event(description, start, end), storage, ui);
    }
}
