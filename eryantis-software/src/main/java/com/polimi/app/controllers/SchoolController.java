package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.*;
import main.java.com.polimi.app.utils.Utils;

import java.io.Serializable;
import java.util.*;

/**
 * School controller class.
 * Manages the school instances.
 * @author Group 53
 */
public class SchoolController implements Serializable {
    //Number of players
    private final int playerNumber;
    //Instance of the game controller
    private final GameController gmController ;
    //The list of School instances
    private final ArrayList<School> schoolModels = new ArrayList<>();
    //A list of player indexes parallel to the Utils race one which tells, for each race, who has that professor
    private ArrayList<Integer> professorsMap;

    /**
     * Class constructor. Initializes the game infos, sets professors ids to -1 (no one), initializes N schools with M towers of
     * each players' chosen colour and links them to the players by each player ids.
     * @param gmController the game controller instance
     * @param playerNumber the number of players in this game
     */
    public SchoolController( GameController gmController, int playerNumber ){
        this.playerNumber = playerNumber;
        this.gmController = gmController;
        this.professorsMap = new ArrayList<>(Arrays.asList(-1,-1,-1,-1,-1));

        //Automatically initializes 2 or 3 school, depending on playerNumber
        if(playerNumber == 2) {
            for(Player p: gmController.getPlController().getPlayers()) {
                ArrayList<Tower> towers = new ArrayList<>();
                for(int i=0; i<8; i++) {
                    towers.add(new Tower(p.getSchoolColour()));
                }

                schoolModels.add(new School(towers, p.getPlayerId()));
            }
        }
        else if (playerNumber == 3) {
            for(Player p: gmController.getPlController().getPlayers()) {
                ArrayList<Tower> towers = new ArrayList<>();
                for(int i=0; i<6; i++) {
                    towers.add(new Tower(p.getSchoolColour()));
                }

                schoolModels.add(new School(towers, p.getPlayerId()));
            }
        }
    }

    /**
     * Initializes the school halls with N random students from the bag.
     * @return a map of useful infos about the schools' halls
     */
    public HashMap<Integer, Object> initializeHalls() {
        HashMap<Integer, Object> data = new HashMap<>();

        for(School s: schoolModels) {
            ArrayList<Student> drawnStudents;
            if(playerNumber == 2) {
                drawnStudents = gmController.getBagController().drawStudentsByQuantity(7);
            } else {
                drawnStudents = gmController.getBagController().drawStudentsByQuantity(9);
            }
            data.put(s.getSchoolId(), moveStudentsToHall(drawnStudents, s.getSchoolId()));
        }

        return data;
    }

    /**
     * @param schoolId the id of the school
     * @return a map of useful infos about the school
     */
    public HashMap<String, Object> getSchoolInfo(int schoolId) {
        HashMap<String, Object> data = new HashMap<>();
        //find the school by Id
        School s = schoolModels.stream().filter(x -> x.getSchoolId() == schoolId).findAny().orElse(null);
        if(s!=null) {
            data.put("students_hall", Utils.getNumStudentsByRace(s.getStudentsInHall()));
            data.put("tables", getNumStudentsInTables(schoolId));
            data.put("prof_schoolIds", this.professorsMap);
            data.put("towers", this.getTowerQuantity(schoolId));
        }
        return data;
    }

    /**
     * Moves some students to the school hall
     * @param students the list of student to move
     * @param schoolId the id of the school
     * @return a map of useful info about the students moved to the school
     */
    public HashMap<String,Object> moveStudentsToHall(ArrayList<Student> students, int schoolId){
        schoolModels.get(getSchoolIndex(schoolId)).addStudentsToHall(students);

        HashMap<String,Object> data = new HashMap<>();
        //SchoolId
        data.put("schoolID", schoolId);
        //Array of integer with students quantity, parallel to race one. Tells how many students and which students have been added to the hall.
        data.put("students_added", Utils.getNumStudentsByRace(students));
        //Array of integer with students quantity, parallel to race one. Tells how many students and which students are in the hall.
        data.put("students_hall", Utils.getNumStudentsByRace(schoolModels.get(getSchoolIndex(schoolId)).getStudentsInHall()));
        return data;
    }

