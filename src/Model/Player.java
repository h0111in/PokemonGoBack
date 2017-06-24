package Model;

import Enums.Area;
import Enums.CardCategory;
import Enums.Coin;
import Listeners.PlayerEventListener;

import javax.swing.event.EventListenerList;
import java.util.*;

import static Controller.Main.logger;


public class Player {

    private Enums.Player name;
    private Coin coin;
    private boolean isComputer;
    List<CardHolder> bench;
    Map<String, Card> hand;//id-UIControls
    Map<String, Card> deck;
    Map<String, Card> prize;
    CardHolder active;
    Map<String, Card> discard;
    private EventListenerList listenerList;

    public Player(Enums.Player name, boolean isComputer) {
        this.isComputer = isComputer;

        this.name = name;
        listenerList = new EventListenerList();
        bench = new ArrayList<CardHolder>();
        for (int i = 0; i < 5; i++)
            bench.add(new CardHolder());
        hand = new HashMap<>();
        deck = new HashMap<>();
        prize = new HashMap<>();
        active = new CardHolder();
        discard = new HashMap<>();

    }


    public Area getCardArea(String cardId) {

        if (deck.containsKey(cardId)) return Area.deck;
        if (hand.containsKey(cardId)) return Area.hand;
        if (prize.containsKey(cardId)) return Area.prize;
        if (discard.containsKey(cardId)) return Area.discard;
        if (active.getAllCard().containsKey(cardId))
            return Area.active;
        for (CardHolder cardHolder : bench)
            if (cardHolder.getAllCard().containsKey(cardId))
                return Area.bench;
        return null;
    }

    public Enums.Player getName() {
        return name;
    }

    public Player addCard(List<Card> cards, Area destination, int cardHolderIndex, String smallCardId) throws Exception {
        for (Card card : cards)
            addCard(card, destination, cardHolderIndex, smallCardId, false);
        return this;
    }

    public Player addCard(List<Card> cards, Area destination, boolean addTop) throws Exception {
        for (Card card : cards)
            if (addTop)
                addCard(card, destination, -1, "", addTop);
        return this;
    }

    public Player addCard(Card card, Area destination, int cardHolderIndex, String cardHolderId) throws Exception {
        return addCard(card, destination, cardHolderIndex, cardHolderId, false);
    }

    public Player addCard(Card card, Area destination, int cardHolderIndex, String cardHolderId, boolean addtoBottom) throws Exception {
        switch (destination) {
            case deck:
                if (card.getId().equals("")) {
                    card.setId(name.name() + deck.size());
                    card.setPlayerName(name);
                }
                if (addtoBottom) deck = addTopMap(card, deck);
                else
                    deck.put(card.getId(), card);
                break;
            case hand:
                if (addtoBottom) hand = addTopMap(card, hand);
                else
                    hand.put(card.getId(), card);
                break;
            case bench:
                if (cardHolderIndex == -1)
                    for (int i = 0; i < bench.size(); i++) {
                        if (bench.get(i).getAllCard().size() == 0) {
                            cardHolderIndex = i;
                            break;
                        }
                    }
                if (cardHolderIndex == -1) {
                    for (CardHolder cardHolder : bench) {
                        logger.info("cardHolder.getId()=> " + cardHolder.getId());
                        for (String innerCard : cardHolder.getAllCard().keySet()) {
                            logger.info("    ->" + innerCard);
                        }
                    }
                    logger.info("column Index is not found=>" + card.getId());

                    throw new Exception("Bench is full");
                }

                bench.get(cardHolderIndex).add(card);
                break;
            case active:
                active.add(card);
                break;
            case prize:
                if (addtoBottom) prize = addTopMap(card, prize);
                else
                    prize.put(card.getId(), card);
                break;
            case discard:
                if (addtoBottom) discard = addTopMap(card, discard);
                else discard.put(card.getId(), card);
                break;
        }
        fireAddCard(new CardEvent(card, destination, getName(), cardHolderIndex, cardHolderId));
        return this;
    }

