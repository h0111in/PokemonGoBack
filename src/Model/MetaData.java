package Model;

import Enums.Area;
import Enums.CardCategory;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

import static Controller.Helper.alert;


/**
 * Created by H0111in on 05/22/2017.
 */
public class MetaData {

    private List<Ability> abilitys;
    private List<Card> cards;


    /**
     *
     */
    public MetaData() throws Exception {
        List<String> strAbilityList = readFile("./asset/abilities.txt");

        abilitys = new ArrayList<>();
        for (String line : strAbilityList) {
            String abilityName = "";
            String[] words = line.replace(",", ":").split(":");

            if (!words[0].isEmpty())//name
            {
                abilityName = words[0];
                List<Action> actionList = new ArrayList<>();

                for (int i = 1; i < words.length && !words[i].isEmpty(); ) {

                    AbstractMap.SimpleEntry<Integer, Action> pair = getAction(i, words);

                    if (pair.getKey() > 1) {
                        i = pair.getKey();
                        actionList.add(pair.getValue());
                    } else i++;
                }
                abilitys.add(new Ability(abilityName, actionList));
            }
        }
        cards = new ArrayList<>();

        //region Load Abilities


        //endregion

        //region Load cards
        List<String> strCardList = readFile("./asset/cards.txt");


        for (String str : strCardList) {
            String[] subCardStr = str.replace("cat:", "").replace(",", ":").split(":");
            if (subCardStr.length > 5)
                switch (subCardStr[1]) {
                    case "pokemon":
                        List<Attack> attacks = new ArrayList<>();
                        //Ability ability, String costType,int costAmount
                        int j = 0;
                        try {
                            int i = 0;
                            if (!subCardStr[2].equals("basic")) {
                                i++;
                                j++;
                            }
                            for (; i + 12 <= subCardStr.length && !subCardStr[i + 9].isEmpty(); i += 3) {
                                attacks.add(new Attack(abilitys.get(Integer.parseInt(subCardStr[i + 11])), subCardStr[i + 9], Integer.parseInt(subCardStr[i + 10])));
                            }

                        } catch (Exception e) {
                            e.getStackTrace();
                            alert(Alert.AlertType.INFORMATION, str);
                        }
                        //String name 0, CardCategory category1, String level2,String type3,  int hitPoint4, Attack retreat5, List<Attack> attacks6)
                        cards.add(new PokemonCard(subCardStr[0], CardCategory.valueOf(subCardStr[1]), subCardStr[j + 2], subCardStr[j + 3], Integer.parseInt(subCardStr[j + 4]),
                                new Attack(null, subCardStr[j + 6], Integer.parseInt(subCardStr[j + 7])), attacks));


                        break;
                    case "energy":
                        //Psychic:energy:cat:psychic
                        //String name, CardCategory category, String type
                        cards.add(new EnergyCard(subCardStr[0], CardCategory.valueOf(subCardStr[1]), subCardStr[2]));
                        break;
                    case "trainer":
                        //Switch:trainer:cat:item:71
                        //String name, CardCategory category, String type, Attack attack
                        cards.add(new TrainerCard(subCardStr[0], CardCategory.valueOf(subCardStr[1]), subCardStr[2], new Attack(abilitys.get(Integer.parseInt(subCardStr[3])), "", 0)));
                        break;
                }
        }
        //endregion


    }

