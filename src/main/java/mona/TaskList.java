package mona;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * Manages Mona's collection of tasks.
 */
public class TaskList implements Iterable<Task> {
    private final ArrayList<Task> tasks;

    /**
     * Creates an empty task list.
     */
    public TaskList() {
        tasks = new ArrayList<>();
    }

    /**
     * Creates a task list containing the given tasks in their existing order.
     *
     * @param tasks the tasks with which to initialize the list.
     */
    public TaskList(ArrayList<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add.
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes and returns the task at the given zero-based index.
     *
     * @param index the zero-based index of the task to delete.
     * @return the deleted task.
     * @throws IndexOutOfBoundsException if the index is outside the task list.
     */
    public Task delete(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given zero-based index.
     *
     * @param index the zero-based index of the task to return.
     * @return the task at the given index.
     * @throws IndexOutOfBoundsException if the index is outside the task list.
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns whether the task list is empty.
     *
     * @return {@code true} if the list contains no tasks, or {@code false} otherwise.
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }

    /**
     * Returns the tasks as an independent list for storage.
     *
     * @return a copy of the tasks in their current order.
     */
    public ArrayList<Task> asList() {
        return new ArrayList<>(tasks);
    }

    /**
     * Returns an iterator over the tasks in their current order.
     *
     * @return an iterator that does not support removing tasks.
     */
    @Override
    public Iterator<Task> iterator() {
        return Collections.unmodifiableList(tasks).iterator();
    }
}
