package View;

import Model.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import javax.script.ScriptException;
import javax.swing.event.EventListenerList;
import java.io.IOException;

public class NormalCard extends GridPane {

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
    protected Button buttonClose;

    private Model.Card card;
    private EventListenerList listenerList;
    private Enums.Player playerName;

    public NormalCard(Card card, Enums.Player playerName) throws ScriptException {

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
                String description = "";
                for (Action action : attack1.getAbility().actionList)
                    description += action.getName() + ",Condition: " + action.getCondition() + ", Target: " + action.getTarget();
                attack1Description.setText(description);
                attack1Power.setText(attack1.getAbility().getActionsPowerText());
                attack1Cost.setText(attack1.getCostAmount() + "X" + attack1.getCostType());
            }

        } else if (card instanceof TrainerCard) {
            TrainerCard trainerCard = (TrainerCard) getCard();
            hp.setText(trainerCard.getType());
            attack1Name.setText(trainerCard.getAttack().getAbility().getName());
        }
        //endregion

    }

    @FXML
    private void handleButtonAction(Event event) {
        ((Stage) getScene().getWindow()).close();
    }

    @FXML
    private void handleAttack1Clicked(Event event) throws Exception {
        if (getCard() instanceof PokemonCard)
            fireAbility(playerName, getId(), 0);
    }

    @FXML
    private void handleAttack2Clicked(Event event) throws Exception {
        if (getCard() instanceof PokemonCard)
            fireAbility(playerName, getId(), 1);
    }

    @FXML
    private void handleAttack3Clicked(Event event) throws Exception {
        if (getCard() instanceof PokemonCard)
            fireAbility(playerName, getId(), 2);
    }

    @FXML
    private void handleAttack4Clicked(Event event) throws Exception {
        if (getCard() instanceof PokemonCard)
            fireAbility(playerName, getId(), 3);
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
    public void addListener(uiCardEvent listener) {
        listenerList.add(uiCardEvent.class, listener);
    }

    public void removeListener(uiCardEvent listener) {
        listenerList.remove(uiCardEvent.class, listener);
    }

    void fireAbility(Enums.Player playerName, String cardId, int attackIndex) throws Exception {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == uiCardEvent.class) {
                ((uiCardEvent) listeners[i + 1]).attackRequest(playerName, cardId, attackIndex);
            }
        }
    }


    //endregion

}
