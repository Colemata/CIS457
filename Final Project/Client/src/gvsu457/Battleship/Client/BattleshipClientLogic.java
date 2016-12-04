package gvsu457.Battleship.Client;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 11/27/2016.
 */
public class BattleshipClientLogic implements Runnable {

    /*The data input stream from the server*/
    public static DataInputStream in_server;

    /*The data output stream to the server.*/
    public static DataOutputStream out_server;

    /*The server socket to maintain a connect*/
    public static Socket server;

    public static BattleshipClientGUI bscgui;

    public boolean doneWithGettingBoard = false;

    public String username;

    public boolean isShutdown = false;

    public BattleshipClientLogic(String username, String otherPlayerHostname, int otherPlayerPort) {

        this.username = username;

        try {
            String hostname = "localhost";
            int port = 6279;
            //First thing we want to do is connect to the server.
            server = new Socket(otherPlayerHostname, otherPlayerPort);

            //set up the streams using the global socket.
            in_server = new DataInputStream(new BufferedInputStream(server.getInputStream()));
            out_server = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        //A thread that will check for new data from the server every 1 second.
        final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {

                try {
                    if (doneWithGettingBoard) {
                        if (in_server.available() > 0) {
                            String type = in_server.readUTF();
                            if (type.equalsIgnoreCase("move")) {
                                int shipIDhit = in_server.readInt();
                                int row = in_server.readInt();
                                int col = in_server.readInt();
                                if (shipIDhit > 0) {
                                    PerformActionForShipHit(shipIDhit, row, col);
                                } else {
                                    PerformActionForMiss(row, col);
                                }
                                //after we get something, enable the buttons for a guess
                                bscgui.setAllGuessButtonsWithoutGuessesEnabled(true);
                                bscgui.switchPlayerTurn();
                            } else {
                                JOptionPane.showMessageDialog(JOptionPane.getRootFrame(), "You win!!!");
                            }
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

    private void PerformActionForShipHit(int shipIDhit, int row, int col) {
        bscgui.setHitElement(row, col, shipIDhit);
    }

    public static void main(String[] args) {
        //bscgui = new BattleshipClientGUI();
    }

    private void PerformActionForMiss(int row, int col) {
        bscgui.setMissedElement(row, col);
    }

    public void SendGameBoardDataToOtherPlayer(int[][] myGameBoard) {
        try {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    out_server.writeInt(myGameBoard[i][j]);
                    out_server.writeInt(i);
                    out_server.writeInt(j);
                }
            }
            out_server.flush();
//            ObjectOutputStream os = new ObjectOutputStream(server.getOutputStream());
//            os.writeObject(myGameBoard);
//            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void GetGameBoardFromOpponent() {

        try {
//            ObjectInputStream is = new ObjectInputStream(server.getInputStream());
            int[][] array = new int[10][10];
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    int val = in_server.readInt();
                    int row = in_server.readInt();
                    int col = in_server.readInt();
                    array[row][col] = val;
                }
            }
            bscgui.setOpponentGameArray(array);
            doneWithGettingBoard = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGuessToOtherPlayer(int shipID, int row, int col) {

        try {
            out_server.writeUTF("move");
            out_server.writeInt(shipID);
            out_server.writeInt(row);
            out_server.writeInt(col);
            out_server.flush();
            bscgui.switchPlayerTurn();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLossReportToOtherPlayer() {
        try {
            out_server.writeUTF("loser");
            out_server.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        bscgui = new BattleshipClientGUI("Welcome " + username, this);
    }

    public void closeSockets() {
        try {
            isShutdown = true;
            server.close();
        } catch (IOException e) {

        }
    }
}
