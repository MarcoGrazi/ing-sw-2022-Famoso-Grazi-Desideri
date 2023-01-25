package main.java.com.polimi.app.models;

import main.java.com.polimi.app.utils.Utils;

import java.io.Serializable;
import java.util.*;
import java.util.Random;

/**
 * Archipelago class.
 * Represented by an array of 12 islands, an ArrayList parallel to it which stores the group index of each island,
 * an HashMap which keeps track of the actually existing island groups and their prohibition counters
 * and a variable which stores the current position of Mother Nature inside the archipelago.
 * @author Group 53
 */
public class Archipelago implements Serializable {
    //Stores the IslandsGroupIndex on which MotherNature is located
    private Integer whereIsMN;
    //Array of Island objects
    private final Island[] islands;
    //Array, parallel to the Island one, that stores the GroupIndex of each Island
    private final ArrayList<Integer> islandsGroupIndexList;
    //Maps each existing IslandsGroupIndex to a boolean that stands for whether the IslandsGroup is prohibited or not
    private final LinkedHashMap<Integer,Integer> currentGroupsIndexMap;

    /**
     * Class constructor.
     * Initializes Islands array with 12 empty islands.
     * Initializes IslandsGroupIndexList with the initial islands' indexes, from 0 to 11.
     * Initializes CurrentGroupsIndexMap with the initial islands' indexes as keys, from 0 to 11, and FALSE as value.
     */
    public Archipelago() {
        this.whereIsMN = randomizeMNstart();
        this.islandsGroupIndexList = new ArrayList<>();
        this.currentGroupsIndexMap = new LinkedHashMap<>();

        //Initializes the Island array with 12 islands
        //Initializes the IslandsGroupIndexList with indexes from 0 to 11
        //Initializes the CurrentGroupsIndexMap with indexes from 0 to 11 and sets prohibition to FALSE
        this.islands = new Island[12];
        for(int i=0; i<12; i++) {
            this.islands[i] = new Island(i);
            this.islandsGroupIndexList.add(i);
            this.currentGroupsIndexMap.put(i, 0);
        }
    }

    /**
     * Class constructor used to initialize a custom archipelago for tests.
     * Initializes Islands array with 12 empty islands.
     * @param whereIsMN the current position of Mother Nature
     * @param islandsGroupIndexList the list of group indexes parallel to the islands array
     * @param currentGroupsIndexMap the map between existing group indexes and their prohibition counters
     */
    public Archipelago ( Integer whereIsMN,
                         ArrayList<Integer> islandsGroupIndexList,
                         LinkedHashMap<Integer,Integer> currentGroupsIndexMap) {
        this.whereIsMN = whereIsMN;
        this.islandsGroupIndexList = islandsGroupIndexList;
        this.currentGroupsIndexMap = currentGroupsIndexMap;

        this.islands = new Island[12];
        for(int i=0; i<12; i++) {
            this.islands[i] = new Island(i);
        }
    }

    /**
     * @return the current position of Mother Nature
     */
    public Integer getMNposition() {
        return this.whereIsMN;
    }

    /**
     * @return islands array
     */
    public Island[] getIslands() {
        return this.islands;
    }

    /**
     * @return the list of group indexes parallel to the islands array
     */
    public ArrayList<Integer> getIslandsGroupIndexList() {
        return this.islandsGroupIndexList;
    }

    /**
     * @return the HashMap between existing group indexes and their prohibition counters
     */
    public LinkedHashMap<Integer, Integer> getCurrentGroupsIndexMap() {
        return this.currentGroupsIndexMap;
    }

    /**
     * @param realIndex the island real index (0 to 11)
     * @return the number of prohibitions placed on the group in which the chosen island is located
     */
    public int getProhibitionCounterByIslandIndex(Integer realIndex) {
        int groupIndex = this.islandsGroupIndexList.get(realIndex);

        return this.currentGroupsIndexMap.get(groupIndex);
    }

    /**
     * @param realIndex the island real index (0 to 11)
     * @return the group of islands in which the chosen island is located
     */
    public ArrayList<Island> getGroupByIslandIndex(Integer realIndex) {
        int groupIndex = this.islandsGroupIndexList.get(realIndex);
        int j = groupIndex;

        ArrayList<Island> tmpIslands = new ArrayList<>();
        do {
            tmpIslands.add(this.islands[j]);
            j = (j == 11) ? 0 : j + 1;
        } while (this.islandsGroupIndexList.get(j).equals(groupIndex));

        return tmpIslands;
    }

