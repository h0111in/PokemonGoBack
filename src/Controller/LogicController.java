package Controller;

import Enums.*;
import Listeners.LogicEventListener;
import Listeners.PlayerEventListener;
import Listeners.uiBoardEventListener;
import Listeners.uiCardEventListener;
import Model.Abilities.Ability;
import Model.Abilities.Add;
import Model.Abilities.IActionStrategy;
import Model.*;
import Model.Player;
import javafx.scene.control.Alert;

import javax.swing.event.EventListenerList;
import java.net.URISyntaxException;
import java.util.*;

import static Controller.Main.logger;

/**
 * Created by H0111in on 05/27/2017.
 */
public class LogicController {

    //region fields


    Map<Enums.Player, Player> players;
    private Enums.Player activePlayer;
    private Map<TurnAction, Integer> turnActions;
    private final EventListenerList listenerList;
    private Map<Enums.Player, IActionStrategy> actionrequestedList;
    private boolean gameFinished;
    private int turnCounter = 0;
    private boolean retreatRestricted;
    private boolean attackRetreatRestricted;

    //endregion

    //region constructor
    public LogicController(Map<Enums.Player, Boolean> playerNames) {

        listenerList = new EventListenerList();
        actionrequestedList = new HashMap<>();
        //Define Players
        players = new HashMap<>();
        for (Enums.Player playerName : playerNames.keySet()) {
            Player player = new Player(playerName, playerNames.get(playerName));
            players.put(playerName, player);
            actionrequestedList.put(playerName, null);
        }

        //Define TurnActions
        turnActions = new HashMap<>();
        for (TurnAction action : Enums.TurnAction.values())
            turnActions.put(action, 0);
    }
    //endregion

    //region Methods

    public void startGame() throws Exception {
        firstTurn = true;
//        fireShowMessage("3", 1);
//        fireShowMessage("2", 1);
//        fireShowMessage("1", 1);
//        fireShowMessage("Welcome!", 1);


        //HAND AREA
//        for (Card card :players.get(Enums.Player.A).getAreaCard(Area.deck))
//            if(card.getTargetCategory()==CardCategory.energy)
//            logger.info(card.getName()+card.getTargetCategory());
//        String cardList="";
//        for (Card card : players.get(Enums.Player.A).getAreaCard(Area.deck))
//            cardList+="\r\n"+card.getName();
//            logger.info(cardList);
//        for (Card card : players.get(Enums.Player.B).getAreaCard(Area.deck))
//            cardList+="\r\n"+card.getName();
//        logger.info(cardList);

        players.get(Enums.Player.A).addCard(players.get(Enums.Player.A).drawCard(7, Area.deck), Area.hand, -1, "");
        players.get(Enums.Player.B).addCard(players.get(Enums.Player.B).drawCard(7, Area.deck), Area.hand, -1, "");

        //PRIZE AREA
        players.get(Enums.Player.A).addCard(players.get(Enums.Player.A).drawCard(6, Area.deck), Area.prize, -1, "");
        players.get(Enums.Player.B).addCard(players.get(Enums.Player.B).drawCard(6, Area.deck), Area.prize, -1, "");

        //Check Mulligan in hands
        int totalMulliganA = checkMulligan(Enums.Player.A);
        int totalMulliganB = checkMulligan(Enums.Player.B);
        logger.info("A " + totalMulliganA);
        logger.info("B " + totalMulliganB);

        if (totalMulliganA > totalMulliganB) {
            fireShowMessage(Alert.AlertType.INFORMATION, "Opponent receives " + (totalMulliganA - totalMulliganB) + " cards due to Mulligan", 1.5);
            players.get(Enums.Player.B).addCard(players.get(Enums.Player.B).drawCard(totalMulliganA - totalMulliganB, Area.deck), Area.hand, -1, "");
        } else if (totalMulliganB > totalMulliganA) {
            fireShowMessage(Alert.AlertType.INFORMATION, "User receives " + (totalMulliganB - totalMulliganA) +
                    " cards due to Mulligan", 1.5);
            players.get(Enums.Player.A).addCard(players.get(Enums.Player.A).drawCard(totalMulliganA - totalMulliganB, Area.deck)
                    , Area.hand, -1, "");

        }

        //DEFINE FIRST PLAYER
        fireShowMessage(Alert.AlertType.INFORMATION, "Choice a face!", 1);
        activePlayer = !fireFlipCoin(Coin.None, -1) ?
                Enums.Player.B :
                Enums.Player.A;

        startTurn(false);
    }

    private int checkMulligan(Enums.Player playerName) throws Exception {
        Player player = players.get(playerName);
        boolean hasPokemon = false;
        int total = 0;
        while (!hasPokemon) {
            for (Card card : player.getAreaCard(Area.hand)) {
                if (card instanceof PokemonCard) {
                    hasPokemon = true;
                    break;
                }
            }
            if (!hasPokemon) {
                fireShowMessage(Alert.AlertType.INFORMATION, "Player " + player.getName() + "doesn't have pokemon", 1);
                fireShowMessage(Alert.AlertType.INFORMATION, "Exchanging a card...", 1);
                player.addCard(player.popAllCard(Area.hand, -1, ""), Area.deck, -1, "");
                player.addCard(Player.shuffle(player.popAllCard(Area.deck, -1, "")), Area.deck, -1, "");
                player.addCard(player.drawCard(7, Area.deck), Area.hand, -1, "");
                total++;
            }
        }
        return total;
    }

