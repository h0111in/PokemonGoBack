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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        players.get(Enums.Player.A).addCard(players.get(Enums.Player.A).drawCard(7, Area.deck), Area.hand, -1, "");
        players.get(Enums.Player.B).addCard(players.get(Enums.Player.B).drawCard(7, Area.deck), Area.hand, -1, "");

        //PRIZE AREA
        players.get(Enums.Player.A).addCard(players.get(Enums.Player.A).drawCard(6, Area.deck), Area.prize, -1, "");
        players.get(Enums.Player.B).addCard(players.get(Enums.Player.B).drawCard(6, Area.deck), Area.prize, -1, "");

        //Check pokemon in hands
        for (Player player : players.values()) {
            boolean hasPokemon = false;
            while (!hasPokemon) {
                for (Card card : player.getAreaCard(Area.hand)) {
                    if (card instanceof PokemonCard) {
                        hasPokemon = true;
                        break;
                    }
                }
                if (!hasPokemon) {
//                    fireShowMessage("Player " + player.getName() + "doesn't have pokemon", 1);
//                    fireShowMessage("Exchanging a card...", 1);
                    player.addCard(player.popAllCard(Area.hand, -1, ""), Area.deck, -1, "");
                    player.addCard(Player.shuffle(player.popAllCard(Area.deck, -1, "")), Area.deck, -1, "");
                    player.addCard(player.drawCard(7, Area.deck), Area.hand, -1, "");
                }
            }
        }

        //DEFINE FIRST PLAYER
        fireShowMessage(Alert.AlertType.INFORMATION, "Choice a face!", 1);
        activePlayer = !fireFlipCoin(Coin.None, -1) ?
                Enums.Player.B :
                Enums.Player.A;

        startTurn(false);
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
                        (activePlayer == Enums.Player.A ? "User " : "Opponent") + "is " + activeCard.getStatus().name(), 1.5);
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
                        if (fireFlipCoin(Coin.Head, -1)) {
                            activeCard.setStatus(Status.none);
                        } else {
                            attackRetreatRestricted = true;
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

        //region put pokemon in bench from hand
        if (turnActions.get(TurnAction.pokemonToBench) == 0)
            if (player.getAreaCard(Area.bench).size() <= 5)
                for (Card card : player.getAreaCard(Area.hand)) {
                    if (player.getAreaCard(Area.bench).size() <= 5) {
                        if (card instanceof PokemonCard && ((PokemonCard) card).getLevel().equals("basic")) {
                            player.addCard(player.popCard(card.getId(), ""), Area.bench, -1, "");
                            turnActions.put(TurnAction.pokemonToBench, 1);
                            logger.info("put to bench area:" + card.getName());
                            break;
                        }
                    } else break;
                }
//endregion

        //region attach stage one to basic card in active area
        if (turnActions.get(TurnAction.attachStage1CardToActive) == 0) {
            if (player.getActiveCard() != null)
                if (player.getActiveCard().getTopCard().getType().equals("basic")) {
                    for (Card handCard : player.getAreaCard(Area.hand)) {
                        if (handCard instanceof PokemonCard && !((PokemonCard) handCard).getLevel().equals("basic")
                                && handCard.getType().equals(player.getActiveCard().getTopCard().getType())) {
                            player.addCard(player.popCard(handCard.getId(), ""), Area.active, player.getActiveCard().getId());
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
                for (Card handCard : player.getAreaCard(Area.hand)) {
                    if (benchCard instanceof PokemonCard && !((PokemonCard) benchCard).getLevel().equals("basic")
                            && benchCard.getType().equals(handCard.getType())) {
                        player.addCard(player.popCard(handCard.getId(), ""), Area.bench, i, benchCard.getId());
                        logger.info(TurnAction.attachStage1CardToBench + ":" + handCard.getName());
                        break;
                    }
                }
            }
        }
//endregion

        //region if(active pokemon needs energy)=> attach energy
        PokemonCard activeCard = player.getActiveCard().getTopCard();
        int totalRequiredEnergy = 0;
        for (Attack attack : activeCard.getAttackList())
            totalRequiredEnergy += attack.getCostAmount("");
        if (player.getActiveCard().getEnergyCards().size() < totalRequiredEnergy) {
            for (Card card : player.getAreaCard(Area.hand)) {
                if (card instanceof EnergyCard) {

                    player.addCard(player.popCard(card.getId(), ""), Area.active, activeCard.getId());
                    turnActions.put(TurnAction.attachEnergyOnActive, turnActions.get(TurnAction.attachEnergyOnActive) + 1);
                    logger.info(TurnAction.attachEnergyOnActive + ":" + card.getName());

                    break;
                }
            }
        }
        //endregion
        else
            //region if(bench pokemon needs energy)=>attach energy
            for (Card benchCard : player.getAreaCard(Area.bench)) {
                if (benchCard instanceof PokemonCard) {
                    PokemonCard pokemonCard = (PokemonCard) benchCard;
                    if (player.getCardHolder(benchCard.getId()).getEnergyCards() != null
                            && player.getCardHolder(benchCard.getId()).getEnergyCards().size() < pokemonCard.getHeaviestAttack().getCostAmount(""))
                        for (Card card : player.getAreaCard(Area.hand)) {
                            if (card instanceof EnergyCard) {
                                player.addCard(player.popCard(card.getId(), ""), Area.bench, benchCard.getId());
                                turnActions.put(TurnAction.attachEnergyOnBench, turnActions.get(TurnAction.attachEnergyOnBench) + 1);
                                logger.info(TurnAction.attachEnergyOnBench + ":" + card.getName());

                                break;
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
                Attack bestOpponentAttack = players.get(getOpponent(activePlayer)).getActiveCard().getBestAttack();
                Attack bestAttack = player.getActiveCard().getBestAttack();
                logger.info(players.get(getOpponent(activePlayer)).getActiveCard().getId());
                if (bestAttack != null && bestAttack.hasSufficientEnergy(player.getActiveCard().getEnergyCards()) &&
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
                        player.getActiveCard().getTopCard().getRetreat().hasSufficientEnergy(player.getActiveCard().getEnergyCards())) {
                    if (!attackRetreatRestricted && !retreatRestricted) {
                        executeRetreat(activePlayer, player.getActiveCard().getTopCard().getRetreat());
                        logger.info(activePlayer + ": " + TurnAction.retreat + ":" + activeCard.getName());
                    } else
                        logger.info("AI can not attack due to restriction");

                } else if (bestAttack != null && bestAttack.hasSufficientEnergy(player.getActiveCard().getEnergyCards())) {//try to attack
                    if (!attackRetreatRestricted) {
                        executeAbility(activePlayer, bestAttack.getAbility());
                        logger.info(activePlayer + ": " + TurnAction.attack + ":" + activeCard.getName());
                    } else
                        logger.info("AI can not attack due to restriction");
                } else {//try to retreat
                    if (player.getAreaCard(Area.bench).size() > 0 &&
                            player.getActiveCard().getTopCard().getRetreat().hasSufficientEnergy(player.getActiveCard().getEnergyCards())) {
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
        CheckKnockout();

        if (activePlayer == Enums.Player.A)
            activePlayer = Enums.Player.B;
        else activePlayer = Enums.Player.A;
        startTurn(true);
    }

    private boolean executeRetreat(Enums.Player activePlayer, Attack retreat) throws Exception {
        if (gameFinished)
            return false;
        Player player = players.get(activePlayer);
        //exchange active card with bench card
        List<String> selectedCard = new ArrayList<>();
        do {
            selectedCard = fireSelectCardRequest("Select a card to be swapped by active card", 1,
                    players.get(activePlayer).getAreaCard(Area.bench), true);
        }
        while (selectedCard.size() != 1);
        //move energy cards into discard
        List<EnergyCard> energyCards = players.get(activePlayer).getActiveCard().getEnergyCards();
        for (int i = 0; i < retreat.getCostAmount(""); i++) {
            EnergyCard card = energyCards.get(i);
            player.addCard(player.getActiveCard().pop(card.getId()), Area.discard, -1, player.getActiveCard().getId());
        }
        player.swapCardHolder(player.getActiveCard(), player.getCardHolder(selectedCard.get(0)), Area.active, Area.bench);

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

        ability.action.addListener(new LogicEventListener() {
            @Override
            public Boolean showMessage(Alert.AlertType confirmation, String message, double duration) {
                return fireShowMessage(confirmation, message, duration);
            }

            @Override
            public boolean flipCoin(Coin defaultFace, double waitForFlipping) throws URISyntaxException {
                return fireFlipCoin(defaultFace, waitForFlipping);
            }

            @Override
            public List<String> selectCardRequest(String message, int totalRequired, List<Card> cardList, boolean showCard) throws Exception {
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
        logger.info("player " + name + ", executes " + ability.getName() + "\r\n action:" + ability.action);

        return true;
    }

    private void CheckKnockout() throws Exception {
        logger.info("CHECK KNOCKOUT");

        if (players.get(getOpponent(activePlayer)).getActiveCard() != null &&
                players.get(getOpponent(activePlayer)).getActiveCard().getTopCard() != null &&
                players.get(getOpponent(activePlayer)).getActiveCard().getTopCard().getHealth() <= 0) {
            logger.info("1");
            for (String activeCardId : players.get(getOpponent(activePlayer)).getActiveCard().getAllCard().keySet()) {
                players.get(getOpponent(activePlayer)).addCard(players.get(getOpponent(activePlayer))
                                .popCard(activeCardId, players.get(getOpponent(activePlayer)).getActiveCard().getId()),
                        Area.discard, -1, "", false);
                logger.info("put " + activeCardId + " on discard");
            }
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
                    fireShowMessage(Alert.AlertType.INFORMATION, "PLAYER " + activePlayer.name() + " WINS THE GAME", 3);
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
                                                    if (!((PokemonCard) flyCard).getLevel().equals("basic")) {
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
                                    movementVerified = true;
                                    if (targetArea == Area.active)
                                        turnAction = TurnAction.pokemonToActive;
                                    else if (targetArea == Area.bench)
                                        turnAction = TurnAction.pokemonToBench;
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
                    (players.get(player).getAreaCard(Area.bench).size() > 0 || players.get(player).getAreaCard(Area.hand, CardCategory.pokemon).size() > 0)) {
                fireShowMessage(Alert.AlertType.INFORMATION, "Have a Pokemon in Active Area", 2);
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
                fireSelectCardRequest("player " + activePlayer + " " + area.name()
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
                if (card != null && cardHolder != null) {
                    boolean result = false;
                    if (card instanceof PokemonCard) {
                        if (attackIndex == -1) {//retreat
                            logger.info(playerName + " :" + card.getId() + " " + TurnAction.retreat);
                            if (((PokemonCard) card).getRetreat().hasSufficientEnergy(cardHolder.getEnergyCards())) {
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

                        } else {//Attack
                            if (((PokemonCard) card).getAttackList().get(attackIndex).hasSufficientEnergy(cardHolder.getEnergyCards())) {
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
                    } else if (card instanceof TrainerCard) {

                        turnActions.put(TurnAction.trainer, turnActions.get(TurnAction.trainer) + 1);

                        Card trainerCard = players.get(playerName).popCard(card.getId(), "");
                        if (executeAbility(playerName, ((TrainerCard) trainerCard).getAttack().getAbility())) {
                            players.get(playerName).addCard(trainerCard, Area.discard, -1, "", false);
                            logger.info("trainer card:" + card.getName());
                            result = true;
                        }

                    }
                    if (result) {
                        LogicController.this.CheckKnockout();

                        if (activePlayer == Enums.Player.A)
                            activePlayer = Enums.Player.B;
                        else activePlayer = Enums.Player.A;
                        startTurn(true);
                    }
                } else
                    fireShowMessage(Alert.AlertType.WARNING, "Active area is empty", 2);
            }
        }


        @Override
        public boolean showFaceRequest(Enums.Player playerName, String cardId) {
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

    //region independent Class
//    class Damage extends Action {
//        public Damage() {
//            super();
//
//        }
//
//        public void AddDamge() throws ScriptException {
//
//            //region condition
//            boolean conditionMet = false;
//            switch (getCondition().getName()) {
//                case "flip":
//                    if (fireFlipCoin(Coin.Head, 1)) {
//                        conditionMet = true;
//                    }
//                    break;
//                case "healed":
//                    //EX:If this Pokémon was healed during this turn, this attack does 80 more damage.
//                    switch (getCondition().getTarget()) {
//                        case opponentActive:
//                            break;
//                        case yourActive:
//                            if (players.get(name).getActiveCard().getTopCard().getTotalHealed() > 0) {
//                                conditionMet = true;
//                            }
//                            break;
//                    }
//                    break;
//                default:
//                    conditionMet = true;
//            }
//            //endregion
}

