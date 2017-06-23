package View;

import Enums.Player;
import Model.Card;
import Listeners.uiCardEventListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static View.GameBoardController.popup;

/**
 * Created by H0111in on 05/29/2017.
 */
public class SelectorDialogController extends GridPane {

    @FXML
    protected Button doneButton;

    @FXML
    protected Label title;

    List<String> selectedCards;

    public SelectorDialogController(List<Card> cardList, String title, int required, boolean showCard, Stage primaryStage) throws Exception {
        selectedCards = new ArrayList<>();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SelectorDialog.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        for (Card card : cardList) {
            SmallCardController smallCard = new SmallCardController(card, primaryStage);
            smallCard.addListener(new uiCardEventListener() {
                @Override
                public void attackRequest(Player playerName, String cardId, int attackIndex) throws Exception {

                }

                @Override
                public boolean showFaceRequest(Player playerName, String cardId) {
                    return showCard;
                }

                @Override
                public void cardClicked(String cardId) {

                    smallCard.setStyle("-fx-background-color: deeppink");
                    if (selectedCards.size() < required) {
                        if (!selectedCards.contains(cardId))
                            selectedCards.add(cardId);
                    } else {
                        popup(new MessageDialogController(Alert.AlertType.INFORMATION, "you can just select " + required + "card(s).", 0.5, Color.PURPLE), primaryStage);
                    }
                }
            });
            smallCard.showFace();
            ((HBox) (SelectorDialogController.this.lookup("#container"))).getChildren().add(smallCard);
        }
        this.title.setText(title);

        doneButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                SelectorDialogController.this.getScene().getWindow().hide();
            }
        });
    }

    public List<String> getSelectedCards() {
        return selectedCards;
    }
}
