package main.java.com.polimi.app.controllers.game_states;

import main.java.com.polimi.app.controllers.GameController;
import main.java.com.polimi.app.models.AssistantDeck;
import main.java.com.polimi.app.models.Colour;
import main.java.com.polimi.app.models.Mage;
import main.java.com.polimi.app.models.Player;
import main.java.com.polimi.app.packets.Packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Waiting For Opponents State.
 * In this state the game is waiting for enough player to finally initialize and start the game.
 * The players already inside the game can exit the game. New players can join the game by choosing a mage and a colour
 * between the ones that are not already assigned to the other players.
 * @author Group 53
 */
public class WaitingForOpponentsState implements State, Serializable {
    //The game controller
    private GameController context;
    //The actions each player can perform during this stage
    private final String[] validActions = {"JOIN_GAME", "EXIT_GAME"};

    /**
     * Class constructor. Initializes the context.
     * @param context the game state
     */
    public WaitingForOpponentsState(GameController context) {
        this.context = context;
    }

    /**
     * @param packet the pack of information necessary to handle the chosen action
     * @return ERROR packets if the action is forbidden or the mage or the colour chosen are already taken
     */
    @Override
    //Takes the action form the packet and verify that's a valid action to be performed in this stage of the game.
    public ArrayList<Packet> verify(Packet packet) {
        ArrayList<Packet> packets = new ArrayList<>();
        String action = packet.getAction();
        if (Arrays.asList(validActions).contains(action)) {
            if(action.equals("JOIN_GAME")){
                ArrayList<Player> players= context.getPlController().getPlayers();
                for(Player p: players){
                    if(p.getMageName().getMage().equals(packet.getFromPayload("mage_name")) ||
                            p.getSchoolColour().getAbbreviation().equals(packet.getFromPayload("tower_colour"))
                    ){
                        Packet response = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                        response.addToPayload("message", "Mage name or tower colour already chosen");
                        packets.add(response);
                        return packets;
                    }
                }
            }
            return handle(packet);
        } else {
            Packet response = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
            response.addToPayload("message", "Forbidden action");
            packets.add(response);
            return packets;
        }
    }

    /**
     * @param packet the pack of information necessary and handle the chosen action
     * @return GAME_JOINED, GAME_STARTING or INFO packets
     */
    @Override
    public ArrayList<Packet> handle(Packet packet) {
        //Takes the action from the packet and performs it
        String action = packet.getAction();
        ArrayList<Packet> packets = new ArrayList<>();

        switch (action) {
            //if the action is JOIN_GAME verify whether the game has reached the needed number of players, otherwise adds a player.
            case "JOIN_GAME":
                Mage mage;
                String mageName = (String) packet.getFromPayload("mage_name");
                switch (mageName) {
                    case "JAFAR":
                        System.out.println(mageName);
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
                String towerColour = (String) packet.getFromPayload("tower_colour");
                switch (towerColour) {
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
                context.getPlController().addPlayer(player);

                Packet responseJoin = new Packet("GAME_JOINED", packet.getPlayerId(), context.getGameId());
                responseJoin.addToPayload("mode", context.isSimpleMode() ? "S" : "E");
                responseJoin.addToPayload("playerId", player.getPlayerId());
                responseJoin.addToPayload("playerMage", mage);
                responseJoin.addToPayload("playerColour", colour);
                responseJoin.addToPayload("playerNumber", context.getPlayerNumber());
                responseJoin.addToPayload("message", "Joined the game");
                packets.add(responseJoin);

                //verify if the game has enough players to start
                if (context.getPlController().getPlayers().size() == context.getPlayerNumber()) {
                    //The initialization changes the state
                    HashMap<String, Object> gameInit = context.initializeGame();
                    changeState(new PlanningPhaseWaitingState(context));

                    //Each player is notified with a "GAME_STARTING" packet
                    for (int i =0; i<context.getPlayerNumber(); i++){
                        int playerId = context.getPlController().getPlayerIds().get(i);
                        Packet gameStartPacket = new Packet("GAME_STARTING", playerId, context.getGameId());
                        gameStartPacket.addToPayload("blocked", i != 0);

                        HashMap<Integer,Object> schoolInit = (HashMap<Integer,Object>) gameInit.get("schools");
                        gameStartPacket.addToPayload("school_view", schoolInit.get(playerId));
                        gameStartPacket.addToPayload("cloud_view", gameInit.get("clouds"));
                        gameStartPacket.addToPayload("playerColour", context.getPlController().getSchoolColourById(playerId));
                        gameStartPacket.addToPayload("playerNumber", context.getPlayerNumber());
                        gameStartPacket.addToPayload("archipelagoView", gameInit.get("archipelago"));

                        packets.add(gameStartPacket);
                    }

                }
                System.out.println("sending all packets");
                return packets;
            //if the action is EXIT_GAME disconnects the player
            case "EXIT_GAME":
                context.getPlController().removePlayer(packet.getPlayerId());
                Packet responseExit = new Packet("INFO", packet.getPlayerId(), context.getGameId());
                responseExit.addToPayload("message", "Exited the game");
                packets.add(responseExit);
                return packets;

            default:
                throw new IllegalStateException("Unexpected value: " + action);
        }
    }

    @Override
    public void changeState(State state) {
        context.setState(state);
    }
    @Override
    public void switchContext(GameController context) {
        this.context = context;
    }
}