    private void startTurn(boolean addCard) throws Exception {
        attackRetreatRestricted = false;
        retreatRestricted = false;
        turnCounter++;
        firstTurn = turnCounter == 1;
        if (gameFinished)
            return;


        fireShowMessage(Alert.AlertType.INFORMATION, (activePlayer == Enums.Player.A ? "User" : "Opponent") + "'s turn...", 1);
        for (
                Enums.Player player : players.keySet())
            if (actionrequestedList.get(player) != null)

            {
                if (actionrequestedList.get(player) instanceof Add) {
                    if (((((Add) actionrequestedList.get(player)).getTriggerTime() == TriggerTime.your_turn_start ||
                            ((Add) actionrequestedList.get(player)).getTriggerTime() == TriggerTime.opponent_turn_end) &&
                            player == activePlayer) ||
                            ((((Add) actionrequestedList.get(player)).getTriggerTime() == TriggerTime.your_turn_end ||
                                    ((Add) actionrequestedList.get(player)).getTriggerTime() == TriggerTime.opponent_turn_start) &&
                                    player != activePlayer))
                        actionrequestedList.get(player).fight(players.get(player), players.get(getOpponent(player)));
                }
                actionrequestedList.put(player, null);
            }

        if (addCard)
            players.get(activePlayer).addCard(players.get(activePlayer).drawCard(1, Area.deck), Area.hand, -1, "");
        for (TurnAction action : Enums.TurnAction.values())
            turnActions.put(action, 0);

        //region check State
        if (players.get(activePlayer).getActiveCard().getTopCard() != null) {
            PokemonCard activeCard = (PokemonCard) players.get(activePlayer).getActiveCard().getTopCard();
            if (activeCard.getStatus() != Status.none) {

                fireShowMessage(Alert.AlertType.INFORMATION,
                        (activePlayer == Enums.Player.A ? "User " : "Opponent") + " is " + activeCard.getStatus().name(), 1.5);
                switch (activeCard.getStatus()) {
                    case none://nothing to do..
                        break;
                    case paralyzed:
                        activeCard.setStatus(Status.none);
                        if (activePlayer == Enums.Player.A)
                            activePlayer = Enums.Player.B;
                        else activePlayer = Enums.Player.A;
                        startTurn(true);
                        return;
                    case stuck:
                        //Pokemon can not retreat during this turn
                        activeCard.setStatus(Status.none);
                        retreatRestricted = true;
                        break;
                    case poisoned:
                        //
                        //one damage counter must be put on the Pokémon in between each turn.
                        activeCard.addDamage(1);

                        break;
                    case asleep: //it cannot attack or retreat by itself.
                        // It must also be turned to the left.
                        // After each turn, if a player's Pokémon is Asleep, the player must flip a coin: if heads,
                        // the Asleep Pokémon "wakes up" and is no longer affected by the Special Condition.

                        attackRetreatRestricted = true;

                        break;
                }
            }
        }
//endregion
        //region check State
        if (players.get(getOpponent(activePlayer)).getActiveCard().getTopCard() != null) {
            PokemonCard activeCard = (PokemonCard) players.get(getOpponent(activePlayer)).getActiveCard().getTopCard();
            if (activeCard.getStatus() != Status.none) {
                switch (activeCard.getStatus()) {
                    case none://nothing to do..
                        break;
                    case paralyzed:
                        return;
                    case stuck:
                    case poisoned:

                        break;
                    case asleep: //it cannot attack or retreat by itself.
                        // It must also be turned to the left.
                        // After each turn, if a player's Pokémon is Asleep, the player must flip a coin: if heads,
                        // the Asleep Pokémon "wakes up" and is no longer affected by the Special Condition.
                        if (!players.get(getOpponent(activePlayer)).isComputer() ?
                                fireFlipCoin(Coin.Head, -1) :
                                new Random().nextBoolean()) {
                            activeCard.setStatus(Status.none);
                        }
                        break;
                }
            }
        }
//endregion
        if (players.get(activePlayer).isComputer())
            playAI();
    }

