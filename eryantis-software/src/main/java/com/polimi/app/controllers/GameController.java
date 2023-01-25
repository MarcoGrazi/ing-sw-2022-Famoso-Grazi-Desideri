package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.controllers.game_states.State;
import main.java.com.polimi.app.controllers.game_states.WaitingForOpponentsState;
import main.java.com.polimi.app.packets.Packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Game Controller class.
 * The mind of the game, keeps a reference of all the controllers to enable communication and handle the most delicate parts of
 * the game.
 * It's defined by an absolute gameId, unique to each game, a player number, used to make important choices of initialization
 * and game evolution, a flag telling which game mode, between Simple end Expert, has been chosen.
 * The game has a state, which represents the current stage of the game and manages all the actions.
 * Further information about the game are represented by the player order, two flags for end game management purpose and, finally,
 * the winner id.
 * If the game is in Expert mode, a map keeps track each turn of all the played character.
 * @author Group 53
 */
public class GameController implements Serializable {
    //The absolute id. Unique to each game.
    private final int gameId;
    //The number of players
    private final int playerNumber;
    //True if the game is in simple mode, false otherwise.
    private final boolean simpleMode;
    //The id of the winner
    private int winnerId = -1;
    //The current state of the game.
    private State state;
    //Map which keeps track of played characters
    private LinkedHashMap<Integer, LinkedHashMap<String, Object>> playedCharacterbyPlayerId;
    //The order in which players play their turn
    private ArrayList<Integer> playerIdTurnOrder;
    //End game flag to end immediately the game
    private boolean endImmediately = false;
    //End game flag to end the game after the current round
    private boolean endAfterTurn = false;

    //All controllers
    private final PlayerController plController;
    private SchoolController schController;
    private BagController bagController;
    private CloudController clController;
    private ArchipelagoController archController;
    private ActiveCharactersController acController;
    private AssistantDeckController asdController;

    /**
     * Class constructor. Initializes a new game and sets the state to WaitingForOpponents.
     * @param gameId the absolute id of the game. Assigned by the server
     * @param gamemode the chosen gamemode for this game
     * @param playerNumber the number of players for this game
     */
    public GameController(int gameId, String gamemode, int playerNumber){
        this.gameId = gameId;
        this.simpleMode = gamemode.equals("S");
        this.playerNumber = playerNumber;
        this.plController = new PlayerController(this, playerNumber);

        this.state = new WaitingForOpponentsState(this);
    }

    /**
     * Handles the received packet depending on which state the game is in.
     * @param packet the pack of information necessary to handle the chosen action
     * @return a list of ERROR packets in case of rejection or a list different types of packets depending on the handle outcome
     */
    public ArrayList<Packet> handle(Packet packet){
        return this.state.verify(packet);
    }

    /**
     * Changes game state
     * @param state the new state
     */
    public void setState(State state) {
        this.state= state;
    }

    /**
     * Initializes all the controllers and then calls specific initialization methods from the respective controllers to
     * initialize halls, clouds, schools and characters (if the game is in expert mode)
     * @return a map of useful information about the outcome of the initialization
     */
    public HashMap<String, Object> initializeGame() {
        HashMap<String, Object> data = new HashMap<>();
        //Controllers initializations
        this.bagController = new BagController(this, playerNumber);
        this.clController = new CloudController(this, playerNumber);
        this.schController = new SchoolController(this, playerNumber);
        this.asdController = new AssistantDeckController(this, playerNumber);
        this.archController = new ArchipelagoController(this);

        if(!this.simpleMode) {
            this.acController = new ActiveCharactersController(this);
            data.put("characters", acController.initializeCharacters());

            this.playedCharacterbyPlayerId = new LinkedHashMap<>();
            for(int playerId : getPlController().getPlayerIds()){
                LinkedHashMap<String, Object> element = new LinkedHashMap<>();
                element.put("effect", 0);
                playedCharacterbyPlayerId.put(playerId, element);
            }
        }

        //First turn order is join order
        this.playerIdTurnOrder = getPlController().getPlayerIds();
        //First turn starts
        data.put("clouds", clController.cloudPlanningPhase());
        data.put("schools", schController.initializeHalls());
        data.put("archipelago", archController.initializeArchipelago());

        return data;
    }

