package main.java.com.polimi.app.models;

import java.io.Serializable;

/**
 * Race class
 * @author Group 53
 */
public enum Race implements Serializable {
    FAIRY("FAIRY"),
    DRAGON("DRAGON"),
    UNICORN("UNICORN"),
    FROG("FROG"),
    ELF("ELF");

    private final String colour;

    private Race(String colour){
        this.colour = colour;
    }

    public String getColour(){
        return this.colour;
    }
}
