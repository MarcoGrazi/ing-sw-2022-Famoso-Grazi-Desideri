package main.java.com.polimi.app.models;

import main.java.com.polimi.app.controllers.GameController;
import main.java.com.polimi.app.packets.Packet;
import java.util.ArrayList;

/**
 * Effect Strategy interface.
 * Each implementation of this interface defines a specific and unique effect from one of the twelve character
 * cards present inside the game.
 * @author Group 53
 */
public interface EffectStrategy {
    /**
     * Activates the character effect.
     * @param context the instance of the game controller
     * @param packet contains player's choices
     * @return a list of packet holding useful information to send to the client
     */
    ArrayList<Packet> ActivateEffect(GameController context, Packet packet);
}
