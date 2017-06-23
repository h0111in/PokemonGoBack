package Model;

import Enums.*;
import Enums.Player;
import Listeners.CardEventListener;
import Model.Abilities.Ability;

import javax.swing.event.EventListenerList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by H0111in on 05/20/2017.
 */
public class TrainerCard implements Card {
    private String id;
    private String name;
    private CardCategory category;
    private String type;
    private Attack attack;
    private final EventListenerList listenerList;
    private Enums.Player playerName;

    public TrainerCard() {

        this.id = "";
        listenerList = new EventListenerList();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Card setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public CardCategory getCategory() {
        return category;
    }

    @Override
    public Card setCategory(CardCategory category) {
        this.category = category;
        return this;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Card setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Card setId(String id) {
        this.id = id;
        return this;
    }

    //region Event
    public void addListener(CardEventListener listener) {
        listenerList.add(CardEventListener.class, listener);
    }

    void fireCardModified(String propertyName) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == CardEventListener.class) {
                ((CardEventListener) listeners[i + 1]).cardModified(propertyName, this);
            }
        }
    }
    //endregion

    public Card clone() throws CloneNotSupportedException {
        return (TrainerCard) super.clone();
    }


    public void parse(String[] words, List<Ability> abilities) {
        //Switch:trainer:cat:item:71
        //String name, CardCategory category, String type, Attack attack
        name = words[0];
        category = CardCategory.valueOf(words[1]);
        type = words[2];
        attack = new Attack(abilities.get(Integer.parseInt(words[3]) - 1),new HashMap<>());

    }

    public Attack getAttack() {
        return attack;
    }

    @Override
    public Player getPlayerName() {
        return playerName;
    }

    public void setPlayerName(Player playerName) {
        this.playerName = playerName;
    }
}