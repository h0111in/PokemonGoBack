package Controller;

import Enums.*;
import Model.*;
import Model.Player;

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
    private PlayerEventListener playerEventListener;
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
        activePlayer = fireFlipCoin(Coin.None, -1) ?
                Enums.Player.A :
                Enums.Player.B;
        for (TurnAction action : Enums.TurnAction.values())
            turnActions.put(action, 0);

        startTurn();


    }

    private void startTurn() {
        if (activePlayer == Enums.Player.A)
            fireShowMessage("Your Turn...", 1);
        else fireShowMessage("Opponent Turn...", 1);
    }

    private Enums.Player getPlayerName(String nodeID) {
        for (Enums.Player name : players.keySet()) {
            if (nodeID.replaceAll("\\d*$", "").endsWith(name.name()))
                return Enums.Player.valueOf(name.name());
        }
        return Enums.Player.None;
    }


    public void executeAbility(Enums.Player playerName, Ability ability) throws Exception {
        PokemonCard targetActiveCard = null;
        String activeCardId = "";
        Enums.Player targetPlayer = Enums.Player.None;


        for (Action action : ability.actionList) {
            switch (action.getTarget()) {

                case opponentActive:
                    targetPlayer = getOpponent(playerName);
                    break;
                case yourActive:
                    targetPlayer = playerName;
                    break;

                default:
                    throw new Exception("The Active Area is not defined.");
            }
            switch (action.getName()) {
                //region dam
                case "dam":
                    //pop card
                    activeCardId = players.get(targetPlayer).getActiveCard().getTopCard().getId();
                    targetActiveCard = (PokemonCard) players.get(targetPlayer).popCard(activeCardId, activeCardId);

                    //apply action
                    switch (action.getCondition().getName()) {
                        case "flip":
                            if (fireFlipCoin(Coin.Head, 1)) {
                                targetActiveCard.setDamage(action.getPower().getAmount(""));
                            }
                            break;
                        default:
                            targetActiveCard.setDamage(action.getPower().getAmount(""));
                    }
                    //push card
                    players.get(targetPlayer).addCard(targetActiveCard, Area.active, -1, activeCardId);


                    break;
                //endregion
                case "":
                    break;
            }
            //check knockout player
            if (players.get(targetPlayer).getCardHolder(activeCardId).getTopCard().getHealth() <= 0) {

                players.get(targetPlayer).addCard(players.get(targetPlayer).getCardHolder(activeCardId).popAllCard(), Area.discard, -1, activeCardId);
                fireShowMessage("You knockout the your opponent's active card", 1);
                fireShowMessage("Please select a card from prize area", 1);

                List<String> selectedCardList = fireSelectCardRequest("Please select a card", 1, Area.prize, getOpponent(targetPlayer));

                for (String cardId : selectedCardList) {
                    players.get(getOpponent(targetPlayer))
                            .addCard(players.get(getOpponent(targetPlayer)).popCard(cardId, Area.prize, -1, cardId), Area.hand, "");
                }

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

    void fireShowMessage(String message, int duration) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == LogicEventListener.class) {
                ((LogicEventListener) listeners[i + 1]).showMessage(message, duration);
            }
        }
    }

    boolean fireFlipCoin(Coin defaultFace, int waitForFlipping) throws URISyntaxException {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == LogicEventListener.class) {
                return ((LogicEventListener) listeners[i + 1]).flipCoin(defaultFace, waitForFlipping);
            }
        }
        return false;
    }

    private List<String> fireSelectCardRequest(String message, int cardNumber, Area area, Enums.Player player) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == LogicEventListener.class) {
                return ((LogicEventListener) listeners[i + 1]).selectCardRequest(message, cardNumber, area, player);
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
        public void doneButtonPressed(Enums.Player player) {
            logger.info(player.name());
            //check turnActions if it is valid

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

    };

    //endregion

    //region Setter-Getter
    public Enums.Player getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(Enums.Player activePlayer) {
        this.activePlayer = activePlayer;
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
