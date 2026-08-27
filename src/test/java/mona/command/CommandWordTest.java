package mona.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link CommandWord}.
 */
public class CommandWordTest {
    @Test
    public void from_listWord_returnsListCommand() {
        assertEquals(Optional.of(CommandWord.LIST), CommandWord.from("list"));
    }

    @Test
    public void from_byeWord_returnsByeCommand() {
        assertEquals(Optional.of(CommandWord.BYE), CommandWord.from("bye"));
    }

    @Test
    public void from_listWithTrailingText_returnsEmpty() {
        assertTrue(CommandWord.from("list 3").isEmpty());
    }

    @Test
    public void from_todoWithArguments_returnsTodoCommand() {
        assertEquals(Optional.of(CommandWord.TODO), CommandWord.from("todo read book"));
    }

    @Test
    public void from_findWithKeyword_returnsFindCommand() {
        assertEquals(Optional.of(CommandWord.FIND), CommandWord.from("find book"));
    }

    @Test
    public void from_bareTodoWord_returnsTodoCommand() {
        assertEquals(Optional.of(CommandWord.TODO), CommandWord.from("todo"));
    }

    @Test
    public void from_todoPrefixOnly_returnsEmpty() {
        assertTrue(CommandWord.from("todox").isEmpty());
    }

    @Test
    public void from_unknownWord_returnsEmpty() {
        assertTrue(CommandWord.from("nonsense").isEmpty());
    }

    @Test
    public void from_wordSharingOnPrefix_returnsEmpty() {
        assertTrue(CommandWord.from("one").isEmpty());
    }

    @Test
    public void extractArguments_todoWithArguments_returnsArgumentText() {
        assertEquals("read book", CommandWord.TODO.extractArguments("todo read book"));
    }

    @Test
    public void extractArguments_bareTodoWord_returnsEmptyString() {
        assertEquals("", CommandWord.TODO.extractArguments("todo"));
    }
}
