package Model.Abilities;

import Enums.Area;
import Model.Card;
import Model.Player;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hosein on 2017-06-22.
 */
public class Deck extends BaseAction implements IActionStrategy {
    private Area destination;
    private Direction drawDirection;

    private boolean isUserChoice;
    private Area choiceArea;


    @Override
    public int getTotalEffectivePower() throws ScriptException {
        return 0;
    }

    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        Player targetPlayer = getTargetPlayer(player, opponent);
        List<Card> cardList = new ArrayList<>();
        if (isUserChoice && !player.isComputer()) {
            List<String> cardIDList = fireSelectCardRequest("Please Select " + power.getCount(player, opponent) + " Card(s).", power.getCount(player, opponent),
                    targetPlayer.getAreaCard(destination), true);
            for (String id : cardIDList)
                cardList.add(targetPlayer.popCard(id, destination, -1, ""));
        } else {
            cardList = targetPlayer.drawCard(power.getCount(player, opponent), Area.hand);
        }

        targetPlayer.addCard(cardList, destination, drawDirection == Direction.bottom);
        return true;
    }

    @Override
    public String[] parse(String[] words) throws Exception {
        //deck:target:your:destination:deck:count(your-hand),shuffle:target:you,draw:5
//        Shuffle your hand into your deck. Then, draw 5 cards.
        //deck:target:opponent:destination:deck:bottom:choice:them:target:1
        //Your opponent puts a card from his or her hand on the bottom of his or her deck.
        //deck:target:opponent:destination:deck:count(opponent:hand),shuffle:target:you,draw:opponent:4
        //deck:target:your:destination:discard:choice:you:1:(search:target:your:source:deck:filter:top:8:1,shuffle:target:your)
        //Discard a card from your hand

        int i = 0;
        name = words[i++];
        if (words[i].equals(targetTag)) {
            i++;
            target = toTarget(words[i++]);
        }
        if (words[i].equals("destination")) {
            i++;
            destination = Area.valueOf(words[i++]);
        }
        if (words[i].contains(countTag)) {
            power = new Counter();
            words = power.parse(Arrays.copyOfRange(words, i, words.length));
            i = 0;
        } else {
            try {
                drawDirection = Direction.valueOf(words[i]);
                i++;
            } catch (Exception e) {
                drawDirection = Direction.none;
            }

            if (words[i].equals("choice")) {
                i++;
                isUserChoice = true;
                if (words[i].equals("you") || words[i].equals("them")) {
                    i++;
                }
            }
            if (words[i].equals(targetTag))
                i++;

            power = new Counter();
            words = power.parse(Arrays.copyOfRange(words, i, words.length));
            i = 0;
        }

        if (i < words.length)
            return Arrays.copyOfRange(words, i, words.length);
        else return new String[0];
    }
}
