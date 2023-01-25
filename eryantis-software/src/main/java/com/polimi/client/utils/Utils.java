package main.java.com.polimi.client.utils;

import main.java.com.polimi.client.models.Student;
import main.java.com.polimi.client.models.Colour;
import main.java.com.polimi.client.models.Mage;
import main.java.com.polimi.client.models.Race;
import main.java.com.polimi.client.views.gui.ClientGUIView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Utils {
    public static final ArrayList<Mage> mageArray = new ArrayList<>(Arrays.asList(Mage.JAFAR, Mage.MORGANA, Mage.MERLIN, Mage.WONG));
    public static final ArrayList<Colour> colourArray =  new ArrayList<>(Arrays.asList(Colour.WHITE, Colour.BLACK, Colour.GREY));
    public static final ArrayList<Race> raceArray = new ArrayList<>(Arrays.asList(Race.FAIRY, Race.DRAGON, Race.UNICORN, Race.FROG, Race.ELF));
    public static final String[] mageStrings  = {"JAFAR", "MORGANA", "MERLIN", "WONG"};
    public static final String[] colourStrings  = {"WHITE", "BLACK", "GREY"};
    public static final String[] playerNumberArray ={"2","3"};
    public static final int baseX=219;
    public static final int distX=57;
    public static final int baseXProf=838;

    public static int[] BASE_Y_COORDS = {63, 148, 231, 314, 397};

    public static int[][] TOWER_COORDS={
            {66, 957},  {66,1048},
            {150,957}, {150,1048},
            {234,957}, {234,1048},
            {317,957}, {317,1048}
    };
    public static int[][] HALL_COORDS={
            {63, 110},
            {148,40}, {148,110},
            {231,40}, {231,110},
            {314,40}, {314,110},
            {397,40}, {397,110},
    };

    public static int[][] CLOUD_COORDS={
            {55,41},
            {241,41},
            {448,41},
    };
    public static int[][] STU_IN_CL2_COORDS={
            {25,70},
            {47,11},
            {88,65}
    };

    public static int[][] STU_IN_CL3_COORDS={
            {19,75},
            {32,18},
            {75,86},
            {87,32}
    };

    public static int[][] STU_IN_IS_COORDS= {
            {10,10},
            {10,70},
            {38,38},
            {70,10},
            {70,70},
    };

    public static int[] MN_IN_IS_COORDS= {80,30};

    public static int[][] STU_IN_CHAR= {
            {20, 20},
            {120, 20},
            {70, 60},
            {20, 100},
            {120, 100}
    };

    static public ArrayList<String> getMageString() {
        ArrayList<String> mageMap = new ArrayList<>();
        for(Mage m: mageArray) {
            mageMap.add(m.name());
        }
        return mageMap;
    }

    static public ArrayList<String> getColourString() {
        ArrayList<String> colourMap = new ArrayList<>();
        for(Colour c: colourArray) {
            colourMap.add(c.name());
        }
        return colourMap;
    }

    static public ArrayList<String> getRaceString() {
        ArrayList<String> raceMap = new ArrayList<>();
        for(Race r: raceArray) {
            raceMap.add(r.name());
        }
        return raceMap;
    }

    public static Race getRaceFromString(String r){
        Race race;
        switch (r) {
            case "FAIRY":
                race = Race.FAIRY;
                break;
            case "DRAGON":
                race = Race.DRAGON;
                break;
            case "UNICORN":
                race = Race.UNICORN;
                break;
            case "FROG":
                race = Race.FROG;
                break;
            default:
                race = Race.ELF;
                break;
        }
        return race;
    }

    public static Mage getMageFromString(String m){
        Mage mage;
        switch (m) {
            case "JAFAR":
                mage = Mage.JAFAR;
                break;
            case "MORGANA":
                mage = Mage.MORGANA;
                break;
            case "MERLIN":
                mage = Mage.MERLIN;
                break;
            case "WONG":
                mage = Mage.WONG;
                break;
            default:
                mage = Mage.MORGANA;
                break;
        }
        return mage;
    }

    public static Colour getColourFromString(String c){
        Colour colour;
        switch (c) {
            case "WHITE":
                colour = Colour.WHITE;
                break;
            case "BLACK":
                colour = Colour.BLACK;
                break;
            case "GREY":
                colour = Colour.GREY;
                break;
            default:
                colour = Colour.WHITE;
                break;
        }
        return colour;
    }
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

    public static Image getImageFromPath(String path, String pathFromGraphAssets){
        Image image;
        try{
            image= new ImageIcon(ClientGUIView.class.getResource("graphical_assets/"+pathFromGraphAssets)).getImage();
        }catch (NullPointerException e){
            image= new ImageIcon(path+pathFromGraphAssets).getImage();
        }
        return image;

    }
    public static ImageIcon getImageIconFromPath(String path, String pathFromGraphAssets){
        ImageIcon image;
        try{
            image= new ImageIcon(ClientGUIView.class.getResource("graphical_assets/"+pathFromGraphAssets));
        }catch (NullPointerException e){
            image= new ImageIcon(path+pathFromGraphAssets);
        }
        return image;

    }

}
