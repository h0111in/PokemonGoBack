package Model.Abilities;

import Enums.TriggerTime;
import Model.Player;

import java.util.Arrays;

/**
 * Created by hosein on 2017-06-21.
 */
public class Add extends BaseAction implements IActionStrategy {
    private IActionStrategy innerAction;
    private TriggerTime triggerTime;


    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        fireActionRequest(player.getName(), innerAction);
        return true;
    }

    @Override
    public String[] parse(String[] words) throws Exception {
        //Floral Crown:add:target:your:trigger:opponent:turn-end:(heal:target:self:20)
        //At the end of your opponent's turn, heal 20 damage from the Basic Pokémon this card is attached to.
        int i = 0;
        name = words[i++].replace("(", "");
        if (words[i].equals(targetTag))
            i++;//skip 'target'
        target = toTarget(words[i++]);
        if (words[i].equals("trigger")) {
            i++;
            triggerTime = TriggerTime.valueOf(words[i++] + "_" + words[i++].replace("-", "_"));
        }
        if (!words[i].isEmpty()) {
            innerAction = new ActionFactory().getAction(words[i]);
            innerAction.parse(Arrays.copyOfRange(words, i, words.length));
        }
        power = ((BaseAction) innerAction).power;

        if (i < words.length)
            return Arrays.copyOfRange(words, i, words.length);
        else return new String[0];
    }

    public TriggerTime getTriggerTime() {
        return triggerTime;
    }

}
