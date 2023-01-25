package main.java.com.polimi.app.controllers;

import main.java.com.polimi.app.models.Character;
import main.java.com.polimi.app.packets.Packet;
import main.java.com.polimi.app.utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 * Active characters controller.
 * Manages the active characters.
 * @author Group 53
 */
public class ActiveCharactersController implements Serializable {
    //Instance of the game controller
    private final GameController gmController;
    //The list of Character instances.
    private final ArrayList<Character> characterModels = new ArrayList<>();

    /**
     * Class constructors. Initializes game controller.
     * @param gmController
     */
    public ActiveCharactersController(GameController gmController){
        this.gmController = gmController;
    }

    /**
     * Initializes 3 characters basing on the random extracted ids.
     * @return a map of useful infos about the characters
     */
    public HashMap<Integer, Object> initializeCharacters(){
        Integer[] ids = randomizeIds();
        for (Integer id : ids) {
            characterModels.add(new Character(id, gmController));
        }

        HashMap<Integer, Object> data = new HashMap<>();
        for(Character c: characterModels) {
            data.put(c.getId(), getCharacterInfoById(c.getId()));
        }
        return data;
    }

    /**
     * Extract 3 random ids from 0 to 12, without repetitions, and initializes correspondent characters
     * @return an array of 3 ids
     */
    private Integer[] randomizeIds(){
        Random randomizer = new Random();
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for(int i=0; i<3; i++){
            int id;
            do {
                //character ids goes from 1 to 12, and nextInt(n) returns a random number
                //between 0 (inclusive) and n exclusive
                id = 1 + randomizer.nextInt(12);
            } while(ids.contains(id));
            ids.add(id);
        }
        return ids.toArray(new Integer[0]);
    }

    /**
     * @param id the id of the character
     * @return a map of useful infos about the character
     */
    public HashMap<String, Object> getCharacterInfoById(int id) {
        HashMap<String, Object> data = new HashMap<>();

        Character c = characterModels.stream().filter(x -> x.getId() == id).findAny().orElse(null);
        if(c!=null) {
            data.put("cost", c.getCost());
            switch(id) {
                case 1:
                    data.put("students", Utils.getNumStudentsByRace(((Effect1) c.getEffect()).getStudents() ));
                    break;
                case 5:
                    data.put("prohibitions", ((Effect5) c.getEffect()).getProhibitionCounter() );
                    break;
                case 7:
                    data.put("students", Utils.getNumStudentsByRace(((Effect7) c.getEffect()).getStudents() ));
                    break;
                case 11:
                    data.put("students", Utils.getNumStudentsByRace(((Effect11) c.getEffect()).getStudents() ));
                    break;
            }
        }

        return data;
    }

    /**
     * @return a map of useful infos about the characters
     */
    public HashMap<String, Object> getCharactersInfo(){
        HashMap<String, Object> data = new HashMap<>();
        for (Character c : characterModels){
            data.put(String.valueOf(c.getId()), getCharacterInfoById(c.getId()));
        }
        return data;
    }

    //TODO: remove
    public GameController getGmController() {
        return gmController;
    }

    //TODO: remove
    public int getCharacterIdByIndex(int index) {
        return characterModels.get(index).getId();
    }

    /**
     * @param id the id of the character
     * @param packet a packet containing the effect inputs and parameters
     * @return a list of packets containing infos about the played effect
     */
    public ArrayList<Packet> EffectById(int id, Packet packet){
        for(Character c : characterModels){
            if(c.getId() == id){
                return c.Effect(gmController, packet);
            }
        }
        return null;
    }

    /**
     * @param id the id of the character
     * @return the current cost of the character
     */
    public int getCostById(int id) {
        for(Character c : characterModels){
            if(c.getId() == id){
                return c.getCost();
            }
        }
        return 0;
    }

    /**
     * Increments the character cost
     * @param id the id of the character
     */
    public void incrementCostById(int id){
        for(Character c: characterModels) {
            if(c.getId() == id) {
                c.incrementCost();
            }
        }
    }

    /**
     * Adds a prohibition to the character 5
     */
    public void effect5AddProhibition(){
        for(Character c : characterModels){
            if(c.getId() == 5){
                c.getEffect5().addProhibition();
            }
        }
    }
}
