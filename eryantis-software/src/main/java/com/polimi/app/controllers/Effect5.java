package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.EffectStrategy;
import main.java.com.polimi.app.packets.Packet;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Effect 5.
 * Place a prohibition on an island of your choice. The first time that Mother Nature is going to end her movement on
 * that island, replace the prohibition on this card without applying the influence calc and without placing any tower.
 * @author Group 53
 */
public class Effect5 implements EffectStrategy, Serializable {
    //List of prohibitions on the card
    private int prohibitionCounter;

    /**
     * Class constructor.
     * Initializes the number of prohibition.
     */
    public Effect5(){
        this.prohibitionCounter = 4;
    }

    /**
     * @return prohibition amount
     */
    public int getProhibitionCounter() {
        return prohibitionCounter;
    }

    /**
     * Adds a prohibition to the character
     */
    public void addProhibition(){
        this.prohibitionCounter +=1;
    }

    public ArrayList<Packet> ActivateEffect(GameController context, Packet packet){

        ArrayList<Packet> report = new ArrayList<>();
        int islandChoice = (int) Math.floor(Float.parseFloat(packet.getFromPayload("character_island_choice_5").toString()));

        if(islandChoice < 1 || islandChoice > context.getArchipelagoController().getGroupsIndexes().size()){
            Packet groupError = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
            groupError.addToPayload("message", "group index out of bound");
            report.add(groupError);

            return report;
        } else{
            int groupIndex = (int) context.getArchipelagoController().getGroupsIndexes().toArray()[islandChoice-1];
            context.getArchipelagoController().addProhibition(groupIndex);
            prohibitionCounter -= 1;

            for(Integer pId: context.getPlController().getPlayerIds()) {
                Packet effectResponse = new Packet("PLAYED_EFFECT", pId, packet.getGameId());
                effectResponse.addToPayload("archipelago_view", context.getArchipelagoController().getArchipelago().getArchipelagoInfo());
                effectResponse.addToPayload("character_view", prohibitionCounter);
                effectResponse.addToPayload("character_played", 5);
                if(pId== packet.getPlayerId()){
                    effectResponse.addToPayload("character_cost", context.getActiveCharactersController().getCostById(5));
                }else {
                    effectResponse.addToPayload("message", "player "+packet.getPlayerId()+" played effect 5");
                }
                report.add(effectResponse);
            }

            context.replacePlayedCharacterByPlayerId(packet.getPlayerId(), 5, groupIndex);
        }

        return report;
    }
}