    private void playAI() throws Exception {
        if (gameFinished)
            return;
        Player player = players.get(activePlayer);

        //region put pokemon in active area from hand
        if (turnActions.get(TurnAction.pokemonToActive) == 0)
            if (player.getAreaCard(Area.active).size() == 0) {
                for (Card card : player.getAreaCard(Area.hand)) {
                    if (card instanceof PokemonCard && ((PokemonCard) card).getLevel().equals("basic")) {
                        Card candidateCard = player.popCard(card.getId(), "");
                        player.addCard(candidateCard, Area.active, "");
                        turnActions.put(TurnAction.pokemonToActive, 1);
                        logger.info("put to active area:" + candidateCard.getName());
                        break;
                    }
                }

            }
        //endregion

        //region put pokemon into active area from bench
        if (turnActions.get(TurnAction.pokemonToActive) == 0)
            if (player.getAreaCard(Area.active).size() == 0) {
                for (Card card : player.getAreaCard(Area.bench)) {
                    player.swapCardHolder(player.getCardHolder(card.getId()), null, Area.bench, Area.active);
                    turnActions.put(TurnAction.pokemonToActive, 1);
                    logger.info("put From BENCH to active area:" + card.getName());
                    break;
                }

            }
        //endregion

        //region put pokemon in bench from hand

        logger.info("region put pokemon in bench from hand");
        putPokemonIntoBench(activePlayer);
        //endregion

        //region attach stage one to basic card in active area
        if (turnActions.get(TurnAction.attachStage1CardToActive) == 0) {
            if (player.getActiveCard() != null && player.getActiveCard().getTopCard() != null)
                if (player.getActiveCard().getTopCard().getType().equals("basic")) {
                    for (Card handCard : player.getAreaCard(Area.hand)) {
                        if (handCard instanceof PokemonCard && !((PokemonCard) handCard).getLevel().equals("basic")
                                && ((PokemonCard) handCard).getLevel().equals(player.getActiveCard().getTopCard().getName())) {
                            player.addCard(player.popCard(handCard.getId(), Area.hand, -1, "")
                                    , Area.active, player.getActiveCard().getId());
                            turnActions.put(TurnAction.attachStage1CardToActive, 1);
                            logger.info(TurnAction.attachStage1CardToActive + ":" + handCard.getName());
                            break;
                        }
                    }
                }
        }

        //endregion

        //region attach stage one to basic card in bench
        if (turnActions.get(TurnAction.attachStage1CardToBench) + turnActions.get(TurnAction.attachStage1CardToActive) == 0) {
            List<Card> benchCards = player.getAreaCard(Area.bench);
            for (int i = 0; i < benchCards.size(); i++) {
                Card benchCard = benchCards.get(i);
                logger.info("benchCard " + i + " = " + benchCard.getCategory().name() + " name: " + benchCard.getName());
                for (Card handCard : player.getAreaCard(Area.hand)) {
                    if (handCard instanceof PokemonCard && ((PokemonCard) benchCard).getLevel().equals("basic")
                            && ((PokemonCard) handCard).getLevel().equals(benchCard.getName())) {
                        player.addCard(player.popCard(handCard.getId(), Area.hand, -1, ""), Area.bench, i, benchCard.getId());
                        logger.info(TurnAction.attachStage1CardToBench + ":" + handCard.getName());
                        break;
                    }
                }
            }
        }
//endregion

        //region if(active pokemon needs energy)=> attach energy
        logger.info("region if(active pokemon needs energy)=> attach energy");
        if (turnActions.get(TurnAction.attachEnergyOnActive) == 0)
            if (player.getActiveCard() != null && player.getActiveCard().getTopCard() != null) {
                PokemonCard activeCard = player.getActiveCard().getTopCard();
                if (turnActions.get(TurnAction.attachEnergyOnActive) == 0)
                    for (Attack activeCardAttack : activeCard.getAttackList()) {
                        logger.info("activeCard.getAttackList: " + activeCardAttack.getAbility().getName());
                        if (turnActions.get(TurnAction.attachEnergyOnActive) == 0)
                            if (player.getActiveCard().getEnergyCards() != null) {
                                logger.info("player.getActiveCard().getEnergyCards() != null");
                                Map<String, Integer> requiredEnergyMap = activeCardAttack.getRequiredEnergy(player.getActiveCard().getEnergyCards());
                                logger.info("requiredEnergyMap.size: " + requiredEnergyMap.size());
                                for (String costType : requiredEnergyMap.keySet()) {
                                    logger.info("costType: " + costType);
                                    if (turnActions.get(TurnAction.attachEnergyOnActive) == 0) {
                                        for (Card handCard : player.getAreaCard(Area.hand)) {
                                            if (turnActions.get(TurnAction.attachEnergyOnActive) == 0)
                                                if (handCard instanceof EnergyCard)
                                                    if (handCard.getType().equals(costType) || costType.equals("colorless") || handCard.getType().equals("colorless")) {
                                                        player.addCard(player.popCard(handCard.getId(), ""), Area.active, activeCard.getId());
                                                        turnActions.put(TurnAction.attachEnergyOnActive, turnActions.get(TurnAction.attachEnergyOnActive) + 1);
                                                        logger.info("attach energy :" + handCard.getName() + " into: ActivePokemon: " + activeCard.getName());
                                                    }

                                        }
                                    }
                                }
                            }
                    }

            }
        //endregion
        //region if(bench pokemon needs energy)=>attach energy
        if (turnActions.get(TurnAction.attachEnergyOnBench) + turnActions.get(TurnAction.attachEnergyOnActive) == 0)
            for (Card benchCard : player.getAreaCard(Area.bench)) {
                if (benchCard instanceof PokemonCard) {
                    PokemonCard benchPokemonCard = (PokemonCard) benchCard;
                    if (turnActions.get(TurnAction.attachEnergyOnBench) == 0)
                        for (Attack benchCardAttack : benchPokemonCard.getAttackList()) {
                            if (turnActions.get(TurnAction.attachEnergyOnBench) == 0)
                                if (player.getCardHolder(benchPokemonCard.getId()).getEnergyCards() != null) {
                                    Map<String, Integer> requiredEnergyMap = benchCardAttack.getRequiredEnergy(player.getCardHolder(benchPokemonCard.getId()).getEnergyCards());
                                    for (String costType : requiredEnergyMap.keySet()) {
                                        if (turnActions.get(TurnAction.attachEnergyOnBench) == 0) {
                                            for (Card handCard : player.getAreaCard(Area.hand)) {
                                                if (turnActions.get(TurnAction.attachEnergyOnBench) == 0)
                                                    if (handCard instanceof EnergyCard)
                                                        if (handCard.getType().equals(costType) || costType.equals("colorless") || handCard.getType().equals("colorless")) {
                                                            player.addCard(player.popCard(handCard.getId(), ""), Area.bench, benchPokemonCard.getId());
                                                            turnActions.put(TurnAction.attachEnergyOnBench, turnActions.get(TurnAction.attachEnergyOnBench) + 1);
                                                            logger.info("attach energy :" + handCard.getName() + " into: benchPokemon: " + benchPokemonCard.getName());
                                                        }

                                            }
                                        }
                                    }
                                }
                        }
                }
            }
        //endregion

        //region run trainer
        if (!firstTurn)
            if (players.get(getOpponent(activePlayer)).getActiveCard() != null
                    && players.get(getOpponent(activePlayer)).getActiveCard().getTopCard() != null
                    && players.get(activePlayer).getActiveCard().getTopCard() != null)
                if (turnActions.get(TurnAction.trainer) == 0) {
                    for (Card handCard : player.getAreaCard(Area.hand)) {
                        if (handCard instanceof TrainerCard) {
                            Card trainerCard = player.popCard(handCard.getId(), "");
                            executeAbility(activePlayer, ((TrainerCard) trainerCard).getAttack().getAbility());

                            players.get(activePlayer).addCard(trainerCard, Area.discard, -1, "", false);
                            logger.info("trainer card:" + handCard.getName());

                            break;
                        }

                    }
                }

        //endregion

        //region run retreat or attack
        //attack OR retreat?
        //attack?
        //get  opponent active card's health point

        if (!firstTurn)
            if (players.get(getOpponent(activePlayer)).getActiveCard().getTopCard() != null &&
                    players.get(activePlayer).getActiveCard().getTopCard() != null) {

                PokemonCard activeCard = player.getActiveCard().getTopCard();
                Attack bestOpponentAttack = players.get(getOpponent(activePlayer)).getActiveCard().getBestAttack();
                Attack bestAttack = player.getActiveCard().getBestAttack();
                logger.info(players.get(getOpponent(activePlayer)).getActiveCard().getId());
                if (bestAttack != null && bestAttack.hasEnoughEnergy(player.getActiveCard().getEnergyCards()) &&
                        bestAttack.getAbility().getActionsPower() >= players.get(getOpponent(activePlayer)).getActiveCard().getTopCard().getHealth()) {
                    //justDoAttack = true;
                    if (!attackRetreatRestricted) {
                        executeAbility(activePlayer, bestAttack.getAbility());
                        logger.info(activePlayer + ": " + TurnAction.attack + ":" + activeCard.getName());
                    } else
                        logger.info("AI can not attack due to restriction");

                } //check for retreat
                else if (bestOpponentAttack != null
                        && bestOpponentAttack.getAbility().getActionsPower() >= player.getActiveCard().getTopCard().getHealth()
                        //justDoRetreat = true;
                        && player.getAreaCard(Area.bench).size() > 0 &&
                        player.getActiveCard().getTopCard().getRetreat().hasEnoughEnergy(player.getActiveCard().getEnergyCards())) {
                    if (!attackRetreatRestricted && !retreatRestricted) {
                        executeRetreat(activePlayer, player.getActiveCard().getTopCard().getRetreat());
                        logger.info(activePlayer + ": " + TurnAction.retreat + ":" + activeCard.getName());
                    } else
                        logger.info("AI can not attack due to restriction");

                } else if (bestAttack != null && bestAttack.hasEnoughEnergy(player.getActiveCard().getEnergyCards())) {//try to attack
                    if (!attackRetreatRestricted) {
                        executeAbility(activePlayer, bestAttack.getAbility());
                        logger.info(activePlayer + ": " + TurnAction.attack + ":" + activeCard.getName());
                    } else
                        logger.info("AI can not attack due to restriction");
                } else {//try to retreat
                    if (player.getAreaCard(Area.bench).size() > 0 &&
                            player.getActiveCard().getTopCard().getRetreat().hasEnoughEnergy(player.getActiveCard().getEnergyCards())) {
                        if (!attackRetreatRestricted && !retreatRestricted) {
                            executeRetreat(activePlayer, player.getActiveCard().getTopCard().getRetreat());
                            logger.info(activePlayer + ": " + TurnAction.attack + ":" + activeCard.getName());
                        } else
                            logger.info("AI can not attack due to restriction");
                    }
                }
            }
        //endregion

        //check knockout active Card
        checkKnockout();

        if (activePlayer == Enums.Player.A)
            activePlayer = Enums.Player.B;
        else activePlayer = Enums.Player.A;
        startTurn(true);
    }

