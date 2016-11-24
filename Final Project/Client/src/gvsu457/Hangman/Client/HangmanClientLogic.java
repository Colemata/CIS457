package gvsu457.Hangman.Client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    /** Shortcut for image directory */
    public static String IMAGE_DIR = System.getProperty("user.dir") + File.separator + ".." + File.separator + "images";

    /*The word the user must guess*/
    public static String word;

    public ArrayList<String> charGuessList = new ArrayList<String>();


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

            final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleWithFixedDelay(new Runnable()
            {
                @Override
                public void run()
                {
                    try {
                        if(in_server.available() > 0){
                            String guess = in_server.readUTF();
                            charGuessList.add(guess);
                            hangman.listModel.addElement(guess);
                            setDisplayForWordAndGuessList();
                            int numToShow = in_server.readInt();
                            if(numToShow == 9){
                                numToShow++;
                            }
                            if(numToShow == 6){
                                numToShow = 9;
                            }
                            setHangManImage(numToShow);

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDisplayForWordAndGuessList() {
        String display = "";

        for(int i = 0; i < word.length(); i++){
            boolean found = false;
            for(String guessChar: charGuessList){
                if(word.charAt(i) == guessChar.charAt(0)){
                    display += word.charAt(i) + " ";
                    found = true;
                }
            }
            if(!found) {
                display += "_ ";
            }
        }
        hangman.setWordDisplay(display);
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
                break;
            case 7:
                hangman.setImagePanel(new ImageIcon(IMAGE_DIR + File.separator + "set.png"));
                break;
            case 8:
                hangman.setImagePanel(new ImageIcon(IMAGE_DIR + File.separator + "guess.png"));
                break;
            case 9:
                hangman.setImagePanel(new ImageIcon(IMAGE_DIR + File.separator + "win.png"));
                hangman.setAllFieldsEnabled(false);
                break;
            case 10:
                hangman.setImagePanel(new ImageIcon(IMAGE_DIR + File.separator + "lose.png"));
                hangman.setAllFieldsEnabled(false);
                break;
        }
    }
}
