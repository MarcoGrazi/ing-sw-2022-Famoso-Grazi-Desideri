package test.java.com.polimi.app;

import main.java.com.polimi.app.controllers.CloudController;
import main.java.com.polimi.app.controllers.GameController;
import main.java.com.polimi.app.models.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CloudControllerTest {
    private static GameController context = new GameController(0, "Expert", 3);
    private static CloudController controller;

    @Before
    public void setUp() {
        context.getPlController().addPlayer(new Player(0, Mage.JAFAR, Colour.BLACK));
        context.getPlController().addPlayer(new Player(1, Mage.MORGANA, Colour.WHITE));
        context.getPlController().addPlayer(new Player(2, Mage.WONG, Colour.GREY));
        controller = new CloudController(context, 3);
        context.initializeGame();
    }

    @After
    public void clean() {
        controller = null;
        context = new GameController(0, "Expert", 3);
    }

    @Test
    public void constructorTest(){
        //tests we instantiated the right amount of Clouds with the right amount of students on it for 3 players
        controller.cloudPlanningPhase();
        HashMap<Integer, Object> report = controller.getCloudsInfo();
        assertEquals(3, report.keySet().size());
        for(int id : report.keySet()){
            assertEquals(4, controller.removeStudentsFromCloud(id).size());
        }

        //tests we instantiated the right amount of Clouds with the right amount of students on it for 2 players
        clean();
        context = new GameController(0, "Expert", 2);
        context.getPlController().addPlayer(new Player(0, Mage.MORGANA, Colour.WHITE));
        context.getPlController().addPlayer(new Player(1, Mage.WONG, Colour.GREY));
        controller = new CloudController(context, 2);
        context.initializeGame();
        controller.cloudPlanningPhase();
        report = controller.getCloudsInfo();
        assertEquals(2, report.keySet().size());
        for(int id : report.keySet()){
            assertEquals(3, controller.removeStudentsFromCloud(id).size());
        }
    }

    @Test
    public void cloudStudentsTest(){
        assertTrue(controller.isEmpty(1));
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student(Race.DRAGON));
        students.add(new Student(Race.ELF));
        controller.addStudentsToCloud(students, 1);
        assertEquals(students, controller.removeStudentsFromCloud(1));
    }
}