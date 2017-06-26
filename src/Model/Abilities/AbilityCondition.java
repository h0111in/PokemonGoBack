package Model.Abilities;

import Model.Player;
import javafx.scene.control.Alert;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static Controller.Helper.alert;

/**
 * Created by hosein on 2017-06-21.
 */
public class AbilityCondition extends BaseAction implements IActionStrategy {

    private IActionStrategy mainAction;
    private IActionStrategy subordinateAction;


    @Override
    public int getTotalEffectivePower() throws ScriptException {
        int totalPower = 0;
        if (mainAction != null)
            totalPower = mainAction.getTotalEffectivePower();

        if (subordinateAction != null)
            totalPower += subordinateAction.getTotalEffectivePower();

        return totalPower / 2;
    }

    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        if (player.isComputer()) {
            if (new Random().nextBoolean()) {
                mainAction.fight(player, opponent);
                subordinateAction.fight(player, opponent);
                return true;
            }
        } else if (fireShowMessage(Alert.AlertType.CONFIRMATION, "Do you want to " + mainAction.toString(), -1)) {
            mainAction.fight(player, opponent);
            subordinateAction.fight(player, opponent);
            return true;
        }
        return true;
    }

    @Override
    public String[] parse(String[] words) throws Exception {
        //cond:ability:deck:destination:discard:target:choice:you:1:(search:target:you:source:deck:filter:top:8:1,shuffle:target:you)
        //Discard a card from your hand.
        // If you do, look at the top 8 cards of your deck and put 1 of them into your hand. Shuffle the other cards back into your deck.
        //cond:ability:deenergize:target:your-active:1:(search:target:you:source:discard:filter:cat:item:1)
        //Discard a Psychic Energy attached to this Pokémon. If you do, put an Item card from your discard pile into your hand.
        int i = 0;
        name = words[i++];
        String[] newWords = new String[0];
        if (!words[i].isEmpty()) {
            mainAction = new ActionFactory().getAction(words[i]);
            newWords = mainAction.parse(Arrays.copyOfRange(words, i, words.length));
            i = 0;
            //parse=>(action1,action2,...)
            String[] newWords2 = new String[0];
            boolean isMultiActions = false;
            if (newWords[i].contains("(")) {
                while (i < newWords.length && !newWords[i++].contains(")")) ;
                if (i <= newWords.length) {
                    {
                        newWords2 = Arrays.copyOfRange(newWords, 0, i);
                        words = Arrays.copyOfRange(newWords, i, newWords.length);
                        isMultiActions = true;
                    }
                }
            } else {
                newWords2 = newWords;
                isMultiActions = false;
            }

            List<IActionStrategy> innerActionList = new ArrayList<>();
            while (newWords2.length > 0) {
                IActionStrategy innerAction = new ActionFactory().getAction(newWords2[0]);
                newWords2 = innerAction.parse(newWords2);
                innerActionList.add(innerAction);
                if (!isMultiActions)
                    break;
            }
            if (innerActionList.size() > 0) {
                if (innerActionList.size() > 1) {
                    subordinateAction = new ActionFactory().getAction("composite");
                    ((ActionComposite) subordinateAction).addRange(innerActionList);
                } else {
                    subordinateAction = innerActionList.get(0);
                }

            } else alert(Alert.AlertType.WARNING, "action not found if ability applied / actionName= " + name);

            return isMultiActions ? words : newWords2;
        }
        return newWords;
    }
}
