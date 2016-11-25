package gvsu457.Tic2.Client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 11/24/2016.
 */
public class TicTacToeClientLogic {

    /*The data input stream from the server*/
    public static DataInputStream in_server;

    /*The data output stream to the server.*/
    public static DataOutputStream out_server;

    /*The server socket to maintain a connect*/
    public static Socket server;

    public TicTacToeClientGUI ticTacToeClientGUI;

    public int playerNumber = 1;


    private int[][] gameBoard;

    /** Shortcut for image directory */
    public static String IMAGE_DIR = System.getProperty("user.dir") + File.separator + ".." + File.separator + "images";

    public TicTacToeClientLogic() {

        try {
            //First thing we want to do is connect to the server.
            server = new Socket("localhost", 8989);

            //set up the streams using the global socket.
            in_server = new DataInputStream(new BufferedInputStream(server.getInputStream()));
            out_server = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));

            ticTacToeClientGUI = new TicTacToeClientGUI("Client Game");

            for(int i = 0; i < 3; i++){
                for(int k = 0; k < 3; k++){
                    gameBoard = new int[3][3];
                    gameBoard[i][k] = -1;
                }
            }

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
                        e.printStackTrace();
                    }

                }
            }, 0, 1, TimeUnit.SECONDS);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
    public void setSpotForOtherPlayer(int x, int y) {
        gameBoard[x][y] = 2;
    }

    public void setSpotForUser(int x, int y){
            gameBoard[x][y] = playerNumber;
    }

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
}
