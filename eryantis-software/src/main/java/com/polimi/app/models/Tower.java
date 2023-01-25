package main.java.com.polimi.app.models;

import java.io.Serializable;

/**
 * Tower class.
 * Each instance is readOnly and represented by a specific colour from the enumeration.
 * @author Group 53
 */
public class Tower implements Serializable {
    //The tower actual colour
    private final Colour colour;

    public Tower(Colour colour) {
        this.colour = colour;
    }

    public Colour getColour() {
        return this.colour;
    }

    public String getAbbreviation() {
        return this.colour.getAbbreviation();
    }

}
