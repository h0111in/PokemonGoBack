package Model;

import Enums.*;
import Enums.Player;
import Listeners.CardEventListener;
import Model.Abilities.Ability;
import javafx.scene.control.Alert;

import javax.swing.event.EventListenerList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Controller.Helper.alert;
import static Controller.Main.logger;

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
    private Player playerName;
    private Status status;

    private final EventListenerList listenerList;
    private int lastHeal;
    private int health;
    // Glameow 0 :pokemon 1:            basic 2        colorless 3:       60 4:           retreat 5:cat:colorless 6:2 7:attacks 8:cat:colorless 9:1 10:1 11,cat:colorless 12:2 13:2 14

    public PokemonCard() {
        attackList = new ArrayList<>();
        status = Status.none;
        this.id = "";
        listenerList = new EventListenerList();

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

    @Override
    public Player getPlayerName() {
        return playerName;
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
//                for (Action action : attack.getAbility().action) {
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

    public void parse(String[] words, List<Ability> abilities) {
//        Pikachu:pokemon:cat:basic:cat:lightning:60:retreat:cat:colorless:1:attacks:cat:colorless:1:5,cat:colorless:2:6
//        Raichu:pokemon:cat:stage-one:Pikachu:cat:lightning:90:attacks:cat:colorless:2:7,cat:colorless:1,cat:lightning:2:8

        try {
            int j = 0;
            int i = 0;
            name = words[0];

            category = CardCategory.valueOf(words[1]);

            if (!words[2].equals("basic")) {
                j++;
            }
            level = words[j + 2];
            type = words[j + 3];
            hitPoint = Integer.parseInt(words[j + 4]);
            health = hitPoint;
            if (words[j + 5].equals("retreat")) {
                Map<String, Integer> cost = new HashMap<>();
                cost.put(words[j + 6], Integer.parseInt(words[j + 7]));
                while (!words[j + 8].equals("attacks") && j + 7 < words.length && !words[j + 7].isEmpty()) {
                    j += 2;
                    cost.put(words[j + 6], Integer.parseInt(words[j + 7]));
                }
                retreat = new Attack(null, cost);
            } else j -= 3;
            for (; i + j + 12 <= words.length && !words[i + j + 9].isEmpty(); i += 3) {
                try {

                    Map<String, Integer> costList = new HashMap<>();
                    costList.put(words[i + j + 9], Integer.parseInt(words[i + j + 10]));
                    while (!tryInt(words[i + j + 11]) && i + j + 10 < words.length && !words[i + j + 10].isEmpty()) {
                        j += 2;
                        costList.put(words[i + j + 9], Integer.parseInt(words[i + j + 10]));

                    }
                    this.attackList.add(new Attack(abilities.get(Integer.parseInt(words[i + j + 11]) - 1), costList));
                } catch (Exception e) {
                    e.getStackTrace();
                    alert(Alert.AlertType.ERROR, words[0]);
                }
            }
        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, words[0]);
        }

    }

    private boolean tryInt(String integerString) {
        try {
            Integer.parseInt(integerString);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    public int getHealth() {
        return health;
    }

    public int getDamage() {
        return damage;
    }

    public Card addDamage(int damage) {
        health -= damage;
        if (health < 0)
            health = 0;
        this.damage += damage;
        fireCardModified("addDamage");
        return this;
    }

    public void setHeal(int heal) {
        health += heal;
        if (health >= hitPoint)
            health = hitPoint;
        lastHeal = heal;
        totalHealed = totalHealed + heal;

        fireCardModified("setHeal");
    }

    public int getLastHeal() {
        return lastHeal;
    }

    public int getTotalHealed() {
        return totalHealed;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status)
    {
        fireCardModified("status");
        this.status = status;
    }

    public Map<String, Integer> getRequiredEnergy() {
        Map<String, Integer> outputList = new HashMap<>();
        for (Attack attack : attackList) {
            int found = 0;
            for (String attackCostType : attack.getCostList().keySet()) {
                for (String key : outputList.keySet())
                    if (attackCostType.equals(key)) {
                        outputList.put(key, outputList.get(key) + attack.getCostAmount(key));
                        found++;
                        break;
                    }
                if (found == outputList.size())
                    outputList.put(attackCostType, attack.getCostAmount(attackCostType));
            }
        }
        return outputList;

    }


    //region Event
    public void addListener(CardEventListener listener) {
        listenerList.add(CardEventListener.class, listener);
    }

    void fireCardModified(String propertyName) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == CardEventListener.class) {
                ((CardEventListener) listeners[i + 1]).cardModified(propertyName, this);
            }
        }
    }

    //endregion

    public void setPlayerName(Player playerName) {
        this.playerName = playerName;
    }

    public Attack getHeaviestAttack() {
        int max = 0;
        Attack heavyAttack = null;
        for (Attack attack : attackList) {
            int totalCost = 0;
            for (int costAmount : attack.getCostList().values())
                totalCost += costAmount;

            if (totalCost > max) {
                max = totalCost;
                heavyAttack = attack;
            }
        }
        return heavyAttack;

    }
}