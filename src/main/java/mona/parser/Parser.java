package mona.parser;

import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mona.MonaException;
import mona.command.Command;
import mona.command.CommandWord;
import mona.command.DeadlineCommand;
import mona.command.DeleteCommand;
import mona.command.EventCommand;
import mona.command.ExitCommand;
import mona.command.FindCommand;
import mona.command.InCommand;
import mona.command.ListCommand;
import mona.command.MarkCommand;
import mona.command.OnCommand;
import mona.command.SortCommand;
import mona.command.TodoCommand;
import mona.command.UnmarkCommand;
import mona.task.TaskDateTime;

/**
 * Parses and validates arguments from commands entered by the user.
 */
public final class Parser {
    private static final Pattern DEADLINE_SEPARATOR_PATTERN =
            Pattern.compile("(?<!\\S)/by(?!\\S)");
    private static final Pattern EVENT_START_SEPARATOR_PATTERN =
            Pattern.compile("(?<!\\S)/from(?!\\S)");
    private static final Pattern EVENT_END_SEPARATOR_PATTERN =
            Pattern.compile("(?<!\\S)/to(?!\\S)");

    private Parser() {
    }

    /**
     * Returns an executable command parsed from the given user input.
     *
     * @param userInput the trimmed line entered by the user.
     * @return the parsed executable command.
     * @throws MonaException if the command word or any of its arguments is invalid.
     */
    public static Command parse(String userInput) throws MonaException {
        if (userInput.trim().isEmpty()) {
            throw MonaException.withHint(
                    "❌ Silence carries no fate. Please enter a command.",
                    "list");
        }

        String trimmedInput = userInput.trim();
        Optional<CommandWord> parsedCommandWord = CommandWord.from(trimmedInput);
        if (parsedCommandWord.isEmpty()) {
            throw MonaException.withHint(
                    "❌ That command is not written in the stars I can read. "
                            + "Try a todo, deadline, or event.",
                    "list | todo <description> | deadline <description> /by <yyyy-mm-dd> | "
                            + "event <description> /from <yyyy-mm-dd> /to <yyyy-mm-dd> | "
                            + "find <keyword> | sort | on <yyyy-mm-dd> | in <days> | mark <number> | "
                            + "unmark <number> | delete <number> | bye");
        }

        CommandWord commandWord = parsedCommandWord.get();
        return switch (commandWord) {
            case BYE -> new ExitCommand();
            case LIST -> new ListCommand();
            case SORT -> new SortCommand();
            case FIND -> new FindCommand(parseFindKeyword(trimmedInput));
            case MARK -> new MarkCommand(parseTaskNumber(trimmedInput, CommandWord.MARK));
            case UNMARK -> new UnmarkCommand(parseTaskNumber(trimmedInput, CommandWord.UNMARK));
            case DELETE -> new DeleteCommand(parseTaskNumber(trimmedInput, CommandWord.DELETE));
            case TODO -> new TodoCommand(parseTodoDescription(trimmedInput));
            case DEADLINE -> {
                DeadlineArguments arguments = parseDeadline(trimmedInput);
                yield new DeadlineCommand(arguments.description(), arguments.deadline());
            }
            case EVENT -> {
                EventArguments arguments = parseEvent(trimmedInput);
                yield new EventCommand(arguments.description(), arguments.start(), arguments.end());
            }
            case ON -> new OnCommand(parseOnDate(trimmedInput));
            case IN -> new InCommand(parseDaysAhead(trimmedInput));
        };
    }

    /**
     * Returns the validated description from a todo command.
     *
     * @param userInput the trimmed line entered by the user.
     * @return the todo description.
     * @throws MonaException if no description is given.
     */
    public static String parseTodoDescription(String userInput) throws MonaException {
        String description = CommandWord.TODO.extractArguments(userInput);
        return validateDescription(description,
                "❌ A todo needs a name before its fate can be charted.",
                "todo read book");
    }

