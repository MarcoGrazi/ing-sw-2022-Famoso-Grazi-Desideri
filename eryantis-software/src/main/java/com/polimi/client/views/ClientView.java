package main.java.com.polimi.client.views;

import com.google.gson.internal.LinkedTreeMap;
import main.java.com.polimi.client.clientPackets.Packet;
import main.java.com.polimi.client.controllers.ClientController;
import main.java.com.polimi.client.models.ClientModel;
import main.java.com.polimi.client.models.Message;
import main.java.com.polimi.client.utils.ColorStrings;
import main.java.com.polimi.client.utils.Utils;

import java.util.*;

public class ClientView extends Observable implements Observer, ViewInterface {
    Scanner sc;
    String input;
    Integer intIn = -1;
    Integer intChoice = -1;
    Integer gameId;
    Integer playerId;
    Packet packet;
    Boolean inAGame = false;
    Integer tempPlayerId;
    boolean isReconnecting = false;

    @Override
    public void addController(ClientController clientController) {

        addObserver(clientController);
    }

    /**
     * listens for user inputs
     */
    public void start(){
        sc= new Scanner(System.in);
        while (true){
            input = sc.nextLine().toLowerCase();
            switch (input){
                case "ping server":
                    packet = new Packet("PING_SERVER");
                    setChanged();
                    notifyObservers(packet);
                    break;
                case "create game":
                    packet = new Packet("CREATE_GAME",playerId);

                    do {
                        System.out.println("Choose game mode: Simple(S) or Expert(E)");
                        input = sc.nextLine().toUpperCase();
                    } while (!input.equals("S") && !input.equals("E"));
                    packet.addToPayload("game_mode", input);

                    do {
                        try {
                            System.out.println("insert player number");
                            intIn = sc.nextInt();
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input format. Please insert a NUMBER.");
                        }
                        sc.nextLine();
                    } while (intIn!=2 && intIn!=3);
                    packet.addToPayload("player_number", 2); intIn = -1;

                    do {
                        System.out.println("insert mage name: JAFAR, MORGANA, MERLIN or WONG");
                        input = sc.nextLine().toUpperCase();
                    } while (!Utils.getMageString().contains(input));
                    packet.addToPayload("mage_name", input);

                    do {
                        System.out.println("insert tower colour: WHITE, BLACK or GREY");
                        input = sc.nextLine().toUpperCase();
                    } while (!Utils.getColourString().contains(input));
                    packet.addToPayload("tower_colour", input);


                    setChanged();
                    notifyObservers(packet);
                    break;
                case "set nickname":
                    if(isReconnecting) {
                        packet = new Packet("SET_NICKNAME",tempPlayerId);
                    } else {
                        packet = new Packet("SET_NICKNAME",playerId);
                    }

                    do {
                        System.out.println("insert nickname");
                        input = sc.nextLine().toLowerCase();
                    } while (input.equals("") || (input.charAt(0) >= '0' && input.charAt(0) <= '9'));
                    packet.addToPayload("nickname", input);

                    setChanged();
                    notifyObservers(packet);
                    break;
                case "reset nickname":
                    packet = new Packet("RESET_NICKNAME",playerId,gameId);

                    System.out.println("insert nickname");
                    input = sc.nextLine();
                    packet.addToPayload("nickname", input);
                    packet.addToPayload("tmpPlId",tempPlayerId);
                    setChanged();
                    notifyObservers(packet);
                    break;
                case "fetch games":
                    packet = new Packet("FETCH_GAMES",playerId);

                    setChanged();
                    notifyObservers(packet);
                    break;
                case "join game":
                    do {
                        try {
                            System.out.println("insert game number");
                            intIn = sc.nextInt();
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input format. Please insert a NUMBER.");
                        }
                        sc.nextLine();
                    } while (intIn < 0);

                    packet = new Packet("JOIN_GAME", playerId, intIn);
                    do {
                        System.out.println("insert mage name: JAFAR, MORGANA, MERLIN or WONG");
                        input = sc.nextLine().toUpperCase();
                    } while (!Utils.getMageString().contains(input));
                    packet.addToPayload("mage_name", input);

                    do {
                        System.out.println("insert tower colour: WHITE, BLACK or GREY");
                        input = sc.nextLine().toUpperCase();
                    } while (!Utils.getColourString().contains(input));
                    packet.addToPayload("tower_colour", input);

                    setChanged();
                    notifyObservers(packet);
                    break;
                case "play assistant":
                    packet = new Packet("PLAY_ASSISTANT",playerId, gameId);

                    do {
                        try {
                            System.out.println("insert assistant number: 1 to 10");
                            intIn = sc.nextInt();
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input format. Please insert a NUMBER.");
                        }
                        sc.nextLine();
                    } while(intIn < 1 || intIn > 10);
                    packet.addToPayload("assistant",intIn); intIn = -1;

                    setChanged();
                    notifyObservers(packet);
                    break;

                case "play character":
                    packet = new Packet("PLAY_CHARACTER", playerId, gameId);
                    do{
                        try {
                            System.out.println("insert character card id");
                            intIn = sc.nextInt();
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input format. Please insert a NUMBER.");
                        }
                        sc.nextLine();
                    } while(intIn < 1 || intIn > 12);
                    packet.addToPayload("character", intIn);

                    switch(intIn) {
                        case 1 ->{
                            do{
                                try {
                                    System.out.println("insert chosen student index");
                                    intIn = sc.nextInt();
                                } catch (InputMismatchException e) {
                                    System.out.println("Invalid input format. Please insert a NUMBER.");
                                }
                                sc.nextLine();
                            } while(intIn < 0 || intIn > 3);
                            packet.addToPayload("character_student_choice_1", intIn); intIn = -1;
                            do{
                                try {
                                    System.out.println("insert number of the island (or island group) you want to move the student to");
                                    intIn = sc.nextInt();
                                } catch (InputMismatchException e) {
                                    System.out.println("Invalid input format. Please insert a NUMBER.");
                                }
                                sc.nextLine();
                            } while(intIn < 1 || intIn > 12);
                            packet.addToPayload("character_island_choice_1", intIn); intIn = -1;
                        }
                        case 3 -> {
                            do{
                                try {
                                    System.out.println("insert number of the island (or island group) you chose for the influence calculation");
                                    intIn = sc.nextInt();
                                } catch (InputMismatchException e) {
                                    System.out.println("Invalid input format. Please insert a NUMBER.");
                                }
                                sc.nextLine();
                            } while(intIn < 1 || intIn > 12);
                            packet.addToPayload("character_island_choice_3", intIn); intIn = -1;
                        }
                        case 5 -> {
                            do{
                                try {
                                    System.out.println("insert number of the island (or island group) you want to add a prohibition to");
                                    intIn = sc.nextInt();
                                } catch (InputMismatchException e) {
                                    System.out.println("Invalid input format. Please insert a NUMBER.");
                                }
                                sc.nextLine();
                            } while(intIn < 1 || intIn > 12);
                            packet.addToPayload("character_island_choice_5", intIn); intIn = -1;
                        }
                        case 7 -> {
                            do{
                                try {
                                    System.out.println("insert how many replacements you want to make");
                                    intIn = sc.nextInt();
                                } catch (InputMismatchException e) {
                                    System.out.println("Invalid input format. Please insert a NUMBER.");
                                }
                                sc.nextLine();
                            } while(intIn < 0 || intIn > 3);
                            packet.addToPayload("character_number_moves", intIn);

                            for(int i=0; i<intIn; i++){
                                do{
                                    try {
                                        System.out.println("insert student choice on this card");
                                        intChoice = sc.nextInt();
                                    } catch (InputMismatchException e) {
                                        System.out.println("Invalid input format. Please insert a NUMBER.");
                                    }
                                    sc.nextLine();
                                } while(intChoice < 0 || intChoice > 5);
                                packet.addToPayload("character_card_choice_" + i, intChoice); intChoice = -1;

                                do {
                                    try {
                                        System.out.println("insert student choice in the hall");
                                        intChoice = sc.nextInt();
                                    } catch (InputMismatchException e) {
                                        System.out.println("Invalid input format. Please insert a NUMBER.");
                                    }
                                    sc.nextLine();
                                } while (intChoice < 0 || intChoice > 9);
                                packet.addToPayload("character_hall_choice_" + i, intChoice); intChoice = -1;
                            }
                        }
                        case 9 -> {
                            do{
                                System.out.println("insert student race");
                                input = sc.nextLine().toUpperCase();
                            }while(!Utils.getRaceString().contains(input));
                            packet.addToPayload("character_colour_choice_9", input);
                        }
                        case 10 -> {
                            do{
                                try {
                                    System.out.println("insert how many replacements you want to make");
                                    intIn = sc.nextInt();
                                } catch (InputMismatchException e) {
                                    System.out.println("Invalid input format. Please insert a NUMBER.");
                                }
                                sc.nextLine();
                            } while(intIn < 0 || intIn > 2);
                            packet.addToPayload("character_number_moves", intIn);

                            for(int i=0; i<intIn; i++){
                                do{
                                    System.out.println("insert the race of the table you want to subtract one student to");
                                    input = sc.nextLine().toUpperCase();
                                } while(!Utils.getRaceString().contains(input));
                                packet.addToPayload("character_table_choice_" + i, input);

                                do {
                                    try {
                                        System.out.println("insert the index of the student in the hall you want to replace");
                                        intChoice = sc.nextInt();
                                    } catch (InputMismatchException e) {
                                        System.out.println("Invalid input format. Please insert a NUMBER.");
                                    }
                                    sc.nextLine();
                                } while (intChoice < 0 || intChoice > 9);
                                packet.addToPayload("character_hall_choice_" + i, intChoice); intChoice = -1;
                            }
                        }
                        case 11 -> {
                            do{
                                try {
                                    System.out.println("insert chosen student index");
                                    intIn = sc.nextInt();
                                } catch (InputMismatchException e) {
                                    System.out.println("Invalid input format. Please insert a NUMBER.");
                                }
                                sc.nextLine();
                            } while( intIn < 0 || intIn > 3);
                            packet.addToPayload("character_student_choice_11", intIn); intIn = -1;
                        }
                        case 12 -> {
                            do{
                                System.out.println("insert chosen student race");
                                input = sc.nextLine().toUpperCase();
                            } while(!Utils.getRaceString().contains(input));
                            packet.addToPayload("character_colour_choice_12", input);
                        }
                    }

                    intIn = -1;
                    setChanged();
                    notifyObservers(packet);
                    break;
                case "end planning":
                    packet = new Packet("END_PLANNING",playerId, gameId);

                    setChanged();
                    notifyObservers(packet);
                    break;
                case "move student":
                    packet = new Packet("MOVE_STUDENT",playerId, gameId);

                    do {
                        try {
                            System.out.println("insert chosen student index");
                            intIn = sc.nextInt();
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input format. Please insert a NUMBER.");
                        }
                        sc.nextLine();
                    } while (intIn < 0 || intIn > 9);
                    packet.addToPayload("student_index", intIn); intIn = -1;

                    do {
                        System.out.println("insert where you want it to move: table (T) or island (I)");
                        input = sc.nextLine().toUpperCase();
                    } while (!input.equals("T") && !input.equals("I"));
                    packet.addToPayload("move_to", input);

                    if(input.equals("I")) {
                        do {
                            try {
                                System.out.println("Insert island (or island group) number");
                                intIn = sc.nextInt();
                            } catch (InputMismatchException e) {
                                System.out.println("Invalid input format. Please insert a NUMBER.");
                            }
                            sc.nextLine();
                        } while (intIn < 1 || intIn > 12);

                        //If the selected island is a group, students are actually added to the island which index identifies the group at this moment
                        packet.addToPayload("group_number", intIn); intIn = -1;
                    }

                    setChanged();
                    notifyObservers(packet);
                    break;
                case "move mn":
                    packet = new Packet("MOVE_MN", playerId, gameId);

                    do {
                        try {
                            System.out.println("insert moves number");
                            intIn = sc.nextInt();
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input format. Please insert a NUMBER.");
                        }
                        sc.nextLine();
                    } while(intIn <= 0);
                    packet.addToPayload("mn_moves",intIn); intIn = -1;

                    setChanged();
                    notifyObservers(packet);
                    break;
                case "get students":
                    packet = new Packet("GET_STUDENTS",playerId, gameId);

                    do {
                        try {
                            System.out.println("insert cloud number");
                            intIn = sc.nextInt();
                        } catch (InputMismatchException e) {
                            System.out.println("Invalid input format. Please insert a NUMBER.");
                        }
                        sc.nextLine();
                    } while (intIn < 0 || intIn > 4);
                    packet.addToPayload("cloud",intIn); intIn = -1;

                    setChanged();
                    notifyObservers(packet);
                    break;
                case "help":
                    printCommands();
                    break;
                case "fetch characters":
                    packet = new Packet("FETCH_CHARACTERS",playerId,gameId);
                    setChanged();
                    notifyObservers(packet);
                    break;
                default:
                    System.out.println("Invalid command, here a list of valid commands to use during the game");
                    printCommands();
                    break;
            }
        }
    }
    @Override
    public void update(Observable o, Object arg) {
        ClientModel clientModel = (ClientModel) o;
        this.playerId=clientModel.getPlayerId();
        this.gameId = clientModel.getGameId();
        this.inAGame =clientModel.isInAGame();
        this.tempPlayerId= clientModel.getTempPlayerId();
        Message msg = (Message) arg;
        switch (msg.getAction()){
            case "ATTEMPT_RECONNECTION":
                isReconnecting = true;
            case "PLAYER_ID_SET":
            case "GAME_ID_SET":
            case "PRINT_PLAYER":
                printPlayer(clientModel);
                break;
            case "PRINT_PHASE":
                System.out.println(clientModel.getPhase());
                break;
        }
    }

