package gvsu457;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class Client {

    /*The user input for system.in*/
    public String userInput;

    /*The split user input from system.in*/
    public String[] userInputSplit;

    /*The username that the user enters*/
    private String username;

    /*Hashmap that contains command parameter info for both h commands and param checking*/
    public HashMap<String, String> CommandParameterHashMap;

    /*The server socket to maintain a connect*/
    public Socket server;

    /**
     * Client method where all of our command line operations will happen.
     * @param args empty
     */
    public void main(String[] args) {

        //Init any important data structures for the commands issued by the user.
        InitializeClientDataLoad();

        //Give the user some commands to work with:
        while (true) {

            System.out.println("Welcome to our final cis 457 project!");
            System.out.println("_____________________________________");
            System.out.println("Please enter your desired username for this session: ");

            //Get the first command from the user.
            Scanner cmd = new Scanner(System.in);
            setUsername(cmd.nextLine());

            System.out.println("You have set your username as: " + getUsername());
            System.out.println("\n\nPlease enter a command (h for help)!");
            System.out.println(">>:");

            while (true) {
                //prompt the user for input.
                System.out.println(">>:");

                //Read the user input from system.in
                userInput = cmd.nextLine();

                //If there are other parameters to the command we want to seperate those for later use.
                userInputSplit = userInput.split("\\s+");

                //switch based upon the first command in the line.
                switch (userInputSplit[0]) {
                    case "h":
                        break;
                    case "u":
                        System.out.println("Please enter your desired username for this session: ");
                        setUsername(cmd.nextLine());
                        System.out.println("Username has been changed sucessfully!");
                        break;
                    case "connect":
                        if(userInputSplit.length != 3){
                            InvalidParametersEntered(userInputSplit[0]);
                            break;
                        }
                        ConnectToTheServer(userInputSplit[1], userInputSplit[2]);
                        break;
                    default:
                        System.out.println("Invalid command, enter command (h) if you need help!");
                }
            }
        }
    }

    private void ConnectToTheServer(String ip, String port) {
        try {
            server = new Socket(ip, Integer.parseInt(port));
            if(server.isConnected()){
                System.out.println("Connection established to game server!");
            }
        } catch (IOException e) {
            System.out.println("Unable to connect to: " + ip + ":" + port);
        }
    }

    /**
     * This method will contain inits for all the important data we will use
     * during the execution of the program.
     */
    private void InitializeClientDataLoad() {
        CommandParameterHashMap = new HashMap<String, String>();

        //This can maybe be done another way if we want, but this is the easiest for now...
        CommandParameterHashMap.put("h", "");
        CommandParameterHashMap.put("u", "");
        CommandParameterHashMap.put("connect", "<ip> <port>");
        CommandParameterHashMap.put("quit", "");
    }

    /**
     * Prints the invalid parameter text to the user.
     * @param command the command that the user messed up.
     */
    private void InvalidParametersEntered(String command) {
        System.out.println("Invalid parameters for command: " + command);
        String properParameters = GetParametersForCommand(command);
        System.out.println("The proper parameters for <" + command + "> are: " + properParameters);
    }

    /**
     * Gets the parameters used for the command specified.
     * @param command the lookup value.
     * @return the parameters for the command.
     */
    private String GetParametersForCommand(String command) {
        return CommandParameterHashMap.get(command);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
