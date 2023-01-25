package test.java.com.polimi.app;

import main.java.com.polimi.app.models.AssistantDeck;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssistantDeckTest {

    private static AssistantDeck assistantDeck;

    @Before
    public void setUp(){
        assistantDeck = new AssistantDeck();
    }

    @After
    public void clean(){
        assistantDeck = null;
    }
    //makes sure that the assistantHand has been initialized properly (contains all the initial ids
    //and the assistantDiscardPile is empty

    @Test
    public void deckIdTest(){
        assistantDeck.setDeckId(1);
        assertEquals(1, assistantDeck.getDeckId());
    }

    @Test
    public void getAssistantHand__CorrectInitialSituation(){
        int[] trueHand = new int[] {1,2,3,4,5,6,7,8,9,10};
        ArrayList<Integer> actualHand = assistantDeck.getAssistantsHand();
        for(int id : trueHand){
            assertEquals(id, actualHand.get(id-1));
        }
        assertEquals(0, assistantDeck.getAssistantDiscardPile().size());
    }

    //tests that each id corresponds to the right amount of Mother Nature moves
    @Test
    public void getMNMovesById__CorrectMNMoves(){
        int[] trueMNMoves = new int[] {1,1,2,2,3,3,4,4,5,5};
        for(int i=1; i<11; i++){
            assertEquals(trueMNMoves[i-1], assistantDeck.getMNmovesByID(i));
        }
    }

    //simulates discarding all the cards in the assistantHand, and make sure that after
    // that the assistantHand is empty and the assistantDiscardPile contains all the ids it should
    @Test
    public void discardAssistant__CorrectFinalHand(){
        int[] trueDiscardPile = new int[] {1,2,3,4,5,6,7,8,9,10};
        for(int id: trueDiscardPile){
            assistantDeck.discardAssistant(id);
        }
        assertEquals(0, assistantDeck.getAssistantsHand().size());
        Stack<Integer> actualDiscardPile = assistantDeck.getAssistantDiscardPile();
        for(int id : trueDiscardPile){
            assertEquals(id, actualDiscardPile.get(id-1));
        }
    }
}