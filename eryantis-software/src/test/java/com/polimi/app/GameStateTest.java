package test.java.com.polimi.app;

import main.java.com.polimi.app.models.GameState;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {
    private static GameState gameState = new GameState();

    @Before
    public void setUp() {
        gameState = new GameState();
    }

    @After
    public void clean() {
        gameState = null;
    }

    @Test
    public void MaxPlIdTest() {
        gameState.setMaxPlId(4);
        assertEquals(4, gameState.getMaxPlId());
    }

    @Test
    public void MaxGmIdTest() {
        gameState.setMaxGmId(2);
        assertEquals(2, gameState.getMaxGmId());
    }

    @Test
    public void NicknamesToReinsertTest() {
        HashMap<Integer, String> correctNicknames = new HashMap<>();
        correctNicknames.put(1, "Cesare");
        correctNicknames.put(2, "Bruto");
        correctNicknames.put(3, "Cassio");

        for(int k : correctNicknames.keySet()){
            gameState.addNicknameToReinsert(k, correctNicknames.get(k));
        }

        assertEquals(correctNicknames, gameState.getNicknamesToReinsert());

        gameState.removeNicknameToReinsert(2);
        correctNicknames.remove(2);
        assertEquals(correctNicknames, gameState.getNicknamesToReinsert());
    }

}