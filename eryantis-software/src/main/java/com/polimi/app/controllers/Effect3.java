package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.EffectStrategy;
import main.java.com.polimi.app.packets.Packet;
import main.java.com.polimi.app.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Effect 3.
 * Chose an island and calc the influence as if mother nature is on the island.
 * Mother nature will still move as usual during this turn and influence will be calculated on the island
 * she will land on.
 * @author Group 53
 */
public class Effect3 implements EffectStrategy, Serializable {

    public ArrayList<Packet> ActivateEffect(GameController context, Packet packet){

        ArrayList<Packet> report = new ArrayList<>();
        int islandChoice = (int) Math.floor(Float.parseFloat(packet.getFromPayload("character_island_choice_3").toString()));

        if(islandChoice < 1 || islandChoice > context.getArchipelagoController().getGroupsIndexes().size()){
            Packet groupError = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
            groupError.addToPayload("message", "group index out of bound");
            report.add(groupError);

            return report;
        } else {
            int groupIndex = (int)context.getArchipelagoController().getGroupsIndexes().toArray()[islandChoice-1];
            context.getArchipelagoController().archipelagoActionPhase(groupIndex);

            for(Integer pId: context.getPlController().getPlayerIds()) {
                Packet effectResponse = new Packet("PLAYED_EFFECT", pId, packet.getGameId());
                effectResponse.addToPayload("character_played", 3);
                effectResponse.addToPayload("archipelago_view", context.getArchipelagoController().getArchipelago().getArchipelagoInfo());

                if(pId== packet.getPlayerId()){
                    effectResponse.addToPayload("character_cost", context.getActiveCharactersController().getCostById(3));
                }else {
                    effectResponse.addToPayload("message", "player "+packet.getPlayerId()+" played effect 3");
                }
                report.add(effectResponse);
            }
        }

        context.replacePlayedCharacterByPlayerId(packet.getPlayerId(), 3, islandChoice);
        return report;
    }
}
