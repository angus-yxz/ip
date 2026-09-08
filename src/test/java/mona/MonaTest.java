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

    @Test
    public void getResponse_sortThenList_listsTasksChronologically() {
        Mona mona = new Mona(temporaryDirectory.resolve("mona.txt").toString());
        mona.getResponse("todo read book");
        mona.getResponse("deadline submit report /by 2019-10-16 1800");
        mona.getResponse("deadline return book /by 2019-10-15");

        String sortResponse = mona.getResponse("sort");
        String listResponse = mona.getResponse("list");

        assertEquals("✅ The constellations align. Your tasks are now in chronological order.",
                sortResponse);
        assertEquals("✨ Here is what the stars reveal:"
                + System.lineSeparator() + "1.[D][ ] return book (by: Oct 15 2019)"
                + System.lineSeparator() + "2.[D][ ] submit report (by: Oct 16 2019, 6:00 pm)"
                + System.lineSeparator() + "3.[T][ ] read book", listResponse);
    }

    @Test
    public void getResponse_markAndUnmarkCommands_updateTaskStatus() {
        Mona mona = new Mona(temporaryDirectory.resolve("mona.txt").toString());
        mona.getResponse("todo read book");

        String markedResponse = mona.getResponse("mark 1");
        String unmarkedResponse = mona.getResponse("unmark 1");

        assertTrue(markedResponse.contains("[T][X] read book"));
        assertTrue(unmarkedResponse.contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_deleteCommand_removesSelectedTask() {
        Mona mona = new Mona(temporaryDirectory.resolve("mona.txt").toString());
        mona.getResponse("todo read book");
        mona.getResponse("todo watch movie");

        String deleteResponse = mona.getResponse("delete 1");
        String listResponse = mona.getResponse("list");

        assertTrue(deleteResponse.contains("[T][ ] read book"));
        assertEquals("✨ Here is what the stars reveal:"
                + System.lineSeparator() + "1.[T][ ] watch movie", listResponse);
    }

    @Test
    public void getResponse_taskNumberOutOfRange_returnsValidationError() {
        Mona mona = new Mona(temporaryDirectory.resolve("mona.txt").toString());
        mona.getResponse("todo read book");

        String response = mona.getResponse("mark 2");

        assertEquals("❌ No such fate is written in the constellations. Please enter a valid task number."
                + "\nHint: list", response);
    }

    @Test
    public void getResponse_deleteWithEmptyList_returnsDeleteSpecificError() {
        Mona mona = new Mona(temporaryDirectory.resolve("mona.txt").toString());

        String response = mona.getResponse("delete 1");

        assertEquals("❌ The constellations remain still. There are no tasks to be deleted."
                + "\nHint: todo read book", response);
    }
}
