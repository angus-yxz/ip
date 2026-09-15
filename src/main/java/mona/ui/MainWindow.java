package mona.ui;

import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import mona.Mona;
import mona.MonaResponse;

/**
 * Controls Mona's main JavaFX window.
 */
public class MainWindow extends AnchorPane {
    /** Delay before the window closes after a {@code bye} command, so the farewell message is visible. */
    private static final Duration EXIT_DELAY = Duration.seconds(1);

    private final Image userImage = loadImage("/images/userImage.png");
    private final Image monaImage = loadImage("/images/monaImage.png");

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Mona mona;

    @FXML
    private void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
    }

    /**
     * Sets the Mona instance that executes commands entered in this window.
     *
     * @param mona Mona's command processor.
     */
    public void setMona(Mona mona) {
        this.mona = mona;
        dialogContainer.getChildren().add(
                DialogBox.getMonaDialog(mona.getWelcomeResponse(false), monaImage));
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        MonaResponse response = mona.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getMonaDialog(response, monaImage));
        userInput.clear();
        if (response.isExit()) {
            closeAfterDelay();
        }
    }

    /**
     * Closes the application window after a short delay, giving the user time to read
     * the farewell message before the GUI disappears.
     */
    private void closeAfterDelay() {
        userInput.setDisable(true);
        sendButton.setDisable(true);
        PauseTransition delay = new PauseTransition(EXIT_DELAY);
        delay.setOnFinished(event -> Platform.exit());
        delay.play();
    }

    private static Image loadImage(String resourcePath) {
        return new Image(Objects.requireNonNull(
                MainWindow.class.getResourceAsStream(resourcePath),
                "Missing image resource: " + resourcePath));
    }
}
