package Model.Abilities;

import Enums.*;
import Listeners.LogicEventListener;
import Model.Card;
import Model.Condition;
import javafx.scene.control.Alert;

import javax.script.ScriptException;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.net.URISyntaxException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hosein on 2017-06-20.
 */
public abstract class BaseAction implements IActionStrategy {

    final String targetTag = "target";
    final String condTag = "cond";
    final String statusTag = "status";
    final String countTag = "count";

    //fields
    protected String name;
    protected ActionTarget target;
    protected Counter power;
    private EventListenerList listenerList = new EventListenerList();
    private boolean isUserChoice;

    @Override
    public String toString() {

        String description = name + " with power &power& on target &target& in condition &condition&";
        try {
            description.replace("&power&", String.valueOf(getTotalEffectivePower()));
        } catch (ScriptException e) {
            description.replace("&power&", "?");
        }
        try {
            description.replace("&target&", target.name());
        } catch (Exception e) {
            description.replace("&target&", "?");
        }

        return description;
    }

    @Override
    public int getTotalEffectivePower() throws ScriptException {
        try {
            return power.getTotalEffectivePower();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String getActionsPowerText() throws ScriptException {
        return String.valueOf(getTotalEffectivePower());
    }

    @Override
    public String[] parse(String[] words) throws Exception {
        int i = 0;
        name = words[i++].replace("(", "");
        if (words[i].equals(targetTag)) {
            i++;//skip 'target'
            if (words[i].equals("choice")) {
                i++;
                isUserChoice = true;
            }
            target = toTarget(words[i++]);

            power = new Counter();
            words = power.parse(Arrays.copyOfRange(words, i, words.length));
            i = 0;
        }
        if (i < words.length)
            return Arrays.copyOfRange(words, i, words.length);
        else return new String[0];
    }

    protected Model.Player getTargetPlayer(Model.Player player, Model.Player opponent) throws Exception {
        Model.Player targetPlayer = null;
        switch (target) {
            case opponentActive:
                targetPlayer = opponent;

                break;
            case yourActive:
                targetPlayer = player;
                break;
            case yourBench:
            default:
                throw new Exception("TargetPlayer is not defined.");
        }
        return targetPlayer;
    }

    protected static ActionStatus toStatus(String statusName) {

        switch (statusName) {
            case "paralyzed":
                return ActionStatus.paralyzed;
            case "stuck":
                return ActionStatus.stuck;
            case "poisoned":
                return ActionStatus.poisoned;
            case "asleep":
                return ActionStatus.asleep;
        }
        return ActionStatus.none;
    }

    protected static Area toArea(String areaName) {
        switch (areaName) {
            case "your-bench":
            case "opponent-bench":
                return Area.bench;
        }
        return Area.none;
    }

    protected static ActionTarget toTarget(String targetName) {
        ActionTarget target = ActionTarget.none;
        switch (targetName) {
            case "opponent-active":
            case "opponent":
                return ActionTarget.opponentActive;
            case "your-active":
            case "your":
            case "you":
                return ActionTarget.yourActive;
            case "your-bench":
                return ActionTarget.yourBench;
            case "last":
                return ActionTarget.last;
            case "your-hand":
                return ActionTarget.yourHand;
            case "opponent-hand":
                return ActionTarget.opponentHand;
            case "them":
                return ActionTarget.them;
        }
        return target;
    }

    //region Event
    public void addListener(LogicEventListener listener) {
        listenerList.add(LogicEventListener.class, listener);
    }

    public void clearListener() {
        listenerList = new EventListenerList();
    }

    protected boolean fireShowMessage(Alert.AlertType confirmation, String message, double duration) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == LogicEventListener.class) {
                return ((LogicEventListener) listeners[i + 1]).showMessage(confirmation, message, duration);
            }
        }
        return false;
    }

    protected boolean fireFlipCoin(Coin defaultFace, double waitForFlipping) throws URISyntaxException {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == LogicEventListener.class) {
                return ((LogicEventListener) listeners[i + 1]).flipCoin(defaultFace, waitForFlipping);
            }
        }
        return false;
    }

    protected List<String> fireSelectCardRequest(String message, int cardNumber, List<Card> cardList, boolean showCard) throws Exception {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == LogicEventListener.class) {
                return ((LogicEventListener) listeners[i + 1]).selectCardRequest(message, cardNumber, cardList, showCard);
            }
        }
        return new ArrayList<>();
    }

    protected boolean fireActionRequest(Player playerName, IActionStrategy action) throws Exception {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == LogicEventListener.class) {
                return ((LogicEventListener) listeners[i + 1]).actionRequest(playerName, action);
            }
        }
        return false;
    }
    //endregion

    protected enum Direction {
        top,
        none, bottom
    }
}
