package Model.Abilities;

import Enums.ActionTarget;
import Enums.Area;
import Enums.CardCategory;
import Model.Card;
import Model.Player;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hosein on 6/8/2017.
 */
public class Counter extends BaseAction implements IActionStrategy {

    private CardCategory category;
    private String type;
    private String formula;
    private boolean isUserChoice;
    private String source;
    private String sourceType;


    public Counter() {
        this.formula = "";
        this.target = ActionTarget.none;
        this.category = CardCategory.none;
        this.type = "";
    }

    public int getCount(Player player, Player opponent) throws ScriptException {
        try {
            return Integer.valueOf(eval(player, opponent));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public int getTotalEffectivePower() throws ScriptException {
        String leftOperand = "";
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        return Integer.parseInt(String.valueOf(engine.eval(leftOperand + formula)));
    }

    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        try {
            return Boolean.valueOf(eval(player, opponent));
        } catch (Exception e) {
            return false;
        }
    }

    private String eval(Player player, Player opponent) throws Exception {
        String operand = "";
        if (category == CardCategory.none && target == ActionTarget.none) {
            operand = "";
        } else {
            switch (target) {

                case opponentActive:
                    for (Card card : opponent.getAreaCard(Area.active)) {
                        if (category == CardCategory.none ||
                                card.getCategory() == category)
                            if (type.isEmpty()
                                    || type.equals(card.getType()))
                                operand = String.valueOf(Integer.parseInt(operand) + 1);
                    }
                    break;
                case yourActive:
                    for (Card card : player.getAreaCard(Area.active)) {
                        if (category == CardCategory.none
                                || card.getCategory() == category)
                            if (type.isEmpty()
                                    || type.equals(card.getType()))
                                operand = String.valueOf(Integer.parseInt(operand) + 1);
                    }
                    break;
                case yourBench:
                    for (Card card : player.getAreaCard(Area.bench)) {
                        if (category == CardCategory.none ||
                                card.getCategory() == category)
                            if (type.isEmpty()
                                    || type.equals(card.getType()))
                                operand = String.valueOf(Integer.parseInt(operand) + 1);
                    }
                    break;
                case opponentBench:
                    for (Card card : opponent.getAreaCard(Area.bench)) {
                        if (category == CardCategory.none ||
                                card.getCategory() == category)
                            if (type.isEmpty()
                                    || type.equals(card.getType()))
                                operand = String.valueOf(Integer.parseInt(operand) + 1);
                    }
                    break;
                case none:
                    break;
                case last:
                    break;
                case yourHand:
                    for (Card card : player.getAreaCard(Area.hand)) {
                        if (category == CardCategory.none ||
                                card.getCategory() == category)
                            if (type.isEmpty()
                                    || type.equals(card.getType()))
                                operand = String.valueOf(Integer.parseInt(operand) + 1);
                    }
                    break;

                case opponentHand:
                    for (Card card : opponent.getAreaCard(Area.hand)) {
                        if (category == CardCategory.none ||
                                card.getCategory() == category)
                            if (type.isEmpty()
                                    || type.equals(card.getType()))
                                operand = String.valueOf(Integer.parseInt(operand) + 1);
                    }
                    break;
            }
        }

        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        return String.valueOf(engine.eval(operand + formula));
    }

    @Override
    public String[] parse(String[] words) throws Exception {
        //count(target:your-active:energy:psychic)>0
        //count(target:your-active:damage)*10
        //count(target:opponent-active:energy)*10
        //count(target:last:source:damage)
        //count(your-hand)
        //20*count(target:your-bench)
        //count(target:your-active:energy)
        //100

        int i = 0;
        String[] words2 = new String[0];
        if (words[i].contains(countTag)) {
            name = countTag;
            int j = 0;
            if (words[i].contains("(")) {
                j = i;
                while (!words[j++].contains(")")) ;
                List<String> temp = new ArrayList<>();
                for (int i1 = i; i1 < j; i1++) {
                    String word = words[i1];
                    word = word.replace(")", "(");
                    if (word.contains("(")) {
                        for (String w : word.replace(countTag, "").split("\\("))
                            if (w.length() > 0)
                                temp.add(w.replace(countTag, ""));
                    } else temp.add(word.replace(countTag, ""));
                }
                if (temp.size()-1>0 &&temp.size()<words.length)
                    words2 = Arrays.copyOfRange(words, temp.size()-1, words.length);
                words = temp.toArray(new String[0]);

            }
            ////////////////////////////////////////////////////////////////////////
            if (words[i].matches("\\d.*"))
                formula = words[i++];

            if (words[i].endsWith(targetTag))
                i++;//skip 'count[target' or 'count(target'

            target = toTarget(words[i++]);
            if (i < words.length) {
                if (words[i].equals("source")) {
                    i++;//skip 'source'
                }
            }

            if (i < words.length) {
                if (words[i].matches("\\d.*"))
                    formula = words[i++];
                else {
                    source = words[i++];
                    if (i < words.length) {
                        if (words[i].matches(".*\\d"))
                            formula = words[i++];
                        else {
                            sourceType = words[i++];
                            if (words[i].matches(".*\\d"))
                                formula = words[i++];

                        }
                    }
                }
            }
        } else
            formula = words[i++];

        if (i < words.length) {
            List<String> output = new ArrayList<>();
            for (String word : Arrays.copyOfRange(words, i, words.length)) {
                output.add(word);
            }
            for (String word : words2) {
                output.add(word);
            }

            return output.toArray(new String[0]);
        } else {

            return words2;
        }
    }


}
