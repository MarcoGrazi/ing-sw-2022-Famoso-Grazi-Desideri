package test.java.com.polimi.app;

import main.java.com.polimi.app.controllers.GameController;
import main.java.com.polimi.app.models.Colour;
import main.java.com.polimi.app.models.Mage;
import main.java.com.polimi.app.models.Player;
import main.java.com.polimi.app.packets.Packet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class GameControllerTest {
    private static GameController controller;
    @Before
    public void setUp() {
        controller = new GameController(0, "E", 3);
        controller.getPlController().addPlayer(new Player(0, Mage.JAFAR, Colour.BLACK));
        controller.getPlController().addPlayer(new Player(1, Mage.MORGANA, Colour.WHITE));
        controller.getPlController().addPlayer(new Player(2, Mage.WONG, Colour.GREY));
        controller.initializeGame();
    }

    @After
    public void clean() {
        controller = null;
    }

    @Test
    public void initializationTest(){
        assertEquals(0, controller.getGameId());
        assertEquals(3, controller.getPlayerNumber());
        assertFalse(controller.isSimpleMode());
        assertNotNull(controller.getPlController());
        assertNotNull(controller.getSchoolController());
        assertNotNull(controller.getCloudController());
        assertNotNull(controller.getArchipelagoController());
        assertNotNull(controller.getActiveCharactersController());
        assertNotNull(controller.getAssistantDeckController());
        assertNotNull(controller.getBagController());

        for(int id : controller.getPlController().getPlayerIds()){
            assertEquals(0, controller.getPlayedCharacterByPlayerId(id).get("effect"));
        }
    }

    @Test
    public void turnTest(){
        //test setPlayerIdTurnOrder
        controller.getAssistantDeckController().playCard(0, 1);
        controller.getAssistantDeckController().playCard(1, 2);
        controller.getAssistantDeckController().playCard(2, 3);
        ArrayList<Packet> report= controller.setPlayerIdTurnOrder(2);
        for(Packet p : report){
            if(p.getAction().equals("UNLOCK_ACTION")){
                assertEquals(0, p.getPlayerId());
            }
            if(p.getAction().equals("LOCK")){
                assertTrue(p.getPlayerId()==1 || p.getPlayerId()==2);
            }
        }

        //test isLastPlayer
        assertTrue(controller.isLastPlayer(2));

        //test getPlayerIdTurnOrder
        for(int i=0;  i < controller.getPlayerIdTurnOrder().size(); i++){
            assertEquals(i, (int)controller.getPlayerIdTurnOrder().get(i));
        }

        //test startTurn
        controller.replacePlayedCharacterByPlayerId(0, 3, 0);
        controller.startTurn();
        assertEquals(0, controller.getPlayedCharacterByPlayerId(0).get("effect"));
    }

    @Test
    public void PlayedCharacterByPlayerId(){
        controller.replacePlayedCharacterByPlayerId(0, 3, 0);
        assertEquals(3, controller.getPlayedCharacterByPlayerId(0).get("effect"));
        assertEquals(0, controller.getPlayedCharacterByPlayerId(0).get("info"));
    }

    @Test
    public void EndGameTest(){
        //test endImmediately
        controller.endImmediately();
        assertTrue(controller.getEndImmediately());

        //test endAfterTurn
        controller.endAfterTurn();
        assertTrue(controller.getEndAfterTurn());

        //test setWinner
        controller.setWinnerId(0);
        assertEquals(0, controller.getWinnerId());
    }
}