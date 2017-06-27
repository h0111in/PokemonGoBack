package View;

import Controller.Helper;
import Model.Abilities.BaseAction;
import Parser.IDataLoader;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.io.IOException;

/**
 * Created by H0111in on 05/29/2017.
 */
public class MessageDialogController extends GridPane implements IDialog {

    private final Alert.AlertType alertType;
    @FXML
    protected Label message;
    @FXML
    protected Button buttonLeft;
    @FXML
    protected Button buttonRight;
    private ButtonType result = ButtonType.CLOSE;
    ;

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
        buttonLeft.setVisible(false);
        buttonRight.setVisible(false);
        switch (alertType) {

            case NONE:
                break;
            case INFORMATION:
                color = Color.PURPLE;
                result = ButtonType.YES;
                Helper.wait(duration, new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        result = ButtonType.YES;
                        MessageDialogController.this.getScene().getWindow().hide();
                    }
                });
                this.message.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    result = ButtonType.YES;
                    MessageDialogController.this.getScene().getWindow().hide();
                }
            });
                break;
            case WARNING:
                color = Color.ORANGE;
                buttonRight.setVisible(true);
                buttonRight.setText("OK");
                buttonRight.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        result = ButtonType.YES;
                        MessageDialogController.this.getScene().getWindow().hide();
                    }
                });
                this.message.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        result = ButtonType.YES;
                        MessageDialogController.this.getScene().getWindow().hide();
                    }
                });
                break;
            case CONFIRMATION:

                color = Color.BLUE;
                buttonRight.setVisible(true);
                buttonRight.setText("Yes");
                buttonRight.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        result = ButtonType.YES;
                        MessageDialogController.this.getScene().getWindow().hide();
                    }
                });
                buttonLeft.setVisible(true);
                buttonLeft.setText("No");
                buttonLeft.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        result = ButtonType.NO;
                        MessageDialogController.this.getScene().getWindow().hide();
                    }
                });
                break;
            case ERROR:
                color = Color.MAROON;
                buttonRight.setVisible(true);
                buttonRight.setText("OK");
                buttonRight.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        result = ButtonType.YES;
                        MessageDialogController.this.getScene().getWindow().hide();
                    }
                });
                break;
        }
        this.message.setText(message);
        this.message.setTextFill(color);
        this.setStyle("-fx-background-color: transparent;");


    }

    @Override
    public ButtonType getResult() {
        return result;
    }
}
