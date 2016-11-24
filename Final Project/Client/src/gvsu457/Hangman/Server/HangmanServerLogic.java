package gvsu457.Hangman.Server;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

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


    /*The word the server needs to guess*/
    public static String word;

    /*The GUI*/
    public static HangmanServerGUI hangman;

    public int missCount = 0;

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

            //Let the user know to start guessing...
            setHangManImage(0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setHangManImage(int numWrong) {

        switch (numWrong) {
            case 0:
                hangman.setImagePanel(new ImageIcon("C:\\CIS457\\Final Project\\Client\\src\\gvsu457\\Hangman\\0.png"));
                break;
            case 1:
                hangman.setImagePanel(new ImageIcon("C:\\CIS457\\Final Project\\Client\\src\\gvsu457\\Hangman\\1.png"));
                break;
            case 2:
                hangman.setImagePanel(new ImageIcon("C:\\CIS457\\Final Project\\Client\\src\\gvsu457\\Hangman\\2.png"));
                break;
            case 3:
                hangman.setImagePanel(new ImageIcon("C:\\CIS457\\Final Project\\Client\\src\\gvsu457\\Hangman\\3.png"));
                break;
            case 4:
                hangman.setImagePanel(new ImageIcon("C:\\CIS457\\Final Project\\Client\\src\\gvsu457\\Hangman\\4.png"));
                break;
            case 5:
                hangman.setImagePanel(new ImageIcon("C:\\CIS457\\Final Project\\Client\\src\\gvsu457\\Hangman\\5.png"));
                break;
            case 6:
                hangman.setImagePanel(new ImageIcon("C:\\CIS457\\Final Project\\Client\\src\\gvsu457\\Hangman\\6.png"));
                break;
            case 7:
                hangman.setImagePanel(new ImageIcon("C:\\CIS457\\Final Project\\Client\\src\\gvsu457\\Hangman\\set.png"));
                break;
            case 8:
                hangman.setImagePanel(new ImageIcon("C:\\CIS457\\Final Project\\Client\\src\\gvsu457\\Hangman\\guess.png"));
                break;
        }
    }

    public void guessLetter(String text) {
        if(word.contains(text)){
            //correct guess... display all the same chars on the reveal part
        }else{
            missCount++;
            setHangManImage(missCount);
        }
    }
}
