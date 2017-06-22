package View;

import Controller.Helper;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;

/**
 * Created by H0111in on 05/29/2017.
 */
public class MessageDialogController extends HBox implements IDialog {

    private final Alert.AlertType alertType;
    @FXML
    protected Label message;
    private ButtonType result= ButtonType.CLOSE;;

    public MessageDialogController(Alert.AlertType alertType, String message, double duration, javafx.scene.paint.Color color) {
        this.alertType = alertType;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MessageDialog.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.message.setText(message);
        this.message.setTextFill(color);
        this.setStyle("-fx-background-color: transparent;");
        this.message.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                MessageDialogController.this.getScene().getWindow().hide();
            }
        });
        Helper.wait(duration, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                MessageDialogController.this.message.getOnMouseClicked().handle(null);
            }
        });
    }

    @Override
    public ButtonType getResult() {
        return result;
    }
}
