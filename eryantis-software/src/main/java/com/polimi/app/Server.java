package main.java.com.polimi.app;

import main.java.com.polimi.app.controllers.ServerController;

/**
 * Main class.
 * Prints an introduction and starts the Server side thread.
 */
public class Server
{
    public static void main( String[] args )
    {
        if(args.length<1){
            System.out.println("PATH REQUIRED");
            return;
        }
        System.out.println( "Hello welcome to Eriantys!");
        ServerController server = new ServerController(80, args[0]);
        server.start();
    }
}
