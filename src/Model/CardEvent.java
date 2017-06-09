package Model;

import Enums.*;

import java.util.EventObject;

public class CardEvent extends EventObject {
    private Card card;
    private Area area;
    private Enums.Player playerName;
    private int cardHolderIndex;//columnIndex in grid
    private String cardHolderId;//SmallCardID

    public CardEvent(Card card, Area area, Enums.Player playerName) {
        super(card);
        setCard(card);
        cardHolderId="";
        this.area = area;
        this.playerName = playerName;
    }

    public CardEvent(Card card, Area area, Enums.Player playerName, int cardHolderIndex, String cardHolderId) {
        this(card, area, playerName);
        this.cardHolderIndex = cardHolderIndex;
        this.cardHolderId=cardHolderId;
    }

    public Enums.Player getPlayerName() {
        return playerName;
    }

    public Card getCard() {
        return card;
    }
    public CardEvent setCard(Card card) {
        this.card=card;
        return this;
    }

    public Area getArea() {
        return area;
    }

    public int getCardHolderIndex() {
        return cardHolderIndex;
    }

    public String getSmallCardId() {
        return cardHolderId;
    }
}
