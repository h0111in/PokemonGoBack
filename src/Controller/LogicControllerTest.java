package Controller;

import Enums.Area;
import Enums.Player;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Hosein on 6/7/2017.
 */
@RunWith(Arquillian.class)
public class LogicControllerTest {
    LogicController logicController;
    Map<Player, Boolean> players;

    @Before
    public void setUp() throws Exception {
        players = new HashMap<>();
        players.put(Player.A, false);
        players.put(Player.B, true);
        logicController = new LogicController(players);
    }

    @Test
    public void startGame() throws Exception {
        logicController.startGame();
        if (logicController.firstTurn) {
            assertEquals(logicController.players.get(Player.A).getAreaCard(Area.deck).size(), 47);
            assertEquals(logicController.players.get(Player.B).getAreaCard(Area.deck).size(), 47);
            assertEquals(logicController.players.get(Player.A).getAreaCard(Area.prize).size(), 6);
            assertEquals(logicController.players.get(Player.B).getAreaCard(Area.prize).size(), 6);
            assertEquals(logicController.players.get(Player.A).getAreaCard(Area.hand).size(), 7);
            assertEquals(logicController.players.get(Player.B).getAreaCard(Area.hand).size(), 7);
        }
    }


    @Test
    public void isMovementVerified() throws Exception {

    }

    @Test
    public void addListener() throws Exception {

    }

    @Test
    public void fireShowMessage() throws Exception {
    }

    @Test
    public void fireFlipCoin() throws Exception {
    }

    @Test
    public void getActivePlayer() throws Exception {
    }

    @Test
    public void setActivePlayer() throws Exception {
    }

    @Test
    public void addPlayerEventListener() throws Exception {
    }

    @Test
    public void getOpponent() throws Exception {
    }

    @Test
    public void getPlayers() throws Exception {
    }

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClass(LogicController.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

}
