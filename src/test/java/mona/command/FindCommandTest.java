package mona.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import mona.task.Deadline;
import mona.task.TaskDateTime;
import mona.task.TaskList;
import mona.task.Todo;
import mona.ui.Ui;

/**
 * Tests {@link FindCommand}.
 */
public class FindCommandTest {
    private final PrintStream originalOutput = System.out;

    @AfterEach
    public void restoreOutput() {
        System.setOut(originalOutput);
    }

    @Test
    public void execute_matchingTasks_showsMatchesNumberedFromOne() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(new Todo("watch movie"));
        tasks.add(new Deadline("return book", TaskDateTime.parse("2019-06-06")));
        RecordingUi ui = new RecordingUi();

        new FindCommand("book").execute(tasks, ui, null);

        assertEquals("✨ Here are the matching tasks in your list:"
                + System.lineSeparator() + "1.[T][ ] read book"
                + System.lineSeparator() + "2.[D][ ] return book (by: Jun 6 2019)",
                ui.getLastMessage());
    }

    @Test
    public void execute_noMatchingTasks_showsNoMatchMessage() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        RecordingUi ui = new RecordingUi();

        new FindCommand("movie").execute(tasks, ui, null);

        assertEquals("✨ No matching tasks are written in the constellations.",
                ui.getLastMessage());
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
