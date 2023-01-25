package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.EffectStrategy;
import main.java.com.polimi.app.models.Student;
import main.java.com.polimi.app.packets.Packet;
import main.java.com.polimi.app.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Effect 7.
 * You can take up to three students from this card and exchange with the same number of students from your hall.
 * @author Group 53
 */
public class Effect7 implements EffectStrategy, Serializable {
    //The list of students placed on the card
    ArrayList<Student> students;

    /**
     * Class constructor.
     * @param context an instance of the game controller
     */
    public Effect7(GameController context){
        students = new ArrayList<Student>();
        students.addAll(context.getBagController().drawStudentsByQuantity(6));
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
        int numberOfMoves = (int) Math.floor(Float.parseFloat(packet.
                getFromPayload("character_number_moves").toString()));
        ArrayList<Integer> studentCardChoices = new ArrayList<>();
        ArrayList<Integer> studentHallChoices = new ArrayList<>();
        for(int i=0; i<numberOfMoves; i++){
            studentHallChoices.add((int) Math.floor(Float.parseFloat(packet.
                    getFromPayload("character_hall_choice_" + i).toString())));
            studentCardChoices.add((int) Math.floor(Float.parseFloat(packet.
                    getFromPayload("character_card_choice_" + i).toString())));
        }
        for(int i=0; i<studentHallChoices.size(); i++){
            if(context.getSchoolController().getSchools().get(context.getSchoolController().
                            getSchoolIndex(packet.getPlayerId())).
                    getStudentFromHallByIndex(studentHallChoices.get(i)) == null){
                Packet choiceError = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                choiceError.addToPayload("message", "choice number " + i + " for students in hall is invalid");
                report.add(choiceError);
                return report;
            }
        }

        ArrayList<Student> studentsToHall = new ArrayList<>();
        for(int i=0; i < studentCardChoices.size(); i++){
            studentsToHall.add(students.get(studentCardChoices.get(i)));
        }
        ArrayList<Student> studentsToCard = new ArrayList<>();
        for(int i=0; i< studentHallChoices.size(); i++){
            studentsToCard.add(context.getSchoolController().getSchools().get(context.getSchoolController().
                            getSchoolIndex(packet.getPlayerId())).
                    getStudentFromHallByIndex(studentHallChoices.get(i)));
        }

        students.removeAll(studentsToHall);
        students.addAll(studentsToCard);
        students = Utils.sortStudentByRace(students);

        context.getSchoolController().getSchools().get(context.getSchoolController().
                getSchoolIndex(packet.getPlayerId())).removeStudentsFromHall(studentsToCard);
        context.getSchoolController().moveStudentsToHall(studentsToHall,packet.getPlayerId());

        for(Integer pId: context.getPlController().getPlayerIds()) {
            Packet responseEffect = new Packet("PLAYED_EFFECT", pId, packet.getGameId());
            responseEffect.addToPayload("character_played", 7);
            responseEffect.addToPayload("school_view", context.getSchoolController().getSchoolInfo(pId));
            responseEffect.addToPayload("character_view", Utils.getNumStudentsByRace(students));
            if(pId== packet.getPlayerId()){
                responseEffect.addToPayload("character_cost", context.getActiveCharactersController().getCostById(7));
            }else {
                responseEffect.addToPayload("message", "player "+packet.getPlayerId()+" played effect 7");
            }
            report.add(responseEffect);
        }

        context.replacePlayedCharacterByPlayerId(packet.getPlayerId(), 7, -1);
        return report;
    }
}
