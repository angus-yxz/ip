package mona.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Ui}.
 */
public class UiTest {
    private final InputStream originalInput = System.in;
    private final PrintStream originalOutput = System.out;

    @AfterEach
    public void restoreStreams() {
        System.setIn(originalInput);
        System.setOut(originalOutput);
    }

    @Test
    public void showMessage_text_printsTextBetweenSeparatorLines() {
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
        Ui ui = new Ui();

        ui.showMessage("Hello");

        String[] lines = capturedOutput.toString(StandardCharsets.UTF_8).split("\\R");
        assertEquals(3, lines.length);
        assertEquals(lines[0], lines[2]);
        assertEquals("Hello", lines[1]);
    }

    @Test
    public void readCommand_inputWithSurroundingWhitespace_returnsTrimmedLine() {
        System.setIn(new ByteArrayInputStream("  todo read book  \n".getBytes(StandardCharsets.UTF_8)));
        ByteArrayOutputStream capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput, true, StandardCharsets.UTF_8));
        Ui ui = new Ui();

        String command = ui.readCommand();

        assertEquals("todo read book", command);
        assertTrue(capturedOutput.toString(StandardCharsets.UTF_8).contains("Mona > "));
    }
}