    private void putPokemonIntoBench(Enums.Player playerName) throws Exception {

        for (Card card : players.get(playerName).getAreaCard(Area.hand)) {
            if (players.get(playerName).getAreaCard(Area.bench).size() < 5) {

                if (card instanceof PokemonCard && ((PokemonCard) card).getLevel().equals("basic")) {
                    logger.info("handCard:" + card.getName() + "moves into bench");
                    players.get(playerName).addCard(players.get(playerName).popCard(card.getId(), Area.hand, -1, ""),
                            Area.bench, -1, "");
                    turnActions.put(TurnAction.pokemonToBench, turnActions.get(TurnAction.pokemonToBench) + 1);
                    logger.info("put to bench area:" + card.getName());
                    putPokemonIntoBench(playerName);
                    return;
                }
            } else break;
        }
    }

    private boolean executeRetreat(Enums.Player activePlayer, Attack retreat) throws Exception {
        if (gameFinished)
            return false;
        Player player = players.get(activePlayer);
        //exchange active card with bench card
        List<String> selectedCard = new ArrayList<>();
        if (player.isComputer())
            selectedCard.add(player.getRandomCardId(Area.bench));
        else
            do {
                selectedCard = fireSelectCardRequest("Select a card to be swapped by active card", 1,
                        players.get(activePlayer).getAreaCard(Area.bench), true);
            }
            while (selectedCard.size() != 1);
        //move energy cards into discard
        List<EnergyCard> energyCards = player.getActiveCard().getEnergyCards();

        for (int i = 0; i < retreat.getCostAmount(""); i++) {
            EnergyCard card = energyCards.get(0);
            player.addCard(player.popCard(card.getId(), player.getActiveCard().getId()), Area.discard, -1, "");
        }
        player.swapCardHolder(player.getCardHolder(player.getActiveCard().getId()), player.getCardHolder(selectedCard.get(0)), Area.active, Area.bench);

        return true;
    }

