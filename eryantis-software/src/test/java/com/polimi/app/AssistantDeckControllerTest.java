package test.java.com.polimi.app;

import main.java.com.polimi.app.controllers.AssistantDeckController;
import main.java.com.polimi.app.controllers.GameController;
import main.java.com.polimi.app.models.Colour;
import main.java.com.polimi.app.models.Mage;
import main.java.com.polimi.app.models.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class AssistantDeckControllerTest {

    private static GameController context = new GameController(0, "Expert", 3);
    private static AssistantDeckController controller;

    @Before
    public void setUp() {
        context.getPlController().addPlayer(new Player(0, Mage.JAFAR, Colour.BLACK));
        context.getPlController().addPlayer(new Player(1, Mage.MORGANA, Colour.WHITE));
        context.getPlController().addPlayer(new Player(2, Mage.WONG, Colour.GREY));
        controller = new AssistantDeckController(context, 3);
        context.initializeGame();
    }

    @After
    public void clean() {
        controller = null;
        context = new GameController(0, "Expert", 3);
    }

    @Test
    public void ConstructorTest(){
         for(int id : context.getPlController().getPlayerIds()){
             assertEquals(id, controller.getDeck(id).getDeckId());
         }
    }

    @Test
    public void playCardTest(){
        controller.playCard(0, 1);
        assertEquals(1, controller.getLastPlayerCardIndex(0));
        assertEquals(1, controller.getLastPlayedCardWeight(0));
        assertEquals(1, controller.getLastPlayedCardMoves(0));

        controller.playCard(0, 3);
        assertEquals(3, controller.getLastPlayerCardIndex(0));
        assertEquals(3, controller.getLastPlayedCardWeight(0));
        assertEquals(2, controller.getLastPlayedCardMoves(0));

        controller.playCard(0, 5);
        assertEquals(5, controller.getLastPlayerCardIndex(0));
        assertEquals(5, controller.getLastPlayedCardWeight(0));
        assertEquals(3, controller.getLastPlayedCardMoves(0));

        controller.playCard(0, 7);
        assertEquals(7, controller.getLastPlayerCardIndex(0));
        assertEquals(7, controller.getLastPlayedCardWeight(0));
        assertEquals(4, controller.getLastPlayedCardMoves(0));

        controller.playCard(0, 9);
        assertEquals(9, controller.getLastPlayerCardIndex(0));
        assertEquals(9, controller.getLastPlayedCardWeight(0));
        assertEquals(5, controller.getLastPlayedCardMoves(0));

        ArrayList<Integer> remainingCards = new ArrayList<>(Arrays.asList(2,4,6,8,10));
        assertEquals(remainingCards, controller.getDeck(0).getAssistantsHand());

        assertEquals(remainingCards.size(), controller.getHandSize(0));
    }
}