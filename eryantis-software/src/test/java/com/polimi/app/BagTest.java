package test.java.com.polimi.app;

import main.java.com.polimi.app.models.Bag;
import main.java.com.polimi.app.models.Race;
import main.java.com.polimi.app.models.Student;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BagTest {

    public static Bag bag;
    protected Race[] raceArray = {Race.FAIRY, Race.DRAGON, Race.UNICORN, Race.FROG, Race.ELF};

    @Before
    public void setUp() {
        bag = new Bag();
    }

    @After
    public void clean(){
        bag = null;
    }

    @Test
    public void isEmptyTest(){
        bag.draw(130);
        assertTrue(bag.isEmpty());
    }

    @Test
    public void drawTest() {

        HashMap<Race, Integer> tmpStudentsCounter = new HashMap<>();
        for(Race r : raceArray) {
            tmpStudentsCounter.put(r, 24);
        }

        ArrayList<Student> studentsDrawn = bag.draw(3);
        for(Student s : studentsDrawn) {
            tmpStudentsCounter.replace(s.getRace(), tmpStudentsCounter.get(s.getRace()) - 1 );
        }

        assertEquals(3, studentsDrawn.size());
        for(Race r : raceArray) {
            assertEquals( bag.getStudentsCounter().get(r), tmpStudentsCounter.get(r));
        }
    }

    @Test
    public void reinsertTest() {
        ArrayList<Student> studentsDrawn = bag.draw(3);

        bag.reinsert(studentsDrawn);

        for(Race r : raceArray) {
            assertEquals(bag.getStudentsCounter().get(r), 24);
        }
    }
}