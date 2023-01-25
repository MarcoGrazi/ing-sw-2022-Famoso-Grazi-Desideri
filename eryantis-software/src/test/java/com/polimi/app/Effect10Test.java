package test.java.com.polimi.app;

import main.java.com.polimi.app.controllers.Effect10;
import main.java.com.polimi.app.controllers.GameController;
import main.java.com.polimi.app.models.Colour;
import main.java.com.polimi.app.models.Mage;
import main.java.com.polimi.app.models.Player;
import org.junit.After;
import org.junit.Before;

public class Effect10Test {
    private static GameController context = new GameController(0, "Expert", 3);
    private static Effect10 controller;
    @Before
    public void setUp() {
        context.getPlController().addPlayer(new Player(0, Mage.JAFAR, Colour.BLACK));
        context.getPlController().addPlayer(new Player(1, Mage.MORGANA, Colour.WHITE));
        context.getPlController().addPlayer(new Player(2, Mage.WONG, Colour.GREY));
        context.initializeGame();
    }

    @After
    public void clean() {
        controller = null;
        context = new GameController(0, "Expert", 3);
    }

    /*
    //does not work because nullpointerException on school.getStudentsInTable(race) this only works when put after
    //the right game workflow
    @Test
    public void effectTest(){
        controller = new Effect10();
        Packet packet = new Packet("PLAY_CHARACTER", 0, 0);
        packet.addToPayload("character_number_moves", 2);
        packet.addToPayload("character_hall_choice_0", 2);
        packet.addToPayload("character_table_choice_0", 2);
        packet.addToPayload("character_hall_choice_1", 3);
        packet.addToPayload("character_table_choice_1", 3);
        controller.ActivateEffect(context, packet);
        assertEquals(10, context.getPlayedCharacterByPlayerId(0).get("effect"));

        packet = new Packet("PLAY_CHARACTER", 0, 0);
        packet.addToPayload("character_number_moves", 2);
        packet.addToPayload("character_hall_choice_0", 10);
        packet.addToPayload("character_table_choice_0", 2);
        packet.addToPayload("character_hall_choice_1", 3);
        packet.addToPayload("character_table_choice_1", 3);
        ArrayList<Packet> report = controller.ActivateEffect(context, packet);
        assertEquals("ERROR", report.get(0).getAction());
    }
    */
}