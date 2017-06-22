package View;

import Enums.Coin;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;

public class CoinDialogController extends Pane {

    private final Image head;
    private final Image tail;
    private Coin defaultFace = Coin.None;
    private double waitForFlipping = 0;
    @FXML
    private ImageView imageCoinHead;
    @FXML
    private ImageView imageCoinTail;
    @FXML
    private ImageView imageCoinFlip;
    @FXML
    private HBox coinFlipHolder;
    @FXML
    private Label clickLabel;

    @FXML
    private Label messageLabel;
    private int playerChoice;
    protected boolean result;
    private boolean flipped;

    public CoinDialogController(Coin defaultFace, double waitForFlipping) throws URISyntaxException {
        this.defaultFace = defaultFace;
        this.waitForFlipping = waitForFlipping;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CoinDialog.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        head = new Image(getClass().getResource("/asset/coin-head.jpg").toURI().toString());
        imageCoinHead.setImage(head);
        tail = new Image(getClass().getResource("/asset/coin-tail.jpg").toURI().toString());
        imageCoinTail.setImage(tail);


        imageCoinFlip.setStyle("-fx-background-color: white;");
        imageCoinFlip.setImage(new Image(getClass().getResource("/asset/coin-flip.gif").toURI().toString()));
        coinFlipHolder.setVisible(false);
        clickLabel.setVisible(false);

        imageCoinHead.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                playerChoice = 1;
                messageLabel.setText("you selected head");
                coinFlipHolder.setVisible(true);
                clickLabel.setVisible(true);
            }
        });
        imageCoinTail.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                playerChoice = 0;
                messageLabel.setText("you selected tail");
                coinFlipHolder.setVisible(true);

                clickLabel.setVisible(true);
            }
        });

        imageCoinFlip.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                if (!CoinDialogController.this.flipped) {
                    CoinDialogController.this.flipped = true;
                    int result = new Random().nextInt(100) % 2;
                    if (result == 1)
                        imageCoinFlip.setImage(head);
                    else imageCoinFlip.setImage(tail);

                    CoinDialogController.this.result = playerChoice == result;

                    if (playerChoice == result) {
                        messageLabel.setText("YOU WON!");
                        messageLabel.setTextFill(Color.GREENYELLOW);
                    } else {

                        messageLabel.setText("YOU LOST!");
                        messageLabel.setTextFill(Color.RED);
                    }
                    clickLabel.setText("RETURN TO GAME");
                } else {
                    ((Stage) getScene().getWindow()).close();
                }
            }
        });
//Auto Choice
        if (defaultFace != Coin.None) {
            if (defaultFace == Coin.Head) {
                imageCoinHead.getOnMouseClicked().handle(null);

            } else imageCoinTail.getOnMouseClicked().handle(null);


        }
    }


}


