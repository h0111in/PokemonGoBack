package Model;

import Enums.ActionStatus;
import Enums.ActionTarget;
import Enums.Area;
import Enums.CardCategory;
import Model.Abilities.Ability;
import Model.Abilities.Counter;
import Parser.IDataLoader;
import javafx.scene.control.Alert;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import static Controller.Helper.alert;


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
