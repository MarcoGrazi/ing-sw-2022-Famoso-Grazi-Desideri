package test.java.com.polimi.app;

import main.java.com.polimi.app.controllers.ArchipelagoController;
import main.java.com.polimi.app.controllers.GameController;
import main.java.com.polimi.app.models.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ArchipelagoControllerTest {
    private static GameController context = new GameController(0, "Expert", 3);
    private static ArchipelagoController controller;
    @Before
    public void setUp() {
        context.initializeGame();
    }

    @After
    public void clean() {
        controller = null;
        context = new GameController(0, "Expert", 3);
    }

    @Test
    public void initializationTest(){
        controller = new ArchipelagoController(context);
        controller.initializeArchipelago();
        Archipelago archModel = controller.getArchipelago();
        int positionMN = archModel.getMNposition();
        int positionOpposed = positionMN < 6 ? positionMN + 6 : ( 6 + positionMN - 12  );

        for(int i=0; i<controller.getArchipelago().getIslands().length; i++){
            if(i!=positionMN && i!=positionOpposed){
                assertEquals(1, archModel.getIslands()[i].getStudentList().size());
            }
            else{
                assertEquals(0, archModel.getIslands()[i].getStudentList().size());
            }
        }
    }

    @Test
    public void addStudentToIslandTest(){
        controller = new ArchipelagoController(context);
        controller.initializeArchipelago();
        Archipelago archModel = controller.getArchipelago();
        Student student = new Student(Race.DRAGON);

        controller.addStudentToIsland(student, 1);
        assertTrue(archModel.getIslands()[1].getStudentList().contains(student));
    }

    @Test
    public void addProhibitionTest(){
        controller = new ArchipelagoController(context);
        controller.initializeArchipelago();
        Archipelago archModel = controller.getArchipelago();
        controller.addProhibition(1);

        assertTrue(archModel.isIslandProhibited(1));
    }

    @Test
    public void getGroupIndexes__correctIndexes() {
        controller = new ArchipelagoController(context);
        controller.initializeArchipelago();
        Archipelago archModel = controller.getArchipelago();
        ArrayList<Tower> towers = new ArrayList<Tower>();

        towers.add(new Tower(Colour.BLACK));
        archModel.takeoverTowers(0, towers);
        towers.add(new Tower(Colour.BLACK));
        archModel.takeoverTowers(1, towers);
        towers.add(new Tower(Colour.BLACK));
        archModel.takeoverTowers(2, towers);
        archModel.mergeIslands();

        assertTrue(controller.getGroupsIndexes().contains(0));
        assertFalse(controller.getGroupsIndexes().contains(1));
        assertFalse(controller.getGroupsIndexes().contains(2));
    }
}