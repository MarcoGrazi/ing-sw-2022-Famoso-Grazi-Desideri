package main.java.com.polimi.app.controllers;
import main.java.com.polimi.app.models.EffectStrategy;
import main.java.com.polimi.app.models.School;
import main.java.com.polimi.app.models.Student;
import main.java.com.polimi.app.packets.Packet;
import main.java.com.polimi.app.utils.Utils;

import java.io.Serializable;
import java.util.*;

/**
 * Effect 11.
 * Choose a student from this card and place it inside your table. Then draw a student from the bag and place it on the card.
 * @author Group 53
 */
public class Effect11 implements EffectStrategy, Serializable {
    ArrayList<Student> students;

    /**
     * Class constructor.
     * @param context an instance of the game controller
     */
    public Effect11(GameController context){
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
        School playerSchool = context.getSchoolController().getSchoolByIndex(packet.getPlayerId());

        int studentChoice = (int) Math.floor(Float.parseFloat(packet.getFromPayload("character_student_choice_11").toString()));
        String studentRace = students.get(studentChoice).getRace().name();

        //Adds the chosen student to the player school
        playerSchool.moveStudentToTable(students.remove(studentChoice));

        //Updates the coins
        int numStudents = context.getSchoolController().getNumStudentsInTables(packet.getPlayerId()).
                    get(Utils.getRaceString().indexOf(studentRace));
        if(numStudents % 3 == 0) {
            context.getPlController().addCoin(packet.getPlayerId());
        }

        //Adds a new student to the character
        students.addAll(context.getBagController().drawStudentsByQuantity(1));
        students = Utils.sortStudentByRace(students);

        context.replacePlayedCharacterByPlayerId(packet.getPlayerId(), 11, -1);
        for(Integer pId: context.getPlController().getPlayerIds()) {
            Packet responseEffect = new Packet("PLAYED_EFFECT", pId, packet.getGameId());
            responseEffect.addToPayload("character_played", 11);
            responseEffect.addToPayload("professors_view", context.getSchoolController().setProfessors());
            responseEffect.addToPayload("school_view", context.getSchoolController().getSchoolInfo(pId));
            responseEffect.addToPayload("character_view", Utils.getNumStudentsByRace(students));
            if(pId== packet.getPlayerId()){
                responseEffect.addToPayload("coins", context.getPlController().getCoins(packet.getPlayerId()));
                responseEffect.addToPayload("character_cost", context.getActiveCharactersController().getCostById(11));
            }else {
                responseEffect.addToPayload("message", "player "+packet.getPlayerId()+" played effect 11");
            }
            report.add(responseEffect);
        }

        return report;
    }
}
