package mona.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link TaskList}.
 */
public class TaskListTest {
    @Test
    public void constructor_noTasks_isEmpty() {
        TaskList tasks = new TaskList();

        assertTrue(tasks.isEmpty());
    }

    @Test
    public void size_noTasks_returnsZero() {
        TaskList tasks = new TaskList();

        assertEquals(0, tasks.size());
    }

    @Test
    public void add_oneTask_increasesSizeByOne() {
        TaskList tasks = new TaskList();

        tasks.add(new Todo("first"));

        assertEquals(1, tasks.size());
    }

    @Test
    public void get_addedTask_returnsSameInstance() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("first");
        tasks.add(todo);

        assertSame(todo, tasks.get(0));
    }

    @Test
    public void get_negativeIndex_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(-1));
    }

    @Test
    public void get_indexEqualToSize_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.get(1));
    }

    @Test
    public void delete_validIndex_returnsRemovedTask() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        tasks.add(first);
        tasks.add(second);

        Task removed = tasks.delete(0);

        assertSame(first, removed);
    }

    @Test
    public void delete_validIndex_decreasesSizeByOne() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        tasks.add(new Todo("second"));

        tasks.delete(0);

        assertEquals(1, tasks.size());
    }

    @Test
    public void delete_middleIndex_shiftsRemainingTasksDown() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        Todo third = new Todo("third");
        tasks.add(first);
        tasks.add(second);
        tasks.add(third);

        tasks.delete(1);

        assertEquals(List.of(first, third), tasks.asList());
    }

    @Test
    public void delete_negativeIndex_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(-1));
    }

    @Test
    public void delete_indexEqualToSize_throwsIndexOutOfBoundsException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));

        assertThrows(IndexOutOfBoundsException.class, () -> tasks.delete(1));
    }

    @Test
    public void asList_multipleTasks_returnsTasksInOrder() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        tasks.add(first);
        tasks.add(second);

        assertEquals(List.of(first, second), tasks.asList());
    }

    @Test
    public void asList_returnedListCleared_originalListIsUnchanged() {
        TaskList tasks = new TaskList();
        Todo todo = new Todo("first");
        tasks.add(todo);
        ArrayList<Task> copiedTasks = tasks.asList();

        copiedTasks.clear();

        assertEquals(List.of(todo), tasks.asList());
    }

    @Test
    public void constructor_inputListCleared_newTaskListIsUnchanged() {
        Todo todo = new Todo("first");
        ArrayList<Task> inputTasks = new ArrayList<>();
        inputTasks.add(todo);
        TaskList tasks = new TaskList(inputTasks);

        inputTasks.clear();

        assertEquals(List.of(todo), tasks.asList());
    }

    @Test
    public void iterator_multipleTasks_yieldsTasksInInsertionOrder() {
        TaskList tasks = new TaskList();
        Todo first = new Todo("first");
        Todo second = new Todo("second");
        tasks.add(first);
        tasks.add(second);
        ArrayList<Task> iteratedTasks = new ArrayList<>();

        for (Task task : tasks) {
            iteratedTasks.add(task);
        }

        assertEquals(List.of(first, second), iteratedTasks);
    }

    @Test
    public void iterator_removeCalled_throwsUnsupportedOperationException() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));
        Iterator<Task> iterator = tasks.iterator();
        iterator.next();

        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    public void find_keywordWithDifferentCase_returnsDescriptionMatchesInOrder() {
        TaskList tasks = new TaskList();
        Todo firstMatch = new Todo("Read Book");
        Deadline nonMatch = new Deadline("return notes", TaskDateTime.parse("2019-10-15"));
        Deadline secondMatch = new Deadline("return book", TaskDateTime.parse("2019-10-16"));
        tasks.add(firstMatch);
        tasks.add(nonMatch);
        tasks.add(secondMatch);

        ArrayList<Task> matchingTasks = tasks.find("BOOK");

        assertEquals(List.of(firstMatch, secondMatch), matchingTasks);
    }

    @Test
    public void find_textOnlyInFormattedDetails_returnsNoMatches() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("return notes", TaskDateTime.parse("2019-10-15")));

        ArrayList<Task> matchingTasks = tasks.find("2019");

        assertTrue(matchingTasks.isEmpty());
    }

    @Test
    public void find_keywordWithoutMatches_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        ArrayList<Task> matchingTasks = tasks.find("movie");

        assertTrue(matchingTasks.isEmpty());
    }
}
