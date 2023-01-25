package main.java.com.polimi.client.models;

/**
 * Race class
 * @author Group 53
 */
public enum Race {
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
