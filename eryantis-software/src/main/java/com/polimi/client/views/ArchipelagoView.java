package main.java.com.polimi.client.views;

import main.java.com.polimi.client.models.Message;
import main.java.com.polimi.client.models.Archipelago;
import main.java.com.polimi.client.models.Island;
import main.java.com.polimi.client.models.Race;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class ArchipelagoView extends Observable implements Observer {

    private final String spacer = "   ";

    /**
     * @param archipelagoController takes a client controler to add as an observer
     */
    public ArchipelagoView(Observer archipelagoController) {
        addObserver(archipelagoController);
    }

    @Override
    public void update(Observable o, Object arg) {
        Archipelago arch = (Archipelago) o;

        Message msg = (Message) arg;
        String actionType = msg.getAction();

        switch (actionType) {
            case "SETUP_ARCH" -> System.out.println("View initialized...");
            case "UPDATE_ARCH" -> System.out.println("View updated after merging...");
        }
        System.out.println(spacer);
        showArchipelago(arch);
    }

    /**
     * @param arch
     * Prints the archipelago info
     */
    //UTILS
    //Stamps the current state of the Archipelago
    private void showArchipelago(Archipelago arch) {
        int fakeIndex = 1;

        for( Integer i : arch.getCurrentGroupsIndexMap().keySet()) {
            ArrayList<Island> group = arch.getGroupByIslandIndex(i);

            System.out.println(
                    group.size()==1 ? "ISLAND " + fakeIndex + ":"
                                    : "ISLAND GROUP " + fakeIndex + ":"
            );
            System.out.println(
                    arch.getMNposition().equals(i) ? "Mother nature IS here"
                                                   : "Mother nature IS NOT here"
            );
            System.out.println( spacer + "- Prohibitions: " + arch.getProhibitionCounterByIslandIndex(i));
            System.out.println( spacer + "- Tower colour: " + (group.get(0).getTower()!=null ? group.get(0).getTower().getColour() : "null") );
            System.out.println( spacer + "- Students: ");
            printStudentsByRace(group);

            fakeIndex++;
        }
    }

    /**
     * @param group
     * Prints student number by race
     */
    //Stamps the list of student present on a group of islands
    private void printStudentsByRace(ArrayList<Island> group) {
        Race[] raceArray = {Race.FAIRY, Race.DRAGON, Race.UNICORN, Race.FROG, Race.ELF};

        for( Race r : raceArray ) {
            int studentsCounter = 0;
            for (Island i : group) {
                studentsCounter += i.getStudentNumByRace(r);
            }

            if(studentsCounter != 0) {
                System.out.println(
                        studentsCounter==1 ? spacer + spacer + studentsCounter + r.name() + " student;"
                                           : spacer + spacer + studentsCounter + r.name() + " students;"
                );
            }
        }
    }

}
