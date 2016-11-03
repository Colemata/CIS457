package edu.gvsu;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {


    public static ArrayList<String> cmdList = new ArrayList<String>() {{
        add("quit");
        add("list");
        add("retr <filename>");
        add("stor <filename>");
        add("connect <server name/IP address> <server port>");
    }};

    public static Socket server;
    public static DataInputStream in;
    public static DataOutputStream out;

    public static void main(String[] args) {
        try {
            String port = "";
            String ip = "";
            String filename;
            String response;
            String firstEntry;
            String cmdIP;
            String cmdPort;

            server = new Socket();

            //use a scanner for the user input.
            Scanner userInput = new Scanner(System.in);

            //Now lets start our command line stuff... basically just getting user input and deciding what to do here.
            while (true) {

                //repeat this message to the user...
                System.out.println("Enter a command (h for help): ");

                //snag the user input.
                String cmd = userInput.nextLine();

                //if the command contains a space, we should seperate the junk off the end to determine the proper command.
                if (cmd.contains(" ")) {
                    firstEntry = cmd.split(" ")[0];
                } else {
                    //If there wasn't a space, just take the whole thing.
                    firstEntry = cmd;
                }


                if (!server.isConnected() && !firstEntry.toLowerCase().trim().equalsIgnoreCase("connect")) {
                    System.out.println("You must first connect to a server before issuing commands.");
                } else {
                    //use lower case for all ops. find out what the user wants to do.
                    switch (firstEntry.toLowerCase().trim()) {
                        case "connect":
                            if (server.isConnected()) {
                                System.out.println("You are already connected to a server!");
                            }
                            String[] splitCMD = cmd.split("\\s+");

                            //just here for easy debugging.
                            if (splitCMD.length == 1) {
                                server = new Socket("localhost", 33333);
                            }

                            //make sure the args match for this command.
                            else if (splitCMD.length == 3) {
                                cmdIP = splitCMD[1];
                                cmdPort = splitCMD[2];
                                try {
                                    server = new Socket(cmdIP, Integer.parseInt(cmdPort));
                                } catch (NumberFormatException ex) {
                                    System.out.println("Port must be a valid number.");
                                }
                            } else {
                                System.out.println("Invalid number of args for this command.");
                            }

                            if (server.isConnected()) {

                                System.out.println("Connected to " + server.getInetAddress());

                                //new up some input and output streams.
                                in = new DataInputStream(new BufferedInputStream(server.getInputStream()));
                                out = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));
                            }
                            break;
                        case "h":
                            DisplayCommands();
                            break;
                        //This command isn't for a grade, just for testing, leaving it here.
                        case "server":
                            System.out.println("sending server message");
                            out.writeUTF("server");
                            out.flush();
                            Thread.sleep(2000);
                            while (in.available() > 0) {
                                response = in.readUTF();
                                System.out.println(response);
                            }
                            break;
                        case "quit":
                            SendQuitAndQuit(out, in);
                            break;
                        case "list":
                            out.writeUTF("list");
                            out.flush();

                            //find a better way to wait for response.
                            Thread.sleep(1000);

                            System.out.println("The server contains the following files: ");

                            //if there is a response, print that response until there isn't any more stuff to write.
                            while (in.available() > 0) {
                                response = in.readUTF();
                                System.out.println("\t" + response);
                            }

                            break;
                        case "retr":
                            filename = cmd.split(" ")[1];
                            RetrieveFileFromServer(out, in, filename);
                            break;
                        case "stor":
                            filename = cmd.split(" ")[1];
                            StoreFileOnTheServer(out, in, filename);
                            break;
                        //This also isn't required, but it will say what files are in the current dir for the client.
                        case "clist":
                            File curDir = new File(".");
                            File[] FileList = curDir.listFiles();
                            for (File file : FileList) {
                                System.out.println(file.getName());
                            }
                            break;
                        default:
                            System.out.println("Invalid command, press h for possible commands...");
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void StoreFileOnTheServer(DataOutputStream out, DataInputStream in, String filename) {

        //yet again, is this an ok size?
        byte[] buffer = new byte[4098];
        int n = 0;

        try {

            //write what we are doing to the server, along with the file name.
            out.writeUTF("stor");
            out.writeUTF(filename);

            //new up the file object that we are going to send to the server.
            File fileToSend = new File(System.getProperty("user.dir") + File.separator + filename);
            FileInputStream fis = new FileInputStream(fileToSend);

            //write the file size to the server so that it knows when to stop writing/reading.
            out.writeLong(fileToSend.length());

            //write the file while the file has bytes buffered.
            while ((n = fis.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }

            //close the file output stream.
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void RetrieveFileFromServer(DataOutputStream out, DataInputStream in, String filename) {

        //Should we make this buffer a different size?
        byte[] buffer = new byte[4098];

        try {

            //send the request to the server.
            out.writeUTF("retr");

            //the second parameter should be the filename.
            out.writeUTF(filename);
            out.flush();

            //wait for the server response.
            Thread.sleep(1000);

            //Make the file object for the file output stream.
            File newFile = new File(System.getProperty("user.dir") + File.separator + filename);
            FileOutputStream fos = new FileOutputStream(newFile);

            //read in the file size, this is important.
            Long filesize = in.readLong();
            int read = 0;
            int remaining = filesize.intValue();

            //while there is data being read in, read.
            while ((read = in.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                //the remaining size left is = to the remaining size minus the size of what we just read.
                remaining -= read;

                //write these bytes that are buffered to the new file.
                fos.write(buffer, 0, read);
            }

            //close the file output stream.
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("File copied from server: " + filename);
    }

    private static void SendQuitAndQuit(DataOutputStream out, DataInputStream in) {
        try {

            //write the quit message to the server.
            out.writeUTF("quit");
            out.flush();

            //wait a little bit for the message to complete sending. maybe?
            Thread.sleep(500);


            System.out.println("Disconnecting from socket: " + server.getInetAddress());

            //commented a lot of stuff out here because I don't think this stuff is needed...

//            try {
                //close the streams.
                out.flush();
//                in.flush();
//            }
//            finally {
//                server.shutdownInput();
//                server.shutdownOutput();
//                server.close();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //kill the thread.
        Runtime.getRuntime().exit(0);
    }

    private static void DisplayCommands() {

        System.out.println("Commands are as follows:");

        for (String cmd : cmdList) {
            System.out.println("\t-" + cmd + "-");
        }
    }
}
