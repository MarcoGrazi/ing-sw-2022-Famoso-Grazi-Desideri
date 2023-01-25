package main.java.com.polimi.client.models;

import com.google.gson.internal.LinkedTreeMap;
import jdk.jshell.execution.Util;
import main.java.com.polimi.client.models.Colour;
import main.java.com.polimi.client.models.Race;
import main.java.com.polimi.client.models.Student;
import main.java.com.polimi.client.models.Tower;
import main.java.com.polimi.client.models.*;
import main.java.com.polimi.client.utils.Utils;

import java.util.*;
/**
 * Archipelago class.
 * Represented by an array of 12 islands, an ArrayList parallel to it which stores the group index of each island,
 * an HashMap which keeps track of the actually existing island groups and their prohibition counters
 * and a variable which stores the current position of Mother Nature inside the archipelago.
 * @author Group 53
 */
public class Archipelago extends Observable {
    //Stores the IslandsGroupIndex on which MotherNature is located
    private Integer whereIsMN;
    //Array of the real Island objects
    private final Island[] islands;
    //Array, parallel to the Island one, that stores the GroupIndex of each Island
    private final ArrayList<Integer> islandsGroupIndexList;
    //Maps each existing IslandsGroupIndex to an integer that is 0 or 1  that stands for whether the IslandsGroup is prohibited or not
    private final LinkedHashMap<Integer,Integer> currentGroupsIndexMap;

    //CONSTRUCTOR
    public Archipelago(LinkedTreeMap<String, Object> archipelagoHash, Observer o) {
        this.islandsGroupIndexList = new ArrayList<>();
        this.currentGroupsIndexMap = new LinkedHashMap<>();
        this.islands = new Island[12];

        for (String key: archipelagoHash.keySet()){
            int keyInt =(int) Math.round(Float.parseFloat(key));
            LinkedTreeMap<String,Object> islandMap= (LinkedTreeMap<String, Object>) archipelagoHash.get(key);
            if ((Boolean)islandMap.get("mother_nature")) {
                this.whereIsMN= keyInt;
            }
            this.islands[keyInt] = new Island(keyInt);
            this.islandsGroupIndexList.add(keyInt);
            this.currentGroupsIndexMap.put(keyInt, 0);
            LinkedTreeMap<String, Object> islandsInfo= (LinkedTreeMap<String, Object>) islandMap.get("islands_info");

            ArrayList<Double> stuInGroup=new ArrayList<>(Arrays.asList(0.0,0.0,0.0,0.0,0.0));

            for(String islandKey : islandsInfo.keySet()){
                ArrayList<Double> stuInIsland= (ArrayList<Double>) islandsInfo.get(islandKey);
                for(int i=0; i<Utils.raceArray.size(); i++){

                    stuInGroup.set(i,stuInGroup.get(i)+stuInIsland.get(i));
                }
            }

            for (int i =0; i< stuInGroup.size(); i++){
                for (int j=0 ; j<stuInGroup.get(i); j++){
                    this.islands[keyInt].addStudent(new Student(Utils.raceArray.get(i)));
                }
            }
        }

        addObserver(o);
        setChanged();
        notifyObservers(new Message("SETUP_ARCH"));
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

        if(this.currentGroupsIndexMap.get(groupIndex) > 0) {
            return true;
        }
        return false;
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
     * Counts how many students of each race are present on a group
     * @param realIndex the index of the island group
     * @return the array of counter
     */
    //TODO: remove
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
    public Colour getTowersColour(Integer realIndex) {
        return islands[realIndex].getTower().getColour();
    }

    public String getTowersColourString(Integer realIndex) {
        if(islands[realIndex].getTower() != null) {
            return islands[realIndex].getTower().getColour().name();
        } else {
            return "";
        }
    }

    /**
     * Updates a specific group
     * @param islandsInfo the new group info
     * @param towerColour the new tower colour
     * @param motherNature true if mother nature is here, false otherwise
     * @param prohibitionCounter the number of prohibitions
     * @param groupIndex the index of the group to update
     */
    public void updateGroup( LinkedTreeMap<String, Object> islandsInfo, String towerColour,boolean motherNature, int prohibitionCounter, int groupIndex ) {
        for(String islandKey : islandsInfo.keySet()){
            ArrayList<Double> numStuByRace  = (ArrayList<Double>) islandsInfo.get(islandKey);
            ArrayList<Student> stuInIsland = new ArrayList<>();
            for( int i =0; i<Utils.raceArray.size(); i++){
                for(int j =0; j<numStuByRace.get(i).intValue(); j++ ){
                    stuInIsland.add(new Student(Utils.raceArray.get(i)));
                }
            }
            int islandIndex = (int) Math.round(Double.parseDouble(islandKey));
            islands[islandIndex].setStudentList(stuInIsland);
            if(towerColour.equals("BLACK") || towerColour.equals("WHITE") || towerColour.equals("GREY")){
                islands[islandIndex].setTower(new Tower(Utils.getColourFromString(towerColour)));
            }
            if(motherNature){
                this.whereIsMN=groupIndex;
            }

            this.islandsGroupIndexList.set(islandIndex,groupIndex);

        }
    }

    /**
     * @param archipelagoInfo the new archipelago infos
     */
    public void updateArchipelago(LinkedTreeMap<String, Object> archipelagoInfo){
        LinkedHashMap<Integer,Integer> currentGroupsIndexMapNew = new LinkedHashMap<>();
        for(String archKey : archipelagoInfo.keySet()) {
            LinkedTreeMap<String, Object> groupInfo = (LinkedTreeMap<String, Object>) archipelagoInfo.get(archKey);
            String towerColour = (String) groupInfo.get("tower_colour");
            int prohibitionCount= (int) Math.round(Double.parseDouble(groupInfo.get("prohibition_quantity").toString()));
            boolean MotherNature = (boolean) groupInfo.get("mother_nature");
            LinkedTreeMap<String, Object> islandsInfo = (LinkedTreeMap<String, Object>) groupInfo.get("islands_info");
            int groupIndex = (int)Math.round(Float.parseFloat(archKey));
            updateGroup(islandsInfo,towerColour,MotherNature,prohibitionCount, groupIndex);
            currentGroupsIndexMapNew.put(groupIndex,prohibitionCount);

        }
        this.currentGroupsIndexMap.clear();
        this.currentGroupsIndexMap.putAll(currentGroupsIndexMapNew);

        setChanged();
        notifyObservers(new Message("UPDATE_ARCH"));
    }
}
