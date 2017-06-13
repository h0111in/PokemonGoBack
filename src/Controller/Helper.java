package Controller;

import Enums.Coin;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Created by H0111in on 05/29/2017.
 */
public class Helper {

    public static ButtonType alert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent())
            return result.get();
        return ButtonType.CANCEL;
    }

    public static void wait(double millisecond, EventHandler<WorkerStateEvent> onSucceeded) {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    Thread.sleep((int) (millisecond * 1000));
                } catch (InterruptedException e) {
                }
                return null;
            }
        };
        if (onSucceeded != null)
            sleeper.setOnSucceeded(onSucceeded);
        new Thread(sleeper).start();
    }

}
