package main.java.com.polimi.client.views;

import main.java.com.polimi.client.models.AssistantDeck;
import main.java.com.polimi.client.models.Message;
import main.java.com.polimi.client.utils.ColorStrings;

import java.util.Observable;
import java.util.Observer;

public class AssistantDeckView extends Observable implements Observer{

    /**
     * @param deck
     * prints the deck given a deck model
     */
    public void printDeck(AssistantDeck deck){
        System.out.println("______DECK______");
        String printString=" _ _  _ _  _ _  _ _  _ _  _ _  _ _  _ _  _ _  _ _ \n";
        for(int i =1; i<=9; i++){
            if(deck.getAssistantsHand().indexOf(i)!=-1){
                printString+=  "|"+ColorStrings.DECK_ACTIVE_COLORS[i-1]+i+"  "+ColorStrings.RESET+"|";
            }else{
                printString+=  "|"+ColorStrings.PLAYED_CARD+i+"  "+ColorStrings.RESET+"|";
            }
        }
        if(deck.getAssistantsHand().indexOf(10)!=-1){
            printString+=  "|"+ColorStrings.DECK_ACTIVE_COLORS[9]+10+" "+ColorStrings.RESET+"|";
        }else{
            printString+=  "|"+ColorStrings.PLAYED_CARD+10+" "+ColorStrings.RESET+"|";
        }
        printString+="\n";
        for(int i =1; i<=10; i++){
            if(deck.getAssistantsHand().indexOf(i)!=-1){
                printString+=  "|"+ColorStrings.DECK_ACTIVE_COLORS[i-1]+"   "+ColorStrings.RESET+"|";
            }else{
                printString+=  "|"+ColorStrings.PLAYED_CARD+"   "+ColorStrings.RESET+"|";
            }
        }
        printString+="\n";
        for(int i =1; i<=10; i++){
            if(deck.getAssistantsHand().indexOf(i)!=-1){
                printString+=  "|"+ColorStrings.DECK_ACTIVE_COLORS[i-1]+"  "+deck.getMNmovesByID(i)+ColorStrings.RESET+"|";
            }else{
                printString+=  "|"+ColorStrings.PLAYED_CARD+"  "+deck.getMNmovesByID(i)+ColorStrings.RESET+"|";
            }
        }

        System.out.println(printString);
    }

    /**
     * @param controller
     * takes a client controller to add as an objerver
     */
    public AssistantDeckView(Observer controller){
        addObserver(controller);
    }

    @Override
    public void update(Observable o, Object arg) {
        Message msg = (Message) arg;
        switch (msg.getAction()){
            case "SETUP_DECK":
            case "PRINT_DECK":
                printDeck((AssistantDeck) o);
        }
    }
}


