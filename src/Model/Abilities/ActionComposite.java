package Model.Abilities;

import Model.Player;

import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by H0111in on 2017-06-20.
 */
public class ActionComposite extends BaseAction implements IActionStrategy {
    private List<IActionStrategy> actions;

    public ActionComposite() {
        actions = new ArrayList<>();
    }

    public void addRange(List<IActionStrategy> actionList) {
        for (IActionStrategy actionStrategy : actionList)
            actions.add(actionStrategy);
    }


    @Override
    public String toString() {
        String description = "";
        for (IActionStrategy strategy : actions)
            description += strategy.toString();
        return description;
    }

    @Override
    public int getTotalEffectivePower() throws ScriptException {
        int total = 0;
        for (IActionStrategy actionStrategy : actions)
            total += actionStrategy.getTotalEffectivePower();
        return total;
    }

    @Override
    public String getActionsPowerText() throws ScriptException {

        int total = 0;
        boolean conditionalAmount = false;
        for (IActionStrategy action : actions) {
            try {
                int actionPower = action.getTotalEffectivePower();

                total += actionPower;
                if (actionPower == 0)
                    conditionalAmount = true;
            } catch (Exception e) {
                conditionalAmount = true;
            }
        }

        return (conditionalAmount ? "+" : "") + total;
    }

    @Override
    public boolean fight(Player player, Player opponent) throws Exception {
        for (IActionStrategy actionStrategy : actions)
            actionStrategy.fight(player, opponent);
        return true;
    }

    @Override
    public String[] parse(String[] words) throws Exception {
        return words;
    }
}
