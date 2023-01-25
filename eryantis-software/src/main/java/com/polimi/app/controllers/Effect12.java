package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.EffectStrategy;
import main.java.com.polimi.app.models.Race;
import main.java.com.polimi.app.packets.Packet;
import main.java.com.polimi.app.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Effect 12.
 * Choose a race, every player must return to the bag three students of that race placed inside their tables.
 * If someone has less than three students of that race, they must return as many students as they can, by emptying the table.
 * @author Group 53
 */
public class Effect12 implements EffectStrategy, Serializable {

    public ArrayList<Packet> ActivateEffect(GameController context, Packet packet){

        ArrayList<Packet> report = new ArrayList<>();
        Race selectedRace = Utils.getRaceFromString(packet.getFromPayload("character_colour_choice_12").toString());

        for(Integer pId: context.getPlController().getPlayerIds()) {
            context.getBagController().reinsertStudents(
                    context.getSchoolController().removeStudentsFromTable(3, selectedRace, pId));
        }

        for(Integer pId: context.getPlController().getPlayerIds()) {
            Packet responseEffect = new Packet("PLAYED_EFFECT", pId, packet.getGameId());
            responseEffect.addToPayload("character_played", 12);
            responseEffect.addToPayload("professors_view", context.getSchoolController().setProfessors().get("prof_schoolIds"));
            responseEffect.addToPayload("school_view", context.getSchoolController().getSchoolInfo(pId));
            if(pId== packet.getPlayerId()){
                responseEffect.addToPayload("character_cost", context.getActiveCharactersController().getCostById(12));
            }else {
                responseEffect.addToPayload("message", "player "+packet.getPlayerId()+" played effect 12");
            }
            report.add(responseEffect);
        }

        context.replacePlayedCharacterByPlayerId(packet.getPlayerId(), 12, -1);
        return report;
    }
}