    /**
     * Restart a new turn by refilling the clouds.
     * If the game is in Expert mode, resets the map between players and played characters and reassign correctly professors.
     * @return a map of useful information about the outcome of the initialization
     */
    public HashMap<String, Object> startTurn() {

        if(!isSimpleMode()) {
            playedCharacterbyPlayerId.clear();
            for(int playerId : getPlController().getPlayerIds()){
                LinkedHashMap<String, Object> element = new LinkedHashMap<>();
                element.put("effect", 0);
                playedCharacterbyPlayerId.put(playerId, element);
            }
        }

        HashMap<String, Object> data = new HashMap<>();
        //Clouds are filled with new students
        data.put("clouds", clController.cloudPlanningPhase());
        data.put("professors", schController.setProfessors());
        return data;
    }

    /**
     * Sets turn order depending on the last played card of each player. If there are no more cards to play (turn 10), sets
     * the flag to end the game after this round.
     * @param lastPlayerId the id of the last player
     * @return LOCK/UNLOCK packets
     */
    public ArrayList<Packet> setPlayerIdTurnOrder(Integer lastPlayerId){
        ArrayList<Packet> turnOrderPacket = new ArrayList<>();

        this.playerIdTurnOrder.sort((player1, player2) -> {
            Integer weight1 = getAssistantDeckController().getLastPlayedCardWeight(player1);
            Integer weight2 = getAssistantDeckController().getLastPlayedCardWeight(player2);

            return weight1.compareTo(weight2);
        });

        for(Integer pId: playerIdTurnOrder) {
            if(playerIdTurnOrder.indexOf(pId) == 0) {
                Packet locker = new Packet("UNLOCK_ACTION", pId, gameId);
                locker.addToPayload("message", "Player " + pId + " action phase: START");
                turnOrderPacket.add(locker);
            } else {
                Packet locker = new Packet("LOCK", pId, gameId);
                locker.addToPayload("message", "Your turn number is: " + (playerIdTurnOrder.indexOf(pId) + 1) );
                turnOrderPacket.add(locker);
            }
        }

        //After every planning phase, calls the EndAfterTurn to check whether the game must end or not.
        if(this.getAssistantDeckController().getHandSize(lastPlayerId) == 0) {
            this.endAfterTurn();
        }
        return turnOrderPacket;
    }

    /**
     * Calculates the victorious player depending on the number of towers still inside each school or, in case of par, the number
     * of professors inside the school. If players are still even, the games end without a winner.
     * @return the id of the winner, or -1 in case of par
     */
    public int calcVictory(){
        int minTowers= this.getSchoolController().getSchools().get(0).getTowers().size();
        int playerId= this.getSchoolController().getSchools().get(0).getSchoolId();

        for(int i =1; i<playerNumber ; i++){
            int numTowers= this.getSchoolController().getSchools().get(i).getTowers().size();
            if(numTowers==minTowers){
                playerId = -1;
            }else if(numTowers<minTowers){
                minTowers=numTowers;
                playerId=this.getSchoolController().getSchools().get(i).getSchoolId();
            }
        }

        if (playerId==-1) {
            ArrayList<Integer> playerList = this.getPlController().getPlayerIds();
            ArrayList<Integer> professorMap= this.getSchoolController().getProfessorsMap();
            int[] playerOccurrences ={0,0,0};

            for(Integer pId : professorMap) {
                if(pId != -1) {
                    int pIndex = playerList.indexOf(pId);
                    playerOccurrences[pIndex] = playerOccurrences[pIndex] + 1;
                }
            }

            int maxProf = playerOccurrences[0];
            playerId = playerList.get(0);

            for(int i=1; i<playerNumber; i++) {
                int numProf = playerOccurrences[i];
                if(maxProf == numProf) {
                    playerId = -1;
                } else  if (numProf > maxProf) {
                    maxProf = numProf;
                    playerId = playerList.get(i);
                }
            }
        }

        //If playerId == -1 the game is even.
        this.winnerId = playerId;
        return playerId;
    }

