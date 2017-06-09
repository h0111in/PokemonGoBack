package Model;

import Enums.Area;

/**
 * Created by H0111in on 05/22/2017.
 */
public class Action {
    private String name;
    private Area source;
    private Condition condition;
    private Action action1;
    private Action action2;
    private ActionTarget target;
    private ActionStatus status;

    private Counter power;
    private String filterType;

    public Action(String name, Condition condition, ActionTarget target, Counter power) {

        this.name = name;
        this.condition = condition;
        this.target = target;
        this.power = power;
    }

    /**
     * ApplyStat Constructor
     *
     * @param actionName
     * @param target
     * @param status
     */
    public Action(String actionName, ActionTarget target, ActionStatus status) {

        name = actionName;
        this.target = target;
        this.status = status;
    }

    public Action(String actionName, Condition condition, Action action1, Action action2) {

        name = actionName;
        this.condition = condition;
        this.action1 = action1;
        this.action2 = action2;
    }

    public Action(String actionName, ActionTarget target, Area source, String filterType) {
        name = actionName;
        this.source = source;
        this.condition = condition;
        this.target = target;

        this.filterType = filterType;
    }


    public String getName() {
        return name;
    }

    public Action setName(String name) {
        this.name = name;
        return this;
    }

    public Condition getCondition() {
        return condition;
    }

    public Action setCondition(Condition condition) {
        this.condition = condition;
        return this;
    }

    public ActionTarget getTarget() {
        return target;
    }

    public void setTarget(ActionTarget target) {
        this.target = target;
    }


    public ActionStatus getStatus() {
        return status;
    }


    public Counter getPower() {
        return power;
    }

    public Action getAction1() {
        return action1;
    }

    public Action getAction2() {
        return action2;
    }

    public Area getSource() {
        return source;
    }
}
