package mona.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

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
import mona.command.TodoCommand;
import mona.command.UnmarkCommand;
import mona.parser.Parser.DeadlineArguments;
import mona.parser.Parser.EventArguments;
import mona.task.TaskDateTime;

/**
 * Tests {@link Parser}.
 */
public class ParserTest {
    @Test
    public void parse_byeCommand_returnsExitCommand() throws MonaException {
        Command command = Parser.parse("bye");

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    public void parse_listCommand_returnsListCommand() throws MonaException {
        Command command = Parser.parse("list");

        assertInstanceOf(ListCommand.class, command);
        assertFalse(command.isExit());
    }

    @Test
    public void parse_findCommand_returnsFindCommand() throws MonaException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_findWithoutKeyword_throwsMonaException() {
        assertThrows(MonaException.class, () -> Parser.parse("find"));
    }

    @Test
    public void parse_markCommand_returnsMarkCommand() throws MonaException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 2"));
    }

    @Test
    public void parse_unmarkCommand_returnsUnmarkCommand() throws MonaException {
        assertInstanceOf(UnmarkCommand.class, Parser.parse("unmark 2"));
    }

    @Test
    public void parse_deleteCommand_returnsDeleteCommand() throws MonaException {
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 2"));
    }

    @Test
    public void parse_todoCommand_returnsTodoCommand() throws MonaException {
        assertInstanceOf(TodoCommand.class, Parser.parse("todo read book"));
    }

    @Test
    public void parse_deadlineCommand_returnsDeadlineCommand() throws MonaException {
        assertInstanceOf(DeadlineCommand.class,
                Parser.parse("deadline return book /by 2019-10-15"));
    }

    @Test
    public void parse_eventCommand_returnsEventCommand() throws MonaException {
        assertInstanceOf(EventCommand.class,
                Parser.parse("event meeting /from 2019-10-15 /to 2019-10-16"));
    }

    @Test
    public void parse_onCommand_returnsOnCommand() throws MonaException {
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-10-15"));
    }

    @Test
    public void parse_inCommand_returnsInCommand() throws MonaException {
        assertInstanceOf(InCommand.class, Parser.parse("in 3"));
    }

    @Test
    public void parse_emptyInput_throwsMonaException() {
        assertThrows(MonaException.class, () -> Parser.parse(""));
    }

    @Test
    public void parse_whitespaceOnlyInput_throwsMonaException() {
        assertThrows(MonaException.class, () -> Parser.parse("   "));
    }

    @Test
    public void parse_unknownCommand_throwsMonaException() {
        assertThrows(MonaException.class, () -> Parser.parse("notacommand"));
    }

    @Test
    public void parseTodoDescription_validDescription_returnsDescription() throws MonaException {
        assertEquals("read book", Parser.parseTodoDescription("todo read book"));
    }

    @Test
    public void parseTodoDescription_noDescription_throwsMonaException() {
        assertThrows(MonaException.class, () -> Parser.parseTodoDescription("todo"));
    }

    @Test
    public void parseTodoDescription_blankDescription_throwsMonaException() {
        assertThrows(MonaException.class, () -> Parser.parseTodoDescription("todo   "));
    }

    @Test
    public void parseFindKeyword_validKeyword_returnsKeyword() throws MonaException {
        assertEquals("book", Parser.parseFindKeyword("find book"));
    }

    @Test
    public void parseFindKeyword_missingKeyword_throwsMonaException() {
        assertThrows(MonaException.class, () -> Parser.parseFindKeyword("find"));
    }

    @Test
    public void parseFindKeyword_blankKeyword_throwsMonaException() {
        assertThrows(MonaException.class, () -> Parser.parseFindKeyword("find   "));
    }

    @Test
    public void parseDeadline_validDate_returnsExpectedArguments() throws MonaException {
        DeadlineArguments arguments = Parser.parseDeadline(
                "deadline return book /by 2019-10-15");

        assertEquals("return book", arguments.description());
        assertEquals(LocalDate.of(2019, 10, 15), arguments.deadline().toLocalDate());
    }

    @Test
    public void parseDeadline_validDateAndTime_returnsExpectedDeadline() throws MonaException {
        DeadlineArguments arguments = Parser.parseDeadline(
                "deadline return book /by 2019-10-15 1800");

        assertEquals("2019-10-15 1800", arguments.deadline().toSaveFormat());
    }

    @Test
    public void parseDeadline_missingSeparator_throwsMonaException() {
        assertThrows(MonaException.class, () -> Parser.parseDeadline("deadline return book"));
    }

    @Test
    public void parseDeadline_emptyDescription_throwsMonaException() {
        assertThrows(MonaException.class,
                () -> Parser.parseDeadline("deadline /by 2019-10-15"));
    }

    @Test
    public void parseDeadline_emptyDate_throwsMonaException() {
        assertThrows(MonaException.class,
                () -> Parser.parseDeadline("deadline return book /by "));
    }

