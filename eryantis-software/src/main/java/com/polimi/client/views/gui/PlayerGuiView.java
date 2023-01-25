package main.java.com.polimi.client.views.gui;

import com.google.gson.internal.LinkedTreeMap;
import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.models.*;
import main.java.com.polimi.client.utils.Utils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;

public class PlayerGuiView extends Observable implements Observer {
    JTabbedPane mainPanel;
    JPanel schoolDeckPlayerPanel;
    JPanel playerInfoPanel;
    JPanel charactersPanel;
    JPanel archipelagoAndCloudsPanel;
    JPanel archipelagoPanel;
    JPanel cloudsPanel;
    JLabel player;
    CircularImageLabel mageName;
    CircularImageLabel colour;
    JLabel coins;
    JPanel schoolPanel;
    JPanel deckPanel;
    JLabel assistantLabel;
    JLabel phase;
    JButton planningPhaseButton;
    JPanel characterHandPanel;
    JLabel characterLabel;
    JButton charactersButton;
    private int[] x;
    private int[] y;
    private int[] w;
    private int[] v;
    private int chosenAssistId;
    private int chosenCharacterId;
    private final ClientController controller;
    private final int playerId;
    private final int gameId;
    private int numberOfPlayers;
    private boolean movedMN;
    public final ArrayList<Race> orderedRaceArr = new ArrayList<>(Arrays.asList( Race.FROG,Race.DRAGON, Race.ELF,Race.FAIRY, Race.UNICORN ));
    private int stuInSelChar;
    private String path;

