package main.java.com.polimi.client.models;

/**
 * Tower class.
 * Each instance is readOnly and represented by a specific colour from the enumeration.
 * @author Group 53
 */
public class Tower {
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
