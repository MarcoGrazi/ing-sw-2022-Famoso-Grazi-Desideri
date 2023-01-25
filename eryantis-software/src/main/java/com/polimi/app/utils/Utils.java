package main.java.com.polimi.app.utils;

import main.java.com.polimi.app.models.Colour;
import main.java.com.polimi.app.models.Race;
import main.java.com.polimi.app.models.Student;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Utils class.
 * Used to implement methods useful in a lot of different situations and classes. Avoids code repetition.
 */
public class Utils {
    //Array of colours
    public static final ArrayList<Colour> colourArray =  new ArrayList<>(Arrays.asList(Colour.WHITE, Colour.BLACK, Colour.GREY));
    //Array of races
    public static final ArrayList<Race> raceArray = new ArrayList<>(Arrays.asList(Race.FAIRY, Race.DRAGON, Race.UNICORN, Race.FROG, Race.ELF));

    /**
     * @param name the string representing the race
     * @return the respective enum instance
     */
    static public Race getRaceFromString(String name){
        switch(name){
            case "FAIRY" -> {return Race.FAIRY;}
            case "DRAGON" -> {return Race.DRAGON;}
            case "UNICORN" -> {return Race.UNICORN;}
            case "FROG" -> {return Race.FROG;}
            case "ELF" -> {return Race.ELF;}
            default -> {return null;}
        }
    }

    /**
     * @return the array of races as strings
     */
    static public ArrayList<String> getRaceString() {
        ArrayList<String> raceMap = new ArrayList<>();
        for(Race r: raceArray) {
            raceMap.add(r.name());
        }
        return raceMap;
    }

    //TODO: remove
    public static ArrayList<Colour> getColourArray() {
        return colourArray;
    }

    /**
     * @param c the string representing the colour
     * @return the respective enum instance
     */
    public static Colour getColourFromString(String c){
        Colour colour = switch (c) {
            case "WHITE" -> Colour.WHITE;
            case "BLACK" -> Colour.BLACK;
            case "GREY" -> Colour.GREY;
            default -> Colour.WHITE; //NOT CORRECT!
        };
        return colour;
    }

    //TODO: remove
    static public ArrayList<String> getColourString() {
        ArrayList<String> colourMap = new ArrayList<>();
        for(Colour r: colourArray) {
            colourMap.add(r.name());
        }
        return colourMap;
    }

    /**
     * Counts all the students inside a list for each race
     * @param students the list of students
     * @return an array of 5 integers with the students counter for each race. Parallel to the races array.
     */
    static public ArrayList<Integer> getNumStudentsByRace(ArrayList<Student> students) {
        ArrayList<Integer> numStudents = new ArrayList<>();

        for(Race r: raceArray) {
            int raceCounter = 0;

            for(Student s: students) {
                if(r == s.getRace()) {
                    raceCounter++;
                }
            }
            numStudents.add(raceCounter);
        }
        return numStudents;
    }

    /**
     * Sorts students inside an array by race. Following the race array order
     * @param students the list of students to sort
     * @return the sorted array
     */
    static public ArrayList<Student> sortStudentByRace(ArrayList<Student> students) {
        ArrayList<Student> unsorted = students;

        unsorted.sort((o1, o2) -> {
            Integer index1 = getRaceString().indexOf(o1.getRace().name());
            Integer index2 = getRaceString().indexOf(o2.getRace().name());

            return index1.compareTo(index2);
        });

        return unsorted;
    }
}
