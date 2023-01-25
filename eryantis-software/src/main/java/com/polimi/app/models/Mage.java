package main.java.com.polimi.app.models;

import java.io.Serializable;

/**
 * Mage class
 * @author Group 53
 */
public enum Mage implements Serializable {
    MERLIN("MERLIN"),
    JAFAR("JAFAR"),
    MORGANA("MORGANA"),
    WONG("WONG");

    private final String mage;

    Mage(String mage) {
        this.mage = mage;
    }

    public String getMage(){
        return this.mage;
    }
}
