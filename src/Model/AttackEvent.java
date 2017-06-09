package Model;


import java.util.EventObject;

public class AttackEvent extends EventObject {

    private String cardId;
    private int attackIndex;

    public AttackEvent(String cardId, int attackIndex) {
        super(cardId);

        this.cardId = cardId;
        this.attackIndex = attackIndex;
    }


    public String getCardId() {
        return cardId;
    }

    public int getAttackIndex() {
        return attackIndex;
    }
}
