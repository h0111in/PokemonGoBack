package Listeners;

import Enums.*;
import Enums.Player;
import Model.Card;

import java.util.EventListener;
import java.util.List;

/**
 * Created by H0111in on 6/4/2017.
 */
public interface uiBoardEventListener extends EventListener {

    int getAreaSize(Enums.Player player, Area area);

    void MoveCard(Area targetArea, Card targetCard, Card targetStageCard, Card flyCard, Area sourceArea, Enums.Player senderPlayer,
                  List<Card> uiCardList, int targetColumnIndex, int sourceColumnIndex, String uiCardId, String uiSmallCardId) throws Exception;

    void doneButtonPressed(Player player) throws Exception;
    void showAreaCard(Area area,Player playerName) throws Exception;
}
