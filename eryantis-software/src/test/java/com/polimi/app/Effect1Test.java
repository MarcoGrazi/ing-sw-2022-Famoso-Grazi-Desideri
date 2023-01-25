package test.java.com.polimi.app;

import main.java.com.polimi.app.controllers.Effect1;
import main.java.com.polimi.app.controllers.GameController;
import main.java.com.polimi.app.models.Colour;
import main.java.com.polimi.app.models.Mage;
import main.java.com.polimi.app.models.Player;
import main.java.com.polimi.app.packets.Packet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Effect1Test {
    private static GameController context = new GameController(0, "Expert", 3);
    private static Effect1 controller;
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

    @Test
    public void EffectTest(){
        Packet packet = new Packet("PLAY_CHARACTER", 0, 0);
        packet.addToPayload("character_student_choice_1", 1);
        packet.addToPayload("character_island_choice_1", 2);
        controller = new Effect1(context);
        controller.ActivateEffect(context, packet);
        assertEquals(1, context.getPlayedCharacterByPlayerId(0).get("effect"));

        packet = new Packet("PLAY_CHARACTER", 0, 0);
        packet.addToPayload("character_student_choice_1", 1);
        packet.addToPayload("character_island_choice_1", 13);
        ArrayList<Packet> report = controller.ActivateEffect(context, packet);
        assertEquals("ERROR", report.get(0).getAction());
    }
}