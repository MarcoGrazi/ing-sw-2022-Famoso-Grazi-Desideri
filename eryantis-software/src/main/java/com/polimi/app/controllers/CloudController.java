package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.Cloud;
import main.java.com.polimi.app.models.Student;
import main.java.com.polimi.app.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Cloud controller class.
 * Manages the clouds.
 * @author Group 53
 */
public class CloudController implements Serializable {
    //Number of players
    private final int playerNumber;
    //Instance of the game controller
    private final GameController gmController;
    //The list of Cloud instances
    private final ArrayList<Cloud> cloudModels = new ArrayList<>();

    /**
     * Class constructor. Initializes game infos and N empty clouds depending on the number of players
     * @param gmController the game controller instance
     * @param playerNumber the number of players in this game
     */
    public CloudController( GameController gmController, int playerNumber ){
        this.playerNumber = playerNumber;
        this.gmController = gmController;

        cloudModels.add(new Cloud(1));
        cloudModels.add(new Cloud(2));
        if(playerNumber == 3) {
            cloudModels.add(new Cloud(3));
        }
    }

    /**
     * Adds N random students from the bag to each cloud.
     * @return a map of useful infos about the students drawn
     */
    public HashMap<Integer, Object> cloudPlanningPhase() {
        HashMap<Integer,Object> data = new HashMap<>();

        for(Cloud c: cloudModels) {
            ArrayList<Student> studentsDrawn = this.gmController.getBagController().drawForCloud();
            addStudentsToCloud(studentsDrawn, c.getCloudId());

            data.put(c.getCloudId(), Utils.getNumStudentsByRace(studentsDrawn));
        }

        return data;
    }

    /**
     * @return a map of useful infos about each cloud
     */
    public HashMap<Integer,Object> getCloudsInfo(){
        HashMap<Integer,Object> data = new HashMap<>();

        for(Cloud c: cloudModels) {

            data.put(c.getCloudId(), Utils.getNumStudentsByRace(c.getStudents()));
        }

        return data;
    }

    /**
     * Adds students to a specific cloud
     * @param students the list of students to add
     * @param cloudId the id of the cloud
     */
    public void addStudentsToCloud(ArrayList<Student> students, int cloudId) {
        if(students.size() > playerNumber + 1) {
            System.out.println("CloudMaxCapacityException: " + students.size());
        } else {
            for(Cloud c : cloudModels) {
                if(c.getCloudId() == cloudId) {
                    c.addStudents(students);
                }
            }
        }
    }

    /**
     * Removes all the students form a cloud
     * @param cloudId the id of the cloud
     * @return the list of students previously on the cloud
     */
    public ArrayList<Student> removeStudentsFromCloud(int cloudId) {
        for(Cloud c : cloudModels) {
            if(c.getCloudId() == cloudId) {
                return c.removeStudents();
            }
        }
        return null;
    }

    /**
     * @param cloudId the id of the cloud
     * @return true if the cloud is empty, false otherwise
     */
    public boolean isEmpty(int cloudId) {
        for(Cloud c : cloudModels) {
            if(c.getCloudId() == cloudId) {
                return c.getStudents().size() == 0;
            }
        }
        return true;
    }


}
