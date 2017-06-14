package Controller;

import Enums.Area;
import Enums.Coin;
import Enums.Player;
import Model.*;
import View.MessageDialog;
import View.SelectorDialog;
import View.SmallCard;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static Controller.Main.logger;

public class GameBoardController extends GridPane {

    //region fields
    private Stage primaryStage;
    private uiCardEvent cardEventHandler;
    private final EventListenerList listenerList;
    Map<Player, Boolean> players;
    //endregion

    //region constructor
    public GameBoardController(Map<Player, Boolean> players, Stage primaryStage) throws URISyntaxException {

        this.primaryStage = primaryStage;
        listenerList = new EventListenerList();
        this.players = players;

        //region Initialize essential GUI properties
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GameBoardView.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.getStylesheets().add("./asset/gameBoard.css");

        //endregion

        //initialize Players
        for (Enums.Player player : players.keySet()) {

            //attach GUI Events
            for (int i = 0; i < 5; i++) {
                this.lookup("#" + Area.bench + player + i).setOnDragOver(dragHandler);
                this.lookup("#" + Area.bench + player + i).setOnDragDropped(dropHandler);
            }
            this.lookup("#" + Area.active + player).setOnDragOver(dragHandler);
            this.lookup("#" + Area.active + player).setOnDragDropped(dropHandler);
            this.lookup("#" + Area.hand + player).setOnDragOver(dragHandler);
            this.lookup("#" + Area.hand + player).setOnDragDropped(dropHandler);

            //coin image
            ((ImageView) this.lookup("#coin" + player))
                    .setImage(new Image(getClass().getResource("/asset/coin-symbol3.png").toURI().toString()));

        }
        this.lookup("#doneA").setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    fireDoneButton(Player.A);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info(e.toString());
                    //  alert(Alert.AlertType.INFORMATION,e.toString());

                }
            }
        });

    }
    //endregion

    //region EventHandlers
    public PlayerEventListener playerEventListener = new PlayerEventListener() {
        @Override
        public void pushCard(CardEvent evt) {

            SmallCard smallCard = null;
            try {

                if (evt.getSmallCardId().length() == 0) {//push to area

                    //region make new UIControls
                    smallCard = new SmallCard(evt.getCard(), primaryStage);
                    smallCard.setOnDragOver(dragHandler);
                    smallCard.setOnDragDropped(dropHandler);
                    smallCard.addListener(cardEventHandler);
                    smallCard.addListener(new uiCardEvent() {
                        @Override
                        public void attackRequest(Player playerName, String cardId, int attackIndex) throws Exception {

                        }

                        @Override
                        public void applyTrainerCardRequest(Player playerName, String cardId) throws Exception {

                        }

                        @Override
                        public boolean showFaceRequest(Player playerName, String cardId) {
                            return false;
                        }

                        @Override
                        public void cardClicked(String cardId) {
                            Node node = lookup("#" + cardId);
                            if (node != null && node.getParent() != null) {
                                try {
                                    fireShowAreaCard(getAreaName(node.getParent().getId()), evt.getPlayerName());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    smallCard.showFace();
                    //endregion

                    //region push card
                    switch (evt.getArea()) {

                        case deck:
                            ((StackPane) GameBoardController.this.lookup("#" + evt.getArea() + evt.getPlayerName())).getChildren().add(smallCard);
                            break;
                        case hand:
                            ((HBox) GameBoardController.this.lookup("#" + evt.getArea() + evt.getPlayerName())).getChildren().add(smallCard);
                            //to do... relocate cards
                            break;
                        case bench:
                            if (evt.getCardHolderIndex() >= 0)
                                ((Pane) GameBoardController.this.lookup("#" + evt.getArea() + evt.getPlayerName() + String.valueOf(evt.getCardHolderIndex()))).getChildren().add(smallCard);

                            break;
                        case active:
                            ((Pane) GameBoardController.this.lookup("#" + evt.getArea() + evt.getPlayerName())).getChildren().add(smallCard);
                            break;
                        case prize:
                            ((FlowPane) GameBoardController.this.lookup("#" + evt.getArea() + evt.getPlayerName())).getChildren().add(smallCard);
                            break;
                        case discard:
                            ((Pane) GameBoardController.this.lookup("#" + evt.getArea() + evt.getPlayerName())).getChildren().add(smallCard);
                            break;
                    }
                    //endregion

                } else
                    //region push into another UIControls
                    ((SmallCard) GameBoardController.this.lookup("#" + evt.getSmallCardId())).push(evt.getCard());//endregion

                //region update card list size in UI
                if (evt.getArea() == Area.hand || evt.getArea() == Area.discard || evt.getArea() == Area.active || evt.getArea() == Area.deck)
                    ((Label) GameBoardController.this.lookup("#" + evt.getArea() + "Size" + evt.getPlayerName())).
                            setText(String.valueOf(fireGetAreaSize(evt.getPlayerName(), evt.getArea())));
                //endregion

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void popCard(CardEvent evt) throws Exception {

            Pane area = null;

            //region update size
            if (evt.getArea() == Area.hand || evt.getArea() == Area.discard || evt.getArea() == Area.active || evt.getArea() == Area.deck)
                ((Label) GameBoardController.this.lookup("#" + evt.getArea() + "Size" + evt.getPlayerName())).
                        setText(String.valueOf(fireGetAreaSize(evt.getPlayerName(), evt.getArea())));
            //endregion

            //region get target area
            if (evt.getArea() == Area.bench)
                area = (Pane) GameBoardController.this.lookup("#" + evt.getArea() + evt.getPlayerName() + evt.getCardHolderIndex());
            else
                area = (Pane) GameBoardController.this.lookup("#" + evt.getArea() + evt.getPlayerName());
            //endregion

            if (evt.getSmallCardId().length() != 0)
            //region pop from another card
            {

                // pop card
                ((SmallCard) area.lookup("#" + evt.getSmallCardId())).pop(evt.getCard().getId());

                //pop cardHolder
                if (((SmallCard) area.lookup("#" + evt.getSmallCardId())).getAllCard().size() == 0)
                    (area).getChildren().remove(area.lookup("#" + evt.getSmallCardId()));

            }  //endregion
            else
                //region pop from area
                (area).getChildren().remove(
                        area.lookup("#" + evt.getCard().getId()));
            //endregion

        }
    };


    public LogicEventListener logicEventListener = new LogicEventListener() {
        @Override
        public void showMessage(String message, double duration) {

            popup(new MessageDialog(message, duration, Color.GRAY), primaryStage);

        }

        @Override
        public boolean flipCoin(Coin defaultFace, double waitForFlipping) throws URISyntaxException {
            return GameBoardController.this.flipCoin(defaultFace,
                    waitForFlipping, primaryStage);
        }

        @Override
        public List<String> selectCardRequest(String message, int totalRequired, List<Card> cardList, boolean showCard) throws Exception {

            return selectCard(message, totalRequired, cardList, showCard, primaryStage);
        }
    };

    private static List<String> selectCard(String message, int totalRequired, List<Card> cardList, boolean showCard, Stage primaryStage) throws Exception {
        SelectorDialog selectorDialog = new SelectorDialog(cardList, message, totalRequired, showCard, primaryStage);

        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene(selectorDialog));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setIconified(false);
        stage.initOwner(primaryStage);
        stage.showAndWait();
        return selectorDialog.getSelectedCards();
    }

    private EventHandler dragHandler = new EventHandler<DragEvent>() {

        @Override
        public void handle(DragEvent event) {

            if (event.getGestureSource() != (event.getTarget()) && event.getDragboard().hasString()) {

                //////////////////////
                //Validate Piking UIControls
                //////////////////////

                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }

            event.consume();
        }
    };

    private EventHandler dropHandler = new EventHandler<DragEvent>() {

        public void handle(DragEvent event) {
            String uiSmallCardId = "";
            Area targetArea;
            SmallCard targetCard = null;

            try {
                if (((Pane) event.getSource()).getId() != ((GridPane) event.getGestureSource()).getParent().getId() && event.getGestureSource() != (event.getTarget()) &&
                        event.getDragboard().hasString()) {

                    String cardID = (event.getDragboard().getContent(DataFormat.PLAIN_TEXT).toString());
                    GridPane source = ((GridPane) event.getGestureSource());
                    SmallCard flyCard = (SmallCard) GameBoardController.this.lookup("#" + cardID);
                    Area sourceArea = getAreaName(source.getParent().getId());

                    if (event.getGestureTarget() instanceof SmallCard) {
                        targetArea = getAreaName((((SmallCard) event.getGestureTarget()).getParent()).getId());
                        targetCard = (SmallCard) event.getGestureTarget();
                        uiSmallCardId = ((SmallCard) event.getGestureTarget()).getId();
                    } else
                        targetArea = getAreaName(((Pane) event.getGestureTarget()).getId());

                    int sourceColumnIndex = -1;
                    if (sourceArea == Area.bench)
                        sourceColumnIndex = Integer.parseInt(source.getParent().getId().substring(source.getParent().getId().length() - 1));

                    int targetColumnIndex = -1;
                    if (targetArea == Area.bench)
                        targetColumnIndex = Integer.parseInt(((Pane) event.getGestureTarget()).getId().substring(((Pane) event.getGestureTarget()).getId().length() - 1));

                    List<Card> uiCardList = flyCard.getAllCard();
                    //continue on Logic
                    fireMoveCard(targetArea, targetCard != null ? targetCard.getMainCard() : null, targetCard != null ? targetCard.getStageCard() : null,
                            flyCard != null ? flyCard.getMainCard() : null, sourceArea, flyCard != null ? flyCard.getPlayerName() : null,
                            uiCardList, targetColumnIndex, sourceColumnIndex, cardID, uiSmallCardId);

                }

                event.setDropCompleted(true);

                event.consume();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    //endregion

    //region Events
    public void addListener(BoardEventListener listener) {
        listenerList.add(BoardEventListener.class, listener);
    }

    void fireMoveCard(Area targetArea, Card targetCard, Card targetStageCard, Card flyCard, Area sourceArea, Enums.Player senderPlayer,
                      List<Card> uiCardList, int targetColumnIndex, int sourceColumnIndex, String uiCardId, String uiSmallCardId) throws Exception {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == BoardEventListener.class) {
                ((BoardEventListener) listeners[i + 1]).MoveCard(targetArea, targetCard, targetStageCard, flyCard, sourceArea, senderPlayer,
                        uiCardList, targetColumnIndex, sourceColumnIndex, uiCardId, uiSmallCardId);
            }
        }
    }

    int fireGetAreaSize(Enums.Player player, Area area) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == BoardEventListener.class) {
                return ((BoardEventListener) listeners[i + 1]).getAreaSize(player, area);
            }
        }
        return 0;
    }

    void fireDoneButton(Player player) throws Exception {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == BoardEventListener.class) {
                ((BoardEventListener) listeners[i + 1]).doneButtonPressed(player);
            }
        }

    }

    private void fireShowAreaCard(Area areaName, Player playerName) throws Exception {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == BoardEventListener.class) {
                ((BoardEventListener) listeners[i + 1]).showAreaCard(areaName, playerName);
            }
        }
    }
//endregion


    private Area getAreaName(String nodeID) {
        for (Enums.Player name : players.keySet()) {
            if (nodeID.replaceAll("\\d*$", "").endsWith(name.name()))
                return Enums.Area.valueOf(nodeID.substring(0, nodeID.indexOf(name.name())));
        }
        return Area.none;
    }


    public static void popup(Pane dialog, Stage primaryStage) {

        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene(dialog));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setIconified(false);
        stage.initOwner(primaryStage);
        stage.showAndWait();
    }

    public boolean flipCoin(Coin defaultFace, double waitForFlipping, Stage primaryStage) throws URISyntaxException {
        CoinDialog coinDialog = new CoinDialog(defaultFace, waitForFlipping);
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene(coinDialog));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.initOwner(primaryStage);
        stage.showAndWait();
        return coinDialog.result;
    }


    public void setCardEventHandler(uiCardEvent cardEventHandler) {
        this.cardEventHandler = cardEventHandler;
    }
}
