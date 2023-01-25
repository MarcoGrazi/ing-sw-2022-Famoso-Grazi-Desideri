package main.java.com.polimi.app.controllers;

import com.google.gson.Gson;
import main.java.com.polimi.app.controllers.game_states.Rejoin;
import main.java.com.polimi.app.controllers.game_states.State;
import main.java.com.polimi.app.models.Colour;
import main.java.com.polimi.app.models.GameState;
import main.java.com.polimi.app.models.Mage;
import main.java.com.polimi.app.models.Player;
import main.java.com.polimi.app.packets.Packet;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Client handler controller class.
 * @author Group 53
 */
public class ClientHandlerController {

    private HashMap<Integer, ClientHandler> clientHandlers = new HashMap<>();
    private HashMap<Integer, GameController> gameControllers = new HashMap<>();
    private Gson gson = new Gson();
    FileWriter myWriter;
    GameState state;
    private String path;

    /**
     * @param path path where to find the checkpoints folder
     */
    public ClientHandlerController(String path) {
        this.path=path;
        File myObj = new File(this.path+"GameState.txt");
        if (!myObj.exists()) {
            try {
                myObj.createNewFile();
                state = new GameState();
                writeClear(state, "GameState");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            state = (GameState) read("GameState");
        }
    }

    /**
     * @param client clientHandler to add to the list of connected clients
     */
    public void addClient(ClientHandler client) {
        int playerId = state.getMaxPlId();
        clientHandlers.put(state.getMaxPlId(), client);
        client.setPlayerID(playerId);

        Packet packet = new Packet("INFO_CLIENT_CONNECTED", playerId);
        packet.addToPayload("message", "Reinsert your nickname");
        Gson gson = new Gson();

        String json = gson.toJson(packet);
        client.sendObject(json);

        state.setMaxPlId(state.getMaxPlId()+1);
    }

    /**
     * @param json json string of the packet received
     * @param c handler associated to the client who sent the request
     */
    public void received(String json, ClientHandler c) {
        Packet packet = gson.fromJson(json, Packet.class);
        String action = packet.getAction();

        if (action.equals("CREATE_GAME")) {
            String gameMode = (String) packet.getFromPayload("game_mode");
            int playerNumber = Math.round(Float.parseFloat(packet.getFromPayload("player_number").toString()));
            int gameId = state.getMaxGmId();

            GameController gc = new GameController(gameId, gameMode, playerNumber);
            gameControllers.put(gameId, gc);

            Mage mage;
            String mage_name = (String) packet.getFromPayload("mage_name");
            switch (mage_name) {
                case "JAFAR":
                    System.out.println(mage_name);
                    mage = Mage.JAFAR;
                    break;
                case "MORGANA":
                    mage = Mage.MORGANA;
                    break;
                case "MERLIN":
                    mage = Mage.MERLIN;
                    break;
                case "WONG":
                    mage = Mage.WONG;
                    break;
                default:
                    mage = Mage.MORGANA;
                    break;
            }

            Colour colour;
            String color = (String) packet.getFromPayload("tower_colour");
            switch (color) {
                case "WHITE":
                    colour = Colour.WHITE;
                    break;
                case "BLACK":
                    colour = Colour.BLACK;
                    break;
                case "GREY":
                    colour = Colour.GREY;
                    break;
                default:
                    colour = Colour.WHITE;
                    break;
            }

            Player player = new Player(packet.getPlayerId(), mage, colour);
            gc.getPlController().addPlayer(player);

            Packet packetToSend = new Packet("GAME_CREATED", packet.getPlayerId(), gameId);
            packetToSend.addToPayload("mode", gc.isSimpleMode() ? "S" : "E");
            packetToSend.addToPayload("playerId", player.getPlayerId());
            packetToSend.addToPayload("playerMage", mage_name);
            packetToSend.addToPayload("playerColour", colour);
            packetToSend.addToPayload("playerNumber", playerNumber);
            packetToSend.addToPayload("mode", gameMode);
            packetToSend.addToPayload("message", "Game created");
            //maxGmId++;
            state.setMaxGmId(state.getMaxGmId()+1);
            writeClear(state, "GameState");
            writeClear(gc, "GameController_" + gameId);
            c.sendObject(gson.toJson(packetToSend));

        } else if (action.equals("SET_NICKNAME")) {
            Integer matchingInOld = state.getNicknamesToReinsert().keySet().stream().
                    filter(key -> state.getNicknamesToReinsert().get(key) != null && state.getNicknamesToReinsert().get(key).equals(packet.getFromPayload("nickname"))).findAny().orElse(null);
            Integer matchingObject = clientHandlers.keySet().stream().
                    filter(key -> clientHandlers.get(key).getNickname() != null && clientHandlers.get(key).getNickname().equals(packet.getFromPayload("nickname"))).
                    findAny().orElse(null);
            if (matchingObject == null && matchingInOld == null) {
                clientHandlers.get(packet.getPlayerId()).setNickname((String) packet.getFromPayload("nickname"));
                Packet response = new Packet("SUCCESS_NICKNAME_SET", packet.getPlayerId());
                response.addToPayload("message", "Nickname SET");
                response.addToPayload("nickname", (String) packet.getFromPayload("nickname"));
                clientHandlers.get(packet.getPlayerId()).sendObject(gson.toJson(response));
                state.addNicknameToReinsert(packet.getPlayerId(), (String) packet.getFromPayload("nickname"));
                writeClear(state, "GameState");
            } else {
                Packet response = new Packet("DUPLICATE_NICKNAME", packet.getPlayerId());
                response.addToPayload("message", "Duplicate nickname");
                clientHandlers.get(packet.getPlayerId()).sendObject(gson.toJson(response));
            }

        } else if (action.equals("RESET_NICKNAME")) {
            //GameController gc= (GameController) read("GameController_" + packet.getGameId());
            int tmpPlayerId = (int) Math.round(Float.parseFloat(packet.getFromPayload("tmpPlId").toString()));
            if (state.getNicknamesToReinsert().containsKey(packet.getPlayerId())) {
                String nickname = state.getNicknamesToReinsert().get(packet.getPlayerId());
                if(nickname.equals(packet.getFromPayload("nickname"))){
                    ClientHandler ch = clientHandlers.get(tmpPlayerId);
                    ch.setNickname(nickname);
                    ch.setPlayerID(packet.getPlayerId());
                    clientHandlers.put(packet.getPlayerId(), ch);
                    clientHandlers.remove(tmpPlayerId);
                    //Reset the game if the user was in a game
                    int gameId= packet.getGameId();
                    if(gameId!=-1){
                      //look for a game in the game controllers
                        if(gameControllers.containsKey(gameId)) {
                            GameController gc = gameControllers.get(gameId);
                            ArrayList<Packet> packetsToSend = gc.handle(packet);
                            writeClear(gc, "GameController_" + gameId);
                            for (Packet p : packetsToSend) {
                                clientHandlers.get(p.getPlayerId()).sendObject(gson.toJson(p));
                            }
                        }else {
                            GameController gc= (GameController) read("GameController_" + packet.getGameId());
                            if(gc!=null){
                                State current = new Rejoin(gc);
                                gc.setState(current);
                                ArrayList<Packet> packetsToSend = gc.handle(packet);
                                gameControllers.put(packet.getGameId(), gc);
                                writeClear(gc, "GameController_" + gameId);
                                for (Packet p : packetsToSend) {
                                    clientHandlers.get(p.getPlayerId()).sendObject(gson.toJson(p));
                                }
                            }
                            else {
                                Packet response = new Packet("ERROR", packet.getPlayerId());
                                response.addToPayload("message", "game not found create a new one");
                                response.addToPayload("nickname", (String) packet.getFromPayload("nickname"));
                                clientHandlers.get(packet.getPlayerId()).sendObject(gson.toJson(response));
                            }

                        }
                    }else{
                        Packet response = new Packet("SUCCESS_NICKNAME_SET", packet.getPlayerId());
                        response.addToPayload("message", "nickname SET");
                        response.addToPayload("nickname", (String) packet.getFromPayload("nickname"));
                        writeClear(state, "GameState");
                        clientHandlers.get(packet.getPlayerId()).sendObject(gson.toJson(response));
                    }

                }else{
                    Packet response = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                    response.addToPayload("message", "Wrong old nickname");
                    clientHandlers.get(tmpPlayerId).sendObject(gson.toJson(response));
                    return;
                }

            } else {
                Packet response = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                response.addToPayload("message", "No nicknames to reset please use the set_nickname command");
                clientHandlers.get(tmpPlayerId).sendObject(gson.toJson(response));
                return;

            }

        } else if (action.equals("FETCH_GAMES")) {
            Packet response = new Packet("GAMES", packet.getPlayerId());
            for (int key : gameControllers.keySet()) {
                GameController g = gameControllers.get(key);

                HashMap<String, Object> game = new HashMap<>();
                game.put("empty", g.getPlayerNumber() - g.getPlController().getPlayerIds().size());

                String mode = g.isSimpleMode() ? "S" : "E";
                game.put("mode", mode);

                ArrayList<String> mages = new ArrayList<>();
                for (Player p : g.getPlController().getPlayers()) {
                    mages.add(p.getMageName().getMage());
                }
                game.put("mages", mages);

                ArrayList<String> towers=new ArrayList<>();
                for(Player p: g.getPlController().getPlayers()){
                    towers.add(p.getSchoolColour().getAbbreviation());
                }
                game.put("towers", towers);
                System.out.println(g.getGameId());
                response.addToPayload(String.valueOf(g.getGameId()), game);

                game.put("mode", g.isSimpleMode() ? "S" : "E");
            }
            clientHandlers.get(packet.getPlayerId()).sendObject(gson.toJson(response));
        } else {
            int gameId = packet.getGameId();
            if (!gameControllers.containsKey(gameId)) {
                Packet response = new Packet("NO_SUCH_GAME", packet.getPlayerId(), packet.getGameId());
                response.addToPayload("message", "The game does not exist or has already started");
                clientHandlers.get(packet.getPlayerId()).sendObject(gson.toJson(response));
                return;
            }
            GameController gc = gameControllers.get(gameId);
            ArrayList<Packet> packetsToSend = gc.handle(packet);
            writeClear(gc, "GameController_" + gameId);
            for (Packet packet1 : packetsToSend) {
                ClientHandler ch = clientHandlers.get(packet1.getPlayerId());
                ch.sendObject(gson.toJson(packet1));
                if(packet1.getAction().equals("END_GAME")){
                    removeGame(packet.getGameId());
                }
            }
        }

    }

    /**
     * @param gameID
     * deletes a checkpoint file when a game ends
     */
    public void removeGame(int gameID){
        for (int key : gameControllers.keySet()) {
            GameController gc = gameControllers.get(key);
            if(gc.getGameId()==gameID){
                gameControllers.remove(key);
                File fileToRemove= new File(this.path+"GameController_"+key+".txt");
                fileToRemove.delete();
                writeClear(state,"GameState");
                break;
            }
        }
    }


    /**
     * @param playerID
     * removes a client from the list when it closes the connection
     */
    public void removeClient(int playerID) {
        clientHandlers.remove(playerID);
        state.removeNicknameToReinsert(playerID);


        //a client can only be in one game at a time
        //look for the game that contains the player
        for (int key : gameControllers.keySet()) {
            GameController gc = gameControllers.get(key);
            if (gc.getPlController().getPlayerIds().contains(playerID)) {
                for (int plId : gc.getPlController().getPlayerIds()) {
                    if (plId != playerID) {
                        Packet packet = new Packet("PLAYER_LEFT", plId);
                        packet.addToPayload("playerId", playerID);
                        clientHandlers.get(plId).sendObject(gson.toJson(packet));
                    }
                }
                gameControllers.remove(key);
                writeClear(state,"GameState");
                File fileToRemove= new File(this.path+"GameController_"+key+".txt");
                fileToRemove.delete();

                break;
            }
        }
    }


    /**
     * @param o object to write
     * @param fileName
     * writes on a file cleaning it first
     */
    public void writeClear(Object o, String fileName) {
        try {
            FileOutputStream myFileOutStream
                    = new FileOutputStream(
                    this.path+fileName+".txt");
            ObjectOutputStream myObjectOutStream
                    = new ObjectOutputStream(myFileOutStream);

            myObjectOutStream.writeObject(o);

            // closing FileOutputStream and
            // ObjectOutputStream
            myObjectOutStream.close();
            myFileOutStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param filename
     * @return object read
     * reads from a file
     */
    public Object read (String filename){
        Object o = null;
        try {
            FileInputStream myFileInStream
                    = new FileInputStream(
                    this.path+filename+".txt");
            ObjectInputStream myObjectInStream
                    = new ObjectInputStream(myFileInStream);

            o = myObjectInStream.readObject();

            // closing FileInputStream and
            // ObjectInputStream
            myObjectInStream.close();
            myFileInStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return o;
    }
}
