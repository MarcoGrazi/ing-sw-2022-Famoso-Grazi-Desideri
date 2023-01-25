package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.*;
import main.java.com.polimi.app.utils.Utils;

import java.io.Serializable;
import java.util.*;

/**
 * Archipelago Controller class.
 * @author Group 53
 */
public class ArchipelagoController implements Serializable {
    //Instance of the game controller
    private final GameController gmController;
    //Instance of the archipelago model
    private final Archipelago archModel = new Archipelago();

    /**
     * Class constructor.
     * Initializes the GameController.
     * @param gmController
     */
    public ArchipelagoController(GameController gmController) {
        this.gmController = gmController;
    }

    /**
     * Set up the archipelago by initializing 2 students for each race, shuffling them and placing each one on the correct islands.
     * @return and hash map of useful infos about the whole archipelago. Used to update view.
     */
    public HashMap<Integer, Object> initializeArchipelago() {

        //Initializes 10 students, 2 for each race
        ArrayList<Student> students = new ArrayList<>();
        for(Race r: Utils.raceArray) {
            for(int i=0; i<2; i++) {
                students.add(new Student(r));
            }
        }
        Collections.shuffle(students);

        int positionMN = this.archModel.getMNposition();
        int positionOpposed = positionMN < 6 ? positionMN + 6 : ( 6 + positionMN - 12  );

        for(int i=0; i<12; i++) {
            if(i != positionMN && i != positionOpposed) {
                this.archModel.addStudentToIsland(students.remove(students.size()-1), i);
            }
        }

        return this.archModel.getArchipelagoInfo();
    }

    /**
     * Calls the moveMn() function of the model.
     * @param moves number of moves
     * @return Mother nature new position
     */
    public int moveMn(int moves) {
        this.archModel.moveMN(moves);
        return this.archModel.getMNposition();
    }

