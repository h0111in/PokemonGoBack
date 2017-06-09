package Model;

/**
 * Created by H0111in on 05/22/2017.
 */
public class Attack {

    private String costType;
    private int costAmount;
    private Ability ability;

    public Attack (Ability ability, String costType,int costAmount){

        this.ability=ability;
        this.costAmount=costAmount;
        this.costType=costType;

    }

    public String getCostType() {
        return costType;
    }

    public int getCostAmount() {
        return costAmount;
    }

    public Ability getAbility() {
        return ability;
    }

}
