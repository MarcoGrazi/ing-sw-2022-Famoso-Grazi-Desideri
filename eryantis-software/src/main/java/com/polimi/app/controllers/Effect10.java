package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.EffectStrategy;
import main.java.com.polimi.app.models.School;
import main.java.com.polimi.app.models.Student;
import main.java.com.polimi.app.packets.Packet;
import main.java.com.polimi.app.models.Race;
import main.java.com.polimi.app.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Effect 10.
 * You can exchange up to two students from your tables with the same amount of students in your hall.
 * @author Group 53
 */
public class Effect10 implements EffectStrategy, Serializable {

    public ArrayList<Packet> ActivateEffect(GameController context, Packet packet) {

        ArrayList<Packet> report = new ArrayList<>();
        School playerSchool = context.getSchoolController().getSchoolByIndex(packet.getPlayerId());

        int numberOfMoves = (int) Math.floor(Float.parseFloat(packet.getFromPayload("character_number_moves").toString()));

        ArrayList<Race> studentTableChoices = new ArrayList<>();
        ArrayList<Integer> studentHallChoices = new ArrayList<>();
        //Gets hall and races choices
        for (int i = 0; i < numberOfMoves; i++) {
            studentHallChoices.add((int) Math.floor(Float.parseFloat(packet.getFromPayload("character_hall_choice_" + i).toString())));
            studentTableChoices.add(Utils.getRaceFromString(packet.getFromPayload("character_table_choice_" + i).toString()));
        }

        //Checks if choices are duplicated or invalid
        for (int i = 0; i< studentHallChoices.size(); i++) {
            if(studentHallChoices.lastIndexOf(studentHallChoices.get(i)) != i) {
                Packet choiceError = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                choiceError.addToPayload("message", "you have chosen twice the same student from the hall");
                report.add(choiceError);
                return report;
            }

            if (playerSchool.getStudentFromHallByIndex(studentHallChoices.get(i)) == null) {
                Packet choiceError = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                choiceError.addToPayload("message", "choice number " + studentHallChoices.get(i) + " out of range");
                report.add(choiceError);
                return report;
            }
        }

        //Checks how many times a race has been chosen
        LinkedHashMap<Race, Integer> raceCont = new LinkedHashMap<>();
        for (int i = 0; i < studentTableChoices.size(); i++) {
            if (raceCont.containsKey(studentTableChoices.get(i))) {
                int cont = raceCont.get(studentTableChoices.get(i));
                raceCont.replace(studentTableChoices.get(i), cont + 1);
            } else {
                raceCont.put(studentTableChoices.get(i), 1);
            }
        }
        for (Race race : raceCont.keySet()) {
            if (playerSchool.getStudentsInTable(race).size() < raceCont.get(race)) {
                Packet choiceError = new Packet("ERROR", packet.getPlayerId(), packet.getGameId());
                choiceError.addToPayload("message", "there are not enough " + race.getColour() + " students inside your table");
                report.add(choiceError);
                return report;
            }
        }

        //Returns the player's new amount of coins after the exchanges
        int newCoins = context.getSchoolController().exchangeHallTable(studentHallChoices, studentTableChoices, playerSchool.getSchoolId());

        context.replacePlayedCharacterByPlayerId(packet.getPlayerId(), 10, -1);
        for (Integer pId : context.getPlController().getPlayerIds()) {
            Packet responseEffect = new Packet("PLAYED_EFFECT", pId, packet.getGameId());
            responseEffect.addToPayload("character_played", 10);
            responseEffect.addToPayload("professors_view", context.getSchoolController().setProfessors());
            if (pId == packet.getPlayerId()) {
                responseEffect.addToPayload("school_view", context.getSchoolController().getSchoolInfo(pId));
                responseEffect.addToPayload("coins", newCoins);
                responseEffect.addToPayload("character_cost", context.getActiveCharactersController().getCostById(10));
            } else {
                responseEffect.addToPayload("message", "player " + packet.getPlayerId() + " played effect 10");
            }
            report.add(responseEffect);
        }

        return report;
    }
}
