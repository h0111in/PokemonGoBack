package Model.Abilities;

import Enums.Area;
import Model.EnergyCard;
import Model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hosein on 2017-06-20.
 */
public class DeEnergize extends BaseAction {


    @Override
    public boolean fight(Player player, Player opponent) throws Exception {

        Player targetPlayer = getTargetPlayer(player, opponent);

        List<String> energyCardList = new ArrayList<>();
        for (EnergyCard energyCard : targetPlayer.getActiveCard().getEnergyCards())
            energyCardList.add(energyCard.getId());
        for (String energyCard : energyCardList) {
            targetPlayer.addCard(targetPlayer.popCard(energyCard, targetPlayer.getActiveCard().getId()), Area.discard, "");
        }
        return true;
    }

}
