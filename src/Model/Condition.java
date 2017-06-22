package Model;

import Enums.ActionTarget;
import Model.Abilities.Counter;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by Hosein on 6/8/2017.
 */
public class Condition {
    private String name;
    private ActionTarget target;
    private Counter counter;
    private String formula;

    public Condition(String name, ActionTarget target, Counter counter, String formula) {
        this.name = name;

        this.target = target;
        this.counter = counter;
        this.formula = formula;
    }

    public int getAmount(String operand) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        return Integer.parseInt((String) engine.eval(operand + formula));
    }

    public String getName() {
        return name;
    }

    public ActionTarget getTarget() {
        return target;
    }

    public Counter getCounter() {
        return counter;
    }

    public String getFormula() {
        return formula;
    }
}
