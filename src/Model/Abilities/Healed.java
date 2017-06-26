package Model.Abilities;

import Model.Player;

import javax.script.ScriptException;
import java.util.Arrays;

/**
 * Created by hosein on 2017-06-22.
 */
public class Healed extends BaseAction implements IActionStrategy {


    @Override
    public int getTotalEffectivePower() throws ScriptException {
        return 0;
    }

    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        Player targetPlayer = getTargetPlayer(player, opponent);
        return targetPlayer.getActiveCard().getTopCard().getTotalHealed() > 0;
    }

    @Override
    public String[] parse(String[] words) throws Exception {
        //cond:healed:target:your-active todo
        int i = 0;
        name = words[i++];
        if (words[i].equals(targetTag))
            i++;
        target = toTarget(words[i++]);

        if (i < words.length)
            return Arrays.copyOfRange(words, i, words.length);
        else return new String[0];

    }
}
