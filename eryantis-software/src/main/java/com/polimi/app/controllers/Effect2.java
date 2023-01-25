package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.EffectStrategy;
import main.java.com.polimi.app.packets.Packet;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Effect 2.
 * During this turn, you still gain a professor even if the number of students in your table is even to the number
 * of students inside the table of the player who now controls that professor.
 * @author Group 53
 */
public class Effect2 implements EffectStrategy, Serializable {

    public ArrayList<Packet> ActivateEffect(GameController context, Packet packet){

        ArrayList<Packet> report = new ArrayList<>();

        for(Integer pId: context.getPlController().getPlayerIds()) {
            Packet responseEffect = new Packet("PLAYED_EFFECT", pId, packet.getGameId());
            responseEffect.addToPayload("character_played", 2);
            if(pId== packet.getPlayerId()){
                responseEffect.addToPayload("character_cost", context.getActiveCharactersController().getCostById(2));
            }else {
                responseEffect.addToPayload("message", "player "+packet.getPlayerId()+" played effect 2");
            }
            report.add(responseEffect);
        }

        context.replacePlayedCharacterByPlayerId(packet.getPlayerId(), 2, -1);
        return report;
    }
}
