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
    private ActionStatus status;

    private final EventListenerList listenerList;
    private int lastHeal;
    private int health;
    // Glameow 0 :pokemon 1:            basic 2        colorless 3:       60 4:           retreat 5:cat:colorless 6:2 7:attacks 8:cat:colorless 9:1 10:1 11,cat:colorless 12:2 13:2 14

    public PokemonCard() {
        status = ActionStatus.none;
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
        List<Attack> attacks = new ArrayList<>();
        //Ability ability, String costType,int costAmount
        int j = 0;
        try {
            int i = 0;
            if (!words[2].equals("basic")) {
                i++;
                j++;
            }
            for (; i + 12 <= words.length && !words[i + 9].isEmpty(); i += 3) {
                attacks.add(new Attack(abilities.get(Integer.parseInt(words[i + 11]) - 1), words[i + 9], Integer.parseInt(words[i + 10])));
            }

        } catch (Exception e) {
            e.getStackTrace();
            alert(Alert.AlertType.INFORMATION, words[0]);
        }
        name = words[0];
        category = CardCategory.valueOf(words[1]);
        level = words[j + 2];
        type = words[j + 3];
        hitPoint = Integer.parseInt(words[j + 4]);
        retreat = new Attack(null, words[j + 6], Integer.parseInt(words[j + 7]));
        this.attackList = attacks;


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

    public ActionStatus getStatus() {
        return status;
    }

    public void setStatus(ActionStatus status) {
        this.status = status;
    }

    public Map<String, Integer> getRequiredEnergy() {
        Map<String, Integer> outputList = new HashMap<>();
        for (Attack attack : attackList) {
            String costType = attack.getCostType();
            boolean found = false;
            for (String key : outputList.keySet()) {
                if (costType.equals(key)) {
                    outputList.put(key, outputList.get(key) + attack.getCostAmount());
                    found = true;
                    break;
                }
            }
            if (!found)
                outputList.put(attack.getCostType(), attack.getCostAmount());
        }
        return outputList;

    }

    public int getTotalRequiredEnergy() {
        int totalRequiredEnergy = 0;
        for (Attack attack : getAttackList())
            totalRequiredEnergy += attack.getCostAmount();
        return totalRequiredEnergy;
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
}