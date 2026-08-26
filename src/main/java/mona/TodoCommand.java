package mona;

/**
 * Adds a todo to the task list.
 */
public class TodoCommand extends Command {
    private final String description;

    /**
     * Creates a command that adds a todo with the given description.
     *
     * @param description the todo description.
     */
    public TodoCommand(String description) {
        this.description = description;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MonaException {
        addTask(tasks, new Todo(description), storage, ui);
    }
}
