package Model.Abilities;

import Enums.Area;
import Model.Card;
import Model.EnergyCard;
import Model.Player;
import Model.PokemonCard;
import javafx.scene.control.Alert;

import javax.script.ScriptException;
import java.util.*;

import static Controller.Main.logger;

/**
 * Created by hosein on 2017-06-22.
 */
public class Search extends BaseAction implements IActionStrategy {
    private int amount;
    private String filterValue;
    private Area source;
    private String filterType;
    private String sourceFilterType;
    private String sourceFilterValue;

    public Search() {
        super();
        sourceFilterType = "";
        sourceFilterValue = "";
        filterType = "";
        filterValue = "";
    }

    @Override
    public int getTotalEffectivePower() throws ScriptException {
        return 0;
    }

    @Override
    public final boolean fight(Player player, Player opponent) throws Exception {
        //search:target:opponent:source:deck:filter:top:1:0,cond:choice:shuffle:target:opponent
        //Look at the top card of your opponent's deck. Then, you may have your opponent shuffle his or her deck.
        //search:target:you:source:deck:filter:type:energy:4
        //Search your deck for up to 4 Lightning Energy cards, reveal them, and put them into your hand. Shuffle your deck afterward.
        //search:target:opponent:source:deck:filter:top:1:0,cond:choice:shuffle:target:opponent
        //search:target:you:source:deck:filter:pokemon:cat:basic:2,shuffle:target:you
        //Search your deck for up to 2 Basic Pokémon, reveal them, and put them into your hand. Shuffle your deck afterward.
        //Wally:search:target:your:choice:filter:pokemon:cat:basic:source:deck:filter:evolves-from:target:last:1,shuffle:target:you

        logger.info("listener list size: " + listenerList.getListenerCount());
        Player targetPlayer = getTargetPlayer(player, opponent);
        String selectedId = "";
        String basicCardType = "";
        if (!sourceFilterType.isEmpty()) {
            List<Card> holderList = targetPlayer.getAreaCard(Area.bench);
            if (targetPlayer.getActiveCard().getTopCard() != null)
                holderList.add(targetPlayer.getActiveCard().getTopCard());

            List<Card> sourceList = getFilteredList(sourceFilterType, sourceFilterValue, Integer.MAX_VALUE, holderList, "");
            logger.info("sourceList.size:" + sourceList.size());
            if (sourceList.size() > 0) {
                if (isUserChoice && !player.isComputer()) {
                    List<String> selectedCards = fireSelectCardRequest("Select a card to search deck for stage-1", 1, sourceList, true);
                    if (selectedCards.size() > 0) {
                        basicCardType = targetPlayer.getCard(selectedCards.get(0)).getName();
                        selectedId = targetPlayer.getCard(selectedCards.get(0)).getId();
                    }
                } else {
                    basicCardType = sourceList.get(0).getName();
                    selectedId = sourceList.get(0).getId();
                }

            } else {
                fireShowMessage(Alert.AlertType.INFORMATION, "Search: Sorry, there is no potential card in your hand!", 2);
                return true;
            }
        }
        List<Card> areaCard = targetPlayer.getAreaCard(source);
        List<Card> filteredList = getFilteredList(filterType, filterValue, amount, areaCard, basicCardType);
        if (filterType.equals("evolves-from")) {
            logger.info("evolves-from basic: " + selectedId + " to: " + (filteredList.size() > 0 ? filteredList.get(0).getId() : "!"));
            if (filteredList.size() > 0)
                targetPlayer.addCard(targetPlayer.popCard(filteredList.get(0).getId(), "")
                        , targetPlayer.getCardArea(selectedId), selectedId);
        } else
            for (int i = 0; i < amount; i++) {
                Card card = targetPlayer.popCard(filteredList.get(i).getId(), "");
                targetPlayer.addCard(card, Area.hand, "");
                logger.info("evolves-from basic: " + selectedId + " to: " + (filteredList.size() > 0 ? filteredList.get(i).getId() : "!"));

            }


        return true;

    }

    private static List<Card> getFilteredList(String filterType, String filterValue, int amount, List<Card> sourceList, String basicCardType) {
        List<Card> filteredList = new ArrayList<>();
        for (int i = 0; i < sourceList.size(); i++) {
            Card card = sourceList.get(i);
            switch (filterType) {
                case "cat":
                    if (card.getType().equals(filterValue))
                        filteredList.add(card);
                    if (filteredList.size() == amount)
                        return filteredList;
                    break;
                case "energy":
                    if (card instanceof EnergyCard)
                        filteredList.add(card);
                    if (filteredList.size() == amount)
                        return filteredList;
                    break;
                case "top":
                    filteredList.add(card);
                    if (filteredList.size() == Integer.valueOf(filterValue))
                        return filteredList;
                    break;
                case "pokemon":
                    if (card instanceof PokemonCard && ((PokemonCard) card).getLevel().equals(filterValue)) {
                        if (!basicCardType.isEmpty()) {
                            if (card instanceof PokemonCard)
                                if (((PokemonCard) card).getLevel().equals(basicCardType)) {
                                    filteredList.add(card);
                                }
                        } else
                            filteredList.add(card);
                    }
                    if (filteredList.size() == amount)
                        return filteredList;
                    break;
                case "evolves-from":
                    if (!basicCardType.isEmpty())
                        if (card instanceof PokemonCard)
                            if (((PokemonCard) card).getLevel().equals(basicCardType)) {
                                filteredList.add(card);
                            }
                    if (filteredList.size() == amount)
                        return filteredList;
                    break;
            }
        }
        return filteredList;
    }

