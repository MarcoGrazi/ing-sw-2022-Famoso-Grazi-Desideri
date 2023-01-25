package main.java.com.polimi.client.views;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import main.java.com.polimi.client.models.*;
import main.java.com.polimi.client.utils.ColorStrings;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;


public class SchoolView extends Observable implements Observer{
    private final String red_present = "\033[48;2;216;0;0m";
    private final String red_absent = "\033[48;2;158;115;112m";
    private final String green_present = "\033[48;2;0;216;0m";
    private final String green_absent = "\033[48;2;86;130;86m";
    private final String pink_present = "\033[48;2;196;12;181m";
    private final String pink_absent = "\033[48;2;133;80;128m";
    private final String blue_present = "\033[48;2;0;0;216m";
    private final String blue_absent = "\033[48;2;86;86;145m";
    private final String yellow_present = "\033[48;2;235;235;5m";
    private final String yellow_absent = "\033[48;2;120;120;74m";
    private final String towerB= "\033[48;2;0;0;0mT";
    private final String towerW= "\033[38;2;0;0;0;48;2;255;255;255mT";
    private final String towerG= "\033[38;2;0;0;0;48;2;99;98;95mT";
    private final HashMap<Race, String[]> colors = new HashMap<>();
    private final HashMap<Colour,String> towerColors=new HashMap<>();


    @FXML
    AnchorPane schoolPane;

    /**
     * @param obs observer to add to the observer list
     */
    public SchoolView(Observer obs) {
        addObserver(obs);
        colors.put(Race.DRAGON, new String[]{red_present, red_absent});
        colors.put(Race.ELF, new String[]{yellow_present,yellow_absent});
        colors.put(Race.FAIRY, new String[]{pink_present,pink_absent});
        colors.put(Race.UNICORN, new String[]{blue_present,blue_absent});
        colors.put(Race.FROG, new String[]{green_present,green_absent});

        towerColors.put(Colour.WHITE,towerW);
        towerColors.put(Colour.BLACK,towerB);
        towerColors.put(Colour.GREY,towerG);
    }

    /**
     * @param school
     * Prints the school model
     */
    private void printSchool(School school){
        String printingString= "";
        for (Race r: colors.keySet()){
            for (int i =0; i<school.getStudentsInTable(r); i++){
                printingString+=colors.get(r)[0]+" |";
            }
            for (int i =0; i<12-school.getStudentsInTable(r); i++){
                printingString+=colors.get(r)[1]+" |";
            }
            printingString+= ColorStrings.RESET;
            printingString += "     ";
            if (school.getProfessorInTable(r)){
                printingString+=colors.get(r)[0]+"| |";
            }else{
                printingString+=colors.get(r)[1]+"| |";
            }
            printingString+="\033[m";
            System.out.println(printingString);
            printingString="";
        }
        System.out.println("______HALL______");
        int k=0;
        for (Student s : school.getStudentsInHall()){
            printingString+=colors.get(s.getRace())[0]+school.getStudentsInHall().indexOf(s)+"|"+ColorStrings.RESET;
            k++;
            if(k==15){
                printingString+="\n";
                k=0;
            }
        }
        System.out.println(printingString);
        printingString="";
        System.out.println("______TOWERS______");
        for(Tower t : school.getTowers()){
            printingString+=towerColors.get(t.getColour());
        }
        printingString+="\033[m";
        System.out.println(printingString);

    }

    @Override
    public void update(Observable o, Object arg) {
        Message msg = (Message) arg;
        School school = (School) o;
        switch (msg.getAction()){
            case "PRINT_SCHOOL":
            case "SETUP_SCHOOL":
                printSchool(school);
                break;
        }
    }
}
