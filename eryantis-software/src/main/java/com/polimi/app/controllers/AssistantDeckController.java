package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.AssistantDeck;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Assistant deck controller.
 * Manages assistant decks.
 * @author Group 53
 */
public class AssistantDeckController implements Serializable {
    //Instance of the game controller
    private final GameController gmController;
    //The list of Deck models
    private final ArrayList<AssistantDeck> asdModels;

    /**
     * Class constructor. Initializes game infos and N assistant decks, depending on the player number.
     * @param gmController the game controller instance
     * @param playerNumber the number of players in this game
     */
    public AssistantDeckController(GameController gmController, int playerNumber){
        this.gmController = gmController;
        this.asdModels = new ArrayList<>();

        for(int i=0; i<playerNumber; i++) {
            this.asdModels.add(new AssistantDeck());
        }
        linkToPlayers();
    }

    /**
     * Links one deck to each player by the player id
     */
    public void linkToPlayers() {
        for (int i =0; i<gmController.getPlController().getPlayerIds().size();i++){
            asdModels.get(i).setDeckId(gmController.getPlController().getPlayerIds().get(i));
        }
    }

    /**
     * Plays a card from the deck
     * @param deckId the id of the deck
     * @param id the id of the assistant
     */
    public void playCard(int deckId, int id) {
        for(AssistantDeck ad: asdModels) {
            if(ad.getDeckId() == deckId) {
                ad.discardAssistant(id);
            }
        }
    }

    /**
     * @param deckId the id of the deck
     * @return the amount of playable cards
     */
    public int getHandSize(int deckId) {
        for(AssistantDeck ad: asdModels) {
            if(ad.getDeckId() == deckId) {
                return ad.getAssistantsHand().size();
            }
        }
        return -1;
    }

    /**
     * @param deckId the id of the deck
     * @return the id of the last played card
     */
    public int getLastPlayerCardIndex(int deckId) {
        for(AssistantDeck ad: asdModels) {
            if(ad.getDeckId() == deckId) {
                return ad.getAssistantDiscardPile().peek();
            }
        }
        return -1;
    }

    /**
     * @param deckId the id of the deck
     * @return the amount of possible moves of the last played card
     */
    public Integer getLastPlayedCardMoves(int deckId) {
        for(AssistantDeck ad: asdModels) {
            if(ad.getDeckId() == deckId) {
                int cardId = ad.getAssistantDiscardPile().peek();
                return ad.getMNmovesByID(cardId);
            }
        }
        return null;
    }

    /**
     * @param deckId the id of the deck
     * @return the turn weight of the last played card
     */
    public Integer getLastPlayedCardWeight(int deckId) {
        for(AssistantDeck ad: asdModels) {
            if(ad.getDeckId() == deckId) {
                int cardId = ad.getAssistantDiscardPile().peek();
                return ad.getTurnWeightByID(cardId);
            }
        }
        return null;
    }

    /**
     * @param deckId the id of the deck
     * @return the deck instance
     */
    public AssistantDeck getDeck(int deckId) {
        for(AssistantDeck ad: asdModels) {
            if(ad.getDeckId() == deckId) {
                return ad;
            }
        }
        return null;
    }
}
