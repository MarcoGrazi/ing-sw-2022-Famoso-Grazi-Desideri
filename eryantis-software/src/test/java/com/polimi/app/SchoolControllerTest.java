package test.java.com.polimi.app;

import main.java.com.polimi.app.controllers.GameController;
import main.java.com.polimi.app.controllers.SchoolController;
import main.java.com.polimi.app.models.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SchoolControllerTest {
    private static GameController context = new GameController(0, "Expert", 3);
    private static SchoolController controller;
    @Before
    public void setUp() {
        context.getPlController().addPlayer(new Player(0, Mage.JAFAR, Colour.BLACK));
        context.getPlController().addPlayer(new Player(1, Mage.MORGANA, Colour.WHITE));
        context.getPlController().addPlayer(new Player(2, Mage.WONG, Colour.GREY));
        controller = new SchoolController(context, 3);
        context.initializeGame();
    }

    @After
    public void clean() {
        controller = null;
        context = new GameController(0, "Expert", 3);
    }

    @Test
    public void ConstructorTest(){
        assertEquals(Colour.BLACK, controller.getSchoolColour(0));
        assertEquals(Colour.WHITE, controller.getSchoolColour(1));
        assertEquals(Colour.GREY, controller.getSchoolColour(2));

        assertEquals(0, controller.getSchoolIdByColour(Colour.BLACK));
        assertEquals(1, controller.getSchoolIdByColour(Colour.WHITE));
        assertEquals(2, controller.getSchoolIdByColour(Colour.GREY));

        assertEquals(6, controller.getTowerQuantity(0));
        assertEquals(6, controller.getTowerQuantity(1));
        assertEquals(6, controller.getTowerQuantity(2));

        assertEquals(controller.getSchoolByIndex(0), controller.getSchools().get(controller.getSchoolIndex(0)));
        assertEquals(controller.getSchoolByIndex(1), controller.getSchools().get(controller.getSchoolIndex(1)));
        assertEquals(controller.getSchoolByIndex(2), controller.getSchools().get(controller.getSchoolIndex(2)));
    }

    @Test
    public void moveStudentTest(){
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student(Race.DRAGON));
        students.add(new Student(Race.FROG));

        //testing moveStudentsToHall
        controller.moveStudentsToHall(students, 0);
        assertEquals(students, controller.getSchoolByIndex(0).getStudentsInHall());

        //testing moveStudentToTable
        controller.moveStudentToTable(0, 0);
        Stack<Student> stack= new Stack<>();
        stack.add(students.get(0));
        assertEquals(stack, controller.getSchoolByIndex(0).getStudentsInTable(Race.DRAGON));

        //testing moveStudentToIsland
        controller.moveStudentToIsland(0, 1, 0);
        students.remove(0);
        assertTrue(context.getArchipelagoController().getArchipelago().getIslands()[1].getStudentList().contains(students.get(0)));

        //testing exchangeHallTable
        students.remove(0);
        students.add(new Student(Race.ELF));
        controller.moveStudentsToHall(students, 0);
        ArrayList<Integer> hall = new ArrayList<>(Arrays.asList(0));
        ArrayList<Race> table = new ArrayList<Race>();
        table.add(Race.DRAGON);
        controller.exchangeHallTable(hall, table, 0);
        assertEquals(Race.DRAGON, controller.getSchoolByIndex(0).getStudentsInHall().get(0).getRace());
        assertEquals(1, controller.getNumStudentsInTables(0).get(4));

    }

    @Test
    public void removeStudentFromTableTest(){
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student(Race.DRAGON));
        controller.moveStudentsToHall(students, 0);
        controller.moveStudentToTable(0, 0);

        controller.removeStudentsFromTable(1, Race.DRAGON, 0);
        assertEquals(0, controller.getNumStudentsInTables(0).get(1));
    }

    @Test
    public void initializeHallsTest(){
        controller.initializeHalls();
        assertEquals(9, controller.getNumStudentsInHall(0));

        clean();
        context = new GameController(0, "Expert", 2);
        context.getPlController().addPlayer(new Player(0, Mage.MORGANA, Colour.WHITE));
        context.getPlController().addPlayer(new Player(1, Mage.WONG, Colour.GREY));
        controller = new SchoolController(context, 2);
        context.initializeGame();

        controller.initializeHalls();
        assertEquals(7, controller.getNumStudentsInHall(0));
    }

    @Test
    public void towerTest(){
        ArrayList<Tower> towers = new ArrayList<>();
        towers.add(new Tower(Colour.BLACK));
        towers.add(new Tower(Colour.BLACK));

        controller.addTowers(0, towers);
        assertEquals(8, controller.getTowerQuantity(0));

        controller.subTowers(0, 2);
        assertEquals(6, controller.getTowerQuantity(0));
    }

    @Test
    public void professorsTest(){
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student(Race.DRAGON));
        controller.moveStudentsToHall(students, 0);
        controller.moveStudentToTable(0, 0);
        controller.setProfessors();

        assertEquals(0, controller.getProfessorsMap().get(1));
    }
}