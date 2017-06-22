package Model.Abilities;

import Listeners.LogicEventListener;
import Model.Player;

import javax.script.ScriptException;

/**
 * Created by H0111in on 2017-06-20.
 */
public interface IActionStrategy {
    int getTotalEffectivePower() throws ScriptException;
    String getActionsPowerText() throws ScriptException;
    boolean fight(Player player, Player opponent) throws Exception;
    String[] parse(String[] words) throws Exception;
    String toString();
    void addListener(LogicEventListener listener);
    void clearListener();

}
