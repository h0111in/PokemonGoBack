package Model.Abilities;

import Enums.ActionTarget;
import Enums.Area;
import Model.Card;
import Model.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by hosein on 2017-06-22.
 */
public class ReEnergize extends BaseAction implements IActionStrategy {

    private String amountDestination;
    private boolean isUserChoiceSource;
    private String amountSource;
    private boolean isUserChoiceDestination;
    private ActionTarget targetDestination;


    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        Player targetPlayer = getTargetPlayer(player, opponent);
        List<Card> holderList = targetPlayer.getAreaCard(Area.bench);
        if (targetPlayer.getActiveCard().getTopCard() != null)
            holderList.add(targetPlayer.getActiveCard().getTopCard());
        Card sourceHolder = null;
        if (holderList.size() > 0) {
            if (targetPlayer.isComputer())
                sourceHolder = holderList.get(0);
            else {
                List<String> selectedList = fireSelectCardRequest("select a card as source for energy", 1, holderList, true);
                if (selectedList.size() > 0)
                    sourceHolder = targetPlayer.getCard(selectedList.get(0));
                else return true;
            }
        }
        List<Card> energyList = new ArrayList<>();
        for (Card energy : targetPlayer.getCardHolder(sourceHolder.getId()).getEnergyCards())
            energyList.add(energy);

        List<String> selectedEnergyList = new ArrayList<>();
        if (energyList.size() > 0)
            if (targetPlayer.isComputer()) {
                selectedEnergyList = new ArrayList<>();
                selectedEnergyList.add(energyList.get(new Random().nextInt(energyList.size())).getId());
            } else {
                selectedEnergyList = fireSelectCardRequest("select one energy card", 1, energyList, true);
            }

        if (selectedEnergyList.size() > 0) {

            if (holderList.size() > 1) {
                Card targetHolder = null;
                if (targetPlayer.isComputer())
                    targetHolder = holderList.get(1);
                else {
                    List<String> selectedList =
                            fireSelectCardRequest("select a card as target for energy", 1, holderList, true);
                    if (selectedList.size() > 0)
                        targetHolder = targetPlayer.getCard(selectedList.get(0));
                    else return true;
                }
                targetPlayer.addCard(targetPlayer.popCard(selectedEnergyList.get(0), sourceHolder.getId())
                        , targetPlayer.getCardArea(targetHolder.getId()), targetHolder.getId());
            }
        }
        return true;
    }

    @Override
    public String[] parse(String[] words) throws Exception {
        //Energy Switch:reenergize:target:choice:your:1:target:choice:your:1
        int i = 0;
        name = words[i++];
        if (words[i].equals(targetTag))
            i++;
        if (words[i].equals("choice")) {
            isUserChoiceSource = true;
            i++;
        }
        target = toTarget(words[i++]);
        amountSource = words[i++];

        if (words[i].equals(targetTag))
            i++;
        if (words[i].equals("choice")) {
            isUserChoiceDestination = true;
            i++;
        }
        targetDestination = toTarget(words[i++]);
        amountDestination = words[i++];

        if (i < words.length)
            return Arrays.copyOfRange(words, i, words.length);
        else return new String[0];
    }

}