    private Enums.Player getPlayerName(String nodeID) {
        for (Enums.Player name : players.keySet()) {
            if (nodeID.replaceAll("\\d*$", "").endsWith(name.name()))
                return Enums.Player.valueOf(name.name());
        }
        return Enums.Player.None;
    }

    public boolean executeAbility(Enums.Player name, Ability ability) throws Exception {
        if (gameFinished)
            return false;

        logger.info("player " + name + ", executes " + ability.getName() + "\r\n action:" + ability.action);
        ability.action.addListener(new LogicEventListener() {
            @Override
            public Boolean showMessage(Alert.AlertType confirmation, String message, double duration) {
                logger.info("Player " + name + " in Ability :" + ability.getName() + " message request :" + confirmation.name() + " " + message + " " + duration);

                return fireShowMessage(confirmation, message, duration);
            }

            @Override
            public boolean flipCoin(Coin defaultFace, double waitForFlipping) throws URISyntaxException {
                logger.info("Player " + name + " in Ability :" + ability.getName() + " flip request ");

                return fireFlipCoin(defaultFace, waitForFlipping);
            }

            @Override
            public List<String> selectCardRequest(String message, int totalRequired, List<Card> cardList, boolean showCard) throws Exception {
                logger.info("Player " + name + " in Ability :" + ability.getName() + " Select Card request ");
                return fireSelectCardRequest(message, totalRequired, cardList, showCard);
            }

            @Override
            public boolean actionRequest(Enums.Player playerName, IActionStrategy action) throws Exception {
                actionrequestedList.put(playerName, action);
                return true;
            }
        });
        ability.action.fight(players.get(name), players.get(getOpponent(name)));
        ability.action.clearListener();

        return true;
    }

