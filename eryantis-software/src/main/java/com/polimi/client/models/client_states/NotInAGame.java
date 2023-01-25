package main.java.com.polimi.client.models.client_states;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import main.java.com.polimi.app.models.Colour;
import main.java.com.polimi.app.models.Mage;
import main.java.com.polimi.app.models.Player;
import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.utils.ColorStrings;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * In this state the client is not in a game. They can either create one, join one or fetch the existing games.
 */
public class NotInAGame  implements State{
    private ClientController context;
    private final ArrayList<String> allowedClientActions= new ArrayList<>(Arrays.asList("FETCH_GAMES", "JOIN_GAME", "CREATE_GAME"));

    /**
     * sets the game id to -1
     * sets the player to blocked
     * sets in a game as false
     * @param context client controller
     */
    public NotInAGame(ClientController context) {
        this.context = context;
        context.getClientModel().setPhase(ColorStrings.ERROR+"Not in a game"+ColorStrings.RESET);
        context.getClientModel().setBlocked(true);
        context.getClientModel().setGameId(-1);
        context.getClientModel().setInAGame(false);
    }
    private Gson gson = new Gson();

    /**
     * @param packet packet from client to verify
     * @throws Exception
     */
    @Override
    public void verify(Packet packet) throws Exception {
        String action = packet.getAction();
        if (allowedClientActions.contains(action)) {

            context.sendObject(packet);
        } else {
            throw new Exception("IllegalActionException. Allowed actions: "+allowedClientActions.toString());

        }
    }

    /**
     * @param packet packet from server to handle
     */
    @Override
    public void handle(Packet packet) {
        String action = packet.getAction();
        switch (action){
            case "GAME_CREATED":
            case "GAME_JOINED":
                context.setMode(packet.getFromPayload("mode").toString());
                context.setGameId(packet.getGameId());
                context.createPlayer((int) Math.round((Double) packet.getFromPayload("playerId")),(String)packet.getFromPayload("playerMage"), (String)packet.getFromPayload("playerColour"),
                        (int)Math.round((Double) packet.getFromPayload("playerNumber")));

                context.getClientView().printMessage(packet.getFromPayload("message"));
                changeState(new Locked(context));
                break;
            case "GAME_STARTING":
                context.setGameId(packet.getGameId());

                Boolean locked = (Boolean) packet.getFromPayload("blocked");
                String player_colour = (String) packet.getFromPayload("playerColour");
                context.createSchool(player_colour, (int)Math.round((Double) packet.getFromPayload("playerNumber")), (LinkedTreeMap<String, Object>) packet.getFromPayload("school_view"), (LinkedTreeMap<String, Object>) packet.getFromPayload("cloud_view"));
                context.createArchipelago((LinkedTreeMap<String, Object>) packet.getFromPayload("archipelagoView"));
                context.createDeck();
                if(context.getVisMode().equals("GUI") && context.getMode().equals("E")){
                    context.initializeCharacterPanel();
                }
                if(locked){
                    changeState(new Locked(context));
                }else{
                    changeState(new UnlockedPlanning(context));
                }
                break;
            case"GAMES":
                context.getClientView().printGames(packet);
                break;
            case "NO_SUCH_GAME":
            case "ERROR":
                context.getClientView().printErrorMessage(packet.getFromPayload("message").toString());
                break;
        }
    }

    /**
     * @param state next state
     */
    @Override
    public void changeState(State state) {
        context.setState(state);
    }

    /**
     * @param context previous context
     */
    @Override
    public void switchContext(ClientController context) {
        this.context=context;
    }

    @Override
    public void reset() {

    }
}
