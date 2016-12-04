package gvsu457.TicTacToe.Server;


import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The tic tac toe main operations performed by the user.
 *
 * Created by taylor.coleman on 11/24/2016.
 */
public class TicTacToeServerLogic implements Runnable {

    /*The data input stream from the server*/
    public static DataInputStream in_client;

    /*The data output stream to the server.*/
    public static DataOutputStream out_client;

    /*The server socket to maintain a connect*/
    public static ServerSocket client_connection;

    /*The player number used to determine moves*/
    public int playerNumber = 2;

    /*The gameboard used to keep score and check for wins*/
    private int[][] gameBoard;

    /*The user interface*/
    public TicTacToeServerGUI ticTacToeServerGUI;

    public boolean isShutdown = false;

    /*Shortcut for image directory*/
    public static String IMAGE_DIR = System.getProperty("user.dir") + File.separator + ".." + File.separator + "images";

    public TicTacToeServerLogic(int port, String username) {

        //set up the connection to the client...
        try {
            client_connection = new ServerSocket(port);
            client_connection.setReuseAddress(true);
            Socket socket = client_connection.accept();

            //set up the streams using the global socket.
            in_client = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out_client = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Bring up the ui.
        ticTacToeServerGUI = new TicTacToeServerGUI("Welcome " + username, this);

        gameBoard = new int[3][3];

        //init the gameboard to all -1.
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                gameBoard[i][k] = 0;
            }
        }

        //A thread that will check for new data from the server every 1 second.
        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {

                try {
                    if (in_client.available() > 0) {
                        int buttonNum = in_client.readInt();
                        performMoveForOtherPlayer(buttonNum);
                        ticTacToeServerGUI.setButtonImageForOtherPlayer(buttonNum);
                        ticTacToeServerGUI.setButtonsEnabled(true);
                        if (checkIfWinner(playerNumber)) {
                            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "You Win!!!");
                            ticTacToeServerGUI.setButtonsEnabled(false);
                        } else if (checkIfWinner(1)) {
                            JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Loser!!!");
                            ticTacToeServerGUI.setButtonsEnabled(false);
                        }
                    }
                } catch (IOException e) {
                    if(isShutdown){
                        Thread.currentThread().interrupt();
                    }
                }finally {
                    if(isShutdown){
                        Thread.currentThread().interrupt();
                    }
                }

            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    /**
     * Set the spot for this user on the gameboard.
     * @param x the x coordinate of the button.
     * @param y the y coordinate of the button.
     */
    public void setSpotForUser(int x, int y) {
        gameBoard[x][y] = playerNumber;
    }

    /**
     * Set the spot on the gameboard that the other player has taken.
     * @param x the x coordinate of the button.
     * @param y the y coordinate of the button.
     */
    public void setSpotForOtherPlayer(int x, int y) {
        gameBoard[x][y] = 1;
    }

    public int getValueForSpot(int row, int col){
        return gameBoard[row][col];
    }

    /**
     * Perform a move once it has been issued from the other client.
     * @param buttonNum the button number pressed.
     */
    private void performMoveForOtherPlayer(int buttonNum) {
        switch (buttonNum) {
            case 1:
                setSpotForOtherPlayer(0, 0);
                break;
            case 2:
                setSpotForOtherPlayer(0, 1);
                break;
            case 3:
                setSpotForOtherPlayer(0, 2);
                break;
            case 4:
                setSpotForOtherPlayer(1, 0);
                break;
            case 5:
                setSpotForOtherPlayer(1, 1);
                break;
            case 6:
                setSpotForOtherPlayer(1, 2);
                break;
            case 7:
                setSpotForOtherPlayer(2, 0);
                break;
            case 8:
                setSpotForOtherPlayer(2, 1);
                break;
            case 9:
                setSpotForOtherPlayer(2, 2);
                break;
        }
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
    public void sendDataToOtherPlayer(int buttonNumber) {
        try {
            out_client.writeInt(buttonNumber);
            out_client.flush();
            ticTacToeServerGUI.setButtonsEnabled(false);
            if (checkIfWinner(playerNumber)) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "You Win!!!");
                ticTacToeServerGUI.setButtonsEnabled(false);
            } else if (checkIfWinner(1)) {
                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "Loser!!!");
                ticTacToeServerGUI.setButtonsEnabled(false);
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
            client_connection.close();
        } catch (IOException e) {

        }
    }

}
