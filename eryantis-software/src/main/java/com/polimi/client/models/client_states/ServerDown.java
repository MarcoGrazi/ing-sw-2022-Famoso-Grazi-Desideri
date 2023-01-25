package main.java.com.polimi.client.models.client_states;

import com.google.gson.internal.LinkedTreeMap;
import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * In this state the server is down so the client can ping the server, set a nickname after the server
 * responds or reset their nickname
 */
public class ServerDown  implements State{

    private ClientController context;
    private State previousState;
    private final ArrayList<String> allowedClientActions= new ArrayList<>(Arrays.asList("PING_SERVER","RESET_NICKNAME", "SET_NICKNAME"));

    /**
     * @param context current client controller
     * @param previousState previous state of the client
     */
    public ServerDown(ClientController context, State previousState) {
        this.context = context;
        System.out.println("Server is down, try to ping with ping_server command");
        this.previousState = previousState;
    }

    /**
     * if the action is ping server then calls connect
     * elsewhere sends a packet
     * @param packet packet from client to verify
     * @throws Exception
     */
    @Override
    public void verify(Packet packet) throws Exception {
        String action = packet.getAction();
        if (allowedClientActions.contains(action)) {
            if(action.equals("PING_SERVER")) {
                context.connect();
            }else{
                context.sendObject(packet);
            }
        } else {
            throw new Exception("IllegalActionException. Allowed actions: "+allowedClientActions.toString());

        }
    }

    @Override
    public void reset() {

    }

    /**
     * @param packet packet from server to handle
     * @throws Exception
     */
    @Override
    public void handle(Packet packet) throws Exception {
        String action = packet.getAction();
        String message;

        switch (action) {
            case "INFO_CLIENT_CONNECTED":
                context.getClientModel().setTempPlayerId(packet.getPlayerId());
                message= (String) packet.getFromPayload("message");
                context.getClientView().printMessage(message);
                break;
            case "SUCCESS_NICKNAME_SET":
                context.getClientModel().setPlayerId(packet.getPlayerId());
                changeState(new NotInAGame(context));
                context.setNickName((String) packet.getFromPayload("nickname"));
                break;
            case "DUPLICATE_NICKNAME":
                context.getClientModel().setPlayerId(packet.getPlayerId());
                context.getClientView().printNicknameWarning();
                changeState(new NickNameNotSet(context));
                break;
            case "ERROR":
                message= (String) packet.getFromPayload("message");
                context.getClientView().printErrorMessage(message);
                break;
            case "SUCCESS_GAME_REJOINED":
                changeState(new Locked(context, previousState));
                context.setNickName((String) packet.getFromPayload("nickname"));
                context.getClientModel().setPlayerId(packet.getPlayerId());
                context.printGameRejoined();
                break;
            case "SUCCESS_GAME_RESTARTING":
                context.setNickName((String) packet.getFromPayload("nickname"));

                previousState.switchContext(context);
                resetState(previousState);

                LinkedTreeMap<String, Object> archipelagoView = (LinkedTreeMap<String, Object>) packet.getFromPayload("archipelago_view");
                if(archipelagoView!=null){
                    context.updateArchipelago(archipelagoView);
                }
                LinkedTreeMap<String,Object> schoolView = (LinkedTreeMap<String, Object>) packet.getFromPayload("school_view");
                if(schoolView!=null){
                    context.resetSchool(schoolView, packet.getPlayerId());
                }
                LinkedTreeMap<String,Object> cloudView = (LinkedTreeMap<String, Object>) packet.getFromPayload("cloud_view");
                if(cloudView!=null){
                    context.resetClouds(cloudView);
                }
                LinkedTreeMap<String,Object> assistantDeckView = (LinkedTreeMap<String, Object>) packet.getFromPayload("assistant_deck");
                if(assistantDeckView!=null){
                    context.resetDeck(assistantDeckView);
                }
                context.printPlayer();
                break;
        }
    }

    /**
     * Reset the state of the game with a previous checkpoint state
     * @param state the previous state
     */
    public void resetState(State state) {
        context.resetState(state);
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
}
