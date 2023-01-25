package main.java.com.polimi.app.models;

import java.io.Serializable;

/**
 * Student class.
 * Each instance is readOnly and represented by a specific race from the enumeration.
 * @author Group 53
 */
public class Student implements Serializable {
    private final Race race;

    public Student(Race race) {
        this.race = race;
    }

    public Race getRace() {
        return this.race;
    }

    public String getColour() {
        return this.race.getColour();
    }

}
