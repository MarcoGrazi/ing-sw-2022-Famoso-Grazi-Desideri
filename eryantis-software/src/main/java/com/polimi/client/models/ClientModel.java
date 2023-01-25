package main.java.com.polimi.client.models;

import main.java.com.polimi.client.views.ViewInterface;

import java.util.Observable;
import java.util.Observer;

/**
 * Client model class.
 * Represents the client instance.
 */
public class ClientModel extends Observable{
    private  int gameId = -1;
    private int playerId;
    private int tempPlayerId;
    private boolean inAGame= false;
    private String nickname;
    private boolean isBlocked =true;
    private String phase= "Game not started";

    public ClientModel(ViewInterface view){
        addObserver(view);
    }


    public int getGameId() {
        return gameId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setGameId(int gameId) {

        this.gameId = gameId;
        this.inAGame=true;
        setChanged();
        notifyObservers(new Message("GAME_ID_SET"));
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
        setChanged();
        notifyObservers(new Message("PLAYER_ID_SET"));
    }

    public void setInAGame(boolean inAGame) {
        this.inAGame = inAGame;
    }

    public boolean isInAGame() {
        return inAGame;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
        setChanged();
        notifyObservers(new Message("PRINT_PLAYER"));
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        isBlocked = blocked;
        setChanged();
        notifyObservers(new Message("PRINT_PHASE"));
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public void setTempPlayerId(int tempPlayerId) {
        this.tempPlayerId = tempPlayerId;
        setChanged();
        notifyObservers(new Message("ATTEMPT_RECONNECTION"));
    }

    public Integer getTempPlayerId() {
        return tempPlayerId;

    }
    public void printPlayer(){
        setChanged();
        notifyObservers(new Message("PRINT_PLAYER"));
    }
}
