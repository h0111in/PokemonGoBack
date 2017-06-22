package Model.Abilities;

import java.awt.*;

/**
 * Created by H0111in on 2017-06-20.
 */
public class ActionFactory {
    public IActionStrategy getAction(String name) {
        String[] names = name.replace("(", ")").split("\\)");
        for (String name1 : names) {
            name = name1;
            switch (name.replace("(", "")) {
                case "dam":
                    return new Damage();
                case "heal":
                    return new Heal();
                case "reenergize":
                    return new ReEnergize();
                case "deenergize":
                    return new DeEnergize();
                case "destat":
                    return new DeState();
                case "applystat":
                    return new ApplyState();
                case "shuffle":
                    return new Shuffle();
                case "redamage":
                    return new ReDamage();
                case "draw":
                    return new Draw();
                case "swap":
                    return new Swap();
                case "add":
                    return new Add();
                case "cond":
                    return new Cond();
                case "deck":
                    return new Deck();
                case "search":
                    return new Search();
                case "composite":
                    return new ActionComposite();
                //conditional actions
                case "flip":
                    return new Flip();
                case "healed":
                    return new Healed();
                case "ability":
                    return new AbilityCondition();
                case "count":
                    return new Counter();
                case "choice":
                    return new Model.Abilities.Choice();
            }
        }
        return null;
    }
}
