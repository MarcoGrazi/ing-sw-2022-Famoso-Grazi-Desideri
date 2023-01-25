package main.java.com.polimi.app.controllers.game_states;

import main.java.com.polimi.app.controllers.GameController;
import main.java.com.polimi.app.models.Student;
import main.java.com.polimi.app.packets.Packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Action Phase State.
 * This state represents the action phase. In this stage of the game, the player must move 3 or 4 students, depending on the
 * number of players, then move mother nature and, at the end, has to choose a cloud to retrieve new
 * students for their school hall.
 * If the game is in Expert mode, the player can play a character at any time of this state.
 * @author Group 53
 */
public class ActionPhaseWaitingState implements State, Serializable {
    //The game controller
    private GameController context;
    //The actions each player can perform during this stage
    private final ArrayList<String> validActions = new ArrayList<>(Arrays.asList("MOVE_STUDENT", "MOVE_MN", "GET_STUDENTS", "PLAY_CHARACTER", "FETCH_CHARACTERS"));
    //The order in which the player must perform the actions
    private final LinkedHashMap<String,Boolean> actionOrder;
    //A counter to manage how many students the player has already moved. Used to lock the correspondent action.
    private int studentsCounter = 0;

    /**
     * Class constructor. Initializes the context and sets the order to all false.
     * @param context the game state
     */
    public ActionPhaseWaitingState(GameController context) {
        this.context = context;
        actionOrder = new LinkedHashMap<>();
        for(String action : validActions){
            actionOrder.put(action, false);
        }
    }

