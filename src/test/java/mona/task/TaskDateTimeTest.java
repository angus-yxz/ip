package mona.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TaskDateTime}.
 */
public class TaskDateTimeTest {
    @Test
    public void parse_dateAndTime_returnsExpectedLocalDate() {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15 1800");

        assertEquals(LocalDate.of(2019, 10, 15), dateTime.toLocalDate());
    }

    @Test
    public void toString_dateAndTime_returnsFormattedDateAndTime() {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15 1800");

        assertEquals("Oct 15 2019, 6:00 pm", dateTime.toString());
    }

    @Test
    public void parse_dateOnly_returnsExpectedLocalDate() {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15");

        assertEquals(LocalDate.of(2019, 10, 15), dateTime.toLocalDate());
    }

    @Test
    public void toString_dateOnly_returnsFormattedDateWithoutTime() {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15");

        assertEquals("Oct 15 2019", dateTime.toString());
    }

    @Test
    public void toString_midnight_containsTwelveAm() {
        TaskDateTime midnight = TaskDateTime.parse("2019-10-15 0000");

        assertTrue(midnight.toString().contains("12:00 am"));
    }

    @Test
    public void toString_noon_containsTwelvePm() {
        TaskDateTime noon = TaskDateTime.parse("2019-10-15 1200");

        assertTrue(noon.toString().contains("12:00 pm"));
    }

    @Test
    public void parse_garbageText_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> TaskDateTime.parse("not-a-date"));
    }

    @Test
    public void parse_emptyText_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> TaskDateTime.parse(""));
    }

    @Test
    public void parse_dateWithSlashes_throwsDateTimeParseException() {
        assertThrows(DateTimeParseException.class, () -> TaskDateTime.parse("2019/10/15"));
    }

    @Test
    public void toSaveFormat_dateOnly_returnsOriginalInputFormat() {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15");

        assertEquals("2019-10-15", dateTime.toSaveFormat());
    }

    @Test
    public void toSaveFormat_dateAndTime_returnsOriginalInputFormat() {
        TaskDateTime dateTime = TaskDateTime.parse("2019-10-15 1800");

        assertEquals("2019-10-15 1800", dateTime.toSaveFormat());
    }

    @Test
    public void parse_savedDateAndTime_reproducesSameLocalDate() {
        TaskDateTime original = TaskDateTime.parse("2019-10-15 1800");

        TaskDateTime reparsed = TaskDateTime.parse(original.toSaveFormat());

        assertEquals(original.toLocalDate(), reparsed.toLocalDate());
    }
}
