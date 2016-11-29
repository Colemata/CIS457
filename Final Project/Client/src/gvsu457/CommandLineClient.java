package gvsu457;

import gvsu457.Hangman.Client.HangmanClientLogic;
import gvsu457.Hangman.Server.HangmanServerLogic;
import gvsu457.TicTacToe.Client.TicTacToeClientLogic;
import gvsu457.TicTacToe.Server.TicTacToeServerLogic;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is the command line client that will connect to the server and send commands that are entered by
 * the user. Through the command line client you can play games with other users that are connected to the other
 * end of the server. The games are all p2p and the connection information is sent from the server to each client.
 */
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

    /*This will be used to spawn other threads.*/
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    /*End of transmission for a stream.*/
    public static final String EOT = "end_of_transmission";

    /*The listening port for incoming connections from other clients*/
    private static int LISTENING_PORT = 6729;

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

            System.out.println("You have set your username as: " + username);
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

                            System.out.println("Would you like to use: " + LISTENING_PORT + " as your port for p2p games?!");
                            System.out.println("Enter y/n");

                            String choice = cmd.nextLine().trim();
                            if (choice.equalsIgnoreCase("n")) {
                                System.out.println("Please enter a port number to be used for connecting to other clients.");
                                LISTENING_PORT = Integer.parseInt(cmd.nextLine());
                            }
                            ConnectToTheServer(userInputSplit[1], userInputSplit[2], LISTENING_PORT);
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
                            GetAMatchForAGame();
                            try {
                                out_server.writeUTF("kill");
                                out_server.flush();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            break;
                        case "remove":
                            if (userInputSplit.length != 2) {
                                InvalidParametersEntered(userInputSplit[0]);
                                break;
                            }
                            RemoveMyselfFromTheQueueOnTheServer(userInputSplit[0], userInputSplit[1]);
                            break;
                        default:
                            System.out.println("Invalid command, enter command (h) if you need help!");
                    }
                }
            }
        }
    }

    /**
     * This method will wait for a match for the game queues. The matches are issued from the server.
     */
    private static void GetAMatchForAGame() {
        String OtherPlayerName = null;
        String Username = null;
        String OtherPlayerIP = null;
        int OtherPlayerPort = 0;
        String game = null;
        System.out.println("You have entered the queue... Please wait for match...");
        try {

            //loop until there is a transmission from the server.
            while (true) {
                if (in_server.available() > 0) {
                    OtherPlayerName = in_server.readUTF();

                    //If we get "skip" sent from the server, we are going to act as the server in the game connection.
                    if (OtherPlayerName.equalsIgnoreCase("skip")) {
                        game = in_server.readUTF();
                        break;
                    }

                    //grab the important information from the server.
                    Username = in_server.readUTF();
                    OtherPlayerIP = in_server.readUTF();
                    OtherPlayerPort = in_server.readInt();

                    //remove the stupid / from the ip address.
                    if (OtherPlayerIP.contains("/")) {
                        OtherPlayerIP = OtherPlayerIP.substring(1);
                    }

                    //get the game we are going to be playing.
                    game = in_server.readUTF();
                    break;
                } else {
                    System.out.println("Still waiting....");

                    //replace this with something better...
                    Thread.sleep(5000);

                }
            }

            //Yet again, this means we are the server for our game we are going to play.
            if (OtherPlayerName.equalsIgnoreCase("skip")) {

                //you are the server, start the server up for the specified game.
                StartServerVersionOfGame(game);

                return;
            } else {

                //use the information we have gathered to connect to the other client.
                Thread.sleep(5000);

                //Start the client version of the game.
                StartClientVersionOfGame(game, OtherPlayerPort, OtherPlayerIP);

                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * This will start the client version of the game specified.
     * @param game the game type being played.
     * @param otherPlayerPort the other players port to connect to.
     * @param otherPlayerIP the other players IP to connect to.
     */
    private static void StartClientVersionOfGame(String game, int otherPlayerPort, String otherPlayerIP) {
        switch (game) {
            case "tictactoe":
                TicTacToeClientLogic ticTacToeClientLogic = new TicTacToeClientLogic(username, otherPlayerIP, otherPlayerPort);
                executorService.submit(ticTacToeClientLogic);
                break;
            case "hangman":
                HangmanClientLogic hangmanClientLogic = new HangmanClientLogic(username, otherPlayerIP, otherPlayerPort);
                executorService.submit(hangmanClientLogic);
                break;
            case "battleship":
                break;
            case "minesweeper":
                break;
            case "placeholder":
                break;
        }
    }

    /**
     * This will start the server version of the game to be played.
     * @param game the game to be played.
     */
    private static void StartServerVersionOfGame(String game) {
        switch (game) {
            case "tictactoe":
                TicTacToeServerLogic ticTacToeServerLogic = new TicTacToeServerLogic(LISTENING_PORT, username);
                executorService.submit(ticTacToeServerLogic);
                break;
            case "hangman":
                HangmanServerLogic hangmanServerLogic = new HangmanServerLogic(LISTENING_PORT, username);
                executorService.submit(hangmanServerLogic);
                break;
            case "battleship":
                break;
            case "minesweeper":
                break;
            case "placeholder":
                break;
        }
    }

    /**
     * This will remove the user from a queue on the server.
     * @param command the command issued to the server.
     * @param gameNumber the game number to remove for the user.
     */
    private static void RemoveMyselfFromTheQueueOnTheServer(String command, String gameNumber) {
        try {
            out_server = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));
            out_server.writeUTF(command);
            out_server.writeInt(Integer.parseInt(gameNumber));
            out_server.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This will enter the user into a queue on the server in order to play a game.
     * @param command the command issued to the server.
     * @param gameNumber the game number of the game to be played.
     */
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

    /**
     * This will display all the commands the user can use, and the explination of all the commands.
     */
    private static void DisplayCommandsForTheUser() {
        for (String key : CommandExplinationHashMap.keySet()) {
            System.out.println("\t<" + key + "> | " + CommandExplinationHashMap.get(key));
        }
    }

    /**
     * This will query the server for a game list and then display it for the user.
     */
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

    /**
     * Used to connect to the server.
     * @param ip the ip of the server.
     * @param port the port of the server.
     * @param listeningPort the port we are going to use for client to client connections.
     * @return the status of the connection.
     */
    private static boolean ConnectToTheServer(String ip, String port, int listeningPort) {
        try {
            server = new Socket(ip, Integer.parseInt(port));
            if (server.isConnected()) {

                //set up the streams using the global socket.
                in_server = new DataInputStream(new BufferedInputStream(server.getInputStream()));
                out_server = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));

                System.out.println("Connection established to game server!");

                out_server.writeUTF(username);
                out_server.writeInt(listeningPort);
                out_server.flush();
                return true;
            }
        } catch (IOException e) {
            System.out.println("Unable to connect to: " + ip + ":" + port);
        } catch (NumberFormatException e){
            System.out.println("Port must be a number, connection failed.");
        } catch (IllegalArgumentException e){
            System.out.println("Port out of range, connection failed.");
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
        CommandParameterHashMap.put("remove", "<game number>");

        CommandExplinationHashMap = new HashMap<String, String>();

        //This can maybe be done another way if we want, but this is the easiest for now...
        CommandExplinationHashMap.put("h", "This is the command that will display the other commands.");
        CommandExplinationHashMap.put("u", "Allows the user to set their username once again.");
        CommandExplinationHashMap.put("connect", "Parameters: <ip> <port> | Allows the user to connect to a main game server.");
        CommandExplinationHashMap.put("quit", "Quits the server session currently connected too.");
        CommandExplinationHashMap.put("games", "Lists the games from the server.");
        CommandExplinationHashMap.put("play", "Parameters: <game number> | Allows the user to play a game of the specified number.");
        CommandExplinationHashMap.put("remove", "Parameters: <game number> | Allows the user to remove themselves from the game queue for the specified game number.");
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
}
