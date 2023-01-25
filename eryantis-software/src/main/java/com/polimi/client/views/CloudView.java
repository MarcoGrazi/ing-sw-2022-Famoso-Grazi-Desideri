package main.java.com.polimi.client.views;

import main.java.com.polimi.client.models.Clouds;
import main.java.com.polimi.client.models.Message;
import main.java.com.polimi.client.models.Race;
import main.java.com.polimi.client.models.Student;
import main.java.com.polimi.client.utils.ColorStrings;
import main.java.com.polimi.client.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public class CloudView extends Observable implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        Clouds clouds = (Clouds) o;

        printToCli(clouds.getClouds());
    }

    /**
     * @param clouds
     * prints the clouds with their students
     */
    private void printToCli(HashMap<Integer, ArrayList<Student>> clouds) {
        for(Integer clId: clouds.keySet()){
            ArrayList<Integer> numStuPerRace = Utils.getNumStudentsByRace(clouds.get(clId));
            System.out.println("Students in cloud: "+clId);
            for(int i =0; i<numStuPerRace.size(); i++){
                System.out.println("Students of race "+Utils.raceArray.get(i).getColour()+" "+numStuPerRace.get(i));
            }
        }

    }
}
