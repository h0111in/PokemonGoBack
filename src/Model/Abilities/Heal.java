package Model.Abilities;

import Model.Player;

/**
 * Created by H0111in on 2017-06-21.
 */
public class Heal extends BaseAction {

    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        //heal:target:your-active:20
        //Potion:heal:target:your:30 done

        Player targetPlayer = getTargetPlayer(player, opponent);
        if (targetPlayer.getActiveCard().getTopCard() != null) {
            targetPlayer.getActiveCard().getTopCard().setHeal(power.getCount(player, opponent));
        }
        return true;
    }

}