    private AbstractMap.SimpleEntry<Integer, Action> getAction(int i, String[] words) throws Exception {
        String targetTag = "target";
        String condTag = "cond";
        String statusTag = "status";
        String countTag = "count";
        String actionName = words[i++].replace("(", "");
        switch (actionName) {
            case "dam"://dam:target:opponent-active:30,cond:flip
                //dam:target:opponent-active:10,cond:count(target:your-active:energy:psychic)>0 todo
            case "heal"://heal:target:your-active:20
                //Potion:heal:target:your:30 done
            case "deenergize"://deenergize:target:your-active:count[target:your-active:energy]
                if (words[i].equals(targetTag)) {
                    i++;//skip 'target'
                    ActionTarget actionTarget = toTarget(words[i++]);

                    //region Count
                    //20*count[target:your-bench]
                    //count[target:your-active:energy] todo
                    //count(target:opponent-active:energy)*10 todo
                    //count(target:your-active:damage)*10 todo
                    CardCategory powerCardCategory = CardCategory.none;
                    String powerCardType = "";
                    String powerFormula = "";
                    ActionTarget powerTarget = ActionTarget.none;
                    if (words[i].contains(countTag)) {
                        String[] countStr = words[i].split("count");
                        if (countStr.length > 1 && !words[i].startsWith("count"))
                            powerFormula = countStr[0];
                        i++;//skip 'count[target' or 'count(target'
                        powerTarget = toTarget(words[i++].replace("]|)", ""));
                        if (words.length < i) {
                            boolean isNeededEnergyType = !words[i].contains("]") && !words[i].contains(")");
                            if (isNeededEnergyType) {
                                powerCardCategory = CardCategory.valueOf(words[i++]
                                        .replace("]", "").replace(")", ""));

                                String[] subStr;
                                subStr = words[i].contains("]") ? words[i++].split("]") : words[i++].split("\\)");

                                if (subStr.length != 2)
                                    throw new Exception("wrong format.");
                                if (isNeededEnergyType)
                                    powerCardType = subStr[0];
                                else
                                    powerCardCategory = CardCategory.valueOf(subStr[0]);
                                if (powerFormula.length() == 0)
                                    powerFormula = subStr[1];
                            }
                        }
                    } else
                        powerFormula = words[i++];
//endregion

                    //region Condition

                    //cond:count(target:your-active:energy:psychic)>0 todo
                    //cond:healed:target:your-active todo
                    //cond:flip
                    String conditionName = "";
                    String conditionCardType = "";
                    ActionTarget conditionTarget = ActionTarget.none;
                    CardCategory conditionCardCategory = CardCategory.none;
                    String conditionFormula = "";
                    if (words.length > i + 2 && words[i].equals(condTag)) {//+2=>cond + value of condition
                        i++;//skip cond

                        if (words[i].equals("flip")) {
                            conditionName = words[i++];
                        } else if (words[i].contains("count")) {
                            //target:your-active:energy:psychic
                            i++;//skip 'count(target'
                            conditionTarget = toTarget(words[i++]);
                            boolean isNeededEnergyType = !words[i].contains("]") && !words[i].contains(")");

                            if (isNeededEnergyType) {
                                conditionCardCategory = CardCategory.valueOf(words[i++]
                                        .replace("]", "").replace(")", ""));

                                String[] subStr;
                                subStr = words[i].contains("]") ? words[i++].split("]") : words[i++].split("\\)");

                                if (subStr.length != 2)
                                    throw new Exception("wrong format.");
                                if (isNeededEnergyType)
                                    conditionCardType = subStr[0];
                                else
                                    conditionCardCategory = CardCategory.valueOf(subStr[0]);
                                conditionFormula = subStr[1];
                            }
                        } else if (words[i].equals("healed")) {
                            i++;
                            conditionTarget = toTarget(words[i++]);
                        } else
                            throw new Exception("wrong format in ability condition");
                    }
//endregion

                    return new AbstractMap.SimpleEntry<Integer, Action>(i, new Action(actionName, new Condition(conditionName, conditionTarget,
                            new Counter(conditionTarget, conditionCardCategory, conditionCardType, conditionFormula), conditionFormula), actionTarget,
                            new Counter(powerTarget, powerCardCategory, powerCardType, powerFormula)));
                }
            case "destat"://destat:target:last todo
                i++;
                ActionTarget target = toTarget(words[i++]);
                return new AbstractMap.SimpleEntry<Integer, Action>(i, new Action(actionName, target, ActionStatus.none));
            case "applystat"://applystat:status:paralyzed:opponent-active
                if (words[i].equals(statusTag)) {
                    i++;
                    ActionStatus status = toStatus(words[i++]
                            .replace("(", "").replace(")", ""));
                    target = toTarget(words[i++].replace("(", "").replace(")", ""));
                    return new AbstractMap.SimpleEntry<Integer, Action>(i, new Action(actionName, target, status));
                }
                break;
            case "shuffle":
                if (words[i].equals(targetTag)) {
                    i++;
                    target = toTarget(words[i++]);
                    return new AbstractMap.SimpleEntry<Integer, Action>(i, new Action(actionName, target, null));
                }
            case "redamage"://redamage:target:opponent:target:opponent:count(target:last:source:damage) todo
                i++;//skip target
                target = toTarget(words[i++]);
                i++;//skip 'target'
                ActionTarget secondTarget = toTarget(words[i++]);
                i += 4;//skip 'count(target','last','source','damage)
                return new AbstractMap.SimpleEntry<Integer, Action>(i, new Action(actionName, target, null));

            case "draw"://draw:3 todo
                String amount = words[i++];
                return new AbstractMap.SimpleEntry<Integer, Action>(i, new Action(actionName, null, null, new Counter(amount)));
            case "swap"://swap:your-active:your:choice:bench
                return new AbstractMap.SimpleEntry<Integer, Action>(i, new Action(actionName, null, ActionTarget.yourActive, null));
            case "add":
                //Floral Crown:add:target:your:trigger:opponent:turn-end:(heal:target:self:20) todo
                //At the end of your opponent's turn, heal 20 damage from the Basic Pokémon this card is attached to. todo
                i++;//skip 'target'
                target = toTarget(words[i++]);
                i += 6;
                String powerAmount = words[i++];
                return new AbstractMap.SimpleEntry<Integer, Action>(i, new Action(actionName, null, target
                        , new Counter(powerAmount)));
            case "cond":
                //cond:flip:(applystat:status:asleep:opponent-active,applystat:status:poisoned:opponent-active) todo
                //cond:flip:dam:target:opponent-active:20 todo
                //cond:ability:deck:destination:discard:target:choice:you:1:(search:target:you:source:deck:filter:top:8:1,shuffle:target:you) todo
                //cond:flip:dam:target:opponent-active:30:else:dam:target:your-active:30 todo
                //cond:ability:deenergize:target:your-active:1:(search:target:you:source:discard:filter:cat:item:1) todo
                String conditionName = words[i++];
                if (words[i].equals("ability")) i++;//skip 'ability'
                AbstractMap.SimpleEntry<Integer, Action> pair = getAction(i, words);
                AbstractMap.SimpleEntry<Integer, Action> secondPair = new AbstractMap.SimpleEntry<>(0, null);
                if (words.length > pair.getKey() + 1) {
                    if (words[pair.getKey()].equals("else")) {
                        i++;//skip 'else'
                    }
                    secondPair = getAction(pair.getKey(), words);
                }
                return new AbstractMap.SimpleEntry<>(i, new Action(actionName,
                        new Condition(conditionName, ActionTarget.none, null, ""), pair.getValue(), secondPair.getValue()));
            case "deck":
                //deck:target:them:destination:deck:bottom:choice:target:1 todo
                //deck:destination:deck:count(your:hand),shuffle:target:you,draw:5 todo
                //Red Card:deck:target:opponent:destination:deck:count(opponent:hand),shuffle:target:you,draw:opponent:4
                while (words[i].equals("target") || words[i].equals("them") || words[i].equals("destination"))
                    i++;//skip ...
                return new AbstractMap.SimpleEntry<Integer, Action>(i, new Action(actionName, ActionTarget.opponentActive, null));

            case "search":
                //search:target:opponent:source:deck:filter:top:1:0,cond:choice:shuffle:target:opponent todo
                //Look at the top card of your opponent's deck. Then, you may have your opponent shuffle his or her deck.
                //search:target:you:source:deck:filter:type:energy:4 todo
                //Search your deck for up to 4 Lightning Energy cards, reveal them, and put them into your hand. Shuffle your deck afterward.
                //search:target:opponent:source:deck:filter:top:1:0,cond:choice:shuffle:target:opponent todo
                //search:target:you:source:deck:filter:pokemon:cat:basic:2,shuffle:target:you
                //Search your deck for up to 2 Basic Pokémon, reveal them, and put them into your hand. Shuffle your deck afterward.
                //Wally:search:target:your:choice:filter:pokemon:cat:basic:source:deck:filter:evolves-from:target:last:1,shuffle:target:you
                i++;//skip 'target'
                target = toTarget(words[i++]);
                Area source = Area.none;
                if (words[i].equals("source")) {
                    i++;//skip 'source'
                    source = Enums.Area.valueOf(words[i++]);
                } else if (words[i].equals("choice"))
                    i++;

                //filter:type:energy:4
                //filter:top:1:0
                //filter:pokemon:cat:basic:2
                //filter:pokemon:cat:basic:source:deck:
                //filter:evolves-from:target:last:1,
                String filterType = "";
                if (words.length > i + 1 && words[i].equals("filter")) {
                    i++;//skip 'filter'
                    switch (words[i]) {
                        case "cat":
                            i++;
                        case "type":
                        case "top":
                        case "pokemon":
                        case "evolves-from":
                            filterType = words[i++];
                            break;
                        default:
                            throw new Exception("wrong filter type." + words[i]);
                    }
                }
                return new AbstractMap.SimpleEntry<>(i, new Action(actionName, target, source, filterType));

        }
        return new AbstractMap.SimpleEntry<>(-1, null);
    }


