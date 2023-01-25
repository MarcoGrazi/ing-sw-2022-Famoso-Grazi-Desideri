package test.java.com.polimi.app;

import main.java.com.polimi.app.controllers.ActiveCharactersController;
import main.java.com.polimi.app.controllers.GameController;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ActiveCharactersControllerTest {

    private static GameController context = new GameController(0, "Expert", 3);
    private static ActiveCharactersController controller;

    @Before
    public void setUp() {
        controller = new ActiveCharactersController(context);
        context.initializeGame();
    }

    @After
    public void clean() {
        controller = null;
        context = new GameController(0, "Expert", 3);
    }

    @Test
    public void initializationTest(){
        controller.initializeCharacters();
        HashMap<String, Object> characters = controller.getCharactersInfo();
        assertEquals(3, characters.keySet().size());
        for(String k : characters.keySet()){
            Boolean valid = true;
            if(Integer.parseInt(k) > 12 || Integer.parseInt(k) <= 0){
                valid = false;
            }
            assertTrue(valid);
        }
    }

    @Test
    public void costTest(){
        controller.initializeCharacters();
        HashMap<String, Object> characters = controller.getCharactersInfo();
        for(String k: characters.keySet()){
            int cost = controller.getCostById(Integer.parseInt(k));
            switch(k){
                case "1", "4", "7", "10" -> {assertEquals(1, cost);}
                case "2", "5", "8", "11" -> {assertEquals(2, cost);}
                case "3", "6", "9", "12" -> {assertEquals(3, cost);}
            }

            controller.incrementCostById(Integer.parseInt(k));
        }

        characters = controller.getCharactersInfo();
        for(String k: characters.keySet()) {
            int cost = controller.getCostById(Integer.parseInt(k));
            switch (k) {
                case "1", "4", "7", "10" -> {
                    assertEquals(2, cost);
                }
                case "2", "5", "8", "11" -> {
                    assertEquals(3, cost);
                }
                case "3", "6", "9", "12" -> {
                    assertEquals(4, cost);
                }
            }
        }
    }
}