    public Player addCard(Card card, Area destination, String smallCardId) throws Exception {
        this.addCard(card, destination, -1, smallCardId, false);
        return this;
    }

    private Map<String, Card> addTopMap(Card card, Map<String, Card> map) {

        Map<String, Card> temp = new HashMap<>();
        temp.put(card.getId(), card);
        for (Card card1 : map.values())
            temp.put(card1.getId(), card1);
        return temp;

    }

    public List<Card> drawCard(int amount, Area area) throws Exception {
        List<Card> cardList = new ArrayList<>();

        while (amount-- > 0) {
            switch (area) {

                case deck:
                    if (deck.size() > 0) {
                        List<String> keys = new ArrayList<String>(deck.keySet());
                        String randomKey = keys.get(keys.size() - 1);
                        Card card = deck.get(randomKey);
                        cardList.add(card);
                        deck.remove(card.getId());
                        fireRemoveCard(new CardEvent(card, area, getName(), -1, ""));
                    }
                    break;
                case hand:
                    if (hand.size() > 0) {
                        Card card = hand.get(((List<String>) hand.keySet()).get(hand.size() - 1));
                        cardList.add(card);
                        hand.remove(card.getId());
                        fireRemoveCard(new CardEvent(card, area, getName(), -1, ""));
                    }
                    break;
                case bench:
                    throw new Exception("Wrong Request");
                case active:
                    throw new Exception("Wrong Request");
                case prize:

                    if (prize.size() > 0) {
                        Card card = prize.get(((List<String>) prize.keySet()).get(0));
                        cardList.add(card);
                        prize.remove(card.getId());
                        fireRemoveCard(new CardEvent(card, area, getName(), -1, ""));
                    }
                    break;
                case discard:

                    if (deck.size() > 0) {
                        Card card = discard.get(((List<String>) discard.keySet()).get(0));
                        cardList.add(card);
                        discard.remove(card.getId());
                        fireRemoveCard(new CardEvent(card, area, getName(), -1, ""));
                    }
                    break;
            }
        }
        return cardList;
    }

    public Card getCard(String id) throws Exception {
        return getCard(id, getCardArea(id));

    }

    private Card getCard(String id, Area area) throws Exception {
        Card card = null;
        switch (area) {

            case deck:
                card = deck.get(id);
                break;
            case hand:
                card = hand.get(id);
                break;
            case bench:
                for (CardHolder cardHolder : bench)
                    if (cardHolder.getAllCard().containsKey(id)) {
                        card = cardHolder.getCard(id);
                        break;
                    }
                break;
            case active:
                card = active.getCard(id);
                break;
            case prize:
                card = prize.get(id);

                break;
            case discard:
                card = discard.get(id);
                break;
        }
        return card;
    }

    private boolean removeCard(String id, Area area) throws Exception {
        switch (area) {

            case deck:
                deck.remove(id);
                return true;
            case hand:
                hand.remove(id);
                return true;

            case bench:
                for (CardHolder cardHolder : bench)
                    if (cardHolder.getAllCard().containsKey(id)) {
                        cardHolder.remove(id);
                        return true;
                    }
                break;
            case active:
                active.remove(id);
                return true;

            case prize:
                prize.remove(id);
                return true;

            case discard:
                discard.remove(id);
                return true;

            case none:
                break;
        }
        return false;
    }

    public Card popCard(String id, String cardHolderId) throws Exception {
        return popCard(id, getCardArea(id), -1, cardHolderId);
    }

    public CardHolder getActiveCard() {
        return active;
    }

    public Card popCard(String id, Area area, int cardHolderIndex, String cardHolderId) throws Exception {
        Card card = getCard(id, area);
        logger.info(id);
        removeCard(id, area);
        fireRemoveCard(new CardEvent(card, area, getName(), cardHolderIndex, cardHolderId));
        return card;
    }

