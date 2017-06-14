package Model;

import Enums.*;
import Enums.Player;

import javax.swing.event.CellEditorListener;
import javax.swing.event.EventListenerList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ActionStatus status;

    private final EventListenerList listenerList;
    private int lastHeal;
    private int health;
    // Glameow 0 :pokemon 1:            basic 2        colorless 3:       60 4:           retreat 5:cat:colorless 6:2 7:attacks 8:cat:colorless 9:1 10:1 11,cat:colorless 12:2 13:2 14

    public PokemonCard(String name, CardCategory category, String level, String type, int hitPoint, Attack retreat, List<Attack> attacks) {
        status = ActionStatus.none;
        this.id = "";
        this.name = name;
        this.category = category;
        this.type = type;
        this.level = level;
        this.hitPoint = hitPoint;
        health = hitPoint;
        this.retreat = retreat;
        attackList = attacks;
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