    /**
     * @param packet the pack of information necessary to handle the chosen action
     * @return ERROR packets if the action is not one of the valid option, if it has already been performed or if it's not
     * in the right order (the player must play another action before).
     */
    @Override
    public ArrayList<Packet> verify(Packet packet) {
        String action = packet.getAction();

        ArrayList<Packet> packets = new ArrayList<>();
        if (validActions.contains(action)) {
            if(actionOrder.get(action)){
                Packet response = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                response.addToPayload("message", "Action already performed");
                packets.add(response);
                return packets;
            }
            for(int i=0; i< validActions.indexOf(action); i++){
                if(!action.equals("PLAY_CHARACTER") && !action.equals("FETCH_CHARACTERS")) {
                    if (!actionOrder.get(validActions.get(i))) {
                        Packet response = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                        response.addToPayload("message", "Turn actions not in right order. Do '" + validActions.get(i) + "' instead");
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
     * Extract the action from the packet payload and performs it.
     * @param packet the pack of information necessary and handle the chosen action
     * @return ERROR, STUDENT_MOVED_T, STUDENT_MOVED_I, MOVED_MN, END_GAME or LOCK/UNLOCK packets
     */
    @Override
    public ArrayList<Packet> handle(Packet packet) {
        ArrayList<Packet> responseActionPhase = new ArrayList<>();
        String action = packet.getAction();

        switch (action) {
            case "MOVE_STUDENT" -> {
                int schoolId = packet.getPlayerId();
                int studentIndex = (int) Math.floor(Float.parseFloat(packet.getFromPayload("student_index").toString()));

                //Checks whether the student index is valid
                if (studentIndex > context.getSchoolController().getNumStudentsInHall(schoolId) - 1) {
                    Packet response = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                    response.addToPayload("message", "student index out of bound");
                    responseActionPhase.add(response);
                } else {
                    studentsCounter++;
                    //This action can be executed N times, depending on playerNumber. After the N-th, the actionMap is updated;
                    switch (context.getPlayerNumber()) {
                        case 2:
                            if (studentsCounter == 3) this.actionOrder.replace(packet.getAction(), true);
                            break;
                        case 3:
                            if (studentsCounter == 4) this.actionOrder.replace(packet.getAction(), true);
                            break;
                    }

                    String moveTo = (String) packet.getFromPayload("move_to");
                    switch (moveTo) {
                        case "T" -> {
                            for(Integer pId: context.getPlController().getPlayerIds()) {
                                Packet responseT = new Packet("STUDENT_MOVED_T", pId, packet.getGameId());
                                if(pId == packet.getPlayerId()) {
                                    responseT.addToPayload("std_i", studentIndex);
                                    responseT.addToPayload("students_view", context.getSchoolController().moveStudentToTable(studentIndex, schoolId));
                                }
                                responseT.addToPayload("professors_view", context.getSchoolController().setProfessors());
                                responseActionPhase.add(responseT);
                            }
                        }
                        case "I" -> {
                            int groupNumber = (int) Math.floor(Float.parseFloat(packet.getFromPayload("group_number").toString()));

                            if (groupNumber > context.getArchipelagoController().getGroupsIndexes().size()) {
                                Packet responseError = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                                responseError.addToPayload("message", "island index does not exist");
                                responseActionPhase.add(responseError);
                            } else {
                                int groupIndex = new ArrayList<>(context.getArchipelagoController().getGroupsIndexes()).get(groupNumber - 1);
                                HashMap<String,Object> archView = context.getSchoolController().moveStudentToIsland(studentIndex, groupIndex, schoolId);

                                for(Integer pId: context.getPlController().getPlayerIds()) {
                                    Packet responseI = new Packet("STUDENT_MOVED_I", pId, packet.getGameId());
                                    if(pId == packet.getPlayerId()) {
                                        responseI.addToPayload("std_i", studentIndex);
                                    }
                                    responseI.addToPayload("archipelago_view", archView);
                                    responseActionPhase.add(responseI);
                                }
                            }
                        }
                    }
                }
            }
            case "MOVE_MN" -> {
                int mnMoves = (int) Math.floor(Float.parseFloat(packet.getFromPayload("mn_moves").toString()));
                int maxMoves = context.getAssistantDeckController().getLastPlayedCardMoves(packet.getPlayerId());
                if(!context.isSimpleMode()
                        && (int) context.getPlayedCharacterByPlayerId(packet.getPlayerId()).get("effect") == 4){
                    maxMoves += 2;
                }

                //Checks whether the mother nature move value is valid, basing on the last played card
                if (mnMoves > maxMoves) {
                    Packet response = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                    response.addToPayload("message", "invalid mother nature's move value");

                    responseActionPhase.add(response);
                } else {
                    this.actionOrder.replace(packet.getAction(), true);
                    //Mother nature moves
                    int groupIndex = context.getArchipelagoController().moveMn(mnMoves);
                    //This method may set the "winnerId" and "endImmediately" flag
                    HashMap<String,Object> archView = context.getArchipelagoController().archipelagoActionPhase(groupIndex);

                    if(!context.getEndImmediately() && !context.getEndAfterTurn()) {
                        //endImmediately = false && endAfterTurn = false -> normal Packet is sent
                        for(Integer pId: context.getPlController().getPlayerIds()) {
                            Packet responseMN = new Packet("MOVED_MN", pId, packet.getGameId());
                            responseMN.addToPayload("moves", mnMoves);
                            responseMN.addToPayload("archipelago_view", archView);

                            responseActionPhase.add(responseMN);
                        }
                    } else if (context.getEndImmediately() || (context.getEndAfterTurn()) && context.isLastPlayer(packet.getPlayerId())) {
                        //endImmediately = true -> end-game Packet is sent to terminate game immediately without calcVictory
                        //last-player finished their action phase -> end-game Packet is sent to terminate game after calcVictory
                        if(context.getEndAfterTurn() && context.isLastPlayer(packet.getPlayerId())) {
                            //Winner is calculated
                            context.calcVictory();
                        }
                        for(Integer pId: context.getPlController().getPlayerIds()) {
                            Packet responseMN = new Packet("END_GAME", pId, packet.getGameId());
                            responseMN.addToPayload("winner", context.getWinnerId());
                            responseMN.addToPayload("moves", mnMoves);
                            responseMN.addToPayload("archipelago_view", archView);

                            responseActionPhase.add(responseMN);
                        }
                    } else {
                        //endAfterTurn = true but last player has yet to play -> last-turn Packet are sent and game keeps on
                        ArrayList<Integer> playerIdTurnOrder = context.getPlayerIdTurnOrder();
                        //Action reset
                        this.studentsCounter = 0;
                        for (String a : validActions) {
                            this.actionOrder.replace(a, false);
                        }

                        int nextPlayerIndex = playerIdTurnOrder.indexOf(packet.getPlayerId()) + 1;
                        Packet packet1 = new Packet("LOCK_LAST_ACTION", packet.getPlayerId(), packet.getGameId());
                        packet1.addToPayload("message", "Player " + packet.getPlayerId() + "'s last action phase: END");
                        packet1.addToPayload("archipelago_view", archView);
                        packet1.addToPayload("moves", mnMoves);
                        Packet packet2 = new Packet("UNLOCK_LAST_ACTION", playerIdTurnOrder.get(nextPlayerIndex), packet.getGameId());
                        packet2.addToPayload("message", "Player " + playerIdTurnOrder.get(nextPlayerIndex).toString() + "'s last action phase: START");
                        packet1.addToPayload("moves", mnMoves);
                        packet2.addToPayload("archipelago_view", archView);
                        responseActionPhase.add(packet1);
                        responseActionPhase.add(packet2);
                    }
                }
            }
            case "PLAY_CHARACTER" -> {
                int playedCharacter = (int) Math.floor(Float.parseFloat(packet.getFromPayload("character").toString()));

                if (!context.isSimpleMode()) {
                    if ((int)context.getPlayedCharacterByPlayerId(packet.getPlayerId()).get("effect") == 0) {
                        if(context.getActiveCharactersController().getCostById(playedCharacter) != 0) {
                            if (context.getPlController().getPlayerById(packet.getPlayerId()).getCoinCounter()
                                    >= context.getActiveCharactersController().getCostById(playedCharacter)) {

                                responseActionPhase.addAll(context.getActiveCharactersController().
                                        EffectById(playedCharacter, packet));

                            } else {
                                Packet coinError = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                                coinError.addToPayload("message", "you don't have enough money to play this character");
                                responseActionPhase.add(coinError);
                            }
                        }
                        else{
                            Packet idError = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                            idError.addToPayload("message", "this id is not among the Active characters ids");
                            responseActionPhase.add(idError);
                        }
                    } else {
                        Packet alreadyPlayedError = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                        alreadyPlayedError.addToPayload("message", "you already played character card: " +
                                context.getPlayedCharacterByPlayerId(packet.getPlayerId()));
                        responseActionPhase.add(alreadyPlayedError);
                    }
                } else {
                    Packet simpleModeError = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                    simpleModeError.addToPayload("message", "you cannot play character cards in a Simple Mode Game");
                    responseActionPhase.add(simpleModeError);
                }
                return responseActionPhase;
            }
            case "GET_STUDENTS" -> {
                int cloudId = (int) Math.floor(Float.parseFloat(packet.getFromPayload("cloud").toString()));

                //Checks whether the cloudId is valid and then if the cloud is not empty
                if (cloudId > context.getPlayerNumber()) {
                    Packet response = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                    response.addToPayload("message", "non-existent cloud's id");
                    responseActionPhase.add(response);

                } else if (context.getCloudController().isEmpty(cloudId)) {
                    Packet response = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                    response.addToPayload("message", "cloud is already empty");
                    responseActionPhase.add(response);

                } else {
                    this.actionOrder.replace(packet.getAction(), true);
                    ArrayList<Student> cloudStudents = context.getCloudController().removeStudentsFromCloud(cloudId);

                    ArrayList<Integer> playerIdTurnOrder = context.getPlayerIdTurnOrder();
                    if (context.isLastPlayer(packet.getPlayerId())) {
                        changeState(new PlanningPhaseWaitingState(context));
                        //Each time some students are drawn, the game checks if the bag is empty to set the EndOfTurn flag
                        HashMap<String,Object> startInfo = context.startTurn();

                        for (Integer pId : playerIdTurnOrder) {
                            if (playerIdTurnOrder.indexOf(pId) == 0) {
                                Packet locker = new Packet("UNLOCK_PLANNING", pId, packet.getGameId());
                                locker.addToPayload("is_last_turn", context.getEndAfterTurn());
                                if(context.getEndAfterTurn()) {
                                    locker.addToPayload("message", "Last turn started; Player " + pId + "'s last planning phase: START;");
                                } else {
                                    locker.addToPayload("message", "New turn started; Player " + pId + "'s planning phase: START;");
                                    locker.addToPayload("clouds_view", startInfo.get("clouds"));
                                    locker.addToPayload("cloud_id", cloudId);
                                }
                                locker.addToPayload("professors", startInfo.get("professors"));
                                responseActionPhase.add(locker);
                            } else {
                                Packet locker = new Packet("LOCK_PLANNING", pId, packet.getGameId());
                                locker.addToPayload("is_last_turn", context.getEndAfterTurn());
                                if(context.getEndAfterTurn()) {
                                    if(pId == packet.getPlayerId()){
                                        locker.addToPayload("message", "Player " + pId + "'s action phase: END; Last turn started.");
                                        locker.addToPayload("school_view", context.getSchoolController().moveStudentsToHall(cloudStudents, packet.getPlayerId()));
                                    } else {
                                        locker.addToPayload("message", "Last player's action phase ended. Last turn started.");
                                    }
                                    locker.addToPayload("professors", startInfo.get("professors"));
                                    locker.addToPayload("cloud_id", cloudId);
                                } else {
                                    if(pId == packet.getPlayerId()){
                                        locker.addToPayload("message", "Player " + pId + "'s action phase: END; New turn started.");
                                        locker.addToPayload("school_view", context.getSchoolController().moveStudentsToHall(cloudStudents, packet.getPlayerId()));
                                    } else {
                                        locker.addToPayload("message", "Last player's action phase ended. New turn started.");
                                    }
                                    locker.addToPayload("professors", startInfo.get("professors"));
                                    locker.addToPayload("cloud_id", cloudId);
                                    locker.addToPayload("clouds_view", startInfo.get("clouds"));
                                }
                                responseActionPhase.add(locker);
                            }
                        }
                    } else {
                        this.studentsCounter = 0;
                        for (String a : validActions) {
                            this.actionOrder.replace(a, false);
                        }

                        int nextPlayerIndex = playerIdTurnOrder.indexOf(packet.getPlayerId()) + 1;

                        Packet packet1 = new Packet("LOCK_ACTION", packet.getPlayerId(), packet.getGameId());
                        packet1.addToPayload("message", "Player " + packet.getPlayerId() + "'s action phase: END");
                        packet1.addToPayload("cloud_id", cloudId);
                        packet1.addToPayload("school_view", context.getSchoolController().moveStudentsToHall(cloudStudents, packet.getPlayerId()));
                        Packet packet2 = new Packet("UNLOCK_ACTION", playerIdTurnOrder.get(nextPlayerIndex), packet.getGameId());
                        packet2.addToPayload("message", "Player " + playerIdTurnOrder.get(nextPlayerIndex).toString() + "'s action phase: START");
                        packet2.addToPayload("cloud_id", cloudId);
                        responseActionPhase.add(packet1);
                        responseActionPhase.add(packet2);

                        if(context.getPlayerNumber() == 3) {
                            int otherPlayerIndex;

                            if(playerIdTurnOrder.indexOf(packet.getPlayerId()) == 0) {
                                otherPlayerIndex = playerIdTurnOrder.get(2);
                            } else {
                                otherPlayerIndex = playerIdTurnOrder.get(0);
                            }

                            for(Integer pId: playerIdTurnOrder) {
                                if(pId == otherPlayerIndex) {
                                    Packet packet3 = new Packet("GOT_STUDENTS", otherPlayerIndex, packet.getGameId());
                                    packet3.addToPayload("message", "Player " + packet.getPlayerId() + "'s action phase: ENDS. Player " + playerIdTurnOrder.get(nextPlayerIndex).toString() + "'s action phase: START");
                                    packet3.addToPayload("cloud_id", cloudId);
                                    responseActionPhase.add(packet3);
                                }
                            }
                        }

                    }
                }
            }
            case "FETCH_CHARACTERS"->{
                Packet fetchCharacters= new Packet("CHARACTERS", packet.getPlayerId(), packet.getGameId());
                fetchCharacters.addToPayload("characters", context.getActiveCharactersController().getCharactersInfo());
                responseActionPhase.add(fetchCharacters);
            }
        }
        return responseActionPhase;
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
