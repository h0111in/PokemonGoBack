package Model.Abilities;

import Enums.Status;
import Model.Player;

import java.util.Arrays;

/**
 * Created by H0111in on 2017-06-21.
 */
public class DeState extends BaseAction implements IActionStrategy {

    private Status status;

    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        getTargetPlayer(player, opponent).getActiveCard().getTopCard().setStatus(status);
        return true;
    }

    @Override
    public String[] parse(String[] words) throws Exception {
        //destat:target:last
        int i = 0;
        name = words[i++].replace("(", "");
        i++;//skip 'target'
        target = toTarget(words[i++]);
        status = Status.none;

        if (i < words.length)
            return Arrays.copyOfRange(words, i, words.length);
        else return new String[0];
    }
}
