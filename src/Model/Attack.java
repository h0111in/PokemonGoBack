package Model;

import Model.Abilities.Ability;
import org.omg.PortableInterceptor.INACTIVE;

import java.util.List;
import java.util.Map;

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

    public boolean hasSufficientEnergy(List<EnergyCard> energyCards) {
        for (String key : costList.keySet()) {
            int subTotal = 0;
            {
                for (EnergyCard energyCard : energyCards)
                    if (energyCard.getType().equals(key) || key.equals("colorless") || energyCard.getType().equals("colorless"))
                        subTotal++;
            }
            if (subTotal < costList.get(key))
                return false;
        }
        return true;
    }
}
