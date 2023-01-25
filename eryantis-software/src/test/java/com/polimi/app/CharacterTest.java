package test.java.com.polimi.app;

import main.java.com.polimi.app.models.Character;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CharacterTest {
    private static ArrayList<Character> characters = new ArrayList<>();

    @Before
    public void setUp(){
        //initialize 12 characters vith all 1-12 possible ids +
        // one character with id 13 to test the error message
        for(int id=1; id<14; id++){
           characters.add(new Character(id));
        }
    }

    @After
    public void clean(){
        characters.clear();
    }
    //makes sure the characters have been initialized correctly
    // with the right correlation betwwen ids and initial costs
    @Test
    public void getCost__CorrectInitialCost(){
        int[] initialCosts = new int[] {1,2,3,1,2,3,1,2,3,1,2,3};
        for(int i=0; i<12; i++){
            assertEquals(characters.get(i).getCost(), initialCosts[i]);
        }
    }

    //tests the activation of each character effect and then checks that
    // the character costs have increased by one (as stated in the Eriantys rules)
    @Test
    public void incrementCostTest(){
        characters.get(0).incrementCost();
        assertEquals(2, characters.get(0).getCost());
    }

}
