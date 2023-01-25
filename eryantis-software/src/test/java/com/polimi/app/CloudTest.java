package test.java.com.polimi.app;

import main.java.com.polimi.app.models.Cloud;
import main.java.com.polimi.app.models.Race;
import main.java.com.polimi.app.models.Student;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CloudTest {

    public static Cloud cloud;

    @Before
    public void setUp() {
        cloud = new Cloud(1);
    }

    @After
    public void clean(){
        cloud=null;
    }

    @Test
    public void getCloudId__correctCloudId(){
        assertEquals(1, cloud.getCloudId());
    }
    @Test
    public void addStudentsTest() {
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student(Race.DRAGON));
        students.add(new Student(Race.FAIRY));
        students.add(new Student(Race.ELF));

        //test addStudent when cloud is empty
        cloud.addStudents(students);
        assertEquals(students, cloud.getStudents());

        //tests addStudent when cloud is full
        ArrayList<Student> students2 = new ArrayList<>();
        students2.add(new Student(Race.DRAGON));
        cloud.addStudents(students2);
        assertEquals(students, cloud.getStudents());

    }

    @Test
    public void removeStudentsTest() {
        ArrayList<Student> students = new ArrayList<>();
        students.add(new Student(Race.DRAGON));
        students.add(new Student(Race.FAIRY));
        students.add(new Student(Race.ELF));
        cloud.addStudents(students);

        //test removeStudent with cloud full
        assertEquals(students, cloud.removeStudents());

        //test removeStudent with cloud empty
        assertEquals(0, cloud.removeStudents().size());
    }

}