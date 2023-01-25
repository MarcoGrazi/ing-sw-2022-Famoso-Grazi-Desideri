package main.java.com.polimi.client.models;

import main.java.com.polimi.client.utils.Utils;

import java.util.*;

/**
 * School class.
 * Each instance is represented by an ArrayList of students which stores the students inside the school hall,
 * an HashMap which maps each race to a table defined by a Stack that stores the students currently sat at the table,
 * an HashMap which maps each race to a boolean that defines whether the professor of that race is present in the school,
 * an ArrayList of Towers which stores the towers still placeable inside the archipelago,
 * and a school colour, equals to the tower one.
 * @author Group 53
 */
public class School extends Observable {
    //The list of students inside the hall.
    private  ArrayList<Student> hall;
    //The map between race and tables
    private  Map<Race, Integer> table;
    //The map between race and professors
    private  Map<Race, Boolean> professorTable;
    //The list of towers still inside the school
    private ArrayList<Tower> towers;
    //The colour of the school
    private Colour towerColour;
    private final String[] allRaces = {"FAIRY", "DRAGON", "UNICORN", "FROG", "ELF"};

    /**
     * Class constructor.
     * Initializes the hall, the students' tables and the professors' table.
     * Initializes the tower list with the correct towers received as parameter.
     * Initializes the school colour as the towers one.
     * @param towers the list of tower owned by the school.
     * @param students the absolute schoolId.
     */
    public School(ArrayList<Tower> towers, ArrayList<Double> students,  Observer view){
        this.hall= new ArrayList<>();
        this.table = new HashMap<>();
        table.put(Race.FAIRY, 0);
        table.put(Race.DRAGON, 0);
        table.put(Race.UNICORN, 0);
        table.put(Race.FROG, 0);
        table.put(Race.ELF, 0);


        this.professorTable = new HashMap<>();
        professorTable.put(Race.FAIRY, false);
        professorTable.put(Race.DRAGON, false);
        professorTable.put(Race.UNICORN, false);
        professorTable.put(Race.FROG, false);
        professorTable.put(Race.ELF, false);

        this.towers= new ArrayList<>();
        this.towers.addAll(towers);
        this.towerColour = towers.get(0).getColour();

        for(int i =0; i< allRaces.length; i++){
            int students_to_add = (int) Math.round(Float.parseFloat(students.get(i).toString()));
            for (int j =0; j<students_to_add; j++){
                hall.add(new Student(Utils.getRaceFromString(allRaces[i])));
            }
        }

        ArrayList<String> racesNames = Utils.getRaceString();
        this.hall.sort((o1, o2) -> {
            Integer index1 = racesNames.indexOf(o1.getRace().name());
            Integer index2 = racesNames.indexOf(o2.getRace().name());

            return index1.compareTo(index2);
        });

        addObserver(view);
        setChanged();
        notifyObservers(new Message("SETUP_SCHOOL"));
    }


    //TODO: remove
    public void addView(Observer view){
        addObserver(view);
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
    public int getStudentsInTable(Race race){
        return this.table.get(race);
    }

    /**
     * @param race the selected race
     * @return true if the professor of the selected race is inside the school, false otherwise
     */
    public boolean getProfessorInTable(Race race){
        return this.professorTable.get(race);
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

    //TODO: remove
    public void setProfessor(Race race, boolean present){
        this.professorTable.replace(race,present);
    }

    /**
     * Sets the towers owned by the school
     * @param towers
     */
    public void setTowers(ArrayList<Tower> towers) {
        this.towers= towers;
    }

    //TODO: remove
    public void setTowerColour(Colour towerColour) {
        this.towerColour = towerColour;
    }

    /**
     * Sets the hall with a new sets of students
     * @param hall the list of new students to add
     */
    public void setHall(ArrayList<Student> hall) {
        this.hall = hall;

        ArrayList<String> racesNames = Utils.getRaceString();
        this.hall.sort((o1, o2) -> {
            Integer index1 = racesNames.indexOf(o1.getRace().name());
            Integer index2 = racesNames.indexOf(o2.getRace().name());

            return index1.compareTo(index2);
        });
    }

    /**
     * Sets all the tables with new sets of students
     * @param table the new tables map
     */
    public void setTable(Map<Race, Integer> table) {
        this.table = table;
    }

    /**
     * Sets the whole table of professors with new professors
     * @param professorTable the new table of professors
     */
    public void setProfessorTable(Map<Race, Boolean> professorTable) {
        this.professorTable = professorTable;
    }

    /**
     * Adds one student to a specific table
     * @param race the race of the student
     */
    public void addToTable(Race race) {
        this.table.replace(race, table.get(race)+1);
    }

    /**
     * Sets a specific professor to present
     * @param raceFromString the race of the professor
     */
    public void setProfessorToTrue(Race raceFromString) {
        professorTable.replace(raceFromString,true);
    }

    /**
     * Sets a specific professor to absent
     * @param raceFromString the race of the professor
     */
    public void setProfessorToFalse(Race raceFromString) {
        professorTable.replace(raceFromString,false);
    }

    /**
     * Updates the view to print the school
     */
    public void printSchool(){
        setChanged();
        notifyObservers(new Message("PRINT_SCHOOL"));
    }

    /**
     * Clears all tables
     */
    public void clearTables(){
        table.put(Race.FAIRY, 0);
        table.put(Race.DRAGON, 0);
        table.put(Race.UNICORN, 0);
        table.put(Race.FROG, 0);
        table.put(Race.ELF, 0);
    }

}


