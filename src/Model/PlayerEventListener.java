package Model;

import java.util.EventListener;

public interface PlayerEventListener extends EventListener {
    void pushCard(CardEvent evt);

    void popCard(CardEvent evt) throws Exception;
}