    /**
     * Moves a student from the hall to one of the school table
     * @param studentIndex the index of the student to move from the hall
     * @param schoolId the id of the school
     * @return a map of useful info about the students moved to the school, and the school tables
     */
    public HashMap<String,Object> moveStudentToTable(int studentIndex, int schoolId) {
        Student std = schoolModels.get(getSchoolIndex(schoolId)).removeStudentFromHallByIndex(studentIndex);
        schoolModels.get(getSchoolIndex(schoolId)).moveStudentToTable(std);

        HashMap<String,Object> data = new HashMap<>();
        //Moved student race
        data.put("stud_race", std.getRace().getColour());
        //Array of integer with table sizes, parallel to race one. Tells how many students has the school in each table.
        data.put("tables", getNumStudentsInTables(schoolId));

        if(!gmController.isSimpleMode()) {
            int numStudents = getNumStudentsInTables(schoolId).get(Utils.getRaceString().indexOf(std.getRace().name()));
            if(numStudents % 3 == 0) {
                gmController.getPlController().addCoin(schoolId);
            }
            //New amount of coins
            data.put("coins", gmController.getPlController().getCoins(schoolId));
        }

        return data;
    }

    /**
     * Moves a student from the hall to an island
     * @param studentIndex the index of the student to move from the hall
     * @param islandIndex the index of the island
     * @param schoolId the id of the school
     * @return a map of useful info about the student moved and the archipelago
     */
    public HashMap<String,Object> moveStudentToIsland(int studentIndex, int islandIndex, int schoolId) {
        Student std = schoolModels.get(getSchoolIndex(schoolId)).removeStudentFromHallByIndex(studentIndex);
        gmController.getArchipelagoController().addStudentToIsland(std, islandIndex);

        HashMap<String,Object> data = new HashMap<>();
        //Moved student race
        data.put("stud_race", std.getRace().getColour());
        //Array of integer with table sizes, parallel to race one. Tells how many students has the island in each table.
        data.put("archipelago_view", gmController.getArchipelagoController().getArchipelago().getArchipelagoInfo());
        return data;
    }

    /**
     * Removes a specific amount of students from a table
     * @param quantity the amount of students to remove
     * @param race the race of the table
     * @param schoolId the id of the school
     * @return the list of students removed
     */
    public ArrayList<Student> removeStudentsFromTable(int quantity, Race race, int schoolId) {
        for(School s: schoolModels) {
            if(s.getSchoolId() == schoolId) {
                return s.removeStudentsFromTable(quantity, race);
            }
        }

        return null;
    }

    /**
     * Exchanges some students from the hall and the tables. (Effect 10)
     * @param hall the list of indexes of the selected students
     * @param tables the list of table races
     * @param schoolId the id of the school
     * @return the updated amount of coins after the exchange
     */
    public int exchangeHallTable(ArrayList<Integer> hall, ArrayList<Race> tables, int schoolId) {
        for(School s: schoolModels) {
            if(s.getSchoolId() == schoolId) {
                for(int i=0;  i < hall.size(); i++) {
                    s.exchangeStudents(hall.get(i), tables.get(i));

                    int numStudents = getNumStudentsInTables(schoolId).get(Utils.getRaceString().indexOf(tables.get(i).name()));
                    if(numStudents % 3 == 0) {
                        gmController.getPlController().addCoin(schoolId);
                    }

                }
                s.orderHall();
            }
        }

        //New amount of coins
        return gmController.getPlController().getCoins(schoolId);
    }

