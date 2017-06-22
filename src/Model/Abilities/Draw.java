package Model.Abilities;

import Enums.Area;
import Model.Player;

import java.util.Arrays;

/**
 * Created by hosein on 2017-06-21.
 */
public class Draw extends BaseAction implements IActionStrategy {


    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        player.addCard(player
                        .drawCard(power.getCount(player, opponent), Area.deck),
                Area.hand, -1, "");
        return true;
    }

    @Override
    public String[] parse(String[] words) throws Exception {
        //draw:3
        int i = 0;
        name = words[i++].replace("(", "");
        power = new Counter();
        words = power.parse(Arrays.copyOfRange(words, i, words.length));
        i=0;
        if (i < words.length)
            return Arrays.copyOfRange(words, i, words.length);
        else return new String[0];
    }
}