    //region Static Methods
    public static <T> List<T> shuffle(List<T> items) {
        List<T> shuffledList = new ArrayList<T>();
        while (items.size() > 0) {

            int pointer = new Random().nextInt(items.size());

            shuffledList.add(items.get(pointer));
            items.remove(pointer);
        }
        return shuffledList;
    }
    //endregion

    //region Events
    public void addListener(PlayerEventListener listener) {
        listenerList.add(PlayerEventListener.class, listener);
    }

    public void removeListener(PlayerEventListener listener) {
        listenerList.remove(PlayerEventListener.class, listener);
    }

    void fireAddCard(CardEvent evt) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == PlayerEventListener.class) {
                ((PlayerEventListener) listeners[i + 1]).pushCard(evt);
            }
        }
    }

    void fireRemoveCard(CardEvent evt) throws Exception {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == PlayerEventListener.class) {
                ((PlayerEventListener) listeners[i + 1]).popCard(evt);
            }
        }
    }
//endregion


    public String getRandomCardId(Area area) {
        List<Card> tempCardList = getAreaCard(area);
        logger.info(String.valueOf(tempCardList.size()));
        if (tempCardList.size() > 0) {
            int randomIndex = new Random().nextInt(tempCardList.size());
            return tempCardList.get(randomIndex).getId();
        } else return "";
    }

    public List<Card> getAreaCard(Area area, CardCategory cardCategory) {
        List<Card> temp = new ArrayList<>();
        for (Card card : getAreaCard(area)) {
            if (card.getCategory() == cardCategory)
                temp.add(card);
        }
        return temp;

    }

    public List<Card> getAreaCard(Area area) {
        switch (area) {

            case deck:
                return new ArrayList<>(deck.values());
            case hand:
                return new ArrayList<>(hand.values());
            case bench:
                List<Card> list = new ArrayList<>();
                for (CardHolder cardHolder : bench)
                    if (cardHolder.getAllCard().size() != 0)
                        list.add(cardHolder.getTopCard());
                return list;
            case active:
                return new ArrayList<>(getActiveCard().getAllCard().values());
            case prize:
                logger.info(String.valueOf(prize.values().size()));
                return new ArrayList<>(prize.values());
            case discard:
                return new ArrayList<>(discard.values());
        }
        return new ArrayList<>();
    }

    public boolean isComputer() {
        return isComputer;
    }

    public Coin getCoin() {
        return coin;
    }

    public void setCoin(Coin coin) {
        this.coin = coin;
    }

    public CardHolder getCardHolder(String cardId) {
        for (CardHolder cardHolder : bench)
            if (cardHolder.getAllCard().containsKey(cardId)) {
                return cardHolder;
            }
        if (active.getAllCard().containsKey(cardId)) return active;
        return null;

    }

    public List<Card> popAllCard(Area area, int columnIndex, String uiCardId) throws Exception {
        List<Card> outputList = new ArrayList<>();
        for (Card card : getAreaCard(area))
            outputList.add(popCard(card.getId(), uiCardId));
        return outputList;
    }

    public void swapCardHolder(CardHolder cardHolder1, CardHolder cardHolder2, Area area1, Area area2) throws Exception {

        Card topCard1 = cardHolder1.pop(cardHolder1.getId());
        List<Card> cards1 = new ArrayList<>();
        for (Card card : cardHolder1.getAllCard().values()) {
            cards1.add(popCard(card.getId(), topCard1.getId()));
        }
        Card topCard2 = cardHolder2.pop(cardHolder2.getId());
        List<Card> cards2 = new ArrayList<>();
        for (Card card : cardHolder2.getAllCard().values()) {
            cards2.add(popCard(card.getId(), topCard2.getId()));
        }
        addCard(topCard1, area2, -1, "");
        for (Card card : cards1)
            addCard(card, area2, -1, topCard1.getId());

        addCard(topCard2, area1, -1, "");
        for (Card card : cards2)
            addCard(card, area1, -1, topCard2.getId());

    }
}
