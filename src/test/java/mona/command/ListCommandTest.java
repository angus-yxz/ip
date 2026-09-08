package mona.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import mona.MonaException;
import mona.task.Deadline;
import mona.task.TaskDateTime;
import mona.task.TaskList;
import mona.task.Todo;
import mona.ui.Ui;

/**
 * Tests {@link ListCommand}.
 */
public class ListCommandTest {
    private final PrintStream originalOutput = System.out;

    @AfterEach
    public void restoreOutput() {
        System.setOut(originalOutput);
    }

    @Test
    public void execute_tasksProvided_listsTasksInCurrentOrder() throws MonaException {
        TaskList tasks = createTasksInInsertionOrder();
        RecordingUi ui = new RecordingUi();

        new ListCommand().execute(tasks, ui, null);

        assertEquals("✨ Here is what the stars reveal:"
                + System.lineSeparator() + "1.[T][ ] read book"
                + System.lineSeparator() + "2.[D][ ] submit report (by: Oct 16 2019)",
                ui.getLastMessage());
    }

    private static TaskList createTasksInInsertionOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Deadline("submit report", TaskDateTime.parse("2019-10-16")));
        return tasks;
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