    /**
     * @param realIndex the island real index (0 to 11)
     * @return true if the selected group is prohibited, false otherwise
     */
    public boolean isIslandProhibited(Integer realIndex) {
        int groupIndex = this.islandsGroupIndexList.get(realIndex);

        return this.currentGroupsIndexMap.get(groupIndex) > 0;
    }

    /**
     * Adds a single student to the selected island.
     * @param student a single student
     * @param realIndex the island real index (0 to 11)
     */
    public void addStudentToIsland(Student student, Integer realIndex){
        this.islands[realIndex].addStudent(student);
    }

    /**
     * Adds a prohibition to the selected island group.
     * @param groupIndex the index of the chosen island group
     */
    public void addGroupProhibition(Integer groupIndex) {
        this.currentGroupsIndexMap.replace(groupIndex, this.currentGroupsIndexMap.get(groupIndex) + 1 );
    }

    /**
     * Removes a prohibition from the selected island group.
     * @param groupIndex the index of the chosen island group
     */
    public void subGroupProhibition(Integer groupIndex) {
        this.currentGroupsIndexMap.replace(groupIndex, this.currentGroupsIndexMap.get(groupIndex) - 1 );
    }

    /**
     * @param realIndex the island real index (0 to 11)
     * @return a list of numbers that represent the amount of students on the selected island, for each race
     */
    public ArrayList<Integer> getIslandStudentsInfo(Integer realIndex) {
        return Utils.getNumStudentsByRace(islands[realIndex].getStudentList());
    }

    /**
     * Utilized to update the view.
     * @param realIndex the island real index (0 to 11)
     * @return an HashMap of useful information about the island group in which the selected island is located
     */
    public HashMap<String,Object> getIslandGroupInfo(Integer realIndex) {
        int groupIndex = this.islandsGroupIndexList.get(realIndex);
        HashMap<String,Object> data = new HashMap<>();


        //Group index
        data.put("group_index", groupIndex);
        //How many islands are in the group
        data.put("island_quantity", getGroupByIslandIndex(groupIndex).size());
        //Mother Nature is here?
        data.put("mother_nature", this.getMNposition().equals(groupIndex));
        //Prohibited?
        data.put("prohibition_quantity", getProhibitionCounterByIslandIndex(groupIndex));
        //Tower colour of the island
        data.put("tower_colour", getTowersColourString(groupIndex));

        //HashMap of students counter for each island of the group
        HashMap<Integer,Object> islandData = new HashMap<>();
        for(int i=0; i<12; i++) {
            if(islandsGroupIndexList.get(i) == groupIndex) {
                islandData.put(i, getIslandStudentsInfo(i));
            }
        }
        data.put("islands_info", islandData);

        return data;
    }

    /**
     * Utilized to update the view.
     * @return an HashMap of useful information about all the existent island groups
     */
    public HashMap<Integer,Object> getArchipelagoInfo() {
        HashMap<Integer,Object> archData = new HashMap<>();

        for(Integer groupId : getCurrentGroupsIndexMap().keySet()) {
            archData.put(groupId, getIslandGroupInfo(groupId));
        }

        return archData;
    }

    /**
     * Counts how many students of each race are present on a group
     * @param realIndex the index of the island group
     * @return the array of counter
     */
    public ArrayList<Integer> getStudentCounterGroupList(Integer realIndex) {
        ArrayList<Island> islandGroup = getGroupByIslandIndex(realIndex);
        ArrayList<Integer> tmpStudentCounterGroup = new ArrayList<>();

        for(Race r : Utils.raceArray) {
            int raceStudentsCounter = 0;
            for(Island i: islandGroup) {
                raceStudentsCounter += i.getStudentNumByRace(r);
            }
            tmpStudentCounterGroup.add(raceStudentsCounter);
        }

        return tmpStudentCounterGroup;
    }

    /**
     * @param realIndex the island real index (0 to 11)
     * @return the colour of the tower in string, if present on the selected island, otherwise the empty string
     */
    public String getTowersColourString(Integer realIndex) {
        if(islands[realIndex].getTower() != null) {
            return islands[realIndex].getTower().getColour().name();
        } else {
            return "";
        }
    }

