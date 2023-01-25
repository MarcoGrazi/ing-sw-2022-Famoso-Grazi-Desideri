package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.*;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Player controller class.
 * Manages the group of players inside the game.
 * @author Group 53
 */
public class PlayerController implements Serializable {
    //Number of players
    private final int playerNumber;
    //Instance of the game controller
    private final GameController gameController;
    //The list of Players instances
    private final ArrayList<Player> playerModels;

    /**
     * Class constructor. Initializes the game infos and an empty list of players.
     * @param gameController the game controller instance
     * @param playerNumber the number of players in this game
     */
    public PlayerController(GameController gameController, int playerNumber) {
        this.playerNumber = playerNumber;
        this.gameController = gameController;
        this.playerModels = new ArrayList<>();
    }

    /**
     * Adds a player to the list of players till maximum number is reached.
     * @param player a player instance
     */
    public void addPlayer(Player player){
        if(playerModels.size() != playerNumber) {
            this.playerModels.add(player);
        } else {
            System.out.println("Reached max player number");
        }
    }

    /**
     * @param playerId the id of the player
     * @return the player's amount of conins
     */
    public int getCoins(int playerId) {
        for(Player p: playerModels) {
            if(p.getPlayerId() == playerId) {
                return p.getCoinCounter();
            }
        }
        return -1;
    }

    /**
     * Adds a coin to the player balance.
     * @param playerId the id of the player
     */
    public void addCoin(int playerId){
        for(Player p: playerModels) {
            if(p.getPlayerId() == playerId) {
                p.addCoin();
            }
        }
    }

    /**
     * Subs a specified amount of coins from the player balance.
     * @param playerId the id of the player
     * @param amount the amount of coins to subtract
     */
    public void subCoin(int playerId, int amount){
        for(Player p: playerModels) {
            if(p.getPlayerId() == playerId) {
                p.subCoin(amount);
            }
        }
    }

    /**
     * @param playerId the id of the player
     * @return the colour of the player's school
     */
    public Colour getSchoolColourById(int playerId){
        for(Player p: playerModels) {
            if(p.getPlayerId() == playerId) {
                return p.getSchoolColour();
            }
        }
        return null;
    }

    /**
     * @return the list of players instances
     */
    public ArrayList<Player> getPlayers(){
        return this.playerModels;
    }

    /**
     * @param id the id of the player
     * @return the instance of the player
     */
    public Player getPlayerById(int id){
        for(Player p : playerModels){
            if(p.getPlayerId() == id){
                return p;
            }
        }
        return null;
    }

    /**
     * @return the list of player ids, ordered by player instance
     */
    public ArrayList<Integer> getPlayerIds() {
        ArrayList<Integer> tmpPlayerIds = new ArrayList<>();

        for(Player p: playerModels) {
            tmpPlayerIds.add(p.getPlayerId());
        }
        return tmpPlayerIds;
    }

    /**
     * Removes a player
     * @param playerId the id of the player
     */
    public void removePlayer(int playerId) {
        this.playerModels.removeIf(obj -> obj.getPlayerId() == playerId);
    }

}
