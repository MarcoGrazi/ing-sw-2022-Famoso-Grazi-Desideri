package test.java.com.polimi.app;

import main.java.com.polimi.app.models.Race;
import main.java.com.polimi.app.models.Student;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StudentTest {
    private static Race race;
    private static Student student;

    @Before
    public void setUp(){
        race= Race.DRAGON;
        student = new Student(race);
    }

    @Test
    public void getRace(){
        assertEquals(race, student.getRace());
    }

    @Test
    public void getColour(){
        assertEquals(race.getColour(), student.getColour());
    }


}
