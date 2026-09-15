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

        MonaResponse response = mona.getResponse("todo read book");

        assertEquals(ResponseType.SUCCESS, response.type());
        assertTrue(response.text().contains("I've added this task:"));
        assertTrue(response.text().contains("[T][ ] read book"));
        assertTrue(response.text().contains("Now you have 1 tasks in the list."));
    }

    @Test
    public void getResponse_invalidCommand_returnsValidationError() {
        Mona mona = new Mona(temporaryDirectory.resolve("mona.txt").toString());

        MonaResponse response = mona.getResponse("unknown command");

        assertEquals(ResponseType.ERROR, response.type());
        assertTrue(response.text().startsWith("❌ That command is not written in the stars"));
    }

    @Test
    public void getResponse_multipleCommands_shareTaskList() {
        Mona mona = new Mona(temporaryDirectory.resolve("mona.txt").toString());
        mona.getResponse("todo read book");

        MonaResponse response = mona.getResponse("list");

        assertEquals(ResponseType.INFO, response.type());
        assertEquals("✨ Here is what the stars reveal:"
                + System.lineSeparator() + "1.[T][ ] read book", response.text());
    }

    @Test
    public void getResponse_sortThenList_listsTasksChronologically() {
        Mona mona = new Mona(temporaryDirectory.resolve("mona.txt").toString());
        mona.getResponse("todo read book");
        mona.getResponse("deadline submit report /by 2019-10-16 1800");
        mona.getResponse("deadline return book /by 2019-10-15");

        MonaResponse sortResponse = mona.getResponse("sort");
        MonaResponse listResponse = mona.getResponse("list");

        assertEquals(ResponseType.SUCCESS, sortResponse.type());
        assertEquals("✅ The constellations align. Your tasks are now in chronological order.",
                sortResponse.text());
        assertEquals(ResponseType.INFO, listResponse.type());
        assertEquals("✨ Here is what the stars reveal:"
                + System.lineSeparator() + "1.[D][ ] return book (by: Oct 15 2019)"
                + System.lineSeparator() + "2.[D][ ] submit report (by: Oct 16 2019, 6:00 pm)"
                + System.lineSeparator() + "3.[T][ ] read book", listResponse.text());
    }

    @Test
    public void getResponse_markAndUnmarkCommands_updateTaskStatus() {
        Mona mona = new Mona(temporaryDirectory.resolve("mona.txt").toString());
        mona.getResponse("todo read book");

        MonaResponse markedResponse = mona.getResponse("mark 1");
        MonaResponse unmarkedResponse = mona.getResponse("unmark 1");

        assertEquals(ResponseType.SUCCESS, markedResponse.type());
        assertTrue(markedResponse.text().contains("[T][X] read book"));
        assertEquals(ResponseType.SUCCESS, unmarkedResponse.type());
        assertTrue(unmarkedResponse.text().startsWith("✅"));
        assertTrue(unmarkedResponse.text().contains("[T][ ] read book"));
    }

    @Test
    public void getResponse_deleteCommand_removesSelectedTask() {
        Mona mona = new Mona(temporaryDirectory.resolve("mona.txt").toString());
        mona.getResponse("todo read book");
        mona.getResponse("todo watch movie");

        MonaResponse deleteResponse = mona.getResponse("delete 1");
        MonaResponse listResponse = mona.getResponse("list");

        assertEquals(ResponseType.SUCCESS, deleteResponse.type());
        assertTrue(deleteResponse.text().contains("[T][ ] read book"));
        assertEquals("✨ Here is what the stars reveal:"
                + System.lineSeparator() + "1.[T][ ] watch movie", listResponse.text());
    }

    @Test
    public void getResponse_taskNumberOutOfRange_returnsValidationError() {
        Mona mona = new Mona(temporaryDirectory.resolve("mona.txt").toString());
        mona.getResponse("todo read book");

        MonaResponse response = mona.getResponse("mark 2");

        assertEquals(ResponseType.ERROR, response.type());
        assertEquals("❌ No such fate is written in the constellations. Please enter a valid task number."
                + "\nHint: list", response.text());
    }

    @Test
    public void getResponse_deleteWithEmptyList_returnsDeleteSpecificError() {
        Mona mona = new Mona(temporaryDirectory.resolve("mona.txt").toString());

        MonaResponse response = mona.getResponse("delete 1");

        assertEquals(ResponseType.ERROR, response.type());
        assertEquals("❌ The constellations remain still. There are no tasks to be deleted."
                + "\nHint: todo read book", response.text());
    }
}