    /**
     * Returns the validated keyword from a find command.
     *
     * @param userInput the trimmed line entered by the user.
     * @return the keyword to find in task descriptions.
     * @throws MonaException if no keyword is given.
     */
    public static String parseFindKeyword(String userInput) throws MonaException {
        String keyword = CommandWord.FIND.extractArguments(userInput).trim();
        if (keyword.isEmpty()) {
            throw MonaException.withHint(
                    "❌ The stars need a keyword to find matching tasks.",
                    "find book");
        }

        return keyword;
    }

    /**
     * Returns the validated description and date from a deadline command.
     *
     * @param userInput the trimmed line entered by the user.
     * @return the parsed deadline arguments.
     * @throws MonaException if the description, separator, or date is invalid.
     */
    public static DeadlineArguments parseDeadline(String userInput) throws MonaException {
        String arguments = CommandWord.DEADLINE.extractArguments(userInput);
        Matcher separatorMatcher = DEADLINE_SEPARATOR_PATTERN.matcher(arguments);
        if (!separatorMatcher.find()) {
            throw MonaException.withHint(
                    "❌ Even the stars need a fixed point. Specify the deadline using /by.",
                    "deadline return book /by 2019-10-15");
        }
        int separatorStart = separatorMatcher.start();
        int separatorEnd = separatorMatcher.end();
        if (separatorMatcher.find()) {
            throw MonaException.withHint(
                    "❌ A deadline can have only one /by parameter.",
                    "deadline return book /by 2019-10-15");
        }

        String hint = "deadline return book /by 2019-10-15";
        String description = validateDescription(arguments.substring(0, separatorStart),
                "❌ A deadline needs a name before its fate can be charted.", hint);
        String deadlineText = arguments.substring(separatorEnd).trim();
        if (deadlineText.isEmpty()) {
            throw MonaException.withHint(
                    "❌ A deadline needs a point in time. Tell me when it falls due after /by.",
                    hint);
        }

        TaskDateTime deadline = parseDate(deadlineText, hint);
        return new DeadlineArguments(description, deadline);
    }

    /**
     * Returns the validated description, start, and end from an event command.
     *
     * @param userInput the trimmed line entered by the user.
     * @return the parsed event arguments.
     * @throws MonaException if the description, separators, or dates are invalid.
     */
    public static EventArguments parseEvent(String userInput) throws MonaException {
        String arguments = CommandWord.EVENT.extractArguments(userInput);
        Matcher startSeparatorMatcher = EVENT_START_SEPARATOR_PATTERN.matcher(arguments);
        Matcher endSeparatorMatcher = EVENT_END_SEPARATOR_PATTERN.matcher(arguments);
        boolean hasStartSeparator = startSeparatorMatcher.find();
        boolean hasEndSeparator = endSeparatorMatcher.find();
        String hint = "event project meeting /from 2019-10-15 /to 2019-10-16";
        if (!hasStartSeparator || !hasEndSeparator) {
            throw MonaException.withHint(
                    "❌ Fate needs both a dawn and a dusk. Specify the event using /from and /to.",
                    hint);
        }

        int startSeparatorStart = startSeparatorMatcher.start();
        int startSeparatorEnd = startSeparatorMatcher.end();
        int endSeparatorStart = endSeparatorMatcher.start();
        int endSeparatorEnd = endSeparatorMatcher.end();
        if (startSeparatorMatcher.find() || endSeparatorMatcher.find()) {
            throw MonaException.withHint(
                    "❌ An event can have only one /from and one /to parameter.",
                    hint);
        }
        if (startSeparatorStart > endSeparatorStart) {
            throw MonaException.withHint(
                    "❌ Fate flows only forward. Place /from before /to.",
                    hint);
        }

        String description = validateDescription(arguments.substring(0, startSeparatorStart),
                "❌ An event needs a name before its fate can be charted.", hint);
        String startText = arguments.substring(startSeparatorEnd, endSeparatorStart).trim();
        String endText = arguments.substring(endSeparatorEnd).trim();
        if (startText.isEmpty() || endText.isEmpty()) {
            throw MonaException.withHint(
                    "❌ An event needs both a dawn and a dusk. Fill in /from and /to.",
                    hint);
        }

        TaskDateTime start = parseDate(startText, hint);
        TaskDateTime end = parseDate(endText, hint);
        if (start.compareTo(end) >= 0) {
            throw MonaException.withHint(
                    "❌ An event must end after it starts.",
                    hint);
        }
        return new EventArguments(description, start, end);
    }

