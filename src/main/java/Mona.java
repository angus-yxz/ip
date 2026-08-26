import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Provides a command-line task manager named Mona.
 */
public class Mona {
    private static final String DEADLINE_SEPARATOR = " /by ";
    private static final String EVENT_START_SEPARATOR = " /from ";
    private static final String EVENT_END_SEPARATOR = " /to ";
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
        ArrayList<Task> tasks;
        try {
            tasks = storage.load();
        } catch (MonaException exception) {
            // Loading failed (e.g. the data file could not be read), so start with an
            // empty list rather than crashing; the user's tasks for this session are
            // still tracked in memory even though the earlier save could not be recovered.
            ui.showMessage(exception.getMessage());
            tasks = new ArrayList<>();
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

    private static void addTodo(ArrayList<Task> tasks, String userInput, Storage storage, Ui ui)
            throws MonaException {
        String description = Command.TODO.extractArguments(userInput);
        if (description.trim().isEmpty()) {
            throw MonaException.withHint(
                    "❌ A todo needs a name before its fate can be charted.",
                    "todo read book");
        }

        addTask(tasks, new Todo(description), storage, ui);
    }

    private static void addTask(ArrayList<Task> tasks, Task task, Storage storage, Ui ui)
            throws MonaException {
        tasks.add(task);
        // Save before reporting success, so a failed save is reported as an error
        // instead of falsely telling the user the task was added.
        storage.save(tasks);
        ui.showMessage("✅ Your fate is rewritten. I've added this task:\n  " + task
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }

    private static void addDeadline(ArrayList<Task> tasks, String userInput, Storage storage, Ui ui)
            throws MonaException {
        String arguments = Command.DEADLINE.extractArguments(userInput);
        int separatorIndex = arguments.indexOf(DEADLINE_SEPARATOR);
        if (separatorIndex < 0) {
            throw MonaException.withHint(
                    "❌ Even the stars need a fixed point. Specify the deadline using /by.",
                    "deadline return book /by 2019-10-15");
        }

        String description = arguments.substring(0, separatorIndex);
        String deadlineText = arguments.substring(separatorIndex + DEADLINE_SEPARATOR.length());
        if (description.trim().isEmpty()) {
            throw MonaException.withHint(
                    "❌ A deadline needs a name before its fate can be charted.",
                    "deadline return book /by 2019-10-15");
        }
        if (deadlineText.trim().isEmpty()) {
            throw MonaException.withHint(
                    "❌ A deadline needs a point in time. Tell me when it falls due after /by.",
                    "deadline return book /by 2019-10-15");
        }

        TaskDateTime deadline = parseDate(deadlineText.trim(), "deadline return book /by 2019-10-15");
        addTask(tasks, new Deadline(description, deadline), storage, ui);
    }

    private static void addEvent(ArrayList<Task> tasks, String userInput, Storage storage, Ui ui)
            throws MonaException {
        String arguments = Command.EVENT.extractArguments(userInput);
        int startSeparatorIndex = arguments.indexOf(EVENT_START_SEPARATOR);
        int endSeparatorIndex = arguments.indexOf(EVENT_END_SEPARATOR,
                startSeparatorIndex + EVENT_START_SEPARATOR.length());
        if (startSeparatorIndex < 0 || endSeparatorIndex < 0) {
            // /from and /to are both present, but /to comes before /from: point the user at the
            // ordering rather than reporting them as missing.
            if (startSeparatorIndex >= 0 && arguments.indexOf(EVENT_END_SEPARATOR) >= 0) {
                throw MonaException.withHint(
                        "❌ Fate flows only forward. Place /from before /to.",
                        "event project meeting /from 2019-10-15 /to 2019-10-16");
            }
            throw MonaException.withHint(
                    "❌ Fate needs both a dawn and a dusk. Specify the event using /from and /to.",
                    "event project meeting /from 2019-10-15 /to 2019-10-16");
        }

        String description = arguments.substring(0, startSeparatorIndex);
        String startText = arguments.substring(
                startSeparatorIndex + EVENT_START_SEPARATOR.length(), endSeparatorIndex);
        String endText = arguments.substring(endSeparatorIndex + EVENT_END_SEPARATOR.length());
        if (description.trim().isEmpty()) {
            throw MonaException.withHint(
                    "❌ An event needs a name before its fate can be charted.",
                    "event project meeting /from 2019-10-15 /to 2019-10-16");
        }
        if (startText.trim().isEmpty() || endText.trim().isEmpty()) {
            throw MonaException.withHint(
                    "❌ An event needs both a dawn and a dusk. Fill in /from and /to.",
                    "event project meeting /from 2019-10-15 /to 2019-10-16");
        }

        String hint = "event project meeting /from 2019-10-15 /to 2019-10-16";
        TaskDateTime start = parseDate(startText.trim(), hint);
        TaskDateTime end = parseDate(endText.trim(), hint);
        addTask(tasks, new Event(description, start, end), storage, ui);
    }

    /**
     * Parses user-entered text as a date, optionally with a time of day.
     *
     * @param dateText the text to parse, either {@code yyyy-mm-dd HHmm} or {@code yyyy-mm-dd}.
     * @param hint an example command demonstrating the correct usage, shown if parsing fails.
     * @return the parsed date.
     * @throws MonaException if the text matches neither accepted format.
     */
    private static TaskDateTime parseDate(String dateText, String hint) throws MonaException {
        try {
            return TaskDateTime.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw MonaException.withHint(
                    "❌ The stars only read dates as yyyy-mm-dd, optionally followed by a "
                            + "24-hour time, such as 2019-10-15 or 2019-10-15 1800.",
                    hint);
        }
    }

    private static void printTasks(ArrayList<Task> tasks, Ui ui) {
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
    private static void printTasksOnDate(ArrayList<Task> tasks, String userInput, Ui ui)
            throws MonaException {
        String argument = Command.ON.extractArguments(userInput).trim();
        if (argument.isEmpty()) {
            throw MonaException.withHint(
                    "❌ Tell me which date's fate to reveal.",
                    "on 2019-10-15");
        }

        TaskDateTime date = parseDate(argument, "on 2019-10-15");
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
    private static void printTasksInDays(ArrayList<Task> tasks, String userInput, Ui ui)
            throws MonaException {
        String argument = Command.IN.extractArguments(userInput).trim();
        if (argument.isEmpty()) {
            throw MonaException.withHint(
                    "❌ Tell me how many days ahead the stars should look.",
                    "in 3");
        }

        int daysAhead;
        try {
            daysAhead = Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw MonaException.withHint(
                    "❌ The stars only count whole days. Please enter a valid number.",
                    "in 3");
        }
        if (daysAhead < 0) {
            throw MonaException.withHint(
                    "❌ The stars cannot yet see a negative number of days.",
                    "in 3");
        }

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
    private static void printMatchingTasks(ArrayList<Task> tasks, LocalDate date, String header, Ui ui) {
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

    private static void markTask(ArrayList<Task> tasks, String userInput, boolean shouldMarkAsDone,
            Storage storage, Ui ui) throws MonaException {
        Command command = shouldMarkAsDone ? Command.MARK : Command.UNMARK;
        String argument = command.extractArguments(userInput).trim();

        if (argument.isEmpty()) {
            throw MonaException.withHint(
                    "❌ Tell me which task's fate to alter.",
                    command + " 2");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw MonaException.withHint(
                    "❌ No such fate is written in the constellations. Please enter a valid task number.",
                    command + " 2");
        }

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
            storage.save(tasks);
            ui.showMessage("✅ The stars align. I've marked this task as done:\n  " + task);
        } else {
            task.markAsNotDone();
            storage.save(tasks);
            ui.showMessage("❌ The constellation fades. I've marked this task as not done yet:\n  " + task);
        }
    }

    private static void deleteTask(ArrayList<Task> tasks, String userInput, Storage storage, Ui ui)
            throws MonaException {
        String argument = Command.DELETE.extractArguments(userInput).trim();

        if (argument.isEmpty()) {
            throw MonaException.withHint(
                    "❌ Tell me which task should be deleted.",
                    "delete 2");
        }

        int taskNumber;
        try {
            taskNumber = Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw MonaException.withHint(
                    "❌ No such fate is written in the constellations. Please enter a valid task number.",
                    "delete 2");
        }

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

        Task deletedTask = tasks.remove(taskNumber - 1);
        storage.save(tasks);
        ui.showMessage("✅ A fate fades from the constellations. I've removed this task:\n  " + deletedTask
                + "\nNow you have " + tasks.size() + " tasks in the list.");
    }
}
