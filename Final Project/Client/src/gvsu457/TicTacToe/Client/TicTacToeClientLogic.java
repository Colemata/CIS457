package gvsu457.TicTacToe.Client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by taylor.coleman on 11/24/2016.
 */
public class TicTacToeClientLogic implements Runnable {

    /*The data input stream from the server*/
    public static DataInputStream in_server;

    /*The data output stream to the server.*/
    public static DataOutputStream out_server;

    /*The server socket to maintain a connect*/
    public static Socket server;

    /*The user interface*/
    public TicTacToeClientGUI ticTacToeClientGUI;

    /*The player number used to determine whos turn it is*/
    public int playerNumber = 1;

    /*The actual game board used for determining if there is a winner*/
    private int[][] gameBoard;

    public boolean isShutdown = false;

    /** Shortcut for image directory */
    public static String IMAGE_DIR = System.getProperty("user.dir") + File.separator + ".." + File.separator + "images";

    public TicTacToeClientLogic(String username, String hostname, int port) {

        try {
            //First thing we want to do is connect to the server.
            server = new Socket(hostname, port);

            //set up the streams using the global socket.
            in_server = new DataInputStream(new BufferedInputStream(server.getInputStream()));
            out_server = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));

            //Bring up the ui.
            ticTacToeClientGUI = new TicTacToeClientGUI("Welcome " + username, this);

            //init the gameboard with all -1.
            for(int i = 0; i < 3; i++){
                for(int k = 0; k < 3; k++){
                    gameBoard = new int[3][3];
                    gameBoard[i][k] = -1;
                }
            }

            //set a timer service that will run ever 1 second to check for updates from the other client.
            final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            service.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {

                    try {
                        if(in_server.available() > 0){
                            int buttonNum = in_server.readInt();
                            performMoveForOtherPlayer(buttonNum);
                            ticTacToeClientGUI.setButtonImageForOtherPlayer(buttonNum);
                            ticTacToeClientGUI.setButtonsEnabled(true);
                            if (checkIfWinner(playerNumber)) {
                                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "You Win!!!");
                                ticTacToeClientGUI.setButtonsEnabled(false);
                            } else if (checkIfWinner(2)) {
                                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Loser!!!");
                                ticTacToeClientGUI.setButtonsEnabled(false);
                            }
                        }
                    } catch (IOException e) {
                        if(isShutdown){
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Perform a move once it has been issued from the other client.
     * @param buttonNum the button number pressed.
     */
    private void performMoveForOtherPlayer(int buttonNum) {
        switch (buttonNum){
            case 1:
                setSpotForOtherPlayer(0,0);
                break;
            case 2:
                setSpotForOtherPlayer(0,1);
                break;
            case 3:
                setSpotForOtherPlayer(0,2);
                break;
            case 4:
                setSpotForOtherPlayer(1,0);
                break;
            case 5:
                setSpotForOtherPlayer(1,1);
                break;
            case 6:
                setSpotForOtherPlayer(1,2);
                break;
            case 7:
                setSpotForOtherPlayer(2,0);
                break;
            case 8:
                setSpotForOtherPlayer(2,1);
                break;
            case 9:
                setSpotForOtherPlayer(2,2);
                break;
        }
    }

    /**
     * Set the spot on the gameboard that the other player has taken.
     * @param x the x coordinate of the button.
     * @param y the y coordinate of the button.
     */
    public void setSpotForOtherPlayer(int x, int y) {
        gameBoard[x][y] = 2;
    }

    /**
     * Set the spot for this user on the gameboard.
     * @param x the x coordinate of the button.
     * @param y the y coordinate of the button.
     */
    public void setSpotForUser(int x, int y){
            gameBoard[x][y] = playerNumber;
    }

    /**
     * Check if there is a winner for the player passed.
     * @param player the player to check for a win.
     * @return true if there is a win for this player.
     */
    boolean checkIfWinner(int player) {

        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                if ((gameBoard[i][0] == player && gameBoard[i][1] == player && gameBoard[i][2] == player) ||
                        (gameBoard[0][k] == player && gameBoard[1][k] == player && gameBoard[2][k] == player)) {
                    return true;
                }

            }
        }
        if ((gameBoard[0][0] == player && gameBoard[1][1] == player && gameBoard[2][2] == player) ||
                (gameBoard[2][0] == player && gameBoard[1][1] == player && gameBoard[0][2] == player))
            return true;
        return false;
    }

    /**
     * Send data to the other player.
     * @param buttonNumber the button number to send to the other user.
     */
    public void sendDataToOtherPlayer(int buttonNumber){
        try {
            out_server.writeInt(buttonNumber);
            out_server.flush();
            ticTacToeClientGUI.setButtonsEnabled(false);
            if (checkIfWinner(playerNumber)) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "You Win!!!");
                ticTacToeClientGUI.setButtonsEnabled(false);
            } else if (checkIfWinner(2)) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Loser!!!");
                ticTacToeClientGUI.setButtonsEnabled(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }

    public void closeSockets() {
        try {
            isShutdown = true;
            server.close();
        } catch (IOException e) {

        }
    }

}
