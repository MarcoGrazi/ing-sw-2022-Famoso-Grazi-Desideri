package main.java.com.polimi.client.models;

import java.util.Observable;
import java.util.Observer;

/**
 * Player class.
 * Each instance is represented by an absolute playerId, mage and school colour, each unique for each player,
 * and a coin counter which stores the current amount of coins the player owns.
 * @author Group 53
 */
public class Player  extends Observable {
    private final int playerId;
    private final Mage mageName;
    private final Colour schoolColour;
    private int coinCounter;

    /**
     * Class constructor.
     * Initializes the playerId, the mageName, the schoolColour with chosen parameters and sets the coinCounter to 1.
     * Initializes the view and sets it to observe the model.
     * @param playerId the assigned and unique id
     * @param mageName the chosen mage
     * @param schoolColour the chosen colour
     */
    public Player(int playerId, Mage mageName, Colour schoolColour, Observer playerView, String gameMode) {
        this.playerId = playerId;
        this.mageName = mageName;
        this.schoolColour = schoolColour;
        this.coinCounter = 1;

        addObserver(playerView);
        setChanged();
        notifyObservers(new Message("SETUP_"+gameMode));
    }

    /**
     * Adds the view as observer
     * @param view the instance of the player view
     */
    public void setView(Observer view){
        addObserver(view);
    }

    /**
     * @return the player absolute Id
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * @return the player mage
     */
    public Mage getMageName() {
        return mageName;
    }

    /**
     * @return the player school colour
     */
    public Colour getSchoolColour() {
        return schoolColour;
    }

    /**
     * @return the player amount of coins
     */
    public int getCoinCounter() {
        return coinCounter;
    }

    /**
     * Sets the player balance and updates the view
     * @param coinCounter the new amount of coins
     */
    public void setCoinCounter(int coinCounter) {
        this.coinCounter = coinCounter;
        setChanged();
        notifyObservers(new Message("COIN_COUNTER_CHANGED"));
    }

    public void printPlayer(String mode) {
        setChanged();
        notifyObservers(new Message("PRINT_PLAYER_INFO_"+mode));
    }
}