    /**
     * prints the available commands for the user
     */
    public void printCommands(){
        String printString="";
        printString+="Set nickname: set nickname\n"+
                "Not in a game commands: create game, join game, fetch games\n"+
                "Planning phase commands: play assistant, end planning, play character\n"+
                "Action phase commands: move student, move mn, get students, end planning, play character\n"+
                "To restore server connection: ping server, reset nickname\n"+
                "View commands: help";
        System.out.println(printString);
    }

    /**
     * prints a warning for duplicate nickname
     */
    public void printNicknameWarning() {
        System.out.println(ColorStrings.ERROR+"duplicate nickname try again"+ColorStrings.RESET);
    }

    /**
     * @param message
     * prints the error message
     */
    public void printErrorMessage(String message) {
        System.out.println(ColorStrings.ERROR+message+ ColorStrings.RESET);
    }

    /**
     * @param clientModel
     * prints player info
     */
    public  void printPlayer(ClientModel clientModel){
        String printString="";
        printString+="Player id: "+playerId+"\n";
        if(clientModel.getNickname()!=null){
            printString+="Nickname: "+clientModel.getNickname()+"\n";
        }
        if(inAGame){
            printString+="Game id: "+gameId+"\n";
            if(clientModel.isBlocked()){
                printString+="\033[48;2;216;0;0m";
            }else{
                printString+="\033[48;2;0;216;0m";
            }
            printString+=clientModel.getPhase();
            printString+="\033[m";

        }else{
            printString+="Not in a game. Use 'help' command to view a list of possible commands.";
        }
        printString+="\n";
        System.out.println(printString);

    }

