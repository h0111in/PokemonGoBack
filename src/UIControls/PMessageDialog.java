package UIControls;

import Controller.Helper;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by H0111in on 05/29/2017.
 */
public class PMessageDialog extends HBox {

    @FXML
    protected Label message;

    public PMessageDialog(String message,int duration, javafx.scene.paint.Color color){
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

                PMessageDialog.this.getScene().getWindow().hide();
            }
        });
        Helper.wait(duration, new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                PMessageDialog.this.message.getOnMouseClicked().handle(null);
            }
        });
    }
}
