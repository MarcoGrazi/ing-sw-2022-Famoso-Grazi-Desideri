package main.java.com.polimi.app.models;

import main.java.com.polimi.app.utils.Utils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Observable;
import java.util.Stack;

/**
 * School class.
 * Each instance is represented by an absolute schoolId, unique for each school,
 * an ArrayList of students which stores the students inside the school hall,
 * an HashMap which maps each race to a table defined by a Stack that stores the students currently sat at the table,
 * an HashMap which maps each race to a boolean that defines whether the professor of that race is present in the school,
 * an ArrayList of Towers which stores the towers still placeable inside the archipelago,
 * and a school colour, equals to the tower one.
 * @author Group 53
 */
public class School implements Serializable {
    //The absolute schoolId. Unique for each deck.
    private int schoolId;
    //The list of students inside the hall.
    private final ArrayList<Student> hall;
    //The map between race and tables
    private final LinkedHashMap<Race, Stack<Student>> table;
    //The map between race and professors
    private final LinkedHashMap<Race, Boolean> professorTable;
    //The list of towers still inside the school
    private final ArrayList<Tower> towers;
    //The colour of the school
    private Colour towerColour;

    /**
     * Class constructor.
     * Initializes the absolute schoolId.
     * Initializes the hall, the students' tables and the professors' table to empty.
     * Initializes the tower list with the correct towers received as parameter.
     * Initializes the school colour as the towers one.
     * @param towers the list of tower owned by the school.
     * @param schoolId the absolute schoolId.
     */
    public School(ArrayList<Tower> towers, int schoolId){
        this.hall= new ArrayList<>();
        this.table = new LinkedHashMap<>();
        this.schoolId=schoolId;
        table.put(Race.FAIRY, new Stack<>());
        table.put(Race.DRAGON, new Stack<>());
        table.put(Race.UNICORN, new Stack<>());
        table.put(Race.FROG, new Stack<>());
        table.put(Race.ELF, new Stack<>());

        this.professorTable = new LinkedHashMap<>();
        professorTable.put(Race.FAIRY, false);
        professorTable.put(Race.DRAGON, false);
        professorTable.put(Race.UNICORN, false);
        professorTable.put(Race.FROG, false);
        professorTable.put(Race.ELF, false);

        this.towers= new ArrayList<>();
        this.towers.addAll(towers);
        this.towerColour = towers.get(0).getColour();
    }

    /**
     * @return the school absolute Id
     */
    public int getSchoolId(){
        return this.schoolId;
    }

    /**
     * @return the school list of towers, without removing them
     */
    public ArrayList<Tower> getTowers() {
        return towers;
    }

    /**
     * @return the school colour
     */
    public Colour getTowerColour() {
        return towerColour;
    }

    /**
     * @return the list of students in the hall, without removing them
     */
    public ArrayList<Student> getStudentsInHall() {
        return this.hall;
    }

    /**
     * @param race the selected race
     * @return the list of students in the table related to the selected race, without removing them
     */
    public Stack<Student> getStudentsInTable(Race race){
        return this.table.get(race);
    }

    /**
     * @return the amount of students inside the school tables.
     */
    public ArrayList<Integer> getTablesSizes() {
        ArrayList<Integer> counters = new ArrayList<>();

        for(Race r: table.keySet()) {
            counters.add(table.get(r).size());
        }

        return counters;
    }

    /**
     * @param race the selected race
     * @return true if the professor of the selected race is inside the school, false otherwise
     */
    public boolean getProfessorInTable(Race race){
        return this.professorTable.get(race);
    }

    /**
     * Reorders the students inside the hall referring to the race order inside the Utils race array
     */
    public void orderHall() {
        ArrayList<String> racesNames = Utils.getRaceString();
        this.hall.sort((o1, o2) -> {
            Integer index1 = racesNames.indexOf(o1.getRace().name());
            Integer index2 = racesNames.indexOf(o2.getRace().name());

            return index1.compareTo(index2);
        });
    }

