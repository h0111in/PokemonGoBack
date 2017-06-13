package Model;

import Enums.*;
import Enums.Player;

/**
 * Created by H0111in on 05/20/2017.
 */
public interface Card extends Cloneable {
    Card clone() throws CloneNotSupportedException;

    String getName();

    Enums.Player getPlayerName();

    void setPlayerName(Player playerName);

    Card setName(String name);

    CardCategory getCategory();

    Card setCategory(CardCategory category);

    String getType();

    Card setType(String type);

    String getId();

    Card setId(String id);

    public void addListener(CardEventListener listener);

}

