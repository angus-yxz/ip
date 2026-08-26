/**
 * Adds a deadline to the task list.
 */
public class DeadlineCommand extends Command {
    private final String description;
    private final TaskDateTime deadline;

    /**
     * Creates a command that adds a deadline with the given description and due date.
     *
     * @param description the deadline description.
     * @param deadline the date and optional time by which the task is due.
     */
    public DeadlineCommand(String description, TaskDateTime deadline) {
        this.description = description;
        this.deadline = deadline;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws MonaException {
        addTask(tasks, new Deadline(description, deadline), storage, ui);
    }
}
