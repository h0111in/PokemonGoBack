package Model.Abilities;

import Enums.ActionTarget;
import Enums.Area;
import Model.Card;
import Model.Player;
import Model.PokemonCard;

import java.util.Arrays;

/**
 * Created by hosein on 2017-06-21.
 */
public class ReDamage extends BaseAction implements IActionStrategy {

    private boolean isUserChoice;
    private ActionTarget destination;

    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        int benchSize = opponent.getAreaCard(Area.bench).size();
        if (benchSize > 0) {
            if (opponent.getActiveCard().getTopCard() != null) {
                int opponentDamage = opponent.getActiveCard().getTopCard().getDamage();
                opponent.getActiveCard().getTopCard().addDamage(-opponentDamage);
                String lastCardId = "";
                for (Card card : opponent.getAreaCard(Area.bench)) {
                    if (card instanceof PokemonCard) {
                        lastCardId = card.getId();
                        ((PokemonCard) opponent.getCard(card.getId())).addDamage(opponentDamage / benchSize);
                    }
                }
                if (!lastCardId.isEmpty())
                    if (opponentDamage > opponentDamage / benchSize * benchSize)
                        ((PokemonCard) opponent.getCard(lastCardId)).addDamage(opponentDamage - opponentDamage / benchSize * benchSize);
                    else
                        ((PokemonCard) opponent.getCard(lastCardId)).addDamage((opponentDamage / benchSize * benchSize) - opponentDamage);
            }
        }
        return true;
    }

    @Override
    public String[] parse(String[] words) throws Exception {
        //redamage:target:opponent:target:opponent:count(target:last:source:damage)
        int i = 0;
        name = words[i++].replace("(", "");
        //Ear Influence:redamage:source:choice:opponent:destination:opponent:count(target:last:source:damage)
        if (words[i].equals("source"))
            i++;
        if (words[i].equals("choice")) {
            i++;
            isUserChoice = true;
        }
        target = toTarget(words[i++]);
        i++;//skip destination
        destination = toTarget(words[i++]);
        power = new Counter();
        words = power.parse(Arrays.copyOfRange(words, i, words.length));
        i = 0;

        if (i < words.length)
            return Arrays.copyOfRange(words, i, words.length);
        else return new String[0];
    }
}
