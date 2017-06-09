package Model;

import Enums.CardCategory;

/**
 * Created by H0111in on 05/20/2017.
 */
public class TrainerCard implements Card  {
    private String id;
    private String name;
    private CardCategory category;
    private String type;
    private Attack attack;

    public TrainerCard(String name, CardCategory category, String type, Attack attack) {
        this.id = "";
        this.name = name;
        this.category = category;
        this.type = type;
        this.attack=attack;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Card setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public CardCategory getCategory() {
        return category;
    }

    @Override
    public Card setCategory(CardCategory category) {
        this.category=category;
        return this;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public Card setType(String type) {
        this.type=type;
        return this;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Card setId(String id) {
        this.id=id;
        return this;
    }

    public Card clone() throws CloneNotSupportedException {
        return (TrainerCard) super.clone();
    }

    public Attack getAttack() {
        return attack;
    }
}