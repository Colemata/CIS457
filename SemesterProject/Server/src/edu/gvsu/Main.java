package edu.gvsu;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

//Server side of things.
public class Main {

    public static final int port = 9999;
    public static ArrayList<String> OptionsArray = new ArrayList<>();

    public static void main(String[] args) {

        //use this method to init the data we want to use for options.
        initData();

        try {
            ServerSocket server = new ServerSocket(port);
            while(true) {

                System.out.println("Waiting for client....");
                Socket client = server.accept();
                System.out.println("Client from" + client.getInetAddress() + "connected");
                DataOutputStream out = new DataOutputStream(new BufferedOutputStream(client.getOutputStream()));

                //once there is a connection we want to present the user with a selection of stuff they can do.
                SendUserMainMenuOptions(out);

                out.flush();
            }
        } catch (IOException io) {
            System.out.println("IO Exception was caught in the main:");
            System.out.println(io.toString());
        }
    }

    private static void SendUserMainMenuOptions(DataOutputStream out) {
        int index = 0;
        try {
            out.writeUTF("Enter the option number you would like to do and press ENTER:");
            for(String option: OptionsArray){
                out.writeUTF("\t" + index + ") " + option);
                index++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initData(){
        OptionsArray.add("asda23sd");
        OptionsArray.add("a12312334sdasd");
        OptionsArray.add("as123123dasd");
        OptionsArray.add("a3242323sdasd");
        OptionsArray.add("as34243dasd");
        OptionsArray.add("asd123123asd");
        OptionsArray.add("David is a dumbass");

    }
}