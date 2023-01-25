package test.java.com.polimi.app;

import main.java.com.polimi.app.controllers.BagController;
import main.java.com.polimi.app.controllers.GameController;
import main.java.com.polimi.app.models.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BagControllerTest {
    private static GameController context = new GameController(0, "Expert", 3);
    private static BagController controller;

    @Before
    public void setUp() {
        controller = new BagController(context, 3);
        context.initializeGame();
    }

    @After
    public void clean() {
        controller = null;
        context = new GameController(0, "Expert", 3);
    }

    @Test
    public void drawStudentsTest(){
        //test drawForCloud when we have 3 players
        assertEquals(4, controller.drawForCloud().size());
        assertEquals(5, controller.drawStudentsByQuantity(5).size());

        //test drawForCloud when we have 2 players
        clean();
        context = new GameController(0, "Expert", 2);
        context.getPlController().addPlayer(new Player(0, Mage.MORGANA, Colour.WHITE));
        context.getPlController().addPlayer(new Player(1, Mage.WONG, Colour.GREY));
        controller = new BagController(context, 2);
        assertEquals(3, controller.drawForCloud().size());
        assertEquals(5, controller.drawStudentsByQuantity(5).size());
    }

    @Test
    public void reinsertStudentsTest(){
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student(Race.DRAGON));
        students.add(new Student(Race.ELF));
        controller.reinsertStudents(students);
    }
}