    /**
     * @param observer observer controller
     * @param playerId playing player id
     * @param easy game mode boolean
     * @param gameId current game id
     * @param path assets path
     */
    public PlayerGuiView(ClientController observer, int playerId, boolean easy, int gameId, String path){
        this.path=path;
        this.controller = observer;
        this.playerId = playerId;
        this.gameId=gameId;
        this.chosenAssistId = -1;
        this.movedMN = false;

        mainPanel= new JTabbedPane();

        mageName= new CircularImageLabel(60, 60, Color.WHITE, this.path);
        mageName.setPreferredSize(new Dimension(60,60));
        mageName.setMinimumSize(new Dimension(60,60));
        mageName.setMaximumSize(new Dimension(60,60));

        player=new JLabel();
        player.setMinimumSize(new Dimension(60,60));
        player.setFont(new Font("Serif", Font.PLAIN, 20));

        phase =new JLabel();
        phase.setMinimumSize(new Dimension(60,60));
        phase.setFont(new Font("Serif", Font.PLAIN, 20));

        colour=new CircularImageLabel(50,50, Color.WHITE, this.path);
        colour.setPreferredSize(new Dimension(50,50));
        colour.setMinimumSize(new Dimension(50,50));
        colour.setMaximumSize(new Dimension(50,50));

        playerInfoPanel = new JPanel();
        playerInfoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5));
        playerInfoPanel.setPreferredSize(new Dimension(1200, 70));
        playerInfoPanel.setMinimumSize(new Dimension(1200, 70));
        playerInfoPanel.setMaximumSize(new Dimension(1200, 70));
        playerInfoPanel.setBackground(new Color(247, 232, 190));
        playerInfoPanel.add(mageName);
        playerInfoPanel.add(player);
        playerInfoPanel.add(colour);
        playerInfoPanel.add(phase);

        schoolDeckPlayerPanel =new JPanel();
        schoolDeckPlayerPanel.setLayout(new BoxLayout(schoolDeckPlayerPanel, BoxLayout.Y_AXIS));
        schoolDeckPlayerPanel.add(playerInfoPanel);
        playerInfoPanel.setAlignmentX(playerInfoPanel.LEFT_ALIGNMENT);


        mainPanel.addTab("Player", schoolDeckPlayerPanel);
        if(!easy){

            coins=new JLabel("Coins:");
            playerInfoPanel.add(coins);

        }

        archipelagoAndCloudsPanel = new JPanel();
        archipelagoAndCloudsPanel.setLayout(null);
        archipelagoAndCloudsPanel.setBackground(new Color(105,186,233));
        mainPanel.add("Archipelago", archipelagoAndCloudsPanel);
        addObserver(observer);
    }

    /**
     * @return main panel of the view
     */
    public JTabbedPane getMainPanel() {
        return mainPanel;
    }

    /**
     * @param player current player id
     * @param gameMode game mode
     */
    public void printPlayer(Player player, String gameMode){
        this.player.setText("Player " + String.valueOf(player.getPlayerId()));

        String mage = player.getMageName().toString();
        ImageIcon mageIcon= Utils.getImageIconFromPath(this.path,mage+".png");

        Image newImg = mageIcon.getImage().getScaledInstance(60, 60,  java.awt.Image.SCALE_SMOOTH);
        mageIcon = new ImageIcon(newImg);

        mageName.setMageIcon(mageIcon.getImage());

        String colourS = player.getSchoolColour().toString();
        switch (colourS) {
            case "WHITE":
                colour.setColour(Color.WHITE);
                break;
            case "BLACK":
                colour.setColour(Color.BLACK);
                break;
            case "GREY":
                colour.setColour(Color.GRAY);
                break;
        }

        if(gameMode.equals("E")){
            coins.setText("Coins: " + String.valueOf(player.getCoinCounter()));
            coins.setFont(new Font("Serif", Font.PLAIN, 20));
        }

        mainPanel.setSelectedIndex(0);
    }

    /**
     * @param s school to print
     */
    public void setupSchool(School s){
        Image school =Utils.getImageFromPath(this.path, "SchoolPlank.png");

        schoolPanel=new BackgroundPanel(school);
        schoolPanel.setPreferredSize(new Dimension(1200, 575));
        schoolPanel.setMaximumSize(new Dimension(1200, 575));
        schoolPanel.setLayout(null);

        int k=0;
        for(Tower t: s.getTowers() ){

            CircularImageLabel tower;
            switch (t.getColour().name()) {
                case "WHITE":
                    tower = new CircularImageLabel(128, 128, Color.WHITE, this.path);
                    tower.setBounds(Utils.TOWER_COORDS[k][1], Utils.TOWER_COORDS[k][0],128,128);
                    schoolPanel.add(tower);
                    break;
                case "BLACK":
                    tower = new CircularImageLabel(128, 128, Color.BLACK, this.path);
                    tower.setBounds(Utils.TOWER_COORDS[k][1], Utils.TOWER_COORDS[k][0],128,128);
                    schoolPanel.add(tower);
                    break;
                case "GREY":
                    tower = new CircularImageLabel(128, 128, Color.GRAY, this.path);
                    tower.setBounds(Utils.TOWER_COORDS[k][1], Utils.TOWER_COORDS[k][0],128,128);
                    schoolPanel.add(tower);
                    break;
            }
            k++;
        }
        k=0;
        for(Student stud: s.getStudentsInHall()){
            Image student = Utils.getImageFromPath(this.path,"stud-"+stud.getColour()+".png" );

            CircularImageLabel studLabel = new CircularImageLabel(student, 50, 50, this.path);
            studLabel.setIndex(k);
            studLabel.setBounds(Utils.HALL_COORDS[k][1], Utils.HALL_COORDS[k][0],50,50);
            studLabel.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    int chosenStudId = studLabel.getIndex();
                    MoveStudentDialog dialog = new MoveStudentDialog(controller, playerId, chosenStudId, controller.getAvGroupsNumber(),gameId);
                }
            });

            schoolPanel.add(studLabel);
            k++;
        }
        setTowers(s);
        setStudentsInHall(s);

        schoolDeckPlayerPanel.add(schoolPanel);
        schoolPanel.setAlignmentX(schoolPanel.LEFT_ALIGNMENT);
        schoolDeckPlayerPanel.add(Box.createRigidArea(new Dimension(0,55)));
    }

    /**
     * prints the deck of the player
     */
    private void setupDeck() {
        JPanel handPanel = new JPanel();
        handPanel.setLayout(new GridLayout(2,5));

        assistantLabel = new JLabel("You haven't played an assistant yet");
        assistantLabel.setFont(new Font("Serif", Font.PLAIN, 20));

        for (int i=1; i<=10; i++){
            Image assistant =Utils.getImageFromPath(this.path,"assistants/Assistant-"+i+".png" );

            IndexPanel assistantPanel= new IndexPanel(assistant, i, 159, 233);
            assistantPanel.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    chosenAssistId = assistantPanel.getIndex();
                    assistantLabel.setText("Chosen assistant: " + chosenAssistId);
                }
            });
            handPanel.add(assistantPanel);
        }

        planningPhaseButton = new JButton("Confirm Choice");
        planningPhaseButton.setFocusable(false);
        planningPhaseButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 238, 255), 5),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)));
        planningPhaseButton.setFont(new Font("Serif", Font.PLAIN, 15));
        planningPhaseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(chosenAssistId != -1 && planningPhaseButton.getText().equals("Confirm Choice")) {
                    Packet packet = new Packet("PLAY_ASSISTANT", playerId, gameId);
                    packet.addToPayload("assistant", chosenAssistId);
                    controller.guiHandle(packet);
                } else if (chosenAssistId != -1 && planningPhaseButton.getText().equals("End Planning")) {
                    Packet packet = new Packet("END_PLANNING", playerId, gameId);
                    controller.guiHandle(packet);
                } else if (chosenAssistId == -1) {
                    String message = "You must play an assistant to continue";
                    new MessageDialog(path ,message, new Color(207, 14, 14));
                }
            }
        });
        planningPhaseButton.setVisible(false);

        deckPanel= new JPanel();
        deckPanel.setBackground(new Color(247, 232, 190));
        deckPanel.setLayout(new BorderLayout());
        deckPanel.setBorder(new EmptyBorder(60, 195, 100, 195));
        deckPanel.add(assistantLabel, BorderLayout.NORTH);
        deckPanel.add(handPanel, BorderLayout.CENTER);
        deckPanel.add(planningPhaseButton, BorderLayout.SOUTH);

        mainPanel.add(deckPanel,"Deck");
    }

    /**
     * @param clouds
     * setups and prints the clouds
     */
    private void setupClouds(Clouds clouds) {
        this.numberOfPlayers = clouds.getClouds().keySet().size();
        cloudsPanel = new JPanel();
        cloudsPanel.setOpaque(false);

        int i=1;
        for(int key : clouds.getClouds().keySet()){
            JButton cloudChoice = new JButton(String.valueOf(i));
            cloudChoice.setFocusable(false);
            cloudChoice.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(204, 238, 255), 5),
                    BorderFactory.createEmptyBorder(10, 5, 10, 5)));
            cloudChoice.setFont(new Font("Serif", Font.PLAIN, 15));
            cloudChoice.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Packet packet = new Packet("GET_STUDENTS", playerId, gameId);
                    packet.addToPayload("cloud", cloudChoice.getText());

                    controller.guiHandle(packet);
                }
            });

            Image cloud = Utils.getImageFromPath(this.path, "Cloud"+ numberOfPlayers +".png");

            BackgroundPanel cloudPanel= new BackgroundPanel(cloud);
            cloudPanel.setLayout(null);

            int k =0;
            for(Student s : clouds.getClouds().get(key)){
                Image student = Utils.getImageFromPath(this.path,"stud-"+s.getColour()+".png");

                CircularImageLabel studLabel;

                if(numberOfPlayers == 2) {
                    studLabel = new CircularImageLabel(student, 40, 40, this.path);
                    studLabel.setBounds(Utils.STU_IN_CL2_COORDS[k][1],Utils.STU_IN_CL2_COORDS[k][0],40,40);
                } else {
                    studLabel = new CircularImageLabel(student, 30, 30, this.path);
                    studLabel.setBounds(Utils.STU_IN_CL3_COORDS[k][1],Utils.STU_IN_CL3_COORDS[k][0],30,30);
                }

                cloudPanel.add(studLabel);
                k++;
            }
            cloudChoice.setBounds(Utils.CLOUD_COORDS[key-1][0] + 30, Utils.CLOUD_COORDS[key-1][1],40,40);
            cloudPanel.setBounds(Utils.CLOUD_COORDS[key-1][0], Utils.CLOUD_COORDS[key-1][1],141,141);
            cloudsPanel.add(cloudPanel);
            cloudsPanel.add(cloudChoice);
            i++;
        }
        cloudsPanel.setBounds(0,0 ,200, 700);
        cloudsPanel.setBorder(new EmptyBorder(60, 20, 60, 0));
        archipelagoAndCloudsPanel.add(cloudsPanel);
    }

    /**
     * @param arch
     * setups and prints the archipelago
     */
    private void setupArch(Archipelago arch) {
        archipelagoPanel = new JPanel();
        archipelagoPanel.setBackground(new Color(105,186,233));
        archipelagoPanel.setLayout(null);
        getPoints(450,257,257,12);

        int mn_pos = arch.getMNposition();

        int k=1;
        for(int i =0; i<12; i++) {
            JLabel islandIndex = new JLabel(String.valueOf(i+1));
            islandIndex.setFont(new Font("Serif", Font.PLAIN, 15));

            Image isl= Utils.getImageFromPath(this.path, "is-"+k+".png");

            BackgroundPanel islandPanel= new BackgroundPanel(isl);
            islandPanel.setLayout(null);

            if(mn_pos==i){
                Image mn = Utils.getImageFromPath(this.path,"MOTHERNATURE.png");

                CircularImageLabel mn_label= new CircularImageLabel(mn,40,40, this.path);
                mn_label.setBounds(Utils.MN_IN_IS_COORDS[1], Utils.MN_IN_IS_COORDS[0],40,40);
                islandPanel.add(mn_label);
            }

            int h=0;
            for (Race r : Utils.raceArray) {
                int nums= arch.getIslands()[i].getStudentNumByRace(r);

                if(nums > 0) {
                    Image student = Utils.getImageFromPath(this.path, "stud-"+r.name()+".png");

                    CircularImageLabel studLabel = new CircularImageLabel(student, 25, 25, this.path);
                    studLabel.setBounds(Utils.STU_IN_IS_COORDS[h][1], Utils.STU_IN_IS_COORDS[h][0],25,25);

                    if(nums > 1) {
                        studLabel.setText(String.valueOf(nums));
                    }

                    islandPanel.add(studLabel);
                    h++;
                }
            }

            islandIndex.setBounds(w[i], v[i], 50,50);
            islandPanel.setBounds(x[i], y[i], 100, 100);
            archipelagoPanel.add(islandPanel);
            archipelagoPanel.add(islandIndex);
            k++;

            if(k>3){
                k=1;
            }
        }
        archipelagoPanel.setBounds(200,0 ,1000, 700);
        archipelagoAndCloudsPanel.add(archipelagoPanel);
    }

    /**
     * @param deck
     * updates the deck view
     */
    private void updateDeck(AssistantDeck deck) {
        JPanel handPanel= (JPanel) deckPanel.getComponent(1);
        for (Component p: handPanel.getComponents()){
            try {
                IndexPanel ip= (IndexPanel) p;
                if(!deck.getAssistantsHand().contains(ip.getIndex())){
                    handPanel.remove(p);
                }
            }catch (ClassCastException e){

            }
        }

        if(chosenAssistId !=-1) {
            assistantLabel.setText("Played assistant: " + chosenAssistId);
            planningPhaseButton.setText("End Planning");
        }
    }

    /**
     * @param arch
     * updates the archipelago view
     */
    private void updateArch(Archipelago arch) {
        archipelagoPanel.removeAll();

        int groupsNumber = arch.getCurrentGroupsIndexMap().keySet().size();
        getPoints(550,257,257, groupsNumber);
        int mn_pos = arch.getMNposition();

        int k=1;
        int i=1;
        for(Integer groupIndex: arch.getCurrentGroupsIndexMap().keySet()) {
            JLabel islandIndex = new JLabel(String.valueOf(i));

            ArrayList<Island> islandGroup = arch.getGroupByIslandIndex(groupIndex);

            Image isl = Utils.getImageFromPath(this.path, "is-"+k+".png");

            BackgroundPanel islandPanel= new BackgroundPanel(isl);
            islandPanel.setVisible(true);
            islandPanel.setLayout(null);

            //Mother nature
            if(mn_pos == groupIndex){
                Image mn = Utils.getImageFromPath(this.path, "MOTHERNATURE.png");

                CircularImageLabel mn_label= new CircularImageLabel(mn,40,40, this.path);
                mn_label.setBounds(Utils.MN_IN_IS_COORDS[1], Utils.MN_IN_IS_COORDS[0],40,40);
                islandPanel.add(mn_label);
            }

            //Counting and adding towers
            String towerColour = arch.getTowersColourString(groupIndex);
            if(!towerColour.equals("")) {
                int nums = islandGroup.size();

                CircularImageLabel tower;
                switch (towerColour) {
                    case "WHITE":
                        tower = new CircularImageLabel(60, 60, Color.WHITE, this.path);
                        tower.setText(String.valueOf(nums));
                        tower.setBounds(78,38 ,60,60);
                        islandPanel.add(tower);
                        break;
                    case "BLACK":
                        tower = new CircularImageLabel(60, 60, Color.BLACK, this.path);
                        tower.setText(String.valueOf(nums));
                        tower.setTextColour(Color.WHITE);
                        tower.setBounds(78,38 ,60,60);
                        islandPanel.add(tower);
                        break;
                    case "GREY":
                        tower = new CircularImageLabel(60, 60, Color.GRAY, this.path);
                        tower.setText(String.valueOf(nums));
                        tower.setBounds(78,38 ,60,60);
                        islandPanel.add(tower);
                        break;
                }
            }

            if(arch.isIslandProhibited(groupIndex)) {
                int prohibitions = arch.getProhibitionCounterByIslandIndex(groupIndex);
                Image prohibition=Utils.getImageFromPath(this.path, "prohibition.png");

                CircularImageLabel prohibLabel = new CircularImageLabel(prohibition, 45, 45, this.path);
                prohibLabel.setText(String.valueOf(Math.round(prohibitions)));
                prohibLabel.setBounds(34, 34,45,45);
                islandPanel.add(prohibLabel);
            }

            //Counting students
            ArrayList<Integer> studentsCounter = arch.getStudentCounterGroupList(groupIndex);
            int h=0;
            for (Race r : Utils.raceArray) {
                int nums= studentsCounter.get(Utils.raceArray.indexOf(r));

                if(nums > 0) {
                    Image student=Utils.getImageFromPath(this.path, "stud-"+r.name()+".png");

                    CircularImageLabel studLabel = new CircularImageLabel(student, 25, 25, this.path);
                    studLabel.setBounds(Utils.STU_IN_IS_COORDS[h][1], Utils.STU_IN_IS_COORDS[h][0],25,25);

                    if(nums > 1) {
                        studLabel.setText(String.valueOf(nums));
                    }

                    islandPanel.add(studLabel);
                    h++;
                }
            }

            islandIndex.setBounds(w[i-1], v[i-1], 50,50);
            islandPanel.setBounds(x[i-1], y[i-1], 100, 100);
            archipelagoPanel.add(islandPanel);
            archipelagoPanel.add(islandIndex);
            k++;

            if(k>3){
                k=1;
            }
            i++;
        }

        archipelagoPanel.validate();
        archipelagoPanel.repaint();

        mainPanel.setSelectedIndex(1);
    }

    /**
     * @param school
     * updates the school model
     */
    private void updateSchool(School school) {
        this.schoolPanel.removeAll();
        setTowers(school);
        setStudentsInHall(school);
        setTables(school);
        setProfTables(school);
    }

    /**
     * @param s
     * updates school tables
     */
    private void setTables(School s){
        for(Race r: orderedRaceArr){
            int index = orderedRaceArr.indexOf(r);

            for(int i =0; i<s.getStudentsInTable(r); i++){
                Image student = Utils.getImageFromPath(this.path, "stud-"+r.getColour()+".png");

                CircularImageLabel studLabel = new CircularImageLabel(student, 50, 50, this.path);
                studLabel.setBounds(Utils.baseX + i * Utils.distX, Utils.BASE_Y_COORDS[index],50,50);
                schoolPanel.add(studLabel);
            }
        }

        schoolPanel.validate();
        schoolPanel.repaint();
    }

    /**
     * @param s
     * updates professor tables
     */
    private void setProfTables(School s){
        for (Race r : orderedRaceArr){
            int index = orderedRaceArr.indexOf(r);

            if(s.getProfessorInTable(r)){
                Image prof= Utils.getImageFromPath(this.path, "prof-"+r.getColour()+".png");

                CircularImageLabel profLabel = new CircularImageLabel(prof, 50, 50, this.path);
                profLabel.setBounds(Utils.baseXProf, Utils.BASE_Y_COORDS[index],50,50);
                schoolPanel.add(profLabel);

            }
        }

        schoolPanel.validate();
        schoolPanel.repaint();
    }

    /**
     * @param s
     * updates towers of the school
     */
    private void setTowers(School s){
        int k=0;
        for(Tower t: s.getTowers() ){

            CircularImageLabel tower;
            switch (t.getColour().name()) {
                case "WHITE":
                    tower = new CircularImageLabel(128, 128, Color.WHITE, this.path);
                    tower.setBounds(Utils.TOWER_COORDS[k][1], Utils.TOWER_COORDS[k][0],128,128);
                    schoolPanel.add(tower);
                    break;
                case "BLACK":
                    tower = new CircularImageLabel(128, 128, Color.BLACK, this.path);
                    tower.setBounds(Utils.TOWER_COORDS[k][1], Utils.TOWER_COORDS[k][0],128,128);
                    schoolPanel.add(tower);
                    break;
                case "GREY":
                    tower = new CircularImageLabel(128, 128, Color.GRAY, this.path);
                    tower.setBounds(Utils.TOWER_COORDS[k][1], Utils.TOWER_COORDS[k][0],128,128);
                    schoolPanel.add(tower);
                    break;
            }
            k++;
        }

        schoolPanel.validate();
        schoolPanel.repaint();
    }

    /**
     * @param s
     * updates the students in the hall
     */
    private void setStudentsInHall(School s){
        int k=0;
        for(Student stud: s.getStudentsInHall()){
            Image student = Utils.getImageFromPath(this.path, "stud-"+stud.getColour()+".png");

            CircularImageLabel studLabel = new CircularImageLabel(student, 50, 50, this.path);
            studLabel.setIndex(k);
            studLabel.setBounds(Utils.HALL_COORDS[k][1], Utils.HALL_COORDS[k][0],50,50);
            studLabel.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    int chosenStudId = studLabel.getIndex();
                    MoveStudentDialog dialog = new MoveStudentDialog(controller, playerId, chosenStudId, controller.getAvGroupsNumber(),gameId);
                }
            });

            schoolPanel.add(studLabel);
            k++;
        }

        schoolPanel.validate();
        schoolPanel.repaint();

        switch (numberOfPlayers) {
            case 2:
                if(k == 4 && !movedMN) {
                    new MotherNatureDialog(controller, playerId, gameId);
                    movedMN = true;
                }
                break;
            case 3:
                if(k == 5 && !movedMN) {
                    new MotherNatureDialog(controller, playerId, gameId);
                    movedMN = true;
                }
                break;
        }
    }

    /**
     * @param clouds
     * updates and reprints the clouds
     */
    private void updateClouds(Clouds clouds) {
        cloudsPanel.removeAll();

        int i=1;
        for(int key : clouds.getClouds().keySet()){
            JButton cloudChoice = new JButton(String.valueOf(i));
            cloudChoice.setFocusable(false);
            cloudChoice.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(204, 238, 255), 5),
                    BorderFactory.createEmptyBorder(10, 5, 10, 5)));
            cloudChoice.setFont(new Font("Serif", Font.PLAIN, 15));
            cloudChoice.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Packet packet = new Packet("GET_STUDENTS", playerId, gameId);
                    packet.addToPayload("cloud", cloudChoice.getText());

                    controller.guiHandle(packet);
                }
            });

            Image cloud = Utils.getImageFromPath(this.path, "Cloud"+ numberOfPlayers +".png");

            BackgroundPanel cloudPanel= new BackgroundPanel(cloud);
            cloudPanel.setLayout(null);

            int k =0;
            for(Student s : clouds.getClouds().get(key)){
                Image student = Utils.getImageFromPath(this.path, "stud-"+s.getColour()+".png");

                CircularImageLabel studLabel;

                if(numberOfPlayers == 2) {
                    studLabel = new CircularImageLabel(student, 40, 40, this.path);
                    studLabel.setBounds(Utils.STU_IN_CL2_COORDS[k][1],Utils.STU_IN_CL2_COORDS[k][0],40,40);
                } else {
                    studLabel = new CircularImageLabel(student, 30, 30, this.path);
                    studLabel.setBounds(Utils.STU_IN_CL3_COORDS[k][1],Utils.STU_IN_CL3_COORDS[k][0],30,30);
                }

                cloudPanel.add(studLabel);
                k++;
            }
            cloudChoice.setBounds(Utils.CLOUD_COORDS[key-1][0] + 30, Utils.CLOUD_COORDS[key-1][1],40,40);
            cloudPanel.setBounds(Utils.CLOUD_COORDS[key-1][0], Utils.CLOUD_COORDS[key-1][1],141,141);
            cloudsPanel.add(cloudPanel);
            cloudsPanel.add(cloudChoice);
            i++;
        }
        cloudsPanel.setBounds(0,0 ,200, 700);
        cloudsPanel.setBorder(new EmptyBorder(60, 20, 60, 0));
        archipelagoAndCloudsPanel.add(cloudsPanel);

    }

    /**
     * @param x0 satrt point x
     * @param y0 start point y
     * @param r radius
     * @param noOfDividingPoints number of points
     * gives equally spaced circular coordinates
     */
    private void getPoints(int x0,int y0,int r,int noOfDividingPoints) {

        double angle = 0;

        x = new int[noOfDividingPoints];
        y = new int[noOfDividingPoints];
        w = new int[noOfDividingPoints];
        v = new int[noOfDividingPoints];

        for(int i = 0 ; i < noOfDividingPoints  ;i++)
        {
            angle = i * (360/noOfDividingPoints);

            x[i] = (int) (x0 + r * Math.cos(Math.toRadians(angle)));
            y[i] = (int) (y0 + r * Math.sin(Math.toRadians(angle)));
            w[i] = (int) (x0 + (r-80) * Math.cos(Math.toRadians(angle))) + 45;
            v[i] = (int) (y0 + (r-80) * Math.sin(Math.toRadians(angle))) + 30;

        }
    }

    /**
     * @param phase
     * prints the palyer phase on the screen
     * conditional rendering of components according to the phase
     */
    public void setPhase(String phase){
        String cleanPhase = phase.substring(15, phase.length()-3);
        this.phase.setText(cleanPhase);

        if(cleanPhase.equals("Blocked")) {
            this.phase.setForeground(new Color(207, 14, 14));
            mainPanel.setSelectedIndex(0);

            if(controller.getMode().equals("E") && charactersButton!=null) {
                charactersButton.setVisible(false);
                charactersPanel.setBorder(new EmptyBorder(60, 195, 90, 195));
            }
        } else {
            this.phase.setForeground(new Color(40, 222, 20));

            if(cleanPhase.equals("Planning Phase")) {
                mainPanel.setSelectedIndex(2);
            } else if (cleanPhase.equals("Action Phase")) {
                mainPanel.setSelectedIndex(0);
            }

            if(controller.getMode().equals("E") && charactersButton!=null) {
                charactersButton.setVisible(true);
                charactersPanel.setBorder(new EmptyBorder(60, 195, 40, 195));
            }
        }

        if(planningPhaseButton!=null) {
            if(cleanPhase.equals("Planning Phase")) {
                movedMN = false;
                chosenAssistId = -1;
                assistantLabel.setText("You haven't played an assistant yet");
                planningPhaseButton.setText("Confirm Choice");
            }

            planningPhaseButton.setVisible(cleanPhase.equals("Planning Phase"));

            if(!planningPhaseButton.isVisible()) {
                deckPanel.setBorder(new EmptyBorder(60, 195, 100, 195));
            } else {
                deckPanel.setBorder(new EmptyBorder(60, 195, 40, 195));
            }
        }

    }

    /**
     * @param message
     * shows the error dialg
     */
    public void printErrorMessage(String message) {
        new MessageDialog(this.path ,message, new Color(207, 14, 14));

        if(message.equals("invalid mother nature's move value")) {
            new MotherNatureDialog(controller, playerId, gameId);
        }
    }

    /**
     * @param message
     * shows message dialog
     */
    public void printMessage(String message) {
        new MessageDialog(this.path,message, Color.BLACK);
    }

    @Override
    public void update(Observable o, Object arg) {
        Message msg = (Message) arg;
        Player player;
        School school;
        AssistantDeck deck;
        Clouds clouds;
        Archipelago arch;

        switch (msg.getAction()){
            case "COIN_COUNTER_CHANGED":
            case "SETUP_E":
                player = (Player) o;
                printPlayer(player, "E");
                break;
            case "SETUP_S":
                player = (Player) o;
                printPlayer(player, "S");
                break;
            case "SETUP_SCHOOL":
                school =(School) o;
                setupSchool(school);
                break;
            case "SETUP_DECK":
                setupDeck();
                break;
            case "SETUP_ARCH":
                arch= (Archipelago) o;
                setupArch(arch);
                break;
            case "SETUP_CLOUDS":
                clouds= (Clouds) o;
                setupClouds(clouds);
                break;
            case "PRINT_DECK":
                deck = (AssistantDeck) o;
                updateDeck(deck);
                break;
            case "UPDATE_ARCH":
                arch= (Archipelago) o;
                updateArch(arch);
                break;
            case "PRINT_SCHOOL":
                school= (School) o;
                updateSchool(school);
                break;
            case "UPDATE_CLOUDS":
                clouds= (Clouds) o;
                updateClouds(clouds);
                break;
            default:
                System.out.println("Player performed an action");
        }
    }

    /**
     * initializes the character panel
     */
    public void initializeCharacterPanel() {

        characterLabel = new JLabel("You haven't played a character yet");
        characterLabel.setFont(new Font("Serif", Font.PLAIN, 20));

        characterHandPanel = new JPanel();
        characterHandPanel.setLayout(new GridLayout(1,3));
        characterHandPanel.setOpaque(false);

        charactersButton = new JButton("Confirm Choice");
        charactersButton.setFocusable(false);
        charactersButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(204, 238, 255), 5),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)));
        charactersButton.setFont(new Font("Serif", Font.PLAIN, 15));
        charactersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(chosenCharacterId!= -1) {
                    switch (chosenCharacterId){
                        case 1:
                            new Effect1Dialog(controller, playerId, gameId);
                            break;
                        case 3:
                        case 5:
                            new ChoseIslandIndexDialog(controller, playerId, gameId, chosenCharacterId);
                            break;
                        case 7:
                            new Effect7Dialog(controller, playerId, gameId);
                            break;
                        case 9:
                        case 12:
                            new ChoseColorDialog(controller, playerId, gameId, chosenCharacterId);
                            break;
                        case 10:
                            new Effect10Dialog(controller, playerId, gameId);
                            break;
                        case 11:
                            new Effect11Dialog(controller, playerId, gameId);
                            break;
                        default:
                            new CharacterDialog(controller, playerId, gameId, chosenCharacterId);
                            break;
                    }
                    characterHandPanel.validate();
                    characterHandPanel.repaint();
                }
            }
        });
        charactersButton.setVisible(false);

        charactersPanel = new JPanel();
        charactersPanel.setBackground(new Color(247, 232, 190));
        charactersPanel.setLayout(new BorderLayout());
        charactersPanel.setBorder(new EmptyBorder(60, 195, 90, 195));
        charactersPanel.add(characterLabel, BorderLayout.NORTH);
        charactersPanel.add(characterHandPanel, BorderLayout.CENTER);
        charactersPanel.add(charactersButton, BorderLayout.SOUTH);

        mainPanel.add(charactersPanel, "Characters");
        mainPanel.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JTabbedPane sourceTabbedPane = (JTabbedPane) e.getSource();
                int index = sourceTabbedPane.getSelectedIndex();
                if(index==3){
                    Packet packet= new Packet("FETCH_CHARACTERS", playerId, gameId);
                    controller.guiHandle(packet);
                }
            }
        });

    }

    /**
     * @param packet
     * prints available characters when given from the server
     */
    public void printCharacters(Packet packet) {
        characterHandPanel.removeAll();

        LinkedTreeMap<String ,Object> characters= (LinkedTreeMap<String, Object>) packet.getFromPayload("characters");
        for (String key: characters.keySet()){
            JPanel characterContainerPanel=new JPanel();
            characterContainerPanel.setLayout(new GridLayout(2,1));
            characterContainerPanel.setOpaque(false);

            LinkedTreeMap<String, Object> characterObject= (LinkedTreeMap<String, Object>)characters.get(key);
            ArrayList<Double> students = (ArrayList<Double>) characterObject.get("students");
            int cost = (int) Math.floor(Float.parseFloat(characterObject.get("cost").toString()));

            Image character = Utils.getImageFromPath(this.path, "characters/char-"+key+".jpg");

            IndexPanel characterPanel= new IndexPanel(character,Integer.parseInt(key),158,240);

            characterPanel.addMouseListener(new MouseAdapter()
            {
                public void mouseClicked(MouseEvent e)
                {
                    chosenCharacterId = characterPanel.getIndex();
                    characterLabel.setText("Chosen character: " + chosenCharacterId + "; Cost: " + cost);
                    if(students!=null){
                        stuInSelChar = students.size();
                    }
                }
            });
            characterContainerPanel.add(characterPanel);

            JPanel studsPanel = new JPanel();
            studsPanel.setLayout(null);
            studsPanel.setOpaque(false);

            if(students!=null){

                int j=0;
                for(int i =0; i<Utils.raceArray.size(); i++){
                    if(students.get(i)>0){
                        Image student = Utils.getImageFromPath(this.path, "stud-"+Utils.raceArray.get(i).getColour()+".png");

                        CircularImageLabel studLabel = new CircularImageLabel(student, 25, 25, this.path);
                        studLabel.setText(String.valueOf(Math.round(students.get(i))));
                        studLabel.setBounds(Utils.STU_IN_CHAR[j][0], Utils.STU_IN_CHAR[j][1],25,25);
                        studsPanel.add(studLabel);

                        j++;
                    }
                }
                characterContainerPanel.add(studsPanel);
            }

            if(characterObject.containsKey("prohibitions")) {
                double prohibitions = (double) characterObject.get("prohibitions");
                Image prohibition = Utils.getImageFromPath(this.path, "prohibition.png");

                CircularImageLabel prohibLabel = new CircularImageLabel(prohibition, 45, 45, this.path);
                prohibLabel.setText(String.valueOf(Math.round(prohibitions)));
                prohibLabel.setBounds(Utils.STU_IN_CHAR[2][0], Utils.STU_IN_CHAR[2][1],45,45);
                studsPanel.add(prohibLabel);

                characterContainerPanel.add(studsPanel);
            }

            characterHandPanel.add(characterContainerPanel);
        }

        characterHandPanel.validate();
        characterHandPanel.repaint();
    }
}
