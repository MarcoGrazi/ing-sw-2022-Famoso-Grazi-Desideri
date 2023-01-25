package main.java.com.polimi.client.views;

import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;

import java.util.Observable;
import java.util.Observer;

public interface ViewInterface extends Observer {
    /**
     * prints nickname warning
     */
    void printNicknameWarning();

    /**
     * @param packet
     * prints available games with info
     */
    void printGames(Packet packet);

    /**
     * @param message
     * prints a generic error
     */
    void printErrorMessage(String message);

    /**
     * @param message
     * prints a generic message
     */
    void printMessage(Object message);

    /**
     * @param packet
     * prints available characters
     */
    void printCharacters(Packet packet);

    /**
     * @param clientController
     * adds a controller to the observers
     */
    void addController(ClientController clientController);

    @Override
    void update(Observable o, Object arg);
}
