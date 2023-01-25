package main.java.com.polimi.app.models;
import java.io.Serializable;
import java.util.*;

/**
 * Island class.
 * Represented by a tower slot, which can store and a single tower or none, an ArrayList of students which stores the
 * students currently placed on the island.
 * @author Group 53
 */
public class Island implements Serializable {
    //TODO: rimuovere in quanto inutile
    private Integer islandId;
    //The slot for the tower
    private Tower tower;
    //The list of students currently placed on the island
    private ArrayList<Student> studentList;

    /**
     * Class constructor.
     * Initializes an empty island
     * @param islandId
     */
    public Island(Integer islandId) {
        this.islandId = islandId;
        this.studentList = new ArrayList<>();
        this.tower = null;
    }

    //TODO: remove
    public Integer getIslandId() {
        return islandId;
    }

    /**
     * @return the tower present on the island, without removing it
     */
    public Tower getTower() {
        return this.tower;
    }

    /**
     * @return the tower present on the island, removing it
     */
    public Tower removeTower() {
        Tower tmpTower = this.tower;
        this.tower = null;
        return tmpTower;
    }

    /**
     * @return the list of student currently placed on the island
     */
    public ArrayList<Student> getStudentList() {
        return this.studentList;
    }

    /**
     * @param race the selected race
     * @return the amount of student of the selected race currently placed on the island
     */
    public int getStudentNumByRace(Race race) {
        int studentCounter = 0;
        for (Student s : studentList) {
            if (race.equals(s.getRace())) {
                studentCounter++;
            }
        }
        return studentCounter;
    }

    /**
     * Adds a single student to the island.
     * @param newStudent the student to add
     */
    public void addStudent(Student newStudent) {
        this.studentList.add(newStudent);
    }

    /**
     * Replace the island tower with a new one.
     * @param newTower the tower to place on the island
     */
    public void setTower(Tower newTower) {
        this.tower = newTower;
    }
}
