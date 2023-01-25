package main.java.com.polimi.app.models;

import java.io.Serializable;
import java.util.*;

/**
 * Assistant Deck class.
 * Represented by an absolute Id, unique for each deck, an ArrayList which stores the indexes of the assistant currently
 * inside the hand of the player and a Stack which stands as a discard pile and stores the already played assistants' indexes.
 * @author Group 53
 */
public class AssistantDeck implements Serializable {
    //The absolute deckId. Unique for each deck.
    private int deckId;
    //The list of playable assistants' indexes
    private final ArrayList<Integer> assistantsHand;
    //The pile of already played assistants. The last played is on top of the pile.
    private final Stack<Integer> assistantDiscardPile;

    /**
     * Class constructor.
     * Initializes assistantHand with the Ids of all the possible assistants.
     * Initializes assistantDiscardPile as an empty stack.
     */
    public AssistantDeck() {
        assistantsHand = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10));
        assistantDiscardPile = new Stack<>();
    }

    /**
     * @param playerId the id of the player who owns the deck
     */
    public void setDeckId(int playerId) {
        this.deckId = playerId;
    }

    /**
     * @return the deck id
     */
    public int getDeckId() {
        return deckId;
    }

    /**
     * @return the list which contains the remaining playable characters
     */
    public ArrayList<Integer> getAssistantsHand() {
        return assistantsHand;
    }

    /**
     * @return the stack which contains all the already played characters
     */
    public Stack<Integer> getAssistantDiscardPile() {
        return assistantDiscardPile;
    }

    /**
     * Gets how much the selected assistant weights on the turn order calc.
     * @param id the id of the selected assistant
     * @return the selected assistant's turn weight, which is equal to its id
     */
    public int getTurnWeightByID(int id){
        return id;
    }

    /**
     * Gets the maximum number of mother nature moves established by the selected assistant.
     * @param id the id of the selected assistant
     * @return the maximum number of mother nature
     */
    public int getMNmovesByID(int id){
        int maxMNmoves;
        switch(id){
            case 1,2 -> maxMNmoves = 1;
            case 3,4 -> maxMNmoves = 2;
            case 5,6 -> maxMNmoves = 3;
            case 7,8 -> maxMNmoves = 4;
            case 9,10 -> maxMNmoves = 5;
            default -> {System.out.println("Assistant: invalid index"); maxMNmoves=0;}
        }
        return maxMNmoves;
    }

    /**
     * Removes the played assistant from the hand and puts it on top of the discard pile.
     * @param id the id of the selected assistant
     */
    public void discardAssistant(int id){
        boolean removed = assistantsHand.remove(Integer.valueOf(id));
        if(removed){
            assistantDiscardPile.push(id);
        }
        else{
            System.out.println("AssistantDeckModel: the id you want " +
                    "to discard is not present in the assistantHand");
        }
    }

    /**
     * Utilized to update the view.
     * @return an hash map of useful information about the deck
     */
    public HashMap<String,Object> encodeDeckInfo(){
        HashMap<String,Object> deckInfo = new HashMap<>();
        deckInfo.put("assistantsHand",assistantsHand);
        deckInfo.put("assistantDiscardPile",assistantDiscardPile);
        return deckInfo;
    }
}
