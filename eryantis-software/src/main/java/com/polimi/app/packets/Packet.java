package main.java.com.polimi.app.packets;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Packet class.
 * This class is specular to the server packet class to allow Gson to serialize and deserialize packets correctly and
 * move infos between server and client.
 */
public class Packet implements Serializable {
    private static final long serialVersionUID = 1L;
    //The action performed
    private String action;
    //The group of useful infos
    private HashMap<String, Object> payload = new HashMap<>();
    //The id of the player who sent the packet
    private int playerId;
    //The id of the game related to the packet
    private int gameId;

    /**
     * Creates a generic packet. Used in the first stages of the game, until the client joins a game.
     * @param action the action performed
     * @param playerId the id of the player who sent the packet
     */
    public Packet(String action, int playerId) {
        this.action = action;
        this.playerId = playerId;
    }

    /**
     * Creates a game packet. Used sfter the client joined a game.
     * @param action the action performed
     * @param playerId the id of the player who sent the packet
     * @param gameId the id of the game joined by the player
     */
    public Packet(String action, int playerId, int gameId) {
        this.action = action;
        this.playerId = playerId;
        this.gameId = gameId;
    }

    /**
     * @return the action performed
     */
    public String getAction() {
        return action;
    }

    /**
     * @return the whole payload
     */
    public HashMap<String, Object> getPayload() {
        return payload;
    }

    /**
     * @return the id of the player who sent the packet
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * @return the id of the game related to the packet
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * Adds an object to the payload
     * @param key the key of the object
     * @param value the object to add
     */
    public void addToPayload(String key, Object value) {
        payload.put(key, value);
    }

    /**
     * @param key the key of the object
     * @return the object related to the key
     */
    public Object getFromPayload(String key) {
        return this.payload.get(key);
    }
}
