package mona;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Tests Mona's GUI-facing command response API.
 */
public class MonaTest {
    @TempDir
    private Path temporaryDirectory;

    @Test
    public void getResponse_validCommand_executesCommandAndReturnsOutput() {
        Mona mona = new Mona(temporaryDirectory.resolve("mona.txt").toString());

        String response = mona.getResponse("todo read book");

        assertTrue(response.contains("I've added this task:"));
        assertTrue(response.contains("[T][ ] read book"));
        assertTrue(response.contains("Now you have 1 tasks in the list."));
    }

    @Test
    public void getResponse_invalidCommand_returnsValidationError() {
        Mona mona = new Mona(temporaryDirectory.resolve("mona.txt").toString());

        String response = mona.getResponse("unknown command");

        assertTrue(response.startsWith("❌ That command is not written in the stars"));
    }

    @Test
    public void getResponse_multipleCommands_shareTaskList() {
        Mona mona = new Mona(temporaryDirectory.resolve("mona.txt").toString());
        mona.getResponse("todo read book");

        String response = mona.getResponse("list");

        assertEquals("✨ Here is what the stars reveal:"
                + System.lineSeparator() + "1.[T][ ] read book", response);
    }
}
