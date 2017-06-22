package Model.Abilities;

import Enums.ActionTarget;
import Model.Player;

import javax.script.ScriptException;
import java.net.URISyntaxException;

/**
 * Created by hosein on 2017-06-20.
 */
public class Damage extends BaseAction {

    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        getTargetPlayer(player, opponent).getActiveCard().getTopCard().addDamage(power.getCount(player, opponent));
        return true;
    }
}
