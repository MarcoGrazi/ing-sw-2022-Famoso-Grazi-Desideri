package main.java.com.polimi.client.models.client_states;

import com.google.gson.internal.LinkedTreeMap;
import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.utils.ColorStrings;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Locked state of the server. The player is locked and cannot perform actions
 */
public class Locked implements State{
    private ClientController context;
    private State previousState;
    private final ArrayList<String> allowedClientActions= new ArrayList<>(Arrays.asList("SET_NICKNAME", "FETCH_GAMES","FETCH_CHARACTERS"));


    /**
     * @param context client controller
     */
    public Locked(ClientController context) {
        this.context=context;
        context.getClientModel().setPhase(ColorStrings.ERROR+"Blocked"+ColorStrings.RESET);
        context.getClientModel().setBlocked(true);
    }

    /**
     * constructor called when the player has rejoined the game but is waiting for the  others to rejoin
     * @param context client controller
     * @param previousState previous state of the client before server down
     */
    public Locked(ClientController context, State previousState) {
        this.context=context;
        context.getClientModel().setPhase(ColorStrings.ERROR+"Blocked"+ColorStrings.RESET);
        context.getClientModel().setBlocked(true);
        this.previousState=previousState;
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
            this.context.getClientView().printErrorMessage("You are BLOCKED");
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
        int cloudId;
        int towerQuantity;
        boolean isLastTurn;
        LinkedTreeMap<String, Object> archipelago_payload;
        LinkedTreeMap<String, Object> archipelagoView;
        LinkedTreeMap<String,Object> schoolView;
        LinkedTreeMap<String, Object> cloudView;
        LinkedTreeMap<String,Object> clouds;
        ArrayList<Double> hall;
        ArrayList<Double> professorsToSet;

        switch (action){
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
                if(!locked){
                    changeState(new UnlockedPlanning(context));
                }
                break;
            case "STUDENT_MOVED_T":
                LinkedTreeMap<String,Object> table = (LinkedTreeMap<String, Object>) packet.getFromPayload("professors_view");
                ArrayList<Double> professors = (ArrayList<Double>) table.get("prof_schoolIds");
                context.updateProfessors(professors, packet.getPlayerId());
                context.printSchool();
                break;
            case "STUDENT_MOVED_I":
                LinkedTreeMap<String, Object> arch_container = (LinkedTreeMap<String, Object>) packet.getFromPayload("archipelago_view");
                LinkedTreeMap<String, Object> arch_view = (LinkedTreeMap<String, Object>) arch_container.get("archipelago_view");
                context.updateArchipelago(arch_view);
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
            case "GOT_STUDENTS":
                try{
                    cloudId= (int) Math.round(Float.parseFloat(packet.getFromPayload("cloud_id").toString()));
                    context.updateClouds(cloudId);

                }catch (NullPointerException e){

                }finally {
                    context.getClientView().printMessage(packet.getFromPayload("message").toString());
                }
                break;
            case "LOCK_PLANNING":
                isLastTurn = (boolean) packet.getFromPayload("is_last_turn");
                LinkedTreeMap<String, Object> professorsViewLockPlanning= (LinkedTreeMap<String, Object>) packet.getFromPayload("professors_view");
                if(professorsViewLockPlanning!=null){
                    professorsToSet = (ArrayList<Double>) professorsViewLockPlanning.get("prof_schoolIds");
                    context.updateProfessors(professorsToSet, packet.getPlayerId());
                    context.printSchool();
                }

                if(!isLastTurn) {
                    cloudView = (LinkedTreeMap<String, Object>) packet.getFromPayload("clouds_view");
                    context.resetClouds(cloudView);
                }

                context.getClientView().printMessage(packet.getFromPayload("message"));
                changeState(new Locked(context));
                break;
            case "UNLOCK_PLANNING":
                LinkedTreeMap<String, Object> professorsViewUnlockPlanning= (LinkedTreeMap<String, Object>) packet.getFromPayload("professors_view");
                if(professorsViewUnlockPlanning!=null){
                    professorsToSet = (ArrayList<Double>) professorsViewUnlockPlanning.get("prof_schoolIds");
                    context.updateProfessors(professorsToSet, packet.getPlayerId());
                    context.printSchool();
                }


                try{
                    isLastTurn = (boolean) packet.getFromPayload("is_last_turn");
                    //Clouds are not updated before last turn.
                    if(!isLastTurn) {
                        cloudView = (LinkedTreeMap<String, Object>) packet.getFromPayload("clouds_view");
                        context.resetClouds(cloudView);
                    }
                }catch (NullPointerException e){

                } finally {
                    context.getClientView().printMessage(packet.getFromPayload("message").toString());
                    changeState(new UnlockedPlanning(context));
                }
                break;
            case "UNLOCK_ACTION":
                try{
                    cloudId= (int) Math.round(Float.parseFloat(packet.getFromPayload("cloud_id").toString()));
                    context.updateClouds(cloudId);

                }catch (NullPointerException e){

                }finally {
                    context.getClientView().printMessage(packet.getFromPayload("message").toString());
                    changeState(new UnlockedAction(context));
                }
                break;
            case "UNLOCK_LAST_ACTION":
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

                context.getClientView().printMessage(packet.getFromPayload("message").toString());
                changeState(new UnlockedAction(context));
                break;
            case "END_GAME":
                archipelago_payload = (LinkedTreeMap<String, Object>) packet.getFromPayload("archipelago_view");
                archipelagoView = (LinkedTreeMap<String, Object>) archipelago_payload.get("archipelago_view");
                context.updateArchipelago(archipelagoView);

                oldColour= archipelago_payload.get("old_colour").toString();
                newColour= archipelago_payload.get("new_colour").toString();
                towerQuantity= (int) Math.round(Float.parseFloat(archipelago_payload.get("tower_quantity").toString()));
                if(!oldColour.equals("")&& (oldColour).equals(context.getPlayer().getSchoolColour().name())){
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
            case "SUCCESS_GAME_RESTARTING":
                previousState.switchContext(context);
                resetState(previousState);

                archipelagoView = (LinkedTreeMap<String, Object>) packet.getFromPayload("archipelago_view");
                if(archipelagoView!=null){
                    context.updateArchipelago(archipelagoView);
                }
                schoolView = (LinkedTreeMap<String, Object>) packet.getFromPayload("school_view");
                if(schoolView!=null){
                    context.resetSchool(schoolView, packet.getPlayerId());
                }
                cloudView = (LinkedTreeMap<String, Object>) packet.getFromPayload("cloud_view");
                if(cloudView!=null){
                    context.resetClouds(cloudView);
                }
                LinkedTreeMap<String,Object> assistantDeckView = (LinkedTreeMap<String, Object>) packet.getFromPayload("assistant_deck");
                if(assistantDeckView!=null){
                    context.resetDeck(assistantDeckView);
                }
                context.printPlayer();
                break;
            case "PLAYED_EFFECT":
                archipelagoView = (LinkedTreeMap<String, Object>) packet.getFromPayload("archipelago_view");

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

                if(archipelagoView!=null){
                    context.updateArchipelago(archipelagoView);
                }
                context.getClientView().printMessage(packet.getFromPayload("message"));
                break;
            case "ERROR":
                context.getClientView().printErrorMessage(packet.getFromPayload("message").toString());
                break;
            case "CHARACTERS":
                context.getClientView().printCharacters(packet);
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

    @Override
    public void reset(){
        context.getClientModel().setPhase(ColorStrings.ERROR+"Blocked"+ColorStrings.RESET);
        context.getClientModel().setBlocked(true);
    }
}
