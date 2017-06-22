package Parser;

import Enums.Player;
import Model.Card;
import Parser.BaseParser;

import java.util.List;

/**
 * Created by hosein on 2017-06-20.
 */
public class TextParser extends BaseParser {

    public TextParser(String directoryPath) {
        super(directoryPath);
    }

    @Override
    public List<Integer> getDeckIndexes(Player player) {
        return null;
    }
}
