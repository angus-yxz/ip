package mona.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link Todo}. Written by hand as the first JUnit test in this project, to confirm
 * the Gradle + JUnit 5 setup works end to end before delegating the rest of the suite.
 */
public class TodoTest {
    @Test
    public void toString_notDone_showsTypeAndEmptyStatusIcon() {
        Todo todo = new Todo("read book");

        assertEquals("[T][ ] read book", todo.toString());
    }

    @Test
    public void toString_markedDone_showsTypeAndXStatusIcon() {
        Todo todo = new Todo("read book");
        todo.markAsDone();

        assertEquals("[T][X] read book", todo.toString());
    }
}
