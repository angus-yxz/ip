/**
 * Ends the current Mona session after showing a farewell message.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that exits Mona.
     */
    public ExitCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage("✨ Farewell. May the stars guide you until we meet again.");
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
