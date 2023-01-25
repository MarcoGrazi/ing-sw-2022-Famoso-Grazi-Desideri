package main.java.com.polimi.client.models.client_states;

import com.google.gson.internal.LinkedTreeMap;
import main.java.com.polimi.app.utils.Utils;
import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.utils.ColorStrings;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * In this state the client is in the action phase. They can perform only the actions in the action phase
 */
public class UnlockedAction implements State {
    private ClientController context;
    private final ArrayList<String> allowedClientActions= new ArrayList<>(Arrays.asList("MOVE_STUDENT", "MOVE_MN",
            "GET_STUDENTS", "PLAY_CHARACTER", "FETCH_CHARACTERS"));

    /**
     * srts blocked to false
     * @param context client controller
     */
    public UnlockedAction(ClientController context) {
        this.context=context;
        context.getClientModel().setPhase(ColorStrings.SUCCESS+"Action Phase"+ColorStrings.RESET);
        context.getClientModel().setBlocked(false);
    }

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
            this.context.getClientView().printErrorMessage("You cannot perform this action now");
            throw new Exception("IllegalActionException. Allowed actions: "+allowedClientActions.toString());
        }
    }

    /**
     * @param packet packet from server to handle
     */
    @Override
    public void handle(Packet packet) {
        String action = packet.getAction();
        String oldColour;
        String newColour;
        LinkedTreeMap<String, Object> archipelago_payload;
        LinkedTreeMap<String, Object> archipelagoView;
        LinkedTreeMap<String,Object> schoolView;
        LinkedTreeMap<String, Object> cloudView;
        LinkedTreeMap<String,Object> clouds;
        ArrayList<Double> hall;
        int cloudId;
        int towerQuantity;
        int characterId;
        int coins;

        switch (action){
            case "ERROR":
                context.getClientView().printErrorMessage(packet.getFromPayload("message").toString());
                break;
            case "CHARACTER_PLAYED":
                characterId = (int) Math.floor(Float.parseFloat(packet.getFromPayload("characterId").toString()));
                System.out.println("Played character: " + characterId);
                break;
            case "STUDENT_MOVED_T":
                LinkedTreeMap<String, Object> stud = (LinkedTreeMap<String, Object>) packet.getFromPayload("students_view");
                LinkedTreeMap<String,Object> table = (LinkedTreeMap<String, Object>) packet.getFromPayload("professors_view");
                int std_i = (int) Math.round(Float.parseFloat(packet.getFromPayload("std_i").toString()));
                context.updateSchool(stud,table, std_i, packet.getPlayerId());

                if(stud.containsKey("coins")) {
                    coins = (int) Math.round(Float.parseFloat(stud.get("coins").toString()));
                    context.updateBalance(coins);
                }
                break;
            case "STUDENT_MOVED_I":
                LinkedTreeMap<String, Object> arch_container = (LinkedTreeMap<String, Object>) packet.getFromPayload("archipelago_view");
                LinkedTreeMap<String, Object> arch_view = (LinkedTreeMap<String, Object>) arch_container.get("archipelago_view");

                int std_index = (int) Math.round((Double) Double.parseDouble(packet.getFromPayload("std_i").toString()));
                context.updateArchipelago(arch_view);
                context.updateHallIsland(std_index);
                break;
            case "MOVED_MN":
                archipelago_payload = (LinkedTreeMap<String, Object>) packet.getFromPayload("archipelago_view");
                archipelagoView = (LinkedTreeMap<String, Object>) archipelago_payload.get("archipelago_view");
                context.updateArchipelago(archipelagoView);

                oldColour= archipelago_payload.get("old_colour").toString();
                newColour= archipelago_payload.get("new_colour").toString();
                towerQuantity= (int) Math.round(Float.parseFloat(archipelago_payload.get("tower_quantity").toString()));
                if(!oldColour.equals("") && (oldColour).equals(context.getPlayer().getSchoolColour().name())){
                    context.setSchoolTowers(towerQuantity, "ADD");
                }
                if(!newColour.equals("") && (newColour).equals(context.getPlayer().getSchoolColour().name())){
                    context.setSchoolTowers(towerQuantity, "SUB");
                }

                context.getClientView().printMessage(archipelago_payload.get("message"));
                break;
            case "LOCK_PLANNING":
                boolean isLastTurn = (boolean) packet.getFromPayload("is_last_turn");

                schoolView = (LinkedTreeMap<String,Object>) packet.getFromPayload("school_view");
                hall = (ArrayList<Double>) schoolView.get("students_hall");
                cloudId= (int) Math.round(Float.parseFloat(packet.getFromPayload("cloud_id").toString()));
                context.updateHall(hall, cloudId);
                context.printSchool();

                if(!isLastTurn) {
                    cloudView = (LinkedTreeMap<String, Object>) packet.getFromPayload("clouds_view");
                    context.resetClouds(cloudView);
                }

                context.getClientView().printMessage(packet.getFromPayload("message"));
                changeState(new Locked(context));
                break;
            case "LOCK_ACTION":
                cloudId= (int) Math.round(Float.parseFloat(packet.getFromPayload("cloud_id").toString()));
                schoolView = (LinkedTreeMap<String,Object>) packet.getFromPayload("school_view");
                if(schoolView!=null){
                    hall = (ArrayList<Double>) schoolView.get("students_hall");
                    context.updateHall(hall, cloudId);
                    context.printSchool();
                }else{
                    context.updateClouds(cloudId);
                }

                changeState(new Locked(context));
                break;
            case "LOCK_LAST_ACTION":
                archipelago_payload = (LinkedTreeMap<String, Object>) packet.getFromPayload("archipelago_view");
                archipelagoView = (LinkedTreeMap<String, Object>) archipelago_payload.get("archipelago_view");
                context.updateArchipelago(archipelagoView);

                oldColour= archipelago_payload.get("old_colour").toString();
                newColour= archipelago_payload.get("new_colour").toString();
                towerQuantity= (int) Math.round(Float.parseFloat(archipelago_payload.get("tower_quantity").toString()));
                if(!oldColour.equals("") && (oldColour).equals(context.getPlayer().getSchoolColour().name())){
                    context.setSchoolTowers(towerQuantity, "ADD");
                }
                if(!newColour.equals("") &&(newColour).equals(context.getPlayer().getSchoolColour().name())){
                    context.setSchoolTowers(towerQuantity, "SUB");
                }

                context.getClientView().printMessage(packet.getFromPayload("message"));
                changeState(new Locked(context));
                break;
            case "END_GAME":
                archipelago_payload = (LinkedTreeMap<String, Object>) packet.getFromPayload("archipelago_view");
                archipelagoView = (LinkedTreeMap<String, Object>) archipelago_payload.get("archipelago_view");
                context.updateArchipelago(archipelagoView);

                oldColour= archipelago_payload.get("old_colour").toString();
                newColour= archipelago_payload.get("new_colour").toString();
                towerQuantity= (int) Math.round(Float.parseFloat(archipelago_payload.get("tower_quantity").toString()));
                if(!oldColour.equals("") && (oldColour).equals(context.getPlayer().getSchoolColour().name())){
                    context.setSchoolTowers(towerQuantity, "ADD");
                }
                if(!newColour.equals("") && (newColour).equals(context.getPlayer().getSchoolColour().name())){
                    context.setSchoolTowers(towerQuantity, "SUB");
                }

                int winner = (int) Math.round(Float.parseFloat(packet.getFromPayload("winner").toString()));
                if( winner == -1 ) {
                    context.getClientView().printMessage("Game ends EVENLY");
                } else if( winner == packet.getPlayerId()){
                    context.getClientView().printMessage("You are the WINNER !!");
                } else {
                    context.getClientView().printMessage("Unfortunately, you LOST. Player " + winner + " WINS!");
                }
                changeState(new NotInAGame(context));
                context.getClientModel().printPlayer();
                break;
            case "PLAYER_LEFT":
                changeState(new NotInAGame(context));
                context.getClientModel().printPlayer();
                break;
            case "PLAYED_EFFECT":
                LinkedTreeMap<String, Object> hallView = (LinkedTreeMap<String, Object>) packet.getFromPayload("student_view");
                if(hallView != null) {
                    ArrayList<Double> studentsHall = (ArrayList<Double>) hallView.get("students_hall");
                    context.updateHall(studentsHall,-1);
                    context.printSchool();
                }

                LinkedTreeMap<String , Object> schoolViewFromEffect =(LinkedTreeMap<String , Object>)packet.getFromPayload("school_view");
                if(schoolViewFromEffect!=null){
                    ArrayList<Double> professorsView =(ArrayList<Double>)schoolViewFromEffect.get("prof_schoolIds");
                    ArrayList<Double> hallToUpdate= (ArrayList<Double>) schoolViewFromEffect.get("students_hall");
                    ArrayList<Double> tableFromEffect = (ArrayList<Double>) schoolViewFromEffect.get("tables");
                    context.updateHall(hallToUpdate,-1);
                    context.updateTable(tableFromEffect);
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

                context.getClientView().printMessage(packet.getFromPayload("message"));
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
        context.getClientModel().setPhase(ColorStrings.ERROR+"Action Phase"+ColorStrings.RESET);
        context.getClientModel().setBlocked(false);
    }
}
