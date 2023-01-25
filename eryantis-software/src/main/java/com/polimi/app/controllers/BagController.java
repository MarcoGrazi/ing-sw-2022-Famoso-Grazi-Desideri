package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.Bag;
import main.java.com.polimi.app.models.Student;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Bag controller class.
 * Manages the bag.
 * @author Group 53
 */
public class BagController implements Serializable {
    //Number of players
    private final int playerNumber;
    //Instance of the game controller
    private final GameController gmController;
    //The Bag instance
    private final Bag bagModel = new Bag();

    /**
     * Class constructor. Initializes game info.
     * @param gmController
     * @param playerNumber
     */
    public BagController (GameController gmController, int playerNumber) {
        this.playerNumber = playerNumber;
        this.gmController = gmController;
    }

    /**
     * Reinserst some students inside the bag.
     * @param students list of students to reinsert
     */
    public void reinsertStudents(ArrayList<Student> students) {
        this.bagModel.reinsert(students);
    }

    /**
     * Draws a specific amount of students, depending on the player number
     * @return the list of drawn students
     */
    public ArrayList<Student> drawForCloud() {
        ArrayList<Student> students;

        if(playerNumber == 2) {
            students = this.bagModel.draw(3);
        } else {
            students = this.bagModel.draw(4);
        }

        if(this.bagModel.isEmpty()) {
            this.gmController.endAfterTurn();
        }

        return students;
    }

    /**
     * Draws a specific amount of students from the bag.
     * @param number the amount of students to draw
     * @return the list of drawn students
     */
    public ArrayList<Student> drawStudentsByQuantity(int number) {
        return this.bagModel.draw(number);
    }
}
