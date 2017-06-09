package Model;

import Enums.*;

import java.net.URISyntaxException;
import java.util.EventListener;
import java.util.List;

/**
 * Created by H0111in on 6/4/2017.
 */
public interface LogicEventListener extends EventListener {

    void showMessage(String message, int duration);

    boolean flipCoin(Coin defaultFace, int waitForFlipping) throws URISyntaxException;

    List<String> selectCardRequest(String message, int cardNumber, Area area, Enums.Player player);
}
