package main.java.com.polimi.client.models;

import java.util.*;

/**
 * Assistant Deck class.
 * Represented by an ArrayList which stores the indexes of the assistant currently inside the hand of the player and a
 * Stack which stands as a discard pile and stores the already played assistants' indexes.
 * @author Group 53
 */
public class AssistantDeck extends Observable {
    //The list of playable assistants' indexes
    private final ArrayList<Integer> assistantsHand;
    //The pile of already played assistants. The last played is on top of the pile.
    private final Stack<Integer> assistantDiscardPile;

    /**
     * Class constructor.
     * Initializes assistantHand with the Ids of all the possible assistants.
     * Initializes assistantDiscardPile as an empty stack.
     * Initializes and notifies the view.
     */
    public AssistantDeck(Observer view) {
        assistantsHand = new ArrayList<>(Arrays.asList(1,2,3,4,5,6,7,8,9,10));
        assistantDiscardPile = new Stack<>();

        addObserver(view);
        setChanged();
        notifyObservers(new Message("SETUP_DECK"));
    }

    /**
     * Adds the deck view to the model as an observer
     * @param view the instance of the view
     */
    public void setView(Observer view){
        addObserver(view);
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
        setChanged();
        notifyObservers(new Message("PRINT_DECK"));
    }

    /**
     * Updates the deck hand and notifies the view
     * @param deckToSet the new hand of cards
     */
    public void setDeck(ArrayList<Integer> deckToSet) {
        assistantsHand.clear();
        assistantsHand.addAll(deckToSet);

        setChanged();
        notifyObservers(new Message("PRINT_DECK"));
    }

    /**
     * Updates the discard pile
     * @param discardPileToSet the new discard pile
     */
    public void setDiscardPile(ArrayList<Integer> discardPileToSet) {
        assistantDiscardPile.clear();
        assistantDiscardPile.addAll(discardPileToSet);
    }
}