    @Test
    public void parseDeadline_invalidDate_throwsMonaException() {
        assertThrows(MonaException.class,
                () -> Parser.parseDeadline("deadline return book /by not-a-date"));
    }

    @Test
    public void parseEvent_validDates_returnsExpectedArguments() throws MonaException {
        EventArguments arguments = Parser.parseEvent(
                "event meeting /from 2019-10-15 /to 2019-10-16");

        assertEquals("meeting", arguments.description());
        assertEquals(LocalDate.of(2019, 10, 15), arguments.start().toLocalDate());
        assertEquals(LocalDate.of(2019, 10, 16), arguments.end().toLocalDate());
    }

    @Test
    public void parseEvent_missingSeparators_throwsMonaException() {
        assertThrows(MonaException.class, () -> Parser.parseEvent("event meeting"));
    }

    @Test
    public void parseEvent_separatorsInWrongOrder_throwsMonaException() {
        assertThrows(MonaException.class,
                () -> Parser.parseEvent(
                        "event meeting /to 2019-10-16 /from 2019-10-15"));
    }

    @Test
    public void parseEvent_emptyDescription_throwsMonaException() {
        assertThrows(MonaException.class,
                () -> Parser.parseEvent("event /from 2019-10-15 /to 2019-10-16"));
    }

    @Test
    public void parseEvent_emptyStartDate_throwsMonaException() {
        assertThrows(MonaException.class,
                () -> Parser.parseEvent("event meeting /from  /to 2019-10-16"));
    }

    @Test
    public void parseEvent_emptyEndDate_throwsMonaException() {
        assertThrows(MonaException.class,
                () -> Parser.parseEvent("event meeting /from 2019-10-15 /to  "));
    }

    @Test
    public void parseEvent_invalidStartDate_throwsMonaException() {
        assertThrows(MonaException.class,
                () -> Parser.parseEvent("event meeting /from bad-date /to 2019-10-16"));
    }

    @Test
    public void parseTaskNumber_validNumber_returnsNumber() throws MonaException {
        assertEquals(3, Parser.parseTaskNumber("mark 3", CommandWord.MARK));
    }

    @Test
    public void parseTaskNumber_missingNumber_throwsMonaException() {
        assertThrows(MonaException.class,
                () -> Parser.parseTaskNumber("mark", CommandWord.MARK));
    }

    @Test
    public void parseTaskNumber_nonNumericNumber_throwsMonaException() {
        assertThrows(MonaException.class,
                () -> Parser.parseTaskNumber("mark abc", CommandWord.MARK));
    }

    @Test
    public void parseTaskNumber_deleteWithoutNumber_throwsMonaException() {
        assertThrows(MonaException.class,
                () -> Parser.parseTaskNumber("delete", CommandWord.DELETE));
    }

    @Test
    public void parseOnDate_validDate_returnsExpectedDate() throws MonaException {
        TaskDateTime date = Parser.parseOnDate("on 2019-10-15");

        assertEquals(LocalDate.of(2019, 10, 15), date.toLocalDate());
    }

    @Test
    public void parseOnDate_missingDate_throwsMonaException() {
        assertThrows(MonaException.class, () -> Parser.parseOnDate("on"));
    }

    @Test
    public void parseOnDate_invalidDate_throwsMonaException() {
        assertThrows(MonaException.class, () -> Parser.parseOnDate("on not-a-date"));
    }

    @Test
    public void parseDaysAhead_positiveNumber_returnsNumber() throws MonaException {
        assertEquals(3, Parser.parseDaysAhead("in 3"));
    }

    @Test
    public void parseDaysAhead_zero_returnsZero() throws MonaException {
        assertEquals(0, Parser.parseDaysAhead("in 0"));
    }

    @Test
    public void parseDaysAhead_missingNumber_throwsMonaException() {
        assertThrows(MonaException.class, () -> Parser.parseDaysAhead("in"));
    }

    @Test
    public void parseDaysAhead_nonNumericNumber_throwsMonaException() {
        assertThrows(MonaException.class, () -> Parser.parseDaysAhead("in abc"));
    }

    @Test
    public void parseDaysAhead_negativeNumber_throwsMonaException() {
        assertThrows(MonaException.class, () -> Parser.parseDaysAhead("in -1"));
    }

    @Test
    public void parseDate_validDate_returnsExpectedDate() throws MonaException {
        TaskDateTime date = Parser.parseDate("2019-10-15", "hint text");

        assertEquals(LocalDate.of(2019, 10, 15), date.toLocalDate());
    }

    @Test
    public void parseDate_validDateAndTime_returnsExpectedDateAndTime() throws MonaException {
        TaskDateTime date = Parser.parseDate("2019-10-15 1800", "hint text");

        assertEquals("2019-10-15 1800", date.toSaveFormat());
    }

    @Test
    public void parseDate_invalidDate_throwsMonaException() {
        assertThrows(MonaException.class, () -> Parser.parseDate("garbage", "hint text"));
    }
}
