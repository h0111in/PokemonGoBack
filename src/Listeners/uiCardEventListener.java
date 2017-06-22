package Listeners;

import Enums.*;
import Enums.Player;

import java.util.EventListener;

public interface uiCardEventListener extends EventListener{
    void attackRequest(Enums.Player playerName, String cardId, int attackIndex) throws Exception;
    void applyTrainerCardRequest(Player playerName,String cardId) throws Exception;
    boolean showFaceRequest(Player playerName, String cardId);
    void cardClicked(String cardId);
}