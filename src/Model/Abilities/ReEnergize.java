package Model.Abilities;

import Enums.ActionTarget;
import Listeners.LogicEventListener;
import Model.Player;

import javax.script.ScriptException;
import java.util.Arrays;

/**
 * Created by hosein on 2017-06-22.
 */
public class ReEnergize extends BaseAction implements IActionStrategy {

    private String amountDestination;
    private boolean isUserChoiceSource;
    private String amountSource;
    private boolean isUserChoiceDestination;
    private ActionTarget targetDestination;

    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        return false;
    }

    @Override
    public String[] parse(String[] words) throws Exception {
        //Energy Switch:reenergize:target:choice:your:1:target:choice:your:1
        int i = 0;
        name = words[i++];
        if (words[i].equals(targetTag))
            i++;
        if (words[i].equals("choice")) {
            isUserChoiceSource = true;
            i++;
        }
        target = toTarget(words[i++]);
        amountSource = words[i++];

        if (words[i].equals(targetTag))
            i++;
        if (words[i].equals("choice")) {
            isUserChoiceDestination = true;
            i++;
        }
        targetDestination = toTarget(words[i++]);
        amountDestination = words[i++];

        if (i < words.length)
            return Arrays.copyOfRange(words, i, words.length);
        else return new String[0];
    }

    @Override
    public void addListener(LogicEventListener listener) {

    }

    @Override
    public void clearListener() {

    }
}
