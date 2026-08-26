import java.time.LocalDate;
import java.util.Optional;

/**
 * Provides a command-line task manager named Mona.
 */
public class Mona {
    // Relative to the working directory the program is run from, per the project's
    // requirement to avoid absolute, OS-specific paths.
    private static final String DATA_FILE_PATH = "./data/mona.txt";

    /**
     * Starts Mona and processes task commands until the user exits.
     *
     * @param args command-line arguments, which are not used.
     */
    public static void main(String[] args) {
        Ui ui = new Ui();
        Storage storage = new Storage(DATA_FILE_PATH);
        TaskList tasks;
        try {
            tasks = new TaskList(storage.load());
        } catch (MonaException exception) {
            // Loading failed (e.g. the data file could not be read), so start with an
            // empty list rather than crashing; the user's tasks for this session are
            // still tracked in memory even though the earlier save could not be recovered.
            ui.showMessage(exception.getMessage());
            tasks = new TaskList();
        }

        ui.showWelcome();
        while (true) {
            String userInput = ui.readCommand();

            try {
                if (userInput.isEmpty()) {
                    throw MonaException.withHint(
                            "❌ Silence carries no fate. Please enter a command.",
                            "list");
                }

                Optional<Command> parsedCommand = Command.from(userInput);
                if (parsedCommand.isEmpty()) {
                    throw MonaException.withHint(
                            "❌ That command is not written in the stars I can read. "
                                    + "Try a todo, deadline, or event.",
                            "list | todo <description> | deadline <description> /by <yyyy-mm-dd> | "
                                    + "event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd> | "
                                    + "on <yyyy-mm-dd> | in <days> | mark <number> | "
                                    + "unmark <number> | delete <number> | bye");
                }

                Command command = parsedCommand.get();
                switch (command) {
                    case BYE:
                        ui.showMessage("✨ Farewell. May the stars guide you until we meet again.");
                        return;
                    case LIST:
                        printTasks(tasks, ui);
                        break;
                    case MARK:
                        markTask(tasks, userInput, true, storage, ui);
                        break;
                    case UNMARK:
                        markTask(tasks, userInput, false, storage, ui);
                        break;
                    case DELETE:
                        deleteTask(tasks, userInput, storage, ui);
                        break;
                    case TODO:
                        addTodo(tasks, userInput, storage, ui);
                        break;
                    case DEADLINE:
                        addDeadline(tasks, userInput, storage, ui);
                        break;
                    case EVENT:
                        addEvent(tasks, userInput, storage, ui);
                        break;
                    case ON:
                        printTasksOnDate(tasks, userInput, ui);
                        break;
                    case IN:
                        printTasksInDays(tasks, userInput, ui);
                        break;
                    default:
                        throw new AssertionError("Unhandled command: " + command);
                }
            } catch (MonaException exception) {
                ui.showMessage(exception.getMessage());
            }
        }
    }

    private static void addTodo(TaskList tasks, String userInput, Storage storage, Ui ui)
            throws MonaException {
        String description = Parser.parseTodoDescription(userInput);
        addTask(tasks, new Todo(description), storage, ui);
    }

