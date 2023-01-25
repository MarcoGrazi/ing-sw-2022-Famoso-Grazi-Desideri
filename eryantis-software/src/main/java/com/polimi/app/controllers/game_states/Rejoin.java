package main.java.com.polimi.app.controllers.game_states;

import main.java.com.polimi.app.controllers.GameController;
import main.java.com.polimi.app.packets.Packet;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Rejoin state.
 * @author Group 53
 */
public class Rejoin implements State, Serializable {
    private final ArrayList<String> validActions = new ArrayList<>();
    private GameController context;
    private final ArrayList<Integer> missingPlayers;
    private final State previousState;
    public Rejoin(GameController context) {
        this.context = context;
        this.previousState = context.getState();
        this.missingPlayers = context.getPlController().getPlayerIds();
        validActions.add("RESET_NICKNAME");
    }

    @Override
    public ArrayList<Packet> verify(Packet packet) {
        ArrayList<Packet> packets = new ArrayList<>();
        if(validActions.contains(packet.getAction())){
            return handle(packet);
        }else{
            Packet response = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
            response.addToPayload("message", "Forbidden action");
            packets.add(response);
            return packets;
        }
    }

    @Override
    public ArrayList<Packet> handle(Packet packet) {
        ArrayList<Packet> packets = new ArrayList<>();
        if(missingPlayers.contains(packet.getPlayerId())){
            missingPlayers.remove((Integer) packet.getPlayerId());
            if(missingPlayers.isEmpty()){
                for(Integer i : context.getPlController().getPlayerIds()){
                    Packet response = new Packet("SUCCESS_GAME_RESTARTING", i, packet.getGameId());
                    response.addToPayload("archipelago_view",context.getArchipelagoController()!=null ? context.getArchipelagoController().getArchipelago().getArchipelagoInfo() : null);
                    response.addToPayload("cloud_view", context.getCloudController() != null ?  context.getCloudController().getCloudsInfo():null);
                    response.addToPayload("school_view", context.getSchoolController() !=null ? context.getSchoolController().getSchoolInfo(i):null);
                    response.addToPayload("assistant_deck",context.getAssistantDeckController() !=null ? context.getAssistantDeckController().getDeck(i).encodeDeckInfo():null);
                    response.addToPayload("nickname", (String) packet.getFromPayload("nickname"));

                    packets.add(response);
                }
                previousState.switchContext(context);
                changeState(previousState);
            }else{
                Packet response = new Packet("SUCCESS_GAME_REJOINED", packet.getPlayerId(), packet.getGameId());
                response.addToPayload("archipelago_view",context.getArchipelagoController()!=null ? context.getArchipelagoController().getArchipelago().getArchipelagoInfo() : null);
                response.addToPayload("cloud_view", context.getCloudController() != null ?  context.getCloudController().getCloudsInfo():null);
                response.addToPayload("school_view", context.getSchoolController() !=null ? context.getSchoolController().getSchoolInfo(packet.getPlayerId()):null);
                response.addToPayload("assistant_deck",context.getAssistantDeckController() !=null ? context.getAssistantDeckController().getDeck(packet.getPlayerId()).encodeDeckInfo():null);
                response.addToPayload("nickname", (String) packet.getFromPayload("nickname"));

                packets.add(response);
            }
        }
        return packets;
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
