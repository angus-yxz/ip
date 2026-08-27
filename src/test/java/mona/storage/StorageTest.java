package mona.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import mona.MonaException;
import mona.task.Deadline;
import mona.task.Event;
import mona.task.Task;
import mona.task.TaskDateTime;
import mona.task.Todo;

/**
 * Tests {@link Storage}.
 */
public class StorageTest {
    @TempDir
    private Path tempDir;

    @Test
    public void load_fileDoesNotExist_returnsEmptyList() throws MonaException {
        Storage storage = new Storage(tempDir.resolve("mona.txt").toString());

        ArrayList<Task> loadedTasks = storage.load();

        assertTrue(loadedTasks.isEmpty());
    }

    @Test
    public void load_fileDoesNotExist_createsFile() throws MonaException {
        Path filePath = tempDir.resolve("nested").resolve("mona.txt");
        Storage storage = new Storage(filePath.toString());

        storage.load();

        assertTrue(Files.exists(filePath));
    }

    @Test
    public void saveAndLoad_multipleTaskTypes_roundTripsAllTasks() throws MonaException {
        Storage storage = new Storage(tempDir.resolve("mona.txt").toString());
        ArrayList<Task> originalTasks = new ArrayList<>();
        Todo todo = new Todo("read book");
        Deadline dateOnlyDeadline = new Deadline("return book", TaskDateTime.parse("2019-10-15"));
        Deadline timedDeadline = new Deadline("submit report", TaskDateTime.parse("2019-10-16 1800"));
        Event event = new Event("conference", TaskDateTime.parse("2019-10-17 0900"),
                TaskDateTime.parse("2019-10-18 1700"));
        timedDeadline.markAsDone();
        originalTasks.add(todo);
        originalTasks.add(dateOnlyDeadline);
        originalTasks.add(timedDeadline);
        originalTasks.add(event);

        storage.save(originalTasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(toSaveFormats(originalTasks), toSaveFormats(loadedTasks));
    }

    @Test
    public void save_existingContents_overwritesWithNewTasks() throws MonaException {
        Storage storage = new Storage(tempDir.resolve("mona.txt").toString());
        ArrayList<Task> firstTasks = new ArrayList<>();
        firstTasks.add(new Todo("first"));
        firstTasks.add(new Todo("second"));
        ArrayList<Task> replacementTasks = new ArrayList<>();
        replacementTasks.add(new Todo("replacement"));

        storage.save(firstTasks);
        storage.save(replacementTasks);
        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(List.of("T | 0 | replacement"), toSaveFormats(loadedTasks));
    }

    @Test
    public void load_fileContainsCorruptedLine_skipsCorruptedLine() throws Exception {
        Path filePath = tempDir.resolve("mona.txt");
        Files.write(filePath, List.of("T | 0 | read book", "T | 0"), StandardCharsets.UTF_8);
        Storage storage = new Storage(filePath.toString());

        ArrayList<Task> loadedTasks = storage.load();

        assertEquals(List.of("T | 0 | read book"), toSaveFormats(loadedTasks));
    }

    private static List<String> toSaveFormats(List<Task> tasks) {
        return tasks.stream().map(Task::toSaveFormat).toList();
    }
}