    /**
     * Adds towers to a school
     * @param schoolId the id of the school
     * @param towers the list of towers to add
     */
    public void addTowers(Integer schoolId, ArrayList<Tower> towers) {
        for(School s: schoolModels) {
            if(s.getSchoolId() == schoolId) {
                try {
                    s.addTowers(towers);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Subs a specific amount of towers from a school
     * @param schoolId the id of the school
     * @param quantity the amount of towers to sub
     * @return the list of subbed towers
     */
    public ArrayList<Tower> subTowers(Integer schoolId, int quantity) {
        ArrayList<Tower> tmpTowers = new ArrayList<>();

        for(School s: schoolModels) {
            if(s.getSchoolId() == schoolId) {
                try {
                    tmpTowers.addAll(s.subTowers(quantity));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return tmpTowers;
    }

    /**
     * Calculates the positions of the professors depending on all the schools and their tables
     * @return a map of useful infos about the professors' locations
     */
    public HashMap<String,Object> setProfessors() {
        HashMap<String,Object> data = new HashMap<>();

        int effect2Counter = 0;
        if(!gmController.isSimpleMode()) {
            for(Integer pId: gmController.getPlayerIdTurnOrder()) {
                if((int) gmController.getPlayedCharacterByPlayerId(pId).get("effect") == 2) {
                    effect2Counter++;
                }
            }
        }

        for(Race r: Utils.raceArray) {
            int maxId = schoolModels.get(0).getSchoolId();
            int maxStudent = schoolModels.get(0).getStudentsInTable(r).size();

            for(int i=1; i < schoolModels.size(); i++) {
                if(schoolModels.get(i).getStudentsInTable(r).size() == maxStudent) {
                    //Effect 2 check. If more than 1 player played it, effect is ignored.
                    if(!gmController.isSimpleMode() && effect2Counter == 1 &&
                        (int)gmController.getPlayedCharacterByPlayerId(schoolModels.get(i).getSchoolId()).get("effect") == 2) {
                            maxId = schoolModels.get(i).getSchoolId();
                    }
                    if(maxStudent==0) {
                        maxId = -1;
                    } else {
                        maxId = this.professorsMap.get(Utils.raceArray.indexOf(r));
                    }
                }
                if(schoolModels.get(i).getStudentsInTable(r).size() > maxStudent) {
                    maxId = schoolModels.get(i).getSchoolId();
                    maxStudent = schoolModels.get(i).getStudentsInTable(r).size();
                }
            }
            this.professorsMap.set(Utils.raceArray.indexOf(r), maxId);

            for(School s: schoolModels) {
                if(maxId == -1) {
                    s.setProfessor(r, false);
                } else {
                    if(s.getSchoolId() == maxId) {
                        s.setProfessor(r, true);
                    } else {
                        s.setProfessor(r, false);
                    }
                }
            }
        }

        //Array of school ids, parallel to race one. Associates the professor race to his school id. -1: no one has that professor.
        data.put("prof_schoolIds", this.professorsMap);

        return data;
    }

    /**
     * @return the schools' instances
     */
    public ArrayList<School> getSchools() {
        return schoolModels;
    }

    /**
     * @return the list of player indexes owning the professors of each race
     */
    public ArrayList<Integer> getProfessorsMap() {
        return professorsMap;
    }

    /**
     * @param colour the school colour
     * @return the id of the school
     */
    public int getSchoolIdByColour(Colour colour) {
        for(School s: schoolModels) {
            if(s.getTowerColour() == colour) {
                return s.getSchoolId();
            }
        }
        return -1;
    }

    /**
     * @param schoolId the id of the school
     * @return the school colour
     */
    public Colour getSchoolColour(int schoolId) {
        for(School s: schoolModels) {
            if(s.getSchoolId() == schoolId) {
                return s.getTowerColour();
            }
        }
        return null;
    }

    /**
     * @param schoolId the id of the school
     * @return the school instance
     */
    public School getSchoolByIndex(int schoolId) {
        for (School s: schoolModels) {
            if(s.getSchoolId() == schoolId) {
                return s;
            }
        }
        return null;
    }

    /**
     * @param schoolId the id of the school
     * @return the school index relative to the schools list
     */
    public int getSchoolIndex(int schoolId) {
        for(School s: schoolModels) {
            if(s.getSchoolId() == schoolId) {
                return schoolModels.indexOf(s);
            }
        }
        return -1;
    }

    /**
     * @param schoolId the id of the school
     * @return the amount of tower inside the school
     */
    public int getTowerQuantity(int schoolId) {
        for(School s: schoolModels) {
            if(s.getSchoolId() == schoolId) {
                return s.getTowers().size();
            }
        }
        return -1;
    }

    /**
     * @param schoolId the id of the school
     * @return the amount of students inside the school hall
     */
    public int getNumStudentsInHall(int schoolId) {
        for(School s: schoolModels) {
            if(s.getSchoolId() == schoolId) {
                return s.getStudentsInHall().size();
            }
        }
        return -1;
    }

    /**
     * @param schoolId the id of the school
     * @return the amount of towers inside all the tables
     */
    public ArrayList<Integer> getNumStudentsInTables(int schoolId) {
        for(School s: schoolModels) {
            if(s.getSchoolId() == schoolId) {
                return s.getTablesSizes();
            }
        }
        return null;
    }
}