    /**
     * @param packet
     * prints available games with their info
     */
    public void printGames(Packet packet) {
        if(packet.getPayload().keySet().size()==0){
            System.out.println("No games have been created yet. Use create_game to create a new game.");
            return;
        }
        for(String gameId: packet.getPayload().keySet()){
            System.out.println("Game id: "+gameId);
            LinkedTreeMap<String,Object> game = (LinkedTreeMap<String, Object>) packet.getFromPayload(gameId);
            System.out.println("Available space: "+game.get("empty"));

            System.out.println("Mages already taken:");
            ArrayList<String> mages = (ArrayList<String>) game.get("mages");
            for(String mage: mages){
                System.out.println(mage);
            }

            System.out.println("Colors already taken:");
            String printString="";
            ArrayList<String> colors = (ArrayList<String>) game.get("towers");
            for(String color: colors){
                printString+=color+"\n";
            }
            System.out.println(printString);
        }
    }

    /**
     * @param message
     * prints a generic message
     */
    @Override
    public void printMessage(Object message) {
        System.out.println(message);
    }

    /**
     * @param packet
     * Prints all the characters available with their info
     */
    @Override
    public void printCharacters(Packet packet) {
        LinkedTreeMap<String ,Object> characters= (LinkedTreeMap<String, Object>) packet.getFromPayload("characters");
        for (String key: characters.keySet()){
            System.out.println("Id: "+key);
            LinkedTreeMap<String, Object> character= (LinkedTreeMap<String, Object>)characters.get(key);
            ArrayList<Double> students = (ArrayList<Double>) character.get("students");
            if(students!=null){
                for(int i =0; i<Utils.raceArray.size(); i++){
                    System.out.println(Utils.raceArray.get(i)+": "+Math.round(students.get(i)));
                }
            }
            if(character.containsKey("prohibitions")){
                double prohibitions = (double) character.get("prohibitions");
                System.out.println("prohibitions: "+Math.round(prohibitions));
            }
        }
    }


}
