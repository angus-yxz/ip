package mona;

/**
 * Provides a command-line task manager named Mona.
 */
public class Mona {
    // Relative to the working directory the program is run from, per the project's
    // requirement to avoid absolute, OS-specific paths.
    private static final String DATA_FILE_PATH = "./data/mona.txt";

    /**
     * Starts Mona and processes task commands until the user exits.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(DATA_FILE_PATH);
        TaskList tasks;
        try {
            tasks = new TaskList(storage.load());
        } catch (MonaException exception) {
            // Loading failed (e.g. the data file could not be read), so start with an
            // empty list rather than crashing; the user's tasks for this session are
            // still tracked in memory even though the earlier save could not be recovered.
            ui.showMessage(exception.getMessage());
            tasks = new TaskList();
        }

        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                Command command = Parser.parse(fullCommand);
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (MonaException exception) {
                ui.showMessage(exception.getMessage());
            }
        }
    }
}
