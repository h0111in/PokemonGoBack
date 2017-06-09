package Controller;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Created by Hosein on 6/7/2017.
 */
@RunWith(Arquillian.class)
public class LogicControllerTest {
    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void startGame() throws Exception {
    }

    @Test
    public void executeAbility() throws Exception {
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