    /**
     * @param index the selected student index
     * @return the selected student, without removing it from the hall
     */
    public Student getStudentFromHallByIndex(Integer index) {
        if(index < hall.size() && index >= 0) {
            return hall.get(index);
        }
        else{
            return null;
        }
    }

    /**
     * @param index the selected student index
     * @return the selected student, by removing it from the hall
     */
    public Student removeStudentFromHallByIndex(int index) {
        return hall.remove(index);
    }

    /**
     * Removes students from hall by student instance
     * @param students the list of students to remove
     */
    public void removeStudentsFromHall(ArrayList<Student> students){
        hall.removeAll(students);
    }

    /**
     * Adds the given students to the hall.
     * @param students list of students to add to the hall.
     */
    public void addStudentsToHall(ArrayList<Student> students){
        this.hall.addAll(students);

        orderHall();
    }

    /**
     * Replaces a student inside the hall with a student from a specific table
     * @param hallIndex the index of the student inside the hall
     * @param tableRace the race of the student inside the table
     */
    public void exchangeStudents(Integer hallIndex, Race tableRace) {
        Student hallStudent = getStudentFromHallByIndex(hallIndex);
        Student tableStudent = table.get(tableRace).pop();

        moveStudentToTable(hallStudent);
        hall.set(hallIndex, tableStudent);
    }

    /**
     * Adds a given student to a table.
     * @param student the student to add to the table.
     */
    public void moveStudentToTable(Student student) {
        this.table.get(student.getRace()).add(student);
    }

    /**
     * Remove students from a specific table. If the table has not enough students, removes all of them.
     * @param quantity the amount of students to remove
     * @param race the race of the students to remove
     * @return the list of students removed from the table
     */
    public ArrayList<Student> removeStudentsFromTable(int quantity, Race race) {
        ArrayList<Student> students = new ArrayList<>();
        if(quantity <= table.get(race).size()) {
            for(int i=0; i<quantity; i++) {
                students.add(table.get(race).pop());
            }
        } else {
            students.addAll(table.get(race));
            table.get(race).clear();
        }
        return students;
    }

    /**
     * Set the presence of the professors defined by the selected race.
     * @param race the selected race
     * @param present true if the professors is present, false otherwise
     */
    public void setProfessor(Race race, boolean present){
        this.professorTable.replace(race,present);
    }

    /**
     * Add towers to the school towers list.
     * @param towers the list of towers to add
     * @throws Exception if towers colour is different from the school one, or if the final amount of towers is greater than 8
     */
    public void addTowers(ArrayList<Tower> towers) throws Exception {
        for (Tower t : towers){
            if(!t.getColour().equals(this.getTowers().get(0).getColour())){
                throw new Exception("TowerColourMismatchException: Expected colour "+this.getTowers().get(0).getColour().toString()+" but "+t.getColour().toString()+" given");
            }
        }
        //Checks if final total of towers is greater than 8
        if((this.towers.size() + towers.size()) >8){
            //throw an exception
            throw new Exception("TowerSizeExceededException: passed "+towers.size()+ " max amount possible: "+ (8-this.towers.size()));
        }
        else{
            this.towers.addAll(towers);
        }
    }

    /**
     *
     * @param quantity the amount of towers to retrieve
     * @return a list of the selected amount of towers
     * @throws Exception if the quantity is lesser than 0 or greater than the amount of towers inside the school.
     */
    public ArrayList<Tower> subTowers(int quantity) throws Exception{
        if(quantity > this.towers.size()){
            //throw an exception
            throw new Exception("NotEnoughTowersException: max amount subtractable is: " + this.getTowers().size());
        }
        else if(quantity < 0){
            throw new Exception("NegativeQuantityAsArgumentException");
        }
        else{
            ArrayList<Tower> tmpTowers = new ArrayList<>();
            for (int i=0; i<quantity; i++){
                tmpTowers.add(towers.remove(towers.size()-1));
            }
            return tmpTowers;
        }

    }

}
