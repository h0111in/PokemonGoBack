package Model;

import Enums.*;
import Enums.Player;

import javax.swing.event.EventListenerList;

/**
 * Created by H0111in on 05/20/2017.
 */
public class EnergyCard implements Card {
    private String id;
    private String name;
    private CardCategory category;
    private String type;
    private Enums.Player playerName;

    private final EventListenerList listenerList;

    public EnergyCard(String name, CardCategory category, String type) {
        this.playerName = playerName;
        this.id = "";
        this.name = name;
        this.category = category;
        this.type = type;
        listenerList = new EventListenerList();
    }

    @Override
    public Card setName(String name) {
        this.name = name;
        return this;
    }

    public Card clone() throws CloneNotSupportedException {
        return (EnergyCard) super.clone();
    }

    @Override
    public String getName() {
        return name;
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

    @Override
    public Player getPlayerName() {
        return playerName;
    }

    public void  setPlayerName(Player playerName){
        this.playerName=playerName;
    }
    //endregion

}
