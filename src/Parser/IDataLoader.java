package Parser;

import Model.Abilities.Ability;
import Model.Card;

import java.util.List;

/**
 * Created by H0111in on 2017-06-20.
 */
public interface IDataLoader {

    List<Ability> getAbilities() throws Exception;

    List<Card> getCards(List<Ability> abilities);

    List<Integer> getDeckIndexes(Enums.Player player);
}