    private void checkKnockout() throws Exception {
        logger.info("CHECK KNOCKOUT");

        if (players.get(getOpponent(activePlayer)).getActiveCard() != null &&
                players.get(getOpponent(activePlayer)).getActiveCard().getTopCard() != null &&
                players.get(getOpponent(activePlayer)).getActiveCard().getTopCard().getHealth() <= 0) {
            logger.info("1");

            String opponentActiveId = players.get(getOpponent(activePlayer)).getActiveCard().getId();
            for (String activeCardId : players.get(getOpponent(activePlayer)).getActiveCard().getAllCard().keySet()) {
                if (!activeCardId.equals(opponentActiveId)) {
                    players.get(getOpponent(activePlayer)).addCard(players.get(getOpponent(activePlayer))
                            .popCard(activeCardId, opponentActiveId), Area.discard, -1, "", false);
                    logger.info("put " + activeCardId + " on discard");
                }
            }
            players.get(getOpponent(activePlayer)).addCard(players.get(getOpponent(activePlayer))
                    .popCard(opponentActiveId, ""), Area.discard, -1, "", false);
            logger.info("2");

            List<String> selectedCardList = new ArrayList<>();
            if (players.get(activePlayer).isComputer()) {
                selectedCardList.add(players.get(activePlayer).getRandomCardId(Area.prize));
            } else {
                fireShowMessage(Alert.AlertType.INFORMATION, "You knockout the your opponent's active card", 1);
                fireShowMessage(Alert.AlertType.INFORMATION, "Please select a card from prize area", 1);


                selectedCardList = fireSelectCardRequest("Please select a card", 1,
                        players.get((activePlayer)).getAreaCard(Area.prize), false);
            }
            logger.info("3");

            for (String cardId : selectedCardList) {
                logger.info(activePlayer + "select prize card :" + cardId);
                players.get(activePlayer)
                        .addCard(players.get(activePlayer).popCard(cardId, Area.prize, -1, cardId), Area.hand, "");
            }
            logger.info("4");

        }
        logger.info(players.get(getOpponent(activePlayer)).getActiveCard().getAllCard().size() + " \r\n" +
                players.get(getOpponent(activePlayer)).getAreaCard(Area.bench).size());

        if (!firstTurn)
            if (players.get(getOpponent(activePlayer)).getActiveCard().getAllCard().size() == 0) {
                if (players.get(getOpponent(activePlayer)).getAreaCard(Area.bench).size() <= 0) {
                    gameFinished = true;
                    //opponent wins the game
                    fireShowMessage(Alert.AlertType.INFORMATION, "PLAYER " + activePlayer.name() + " WINS THE GAME", -1);
                }
            }

    }

    public TurnAction isMovementVerified(Area targetArea, Card targetCard, Card flyCard, Area sourceArea, Card targetStageCard, Enums.Player player) {

        boolean movementVerified = false;
        TurnAction turnAction = TurnAction.none;
        if (player == activePlayer && getPlayerName(flyCard.getId()) == player)//uc 1-6
            if (sourceArea != targetArea)//uc1-11
                if (sourceArea == Area.bench || sourceArea == Area.hand)//uc 1-9
                    if (targetArea == Area.active || targetArea == Area.bench)//uc1-10
                        //push to another card
                        if (targetCard != null) {

                            switch (targetCard.getCategory()) {
                                case pokemon:

                                    switch (flyCard.getCategory()) {
                                        case pokemon:
                                            if (((PokemonCard) targetCard).getLevel().equals("basic"))
                                                if (targetStageCard == null)//uc1-7
                                                    if (!((PokemonCard) flyCard).getLevel().equals("basic")
                                                            && ((PokemonCard) flyCard).getLevel().equals(targetCard.getName())) {
                                                        movementVerified = true;
                                                        if (targetArea == Area.active)
                                                            turnAction = TurnAction.attachStage1CardToActive;
                                                        else if (targetArea == Area.bench)
                                                            turnAction = TurnAction.attachStage1CardToBench;

                                                    }

                                            break;
                                        case trainer:
                                            break;
                                        case energy:
                                            movementVerified = true;
                                            if (targetArea == Area.active)
                                                turnAction = TurnAction.attachEnergyOnActive;
                                            else if (targetArea == Area.bench)
                                                turnAction = TurnAction.attachEnergyOnBench;
                                            break;
                                    }
                                    break;
                                case trainer:
                                    break;
                                case energy:
                                    break;
                            }
                        }
                        //push to area
                        else {
                            switch (flyCard.getCategory()) {
                                case pokemon:
                                    if (((PokemonCard) flyCard).getLevel().equals("basic")
                                            || (players.get(activePlayer).getCardHolder(flyCard.getId()) != null
                                            && players.get(activePlayer).getCardHolder(flyCard.getId()).getBasicCard() != null)) {
                                        movementVerified = true;
                                        if (targetArea == Area.active)
                                            turnAction = TurnAction.pokemonToActive;
                                        else if (targetArea == Area.bench)
                                            turnAction = TurnAction.pokemonToBench;
                                    }
                                    break;
                                case trainer:
                                    //push to no where // uc1-15
                                    break;
                                case energy:
                                    //push to no empty area//uc1-14
                                    break;
                            }
                        }
        return turnAction;
    }

    //endregion

    //region Event
    public void addListener(LogicEventListener listener) {
        listenerList.add(LogicEventListener.class, listener);
    }

