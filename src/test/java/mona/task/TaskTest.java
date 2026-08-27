package mona.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Task}.
 */
public class TaskTest {
    @Test
    public void constructor_newTask_isNotDone() {
        Task task = new Task("read book");

        assertFalse(task.isDone());
    }

    @Test
    public void getStatusIcon_newTask_returnsSpace() {
        Task task = new Task("read book");

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void markAsDone_newTask_isDone() {
        Task task = new Task("read book");

        task.markAsDone();

        assertTrue(task.isDone());
    }

    @Test
    public void getStatusIcon_markedDone_returnsX() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("X", task.getStatusIcon());
    }

    @Test
    public void markAsNotDone_doneTask_isNotDone() {
        Task task = new Task("read book");
        task.markAsDone();

        task.markAsNotDone();

        assertFalse(task.isDone());
    }

    @Test
    public void getStatusIcon_markedAsNotDone_returnsSpace() {
        Task task = new Task("read book");
        task.markAsDone();
        task.markAsNotDone();

        assertEquals(" ", task.getStatusIcon());
    }

    @Test
    public void getDescription_descriptionProvided_returnsUnchangedDescription() {
        Task task = new Task("Read Book Exactly");

        assertEquals("Read Book Exactly", task.getDescription());
    }

    @Test
    public void occursOn_anyDate_returnsFalse() {
        Task task = new Task("read book");

        assertFalse(task.occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void toSaveFormat_notDone_returnsZeroDoneFlag() {
        Task task = new Task("read book");

        assertEquals("T | 0 | read book", task.toSaveFormat());
    }

    @Test
    public void toSaveFormat_markedDone_returnsOneDoneFlag() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("T | 1 | read book", task.toSaveFormat());
    }

    @Test
    public void toString_notDone_showsEmptyStatusIcon() {
        Task task = new Task("read book");

        assertEquals("[ ] read book", task.toString());
    }

    @Test
    public void toString_markedDone_showsXStatusIcon() {
        Task task = new Task("read book");
        task.markAsDone();

        assertEquals("[X] read book", task.toString());
    }
}
