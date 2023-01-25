package main.java.com.polimi.client.models.client_states;

import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;

public interface State {
    /**
     * @param packet packet from client to verify
     * @throws Exception
     */
    void verify(Packet packet) throws Exception;

    /**
     * @param packet packet from server to handle
     * @throws Exception
     */
    void handle (Packet packet) throws Exception;

    /**
     * @param state next state
     */
    void changeState(State state);

    /**
     * @param context previous context
     */
    void switchContext(ClientController context);

    /**
     * reset the phase od the blocked state of a player after a rejoin
     */
    void reset();
}