    boolean fireShowMessage(Alert.AlertType type, String message, double duration) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == LogicEventListener.class) {
                return ((LogicEventListener) listeners[i + 1]).showMessage(type, message, duration);
            }
        }
        return false;
    }

    boolean fireFlipCoin(Coin defaultFace, double waitForFlipping) throws URISyntaxException {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == LogicEventListener.class) {
                return ((LogicEventListener) listeners[i + 1]).flipCoin(defaultFace, waitForFlipping);
            }
        }
        return false;
    }

    private List<String> fireSelectCardRequest(String message, int cardNumber, List<Card> cardList, boolean showCard) throws Exception {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == LogicEventListener.class) {
                return ((LogicEventListener) listeners[i + 1]).selectCardRequest(message, cardNumber, cardList, showCard);
            }
        }
        return new ArrayList<>();
    }
    //endregion

    //region UI Event Listener
    public uiBoardEventListener uiBoardEventListener = new uiBoardEventListener() {
        @Override
        public int getAreaSize(Enums.Player player, Area area) {
            return players.get(player).getAreaCard(area).size();
        }

        @Override
        public void MoveCard(Area targetArea, Card targetCard, Card targetStageCard, Card flyCard, Area sourceArea, Enums.Player senderPlayer,
                             List<Card> uiCardList, int targetColumnIndex, int sourceColumnIndex, String uiCardId, String uiSmallCardId)
                throws Exception {
            if (gameFinished)
                return;
            TurnAction movementVerified = isMovementVerified(targetArea, targetCard, flyCard, sourceArea, targetStageCard, senderPlayer);
            if (movementVerified == TurnAction.none)
                return;
            else {
                turnActions.put(movementVerified, turnActions.get(movementVerified) + 1);
                if (!checkTurnActions(movementVerified)) {
                    return;
                }
            }
            logger.info("Move card " + uiCardId + " from " + sourceArea + ", to "
                    + targetArea + (targetCard != null ? "(" + uiSmallCardId + ")" : ""));
            //POP card from Source
            Map<String, Card> cardList = new HashMap<>();
            for (Card card : uiCardList)
                cardList.put(card.getId(), players.get(senderPlayer)
                        .popCard(card.getId(), sourceArea, sourceColumnIndex, flyCard.getId()));

            //PUSH card into Destination
            if (cardList.size() > 0 && uiSmallCardId.length() == 0)//complex=>normal Area
            {
                uiSmallCardId = uiCardId;
                //Add Head UIControls
                players.get(senderPlayer).
                        addCard(cardList.get(flyCard.getId()).clone(), targetArea, targetColumnIndex, "", false);
                cardList.remove(uiCardId);
            }
            //Remained Cards
            for (Card card : cardList.values())
                players.get(senderPlayer).
                        addCard(card, targetArea, targetColumnIndex, uiSmallCardId, false);

        }

        @Override
        public void doneButtonPressed(Enums.Player player) throws Exception {
            if (gameFinished)
                return;
            if (players.get(player).getActiveCard().getTopCard() == null &&
                    (players.get(player).getAreaCard(Area.bench).size() > 0
                            || players.get(player).getAreaCard(Area.hand, CardCategory.pokemon).size() > 0)) {
                if (players.get(player).getAreaCard(Area.hand, CardCategory.pokemon).size() > 0)
                    fireShowMessage(Alert.AlertType.INFORMATION, "Have a Pokemon in Active Area", 2);
                else {
                    if (!firstTurn)
                        if (players.get(getOpponent(activePlayer)).getActiveCard().getAllCard().size() == 0) {
                            if (players.get(getOpponent(activePlayer)).getAreaCard(Area.bench).size() <= 0) {
                                gameFinished = true;
                                //opponent wins the game
                                fireShowMessage(Alert.AlertType.INFORMATION, "PLAYER " + activePlayer.name() + " WINS THE GAME", -1);
                            }
                        }
                }

                return;
            }
            if (activePlayer == player) {
                if (activePlayer == Enums.Player.A)
                    activePlayer = Enums.Player.B;
                else activePlayer = Enums.Player.A;
                startTurn(true);
            }
        }

        @Override
        public void showAreaCard(Area area, Enums.Player playerName) throws Exception {
            if (area == Area.discard)
                fireSelectCardRequest("player " + playerName + " " + area.name()
                                + " area - size : " + players.get(playerName).getAreaCard(area).size() + "cards", 0,
                        players.get(playerName).getAreaCard(area), true);
        }
    };

    private boolean checkTurnActions(TurnAction movementVerified) {

        switch (movementVerified) {

            case pokemonToBench:
                //no rules=> always return true
                return true;
            case pokemonToActive:
                if (turnActions.get(TurnAction.pokemonToActive) > 1)
                    return false;
            case attachEnergyOnBench:
            case attachEnergyOnActive:
                if (turnActions.get(TurnAction.attachEnergyOnActive) + turnActions.get(TurnAction.attachEnergyOnBench) > 1)
                    return false;
            case attachStage1CardToActive:
//                if (firstTurn)
//                    return false;//attach stage 1 in first turn is forbidden
                if (turnActions.get(TurnAction.attachStage1CardToActive) > 1)
                    return false;
            case attachStage1CardToBench:
                if (turnActions.get(TurnAction.attachStage1CardToBench) > 5)
                    return false;
                break;
            case retreat:

                break;
            case attack:
                break;
            case trainer:
                break;
            case none:
                break;
        }
        return true;
    }

    boolean firstTurn = true;
    public Listeners.uiCardEventListener uiCardEventListener = new uiCardEventListener() {

        @Override
        public void attackRequest(Enums.Player playerName, String pokemonCardId, int attackIndex) throws Exception {
            if (gameFinished)
                return;
            if (firstTurn)
                return;

            //no card can send attack unless it is active pokemon or trainer card (in activePlayer's hand)
            //and it just happens during it's turn
            if (playerName == activePlayer && !players.get(playerName).isComputer())
            //get opponent
            {
                Card card = players.get(playerName).getCard(pokemonCardId);
                CardHolder cardHolder = players.get(playerName).getCardHolder(pokemonCardId);

                //check cost
                if (card != null) {
                    boolean result = false;
                    if (card instanceof PokemonCard && cardHolder != null && players.get(playerName).getCardArea(pokemonCardId) == Area.active) {
                        if (attackIndex == -1) {//retreat
                            if (players.get(playerName).getAreaCard(Area.bench).size() > 0) {
                                logger.info(playerName + " :" + card.getId() + " " + TurnAction.retreat);
                                if (((PokemonCard) card).getRetreat().hasEnoughEnergy(cardHolder.getEnergyCards())) {
                                    logger.info("retreatCostList: " + ((PokemonCard) card).getRetreat().getCostAmount("") + " / " + cardHolder.getEnergyCards().size());
                                    if (!attackRetreatRestricted && !retreatRestricted) {
                                        if (executeRetreat(playerName, ((PokemonCard) card).getRetreat())) {
                                            logger.info(activePlayer + ": " + TurnAction.attack + ":" + card.getName());
                                            turnActions.put(TurnAction.retreat, turnActions.get(TurnAction.retreat) + 1);
                                            result = true;
                                        }
                                    } else
                                        logger.info("User can not attack due to restriction");
                                } else
                                    fireShowMessage(Alert.AlertType.INFORMATION, "You have not enough energy to do that.", 1);
                            } else {
                                fireShowMessage(Alert.AlertType.INFORMATION, "You have no Pokemon on the bench", 1);
                            }
                        } else {//Attack
                            if (((PokemonCard) card).getAttackList().get(attackIndex).hasEnoughEnergy(cardHolder.getEnergyCards())) {
                                if (!attackRetreatRestricted) {
                                    if (executeAbility(playerName, ((PokemonCard) card).getAttackList().get(attackIndex).getAbility())) {
                                        logger.info(activePlayer + ": " + TurnAction.attack + ":" + card.getName());
                                        turnActions.put(TurnAction.attack, turnActions.get(TurnAction.attack) + 1);
                                        result = true;
                                    }
                                } else {
                                    logger.info("User can not attack due to restriction");

                                }
                            } else
                                fireShowMessage(Alert.AlertType.INFORMATION, "You have not enough energy to do that.", 1);
                        }
                    } else if (card instanceof TrainerCard && players.get(playerName).getCardArea(pokemonCardId) == Area.hand) {

                        turnActions.put(TurnAction.trainer, turnActions.get(TurnAction.trainer) + 1);

                        Card trainerCard = players.get(playerName).popCard(card.getId(), "");
                        if (executeAbility(playerName, ((TrainerCard) trainerCard).getAttack().getAbility())) {
                            players.get(playerName).addCard(trainerCard, Area.discard, -1, "", false);
                            logger.info("trainer card:" + card.getName());
                            result = true;
                        } else
                            fireShowMessage(Alert.AlertType.ERROR,
                                    "Ability " + ((TrainerCard) trainerCard).getAttack().getAbility().getName() + ", did not work properly.", -1);

                    }
                    if (result) {
                        LogicController.this.checkKnockout();

                        if (activePlayer == Enums.Player.A)
                            activePlayer = Enums.Player.B;
                        else activePlayer = Enums.Player.A;
                        startTurn(true);
                    }
                } else
                    fireShowMessage(Alert.AlertType.WARNING, "Card is not found.", 2);
            }
        }


        @Override
        public boolean showFaceRequest(Enums.Player playerName, String cardId) {
            logger.info(playerName + " wants to see " + cardId + " in " + players.get(playerName).getCardArea(cardId));
            switch (players.get(playerName).getCardArea(cardId)) {
                case hand:
                    if (!players.get(playerName).isComputer()) {
                        return true;
                    }
                    break;
                case bench:
                case active:
                case discard:
                    return true;
                case deck:
                case prize:
                    return false;

            }
            return false;
        }

        @Override
        public void cardClicked(String cardId) {
            try {
                logger.info(players.get(getPlayerName(cardId)).getCard(cardId).getCategory().name() +
                        players.get(getPlayerName(cardId)).getCard(cardId).getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    };

    //endregion

    //region Setter-Getter
    public Enums.Player getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(Enums.Player activePlayer) {
        Enums.Player lastPlayer = activePlayer;
        this.activePlayer = activePlayer;
        if (lastPlayer != activePlayer)
            if (activePlayer == Enums.Player.A) {
                fireShowMessage(Alert.AlertType.INFORMATION, "Your Turn...", 0.5);
            } else {
                fireShowMessage(Alert.AlertType.INFORMATION, "Opponent Turn...", 0.5);

            }
    }

    public void addPlayerEventListener(PlayerEventListener playerEventListener) {
        for (Enums.Player playerName : players.keySet()) {
            players.get(playerName).addListener(playerEventListener);
        }
    }

    public Enums.Player getOpponent(Enums.Player me) {
        for (Enums.Player player : players.keySet())
            if (!player.equals(me))
                return player;
        return me;
    }

    public Map<Enums.Player, Player> getPlayers() {
        return players;
    }

    //endregion


}

