package main.java.com.polimi.client;

import main.java.com.polimi.client.utils.ColorStrings;
import main.java.com.polimi.client.views.gui.ClientGUIView;


import java.util.Objects;

public class Client {
    public static void main( String[] args )
    {
        if(args.length<2){
            System.out.println(ColorStrings.ERROR+"VIEW MODE NEEDED: CLI OR GUI, PATH NEEDED");
            return;
        }else if(Objects.equals(args[0], "GUI")){
            ClientGUIView.startGui(args[1]);
        }else if (Objects.equals(args[0], "CLI")){
            CLILaunch.CLILaunch();
        }else {
            System.out.println(ColorStrings.ERROR+"WRONG VIEW MODE: CLI OR GUI");
            return;
        }
    }
}
