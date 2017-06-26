package View;

import Enums.Area;
import Enums.Coin;
import Enums.Player;
import Listeners.uiBoardEventListener;
import Listeners.LogicEventListener;
import Listeners.PlayerEventListener;
import Listeners.uiCardEventListener;
import Model.*;
import Model.Abilities.IActionStrategy;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private uiCardEventListener cardEventHandler;
    private final EventListenerList listenerList;
    Map<Player, Boolean> players;

    @FXML
    private HBox handA;
    //endregion

    //region constructor
    public GameBoardController(Map<Player, Boolean> players, Stage primaryStage) throws URISyntaxException {

        this.primaryStage = primaryStage;
        listenerList = new EventListenerList();
        this.players = players;

        //region Initialize essential GUI properties
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GameBoard.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        this.getStylesheets().add("asset/gameBoard.css");


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

            SmallCardController smallCard = null;
            try {

                if (evt.getSmallCardId().length() == 0) {//push to area

                    //region make new UIControls
                    smallCard = new SmallCardController(evt.getCard(), primaryStage);
                    smallCard.setOnDragOver(dragHandler);
                    smallCard.setOnDragDropped(dropHandler);
                    smallCard.addListener(cardEventHandler);
                    smallCard.addListener(new uiCardEventListener() {
                        @Override
                        public void attackRequest(Player playerName, String cardId, int attackIndex) throws Exception {

                        }

                        @Override
                        public boolean showFaceRequest(Player playerName, String cardId) {
                            return false;
                        }

                        @Override
                        public void cardClicked(String cardId) {
                            logger.info("sender" + cardId + " evt :" + evt.getCard().getId());
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
                    logger.info("evt :Area " + evt.getArea() + " - ID: " + evt.getCard().getId() + " - smallCardId: " + evt.getSmallCardId() + " - columnIndex:" + evt.getCardHolderIndex());
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
                {
                    logger.info("evt :Area " + evt.getArea() + " - ID: " + evt.getCard().getId() + " - smallCardId: " + evt.getSmallCardId() + " - columnIndex:" + evt.getCardHolderIndex());

                    ((SmallCardController) GameBoardController.this.lookup("#" + evt.getSmallCardId())).push(evt.getCard());//endregion

                }

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
            logger.info("evt :Area " + evt.getArea() + " - ID: " + evt.getCard().getId() + " - smallCardId: " + evt.getSmallCardId() + " - columnIndex:" + evt.getCardHolderIndex());

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
                ((SmallCardController) area.lookup("#" + evt.getSmallCardId())).pop(evt.getCard().getId());

                //pop cardHolder
                if (((SmallCardController) area.lookup("#" + evt.getSmallCardId())).getAllCard().size() == 0)
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
        public Boolean showMessage(Alert.AlertType alertType, String message, double duration) {

            return popup(new MessageDialogController(alertType, message, duration, Color.GRAY), primaryStage);

        }

        @Override
        public boolean flipCoin(Coin defaultFace, double waitForFlipping) throws URISyntaxException {
            logger.info("flipCoin - flipping time:" + waitForFlipping);
            return GameBoardController.this.flipCoin(defaultFace,
                    waitForFlipping, primaryStage);
        }

        @Override
        public List<String> selectCardRequest(String message, int totalRequired, List<Card> cardList, boolean showCard) throws Exception {

            return selectCard(message, totalRequired, cardList, showCard, primaryStage);
        }

        @Override
        public boolean actionRequest(Player playerName, IActionStrategy action) throws Exception {
            return false;
        }
    };

    private static List<String> selectCard(String message, int totalRequired, List<Card> cardList, boolean showCard, Stage primaryStage) throws Exception {
        SelectorDialogController selectorDialog = new SelectorDialogController(cardList, message, totalRequired, showCard, primaryStage);

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
            SmallCardController targetCard = null;

            try {
                if (((Pane) event.getSource()).getId() != ((GridPane) event.getGestureSource()).getParent().getId() && event.getGestureSource() != (event.getTarget()) &&
                        event.getDragboard().hasString()) {

                    String cardID = (event.getDragboard().getContent(DataFormat.PLAIN_TEXT).toString());
                    GridPane source = ((GridPane) event.getGestureSource());
                    SmallCardController flyCard = (SmallCardController) GameBoardController.this.lookup("#" + cardID);
                    Area sourceArea = getAreaName(source.getParent().getId());

                    if (event.getGestureTarget() instanceof SmallCardController) {
                        targetArea = getAreaName((((SmallCardController) event.getGestureTarget()).getParent()).getId());
                        targetCard = (SmallCardController) event.getGestureTarget();
                        uiSmallCardId = ((SmallCardController) event.getGestureTarget()).getId();
                    } else
                        targetArea = getAreaName(((Pane) event.getGestureTarget()).getId());

                    int sourceColumnIndex = -1;
                    if (sourceArea == Area.bench)
                        sourceColumnIndex = Integer.parseInt(source.getParent().getId().substring(source.getParent().getId().length() - 1));

                    int targetColumnIndex = -1;
                    if (targetArea == Area.bench) {
                        String area = "";
                        if (event.getGestureTarget() instanceof SmallCardController)
                            area = lookup("#" + ((SmallCardController) event.getGestureTarget()).getId()).getParent().getId();
                        else area = ((Pane) event.getGestureTarget()).getId();
                        targetColumnIndex = Integer.parseInt(area.replaceAll("\\D+", ""));
                        logger.info("area" + area);
                    }
                    List<Card> uiCardList = flyCard.getAllCard();
                    //continue on Logic
                    logger.info(targetArea.name());
                    if (targetCard != null)
                        logger.info(" " + targetCard.getId() + " ");
                    logger.info(flyCard.getPlayerName().name());
                    logger.info("target-source indext: " + targetArea + targetColumnIndex + " " + sourceArea + " " + sourceColumnIndex + " ");
                    logger.info("cardID:" + cardID + " " + uiSmallCardId);
                    fireMoveCard(targetArea, targetCard != null ? targetCard.getTopCard() : null, targetCard != null ? targetCard.getStageCard() : null,
                            flyCard != null ? flyCard.getTopCard() : null, sourceArea, flyCard != null ? flyCard.getPlayerName() : null,
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
    public void addListener(uiBoardEventListener listener) {
        listenerList.add(uiBoardEventListener.class, listener);
    }

    void fireMoveCard(Area targetArea, Card targetCard, Card targetStageCard, Card flyCard, Area sourceArea, Enums.Player senderPlayer,
                      List<Card> uiCardList, int targetColumnIndex, int sourceColumnIndex, String uiCardId, String uiSmallCardId) throws Exception {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == uiBoardEventListener.class) {
                ((uiBoardEventListener) listeners[i + 1]).MoveCard(targetArea, targetCard, targetStageCard, flyCard, sourceArea, senderPlayer,
                        uiCardList, targetColumnIndex, sourceColumnIndex, uiCardId, uiSmallCardId);
            }
        }
    }

    int fireGetAreaSize(Enums.Player player, Area area) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == uiBoardEventListener.class) {
                return ((uiBoardEventListener) listeners[i + 1]).getAreaSize(player, area);
            }
        }
        return 0;
    }

    void fireDoneButton(Player player) throws Exception {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == uiBoardEventListener.class) {
                ((uiBoardEventListener) listeners[i + 1]).doneButtonPressed(player);
            }
        }

    }

    private void fireShowAreaCard(Area areaName, Player playerName) throws Exception {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == uiBoardEventListener.class) {
                ((uiBoardEventListener) listeners[i + 1]).showAreaCard(areaName, playerName);
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


    public static boolean popup(IDialog dialog, Stage primaryStage) {

        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene((Pane) dialog));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.setIconified(false);
        stage.initOwner(primaryStage);
        stage.showAndWait();
        return dialog.getResult() == ButtonType.YES;
    }


    public boolean flipCoin(Coin defaultFace, double waitForFlipping, Stage primaryStage) throws URISyntaxException {
        CoinDialogController coinDialogController = new CoinDialogController(defaultFace, waitForFlipping);
        Stage stage = new Stage(StageStyle.UNDECORATED);
        stage.setScene(new Scene(coinDialogController));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setResizable(false);
        stage.initOwner(primaryStage);
        stage.showAndWait();
        return coinDialogController.result;
    }


    public void setCardEventHandler(uiCardEventListener cardEventHandler) {
        this.cardEventHandler = cardEventHandler;
    }
}
