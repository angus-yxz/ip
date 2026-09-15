package mona.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PrintStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import mona.task.Deadline;
import mona.task.Event;
import mona.task.TaskDateTime;
import mona.task.TaskList;
import mona.task.Todo;
import mona.ui.Ui;

/**
 * Tests {@link OnCommand}.
 */
public class OnCommandTest {
    private final PrintStream originalOutput = System.out;

    @AfterEach
    public void restoreOutput() {
        System.setOut(originalOutput);
    }

    @Test
    public void execute_tasksOccurringOnGivenDate_showsMatchesNumberedFromOne() {
        TaskDateTime onDate = TaskDateTime.parse("2019-10-15");
        Deadline deadline = new Deadline("submit report", onDate);
        Event event = new Event("conference",
                TaskDateTime.parse("2019-10-14"), TaskDateTime.parse("2019-10-16"));
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(deadline);
        tasks.add(event);
        RecordingUi ui = new RecordingUi();

        new OnCommand(onDate).execute(tasks, ui, null);

        assertEquals("✨ On Oct 15 2019, the stars reveal:"
                + System.lineSeparator() + "1." + deadline
                + System.lineSeparator() + "2." + event,
                ui.getLastMessage());
    }

    @Test
    public void execute_noTasksOccurringOnGivenDate_showsNoMatchMessage() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        RecordingUi ui = new RecordingUi();

        new OnCommand(TaskDateTime.parse("2019-10-15")).execute(tasks, ui, null);

        assertEquals("✨ On Oct 15 2019, the stars reveal:"
                + System.lineSeparator() + "  (No fates found on this date.)",
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
