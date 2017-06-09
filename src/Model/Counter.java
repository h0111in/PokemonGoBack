package Model;

import Enums.CardCategory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * Created by Hosein on 6/8/2017.
 */
public class Counter {

    private ActionTarget target;
    private CardCategory category;
    private String type;
    private String formula;

    public Counter( String formula) {
        this.formula = formula;
    }

    public Counter(ActionTarget target, CardCategory category, String type, String formula) {
        this(formula);
        this.target = target;
        this.category = category;
        this.type = type;
    }

    public int getAmount(String operand) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        return Integer.parseInt((String) engine.eval(operand + formula));
    }

    public ActionTarget getTarget() {
        return target;
    }

    public CardCategory getCategory() {
        return category;
    }


    public String getType() {
        return type;
    }
}
