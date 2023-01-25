package main.java.com.polimi.app.controllers.game_states;

import main.java.com.polimi.app.controllers.GameController;
import main.java.com.polimi.app.packets.Packet;

import java.util.ArrayList;

/**
 * Represents the state in which the game currently is. Each state has its own specific features and permitted actions.
 * @author Group 53
 */
public interface State {

    /**
     * First stage of the packet handling. The packet is received and inspected to block any unwanted action.
     * @param packet the pack of information necessary to handle the chosen action
     * @return a list of ERROR packets in case of rejection or a list different types of packets depending on the handle outcome
     */
    public ArrayList<Packet> verify(Packet packet);

    /**
     * Second and last stage of the packet handling. Here the action is executed and processed.
     * @param packet the pack of information necessary and handle the chosen action
     * @return a list of packets of different types depending on the outcome
     */
    public ArrayList<Packet> handle (Packet packet);

    /**
     * Changes the context state to a new one. Used for game progression.
     * @param state the new state
     */
    public void changeState(State state);

    /**
     * Changes the context of the state.
     * @param context the new context.
     */
    void switchContext(GameController context);
}
