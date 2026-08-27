package mona.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Deadline}.
 */
public class DeadlineTest {
    @Test
    public void occursOn_dateOnlyDeadlineSameDate_returnsTrue() {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-10-15"));

        assertTrue(deadline.occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void occursOn_timedDeadlineSameDate_returnsTrue() {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-10-15 1800"));

        assertTrue(deadline.occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void occursOn_dayBeforeDeadline_returnsFalse() {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-10-15"));

        assertFalse(deadline.occursOn(LocalDate.of(2019, 10, 14)));
    }

    @Test
    public void occursOn_dayAfterDeadline_returnsFalse() {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-10-15"));

        assertFalse(deadline.occursOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    public void toSaveFormat_dateOnlyDeadline_returnsDateOnlyFormat() {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-10-15"));

        assertEquals("D | 0 | return book | 2019-10-15", deadline.toSaveFormat());
    }

    @Test
    public void toSaveFormat_timedDeadline_returnsDateAndTimeFormat() {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-10-15 1800"));

        assertEquals("D | 0 | return book | 2019-10-15 1800", deadline.toSaveFormat());
    }

    @Test
    public void toSaveFormat_markedDone_returnsOneDoneFlag() {
        Deadline deadline = new Deadline("return book", TaskDateTime.parse("2019-10-15"));
        deadline.markAsDone();

        assertEquals("D | 1 | return book | 2019-10-15", deadline.toSaveFormat());
    }

    @Test
    public void toString_deadlineProvided_showsTypeDescriptionAndDeadline() {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15 1800");
        Deadline deadline = new Deadline("return book", dateTime);

        assertEquals("[D][ ] return book (by: " + dateTime + ")", deadline.toString());
    }
}