    private static void addTask(TaskList tasks, Task task, Storage storage, Ui ui)
            throws MonaException {
        tasks.add(task);
        // Save before reporting success, so a failed save is reported as an error
        // instead of falsely telling the user the task was added.
        storage.save(tasks.asList());
        ui.showMessage("✅ Your fate is rewritten. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }

    private static void addDeadline(TaskList tasks, String userInput, Storage storage, Ui ui)
            throws MonaException {
        Parser.DeadlineArguments arguments = Parser.parseDeadline(userInput);
        addTask(tasks, new Deadline(arguments.description(), arguments.deadline()), storage, ui);
    }

    private static void addEvent(TaskList tasks, String userInput, Storage storage, Ui ui)
            throws MonaException {
        Parser.EventArguments arguments = Parser.parseEvent(userInput);
        addTask(tasks, new Event(arguments.description(), arguments.start(), arguments.end()),
                storage, ui);
    }

    private static void printTasks(TaskList tasks, Ui ui) {
        StringBuilder taskList = new StringBuilder("✨ Here is what the stars reveal:");
        for (int index = 0; index < tasks.size(); index++) {
            taskList.append(System.lineSeparator())
                    .append(index + 1)
                    .append(".")
                    .append(tasks.get(index));
        }
        ui.showMessage(taskList.toString());
    }

    /**
     * Prints the deadlines and events occurring on a user-specified date.
     *
     * @param tasks the current task list.
     * @param userInput the trimmed line entered by the user, e.g. {@code on 2019-10-15}.
     * @throws MonaException if no date is given, or the date cannot be parsed.
     */
    private static void printTasksOnDate(TaskList tasks, String userInput, Ui ui)
            throws MonaException {
        TaskDateTime date = Parser.parseOnDate(userInput);
        LocalDate localDate = date.toLocalDate();
        printMatchingTasks(tasks, localDate, "✨ On " + date + ", the stars reveal:", ui);
    }

    /**
     * Prints the deadlines and events occurring a given number of days from today.
     *
     * @param tasks the current task list.
     * @param userInput the trimmed line entered by the user, e.g. {@code in 3}.
     * @throws MonaException if no number of days is given, or it is not a non-negative integer.
     */
    private static void printTasksInDays(TaskList tasks, String userInput, Ui ui)
            throws MonaException {
        int daysAhead = Parser.parseDaysAhead(userInput);
        LocalDate targetDate = LocalDate.now().plusDays(daysAhead);
        printMatchingTasks(tasks, targetDate,
                "✨ In " + daysAhead + " day(s), the stars reveal:", ui);
    }

    /**
     * Prints the tasks (usually deadlines and events) that occur on the given date, under the
     * given header. Tasks with no associated date, such as todos, never match.
     *
     * @param tasks the current task list.
     * @param date the date to match tasks against.
     * @param header the line to print before the matching tasks.
     */
    private static void printMatchingTasks(TaskList tasks, LocalDate date, String header, Ui ui) {
        StringBuilder taskList = new StringBuilder(header);
        int matchCount = 0;
        for (Task task : tasks) {
            if (task.occursOn(date)) {
                matchCount++;
                taskList.append(System.lineSeparator())
                        .append(matchCount)
                        .append(".")
                        .append(task);
            }
        }
        if (matchCount == 0) {
            taskList.append(System.lineSeparator()).append("  (No fates found on this date.)");
        }
        ui.showMessage(taskList.toString());
    }

    private static void markTask(TaskList tasks, String userInput, boolean shouldMarkAsDone,
            Storage storage, Ui ui) throws MonaException {
        Command command = shouldMarkAsDone ? Command.MARK : Command.UNMARK;
        int taskNumber = Parser.parseTaskNumber(userInput, command);

        if (tasks.isEmpty()) {
            throw MonaException.withHint(
                    "❌ There are no tasks yet whose fate can be altered.",
                    "todo read book");
        }

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw MonaException.withHint(
                    "❌ No such fate is written in the constellations. Please enter a valid task number.",
                    "list");
        }

        Task task = tasks.get(taskNumber - 1);

        if (shouldMarkAsDone) {
            task.markAsDone();
            storage.save(tasks.asList());
            ui.showMessage("✅ The stars align. I've marked this task as done:\n  " + task);
        } else {
            task.markAsNotDone();
            storage.save(tasks.asList());
            ui.showMessage("❌ The constellation fades. I've marked this task as not done yet:\n  " + task);
        }
    }

    private static void deleteTask(TaskList tasks, String userInput, Storage storage, Ui ui)
            throws MonaException {
        int taskNumber = Parser.parseTaskNumber(userInput, Command.DELETE);

        if (tasks.isEmpty()) {
            throw MonaException.withHint(
                    "❌ The constellations remain still. There are no tasks to be deleted.",
                    "todo read book");
        }

        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw MonaException.withHint(
                    "❌ No such fate is written in the constellations. Please enter a valid task number.",
                    "list");
        }

        Task deletedTask = tasks.delete(taskNumber - 1);
        storage.save(tasks.asList());
        ui.showMessage("✅ A fate fades from the constellations. I've removed this task:\n  " + deletedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }
}
