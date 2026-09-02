package mona.ui;

import java.util.Objects;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import mona.Mona;

/**
 * Controls Mona's main JavaFX window.
 */
public class MainWindow extends AnchorPane {
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
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText().trim();
        String response = mona.getResponse(input);
        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getMonaDialog(response, monaImage));
        userInput.clear();
    }

    private static Image loadImage(String resourcePath) {
        return new Image(Objects.requireNonNull(
                MainWindow.class.getResourceAsStream(resourcePath),
                "Missing image resource: " + resourcePath));
    }
}