    @Override
    public String[] parse(String[] words) throws Exception {

        //search:target:opponent:source:deck:filter:top:1:0,cond:choice:shuffle:target:opponent todo
        //Look at the top card of your opponent's deck. Then, you may have your opponent shuffle his or her deck.
        //search:target:you:source:deck:filter:type:energy:4 todo
        //Search your deck for up to 4 Lightning Energy cards, reveal them, and put them into your hand. Shuffle your deck afterward.
        //search:target:opponent:source:deck:filter:top:1:0,cond:choice:shuffle:target:opponent todo
        //search:target:you:source:deck:filter:pokemon:cat:basic:2,shuffle:target:you
        //Search your deck for up to 2 Basic Pokémon, reveal them, and put them into your hand. Shuffle your deck afterward.

        //Wally:search:target:your:choice:filter:pokemon:cat:basic:source:deck:filter:evolves-from:target:last:1,shuffle:target:you
        //Search your deck for a card that evolves from 1 of your Pokémon (excluding Pokémon-EX) and put it onto that Pokémon.
        //(This counts as evolving that Pokémon.)
        //Shuffle your deck afterward. You can use this card during your first turn or on a Pokémon that was put into play this turn.

        //Scavenge:cond:ability:deenergize:target:your-active:1:(search:target:your:source:discard:filter:cat:item:1)
        //Discard a Psychic Energy attached to this Pokémon. If you do, put an Item card from your discard pile into your hand.
        //search:target:your:source:deck:1
        //search:target:choice:your-pokemon:cat:basic:source:deck:filter:evolves-from:target:last:1,shuffle:target:you

        int i = 0;
        name = words[i++];
        i++;//skip 'target'
        if (words[i].equals("choice")) {
            isUserChoice = true;
            i++;
            if (words[i].contains("-")) {
                target = toTarget(words[i].split("-")[0]);
                sourceFilterType = words[i].split("-")[1];
                i++;
            }
            if (words[i].equals("cat")) {
                i++;
                sourceFilterValue = words[i++];
            }

        } else
            target = toTarget(words[i++]);

        source = Area.none;

        if (words[i].equals("choice")) {
            i++;
            isUserChoice = true;
            sourceFilterType = "";
            if (words.length > i + 1 && words[i].equals("filter")) {
                i++;//skip 'filter'
                sourceFilterType = words[i++];
                switch (words[i - 1]) {
                    case "cat":
                        sourceFilterValue = words[i++];
                        break;
                    case "energy":
                        break;
                    case "top":
                        sourceFilterValue = words[i++];
                        break;
                    case "pokemon":
                        if (words[i].equals("cat")) {
                            i++;//skip 'cat'
                            sourceFilterValue = words[i++];
                        }
                        break;
                    case "evolves-from":
                        i++;//skip 'target'
                        sourceFilterValue = words[i++];
                        break;
                    default:
                        throw new Exception("wrong filter type." + words[i]);
                }
            }
        }
        if (words[i].equals("source")) {
            i++;//skip 'source'
            source = Enums.Area.valueOf(words[i++]);
        }

        filterType = "";
        if (words.length > i + 1 && words[i].equals("filter")) {
            i++;//skip 'filter'
            filterType = words[i++];
            switch (words[i - 1]) {
                case "cat":
                    filterValue = words[i++];
                    break;
                case "energy":
                    break;
                case "top":
                    filterValue = words[i++];
                    break;
                case "pokemon":
                    if (words[i].equals("cat")) {
                        i++;//skip 'cat'
                        filterValue = words[i++];
                    }
                    break;
                case "evolves-from":
                    i++;//skip 'target'
                    filterValue = words[i++];
                    break;
                default:
                    throw new Exception("wrong filter type." + words[i]);
            }
            amount = Integer.parseInt(words[i++].replace(")", "").replace("(", ""));
        } else if (words[i].matches("\\d"))
            amount = Integer.parseInt(words[i++].replace(")", "").replace("(", ""));

        if (i < words.length)
            return Arrays.copyOfRange(words, i, words.length);
        else return new String[0];

    }

}
