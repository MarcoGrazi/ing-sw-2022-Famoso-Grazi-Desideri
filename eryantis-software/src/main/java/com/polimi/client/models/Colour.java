package main.java.com.polimi.client.models;

/**
 * Colour class.
 * @author Group 53
 */
public enum Colour {
    WHITE("WHITE"),
    BLACK("BLACK"),
    GREY("GREY");

    private final String abbreviation;

    private Colour(String abbreviation){
        this.abbreviation= abbreviation;

    }

    public String getAbbreviation(){
        return this.abbreviation;
    }
}