    /**
     * Manages whole action phase and influence logic related to the group received as param.
     * Firstly, checks whether the group is prohibited.
     * If the group is prohibited, removes a prohibition from the group and returns the prohibition to the character card.
     * If the group is not prohibited, retrieves the island group and calculates the influence.
     * If no player has enough influence or if the most influential player already owns the group , the game keeps on.
     * If a player has enough influence and the island is empty, the player places a tower on the island.
     * If there is a takeover, the most influential player swaps their towers with the towers on the island.
     * @param islandGroupIndex the group index of the island involved in the influence calc
     * @return and hash map of useful infos about the whole archipelago. Used to update view.
     */
    public HashMap<String,Object> archipelagoActionPhase(int islandGroupIndex) {
        HashMap<String,Object> data = new HashMap<>();

        //Retrieves island group
        ArrayList<Island> islandGroup = this.archModel.getGroupByIslandIndex(islandGroupIndex);
        //Retrieves island colour (if present) as a string
        String islandGroupColourString = this.archModel.getTowersColourString(islandGroupIndex);
        //Checks if group is prohibited
        boolean isProhibited = this.archModel.isIslandProhibited(islandGroupIndex);
        if(!isProhibited) {
            //Retrieves most influential player colour on the island
            String mostInfluentialPlayerColourString = islandInfluenceCalc(islandGroupIndex, islandGroupColourString);

            if(!mostInfluentialPlayerColourString.equals("")) {
                Colour mostInfluentialPlayerColour = Utils.getColourFromString(mostInfluentialPlayerColourString);
                int mostInfluentialPlayerId = this.gmController.getSchoolController().getSchoolIdByColour(mostInfluentialPlayerColour);

                if(islandGroupColourString.equals("")) {
                    ArrayList<Tower> newTowers = this.gmController.getSchoolController().subTowers(mostInfluentialPlayerId, 1);
                    this.archModel.takeoverTowers(islandGroupIndex, newTowers);

                    //Check if most influential player moved all their towers and sets them as winner
                    if(this.gmController.getSchoolController().getTowerQuantity(mostInfluentialPlayerId) == 0) {
                        this.gmController.setWinnerId(mostInfluentialPlayerId);
                    }

                    data.put("message", mostInfluentialPlayerColour.name() + " took the island");
                    data.put("tower_quantity", 1);
                    data.put("old_colour", "");
                    data.put("old_playerId", -1);
                    data.put("new_colour", mostInfluentialPlayerColour.name());
                    data.put("new_playerId", gmController.getSchoolController().getSchoolIdByColour(mostInfluentialPlayerColour));

                    //Merges island after simple take
                    this.archModel.mergeIslands();
                } else {
                    Colour islandGroupColour = Utils.getColourFromString(islandGroupColourString);
                    int quantity = islandGroup.size();

                    if (mostInfluentialPlayerColourString.equals(islandGroupColourString)) {
                        data.put("message", "The most influential player already controls this island");
                        data.put("tower_quantity", quantity);
                        data.put("old_colour", islandGroupColour.name());
                        data.put("old_playerId", gmController.getSchoolController().getSchoolIdByColour(islandGroupColour));
                        data.put("new_colour", islandGroupColour.name());
                        data.put("new_playerId", gmController.getSchoolController().getSchoolIdByColour(islandGroupColour));
                    } else {
                        int amountOfTowers = this.gmController.getSchoolController().getTowerQuantity(mostInfluentialPlayerId);

                        ArrayList<Tower> newTowers;
                        ArrayList<Tower> oldTowers;

                        if(quantity > amountOfTowers) {
                            this.gmController.endImmediately();

                            newTowers = this.gmController.getSchoolController().subTowers(mostInfluentialPlayerId, amountOfTowers);
                            for(int i=amountOfTowers; i<quantity; i++) {
                                newTowers.add(new Tower(mostInfluentialPlayerColour));
                            }
                            oldTowers = this.archModel.takeoverTowers(islandGroupIndex, newTowers);
                        } else {
                            newTowers = this.gmController.getSchoolController().subTowers(mostInfluentialPlayerId, quantity);
                            oldTowers = this.archModel.takeoverTowers(islandGroupIndex, newTowers);
                        }

                        int schoolId = this.gmController.getSchoolController().getSchoolIdByColour(islandGroupColour);
                        this.gmController.getSchoolController().addTowers(schoolId, oldTowers);

                        //Check if most influential player moved all their towers and sets them as winner
                        if(this.gmController.getSchoolController().getTowerQuantity(mostInfluentialPlayerId) == 0) {
                            this.gmController.setWinnerId(mostInfluentialPlayerId);
                        }

                        data.put("message", mostInfluentialPlayerColour.name() + " took over " + islandGroupColour.name());
                        data.put("tower_quantity", quantity);
                        data.put("old_colour", islandGroupColour.name());
                        data.put("old_playerId", gmController.getSchoolController().getSchoolIdByColour(islandGroupColour));
                        data.put("new_colour", mostInfluentialPlayerColour.name());
                        data.put("new_playerId", gmController.getSchoolController().getSchoolIdByColour(mostInfluentialPlayerColour));

                        //Merges island after takeover
                        this.archModel.mergeIslands();
                    }
                }
            } else {
                if(!islandGroupColourString.equals("")) {
                    Colour islandGroupColour = Utils.getColourFromString(islandGroupColourString);
                    data.put("message", "Nobody has enough influence to takeover the island");
                    data.put("tower_quantity", islandGroup.size());
                    data.put("old_colour", islandGroupColour.name());
                    data.put("old_playerId", gmController.getSchoolController().getSchoolIdByColour(islandGroupColour));
                    data.put("new_colour", islandGroupColour.name());
                    data.put("new_playerId", gmController.getSchoolController().getSchoolIdByColour(islandGroupColour));
                } else {
                    data.put("message", "Nobody has enough influence to take the island");
                    data.put("tower_quantity", 0);
                    data.put("old_colour", "");
                    data.put("old_playerId", -1);
                    data.put("new_colour", "");
                    data.put("new_playerId", -1);
                }
            }
        } else {
            data.put("message", "Mother Nature ended her move on a prohibited island. Nothing happens.");
            data.put("prohibited_before", true);
            archModel.subGroupProhibition(islandGroupIndex);
            //Returns the prohibition to the character
            gmController.getActiveCharactersController().effect5AddProhibition();
            data.put("prohibited_after", this.archModel.isIslandProhibited(islandGroupIndex));

            if(!islandGroupColourString.equals("")) {
                Colour islandGroupColour = Utils.getColourFromString(islandGroupColourString);
                data.put("tower_quantity", islandGroup.size());
                data.put("old_colour", islandGroupColour.name());
                data.put("old_playerId", gmController.getSchoolController().getSchoolIdByColour(islandGroupColour));
                data.put("new_colour", islandGroupColour.name());
                data.put("new_playerId", gmController.getSchoolController().getSchoolIdByColour(islandGroupColour));
            } else {
                data.put("tower_quantity", 0);
                data.put("old_colour", "");
                data.put("old_playerId", -1);
                data.put("new_colour", "");
                data.put("new_playerId", -1);
            }
        }

        //If the number of group islands inside the archipelago is 3 or there is already a winner...
        if(this.archModel.getCurrentGroupsIndexMap().keySet().size() <= 3 || this.gmController.getWinnerId() != -1) {
            //If the game ends because there are three islands, the winner has to be calculated.
            if(this.gmController.getWinnerId() == -1) {
                this.gmController.calcVictory();
            }
            //Sets the game to end immediately
            this.gmController.endImmediately();
        }

        //Puts all archipelago info inside data
        data.put("archipelago_view", this.archModel.getArchipelagoInfo());
        return data;
    }

