package gvsu457.Hangman.Client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

/**
 * Created by Administrator on 11/23/2016.
 */
public class HangmanClientLogic {

    /*The data input stream from the server*/
    public static DataInputStream in_server;

    /*The data output stream to the server.*/
    public static DataOutputStream out_server;

    /*The server socket to maintain a connect*/
    public static Socket server;

    /*The GUI*/
    public static HangmanGUI hangman;

    /*The word the user must guess*/
    public static String word;

    public HangmanClientLogic(){
        try {

            //First thing we want to do is connect to the server.
            server = new Socket("localhost", 8989);

            //set up the streams using the global socket.
            in_server = new DataInputStream(new BufferedInputStream(server.getInputStream()));
            out_server = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));

            //Get the GUI up...
            hangman = new HangmanGUI("username Client");

            //Tell the user to send the word to the client!
            setHangManImage(7);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendWordToServer(String word){

        //The client will be the one to set the word for the server to guess
        this.word = word;
        try {
            out_server.writeUTF(word);
            out_server.flush();
            setHangManImage(0);

            //after we send the word there is nothing else to do but watch...

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
}
