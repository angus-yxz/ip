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

    /**
     * Creates a UI that reads from standard input and writes UTF-8 text to standard output.
     */
    public Ui() {
        // The default console encoding on Windows cannot represent the emoji used in Mona's
        // messages, so stdout is switched to UTF-8 explicitly rather than relying on the
        // platform default.
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));
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
        System.out.print("Mona > ");
        return scanner.nextLine().trim();
    }

    /**
     * Shows a message between Mona's separator lines.
     *
     * @param text the message to show.
     */
    public void showMessage(String text) {
        System.out.println(SEPARATOR);
        System.out.println(text);
        System.out.println(SEPARATOR);
    }
}
