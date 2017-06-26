package Model.Abilities;

import Enums.Area;
import Model.Card;
import Model.EnergyCard;
import Model.Player;

import java.util.List;

/**
 * Created by hosein on 2017-06-20.
 */
public class DeEnergize extends BaseAction {


    @Override
    public boolean fight(Player player, Player opponent) throws Exception {

        Player targetPlayer = getTargetPlayer(player, opponent);

        List<EnergyCard> energyCardList = targetPlayer.getActiveCard().getEnergyCards();
        for (Card energyCard : energyCardList) {
            targetPlayer.addCard(targetPlayer.popCard(energyCard.getId(),
                    targetPlayer.getActiveCard().getId()), Area.discard, "");
        }
        return true;
    }

}
