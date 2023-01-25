package test.java.com.polimi.app;

import main.java.com.polimi.app.controllers.Effect7;
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

public class Effect7Test {
    private static GameController context = new GameController(0, "Expert", 3);
    private static Effect7 controller;
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
    public void effectTest(){
        controller = new Effect7(context);
        Packet packet = new Packet("PLAY_CHARACTER", 0, 0);
        packet.addToPayload("character_number_moves", 2);
        packet.addToPayload("character_hall_choice_0", 2);
        packet.addToPayload("character_card_choice_0", 2);
        packet.addToPayload("character_hall_choice_1", 3);
        packet.addToPayload("character_card_choice_1", 3);
        controller.ActivateEffect(context, packet);
        assertEquals(7, context.getPlayedCharacterByPlayerId(0).get("effect"));

        packet = new Packet("PLAY_CHARACTER", 0, 0);
        packet.addToPayload("character_number_moves", 2);
        packet.addToPayload("character_hall_choice_0", 10);
        packet.addToPayload("character_card_choice_0", 2);
        packet.addToPayload("character_hall_choice_1", 3);
        packet.addToPayload("character_card_choice_1", 3);
        ArrayList<Packet> report = controller.ActivateEffect(context, packet);
        assertEquals("ERROR", report.get(0).getAction());
    }
}