package Model.Abilities;

import Model.Player;

import java.util.Arrays;

/**
 * Created by hosein on 2017-06-21.
 */
public class Cond extends BaseAction implements IActionStrategy {

    private IActionStrategy conditionalAction;

    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        return conditionalAction.fight(player, opponent);
    }

    @Override
    public String[] parse(String[] words) throws Exception {
        //cond:flip:(applystat:status:asleep:opponent-active,applystat:status:poisoned:opponent-active)
        //cond:flip:dam:target:opponent-active:20
        //cond:ability:deck:destination:discard:target:choice:you:1:(search:target:you:source:deck:filter:top:8:1,shuffle:target:you)
        //cond:flip:dam:target:opponent-active:30:else:dam:target:your-active:30
        //cond:ability:deenergize:target:your-active:1:(search:target:you:source:discard:filter:cat:item:1)
        //cond:count(target:your-active:energy:psychic)>0:dam:target:opponent-active:20
        int i = 0;
        name = words[i++].replace("(", "");
        //condition statement

        conditionalAction = new ActionFactory().getAction(words[i]);
        return conditionalAction.parse(Arrays.copyOfRange(words, i, words.length));

    }
}
