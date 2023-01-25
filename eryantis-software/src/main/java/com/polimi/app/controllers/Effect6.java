package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.EffectStrategy;
import main.java.com.polimi.app.packets.Packet;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Effect 6.
 * During the influence calc on an island or a group of island, the towers are not counted.
 * @author Group 53
 */
public class Effect6 implements EffectStrategy, Serializable {

    public ArrayList<Packet> ActivateEffect(GameController context, Packet packet){

        ArrayList<Packet> report = new ArrayList<>();
        for(Integer pId: context.getPlController().getPlayerIds()) {
            Packet responseEffect = new Packet("PLAYED_EFFECT", pId, packet.getGameId());
            responseEffect.addToPayload("character_played", 6);
            if(pId== packet.getPlayerId()){
                responseEffect.addToPayload("character_cost", context.getActiveCharactersController().getCostById(6));
            }else {
                responseEffect.addToPayload("message", "player "+packet.getPlayerId()+" played effect 6");
            }
            report.add(responseEffect);
        }

        context.replacePlayedCharacterByPlayerId(packet.getPlayerId(), 6, -1);
        return report;
    }
}
