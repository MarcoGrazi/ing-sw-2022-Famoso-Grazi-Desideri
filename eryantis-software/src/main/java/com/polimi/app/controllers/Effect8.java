package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.EffectStrategy;
import main.java.com.polimi.app.packets.Packet;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Effect 8.
 * During this turn you gain two additional influence points during the influence calc.
 * @author Group 53
 */
public class Effect8 implements EffectStrategy, Serializable {

    public ArrayList<Packet> ActivateEffect(GameController context, Packet packet){

        ArrayList<Packet> report = new ArrayList<>();
        for(Integer pId: context.getPlController().getPlayerIds()) {
            Packet responseEffect = new Packet("PLAYED_EFFECT", pId, packet.getGameId());
            responseEffect.addToPayload("character_played", 8);
            if(pId== packet.getPlayerId()){
                responseEffect.addToPayload("character_cost", context.getActiveCharactersController().getCostById(8));
            }else {
                responseEffect.addToPayload("message", "player "+packet.getPlayerId()+" played effect 8");
            }
            report.add(responseEffect);
        }

        context.replacePlayedCharacterByPlayerId(packet.getPlayerId(), 8, -1);
        return report;
    }
}
