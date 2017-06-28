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
    List<Card> hand;//id-UIControls
    List<Card> deck;
    List<Card> prize;
    CardHolder active;
    List<Card> discard;
    private EventListenerList listenerList;

    public Player(Enums.Player name, boolean isComputer) {
        this.isComputer = isComputer;

        this.name = name;
        listenerList = new EventListenerList();
        bench = new ArrayList<CardHolder>();
        for (int i = 0; i < 5; i++)
            bench.add(new CardHolder());
        hand = new ArrayList<>();
        deck = new ArrayList<>();
        prize = new ArrayList<>();
        active = new CardHolder();
        discard = new ArrayList<>();

    }


    public Area getCardArea(String cardId) {
        for (Card card : deck)
            if (card.getId().equals(cardId))
                return Area.deck;
        for (Card card : hand)
            if (card.getId().equals(cardId))
                return Area.hand;
        for (Card card : prize)
            if (card.getId().equals(cardId))
                return Area.prize;
        for (Card card : discard)
            if (card.getId().equals(cardId))
                return Area.discard;
        for (Card card : deck)
            if (card.getId().equals(cardId))
                return Area.deck;
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

    public Player addCard(Card card, Area destination, String smallCardId) throws Exception {
        //logger.info(card.getName());
        this.addCard(card, destination, -1, smallCardId, false);
        return this;
    }

    public Player addCard(Card card, Area destination, int cardHolderIndex, String cardHolderId, boolean addtoBottom) throws Exception {

        switch (destination) {
            case deck:
                if (card.getId().equals("")) {
                    card.setId(name.name() + deck.size());
                    card.setPlayerName(name);
                    logger.info(card.getId() + " Area : " + destination + " columnIndex: " + cardHolderIndex + " uiHolderId: " + cardHolderId);

                }
                if (addtoBottom) deck = addTopMap(card, deck);
                else
                    deck.add(card);
                break;
            case hand:
                if (addtoBottom) hand = addTopMap(card, hand);
                else
                    hand.add(card);
                break;
            case bench:
                if (cardHolderIndex == -1) {

                    for (int i = 0; i < bench.size(); i++) {
                        if (!cardHolderId.isEmpty()) {
                            if (cardHolderId.equals(bench.get(i).getId())) {
                                cardHolderIndex = i;
                                break;
                            }
                        } else if (bench.get(i).getAllCard().size() == 0) {
                            cardHolderIndex = i;
                            break;
                        }
                    }
                }
                if (cardHolderIndex == -1) {
                    for (CardHolder cardHolder : bench) {
                        logger.info("cardHolder.getId()=> " + cardHolder.getId());
                        for (Card innerCard : cardHolder.getAllCard().values()) {
                            logger.info("    ->" + innerCard.getName() + " " + innerCard.getCategory() + " " + innerCard.getId());
                        }
                    }
                    logger.info("column Index is not found=>" + card.getId());

                    throw new Exception("Bench is full");
                }
                logger.info("card " + card.getName() + " add to column :" + cardHolderIndex);
                bench.get(cardHolderIndex).add(card);
                break;
            case active:
                active.add(card);
                break;
            case prize:
                if (addtoBottom) prize = addTopMap(card, prize);
                else
                    prize.add(card);
                break;
            case discard:
                if (addtoBottom) discard = addTopMap(card, discard);
                else discard.add(card);
                break;
        }
        fireAddCard(new CardEvent(card, destination, getName(), cardHolderIndex, cardHolderId));
        return this;
    }


    private List<Card> addTopMap(Card card, List<Card> map) {

        map.add(0, card);
        return map;
//        Map<String, Card> temp = new HashMap<>();
//        temp.put(card.getId(), card);
//        for (Card card1 : map.values())
//            temp.put(card1.getId(), card1);
//        return temp;

    }

    public List<Card> drawCard(int amount, Area area) throws Exception {
        List<Card> cardList = new ArrayList<>();

        while (amount-- > 0) {
            switch (area) {

                case deck:

                    if (deck.size() > 0) {
//                        List<String> keys = new ArrayList<>(deck.keySet());
//                        String randomKey = keys.get(keys.size() - 1);
                        Card card = deck.get(0);
                        logger.info(name + " draws " + card.getName() + " " + card.getCategory());
                        cardList.add(card);
                        deck.remove(0);
                        fireRemoveCard(new CardEvent(card, area, getName(), -1, ""));
                    }
                    break;
                case hand:
                    if (hand.size() > 0) {

//                        List<String> keys = new ArrayList<>(hand.keySet());
//                        String randomKey = keys.get(keys.size() - 1);
                        Card card = hand.get(0);
                        cardList.add(card);
                        hand.remove(0);

                        fireRemoveCard(new CardEvent(card, area, getName(), -1, ""));
                    }
                    break;
                case bench:
                    throw new Exception("Wrong Request");
                case active:
                    throw new Exception("Wrong Request");
                case prize:

                    if (prize.size() > 0) {

//                        List<String> keys = new ArrayList<>(prize.keySet());
//                        String randomKey = keys.get(keys.size() - 1);
                        Card card = prize.get(0);
                        cardList.add(card);
                        prize.remove(0);

                        fireRemoveCard(new CardEvent(card, area, getName(), -1, ""));
                    }
                    break;
                case discard:

                    if (deck.size() > 0) {

                        Card card = discard.get(0);
                        cardList.add(card);
                        discard.remove(0);
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
                return getCard(id, deck);
            case hand:
                return getCard(id, hand);
            case bench:
                for (int i = 0; i < bench.size(); i++) {
                    CardHolder cardHolder = bench.get(i);
                    if (cardHolder.getAllCard().containsKey(id)) {
                        card = cardHolder.getCard(id);

                        break;
                    }
                }
                break;
            case active:
                card = active.getCard(id);
                break;
            case prize:
                return getCard(id, prize);
            case discard:
                return getCard(id, discard);
        }
        return card;
    }

    private Card getCard(String id, List<Card> cardList) {
        for (Card tempCard : cardList)
            if (tempCard.getId().equals(id))
                return tempCard;
        return null;
    }

    private int getIndex(String id, List<Card> cardList) {
        for (int i = 0; i < cardList.size(); i++) {
            Card card = cardList.get(i);
            if (card.getId().equals(id))
                return i;
        }
        return -1;
    }

    private boolean removeCard(String id, Area area) throws Exception {
        switch (area) {

            case deck:
                int index = getIndex(id, deck);
                if (index > -1) {
                    deck.remove(index);
                    return true;
                }
                return false;
            case hand:
                index = getIndex(id, hand);
                if (index > -1) {
                    hand.remove(index);
                    return true;
                }
                return false;

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
                index = getIndex(id, prize);
                if (index > -1) {
                    prize.remove(index);
                    return true;
                }
                return false;

            case discard:
                index = getIndex(id, discard);
                if (index > -1) {
                    discard.remove(index);
                    return true;
                }
                return false;

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
        logger.info(id + " Area : " + area + " columnIndex: " + cardHolderIndex + " uiHolderId: " + cardHolderId);
        if (cardHolderIndex == -1 && area == Area.bench)
            for (int i = 0; i < bench.size(); i++) {
                CardHolder cardHolder = bench.get(i);
                if (cardHolder.getAllCard().containsKey(id)) {
                    cardHolderIndex = i;
                    if (cardHolderId.isEmpty() && !id.equals(cardHolder.getId()))
                        cardHolderId = cardHolder.getId();
                    break;
                }
            }


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
                return new ArrayList<>(deck);
            case hand:
                return new ArrayList<>(hand);
            case bench:
                List<Card> list = new ArrayList<>();
                for (CardHolder cardHolder : bench)
                    if (cardHolder.getAllCard().size() != 0)
                        list.add(cardHolder.getTopCard());
                return list;
            case active:
                return new ArrayList<>(getActiveCard().getAllCard().values());
            case prize:
                logger.info(String.valueOf(prize.size()));
                return new ArrayList<>(prize);
            case discard:
                return new ArrayList<>(discard);
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


        List<Card> cards1 = new ArrayList<>();
        Card topCard1 = null;
        if (cardHolder1 != null) {
            for (Card card : cardHolder1.getAllCard().values()) {
                if (!cardHolder1.getId().equals(card.getId()))
                    cards1.add(popCard(card.getId(), cardHolder1.getId()));
            }
            topCard1 = popCard(cardHolder1.getId(), "");
        }
        if (cardHolder2 != null) {
            List<Card> cards2 = new ArrayList<>();
            for (Card card : cardHolder2.getAllCard().values()) {
                if (!cardHolder2.getId().equals(card.getId()))
                    cards2.add(popCard(card.getId(), cardHolder2.getId()));
            }

            Card topCard2 = popCard(cardHolder2.getId(), "");
            addCard(topCard2, area1, -1, "");
            for (Card card : cards2)
                addCard(card, area1, -1, topCard2.getId());
        }
        if (cardHolder1 != null) {
            addCard(topCard1, area2, -1, "");
            for (Card card : cards1)
                addCard(card, area2, -1, topCard1.getId());
        }

    }
}
