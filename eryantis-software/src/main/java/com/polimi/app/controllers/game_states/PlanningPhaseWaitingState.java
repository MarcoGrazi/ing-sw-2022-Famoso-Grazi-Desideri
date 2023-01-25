package main.java.com.polimi.app.controllers.game_states;

import main.java.com.polimi.app.controllers.GameController;
import main.java.com.polimi.app.packets.Packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Planning Phase State.
 * This state represents the planning phase. In this stage of the game, the player must choose and play one of their assistants.
 * Just then, they can end their planning phase.
 * If the game is in Expert mode, the player can play a character at any time of this state.
 * @author Group 53
 */
public class PlanningPhaseWaitingState implements State, Serializable {
    //The game controller
    private GameController context;
    //The actions each player can perform during this stage
    private final String[] validActions = {"PLAY_ASSISTANT", "PLAY_CHARACTER", "END_PLANNING","FETCH_CHARACTERS"};
    //Flag used to check if the player already played an assistant. In this case, unlocks end action.
    private boolean playedAssistant = false;

    /**
     * Class constructor. Initializes the context.
     * @param context the game state
     */
    public PlanningPhaseWaitingState(GameController context) {
        this.context= context;
    }

    /**
     * @param packet the pack of information necessary to handle the chosen action
     * @return ERROR packets if the player plays a forbidden action or tries to end the phase before playing an assistant.
     */
    @Override
    //Takes the action form the packet and verify that's a valid action to be performed in this stage of the game.
    public ArrayList<Packet> verify(Packet packet) {
        String action = packet.getAction();
        ArrayList<Packet> packets = new ArrayList<>();

        if(Arrays.asList(validActions).contains(action)){
            if(action.equals("END_PLANNING") && !playedAssistant) {
                Packet response = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                response.addToPayload("message", "You must play the assistant before ending your planning phase");
                packets.add(response);
                return packets;
            } else {
                return handle(packet);
            }
        } else {
            Packet response = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
            response.addToPayload("message", "Forbidden action");
            packets.add(response);
            return packets;
        }
    }

    /**
     * @param packet the pack of information necessary and handle the chosen action
     * @return ERROR, CARD_ALREADY_PLAYED, ASSISTANT_PLAYED or LOCK/UNLOCK packets
     */
    @Override
    //Handle is for sure called by the "current player" after sending a "PLAY_CARD" action. Because the others are locked.
    public ArrayList<Packet> handle(Packet packet) {
        String action = packet.getAction();
        ArrayList<Packet> responsePlanningPhase = new ArrayList<>();

        switch(action){
            case "PLAY_ASSISTANT" -> {
                int playedCard = (int) Math.floor(Float.parseFloat(packet.getFromPayload("assistant").toString()));
                //Game checks whether the answer is valid (has this card been played by someone?)
                if(context.getAssistantDeckController().getHandSize(packet.getPlayerId()) != 1) {
                    for(int i=0; i < context.getPlayerIdTurnOrder().indexOf(packet.getPlayerId()); i++) {
                        if(playedCard == context.getAssistantDeckController().getLastPlayerCardIndex(context.getPlayerIdTurnOrder().get(i))) {
                            //YES: game asks for a different card
                            Packet responseWrongCard = new Packet("CARD_ALREADY_PLAYED", packet.getPlayerId(), packet.getGameId());
                            responseWrongCard.addToPayload("message", "this card has already been played by another player");
                            responsePlanningPhase.add(responseWrongCard);
                            return responsePlanningPhase;
                        }
                    }
                }
                context.getAssistantDeckController().playCard(packet.getPlayerId(),playedCard);
                playedAssistant = true;

                Packet assistantPacket = new Packet("ASSISTANT_PLAYED", packet.getPlayerId(), packet.getGameId());
                assistantPacket.addToPayload("assistantId", playedCard);
                responsePlanningPhase.add(assistantPacket);

            }
            case "PLAY_CHARACTER" -> {
                int playedCharacter = (int) Math.floor(Float.parseFloat(packet.getFromPayload("character").toString()));

                if(!context.isSimpleMode()){
                    if((int)context.getPlayedCharacterByPlayerId(packet.getPlayerId()).get("effect") == 0){
                        if(context.getActiveCharactersController().getCostById(playedCharacter) != 0){
                            if(context.getPlController().getPlayerById(packet.getPlayerId()).getCoinCounter()
                                    >= context.getActiveCharactersController().getCostById(playedCharacter)){

                                responsePlanningPhase.addAll(context.getActiveCharactersController().
                                        EffectById(playedCharacter, packet));

                            }
                            else {
                                Packet coinError = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                                coinError.addToPayload("message", "you don't have enough money to play this character");
                                responsePlanningPhase.add(coinError);
                            }
                        }
                        else{
                            Packet idError = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                            idError.addToPayload("message", "this id is not among the Active characters ids");
                            responsePlanningPhase.add(idError);
                        }
                    }
                    else{
                        Packet alreadyPlayedError = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                        alreadyPlayedError.addToPayload("message", "you already played character card: " +
                                context.getPlayedCharacterByPlayerId(packet.getPlayerId()));
                        responsePlanningPhase.add(alreadyPlayedError);
                    }
                }
                else {
                    Packet simpleModeError = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                    simpleModeError.addToPayload("message", "you cannot play character cards in a Simple Mode Game");
                    responsePlanningPhase.add(simpleModeError);
                }
            }
            case "END_PLANNING" -> {
                playedAssistant = false;

                if(context.isLastPlayer(packet.getPlayerId())){
                    changeState(new ActionPhaseWaitingState(context));
                    return context.setPlayerIdTurnOrder(packet.getPlayerId());
                } else {
                    ArrayList<Integer> playerIdTurnOrder = context.getPlayerIdTurnOrder();

                    int nextPlayerIndex = playerIdTurnOrder.indexOf(packet.getPlayerId()) +1;
                    Packet packet1 = new Packet("LOCK", packet.getPlayerId(), packet.getGameId());
                    packet1.addToPayload("message", "Player " + packet.getPlayerId() + "'s planning phase: END");
                    Packet packet2 = new Packet("UNLOCK_PLANNING", playerIdTurnOrder.get(nextPlayerIndex), packet.getGameId());
                    packet2.addToPayload("message", "Player " + playerIdTurnOrder.get(nextPlayerIndex).toString() + "'s planning phase: START");
                    responsePlanningPhase.add(packet1);
                    responsePlanningPhase.add(packet2);
                }
            }
            case "FETCH_CHARACTERS"->{
                Packet fetchCharacters= new Packet("CHARACTERS", packet.getPlayerId(), packet.getGameId());
                fetchCharacters.addToPayload("characters", context.getActiveCharactersController().getCharactersInfo());
                responsePlanningPhase.add(fetchCharacters);
            }
        }

        return responsePlanningPhase;
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
