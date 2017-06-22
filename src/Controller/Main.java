package Controller;

import Enums.Area;
import Enums.Player;
import Model.MetaData;
import Parser.IDataLoader;
import Parser.TextParser;
import View.GameBoardController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import static Controller.Helper.alert;


public class Main extends Application {
    GameBoardController ui;
    public static Logger logger = Logger.getLogger("MyLog");
    private LogicController logic;

    @Override
    public void start(Stage primaryStage) throws CloneNotSupportedException, IOException {
        try {

            //region variables
            Map<Player, Boolean> playerNames = new HashMap<>();
            playerNames.put(Player.A, false);
            playerNames.put(Player.B, true);
            //endregion

            //load data
            TextParser parser = new TextParser("");

            MetaData metaData = new MetaData(parser);

            //UI Controller
            ui = new GameBoardController(playerNames, primaryStage);

            //Logic Controller
            logic = new LogicController(playerNames);

            //Connect LogicController and Board
            ui.setCardEventHandler(logic.uiCardEventListener);
            ui.addListener(logic.uiBoardEventListener);

            logic.addPlayerEventListener(ui.playerEventListener);
            logic.addListener(ui.logicEventListener);

            //Load UI
            primaryStage.setScene(new Scene(ui));
            primaryStage.show();

            //Load deckA
            for (String index : parser.readFile("./asset/deck1.txt"))
                logic.players.get(Player.A).addCard(metaData.getCard(Integer.parseInt(index) - 1), Area.deck, "");

            //Load deckB
            for (String index : parser.readFile("./asset/deck2.txt"))
                logic.players.get(Player.B).addCard(metaData.getCard(Integer.parseInt(index) - 1), Area.deck, "");

            logic.startGame();

        } catch (Exception e) {
            e.printStackTrace();
            alert(Alert.AlertType.ERROR, e.getMessage());
        }

    }

    public static void main(String[] args) {


        try {
            //region  config logger
            FileHandler fh = new FileHandler(System.getProperty("user.dir") + "/MyLogFile.log");
            logger.addHandler(fh);
            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
            //endregion

            launch(args);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
