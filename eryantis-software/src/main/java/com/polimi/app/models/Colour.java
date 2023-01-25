package main.java.com.polimi.app.models;

import java.io.Serializable;

/**
 * Colour class.
 * @author Group 53
 */
public enum Colour implements Serializable {
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
