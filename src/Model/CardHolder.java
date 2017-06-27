package Model;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by H0111in on 05/23/2017.
 */
public class CardHolder {

    PokemonCard basicCard;
    PokemonCard stageOneCard;
    List<EnergyCard> energyCards;

    public CardHolder() {
        energyCards = new ArrayList<>();
    }

    public CardHolder add(Card card) throws Exception {
        if (card instanceof PokemonCard) {
            if (((PokemonCard) card).getLevel().contains("basic")) {
                if (basicCard == null)
                    basicCard = (PokemonCard) card;
                else
                    throw new Exception(basicCard.getName() + " is already here");
            } else {
                if (stageOneCard == null)
                    stageOneCard = (PokemonCard) card;
                else
                    throw new Exception(stageOneCard.getName() + " is already here");
            }
        } else if (card instanceof EnergyCard) {
            energyCards.add((EnergyCard) card);
        } else {
            throw new Exception("Add Wrong Type UIControls!");
        }
        return this;
    }

    public Card pop(String id) throws Exception {
        Card card = null;
        for (Card eCard : getEnergyCards())
            if (eCard.getId().equals(id)) {
                card = eCard;
                energyCards.remove(eCard);
                return card;
            }
        if (stageOneCard != null)
            if (stageOneCard.getId().equals(id)) {
                card = getStageOneCard();
                stageOneCard = null;
                return card;
            }

        if (basicCard != null && basicCard.getId().equals(id)) {
            card = getBasicCard();
            basicCard = null;
            return card;
        }
        return card;
    }

    public List<Card> popAllCard() {
        List<Card> output = new ArrayList<>();
        for (Card eCard : energyCards) output.add(eCard);
        if (basicCard != null)
            output.add(getBasicCard());
        if (stageOneCard != null)
            output.add(getStageOneCard());
        energyCards = new ArrayList<>();
        basicCard = null;
        stageOneCard = null;
        return output;
    }

    public Card getCard(String id) throws CloneNotSupportedException {
        for (Card eCard : energyCards) if (eCard.getId().equals(id)) return eCard.clone();
        if (basicCard != null && basicCard.getId().equals(id)) return basicCard.clone();
        if (stageOneCard != null && stageOneCard.getId().equals(id)) return stageOneCard.clone();
        return null;
    }

    public void remove(String id) throws Exception {
        pop(id);
    }

    public Map<String, Card> getAllCard() {
        Map<String, Card> output = new HashMap<>();
        for (Card eCard : energyCards) output.put(eCard.getId(), eCard);
        if (stageOneCard != null)
            output.put(getStageOneCard().getId(), getStageOneCard());
        if (basicCard != null)
            output.put(getBasicCard().getId(), getBasicCard());
        return output;
    }

    public String getId() {
        if (getTopCard() == null)
            return "";
        return getTopCard().getId();
    }

    public PokemonCard getTopCard() {
        if (stageOneCard == null)
            return basicCard;
        else return stageOneCard;
    }

    public PokemonCard getBasicCard() {
        return basicCard;
    }

    public PokemonCard getStageOneCard() {
        return stageOneCard;
    }

    public List<EnergyCard> getEnergyCards() {
        return energyCards;
    }

    public Attack getBestAttack() throws ScriptException {
        Attack bestAttack = null;
        if (getTopCard() != null)
            for (Attack attack : getTopCard().getAttackList()) {
                if (attack.hasEnoughEnergy(getEnergyCards()))//has enough energy?
                {
                    if (bestAttack == null) {
                        bestAttack = attack;
                    } else if (attack.getAbility().getActionsPower() > bestAttack.getAbility().getActionsPower())
                        bestAttack = attack;
                }
            }
        return bestAttack;
    }
}
