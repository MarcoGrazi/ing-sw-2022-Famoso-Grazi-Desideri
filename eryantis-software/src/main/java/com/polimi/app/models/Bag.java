package main.java.com.polimi.app.models;

import java.io.Serializable;
import java.util.Random;
import java.util.*;

/**
 * Bag class.
 * Each instance is represented by an HashMap which maps the amount of students currently inside te bag to their race.
 * @author Group 53
 */
public class Bag implements Serializable {
    //Maps the remaining students for each race
    private final HashMap<Race, Integer> studentsCounter;
    //Array of races used to support methods
    //TODO: use UTILS
    protected Race[] raceArray = {Race.FAIRY, Race.DRAGON, Race.UNICORN, Race.FROG, Race.ELF};

    /**
     * Class constructor.
     * In total there are 26 students for each race. The bag is initialized with 24 students for each race. The 10 missing
     * students are the one already placed on the archipelago.
     */
    public Bag() {
        //Initializes HashMap
        studentsCounter = new HashMap<>();

        //Initializes all race counters to 24.
        studentsCounter.put(Race.FAIRY, 24);
        studentsCounter.put(Race.DRAGON, 24);
        studentsCounter.put(Race.UNICORN, 24);
        studentsCounter.put(Race.FROG, 24);
        studentsCounter.put(Race.ELF, 24);
    }

    /**
     * @return the hash map which contains the remaining students inside the bag
     */
    public HashMap<Race, Integer> getStudentsCounter() {
        return this.studentsCounter;
    }

    /**
     * Draws a given number of students from the bag, if there is not enough, draws all the remaining.
     * @param number amount of student to draw
     * @return a list containing all the drawn students
     */
    public ArrayList<Student> draw(int number) {
        ArrayList<Student> tmpStudents = new ArrayList<>();
        Race tmpRace;

        for (int i = 0; i < number && !isEmpty(); i++) {
            int numStudByRace;
            //Randomizes the race until the number of student of that race is greater than 0
            do {
                tmpRace = randomize();
                numStudByRace = this.studentsCounter.get(tmpRace);
            } while (numStudByRace == 0);

            //Extract a single student of the given random race by the map by decreasing the counter for that race by 1
            this.studentsCounter.replace(tmpRace, numStudByRace - 1);
            //Instantiates the student and adds it to the list
            tmpStudents.add(new Student(tmpRace));
        }
        //Returns tmpStudents
        return tmpStudents;
    }

    /**
     * Reinserts students inside the bag.
     * @param students list of students to reinsert inside the bag
     */
    public void reinsert(ArrayList<Student> students){
        for (Student s : students){
            //Insert a single student by increasing the counter for its race by 1
            Integer newCounter = this.studentsCounter.get(s.getRace()) + 1;
            this.studentsCounter.replace(s.getRace(), newCounter );
        }
    }

    /**
     * @return a random race
     */
    private Race randomize() {
        Random randomizer = new Random();

        int randIdx = randomizer.nextInt(5);
        return raceArray[randIdx];
    }

    /**
     * @return true if the bag is empty, false otherwise
     */
    public boolean isEmpty() {
        for (Race r : raceArray) {
            if (studentsCounter.get(r) > 0) {
                return false;
            }
        }
        return true;
    }

}
