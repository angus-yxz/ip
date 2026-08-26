/**
 * Shows every task in its current list order.
 */
public class ListCommand extends Command {
    /**
     * Creates a command that lists all tasks.
     */
    public ListCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        StringBuilder taskList = new StringBuilder("✨ Here is what the stars reveal:");
        for (int index = 0; index < tasks.size(); index++) {
            taskList.append(System.lineSeparator())
                    .append(index + 1)
                    .append(".")
                    .append(tasks.get(index));
        }
        ui.showMessage(taskList.toString());
    }
}
