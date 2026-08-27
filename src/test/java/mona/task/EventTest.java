package mona.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Event}.
 */
public class EventTest {
    private static final TaskDateTime START = TaskDateTime.parse("2019-10-15 0900");
    private static final TaskDateTime END = TaskDateTime.parse("2019-10-17 1800");

    @Test
    public void occursOn_startDate_returnsTrue() {
        Event event = new Event("conference", START, END);

        assertTrue(event.occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void occursOn_endDate_returnsTrue() {
        Event event = new Event("conference", START, END);

        assertTrue(event.occursOn(LocalDate.of(2019, 10, 17)));
    }

    @Test
    public void occursOn_dateBetweenStartAndEnd_returnsTrue() {
        Event event = new Event("conference", START, END);

        assertTrue(event.occursOn(LocalDate.of(2019, 10, 16)));
    }

    @Test
    public void occursOn_dateBeforeStart_returnsFalse() {
        Event event = new Event("conference", START, END);

        assertFalse(event.occursOn(LocalDate.of(2019, 10, 14)));
    }

    @Test
    public void occursOn_dateAfterEnd_returnsFalse() {
        Event event = new Event("conference", START, END);

        assertFalse(event.occursOn(LocalDate.of(2019, 10, 18)));
    }

    @Test
    public void occursOn_singleDayEventDate_returnsTrue() {
        TaskDateTime date = TaskDateTime.parse("2019-10-15");
        Event event = new Event("workshop", date, date);

        assertTrue(event.occursOn(LocalDate.of(2019, 10, 15)));
    }

    @Test
    public void toSaveFormat_eventProvided_returnsStartAndEndSaveFormats() {
        Event event = new Event("conference", START, END);

        assertEquals("E | 0 | conference | 2019-10-15 0900 | 2019-10-17 1800",
                event.toSaveFormat());
    }

    @Test
    public void toString_eventProvided_showsTypeDescriptionStartAndEnd() {
        Event event = new Event("conference", START, END);

        assertEquals("[E][ ] conference (from: " + START + " to: " + END + ")", event.toString());
    }
}
