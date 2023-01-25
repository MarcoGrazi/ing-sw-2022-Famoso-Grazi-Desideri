package main.java.com.polimi.client.controllers;

import com.google.gson.Gson;

import com.google.gson.internal.LinkedTreeMap;
import main.java.com.polimi.client.models.Archipelago;
import main.java.com.polimi.client.models.Clouds;
import main.java.com.polimi.client.models.client_states.*;
import main.java.com.polimi.client.utils.ColorStrings;
import main.java.com.polimi.client.utils.Utils;
import main.java.com.polimi.client.views.*;
import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.models.*;
import main.java.com.polimi.client.views.gui.ClientGUIView;
import main.java.com.polimi.client.views.gui.PlayerGuiView;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

/**
 * Client controller class.
 * Manages the client side.
 */
public class ClientController implements Observer, Runnable {
    private String path;
    //Client.Client variables
    private String host;
    private int port;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private boolean running=false;
    private ClientModel clientModel;
    private ClientView clientView;
    private ClientGUIView clientGUIView;
    private String visMode;
    private State state;
    private Player player;
    private School school;
    private AssistantDeck deck;
    private Clouds cloud;
    private Archipelago archipelago;
    private String gameMode;

    /**
     * @param host ip address of the server
     * @param port port exposed by the server
     * @param view CLI client view to observe
     */
    public ClientController(String host, int port, ClientView view){
        this.host=host;
        this.port=port;
        this.visMode="CLI";
        clientView =view;
        clientModel = new ClientModel(view);
        view.addController(this);
        this.state = new NickNameNotSet(this);
    }

    /**
     * @param host ip address of the server
     * @param port port exposed by the server
     * @param view CLI client view to observe
     * @param path where to find the assets
     */
    public ClientController(String host, int port, ClientGUIView view, String path){
        this.path=path;
        this.host=host;
        this.port=port;
        clientGUIView =view;
        this.visMode="GUI";
        clientModel = new ClientModel(view);
        view.addController(this);
        this.state = new NickNameNotSet(this);
    }

