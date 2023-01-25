package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.EffectStrategy;
import main.java.com.polimi.app.packets.Packet;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Effect 4.
 * You can move mother nature of 2 additional islands compared to what is determined by the assistant card the player
 * played.
 * @author Group 53
 */
public class Effect4 implements EffectStrategy, Serializable {

    public ArrayList<Packet> ActivateEffect(GameController context, Packet packet){

        ArrayList<Packet> report = new ArrayList<>();

        for(Integer pId: context.getPlController().getPlayerIds()) {
            Packet responseEffect = new Packet("PLAYED_EFFECT", pId, packet.getGameId());
            responseEffect.addToPayload("character_played", 4);
            if(pId== packet.getPlayerId()){
                responseEffect.addToPayload("character_cost", context.getActiveCharactersController().getCostById(4));
            }else {
                responseEffect.addToPayload("message", "player "+packet.getPlayerId()+" played effect 4");
            }
            report.add(responseEffect);
        }

        context.replacePlayedCharacterByPlayerId(packet.getPlayerId(), 4, -1);
        return report;
    }
}
