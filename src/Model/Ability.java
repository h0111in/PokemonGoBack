package Model;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by H0111in on 05/22/2017.
 */
public class Ability {

    private String name;
    public List<Action> actionList;
    private boolean conditionalAmount;

    public Ability(String name, List<Action> actions) {
        actionList = new ArrayList<>();
        this.name = name;
        for (Action action : actions) {
            actionList.add(action);
        }
    }

    public int getActionsPower() throws ScriptException {
        int total = 0;
        for (Action action : actionList) {
            try {
                total += action.getPower().getAmount("");
            } catch (Exception e) {
                conditionalAmount = true;
            }
        }
        return total;
    }

    public String getActionsPowerText() throws ScriptException {
        int total = 0;
        for (Action action : actionList) {
            try {

                total += action.getPower().getAmount("");
            } catch (Exception e) {
                conditionalAmount = true;
            }
        }

        return (conditionalAmount ? "+" : "") + total;
    }

    public String getName() {
        return name;
    }
}