    /**
     * Calculates the influence on a specific island group.
     * @param groupIndex the index of the island group
     * @param currentTowersColourString the current colour of towers on the island group
     * @return the most influential player colour as a string
     */
    public String islandInfluenceCalc(int groupIndex, String currentTowersColourString) {
        ArrayList<Integer> playerIds = this.gmController.getPlController().getPlayerIds();

        //Sets the effect6 flag to true: towers do not count for the influence check
        boolean effect6 = false;
        if(!gmController.isSimpleMode()) {
            for(int id : playerIds){
                if((int)gmController.getPlayedCharacterByPlayerId(id).get("effect") == 6){
                    effect6 = true;
                }
            }
        }

        //Keeps track of the disabled races
        boolean[] disabledRaces = {false, false, false, false, false};
        if(!gmController.isSimpleMode()) {
            for(int id : playerIds){
                if((int)gmController.getPlayedCharacterByPlayerId(id).get("effect") == 9){
                    String chosenRace = gmController.getPlayedCharacterByPlayerId(id).get("info").toString();
                    disabledRaces[Utils.getRaceString().indexOf(chosenRace)] = true;
                }
            }
        }

        //PlayersStudentsCounter is a map between playerId and influence counter of each player.
        HashMap<Integer,Integer> playersStudentsCounter = new HashMap<>();
        for(Integer pId: playerIds) {
            playersStudentsCounter.put(pId, 0);
        }

        //Updates playerStudentsCounter with student influence
        ArrayList<Integer> professorsMap = this.gmController.getSchoolController().getProfessorsMap();
        int i=0;
        for(Integer pId: professorsMap) {
            if(pId != -1 && !disabledRaces[i]) {
                //Returns students number of the "i-th" race. Which is parallel to Utils.raceArray
                int studentCounter = this.archModel.getStudentCounterGroupList(groupIndex).get(i);
                //Adds the students number to the parallel array of counter, basing on the student id
                playersStudentsCounter.replace(pId, playersStudentsCounter.get(pId) + studentCounter);
            }
            i++;
        }

        //Add influence basing on towers (if present) and effects
        if(!currentTowersColourString.equals("")) {
            Colour currentTowersColour = Utils.getColourFromString(currentTowersColourString);

            int islandTowersNumber = this.archModel.getGroupByIslandIndex(groupIndex).size();
            for (Integer pId : playerIds) {
                //If player owns the island and effect6 is not activated, its influence gains as many points as the towers number
                if (currentTowersColour == this.gmController.getPlController().getSchoolColourById(pId) && !effect6) {
                    playersStudentsCounter.replace(pId, playersStudentsCounter.get(pId) + islandTowersNumber);
                }
                //If player played effect8, its influence gains 2 points
                if(!gmController.isSimpleMode() && (int)gmController.getPlayedCharacterByPlayerId(pId).get("effect") == 8){
                    playersStudentsCounter.replace(pId, playersStudentsCounter.get(pId) + 2);
                }
            }
        }


        //Returns colour of most influential player
        boolean isEven = false;
        int maxId = playerIds.get(0);
        for(int j=1; j < this.gmController.getPlayerNumber(); j++) {
            if(playersStudentsCounter.get(playerIds.get(j)).equals(playersStudentsCounter.get(maxId))) {
                isEven = true;
            } else if(playersStudentsCounter.get(playerIds.get(j)) > playersStudentsCounter.get(maxId)) {
                maxId = playerIds.get(j);
                isEven = false;
            }
        }

        //Returns "" if influence count is even. Otherwise, returns the most influential player colour (as a string)
        if(isEven) {
            return "";
        } else {
            return this.gmController.getPlController().getSchoolColourById(maxId).name();
        }
    }

    /**
     * Adds a student to a specific island
     * @param student the student to add
     * @param realIndex the index of the island
     * @return a map of useful info about the island group to which the student was added
     */
    public HashMap<String, Object> addStudentToIsland(Student student, Integer realIndex){
        this.archModel.addStudentToIsland(student, realIndex);
        return this.archModel.getIslandGroupInfo(realIndex);
    }

    /**
     * Adds a prohibition on an island or an island group
     * @param groupIndex the index of the island group
     * @return a map of useful info about the island group to which the prohibition was added
     */
    public HashMap<String,Object> addProhibition(Integer groupIndex) {
        this.archModel.addGroupProhibition(groupIndex);
        return this.archModel.getIslandGroupInfo(groupIndex);
    }

    /**
     * @return the indexes of the current existing groups
     */
    public Set<Integer> getGroupsIndexes() {
        return this.archModel.getCurrentGroupsIndexMap().keySet();
    }

    //TODO: remove
    public int getNumberOfIslands() {
        return archModel.getCurrentGroupsIndexMap().size();
    }

    /**
     * @return the archipelago instance
     */
    public Archipelago getArchipelago() {
        return this.archModel;
    }
}
