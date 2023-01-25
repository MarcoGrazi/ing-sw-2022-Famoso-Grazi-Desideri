package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.EffectStrategy;
import main.java.com.polimi.app.packets.Packet;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Effect 9.
 * Chose a race. During this turn's influence calc, that race does not give influence point.
 * @author Group 53
 */
public class Effect9 implements EffectStrategy , Serializable {
    public ArrayList<Packet> ActivateEffect(GameController context, Packet packet){

        ArrayList<Packet> report = new ArrayList<>();
        String colourChoice = (String) packet.getFromPayload("character_colour_choice_9");

        for(Integer pId: context.getPlController().getPlayerIds()) {
            Packet responseEffect = new Packet("PLAYED_EFFECT", pId, packet.getGameId());
            responseEffect.addToPayload("character_played", 9);
            if(pId== packet.getPlayerId()){
                responseEffect.addToPayload("character_cost", context.getActiveCharactersController().getCostById(9));
            }else {
                responseEffect.addToPayload("message", "player "+packet.getPlayerId()+" played effect 9");
            }
            report.add(responseEffect);
        }

        context.replacePlayedCharacterByPlayerId(packet.getPlayerId(), 9, colourChoice);
        return report;
    }
}
