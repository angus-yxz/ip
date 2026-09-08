package mona.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import mona.MonaException;
import mona.storage.Storage;
import mona.task.Deadline;
import mona.task.Task;
import mona.task.TaskDateTime;
import mona.task.TaskList;
import mona.task.Todo;
import mona.ui.Ui;

/**
 * Tests {@link SortCommand}.
 */
public class SortCommandTest {
    private final PrintStream originalOutput = System.out;

    @TempDir
    private Path temporaryDirectory;

    @AfterEach
    public void restoreOutput() {
        System.setOut(originalOutput);
    }

    @Test
    public void execute_unsortedTasks_sortsAndStoresTaskOrder() throws MonaException {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("submit report", TaskDateTime.parse("2019-10-16")));
        RecordingUi ui = new RecordingUi();
        Storage storage = new Storage(temporaryDirectory.resolve("mona.txt").toString());

        new SortCommand().execute(tasks, ui, storage);

        List<Task> sortedTasks = tasks.asList();
        assertEquals("submit report", sortedTasks.get(0).getDescription());
        assertEquals("read book", sortedTasks.get(1).getDescription());
        assertEquals("✅ The constellations align. Your tasks are now in chronological order.",
                ui.getLastMessage());
        assertEquals(List.of("submit report", "read book"), storage.load().stream()
                .map(Task::getDescription)
                .toList());
    }

    /**
     * Records the most recent message instead of printing it.
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
