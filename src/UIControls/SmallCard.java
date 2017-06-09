package UIControls;

import Enums.Player;
import Model.*;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.script.ScriptException;
import javax.swing.event.EventListenerList;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static Controller.Main.logger;

public class SmallCard extends GridPane {

    @FXML
    protected Label name;
    @FXML
    protected Label header;
    @FXML
    protected Label e0;
    @FXML
    protected Label e1;
    @FXML
    protected Label e2;
    @FXML
    protected Label e3;
    @FXML
    protected Label e4;
    @FXML
    protected Label basicCardSymbol;
    @FXML
    protected Pane energyHolder;

    @FXML
    protected Label button;

    private Enums.Player playerName;

    private PokemonCard basicCard;
    private PokemonCard stageCard;
    private List<EnergyCard> energyCardList;
    private TrainerCard trainerCard;
    private EventListenerList listenerList;


    public SmallCard(Model.Card card, Enums.Player playerName, Stage primaryStage) throws Exception {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SmallCard.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        energyCardList = new ArrayList<>();
        //this.getStylesheets().add("../asset/gameBoard.css");
        // setStyle("-fx-background-color: magenta");
        header.setVisible(false);
        name.setVisible(false);
        button.setVisible(false);

        if (playerName.equals(Player.A))
            setStyle("-fx-background-image:url('/asset/card-back5.png'); ");
        else setStyle("-fx-background-image:url('/asset/card-back6.png'); ");

        //setStyle("-fx-background-image: url('https://encrypted-tbn1.gstatic.com/images?q=tbn:ANd9GcQxsasGQIwQNwjek3F1nSwlfx60g6XpOggnxw5dyQrtCL_0x8IW');");

        this.setId(card.getId());
        this.playerName = playerName;
        listenerList = new EventListenerList();
        this.setCursor(Cursor.HAND);
        button.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                NormalCard normalCard = null;
                try {
                    normalCard = new NormalCard(getMainCard(), getPlayerName());

                    normalCard.addListener(new uiCardEvent() {
                        @Override
                        public void attackRequest(Player playerName, String cardId, int attackIndex) throws Exception {
                            fireAttack(playerName, cardId, attackIndex);
                        }

                        @Override
                        public void applyTrainerCardRequest(Player playerName, String cardId) throws Exception {

                        }

                        @Override
                        public boolean showFaceRequest(Player playerName, String cardId) {
                            return false;
                        }


                    });
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
                Stage stage = new Stage(StageStyle.UNDECORATED);
                stage.setScene(new Scene(normalCard));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setResizable(false);
                stage.setIconified(false);
                stage.initOwner(primaryStage);
                stage.showAndWait();
            }
        });
        e0.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {

                Stage stage = new Stage(StageStyle.UNDECORATED);
                try {
                    stage.setScene(new Scene(new NormalCard(getEnergyCardList().get(0), getPlayerName())));

                    stage.setTitle(getMainCard().getCategory().name().toUpperCase());
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setResizable(false);
                    stage.setIconified(false);
                    stage.initOwner(primaryStage);
                    stage.showAndWait();
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            }
        });
        e1.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    Stage stage = new Stage(StageStyle.UNDECORATED);
                    stage.setScene(new Scene(new NormalCard(getEnergyCardList().get(1), getPlayerName())));
                    stage.setTitle(getMainCard().getCategory().name().toUpperCase());
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setResizable(false);
                    stage.setIconified(false);
                    stage.initOwner(primaryStage);
                    stage.showAndWait();
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            }
        });
        e2.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    Stage stage = new Stage(StageStyle.UNDECORATED);
                    stage.setScene(new Scene(new NormalCard(getEnergyCardList().get(2), getPlayerName())));
                    stage.setTitle(getMainCard().getCategory().name().toUpperCase());
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setResizable(false);
                    stage.setIconified(false);
                    stage.initOwner(primaryStage);
                    stage.showAndWait();
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            }
        });
        e3.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    Stage stage = new Stage(StageStyle.UNDECORATED);
                    stage.setScene(new Scene(new NormalCard(getEnergyCardList().get(3), getPlayerName())));
                    stage.setTitle(getMainCard().getCategory().name().toUpperCase());
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setResizable(false);
                    stage.setIconified(false);
                    stage.initOwner(primaryStage);
                    stage.showAndWait();
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            }
        });
        basicCardSymbol.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                try {
                    Stage stage = new Stage(StageStyle.UNDECORATED);
                    stage.setScene(new Scene(new NormalCard(getBasicCard(), getPlayerName())));
                    stage.setTitle(getMainCard().getCategory().name().toUpperCase());
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setResizable(false);
                    stage.setIconified(false);
                    stage.initOwner(primaryStage);
                    stage.showAndWait();
                } catch (ScriptException e) {
                    e.printStackTrace();
                }
            }

        });
        this.setOnDragDetected(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                Dragboard db = SmallCard.this.startDragAndDrop(TransferMode.ANY);
                ClipboardContent content = new ClipboardContent();
                content.putString(SmallCard.this.getId());
                db.setContent(content);
                event.consume();
            }
        });
        this.setOnDragDone(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                if (event.getTransferMode() == TransferMode.MOVE) {

                }
                event.consume();
            }
        });

        //place card
        push(card);

    }

    public void push(Card card) throws Exception {


        if (card instanceof PokemonCard) {
            PokemonCard pokemonCard = (PokemonCard) card;

            if (pokemonCard.getLevel().contains("basic"))
                if (getBasicCard() == null)
                    setBasicCard(pokemonCard);
                else
                    throw new Exception("Two Basic UIControls!");
            else if (getStageCard() == null)
                setStageCard(pokemonCard);
            else
                throw new Exception("Two Stage UIControls!");

        } else if (card instanceof TrainerCard) {

            if (getTrainerCard() == null)
                setTrainerCard((TrainerCard) card);
            else
                throw new Exception("Incompatible UIControls Type!");

        } else if (card instanceof EnergyCard) {
            getEnergyCardList().add((EnergyCard) card);
        }

        updateView();


//        if (card instanceof PokemonCard) {
//            if (getTrainerCard() == null) {
//                PokemonCard pokemonCard = (PokemonCard) card;
//                if (pokemonCard.getLevel().contains("basic")) {
//                    if (getBasicCard() == null)
//                        setBasicCard(pokemonCard);
//                    else
//                        throw new Exception("Two Basic UIControls!");
//                } else {
//                    if (getStageCard() == null)
//                        setStageCard(pokemonCard);
//                    else
//                        throw new Exception("Two Stage UIControls!");
//                }
//            } else throw new Exception("Incompatible UIControls Type!");
//        } else if (card instanceof TrainerCard) {
//
//            if (getTrainerCard() == null && getBasicCard() == null && getStageCard() == null && getEnergyCardList().size() == 0)
//                setTrainerCard((TrainerCard) card);
//            else
//                throw new Exception("Incompatible UIControls Type!");
//        } else if (card instanceof EnergyCard) {
//            if (getTrainerCard() == null && getBasicCard() == null && getStageCard() == null)
//                getEnergyCardList().add((EnergyCard) card);
//            else
//                throw new Exception("Incompatible UIControls Type!");
//        }
    }

    public Card pop(String id) throws CloneNotSupportedException {
        Card output = null;
        if (getBasicCard() != null && getBasicCard().getId().equals(id)) {
            output = getBasicCard().clone();
            basicCard = null;
        }
        if (getStageCard() != null && getStageCard().getId().equals(id)) {
            output = getStageCard().clone();
            stageCard = null;
        }
        if (getTrainerCard() != null && getTrainerCard().getId().equals(id)) {
            output = getTrainerCard();
            trainerCard = null;
        }
        for (int i = 0; i < getEnergyCardList().size(); i++) {
            if (energyCardList.get(i).getId().equals(id)) {
                output = getEnergyCardList().get(i).clone();
                energyCardList.remove(i);
                break;
            }
        }

        if (getAllCard().size() > 0)
            updateView();
        return output;
    }

    public void updateView() {
        e0.setVisible(false);
        e1.setVisible(false);
        e2.setVisible(false);
        e3.setVisible(false);
        basicCardSymbol.setVisible(false);
        energyHolder.setVisible(getEnergyCardList().size() > 0 && getBasicCard() != null);
        e0.setVisible(getEnergyCardList().size() > 0 && getBasicCard() != null);
        e1.setVisible(getEnergyCardList().size() > 1);
        e2.setVisible(getEnergyCardList().size() > 2);
        e3.setVisible(getEnergyCardList().size() > 3);
        basicCardSymbol.setVisible(getStageCard() != null && getBasicCard() != null && fireShowFaceRequest(playerName, getId()));

        Card topCard = getMainCard();
        this.name.setText(topCard.getName());
        if (topCard instanceof PokemonCard)
            this.header.setText(((PokemonCard) topCard).getLevel() + "-" + ((PokemonCard) topCard).getHealth() + "/" + ((PokemonCard) topCard).getHitPoint());
        else
            this.header.setText(topCard.getCategory() + "/" + topCard.getType());

    }

    public Enums.Player getPlayerName() {
        return playerName;
    }

    public Card getMainCard() {

        if (getTrainerCard() != null)
            return getTrainerCard();
        else if (getStageCard() != null)
            return getStageCard();
        else if (getBasicCard() != null)
            return getBasicCard();
        else if (getEnergyCardList().size() > 0)
            return getEnergyCardList().get(0);
        else return null;
    }

    public List<Card> getAllCard() throws CloneNotSupportedException {
        List<Card> output = new ArrayList<>();
        if (getBasicCard() != null)
            output.add(getBasicCard().clone());
        if (getStageCard() != null) output.add(getStageCard().clone());
        if (getTrainerCard() != null) output.add(getTrainerCard().clone());
        for (Card card : energyCardList)
            output.add(card.clone());
        return output;
    }

    @FXML
    private void handleClick(Event event) {
        // showFace();
    }

    public void showFace() {

        if (fireShowFaceRequest(playerName, getMainCard().getId())) {

            header.setVisible(true);
            name.setVisible(true);
            button.setVisible(true);
            setStyle("-fx-background-color: transparent");
            if (playerName.equals(Player.A))
                setStyle("-fx-background-image:url('/asset/card-face-2.png'); ");
            else setStyle("-fx-background-image:url('/asset/card-face-black1.png'); ");


            if (getMainCard() instanceof PokemonCard) {

            } else {
                energyHolder.setVisible(false);
                basicCardSymbol.setVisible(false);
            }
            updateView();
        }

    }


    //region Events
    public void addListener(uiCardEvent listener) {
        listenerList.add(uiCardEvent.class, listener);
    }

    public void removeListener(uiCardEvent listener) {
        listenerList.remove(uiCardEvent.class, listener);
    }

    void fireAttack(Player playerName, String cardId, int attackIndex) throws Exception {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == uiCardEvent.class) {
                ((uiCardEvent) listeners[i + 1]).attackRequest(playerName, cardId, attackIndex);
            }
        }
    }

    boolean fireShowFaceRequest(Player playerName, String cardId) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == uiCardEvent.class) {
                return ((uiCardEvent) listeners[i + 1]).showFaceRequest(playerName, cardId);
            }
        }
        return false;
    }

    public void setStageCard(PokemonCard stageCard) {
        this.stageCard = stageCard;
        updateView();
    }

    public void setBasicCard(PokemonCard basicCard) {
        this.basicCard = basicCard;
        updateView();
    }

    public List<EnergyCard> getEnergyCardList() {
        return energyCardList;
    }


    public TrainerCard getTrainerCard() {
        return trainerCard;
    }

    public void setTrainerCard(TrainerCard trainerCard) {
        this.trainerCard = trainerCard;
        updateView();
    }

    public PokemonCard getStageCard() {
        return stageCard;
    }

    public PokemonCard getBasicCard() {
        return basicCard;
    }


    //endregion
}

