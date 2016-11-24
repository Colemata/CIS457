package gvsu457.Hangman.Server;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Administrator on 11/23/2016.
 */
public class HangmanServerLogic {

    /*The data input stream from the server*/
    public static DataInputStream in_client;

    /*The data output stream to the server.*/
    public static DataOutputStream out_client;

    /*The server socket to maintain a connect*/
    public static ServerSocket client_connection;

    /**
     * Shortcut for image directory
     */
    public static String IMAGE_DIR = System.getProperty("user.dir") + File.separator + ".." + File.separator + "images";

    /*The word the server needs to guess*/
    public static String word;

    /*The GUI*/
    public static HangmanServerGUI hangman;

    public static ArrayList<String> charGuessList = new ArrayList<String>();

    public int missCount = 0;

    public int wordLength = 0;

    public int foundLetterCount = 0;

    HangmanServerLogic() {
        try {

            //set up the connection to the client...
            client_connection = new ServerSocket(8989);
            Socket socket = client_connection.accept();

            //set up the streams using the global socket.
            in_client = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out_client = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            //Get the GUI up..
            hangman = new HangmanServerGUI("username SERVER");

            //Listen for the word from the client to start the game...
            word = in_client.readUTF();

            wordLength = word.length();

            setDisplayForWordAndGuessList();

            hangman.setAllFieldsEnabled(true);

            //Let the user know to start guessing...
            setHangManImage(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDisplayForWordAndGuessList() {
        String display = "";
        foundLetterCount = 0;
        for(int i = 0; i < word.length(); i++){
            boolean found = false;
            for(String guessChar: charGuessList){
                if(word.charAt(i) == guessChar.charAt(0)){
                    display += word.charAt(i) + " ";
                    found = true;
                    foundLetterCount++;
                }
            }
            if(!found) {
                display += "_ ";
            }
        }

        hangman.setWordDisplay(display);
        if(wordLength == foundLetterCount){
            //the user has guessed the word correctly!
            setHangManImage(9);
        }
    }

    public static void setHangManImage(int numWrong) {

        switch (numWrong) {
            case 0:
                hangman.setImagePanel(new ImageIcon(IMAGE_DIR + File.separator + "0.png"));
                break;
            case 1:
                hangman.setImagePanel(new ImageIcon(IMAGE_DIR + File.separator + "1.png"));
                break;
            case 2:
                hangman.setImagePanel(new ImageIcon(IMAGE_DIR + File.separator + "2.png"));
                break;
            case 3:
                hangman.setImagePanel(new ImageIcon(IMAGE_DIR + File.separator + "3.png"));
                break;
            case 4:
                hangman.setImagePanel(new ImageIcon(IMAGE_DIR + File.separator + "4.png"));
                break;
            case 5:
                hangman.setImagePanel(new ImageIcon(IMAGE_DIR + File.separator + "5.png"));
                break;
            case 6:
                hangman.setImagePanel(new ImageIcon(IMAGE_DIR + File.separator + "6.png"));

                //The user has lost here, lock controls...
                hangman.setAllFieldsEnabled(false);

                break;
            case 7:
                hangman.setImagePanel(new ImageIcon(IMAGE_DIR + File.separator + "set.png"));
                break;
            case 8:
                hangman.setImagePanel(new ImageIcon(IMAGE_DIR + File.separator + "guess.png"));
                break;
            case 9:
                hangman.setImagePanel(new ImageIcon(IMAGE_DIR + File.separator + "win.png"));
                break;
        }
    }

    public boolean guessLetter(String text) {

        boolean retVal = true;

        try {
            if (!charGuessList.contains(text)) {

                if (word.contains(text)) {
                    //correct guess... display all the same chars on the reveal part
                    charGuessList.add(text);
                    retVal = false;
                    out_client.writeUTF(text);
                    out_client.writeInt(-1);
                    out_client.flush();
                } else {
                    missCount++;
                    charGuessList.add(text);
                    setHangManImage(missCount);
                    out_client.writeUTF(text);
                    out_client.writeInt(missCount);
                    out_client.flush();
                    retVal = true;
                }

            } else {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "You have already guessed this letter...");
            }
        }catch (IOException ex){
            System.out.println(ex.getStackTrace());
        }
        setDisplayForWordAndGuessList();
        return retVal;
    }
}
