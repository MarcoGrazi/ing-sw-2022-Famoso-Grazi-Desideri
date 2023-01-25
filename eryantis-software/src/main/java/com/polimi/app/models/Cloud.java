package main.java.com.polimi.app.models;

import java.io.Serializable;
import java.util.*;

/**
 * Cloud class.
 * Each instance is represented by an absolute Id, unique for each cloud, and an ArrayList of students which stores
 * the students currently placed on the cloud.
 * @author Group 53
 */
public class Cloud implements Serializable {
    //The absolute cloudId
    private final int cloudId;
    //List of students on the cloud
    private final ArrayList<Student> students;

    /**
     * Class constructor.
     * Initializes the cloud with its cloudId and an empty list of students.
     * @param cloudId the specific cloud Id
     */
    public Cloud(int cloudId){
        this.cloudId = cloudId;
        this.students= new ArrayList<>();
    }

    /**
     * @return the cloud's absolute Id
     */
    public int getCloudId() {
        return cloudId;
    }

    /**
     * @return the students present on the cloud, without removing them from the list
     */
    public ArrayList<Student> getStudents() {
        return this.students;
    }

    /**
     * Adds the students to the cloud.
     * @param students a list of students to add to the cloud
     */
    public void addStudents(ArrayList<Student> students){
        if(this.students.size() > 0){
            //TODO: Throw new CloudAlreadyFullException();
            System.out.println("CloudAlreadyFullException");
        }
        else {
            this.students.addAll(students);
        }
    }

    /**
     * Removes students from the cloud.
     * @return all the students present on the cloud by removing them from the list
     */
    public ArrayList<Student> removeStudents() {
        ArrayList<Student> tmpStudents = new ArrayList<>();

        if(this.students.size()==0){
            //TODO: Throw new CloudEmptyException();
            System.out.println("CloudEmptyException");
        }
        else {
            tmpStudents.addAll(students);
            students.clear();
        }
        //Returns tmpStudents
        return tmpStudents;
    }


}