    /**
     * creates a new socket with port and host passed in the constructor
     * creates in and out stream to read data from the server and send data to the server
     * starts a new thread for the client
     */
    //connect to the server
    public void connect(){
        try{
            socket=new Socket(host,port);
            out=new ObjectOutputStream(socket.getOutputStream());
            in=new ObjectInputStream(socket.getInputStream());
            //listener=new EventListener();
            new Thread(this).start();
        }catch(ConnectException e){
            this.setState(new ServerDown(this, this.state));
            if(visMode.equals("GUI")){
                this.clientGUIView.serverDown();
            }
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * closes the connection when the server goes down
     * flushes the out stream
     * changes client state to server down so that it can ping the server
     */
    //close the connection
    public void close(){
        try{
            running=false;
            //tell the server we are closing
            /*RemovePlayerPacket packet= new RemovePlayerPacket();
            sendObject(packet);*/
            in.close();
            out.flush();
            out.close();
            socket.close();
            System.out.println("Server down");
            setState(new ServerDown(this, this.state));
            if(visMode.equals("GUI")){
                this.clientGUIView.serverDown();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Packets from gui are verified before sending them through the net
     * @param packet the packet of informations received from the gui
     */
    public void guiHandle(Packet packet) {
        try {
            state.verify(packet);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param o object to send to the server
     * @return packet sent
     */
    //send data to the server
    public Packet sendObject(Packet o){
        Gson gson = new Gson();
        String json = gson.toJson(o);
        try{
            out.writeObject(json);
            out.flush();
        }catch(IOException e){
            e.printStackTrace();
        }
        return o;
    }

    /**
     * starts the thread, listens for server data
     */
    @Override
    public void run() {
        System.out.println("thread started");
        try{
            running=true;
            while(running){
                //listen for new data
                try{
                    Object data=in.readObject();
                    System.out.println(data.toString());
                    Gson gson = new Gson();
                    Packet packet = gson.fromJson((String)data,Packet.class);
                    try {
                        state.handle(packet);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }catch (EOFException e){
                    close();
                }
                //listener.received(data);
            }
        }catch (ClassNotFoundException e){
            //If i am unable to get the object
            close();
            e.printStackTrace();
        }catch (SocketException e){
            //Problem with connection
            close();
            //e.printStackTrace();
        } catch (IOException e){
            close();
            e.printStackTrace();
        }
    }

    /**
     * sets the state of the client
     * @param state new State of the client
     */
    public void setState(State state){
        this.state=state;
    }

    /**
     * @param state
     */
    public void resetState(State state) {
        state.reset();
        this.state=state;
    }

    /**
     * sets the game mode
     * @param mode the mode
     */
    public void setMode(String mode) {
        this.gameMode = mode;
    }

    /**
     * @return the current game mode, "S" for simple or "E" for expert
     */
    public String getMode() {
        return gameMode;
    }

    /**
     * @return the client model
     */
    public ClientModel getClientModel() {
        return clientModel;
    }

    /**
     * @return the client view
     */
    public ViewInterface getClientView() {
        if(visMode.equals("CLI"))
            return this.clientView;
        else
            return this.clientGUIView;
    }

    @Override
    public void update(Observable o, Object arg) {

        try {
            state.verify((Packet)arg);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param nickname sets player nickname
     */
    public void setNickName(String nickname) {
        clientModel.setNickname(nickname);
    }

    /**
     * @param gameId sets player game is
     */
    public void setGameId(int gameId) {
        clientModel.setGameId(gameId);
    }

    /**
     * @param playerId id of the player
     * @param playerMage mage chosen
     * @param playerColour color chosen for the game
     * @param player_number
     */
    public void createPlayer(int playerId, String playerMage, String playerColour, int player_number) {
        Mage mage= Utils.getMageFromString(playerMage);
        Colour colour = Utils.getColourFromString(playerColour);

        if(visMode.equals("CLI")){
            PlayerView playerView = new PlayerView(this);

            this.player = new Player(playerId,mage,colour, playerView, gameMode);
        }else {
            PlayerGuiView playerGuiView= new PlayerGuiView(this, playerId, gameMode.equals("S"), clientModel.getGameId(), this.path);
            clientGUIView.switchToGamePanel(playerGuiView);
            this.player = new Player(playerId,mage,colour, playerGuiView, gameMode);
        }

        this.getClientModel().setInAGame(true);
    }

    /**
     * initializes the deck for the game
     */
    public void createDeck(){
        if(visMode.equals("CLI")){
            AssistantDeckView assistantDeckView= new AssistantDeckView(this);
            this.deck= new AssistantDeck(assistantDeckView);

        }else{
            this.deck= new AssistantDeck(clientGUIView.getPlayerView());
        }

    }

    /**
     * @param playerColour tower colour chosen by the player
     * @param player_number number of players in the game
     * @param stud students in the hall
     * @param cloud_view students in each cloud
     */
    public void createSchool(String playerColour, int player_number, LinkedTreeMap<String, Object> stud, LinkedTreeMap<String, Object> cloud_view){
        Colour colour = Utils.getColourFromString(playerColour);

        ArrayList<Tower> towers = new ArrayList<>();
        if(player_number==2){
            for (int i =0; i<8 ; i++){
                towers.add(new Tower(colour));
            }
        }else{
            for (int i =0; i<6 ; i++){
                towers.add(new Tower(colour));
            }
        }
        if(visMode.equals("CLI")){
            SchoolView schoolView = new SchoolView(this);
            this.school= new School(towers, (ArrayList<Double>) stud.get("students_hall"),schoolView);
            CloudView cloudView = new CloudView();
            this.cloud= new Clouds(cloud_view, cloudView);

        }else{
            this.school= new School(towers, (ArrayList<Double>) stud.get("students_hall"),clientGUIView.getPlayerView());
            this.cloud= new Clouds(cloud_view, clientGUIView.getPlayerView());
        }


    }

    /**
     * @return the player deck
     */
    public AssistantDeck getDeck() {
        return this.deck;
    }

    /**
     * @param stud student to add to the table
     * @param table contains an array with the ids of the player that owns each professor
     * @param std_i index of the student added to the table
     * @param player_id id of the player
     */
    public void updateSchool(LinkedTreeMap<String, Object> stud, LinkedTreeMap<String, Object> table, int std_i, int player_id) {

        //add a student to the right table
        Race race = Utils.getRaceFromString(stud.get("stud_race").toString());
        school.addToTable(race);

        //set the right professor to true
        ArrayList<Double> professors = (ArrayList<Double>) table.get("prof_schoolIds");

        this.updateProfessors(professors, player_id);
        //remove the student from the hall
        ArrayList<Student> hallToSet = this.school.getStudentsInHall();
        hallToSet.remove(std_i);
        this.school.setHall(hallToSet);
        school.printSchool();



    }


    /**
     * @param professors Arraylist of player ids: for each of the 5 professors there is the player id of the player who owns it or -1 if nobody owns it
     * @param player_id id of the player
     */
    public void updateProfessors(ArrayList<Double> professors, int player_id){
        ArrayList<String> races = Utils.getRaceString();
        for(int i =0; i<races.size(); i++){
            if((int) Math.round(Float.parseFloat(professors.get(i).toString()))==player_id){
                school.setProfessorToTrue(Utils.getRaceFromString(races.get(i)));
            }else{
                school.setProfessorToFalse(Utils.getRaceFromString(races.get(i)));
            }
        }
    }

    /**
     * reset the school after the server is up again
     * @param schoolView school view with professors, hall and tables
     * @param player_id id if the player
     */
    public void resetSchool(LinkedTreeMap<String, Object> schoolView, int player_id){
        this.school.clearTables();
        ArrayList<Double> tables = (ArrayList<Double>) schoolView.get("tables");
        for(int i =0; i<Utils.raceArray.size(); i++){
            for(int j = 0; j<Math.round(tables.get(i)); j++){
                this.school.addToTable(Utils.raceArray.get(i));
            }

        }
        ArrayList<Double> professors = (ArrayList<Double>) schoolView.get("prof_schoolIds");
        for(int i =0; i<Utils.raceArray.size(); i++){
            if((int) Math.round(Float.parseFloat(professors.get(i).toString()))==player_id){
                school.setProfessorToTrue(Utils.raceArray.get(i));
            }else{
                school.setProfessorToFalse(Utils.raceArray.get(i));
            }
        }
        ArrayList<Double> hall = (ArrayList<Double>) schoolView.get("students_hall");
        ArrayList<Student> hallToSet = new ArrayList<>();
        for(int i =0; i<hall.size(); i++){
            for(int j =0; j<Math.round(hall.get(i)); j++){
                hallToSet.add(new Student(Utils.raceArray.get(i)));
            }
        }
        this.school.setHall(hallToSet);


        Double towers = (Double) schoolView.get("towers");
        ArrayList<Tower> towersToSet = new ArrayList<>();
        for(int i =0; i<Math.round(towers); i++){
            towersToSet.add(new Tower(this.school.getTowerColour()));
        }
        this.school.setTowers(towersToSet);
        school.printSchool();


    }


    /**
     * creates an archipelago with the initialization data passed from the server
     * @param archipelagoView archipelago hashmap passed from the server
     */
    public void createArchipelago(LinkedTreeMap<String, Object> archipelagoView) {
        if(visMode.equals("CLI")){
            this.archipelago = new Archipelago(archipelagoView, new ArchipelagoView(this));

        }else{
            this.archipelago = new Archipelago(archipelagoView, clientGUIView.getPlayerView());
        }

    }

    /**
     * removesa student that has been added to an island from the hall
     * @param std_i student index added to an island
     */
    public void updateHallIsland( int std_i) {
        ArrayList<Student> hallToSet = this.school.getStudentsInHall();
        hallToSet.remove(std_i);
        this.school.setHall(hallToSet);
        school.printSchool();

    }

    /** updates the archipelago with the data sent from server
     * @param archipelago_view archipelago data from server
     */
    public void updateArchipelago(LinkedTreeMap<String, Object> archipelago_view) {
        this.archipelago.updateArchipelago(archipelago_view);
    }

    /**
     * adds students from cloud to hall
     * @param hall array containing number of students for each race in the hall
     * @param cloudId cloud from wich to get the students
     */
    public void updateHall(ArrayList<Double> hall, int cloudId) {
        ArrayList<Student> hallToSet = new ArrayList<>();
        for (int j =0; j <Utils.raceArray.size(); j++){
            for(int i =0; i< Math.round(hall.get(j)); i++){
                hallToSet.add(new Student(Utils.raceArray.get(j)));
            }
        }
        this.school.setHall(hallToSet);
        if(cloudId!=-1){
            updateClouds(cloudId);
        }
    }

    /**
     * clears a cloud given the id
     * @param cloudId cloud id to clear
     */
    public void updateClouds(int cloudId) {
        this.cloud.clearCloud(cloudId);
    }

    /**
     * resets the clouds after server is up again
     * @param cloud_view cloud data sent from server
     */
    public void resetClouds(LinkedTreeMap<String, Object> cloud_view) {
        this.cloud.updateClouds(cloud_view);
    }

    /**
     * adds or subtracts towers from the school
     * @param towerQuantity tower quantity to add or subtract
     * @param action action to perform
     */
    public void setSchoolTowers(int towerQuantity, String action) {
        ArrayList<Tower> oldT = this.school.getTowers();

        if(action.equals("SUB") && towerQuantity > oldT.size()) {
            oldT.clear();
        } else {
            for(int i=0; i<towerQuantity; i++){
                if(action.equals("SUB")){
                    oldT.remove(0);
                }else{
                    oldT.add(new Tower(this.getPlayer().getSchoolColour()));
                }
            }
        }

        this.school.setTowers(oldT);
        school.printSchool();
    }

    /**
     * resets the deck after server down
     * @param deck_view deck data from server
     */
    public void resetDeck(LinkedTreeMap<String, Object> deck_view) {
        ArrayList<Double> deck = (ArrayList<Double>) deck_view.get("assistantsHand");
        ArrayList<Integer> deckToSet = new ArrayList<>();
        for (int i =0; i<deck.size(); i++){
            deckToSet.add((int) Math.round(deck.get(i)));
        }
        this.deck.setDeck(deckToSet);
        ArrayList<Double> discardPile = (ArrayList<Double>) deck_view.get("assistantDiscardPile");
        ArrayList<Integer> discardPileToSet = new ArrayList<>();
        for (int i =0; i<discardPile.size(); i++){
            discardPileToSet.add((int) Math.round(deck.get(i)));
        }
        this.deck.setDiscardPile(discardPileToSet);
    }

    /**
     * @return current player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @param cost subtracts the cost to the current coins
     */
    public void updateCoins(int cost) {
        this.player.setCoinCounter(player.getCoinCounter()-cost);
    }

    /**
     * Sets a new balance to the player
     * @param amount the player updated balance
     */
    public void updateBalance(int amount) {
        if(this.player.getCoinCounter() != amount) {
            this.player.setCoinCounter(amount);
        }
    }

    /**
     * @param table new table configuration
     */
    public void updateTable(ArrayList<Double> table) {
        this.school.clearTables();
        for(int i =0; i<Utils.raceArray.size(); i++){
            for(int j = 0; j<Math.round(table.get(i)); j++){
                this.school.addToTable(Utils.raceArray.get(i));
            }

        }
    }

    public int getAvGroupsNumber() {
        return this.archipelago.getCurrentGroupsIndexMap().keySet().size();
    }

    /**
     * tells the view to print the school
     */
    public void printSchool() {
        this.school.printSchool();
    }

    /**
     * @return returns the visulization mode
     */
    public String getVisMode() {
        return this.visMode;
    }

    /**
     * initializes the character panel in the gui
     */
    public void initializeCharacterPanel() {
        this.clientGUIView.getPlayerView().initializeCharacterPanel();
    }

    /**
     * tells the view to print the player
     */
    public void printPlayer() {
        if(visMode.equals("GUI")){
            this.clientGUIView.switchToGamePanel(clientGUIView.getPlayerView());
        }else {
            player.printPlayer(getMode());
        }
    }

    /**
     * prints waiting screen when a plyer is waiting for others to rejoin
     */
    public void printGameRejoined() {
        if(visMode.equals("GUI")){
            this.clientGUIView.switchToRejoinPanel();
        }else {
            clientView.printMessage("Wait for players to rejoin");
        }
    }

    /**
     * @return path of graphical assets
     */
    public String getPath() {
        return path;
    }
}
