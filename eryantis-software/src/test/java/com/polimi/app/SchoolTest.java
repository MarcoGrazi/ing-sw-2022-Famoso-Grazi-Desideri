package test.java.com.polimi.app;

import main.java.com.polimi.app.models.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

import static java.util.Collections.shuffle;
import static org.junit.jupiter.api.Assertions.*;


public class SchoolTest {
    private static School school;
    private static ArrayList<Tower> towers;
    private static int schoolId = 0;
    private static Colour colour = Colour.BLACK;

    @Before
    public void setUp() {
        towers = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            towers.add(new Tower(colour));
        }
        school = new School(towers, schoolId);
    }

    @After
    public void clean(){
        towers.clear();
        school = null;
    }

    @Test
    public void constructorTest(){
        assertEquals(schoolId, school.getSchoolId());
        assertEquals(towers, school.getTowers());

        ArrayList<Integer> correctTableSizes = new ArrayList<>(Arrays.asList(0,0,0,0,0));
        assertEquals(correctTableSizes, school.getTablesSizes());
        for(Race r : Race.values()){
            assertFalse(school.getProfessorInTable(r));
        }
    }

    @Test
    public void towersTest() throws Exception {
        assertEquals(Colour.BLACK, school.getTowerColour());

        //test addTowers and subTowers possible Exceptions
        ArrayList<Tower> testColourException = new ArrayList<>(Arrays.asList(new Tower(Colour.WHITE)));
        assertThrows(Exception.class, () -> school.addTowers(testColourException));

        ArrayList<Tower> testSizeException = new ArrayList<>();
        testSizeException.add(new Tower(Colour.BLACK));
        testSizeException.add(new Tower(Colour.BLACK));
        testSizeException.add(new Tower(Colour.BLACK));
        testSizeException.add(new Tower(Colour.BLACK));
        testSizeException.add(new Tower(Colour.BLACK));
        testSizeException.add(new Tower(Colour.BLACK));

        assertThrows(Exception.class, () -> school.addTowers(testSizeException));

        int testEnoughTowersException = 8;
        assertThrows(Exception.class, () -> school.subTowers(testEnoughTowersException));

        int testNegativeException = -3;
        assertThrows(Exception.class, () -> school.subTowers(testNegativeException));

        //test correct addTowers functioning
        ArrayList<Tower> testAddTowers = new ArrayList<>();
        testAddTowers.add(new Tower(Colour.BLACK));
        testAddTowers.add(new Tower(Colour.BLACK));
        testAddTowers.add(new Tower(Colour.BLACK));
        towers.addAll(testAddTowers);

        school.addTowers(testAddTowers);
        assertEquals(towers, school.getTowers());

        //test correct subTowers functioning
        towers.removeAll(testAddTowers);
        school.subTowers(3);
        assertEquals(towers.size(), school.getTowers().size());
    }

    @Test
    public void hallTest(){
        ArrayList<Student> orderedHallStudents = new ArrayList<>();
        orderedHallStudents.add(new Student(Race.FAIRY));
        orderedHallStudents.add(new Student(Race.DRAGON));
        orderedHallStudents.add(new Student(Race.UNICORN));
        orderedHallStudents.add(new Student(Race.FROG));
        orderedHallStudents.add(new Student(Race.ELF));

        ArrayList<Student> scrambledHallStudent = new ArrayList<>();
        scrambledHallStudent.addAll(orderedHallStudents);
        shuffle(scrambledHallStudent);

        //test addStudentToHall, which contains orderHall so we expect orderedHallStudents
        school.addStudentsToHall(scrambledHallStudent);
        assertEquals(orderedHallStudents, school.getStudentsInHall());

        //test orderHall
        school.orderHall();
        assertEquals(orderedHallStudents, school.getStudentsInHall());

        //test getStudentFromHallByIndex
        int testindex = 2;
        assertEquals(orderedHallStudents.get(testindex), school.getStudentFromHallByIndex(testindex));

        //test removeStudentFromHallByIndex
        assertEquals(orderedHallStudents.get(testindex), school.removeStudentFromHallByIndex(testindex));
        orderedHallStudents.remove(testindex);
        assertEquals(orderedHallStudents.size(), school.getStudentsInHall().size());

        //test removeStudentsFromHall
        school.removeStudentsFromHall(orderedHallStudents);
        assertEquals(0, school.getStudentsInHall().size());
    }

    @Test
    public void tableTest(){
        ArrayList<Student> tableStudents = new ArrayList<>();
        tableStudents.add(new Student(Race.FAIRY));
        tableStudents.add(new Student(Race.DRAGON));
        tableStudents.add(new Student(Race.UNICORN));
        tableStudents.add(new Student(Race.FROG));
        tableStudents.add(new Student(Race.ELF));

        //test moveStudentToTable
        for(Student s:tableStudents) {
            school.moveStudentToTable(s);
        }

        ArrayList<Integer> tableCounters = new ArrayList<>();
        //test getStudentsInTable
        for(Student s: tableStudents) {
            Stack<Student> stack = new Stack<>();
            stack.add(s);
            assertEquals(stack, school.getStudentsInTable(s.getRace()));
            tableCounters.add(1);
        }

        //test getTableSizes
        assertEquals(tableCounters, school.getTablesSizes());

        //test removeStudentsFromTable
        Race testRemoveRace = Race.DRAGON;
        school.removeStudentsFromTable(1, testRemoveRace);
        Stack<Student> stack = new Stack<>();
        assertEquals(stack, school.getStudentsInTable(testRemoveRace));
    }

    @Test
    public void professorTest(){
        school.setProfessor(Race.DRAGON, true);
        assertTrue(school.getProfessorInTable(Race.DRAGON));

        school.setProfessor(Race.DRAGON, false);
        assertFalse(school.getProfessorInTable(Race.DRAGON));
    }

    @Test
    public void exchangeStudent__correctExchange(){
        ArrayList<Student> HallStudents = new ArrayList<>();
        HallStudents.add(new Student(Race.FAIRY));
        HallStudents.add(new Student(Race.DRAGON));
        HallStudents.add(new Student(Race.UNICORN));
        HallStudents.add(new Student(Race.FROG));
        HallStudents.add(new Student(Race.ELF));

        school.addStudentsToHall(HallStudents);

        ArrayList<Student> tableStudents = new ArrayList<>();
        tableStudents.add(new Student(Race.FAIRY));
        tableStudents.add(new Student(Race.DRAGON));
        tableStudents.add(new Student(Race.UNICORN));
        tableStudents.add(new Student(Race.FROG));
        tableStudents.add(new Student(Race.ELF));

        //test moveStudentToTable
        for(Student s:tableStudents) {
            school.moveStudentToTable(s);
        }

        int exchangeIndex = 2;
        school.exchangeStudents(exchangeIndex, tableStudents.get(exchangeIndex).getRace());

        //chacks that the student we chose to move from table is now in hall
        assertEquals(tableStudents.get(exchangeIndex), school.getStudentsInHall().get(exchangeIndex));

        //chacks that the student we chose to move from the hall is now in its Race's table
        Stack<Student> stack = new Stack<>();
        stack.add(HallStudents.get(exchangeIndex));
        assertEquals(stack, school.getStudentsInTable(HallStudents.get(exchangeIndex).getRace()));
    }
}
