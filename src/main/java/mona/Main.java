package mona;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mona.ui.MainWindow;

/**
 * Starts and configures Mona's JavaFX user interface.
 */
public class Main extends Application {
    private final Mona mona = new Mona();

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Mona");
        stage.setMinHeight(220);
        stage.setMinWidth(417);
        fxmlLoader.<MainWindow>getController().setMona(mona);
        stage.show();
    }
}
