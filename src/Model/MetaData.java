package Model;

import Model.Abilities.Ability;
import Parser.IDataLoader;

import java.util.List;


/**
 * Created by H0111in on 05/22/2017.
 */
public class MetaData {

    private List<Ability> abilityList;
    private List<Card> cards;
    private IDataLoader dataLoader;

    public MetaData(IDataLoader dataLoader) throws Exception {
        this.dataLoader = dataLoader;
        abilityList = dataLoader.getAbilities();
        cards = dataLoader.getCards(abilityList);

    }

    public Ability getAbility(int index) {
        return abilityList.get(index);
    }

    public Card getCard(int index) throws CloneNotSupportedException {
        return cards.get(index).clone();
    }

}
