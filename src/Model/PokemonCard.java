package Model;

import Enums.CardCategory;

import java.util.List;

/**
 * Created by H0111in on 05/20/2017.
 */
public class PokemonCard implements Card {

    private String id;
    private String name;
    private CardCategory category;
    private String type;
    private String level;
    private int hitPoint;
    private Attack retreat;
    private List<Attack> attackList;
    private int damage;
    private int totalHealed;
    // Glameow 0 :pokemon 1:            basic 2        colorless 3:       60 4:           retreat 5:cat:colorless 6:2 7:attacks 8:cat:colorless 9:1 10:1 11,cat:colorless 12:2 13:2 14

    public PokemonCard(String name, CardCategory category, String level, String type, int hitPoint, Attack retreat, List<Attack> attacks) {
        this.id = "";
        this.name = name;
        this.category = category;
        this.type = type;
        this.level = level;
        this.hitPoint = hitPoint;
        this.retreat = retreat;
        attackList = attacks;
    }

    @Override
    public Card setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public CardCategory getCategory() {
        return category;
    }

    @Override
    public Card setCategory(CardCategory category) {
        this.category = category;
        return this;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Card setType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Card setId(String id) {
        this.id = id;
        return this;
    }

    @Override
    public String getName() {
        return name;
    }


    public String getLevel() {
        return level;
    }

    public int getHitPoint() {
        return hitPoint;
    }

    public Attack getRetreat() {
        return retreat;
    }

//    public int getValue() {
////        Card comparable features:
////
////        1. Count of attacks=>total attack damages=>max *
////                2. Required energy of each attack=>average required energy=>min
////        3. Damage of each attack=>average attack damages=>max
////        4. Risk: total risky damage=>percent of total damage=>min
//
//        int totalDamages = 0;
//        for (Attack attack : attackList) {
//            {
//                for (Action action : attack.getAbility().actionList) {
//                    if (action.getTarget().equals("opponentActive"))
//                     totalDamages += Integer.valueOf(action.getPower());
//
//                }
//                totalEnergy += attack.getCostAmount();
//            }
//        }
//    }

    public List<Attack> getAttackList() {
        return attackList;
    }

    public Card clone() throws CloneNotSupportedException {
        return (PokemonCard) super.clone();
    }

    public int getHealth() {
        return (hitPoint - damage + totalHealed) % hitPoint;
    }

    public int getDamage() {
        return damage;
    }

    public Card setDamage(int damage) {
        this.damage = damage;
        return this;
    }

    public void SetHeal(int heal) {
        totalHealed = totalHealed + heal % hitPoint;
    }

    public int getTotalHealed() {
        return totalHealed;
    }
}