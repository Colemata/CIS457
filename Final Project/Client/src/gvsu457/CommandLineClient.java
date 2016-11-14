package gvsu457;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class CommandLineClient {

    /*The user input for system.in*/
    public static String userInput;

    /*The split user input from system.in*/
    public static String[] userInputSplit;

    /*The username that the user enters*/
    private static String username;

    /*Hashmap that contains command parameter info for both h commands and param checking*/
    public static HashMap<String, String> CommandParameterHashMap;

    /*Hashmap that contains command explinations for the help menu*/
    public static HashMap<String, String> CommandExplinationHashMap;

    /*The server socket to maintain a connect*/
    public static Socket server = new Socket();

    /*The data input stream from the server*/
    public static DataInputStream in_server;

    /*The data output stream to the server.*/
    public static DataOutputStream out_server;

    /*End of transmission for a stream.*/
    public static final String EOT = "end_of_transmission";

    /**
     * CommandLineClient method where all of our command line operations will happen.
     *
     * @param args empty
     */
    public static void main(String[] args) {

        //Init any important data structures for the commands issued by the user.
        InitializeClientDataLoad();

        //Give the user some commands to work with:
        while (true) {

            System.out.println("Welcome to our final cis 457 project!");
            System.out.println("_____________________________________");
            System.out.println("Please enter your desired username for this session: ");

            //Get the first command from the user.
            Scanner cmd = new Scanner(System.in);
            username = cmd.nextLine();

            System.out.println("You have set your username as: " + getUsername());
            System.out.println("\n\nPlease enter a command (h for help)!");

            while (true) {
                //prompt the user for input.
                System.out.print(">>:");

                //Read the user input from system.in
                userInput = cmd.nextLine();

                //If there are other parameters to the command we want to seperate those for later use.
                userInputSplit = userInput.split("\\s+");

                if (!server.isConnected() && !(userInput.contains("connect") || userInput.equalsIgnoreCase("h") ||
                        userInput.equalsIgnoreCase("u"))) {
                    if (userInput.equalsIgnoreCase("quit")) {
                        System.out.println("You are not currently connected to a sever.");
                    } else {
                        System.out.println("Please connect to the server using the <connect> command, enter <h> for help.");
                    }
                } else {

                    //switch based upon the first command in the line.
                    switch (userInputSplit[0]) {
                        case "h":
                            DisplayCommandsForTheUser();
                            break;
                        case "u":
                            System.out.println("Please enter your desired username for this session: ");
                            username = cmd.nextLine();
                            System.out.println("Username has been changed sucessfully!");
                            break;
                        case "connect":
                            if (userInputSplit.length != 3) {
                                InvalidParametersEntered(userInputSplit[0]);
                                break;
                            }
                            ConnectToTheServer(userInputSplit[1], userInputSplit[2]);
                            break;
                        case "games":
                            GetGameListFromServer();
                            break;
                        case "play":
                            if (userInputSplit.length != 2) {
                                InvalidParametersEntered(userInputSplit[0]);
                                break;
                            }
                            PlayAGameFromTheServer(userInputSplit[0], userInputSplit[1]);
                            break;
                        default:
                            System.out.println("Invalid command, enter command (h) if you need help!");
                    }
                }
            }
        }
    }

    private static void PlayAGameFromTheServer(String command, String gameNumber) {

        try {
            out_server = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));
            out_server.writeUTF(command);
            out_server.writeInt(Integer.parseInt(gameNumber));
            out_server.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void DisplayCommandsForTheUser() {
        for (String key : CommandExplinationHashMap.keySet()) {
            System.out.println("\t<" + key + "> | " + CommandExplinationHashMap.get(key));
        }
    }

    private static void GetGameListFromServer() {
        String gamename;
        int index = 0;
        try {
            out_server.writeUTF("games");
            out_server.flush();

            while (true) {
                gamename = in_server.readUTF();
                if (!gamename.equalsIgnoreCase(EOT)) {
                    System.out.println("Game #" + index + ": " + gamename);
                    index++;
                } else {
                    break;
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean ConnectToTheServer(String ip, String port) {
        try {
            server = new Socket(ip, Integer.parseInt(port));
            if (server.isConnected()) {

                //set up the streams using the global socket.
                in_server = new DataInputStream(new BufferedInputStream(server.getInputStream()));
                out_server = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));

                System.out.println("Connection established to game server!");

                out_server.writeUTF(getUsername());
                out_server.flush();
                return true;
            }
        } catch (IOException e) {
            System.out.println("Unable to connect to: " + ip + ":" + port);
        }
        return false;
    }

    /**
     * This method will contain inits for all the important data we will use
     * during the execution of the program.
     */
    private static void InitializeClientDataLoad() {

        CommandParameterHashMap = new HashMap<String, String>();

        //This can maybe be done another way if we want, but this is the easiest for now...
        CommandParameterHashMap.put("h", "");
        CommandParameterHashMap.put("u", "");
        CommandParameterHashMap.put("connect", "<ip> <port>");
        CommandParameterHashMap.put("quit", "");
        CommandParameterHashMap.put("games", "");
        CommandParameterHashMap.put("play", "<game number>");

        CommandExplinationHashMap = new HashMap<String, String>();

        //This can maybe be done another way if we want, but this is the easiest for now...
        CommandExplinationHashMap.put("h", "This is the command that will display the other commands.");
        CommandExplinationHashMap.put("u", "Allows the user to set their username once again.");
        CommandExplinationHashMap.put("connect", "Parameters: <ip> <port> | Allows the user to connect to a main game server.");
        CommandExplinationHashMap.put("quit", "Quits the server session currently connected too.");
        CommandExplinationHashMap.put("games", "Lists the games from the server.");
        CommandExplinationHashMap.put("play", "Parameters: <game number> | Allows the user to play a game of the specified number.");
    }

    /**
     * Prints the invalid parameter text to the user.
     *
     * @param command the command that the user messed up.
     */
    private static void InvalidParametersEntered(String command) {
        System.out.println("Invalid parameters for command: " + command);
        String properParameters = GetParametersForCommand(command);
        System.out.println("The proper parameters for <" + command + "> are: " + properParameters);
    }

    /**
     * Gets the parameters used for the command specified.
     *
     * @param command the lookup value.
     * @return the parameters for the command.
     */
    private static String GetParametersForCommand(String command) {
        return CommandParameterHashMap.get(command);
    }

    public static String getUsername() {
        return username;
    }
}
