package Model.Abilities;

import Enums.Area;
import Model.Player;
import javafx.scene.control.Alert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hosein on 2017-06-21.
 */
public class Swap extends BaseAction implements IActionStrategy {

    private boolean isUserChoice;
    private Area choiceArea;

    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        if (player.getAreaCard(choiceArea).size() > 0) {

            String cardId = "";
            if (player.isComputer()) {
                cardId = player.getRandomCardId(choiceArea);
            } else {
                List<String> selectedCard = new ArrayList<>();
                if (isUserChoice)
                    selectedCard = fireSelectCardRequest("Select a Card to SWAP with active card",
                            1, player.getAreaCard(choiceArea), true);
                else {
                    cardId = player.getRandomCardId(choiceArea);
                }
                if (selectedCard.size() > 0)
                    cardId = selectedCard.get(0);
            }
            player.swapCardHolder(player.getActiveCard(), player.getCardHolder(cardId),
                    Area.active, choiceArea);
        } else
            fireShowMessage(Alert.AlertType.CONFIRMATION, "Player " + player.getName().name() + "has no card in the bench", 1);
        return true;
    }

    @Override
    public String[] parse(String[] words) throws Exception {
        //swap:your-active:your:choice:bench
        //Switch:swap:source:your-active:destination:choice:your-bench
        int i = 0;
        name = words[i++].replace("(", "");
        if (words[i].equals("source"))
            i++;
        if (words[i].equals(targetTag))
            i++;
        target = toTarget(words[i++]);
        if (words[i].equals("destination"))
            i++;
        if (words[i].equals(targetTag))
            i++;
        if (words[i].equals("your"))
            i++;
        if (words[i].equals("choice")) {
            i++;
            isUserChoice = true;
        }
        choiceArea = toArea(words[i++]);

        if (i < words.length)
            return Arrays.copyOfRange(words, i, words.length);
        else return new String[0];
    }
}
