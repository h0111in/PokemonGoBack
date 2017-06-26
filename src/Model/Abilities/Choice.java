package Model.Abilities;

import Model.Player;
import javafx.scene.control.Alert;

import javax.script.ScriptException;
import java.util.Arrays;
import java.util.Random;

import static Controller.Helper.alert;

/**
 * Created by hosein on 2017-06-22.
 */
public class Choice extends BaseAction implements IActionStrategy {


    private IActionStrategy ifAction;
    private IActionStrategy elseAction;

    @Override
    public int getTotalEffectivePower() throws ScriptException {
        return 0;
    }

    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        boolean isFight = false;
        if (player.isComputer())
            isFight = ((new Random().nextInt(2) - 1) == 0) ? true : false;
        else
            isFight = fireShowMessage(Alert.AlertType.CONFIRMATION, "Do you want to " + ifAction.toString() + " ?", -1);

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
