package Model.Abilities;

import Enums.Area;
import Model.Player;

import java.util.Arrays;

/**
 * Created by H0111in on 2017-06-21.
 */
public class Shuffle extends BaseAction implements IActionStrategy {

    @Override
    public boolean fight(Player player, Player opponent) throws Exception {

        Player targetPlayer = getTargetPlayer(player, opponent);
        targetPlayer.addCard(Player.shuffle(targetPlayer.popAllCard(Area.deck, -1, "")), Area.deck, -1, "");
        return true;
    }

    @Override
    public String[] parse(String[] words) throws Exception {
        //shuffle:target:opponent
        int i = 0;
        name = words[i++].replace("(", "");
        if (words[i].equals(targetTag)) {
            i++;//skip 'target'
            if (words[i].equals("choice"))
                i++;
            target = toTarget(words[i++]);
        }
        if (i < words.length)
            return Arrays.copyOfRange(words, i, words.length );
        else return new String[0];
    }
}
