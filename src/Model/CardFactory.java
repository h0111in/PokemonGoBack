package Model;

/**
 * Created by H0111in on 2017-06-20.
 */
public class CardFactory {
   public Card getCard(String type) {
        switch (type) {
            case "pokemon":
                return new PokemonCard();
            case "trainer":
                return new TrainerCard();
            case "energy":
                return new EnergyCard();
            default:
                return null;
        }
    }
}
