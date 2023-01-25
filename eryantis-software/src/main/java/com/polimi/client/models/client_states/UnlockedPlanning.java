package main.java.com.polimi.client.models.client_states;

import com.google.gson.internal.LinkedTreeMap;
import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.utils.ColorStrings;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * In this state the client is in planning phase so they can perform only the actions of the planning phase
 * also this phase can't end before they played an assistant
 */
public class UnlockedPlanning implements State {
    private ClientController context;
    private final ArrayList<String> allowedClientActions= new ArrayList<>(Arrays.asList("PLAY_ASSISTANT", "PLAY_CHARACTER", "END_PLANNING", "FETCH_CHARACTERS"));


    /**
     * sets blocked to false
     * @param context client controller
     */
    public UnlockedPlanning(ClientController context) {
        this.context=context;
        context.getClientModel().setPhase(ColorStrings.SUCCESS+"Planning Phase"+ ColorStrings.RESET);
        context.getClientModel().setBlocked(false);
    }

    /**
     * verifies the player hasn't played the assistant
     * @param packet packet from client to verify
     * @throws Exception
     */
    @Override
    public void verify(Packet packet) throws Exception {
        String action = packet.getAction();
        if (allowedClientActions.contains(action)) {
            if(action.equals("PLAY_ASSISTANT")) {
                int assistantId= (int) Math.floor(Float.parseFloat(packet.getFromPayload("assistant").toString()));
                if(context.getDeck().getAssistantsHand().contains(assistantId))
                    context.sendObject(packet);
                else{
                    context.getClientView().printErrorMessage("You already played this card");
                    throw new Exception("Card already played");
                }
            } else {
                context.sendObject(packet);
            }
        } else {
            this.context.getClientView().printErrorMessage("You cannot perform this action now");
            throw new Exception("IllegalActionException. Allowed actions: " + allowedClientActions.toString());
        }
    }

    /**
     * @param packet packet from server to handle
     * @throws Exception
     */
    @Override
    public void handle(Packet packet) throws Exception {
        String action = packet.getAction();
        LinkedTreeMap<String, Object> archipelagoView;
        LinkedTreeMap<String, Object> hallView;
        int assistantId;
        int characterId;
        int coins;

        switch (action){
            case "CARD_ALREADY_PLAYED":
                String message = "Card has already been played by another player";
                context.getClientView().printErrorMessage(message);
                break;
            case "UNLOCK_ACTION":
                context.getClientView().printMessage(packet.getFromPayload("message"));
                changeState(new UnlockedAction(context));
                break;
            case "LOCK":
                context.getClientView().printMessage(packet.getFromPayload("message"));
                changeState(new Locked(context));
                break;
            case "ASSISTANT_PLAYED":
                assistantId = (int) Math.floor(Float.parseFloat(packet.getFromPayload("assistantId").toString()));
                System.out.println("Played card: " + assistantId);
                context.getDeck().discardAssistant(assistantId);
                break;
            case "CHARACTER_PLAYED":
                characterId = (int) Math.floor(Float.parseFloat(packet.getFromPayload("characterId").toString()));
                System.out.println("Played character: " + characterId);
                break;
            case "ERROR":
                context.getClientView().printErrorMessage(packet.getFromPayload("message").toString());
                break;
            case "PLAYER_LEFT":
                changeState(new NotInAGame(context));
                context.getClientModel().printPlayer();

                break;
            case "PLAYED_EFFECT":
                hallView = (LinkedTreeMap<String, Object>) packet.getFromPayload("student_view");
                if(hallView != null) {
                    ArrayList<Double> studentsHall = (ArrayList<Double>) hallView.get("students_hall");
                    context.updateHall(studentsHall,-1);
                    context.printSchool();
                }

                LinkedTreeMap<String , Object> schoolViewFromEffect =(LinkedTreeMap<String , Object>)packet.getFromPayload("school_view");
                if(schoolViewFromEffect!=null){
                    ArrayList<Double> professorsView =(ArrayList<Double>) schoolViewFromEffect.get("prof_schoolIds");
                    ArrayList<Double> hallToUpdate= (ArrayList<Double>) schoolViewFromEffect.get("students_hall");
                    ArrayList<Double> table = (ArrayList<Double>) schoolViewFromEffect.get("tables");
                    context.updateHall(hallToUpdate,-1);
                    context.updateTable(table);
                    context.updateProfessors(professorsView, packet.getPlayerId());
                    context.printSchool();
                }

                archipelagoView = (LinkedTreeMap<String, Object>) packet.getFromPayload("archipelago_view");
                if(archipelagoView!=null){
                    context.updateArchipelago(archipelagoView);
                }

                if(packet.getFromPayload("coins") != null) {
                    coins = (int) Math.round(Float.parseFloat(packet.getFromPayload("coins").toString()));
                    context.updateBalance(coins);
                }
                break;
            case "CHARACTERS":
                context.getClientView().printCharacters(packet);
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
        context.getClientModel().setPhase(ColorStrings.ERROR+"Planning Phase"+ColorStrings.RESET);
        context.getClientModel().setBlocked(false);
    }
}