    /**
     * Moves mother nature inside the archipelago.
     * After the execution, the new value of MNposition is equal to the island group index on which she landed.
     * MotherNature moves through the island groups, so her movement refers to the indexes stored as keys inside
     * the CurrentGroupsIndexMap.
     * MotherNature returns to the first island group after leaving the last one.
     * @param value the number of moves mother nature has to do
     */
    public void moveMN(int value) {
        //The key-index of the IslandsGroupIndex on which MotherNature is.
        int currentPosition = new ArrayList<>(this.currentGroupsIndexMap.keySet()).indexOf(this.whereIsMN);
        //NewPosition stands for the key-index of the IslandsGroupIndex on which MotherNature will land.
        int newPosition;
        //How many islands are in the Archipelago.
        int islandsCounter = this.currentGroupsIndexMap.size();

        if( currentPosition + value >= islandsCounter ) {
            newPosition = currentPosition + value - islandsCounter;
        }
        else {
            newPosition = currentPosition + value;
        }

        //Retrieve the IslandsGroupIndex value using the key related to the newPosition index
        this.whereIsMN = new ArrayList<>(this.currentGroupsIndexMap.keySet()).get(newPosition);
    }

    /**
     * Substitutes old towers located on the group, with new towers.
     * If there were no towers on the island, just adds the new tower.
     * @param groupIndex the index of the chosen island group
     * @param newTowers the list of towers to substitute
     * @return the list of old towers
     */
    public ArrayList<Tower> takeoverTowers(Integer groupIndex, ArrayList<Tower> newTowers) {
        int i = groupIndex;

        int newTowersSize = newTowers.size();
        ArrayList<Tower> tmpTowerList = new ArrayList<>();
        //IF there is already a tower on the island...
        if(this.islands[i].getTower() != null) {
            //FOR each tower of the newTowers list...
            for(int j=0; j < newTowersSize; j++) {
                //Removes the tower of the i-th island of the group and adds it to the tmp array
                tmpTowerList.add(this.islands[i].removeTower());
                //Sets the tower of the i-th island of the group by passing the last tower of the newTowers list
                this.islands[i].setTower(newTowers.remove(newTowers.size() - 1));

                //IF island[i] is the last island, i = 0. ELSE, i = i + 1
                i = (i == 11) ? 0 : i + 1;
            }
        } //ELSE, just sets the single island tower
        else {
            this.islands[i].setTower(newTowers.remove(newTowers.size() - 1));
        }

        return tmpTowerList;
    }

    /**
     * Scans the whole array and merges the islands together if they are adjacent and share the same tower colour.
     * Updates the islandsGroupIndexList with the new group indexes.
     * Updates the currentGroupsIndexMap removing old group indexes and merging prohibition counters.
     */
    public void mergeIslands() {

        //i index goes from 0 to 11
        for(int i=0; i < 12; i++) {
            //IF islands[i] has a tower...
            if(this.islands[i].getTower() != null) {
                //IF islands[i] is the last island, j = 0. ELSE j = i + 1
                int j = (i != 11) ? i + 1 : 0;

                //IF islands[j] has a tower...
                if(this.islands[j].getTower() != null) {
                    Colour colourI = this.islands[i].getTower().getColour();
                    Colour colourJ = this.islands[j].getTower().getColour();

                    while(colourI == colourJ) {
                        Integer groupI = this.islandsGroupIndexList.get(i);
                        Integer groupJ = this.islandsGroupIndexList.get(j);

                        //IF i and j are from different groups... MERGE
                        if(!groupI.equals(groupJ)) {
                            //Substitute the old groupJ value with new groupI value
                            this.islandsGroupIndexList.set(j, groupI);
                            //Adds possible prohibition of GroupJ to GroupI
                            if(this.currentGroupsIndexMap.keySet().contains(groupJ)) {
                                Integer groupJProhibition = this.currentGroupsIndexMap.get(groupJ);
                                this.currentGroupsIndexMap.replace(groupI, this.currentGroupsIndexMap.get(groupI) + groupJProhibition);
                            }
                            //Removes the groupJ from the CurrentGroupIndexMap
                            this.currentGroupsIndexMap.remove(groupJ);

                            //Updates MN position if present on j-th island
                            if(this.whereIsMN.equals(groupJ)) {
                                this.whereIsMN = groupI;
                            }
                        }

                        //IF islands[j] is the last island, j = 0. ELSE j = j + 1
                        j = (j == 11) ? 0 : j + 1;
                        //IF islands[j] has a tower...
                        if(this.islands[j].getTower() != null) {
                            colourJ = this.islands[j].getTower().getColour();
                        } //ELSE exits while
                        else {
                            break;
                        }
                    }
                    //IF i > j no more merges are needed, the last group includes islands from the head of the list.
                    i = (i < j) ? j-1 : 11;
                }
            }
            //ELSE, increments i and goes on
        }
    }

    /**
     * Randomize MotherNature initial position.
     * @return a random number between 0 and 11 (included)
     */
    private Integer randomizeMNstart() {
        Random random = new Random();
        return random.nextInt(12);
    }
}
