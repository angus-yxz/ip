package mona;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link MonaException}.
 */
public class MonaExceptionTest {
    @Test
    public void constructor_messageProvided_returnsSameMessage() {
        MonaException exception = new MonaException("Something went wrong");

        assertEquals("Something went wrong", exception.getMessage());
    }

    @Test
    public void withHint_messageAndHintProvided_combinesMessageAndHint() {
        MonaException exception = MonaException.withHint("Missing description", "todo read book");

        assertEquals("Missing description\nHint: todo read book", exception.getMessage());
    }
}
