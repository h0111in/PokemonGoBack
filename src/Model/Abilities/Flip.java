package Model.Abilities;

import Enums.Coin;
import Model.Player;
import javafx.scene.control.Alert;

import javax.script.ScriptException;
import java.util.Arrays;
import java.util.Random;

import static Controller.Helper.alert;

/**
 * Created by hosein on 2017-06-21.
 */
public class Flip extends BaseAction implements IActionStrategy {

    private IActionStrategy ifAction;
    private IActionStrategy elseAction;

    @Override
    public int getTotalEffectivePower() throws ScriptException {
        int totalPower = 0;
        if (ifAction != null)
            totalPower = ifAction.getTotalEffectivePower();

        if (elseAction != null)
            totalPower += elseAction.getTotalEffectivePower();

        return totalPower / 2;
    }

    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        boolean isFight = false;
        if (player.isComputer())
            isFight = ((new Random().nextInt(2) - 1) == 0) ? true : false;
        else isFight = fireFlipCoin(Coin.Head, -1);
        if (isFight) {
            if (ifAction != null)
                ifAction.fight(player, opponent);
            return true;
        } else {
            if (elseAction != null)
                elseAction.fight(player, opponent);
            return true;
        }
    }

    @Override
    public String[] parse(String[] words) throws Exception {
        //cond:flip:(applystat:status:asleep:opponent-active,applystat:status:poisoned:opponent-active)
        //cond:flip:dam:target:opponent-active:20
        //cond:flip:dam:target:opponent-active:30:else:dam:target:your-active:30
        int i = 0;
        name = words[i++];
        String[] newWords;
        if (!words[i].isEmpty()) {

            ifAction = new ActionFactory().getAction(words[i]);
            newWords = ifAction.parse(Arrays.copyOfRange(words, i, words.length));

            if (newWords.length < words.length) {
                if (newWords.length > 0) {
                    i = 0;
                    if (newWords[i].equals("else")) i++;
                    elseAction = new ActionFactory().getAction(newWords[i]);
                    newWords = elseAction.parse(Arrays.copyOfRange(newWords, i, newWords.length));
                }
            } else alert(Alert.AlertType.ERROR, "incorrect format for action:" + name);
            words = newWords;
        }
        return words;
    }
}
