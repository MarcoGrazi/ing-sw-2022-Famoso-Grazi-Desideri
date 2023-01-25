package main.java.com.polimi.app.models;

import java.io.Serializable;

/**
 * Player class.
 * Each instance is represented by an absolute playerId, mage and school colour, each unique for each player,
 * and a coin counter which stores the current amount of coins the player owns.
 * @author Group 53
 */
public class Player implements Serializable {
    //The absolute playerId
    private final int playerId;
    //The mage associated with the player
    private final Mage mageName;
    //The colour of the school associated with the player
    private final Colour schoolColour;
    //The current amount of coins
    private int coinCounter;

    /**
     * Class constructor.
     * Initializes the playerId, the mageName, the schoolColour with chosen parameters and sets the coinCounter to 1.
     * @param playerId the assigned and unique id
     * @param mageName the chosen mage
     * @param schoolColour the chosen colour
     */
    public Player(int playerId,  Mage mageName, Colour schoolColour) {
        this.playerId = playerId;
        this.mageName = mageName;
        this.schoolColour = schoolColour;
        this.coinCounter = 1;
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
     * Adds a coin to the amount of coins owned by the player
     */
    public void addCoin(){
        this.coinCounter++;
    }

    /**
     * Subs the selected amount of coins from the player possessions
     * @param amount the amount of coins to subtract
     */
    public void subCoin(int amount){
        if(amount <0){
            //exception
            System.out.println("negative amount passed as parameter");
            return;
        }
        if(getCoinCounter()-amount<0){
            //exception
            System.out.println("negative balance");
            return;
        }
        this.coinCounter = this.coinCounter - amount;
    }
}
