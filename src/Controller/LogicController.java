package Controller;

import Enums.Area;
import Enums.CardCategory;
import Enums.Coin;
import Enums.TurnAction;
import Model.*;

import javax.script.ScriptException;
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

    //endregion

    //region constructor
    public LogicController(Map<Enums.Player, Boolean> playerNames) {

        listenerList = new EventListenerList();

        //Define Players
        players = new HashMap<>();
        for (Enums.Player playerName : playerNames.keySet()) {
            Player player = new Player(playerName, playerNames.get(playerName));
            players.put(playerName, player);
        }

        //Define TurnActions
        turnActions = new HashMap<>();
        for (TurnAction action : Enums.TurnAction.values())
            turnActions.put(action, 0);
    }
    //endregion

    //region Methods

    public void startGame() throws Exception {

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
//        fireShowMessage("Choice a face!", 2);
        activePlayer = //!fireFlipCoin(Coin.None, -1) ?
                //Enums.Player.B :
                Enums.Player.A;

        startTurn();


    }

    private void startTurn() throws Exception {

        players.get(activePlayer).addCard(players.get(activePlayer).drawCard(1, Area.deck), Area.hand, -1, "");
        for (TurnAction action : Enums.TurnAction.values())
            turnActions.put(action, 0);
        if (players.get(activePlayer).isComputer()) {
            playAutomatic();
        }
    }

    private void playAutomatic() throws Exception {
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
            if (player.getAreaCard(Area.bench).size() < 5)
                for (Card card : player.getAreaCard(Area.hand)) {
                    if (player.getAreaCard(Area.bench).size() < 5) {
                        if (card instanceof PokemonCard && ((PokemonCard) card).getLevel().equals("basic")) {
                            player.addCard(player.popCard(card.getId(), ""), Area.bench, -1, "");
                            turnActions.put(TurnAction.pokemonToBench, 1);
                            logger.info("put to bench area:" + card.getName());
                        }
                    } else break;
                }
//endregion

        //region attach stage one to basic card in active area
        if (turnActions.get(TurnAction.attachStage1CardToActive) == 0) {
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
        if (turnActions.get(TurnAction.attachStage1CardToBench) == 0) {
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
            totalRequiredEnergy += attack.getCostAmount();
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
                            && player.getCardHolder(benchCard.getId()).getEnergyCards().size() < pokemonCard.getTotalRequiredEnergy())
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
        if (players.get(getOpponent(activePlayer)).getActiveCard().getTopCard() != null &&
                players.get(activePlayer).getActiveCard().getTopCard() != null)
            if (turnActions.get(TurnAction.trainer) == 0) {
                for (Card handCard : player.getAreaCard(Area.hand)) {
                    if (handCard instanceof TrainerCard) {
                        Card trainerCard = player.popCard(handCard.getId(), "");
                        executeTrainerCard((TrainerCard) trainerCard);
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
        if (players.get(getOpponent(activePlayer)).getActiveCard().getTopCard() != null &&
                players.get(activePlayer).getActiveCard().getTopCard() != null) {
            Attack bestOpponentAttack = players.get(getOpponent(activePlayer)).getActiveCard().getBestAttack();
            Attack bestAttack = player.getActiveCard().getBestAttack();
            logger.info(players.get(getOpponent(activePlayer)).getActiveCard().getId());
            if (bestAttack != null && bestAttack.getAbility().getActionsPower() >= players.get(getOpponent(activePlayer)).getActiveCard().getTopCard().getHealth()) {
                //justDoAttack = true;
                executeAbility(activePlayer, bestAttack.getAbility());
                logger.info(TurnAction.attack + ":" + activeCard.getName());

            } //check for retreat
            else if (bestOpponentAttack != null && bestOpponentAttack.getAbility().getActionsPower() >= player.getActiveCard().getTopCard().getHealth()
                    //justDoRetreat = true;
                    && player.getAreaCard(Area.bench).size() > 0 &&
                    player.getActiveCard().getEnergyCards().size() >= player.getActiveCard().getTopCard().getRetreat().getCostAmount()) {
                executeRetreat(player.getActiveCard().getTopCard(), player.getAreaCard(Area.bench).get(0));

                logger.info(TurnAction.retreat + ":" + activeCard.getName());
            } else if (bestAttack != null) {//try to attack

                logger.info(TurnAction.attack + ":" + activeCard.getName());
                executeAbility(activePlayer, bestAttack.getAbility());
            } else {//try to retreat
                if (player.getAreaCard(Area.bench).size() > 0 &&
                        player.getActiveCard().getEnergyCards().size() >= player.getActiveCard().getTopCard().getRetreat().getCostAmount()) {

                    logger.info(TurnAction.attack + ":" + activeCard.getName());
                    executeRetreat(player.getActiveCard().getTopCard(), player.getAreaCard(Area.bench).get(0));
                }
            }
        }
        //endregion

        //check knockout active Card
        CheckKnockout();

        if (activePlayer == Enums.Player.A)
            activePlayer = Enums.Player.B;
        else activePlayer = Enums.Player.A;
        startTurn();
    }

    private void executeRetreat(Card activeCard, Card benchCard) {
        //exchange active card with bench card
        //detach energy card to discard area
    }

    private void executeTrainerCard(TrainerCard trainerCard) throws Exception {
        Enums.Player player = getPlayerName(trainerCard.getId());
        //execute card ability

        executeAbility(player, trainerCard.getAttack().getAbility());

        //put trainer card into discard area
        players.get(player).addCard(trainerCard, Area.discard, -1, "");
    }

    private Enums.Player getPlayerName(String nodeID) {
        for (Enums.Player name : players.keySet()) {
            if (nodeID.replaceAll("\\d*$", "").endsWith(name.name()))
                return Enums.Player.valueOf(name.name());
        }
        return Enums.Player.None;
    }

    public void executeAbility(Enums.Player name, Ability ability) throws Exception {

        Enums.Player targetPlayer = Enums.Player.None;
        logger.info("player " + name + ", executes " + ability.getName());
        for (Action action : ability.actionList) {
            switch (action.getTarget()) {
                case opponentActive:
                    targetPlayer = getOpponent(name);

                    break;
                case yourActive:
                    targetPlayer = name;
                    break;
                case yourBench:
                    break;
                case none:
                    break;
                case last:
                    break;

                default:
                    throw new Exception("The Active Area is not defined.");
            }
            switch (action.getName()) {

                //region damage
                case "dam":

                    //region condition
                    boolean conditionMet = isConditionMet(name, action);
                    //endregion

                    if (conditionMet) {

                        //region calculate power
                        String operand = countPower(name, action);
                        //endregion

                        //region apply damage on target
                        if (action.getTarget() == ActionTarget.yourActive)
                            players.get(name).getActiveCard().getTopCard().setDamage(action.getPower().getAmount(operand));
                        else if (action.getTarget() == ActionTarget.opponentActive)
                            players.get(getOpponent(name)).getActiveCard().getTopCard().setDamage(action.getPower().getAmount(operand));
//endregion
                    }
                    break;

                //endregion

                //region heal
                case "heal":

                    //heal:target:your-active:20
                    //Potion:heal:target:your:30 done
                    //region condition

                    //endregion
                    if (isConditionMet(name, action)) {

                        //Apply heal
                        if (action.getTarget() == ActionTarget.opponentActive) {
                            players.get(getOpponent(name)).getActiveCard().getTopCard().setHeal(action.getPower().getAmount(countPower(name, action)));
                        } else if (action.getTarget() == ActionTarget.yourActive) {
                            players.get(name).getActiveCard().getTopCard().setHeal(action.getPower().getAmount(countPower(name, action)));
                        }
                    }

                    break;
                //endregion

                case "deenergize":

                    if (isConditionMet(name, action)) {
                        List<EnergyCard> energyCardList = players.get(targetPlayer).getActiveCard().getEnergyCards();
                        for (Card energyCard : energyCardList) {
                            players.get(targetPlayer).addCard(players.get(targetPlayer).popCard(energyCard.getId(),
                                    players.get(targetPlayer).getActiveCard().getId()), Area.discard, "");
                        }
                    }
                    break;
                case "destat":
                    switch (action.getStatus()) {

                        case none:
                            break;
                        case paralyzed:
                            break;
                        case stuck:
                            break;
                        case poisoned:
                            //10 hit point per turn
                            break;
                        case asleep:
                            break;
                    }
                    break;
            }

        }

    }

    private void CheckKnockout() throws Exception {
        for (Enums.Player playerName : players.keySet())
            if (players.get(getOpponent(playerName)).getActiveCard().getTopCard() != null &&
                    players.get(getOpponent(playerName)).getActiveCard().getTopCard().getHealth() <= 0) {

                for (String activeCardId : players.get(getOpponent(playerName)).getActiveCard().getAllCard().keySet()) {
                    players.get(getOpponent(playerName)).addCard(players.get(getOpponent(playerName))
                                    .popCard(activeCardId, players.get(getOpponent(playerName)).getActiveCard().getId()),
                            Area.discard, -1, "");
                    logger.info("put " + activeCardId + " on discard");
                }

                List<String> selectedCardList = new ArrayList<>();
                if (players.get(playerName).isComputer()) {
                    selectedCardList.add(players.get(playerName).getRandomCardId(Area.prize));
                } else {
                    fireShowMessage("You knockout the your opponent's active card", 1);
                    fireShowMessage("Please select a card from prize area", 1);


                    selectedCardList = fireSelectCardRequest("Please select a card", 1,
                            players.get(getOpponent(playerName)).getAreaCard(Area.prize), false);
                }
                for (String cardId : selectedCardList) {
                    players.get(playerName)
                            .addCard(players.get(playerName).popCard(cardId, Area.prize, -1, cardId), Area.hand, "");
                }

            }
    }

    private boolean isConditionMet(Enums.Player name, Action action) throws URISyntaxException, ScriptException {

        boolean conditionMet = false;
        switch (action.getCondition().getName()) {
            case "flip":
                if (fireFlipCoin(Coin.Head, 1)) {
                    conditionMet = true;
                }
                break;
            case "healed":
                //EX:If this Pokémon was healed during this turn, this attack does 80 more damage.
                switch (action.getCondition().getTarget()) {
                    case opponentActive:
                        break;
                    case yourActive:
                        if (players.get(name).getActiveCard().getTopCard().getTotalHealed() > 0) {
                            conditionMet = true;
                        }
                        break;
                }
                break;
            default:
                conditionMet = true;
        }
        return conditionMet;
    }

    private String countPower(Enums.Player name, Action action) {
        String operand = "";
        if (action.getPower().getTargetCategory() == CardCategory.none && action.getTarget() == ActionTarget.none) {
            operand = "";

        } else {
            switch (action.getPower().getTarget()) {

                case opponentActive:
                    for (Card card : players.get(getOpponent(name)).getAreaCard(Area.active)) {
                        if (action.getPower().getTargetCategory() == CardCategory.none ||
                                card.getCategory() == action.getPower().getTargetCategory())
                            if (action.getPower().getType().isEmpty()
                                    || action.getPower().getType().equals(card.getType()))
                                operand = String.valueOf(Integer.parseInt(operand) + 1);
                    }
                    break;
                case yourActive:
                    for (Card card : players.get(name).getAreaCard(Area.active)) {
                        if (action.getPower().getTargetCategory() == CardCategory.none
                                || card.getCategory() == action.getPower().getTargetCategory())
                            if (action.getPower().getType().isEmpty()
                                    || action.getPower().getType().equals(card.getType()))
                                operand = String.valueOf(Integer.parseInt(operand) + 1);
                    }
                    break;
                case yourBench:
                    for (Card card : players.get(name).getAreaCard(Area.bench)) {
                        if (action.getPower().getTargetCategory() == CardCategory.none ||
                                card.getCategory() == action.getPower().getTargetCategory())
                            if (action.getPower().getType().isEmpty()
                                    || action.getPower().getType().equals(card.getType()))
                                operand = String.valueOf(Integer.parseInt(operand) + 1);
                    }
                    break;
                case none:
                    break;
                case last:
                    break;
            }
        }

        return operand;
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

    void fireShowMessage(String message, double duration) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == LogicEventListener.class) {
                ((LogicEventListener) listeners[i + 1]).showMessage(message, duration);
            }
        }
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
    public BoardEventListener boardEventListener = new BoardEventListener() {
        @Override
        public int getAreaSize(Enums.Player player, Area area) {
            return players.get(player).getAreaCard(area).size();
        }

        @Override
        public void MoveCard(Area targetArea, Card targetCard, Card targetStageCard, Card flyCard, Area sourceArea, Enums.Player senderPlayer,
                             List<Card> uiCardList, int targetColumnIndex, int sourceColumnIndex, String uiCardId, String uiSmallCardId) throws Exception {

            TurnAction movementVerified = isMovementVerified(targetArea, targetCard, flyCard, sourceArea, targetStageCard, senderPlayer);
            if (movementVerified == TurnAction.none)
                return;
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
                        addCard(cardList.get(flyCard.getId()).clone(), targetArea, targetColumnIndex, "");
                cardList.remove(uiCardId);
            }
            //Remained Cards
            for (Card card : cardList.values())
                players.get(senderPlayer).
                        addCard(card, targetArea, targetColumnIndex, uiSmallCardId);

        }

        @Override
        public void doneButtonPressed(Enums.Player player) throws Exception {

            if (activePlayer == player) {
                if (activePlayer == Enums.Player.A)
                    activePlayer = Enums.Player.B;
                else activePlayer = Enums.Player.A;
                startTurn();
            }
        }
    };

    public uiCardEvent uiCardEvent = new uiCardEvent() {

        @Override
        public void attackRequest(Enums.Player playerName, String pokemonCardId, int attackIndex) throws Exception {

            //get opponent
            Player opponent = players.get(getOpponent(playerName));
            PokemonCard card = (PokemonCard) players.get(playerName).getCard(pokemonCardId);
            CardHolder cardHolder = players.get(playerName).getCardHolder(pokemonCardId);

            //check cost
            if (card.getAttackList().get(attackIndex).getCostAmount() <= cardHolder.getEnergyCards().size()) {

                executeAbility(playerName, card.getAttackList().get(attackIndex).getAbility());

            }
        }

        @Override
        public void applyTrainerCardRequest(Enums.Player playerName, String cardId) throws Exception {

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
                fireShowMessage("Your Turn...", 0.5);
            } else {
                fireShowMessage("Opponent Turn...", 0.5);

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
