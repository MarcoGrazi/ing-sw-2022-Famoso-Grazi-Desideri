package main.java.com.polimi.app.controllers;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Client handler class.
 * @author Group 53
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private ClientHandlerController listener;
    private String nickname;
    private int playerID;


    /**
     * @param socket socket on which the server is running
     * @param clientHandlerController controller of all the handlers
     */
    public ClientHandler(Socket socket, ClientHandlerController clientHandlerController) {
        this.socket = socket;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            listener = clientHandlerController;
            System.out.println("New Connection");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Implements the run function of runnable interface
     */
    @Override
    public void run() {
        System.out.println("Client Handler Started");
        try {
            while (socket.isConnected()){
                try{
                    Object obj = in.readObject();
                    String json = (String) obj;

                    System.out.println("Received: "+json);
                    listener.received(json, this);
                }
                catch (ClassNotFoundException e){
                    e.printStackTrace();
                }
            }

        }catch (IOException e){
            close();
            //e.printStackTrace();
        }
    }

    /**
     * closes the connection when the client disconnects
     */
    public void close(){
        try {
            in.close();
            out.close();
            socket.close();
            listener.removeClient(this.playerID);
            System.out.println("Client disconnected");
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * @param obj packet to send
     * send s a packet to the client
     */
    public void sendObject(Object obj){
        try {
            out.writeObject(obj);
            out.flush();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * @return returns the nickname of the client
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname nickname to set
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * @param playerID palyer id to set
     */
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }
}

