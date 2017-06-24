package View;

import Listeners.uiCardEventListener;
import Model.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.script.ScriptException;
import javax.swing.event.EventListenerList;
import java.io.IOException;

public class NormalCardController extends GridPane implements IDialog {

    @FXML
    protected GridPane attack1;
    @FXML
    protected Label name;
    @FXML
    protected Label hp;
    @FXML
    protected Label level;
    @FXML
    protected Label attack1Name;
    @FXML
    protected Label attack1Description;
    @FXML
    protected Label attack1Cost;
    @FXML
    protected Label attack1Power;

    @FXML
    protected GridPane attack2;
    @FXML
    protected Label attack2Name;
    @FXML
    protected Label attack2Description;
    @FXML
    protected Label attack2Cost;
    @FXML
    protected Label attack2Power;
    @FXML
    protected GridPane attack3;
    @FXML
    protected Label attack3Name;
    @FXML
    protected Label attack3Description;
    @FXML
    protected Label attack3Cost;
    @FXML
    protected Label attack3Power;

    @FXML
    protected Label retreatDescription;
    @FXML
    protected Label retreatName;
    @FXML
    protected GridPane retreat;

    @FXML
    protected Button buttonClose;

    private Model.Card card;
    private EventListenerList listenerList;
    private Enums.Player playerName;
    public int attackIndex;
    private ButtonType result = ButtonType.CLOSE;

    public NormalCardController(Card card, Enums.Player playerName) throws ScriptException {
        attackIndex = -2;
        this.card = card;
        this.playerName = playerName;
        listenerList = new EventListenerList();

        //region Set Scene
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NormalCard.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        this.setStyle("-fx-background-color: greenyellow");
        //endregion

        //region initialize properties
        this.name.setText(card.getName());
        if (card instanceof PokemonCard) {
            PokemonCard pokemonCard = (PokemonCard) card;
            this.hp.setText("HP " + String.valueOf(pokemonCard.getHitPoint()));
            this.level.setText(pokemonCard.getLevel());
            hp.setVisible(true);
            level.setVisible(true);

            //Attack1
            if (pokemonCard.getAttackList().size() > 0) {
                attack1.setVisible(true);
                attack1.setCursor(Cursor.HAND);
                Attack attack1 = pokemonCard.getAttackList().get(0);
                attack1Name.setText(attack1.getAbility().getName());

                attack1Description.setText(attack1.toString());
                attack1Power.setText(attack1.getAbility().getActionsPowerText());
                attack1Cost.setText(attack1.getCostString());
            }

            //Attack2
            if (pokemonCard.getAttackList().size() > 1) {
                attack2.setVisible(true);
                attack2.setCursor(Cursor.HAND);
                Attack atk2 = pokemonCard.getAttackList().get(1);
                attack2Name.setText(atk2.getAbility().getName());

                attack2Description.setText(atk2.toString());
                attack2Power.setText(atk2.getAbility().getActionsPowerText());
                attack2Cost.setText(atk2.getCostString());
            }

            //Attack3
            if (pokemonCard.getAttackList().size() > 2) {
                attack3.setVisible(true);
                attack3.setCursor(Cursor.HAND);
                Attack atk3 = pokemonCard.getAttackList().get(2);
                attack3Name.setText(atk3.getAbility().getName());
                attack3Description.setText(atk3.toString());
                attack3Power.setText(atk3.getAbility().getActionsPowerText());
                attack3Cost.setText(atk3.getCostString());
            }
            //Retreat

            retreat.setVisible(true);
            retreat.setCursor(Cursor.HAND);
            Attack retreat = pokemonCard.getRetreat();
            retreatDescription.setText(retreat.getCostString());

        } else if (card instanceof TrainerCard) {
            TrainerCard trainerCard = (TrainerCard) getCard();
            hp.setText(trainerCard.getType());
            attack1Name.setText(trainerCard.getAttack().getAbility().getName());

            this.level.setText(card.getType());
            level.setVisible(true);

            attack1Description.setText(trainerCard.getAttack().toString());
            attack1Power.setText(trainerCard.getAttack().getAbility().getActionsPowerText());
            attack1Cost.setText(trainerCard.getAttack().getCostString());
            attack1.setVisible(true);
            attack1.setCursor(Cursor.HAND);

        } else if (card instanceof EnergyCard) {
            this.level.setText(card.getType());
            level.setVisible(true);
        }
        //endregion

    }

    @FXML
    private void handleButtonAction(Event event) {
        ((Stage) getScene().getWindow()).close();
    }

    @FXML
    private void handleAttack1Clicked(Event event) throws Exception {
        attackIndex = 0;
        ((Stage) getScene().getWindow()).close();

//        if (getCard() instanceof PokemonCard)
//            fireAbility(playerName, getId(), 0);
    }

    @FXML
    private void handleAttack2Clicked(Event event) throws Exception {
        attackIndex = 1;
        ((Stage) getScene().getWindow()).close();
//        if (getCard() instanceof PokemonCard)
//            fireAbility(playerName, getId(), 1);
    }

    @FXML
    private void handleAttack3Clicked(Event event) throws Exception {
        attackIndex = 2;
        ((Stage) getScene().getWindow()).close();
//        if (getCard() instanceof PokemonCard)
//            fireAbility(playerName, getId(), 2);
    }

    @FXML
    private void handleRetreatClicked(Event event) throws Exception {
        attackIndex = -1;
        ((Stage) getScene().getWindow()).close();

// if (getCard() instanceof PokemonCard)
//            fireAbility(playerName, getId(), 3);
    }


    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Enums.Player getPlayerName() {
        return playerName;
    }

    //region Events
    public void addListener(uiCardEventListener listener) {
        listenerList.add(uiCardEventListener.class, listener);
    }

    public void removeListener(uiCardEventListener listener) {
        listenerList.remove(uiCardEventListener.class, listener);
    }

    void fireAbility(Enums.Player playerName, String cardId, int attackIndex) throws Exception {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == uiCardEventListener.class) {
                ((uiCardEventListener) listeners[i + 1]).attackRequest(playerName, cardId, attackIndex);
            }
        }
    }

    @Override
    public ButtonType getResult() {
        return result;
    }


    //endregion

}
