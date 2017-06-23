package Parser;

import Enums.CardCategory;
import Model.*;
import Model.Abilities.Ability;
import Model.Abilities.ActionComposite;
import Model.Abilities.ActionFactory;
import Model.Abilities.IActionStrategy;
import javafx.scene.control.Alert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Controller.Helper.alert;
import static Controller.Main.logger;

/**
 * Created by hosein on 2017-06-20.
 */
public abstract class BaseParser implements IDataLoader {

    private String path;
    private String delimiter;
    private String abilitiesFileName;
    private String cardsFileName;

    public BaseParser(String directoryPath) {
        if (directoryPath.isEmpty())
            path = "./asset";
        else
            this.path = directoryPath;
        //initialize fields
        delimiter = ":";
        abilitiesFileName = "abilities.txt";
        cardsFileName = "cards.txt";
    }

    public List<Ability> getAbilities() throws Exception {

        List<Ability> abilities = new ArrayList<>();
        List<String> lines = readFile(path + "/" + abilitiesFileName);
        ActionFactory factory = new ActionFactory();
        for (int i1 = 0; i1 < lines.size(); i1++) {
            String line = lines.get(i1);
            String[] words = line.replace(",", delimiter).split(delimiter);

            if (!words[0].isEmpty())//name
            {
                String abilityName = words[0];
                List<IActionStrategy> actionList = new ArrayList<>();
                words = Arrays.copyOfRange(words, 1, words.length);
                for (; words.length > 0 && !words[0].isEmpty(); ) {
                    logger.info(line + "\r\n" + words[0]);
                    IActionStrategy action = factory.getAction(words[0]);
                    String[] newWords = action.parse(words);

                    if (newWords.length < words.length) {
                        words = newWords;
                        actionList.add(action);
                    } else alert(Alert.AlertType.ERROR, "incorrect format :" + line);
                }

                IActionStrategy action;
                if (actionList.size() == 1)
                    action = actionList.get(0);
                else {
                    action = factory.getAction("composite");
                    ((ActionComposite) action).addRange(actionList);
                }
                abilities.add(new Ability(abilityName, action));
            }

        }

        logger.info("*****************abilities finished*****************");
        return abilities;
    }

    public List<Card> getCards(List<Ability> abilities) {
        List<Card> cards = new ArrayList<>();
        CardFactory factory = new CardFactory();
        List<String> lines = readFile(path + "/" + cardsFileName);
        for (String str : lines) {

            String[] words = str.replace("cat:", "").replace(",", delimiter).split(":");
            if (words.length > 1) {
                Card card = factory.getCard(words[1]);
                card.parse(words, abilities);
                cards.add(card);
            } else if (words[0].equals("#")) cards.add(new PokemonCard());
        }
        logger.info("*****************cards finished*****************");

        return cards;
    }

    public static List<String> readFile(String filename) {
        List<String> records = new ArrayList<>();

        if (!new File(filename).exists() || new File(filename).isDirectory())
            alert(Alert.AlertType.WARNING, "Please make sure the following file is exist then press OK\n\r" +
                    filename.replace("./", System.getProperty("user.dir").replace("\\", "/") + "/"));
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
            reader.close();
            return records;
        } catch (Exception e) {
            alert(Alert.AlertType.ERROR, "System crashs while reading " + filename);
            System.err.format("Exception occurred trying to read '%s'.", filename);
            e.printStackTrace();
            return null;
        }
    }

    //region properties
    public String getAbilitiesFileName() {
        return abilitiesFileName;
    }

    public void setAbilitiesFileName(String abilitiesFileName) {
        this.abilitiesFileName = abilitiesFileName;
    }

    public String getCardsFileName() {
        return cardsFileName;
    }

    public void setCardsFileName(String cardsFileName) {
        this.cardsFileName = cardsFileName;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }
    //endregion
}
