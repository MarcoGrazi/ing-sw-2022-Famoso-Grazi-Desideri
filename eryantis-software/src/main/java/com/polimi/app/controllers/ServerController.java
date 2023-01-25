package main.java.com.polimi.app.controllers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server controller class.
 * @author Group 53
 */
public class ServerController implements Runnable {
    private int port;
    private ServerSocket serverSocket;
    private boolean running=false;
    private String path;
    private ClientHandlerController clientHandlerController;


    /**
     * @param port port the server runs on
     * @param path path where to find the checkpoints folder
     */
    public ServerController(int port, String path) {
        this.port=port;
        this.path=path;
        clientHandlerController= new ClientHandlerController(this.path);

    }

    /**
     * Starts the server, creates a new thread
     */
    public void start(){
        System.out.println("Server started");
        new Thread(this).start();

    }

    /**
     * Function required to implement the runnable interface, creates a new threadpool, listens for connections
     */
    @Override
    public void run() {
        running=true;
        ExecutorService executor= Executors.newCachedThreadPool();
        try{
            serverSocket=new ServerSocket(9999, 0, InetAddress.getByName("169.254.233.39"));
        }catch (IOException e){
            e.printStackTrace();
        }
        while (running){
            try{
                Socket socket=serverSocket.accept();

                ClientHandler clientHandler =new ClientHandler(socket, clientHandlerController);
                executor.submit(clientHandler);
                clientHandlerController.addClient(clientHandler);
                System.out.println("Client connected");

            }catch (IOException e){
                e.printStackTrace();
            }
        }
        shutdown(executor);
    }

    /**
     * @param executor
     * closes the thread pool and the socket
     */
    public void shutdown(ExecutorService executor){
        running=false;
        try{
            executor.shutdown();
            serverSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}

