package mona.ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Handles Mona's command-line input and output.
 */
public class Ui {
    private static final String SEPARATOR = "____________________________________________________________";
    private static final String BANNER = " __  __  ___  _   _    _ \n"
            + "|  \\/  |/ _ \\| \\ | |  / \\\n"
            + "| |\\/| | | | |  \\| | / _ \\\n"
            + "| |  | | |_| | |\\  |/ ___ \\\n"
            + "|_|  |_|\\___/|_| \\_/_/   \\_\\\n";

    private final Scanner scanner;
    private final PrintStream output;

    /**
     * Creates a UI that reads from standard input and writes UTF-8 text to standard output.
     */
    public Ui() {
        // The default console encoding on Windows cannot represent the emoji used in Mona's
        // messages, so write through a UTF-8 stream rather than relying on the platform default.
        output = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        scanner = new Scanner(System.in);
    }

    /**
     * Shows Mona's banner and welcome message.
     */
    public void showWelcome() {
        showMessage(BANNER
                + "\n✨ Hello, I'm Mona.\nThe constellations lie reflected in the water tonight. "
                + "What fate shall we divine?");
    }

    /**
     * Shows the prompt and returns the next trimmed command line entered by the user.
     *
     * @return the trimmed user input.
     */
    public String readCommand() {
        output.print("Mona > ");
        return scanner.nextLine().trim();
    }

    /**
     * Shows a message between Mona's separator lines.
     *
     * @param text the message to show.
     */
    public void showMessage(String text) {
        printLines(SEPARATOR, text, SEPARATOR);
    }

    private void printLines(String... lines) {
        for (String line : lines) {
            output.println(line);
        }
    }
}
