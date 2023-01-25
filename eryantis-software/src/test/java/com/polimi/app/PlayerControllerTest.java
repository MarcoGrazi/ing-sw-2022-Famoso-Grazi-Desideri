package test.java.com.polimi.app;

import main.java.com.polimi.app.controllers.GameController;
import main.java.com.polimi.app.models.Colour;
import main.java.com.polimi.app.models.Mage;
import main.java.com.polimi.app.models.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PlayerControllerTest {
    private static GameController context = new GameController(0, "Expert", 3);
    @Before
    public void setUp() {
        context.initializeGame();
    }

    @After
    public void clean() {
        context = new GameController(0, "Expert", 3);
    }

    @Test
    public void playerMovementsTest(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player(0, Mage.JAFAR, Colour.BLACK));
        players.add(new Player(1, Mage.MORGANA, Colour.WHITE));
        players.add(new Player(2, Mage.WONG, Colour.GREY));

        context.getPlController().addPlayer(players.get(0));
        context.getPlController().addPlayer(players.get(1));
        context.getPlController().addPlayer(players.get(2));

        assertEquals(players, context.getPlController().getPlayers());
        for(int id : context.getPlController().getPlayerIds()){
            assertEquals(players.get(id), context.getPlController().getPlayerById(id));
        }

        context.getPlController().removePlayer(2);
        players.remove(2);
        assertEquals(players, context.getPlController().getPlayers());
        for(int id : context.getPlController().getPlayerIds()){
            assertEquals(players.get(id), context.getPlController().getPlayerById(id));
        }
    }

    @Test
    public void getSchoolColourByIDTest(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player(0, Mage.JAFAR, Colour.BLACK));
        players.add(new Player(1, Mage.MORGANA, Colour.WHITE));
        players.add(new Player(2, Mage.WONG, Colour.GREY));

        context.getPlController().addPlayer(players.get(0));
        context.getPlController().addPlayer(players.get(1));
        context.getPlController().addPlayer(players.get(2));

        assertEquals(Colour.BLACK, context.getPlController().getSchoolColourById(0));
        assertEquals(Colour.WHITE, context.getPlController().getSchoolColourById(1));
        assertEquals(Colour.GREY, context.getPlController().getSchoolColourById(2));
    }
}