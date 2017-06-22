package Model.Abilities;

import javax.script.ScriptException;

/**
 * Created by H0111in on 05/22/2017.
 */
public class Ability {

    private String name;
    public IActionStrategy action;
    private boolean conditionalAmount;


    public Ability(String name, IActionStrategy action) throws Exception {

        this.name = name;
        this.action = action;

    }

    public int getActionsPower() throws ScriptException {
        return action.getTotalEffectivePower();


    }

    public String getActionsPowerText() throws ScriptException {
        return action.getActionsPowerText();
    }

    public String getName() {
        return name;
    }

}
