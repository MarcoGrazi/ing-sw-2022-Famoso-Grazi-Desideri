package main.java.com.polimi.app.models;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Game State class.
 * @author Group 53
 */
public class GameState implements Serializable {
    private int maxPlId = 0;
    private int maxGmId = 0;
    private HashMap<Integer, String> nicknamesToReinsert = new HashMap<>();

    public int getMaxPlId() {
        return maxPlId;
    }

    public int getMaxGmId() {
        return maxGmId;
    }

    public HashMap<Integer, String> getNicknamesToReinsert() {
        return nicknamesToReinsert;
    }

    public void setMaxPlId(int maxPlId) {
        this.maxPlId = maxPlId;
    }

    public void setMaxGmId(int maxGmId) {
        this.maxGmId = maxGmId;
    }

    public void setNicknamesToReinsert(HashMap<Integer, String> nicknamesToReinsert) {
        this.nicknamesToReinsert = nicknamesToReinsert;
    }

    public void addNicknameToReinsert(int id, String nickname) {
        nicknamesToReinsert.put(id, nickname);
    }

    public void removeNicknameToReinsert(int id) {
        nicknamesToReinsert.remove(id);
    }
}
