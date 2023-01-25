package main.java.com.polimi.client.models;

import com.google.gson.internal.LinkedTreeMap;
import main.java.com.polimi.client.utils.Utils;

import java.util.*;

/**
 * Clouds class.
 * Keeps track of all the clouds inside the game.
 * @author Group 53
 */
public class Clouds extends Observable {
    //List of students on each cloud mapped to the cloud id
    private final HashMap<Integer,ArrayList<Student>> clouds;

    /**
     * Class constructor.
     * Initializes the set of infos about the clouds and adds the cloud view as observer.
     * @param cloudHash the map of infos
     * @param view the instance of the view
     */
    public Clouds(LinkedTreeMap<String, Object> cloudHash, Observer view){
        clouds = new HashMap<>();
        for (String key: cloudHash.keySet()){
            ArrayList<Student> studInCl= new ArrayList<>();
            ArrayList<Double> stuInCloud=(ArrayList<Double>) cloudHash.get(key);

            for (int i =0; i< stuInCloud.size(); i++){
                for (int j=0 ; j<stuInCloud.get(i); j++){
                    studInCl.add(new Student(Utils.raceArray.get(i)));
                }
            }
            int intKey = (int) Math.round(Float.parseFloat(key));
            clouds.put(intKey,studInCl);
        }

        addObserver(view);
        setChanged();
        notifyObservers(new Message("SETUP_CLOUDS"));
    }

    /**
     * @return all the cloud ids
     */
    public Set<Integer> getCloudIds() {
        return clouds.keySet();
    }

    /**
     * @param cloudId the id of the cloud
     * @return the list of student on the cloud
     */
    public ArrayList<Student> getStudentsInCloud(int cloudId) {
        return this.clouds.get(cloudId);
    }

    /**
     * Updates the clouds with new infos
     * @param cloudHash the new cloud infos
     */
    public void updateClouds(LinkedTreeMap<String, Object> cloudHash){
        for (String key: cloudHash.keySet()){
            ArrayList<Student> studInCl= new ArrayList<>();
            ArrayList<Double> stuInClNum=(ArrayList<Double>) cloudHash.get(key);
            for (int i =0; i< stuInClNum.size(); i++){
                for (int j=0 ; j<stuInClNum.get(i); j++){
                    studInCl.add(new Student(Utils.raceArray.get(i)));
                }
            }
            int intKey = (int) Math.round(Float.parseFloat(key));
            clouds.replace(intKey,studInCl);
        }

        setChanged();
        notifyObservers(new Message("UPDATE_CLOUDS"));
    }

    //TODO: remove
    public void removeStudents(int cloudId) {
        clouds.replace(cloudId, new ArrayList<Student>());
        setChanged();
        notifyObservers();
    }

    /**
     * @return the infos about the clouds
     */
    public HashMap<Integer, ArrayList<Student>> getClouds() {
        return clouds;
    }

    /**
     * Removes all the students from the cloud
     * @param cloudId the id of the cloud
     */
    public void clearCloud(int cloudId) {
        this.clouds.get(cloudId).clear();
        setChanged();
        notifyObservers(new Message("UPDATE_CLOUDS"));
    }
}
