package Model;

import Model.Abilities.Ability;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Controller.Main.logger;

/**
 * Created by H0111in on 05/22/2017.
 */
public class Attack {
    private Map<String, Integer> costList;
    private Ability ability;

    public Attack(Ability ability, Map<String, Integer> costList) {
        this.costList = costList;
        this.ability = ability;
    }

    public String getCostString() {
        String string = "";

        for (String key : costList.keySet())
            string += costList.get(key) + "X" + key;
        logger.info("cost =  " + string);
        return string;
    }

    public Ability getAbility() {
        return ability;
    }

    public Map<String, Integer> getCostList() {
        return costList;
    }

    public String toString() {
        return ability.action.toString();
    }

    public Integer getCostAmount(String costType) {
        if (costType.isEmpty()) {
            int total = 0;
            for (int value : costList.values())
                total += value;
            return total;
        }

        return costList.get(costType);
    }

    public boolean hasEnoughEnergy(List<EnergyCard> energyCards) {
        return getRequiredEnergy(energyCards).size() == 0;

    }

    public Map<String, Integer> getRequiredEnergy(List<EnergyCard> energyCards) {
        Map<String, Integer> requiredList = new HashMap<>();
        List<String> energyCardTypes = new ArrayList<>();
        for (Card cardType : energyCards) {
            energyCardTypes.add(cardType.getType());
        }
        for (String key : costList.keySet()) {
            int subTotal = 0;
            for (int i = 0; i < energyCardTypes.size(); i++) {
                String energyCard = energyCardTypes.get(i);
                if (energyCard.equals(key) || key.equals("colorless") || energyCard.equals("colorless")) {
                    subTotal++;
                    energyCardTypes.remove(i);
                    i--;
                    if (subTotal == costList.get(key))
                        break;
                }
            }
            if (subTotal < costList.get(key)) {
                requiredList.put(key, costList.get(key) - subTotal);
            }
        }
        return requiredList;
    }
}
