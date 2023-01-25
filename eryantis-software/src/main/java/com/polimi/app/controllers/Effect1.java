package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.EffectStrategy;
import main.java.com.polimi.app.models.Student;
import main.java.com.polimi.app.packets.Packet;
import main.java.com.polimi.app.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Effect 1.
 * Take one student from the card and place it on an island of your choice. Then, draw a student from the bag and place
 * it on this card.
 * @author Group 53
 */
public class Effect1 implements EffectStrategy, Serializable {
    //The list of students placed on the card
    ArrayList<Student> students;

    /**
     * Class constructor.
     * Initializes the list with 4 students drawn from the bag.
     * @param context the instance of the game controller.
     */
    public Effect1(GameController context){
        students = new ArrayList<>();
        students.addAll(context.getBagController().drawStudentsByQuantity(4));
        students = Utils.sortStudentByRace(students);
    }

    /**
     * @return the list of students placed on the card
     */
    public ArrayList<Student> getStudents() {
        return students;
    }

    public ArrayList<Packet> ActivateEffect(GameController context, Packet packet){

        ArrayList<Packet> report = new ArrayList<>();
        int studentChoice = (int) Math.floor(Float.parseFloat(packet.getFromPayload("character_student_choice_1").toString()));
        int islandChoice = (int) Math.floor(Float.parseFloat(packet.getFromPayload("character_island_choice_1").toString())) - 1;

        if(islandChoice <= 0 || context.getArchipelagoController().getGroupsIndexes().size() <= islandChoice){
            Packet groupError = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
            groupError.addToPayload("message", "group index out of bound");
            report.add(groupError);

            return report;
        } else {
            int groupIndex = (int)context.getArchipelagoController().getGroupsIndexes().toArray()[islandChoice];

            context.getArchipelagoController().addStudentToIsland(students.remove(studentChoice), groupIndex);
            students.addAll(context.getBagController().drawStudentsByQuantity(1));
            students = Utils.sortStudentByRace(students);

            for(Integer pId: context.getPlController().getPlayerIds()) {
                Packet responseEffect = new Packet("PLAYED_EFFECT", pId, packet.getGameId());
                responseEffect.addToPayload("character_played", 1);
                responseEffect.addToPayload("archipelago_view", context.getArchipelagoController().getArchipelago().getArchipelagoInfo());
                responseEffect.addToPayload("character_view", Utils.getNumStudentsByRace(students));
                if(pId== packet.getPlayerId()){
                    responseEffect.addToPayload("character_cost", context.getActiveCharactersController().getCostById(1));
                }else {
                    responseEffect.addToPayload("message", "player "+packet.getPlayerId()+" played effect 1");
                }
                report.add(responseEffect);
            }
        }

        context.replacePlayedCharacterByPlayerId(packet.getPlayerId(), 1, -1);
        return report;
    }
}
