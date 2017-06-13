package Model;

import Enums.*;
import Enums.Player;

import javax.swing.event.EventListenerList;

/**
 * Created by H0111in on 05/20/2017.
 */
public class TrainerCard implements Card  {
    private String id;
    private String name;
    private CardCategory category;
    private String type;
    private Attack attack;
    private final EventListenerList listenerList;
    private Enums.Player playerName;

    public TrainerCard(String name, CardCategory category, String type, Attack attack) {

        this.id = "";
        this.name = name;
        this.category = category;
        this.type = type;
        this.attack=attack;
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
        this.category=category;
        return this;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Card setType(String type) {
        this.type=type;
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Card setId(String id) {
        this.id=id;
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

    public Attack getAttack() {
        return attack;
    }

    @Override
    public Player getPlayerName() {
        return playerName;
    }

    public void  setPlayerName(Player playerName){
        this.playerName=playerName;
    }
}