    /**
     * Returns the validated task number from a mark, unmark, or delete command.
     *
     * @param userInput the trimmed line entered by the user.
     * @param commandWord the command whose task number should be parsed.
     * @return the task number entered by the user.
     * @throws MonaException if no task number is given or it is not an integer.
     */
    public static int parseTaskNumber(String userInput, CommandWord commandWord) throws MonaException {
        String argument = commandWord.extractArguments(userInput).trim();
        if (argument.isEmpty()) {
            String message = commandWord == CommandWord.DELETE
                    ? "❌ Tell me which task should be deleted."
                    : "❌ Tell me which task's fate to alter.";
            throw MonaException.withHint(message, commandWord + " 2");
        }

        try {
            return Integer.parseInt(argument);
        } catch (NumberFormatException exception) {
            throw MonaException.withHint(
                    "❌ No such fate is written in the constellations. "
                            + "Please enter a valid task number.",
                    commandWord + " 2");
        }
    }

    /**
     * Returns the validated date from an on command.
     *
     * @param userInput the trimmed line entered by the user.
     * @return the date entered by the user.
     * @throws MonaException if no date is given or it cannot be parsed.
     */
    public static TaskDateTime parseOnDate(String userInput) throws MonaException {
        String argument = CommandWord.ON.extractArguments(userInput).trim();
        if (argument.isEmpty()) {
            throw MonaException.withHint(
                    "❌ Tell me which date's fate to reveal.",
                    "on 2019-10-15");
        }

        return parseDate(argument, "on 2019-10-15");
    }

    /**
     * Returns the validated number of days from an in command.
     *
     * @param userInput the trimmed line entered by the user.
     * @return the non-negative number of days entered by the user.
     * @throws MonaException if no number is given or it is not a non-negative integer.
     */
    public static int parseDaysAhead(String userInput) throws MonaException {
        String argument = CommandWord.IN.extractArguments(userInput).trim();
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

        return daysAhead;
    }

    /**
     * Returns user-entered text parsed as a date, optionally with a time of day.
     *
     * @param dateText the text to parse, either {@code yyyy-mm-dd HHmm} or {@code yyyy-mm-dd}.
     * @param hint an example command demonstrating the correct usage, shown if parsing fails.
     * @return the parsed date.
     * @throws MonaException if the text matches neither accepted format.
     */
    public static TaskDateTime parseDate(String dateText, String hint) throws MonaException {
        try {
            return TaskDateTime.parse(dateText);
        } catch (DateTimeParseException exception) {
            throw MonaException.withHint(
                    "❌ The stars only read dates as yyyy-mm-dd, optionally followed by a "
                            + "24-hour time, such as 2019-10-15 or 2019-10-15 1800.",
                    hint);
        }
    }

    private static String validateDescription(String description, String missingMessage, String hint)
            throws MonaException {
        String trimmedDescription = description.trim();
        if (trimmedDescription.isEmpty()) {
            throw MonaException.withHint(missingMessage, hint);
        }
        if (trimmedDescription.contains("|")) {
            throw MonaException.withHint(
                    "❌ Task descriptions cannot contain the | character.",
                    hint);
        }
        return trimmedDescription;
    }

    /**
     * Stores the validated description and date parsed from a deadline command.
     *
     * @param description the deadline description.
     * @param deadline the deadline date, optionally with a time of day.
     */
    public record DeadlineArguments(String description, TaskDateTime deadline) {
    }

    /**
     * Stores the validated description, start, and end parsed from an event command.
     *
     * @param description the event description.
     * @param start the event's start date, optionally with a time of day.
     * @param end the event's end date, optionally with a time of day.
     */
    public record EventArguments(String description, TaskDateTime start, TaskDateTime end) {
    }
}
