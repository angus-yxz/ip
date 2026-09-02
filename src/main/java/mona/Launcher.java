package mona;

import javafx.application.Application;

/**
 * Launches Mona's JavaFX application without extending {@link Application}.
 */
public class Launcher {
    /**
     * Starts the JavaFX runtime and opens Mona's main window.
     *
     * @param args command-line arguments passed to JavaFX.
     */
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}
