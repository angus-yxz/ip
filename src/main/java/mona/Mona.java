package mona;

import mona.command.Command;
import mona.parser.Parser;
import mona.storage.Storage;
import mona.task.TaskList;
import mona.ui.Ui;

/**
 * Provides a command-line task manager named Mona.
 */
public class Mona {
    // Relative to the working directory the program is run from, per the project's
    // requirement to avoid absolute, OS-specific paths.
    private static final String DATA_FILE_PATH = "./data/mona.txt";

    private final Storage storage;
    private final TaskList tasks;
    private final RecordingUi recordingUi = new RecordingUi();

    /**
     * Creates Mona using the default task data file.
     */
    public Mona() {
        this(DATA_FILE_PATH);
    }

    Mona(String dataFilePath) {
        storage = new Storage(dataFilePath);
        TaskList loadedTasks;
        try {
            loadedTasks = new TaskList(storage.load());
        } catch (MonaException exception) {
            // The GUI cannot display a response before its window is ready. As with the
            // text UI, continue with an empty in-memory list if existing data cannot load.
            loadedTasks = new TaskList();
        }
        tasks = loadedTasks;
    }

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

    /**
     * Executes a user command and returns Mona's response for display by a GUI.
     *
     * @param input the command entered by the user.
     * @return Mona's response, including any validation or storage error.
     */
    public String getResponse(String input) {
        try {
            Command command = Parser.parse(input);
            command.execute(tasks, recordingUi, storage);
        } catch (MonaException exception) {
            recordingUi.showMessage(exception.getMessage());
        }
        return recordingUi.getLastMessage();
    }

    /**
     * Records the most recent message instead of showing it in the console.
     */
    private static class RecordingUi extends Ui {
        private String lastMessage;

        @Override
        public void showMessage(String text) {
            lastMessage = text;
        }

        public String getLastMessage() {
            return lastMessage;
        }
    }
}