    /**
     * Sets the end immediately flag to true.
     */
    public void endImmediately() {
        this.endImmediately = true;
    }

    /**
     * @return the end immediately flag value
     */
    public boolean getEndImmediately() {
        return this.endImmediately;
    }

    /**
     * Sets the end after turn flag to true.
     */
    public void endAfterTurn() {
        this.endAfterTurn = true;
    }

    /**
     * @return the end after turn flag value
     */
    public boolean getEndAfterTurn() { return this.endAfterTurn; }

    /**
     * Sets the winner id. If it's set to -1, there is no winner.
     * @param playerId the id of the winner of this game.
     */
    public void setWinnerId(int playerId) {
        this.winnerId = playerId;
    }

    /**
     * If the id is set to -1 it means there is no winner.
     * @return the winner id.
     */
    public int getWinnerId() {
        return this.winnerId;
    }

    /**
     * @return true if the game is in simple mode, false otherwise
     */
    public boolean isSimpleMode(){
        System.out.println(simpleMode);
        return simpleMode;
    }

    /**
     * @param playerId the id of the player
     * @return true if the player correspondent to the id is the last of this turn, false otherwise
     */
    public boolean isLastPlayer(int playerId) {
        return playerIdTurnOrder.get(playerIdTurnOrder.size()-1) == playerId;
    }

    /**
     * @return the id of this game
     */
    public int getGameId() {
        return this.gameId;
    }

    /**
     * @return the number of players in this game
     */
    public int getPlayerNumber(){
        return this.playerNumber;
    }

    /**
     * @return the list of player ids in current turn progression order
     */
    public ArrayList<Integer> getPlayerIdTurnOrder(){
        return playerIdTurnOrder;
    }

    /**
     * @param playerId the id of the player
     * @return a map of info about the character this player played during the current turn
     */
    public LinkedHashMap<String, Object> getPlayedCharacterByPlayerId(int playerId){
        return playedCharacterbyPlayerId.get(playerId);
    }

    /**
     * Replaces the info inside the map with the info of the played character
     * @param playerId the id of the player
     * @param effectId the id of the played effect
     * @param parameter additional infos about the effect
     */
    public void replacePlayedCharacterByPlayerId(int playerId, int effectId, Object parameter){
        LinkedHashMap<String, Object> element = new LinkedHashMap<>();
        element.put("effect", effectId);
        element.put("info", parameter);
        playedCharacterbyPlayerId.replace(playerId, element);

        //If effect goes right, decrements player balance and increments character cost
        getPlController().subCoin(playerId, getActiveCharactersController().getCostById(effectId));
        getActiveCharactersController().incrementCostById(effectId);
    }

    /**
     * @return player controller instance
     */
    public PlayerController getPlController() {
        return plController;
    }

    /**
     * @return school controller instance
     */
    public SchoolController getSchoolController() {
        return schController;
    }

    /**
     * @return bag controller instance
     */
    public BagController getBagController() {
        return bagController;
    }

    /**
     * @return cloud controller instance
     */
    public CloudController getCloudController() {
        return clController;
    }

    /**
     * @return archipelago controller instance
     */
    public ArchipelagoController getArchipelagoController() {
        return archController;
    }

    /**
     * @return active character controller instance
     */
    public ActiveCharactersController getActiveCharactersController() {
        return acController;
    }

    /**
     * @return assistant deck controller instance
     */
    public AssistantDeckController getAssistantDeckController() {
        return asdController;
    }

    //TODO: remove
    public HashMap<String, Object> waitForRejoin(Packet packet) {
        return null;
    }

    /**
     * @return the current state of the game
     */
    public State getState() {
        return state;
    }
}
