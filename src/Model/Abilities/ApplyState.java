package Model.Abilities;

import Enums.Status;
import Model.Player;

import java.util.Arrays;

/**
 * Created by H0111in on 2017-06-21.
 */
public class ApplyState extends BaseAction implements IActionStrategy {

    private Status status;

    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        getTargetPlayer(player, opponent).getActiveCard().getTopCard().setStatus(status);
        return true;
    }

    @Override
    public String[] parse(String[] words) throws Exception {
        //applystat:status:paralyzed:opponent-active

        int i = 0;
        name = words[i++].replace("(", "");
        if (words[i].equals(statusTag)) {
            i++;//skip 'status'
            status = toStatus(words[i++]
                    .replace("(", "").replace(")", ""));
            target = toTarget(words[i++].replace("(", "").replace(")", ""));
        }
        if (i < words.length)
            return Arrays.copyOfRange(words, i, words.length);
        else return new String[0];
    }
}
