package mona.command;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.PrintStream;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import mona.task.Deadline;
import mona.task.TaskDateTime;
import mona.task.TaskList;
import mona.task.Todo;
import mona.ui.Ui;

/**
 * Tests {@link InCommand}.
 */
public class InCommandTest {
    private final PrintStream originalOutput = System.out;

    @AfterEach
    public void restoreOutput() {
        System.setOut(originalOutput);
    }

    @Test
    public void execute_taskDueInGivenDays_showsMatchingTask() {
        Deadline dueToday = new Deadline("submit report", TaskDateTime.parse(LocalDate.now().toString()));
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));
        tasks.add(dueToday);
        RecordingUi ui = new RecordingUi();

        new InCommand(0).execute(tasks, ui, null);

        assertEquals("✨ In 0 day(s), the stars reveal:"
                + System.lineSeparator() + "1." + dueToday,
                ui.getLastMessage());
    }

    @Test
    public void execute_noTaskDueInGivenDays_showsNoMatchMessage() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("submit report", TaskDateTime.parse(LocalDate.now().plusDays(1).toString())));
        RecordingUi ui = new RecordingUi();

        new InCommand(0).execute(tasks, ui, null);

        assertEquals("✨ In 0 day(s), the stars reveal:"
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
