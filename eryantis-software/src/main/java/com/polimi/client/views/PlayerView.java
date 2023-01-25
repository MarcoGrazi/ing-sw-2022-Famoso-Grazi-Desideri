package main.java.com.polimi.client.views;


import main.java.com.polimi.client.models.Message;
import main.java.com.polimi.client.models.Player;

import java.util.Observable;
import java.util.Observer;

public class PlayerView extends Observable implements Observer{


    /**
     * @param obs observer to add to the observer list
     */
    public PlayerView(Observer obs){
        addObserver(obs);
    }

    /**
     * @param player
     * @param phase
     * print playing player info
     */
    private void printPlayer(Player player, String phase){
        System.out.println("Player id: " + player.getPlayerId());
        System.out.println("Mage name: " + player.getMageName().toString());
        System.out.println("Colour name: " + player.getSchoolColour().toString());
        if(phase.equals("E")){
            System.out.println("Coin count: " + player.getCoinCounter());
        }
    }


    @Override
    public void update(Observable o, Object arg) {
        Message msg = (Message) arg;
        Player player = (Player) o;

        switch (msg.getAction()){
            case "COIN_COUNTER_CHANGED":
                System.out.println("Player "+ player.getPlayerId() +" new coins balance: " + player.getCoinCounter());
                break;
            case "PRINT_PLAYER_INFO_S":
            case "SETUP_S":
                printPlayer(player, "S");
                break;
            case "PRINT_PLAYER_INFO_E":
            case "SETUP_E":
                printPlayer(player, "E");
                break;
            default:
                System.out.println("Player "+ player.getPlayerId() +" performed an action");
        }
    }
}
