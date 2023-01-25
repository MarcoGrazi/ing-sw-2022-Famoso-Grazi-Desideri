package test.java.com.polimi.app;

import main.java.com.polimi.app.models.Colour;
import main.java.com.polimi.app.models.Mage;
import main.java.com.polimi.app.models.Player;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlayerTest {

    private static Player player;

    private static int playerId;
    private static Mage mage;
    private static Colour schoolColour;

    @Before
    public void setUp() {
        playerId = 0;
        mage = Mage.JAFAR;
        schoolColour = Colour.BLACK;

        player =new Player(playerId, mage, schoolColour);
    }

    @After
    public void clean(){
        player = null;
    }

    @Test
    public void ConstructorTest(){
        assertEquals(0, player.getPlayerId());
        assertEquals(Mage.JAFAR, player.getMageName());
        assertEquals(schoolColour, player.getSchoolColour());
        assertEquals(1, player.getCoinCounter());
    }

    @Test
    public void coinTest(){
        int coincount = player.getCoinCounter();

        //test addCoin correct functioning
        player.addCoin();
        coincount += 1;
        assertEquals(coincount, player.getCoinCounter());

        //test subCoin correct functioning
        player.subCoin(1);
        coincount -= 1;
        assertEquals(coincount, player.getCoinCounter());

        //test subCoin negative amount
        player.subCoin(-1);
        assertEquals(coincount, player.getCoinCounter());

        //test subCoin with already 0 coincounter
        while(coincount >0){
            player.subCoin(1);
            coincount = player.getCoinCounter();
        }
        player.subCoin(1);
        assertEquals(coincount, player.getCoinCounter());
    }
}
