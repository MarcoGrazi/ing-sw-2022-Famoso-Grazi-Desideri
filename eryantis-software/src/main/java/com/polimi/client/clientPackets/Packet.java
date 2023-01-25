package main.java.com.polimi.client.clientPackets;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Client packet class.
 * This class is specular to the server packet class to allow Gson to serialize and deserialize packets correctly and
 * move infos between client and server.
 */
public class Packet implements Serializable {
    private static final long serialVersionUID = 1L;
    //The action to request to the server
    private String action;
    //The group of useful infos
    private HashMap<String, Object> payload = new HashMap<>();
    //The id of the player who sent the packet
    private int playerId;
    //The id of the game related to the player
    private int gameId;

    /**
     * @param action the action to request to the server
     * @param playerId current id of the player
     */
    public Packet(String action, int playerId) {
        this.action = action;
        this.playerId = playerId;
    }

    /**
     * @param action the action to request to the server
     */
    public Packet(String action){
        this.action = action;
    }

    /**
     * @param action the action to request to the server
     * @param playerId current id of the player
     * @param gameId current game of the player
     */
    public Packet(String action, int playerId, int gameId) {
        this.action = action;
        this.playerId = playerId;
        this.gameId = gameId;
    }

    /**
     * @return the action of the packet
     */
    public String getAction() {
        return action;
    }

    /**
     * @return the payload of the packet in form of hashmap
     */
    public HashMap<String, Object> getPayload() {
        return payload;
    }

    /**
     * @return the player id assigned to the packet
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * @return the game id assigned to the packet
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * Adds an object to the payload with a given string key
     * @param key name of the object in the payload
     * @param value object to add to the payload
     */
    public void addToPayload(String key, Object value) {
        payload.put(key, value);
    }

    /**
     * Gets an object from the payload from a given key
     * @param key name of the object
     * @return the object in the hashmap with the given key
     */
    public Object getFromPayload(String key) {
        return this.payload.get(key);
    }

    /**
     * Changes the action of the packet
     * @param action the action the client wants to request
     */
    public void setAction(String action) {
        this.action = action;
    }
}
