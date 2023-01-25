package test.java.com.polimi.app;

import main.java.com.polimi.app.models.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ArchipelagoTest {

    private static Archipelago myArchipelago;

    @Before
    public void setUp() {
        Integer whereIsMN = 7;

        Integer[] tempIslandsGroupIndexList = {10, 1, 2, 3, 4, 4, 4, 7, 7, 9, 10, 10};
        ArrayList<Integer> islandsGroupIndexList = new ArrayList<>(Arrays.asList(tempIslandsGroupIndexList));

        LinkedHashMap<Integer,Integer> currentGroupsIndexMap = new LinkedHashMap<>();
        currentGroupsIndexMap.put(1, 0);
        currentGroupsIndexMap.put(2, 0);
        currentGroupsIndexMap.put(3, 1);
        currentGroupsIndexMap.put(4, 1);
        currentGroupsIndexMap.put(7, 1);
        currentGroupsIndexMap.put(9, 2);
        currentGroupsIndexMap.put(10, 0);

        //Initializes a custom archipelago
        myArchipelago = new Archipelago(whereIsMN, islandsGroupIndexList, currentGroupsIndexMap);

        //Initializes 3 black towers on "adjacent (inside the array)" islands
        myArchipelago.getIslands()[4].setTower(new Tower(Colour.BLACK));
        myArchipelago.getIslands()[5].setTower(new Tower(Colour.BLACK));
        myArchipelago.getIslands()[6].setTower(new Tower(Colour.BLACK));

        //Initializes 1 grey tower before and 2 after the black ones
        myArchipelago.getIslands()[3].setTower(new Tower(Colour.GREY));
        myArchipelago.getIslands()[7].setTower(new Tower(Colour.GREY));
        myArchipelago.getIslands()[8].setTower(new Tower(Colour.GREY));

        //Initializes 1 white tower on the first and last islands
        myArchipelago.getIslands()[0].setTower(new Tower(Colour.WHITE));
        myArchipelago.getIslands()[10].setTower(new Tower(Colour.WHITE));
        myArchipelago.getIslands()[11].setTower(new Tower(Colour.WHITE));
    }

    @After
    public void cleanArchipelago() {
        myArchipelago = null;
    }

    @Test
    public void constructorTest(){
        Integer whereIsMN = 7;

        Integer[] tempIslandsGroupIndexList = {10, 1, 2, 3, 4, 4, 4, 7, 7, 9, 10, 10};
        ArrayList<Integer> islandsGroupIndexList = new ArrayList<>(Arrays.asList(tempIslandsGroupIndexList));

        LinkedHashMap<Integer,Integer> currentGroupsIndexMap = new LinkedHashMap<>();
        currentGroupsIndexMap.put(1, 0);
        currentGroupsIndexMap.put(2, 0);
        currentGroupsIndexMap.put(3, 1);
        currentGroupsIndexMap.put(4, 1);
        currentGroupsIndexMap.put(7, 1);
        currentGroupsIndexMap.put(9, 2);
        currentGroupsIndexMap.put(10, 0);

        assertEquals(islandsGroupIndexList, myArchipelago.getIslandsGroupIndexList());
        assertEquals(currentGroupsIndexMap, myArchipelago.getCurrentGroupsIndexMap());

        assertEquals(Colour.BLACK, myArchipelago.getIslands()[4].getTower().getColour());
        assertEquals(Colour.BLACK, myArchipelago.getIslands()[5].getTower().getColour());
        assertEquals(Colour.BLACK, myArchipelago.getIslands()[6].getTower().getColour());

        assertEquals(Colour.GREY, myArchipelago.getIslands()[3].getTower().getColour());
        assertEquals(Colour.GREY, myArchipelago.getIslands()[7].getTower().getColour());
        assertEquals(Colour.GREY, myArchipelago.getIslands()[8].getTower().getColour());

        assertEquals(Colour.WHITE, myArchipelago.getIslands()[0].getTower().getColour());
        assertEquals(Colour.WHITE, myArchipelago.getIslands()[10].getTower().getColour());
        assertEquals(Colour.WHITE, myArchipelago.getIslands()[11].getTower().getColour());
    }

    @Test
    public void mnPositionTest() {
        //test di funzionalitá
        assertEquals(7, myArchipelago.getMNposition());
        //Movement to "adjacent (inside the array)" islands groups
        int firstMove = 1;
        myArchipelago.moveMN(firstMove);
        assertEquals(9, myArchipelago.getMNposition());

        //Movement to "not adjacent (inside the array)" islands groups
        int secondMove = 5;
        myArchipelago.moveMN(secondMove);
        assertEquals(4, myArchipelago.getMNposition());
    }

    @Test
    public void getIslandsGroupIndexList__correctGroupIndexList() {
        Integer[] tempIslandsGroupIndexList = {10, 1, 2, 3, 4, 4, 4, 7, 7, 9, 10, 10};
        ArrayList<Integer> islandsGroupIndexList = new ArrayList<>(Arrays.asList(tempIslandsGroupIndexList));

        assertEquals(islandsGroupIndexList, myArchipelago.getIslandsGroupIndexList());
    }

    @Test
    public void getCurrentGroupsIndexMap__correctGroupIndexMap() {
        LinkedHashMap<Integer,Integer> currentGroupsIndexMap = new LinkedHashMap<>();
        currentGroupsIndexMap.put(1, 0);
        currentGroupsIndexMap.put(2, 0);
        currentGroupsIndexMap.put(3, 1);
        currentGroupsIndexMap.put(4, 1);
        currentGroupsIndexMap.put(7, 1);
        currentGroupsIndexMap.put(9, 2);
        currentGroupsIndexMap.put(10, 0);

        assertEquals(currentGroupsIndexMap, myArchipelago.getCurrentGroupsIndexMap());
    }

    @Test
    public void islandProhibitionTest() {
        //Testing getProhibition on the same group
        assertTrue(myArchipelago.isIslandProhibited(7));
        assertTrue(myArchipelago.isIslandProhibited(8));
        assertFalse(myArchipelago.isIslandProhibited(1));

        //Testing getProhibition on a different group
        assertFalse(myArchipelago.isIslandProhibited(1));

        //Testing setProhibition by GroupIndex and getProhibitionCounterByIslandIndex
        myArchipelago.addGroupProhibition(10);
        assertEquals(1, myArchipelago.getProhibitionCounterByIslandIndex(10));
        assertEquals(2, myArchipelago.getProhibitionCounterByIslandIndex(9));

        //testing subprohibition by group index
        myArchipelago.subGroupProhibition(9);
        assertEquals(1, myArchipelago.getProhibitionCounterByIslandIndex(9));
    }

    @Test
    public void studentTest(){
        //test addStudentToIsland
        myArchipelago.addStudentToIsland(new Student(Race.FROG), 10);
        myArchipelago.addStudentToIsland(new Student(Race.ELF), 11);
        myArchipelago.addStudentToIsland(new Student(Race.DRAGON), 0);

        //test we have the correct number of students by race on each island
        ArrayList<Integer> CorrectStudentNumByRace = new ArrayList<>(Arrays.asList(0, 0, 0, 1, 0));
        assertEquals(CorrectStudentNumByRace, myArchipelago.getIslandStudentsInfo(10));
        CorrectStudentNumByRace = new ArrayList<>(Arrays.asList(0, 0, 0, 0, 1));
        assertEquals(CorrectStudentNumByRace, myArchipelago.getIslandStudentsInfo(11));
        CorrectStudentNumByRace = new ArrayList<>(Arrays.asList(0, 1, 0, 0, 0));
        assertEquals(CorrectStudentNumByRace, myArchipelago.getIslandStudentsInfo(0));

        //test we have the correct number of student by race on group
        CorrectStudentNumByRace = new ArrayList<>(Arrays.asList(0, 1, 0, 1, 1));
        assertEquals(CorrectStudentNumByRace, myArchipelago.getStudentCounterGroupList(10));
    }

    @Test
    public void GetGroupByIslandIndex__correctGroupByIslandIndex() {
        ArrayList<Island> testGroup = new ArrayList<>();

        //Testing a group of more than one island
        testGroup.add(myArchipelago.getIslands()[7]);
        testGroup.add(myArchipelago.getIslands()[8]);
        assertEquals(testGroup, myArchipelago.getGroupByIslandIndex(7));

        //Clears test group
        testGroup.clear();

        //Testing a "group" of one island
        testGroup.add(myArchipelago.getIslands()[1]);
        assertEquals(testGroup, myArchipelago.getGroupByIslandIndex(1));
    }

    @Test
    public void takeoverTowersTest() {
        ArrayList<Tower> greyTowers = new ArrayList<>();
        greyTowers.add(new Tower(Colour.GREY));
        greyTowers.add(new Tower(Colour.GREY));
        greyTowers.add(new Tower(Colour.GREY));
        ArrayList<Tower> blackTowers = myArchipelago.takeoverTowers(4, greyTowers);

        assertEquals(Colour.BLACK, blackTowers.get(0).getColour());
        assertEquals(Colour.BLACK, blackTowers.get(1).getColour());
        assertEquals(Colour.BLACK, blackTowers.get(2).getColour());

        assertEquals(Colour.GREY, myArchipelago.getIslands()[4].getTower().getColour());
        assertEquals(Colour.GREY, myArchipelago.getIslands()[5].getTower().getColour());
        assertEquals(Colour.GREY, myArchipelago.getIslands()[6].getTower().getColour());

        assertEquals("GREY", myArchipelago.getTowersColourString(4));
        assertEquals("GREY", myArchipelago.getTowersColourString(5));
        assertEquals("GREY", myArchipelago.getTowersColourString(6));

        assertEquals("", myArchipelago.getTowersColourString(9));
    }

    @Test
    public void mergeIslandsTest() {
        ArrayList<Tower> t = new ArrayList<Tower>();
        t.add(new Tower(Colour.GREY));
        t.add(new Tower(Colour.GREY));
        t.add(new Tower(Colour.GREY));
        myArchipelago.takeoverTowers(4, t);
        //Now the updated GroupMap should be:
        LinkedHashMap<Integer,Integer> newCurrentGroupsIndexMap = new LinkedHashMap<>();
        newCurrentGroupsIndexMap.put(1, 0);
        newCurrentGroupsIndexMap.put(2, 0);
        newCurrentGroupsIndexMap.put(3, 3);
        newCurrentGroupsIndexMap.put(9, 2);
        newCurrentGroupsIndexMap.put(10, 0);

        //we expect the IslandGroupIndexList to be as follows after the merge
        Integer[] tempIslandsGroupIndexList1 = {10, 1, 2, 3, 3, 3, 3, 3, 3, 9, 10, 10};
        ArrayList<Integer> newIslandsGroupIndexList = new ArrayList<>(Arrays.asList(tempIslandsGroupIndexList1));

        //Calls MergeIslands function
        myArchipelago.mergeIslands();

        assertEquals(newIslandsGroupIndexList, myArchipelago.getIslandsGroupIndexList());
        assertEquals(newCurrentGroupsIndexMap, myArchipelago.getCurrentGroupsIndexMap());
        assertEquals(3, myArchipelago.getMNposition());
    }

}