    private static ActionStatus toStatus(String statusName) {

        switch (statusName) {
            case "paralyzed":
                return ActionStatus.paralyzed;
            case "stuck":
                return ActionStatus.stuck;
            case "poisoned":
                return ActionStatus.poisoned;
            case "asleep":
                return ActionStatus.asleep;
        }
        return ActionStatus.none;
    }

    private static ActionTarget toTarget(String targetName) {
        ActionTarget target = ActionTarget.none;
        switch (targetName) {
            case "opponent-active":
            case "opponent":
                return ActionTarget.opponentActive;
            case "your-active":
            case "your":
                return ActionTarget.yourActive;
            case "your-bench":
                return ActionTarget.yourBench;
            case "last":
                return ActionTarget.last;
        }
        return target;
    }

    public static List<String> readFile(String filename) {
        List<String> records = new ArrayList<>();

        if (!new File(filename).exists() || new File(filename).isDirectory())
            alert(Alert.AlertType.WARNING, "Please make sure the following file is exist then press OK\n\r" +
                    filename.replace("./", System.getProperty("user.dir").replace("\\", "/") + "/"));
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
            reader.close();
            return records;
        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, "System crashs while reading " + filename);
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
            return null;
        }
    }

    public Ability getAbility(int index) {
        return abilitys.get(index);
    }

    public Card getCard(int index) throws CloneNotSupportedException {
        return cards.get(index).clone();
    }

}
