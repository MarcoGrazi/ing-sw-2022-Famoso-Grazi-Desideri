package main.java.com.polimi.client.models.client_states;

import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * In this state the client hasn't set a nickname yet, the only action they can perform is set the nickname or fetch the games
 */
public class NickNameNotSet  implements State{
    private final ArrayList<String> allowedClientActions= new ArrayList<>(Arrays.asList("SET_NICKNAME", "FETCH_GAMES"));
    private ClientController context;

    /**
     * @param context client controller
     */
    public NickNameNotSet(ClientController context) {
        this.context = context;
    }

    /**
     * @param packet packet from client to verify
     * @throws Exception
     */
    @Override
    public void verify(Packet packet) throws Exception {
        //takes the action form the packet and verify it's  a valid action to be performed in this stage
        String action = packet.getAction();
        if (allowedClientActions.contains(action)) {

            context.sendObject(packet);
        } else {
            throw new Exception("IllegalActionException. Allowed actions: "+allowedClientActions.toString());

        }
    }

    /**
     * @param packet packet from server to handle
     * @throws IOException
     */
    @Override
    public void handle(Packet packet) throws IOException {
        String action = packet.getAction();
        switch (action){
            case "SUCCESS_NICKNAME_SET":
                context.setNickName((String) packet.getFromPayload("nickname"));
                changeState(new NotInAGame(context));
                break;
            case "DUPLICATE_NICKNAME":
                context.getClientView().printNicknameWarning();
                break;
            case "INFO_CLIENT_CONNECTED":
                context.getClientModel().setPlayerId(packet.getPlayerId());
                String message = "Set your unique nickname";
                context.getClientView().printMessage(message);
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
