package Listeners;

import Enums.*;
import Model.Abilities.IActionStrategy;
import Model.Card;
import javafx.scene.control.Alert;

import java.net.URISyntaxException;
import java.util.EventListener;
import java.util.List;

/**
 * Created by H0111in on 6/4/2017.
 */
public interface LogicEventListener extends EventListener {

    Boolean showMessage(Alert.AlertType confirmation, String message, double duration);

    boolean flipCoin(Coin defaultFace, double waitForFlipping) throws URISyntaxException;

    List<String> selectCardRequest(String message, int totalRequired, List<Card> cardList, boolean showCard) throws Exception;

    boolean actionRequest(Player playerName, IActionStrategy action) throws Exception;
}
