package gvsu457.Battleship.Server;

import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 11/27/2016.
 */
public class BattleshipServerLogic implements Runnable{

    /*The data input stream from the server*/
    public static DataInputStream in_client;

    /*The data output stream to the server.*/
    public static DataOutputStream out_client;

    /*The server socket to maintain a connect*/
    public static ServerSocket client_connection;

    public Socket socket;

    public boolean doneWithGettingBoard = false;

    public static BattleshipServerGUI bssgui;

    public String username;

    public boolean isShutdown = false;

    public BattleshipServerLogic(int port, String username) {
        //bssgui = new BattleshipServerGUI();

        this.username = username;

        try {
            client_connection = new ServerSocket(port);
            client_connection.setReuseAddress(true);
            socket = client_connection.accept();

            //set up the streams using the global socket.
            in_client = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out_client = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            if (socket.isConnected()) {
                System.out.println("Connection Established");
            }
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
                        if (in_client.available() > 0) {
                            String type = in_client.readUTF();
                            if (type.equalsIgnoreCase("move")) {
                                int shipIDhit = in_client.readInt();
                                int row = in_client.readInt();
                                int col = in_client.readInt();
                                if (shipIDhit > 0) {
                                    PerformActionForShipHit(shipIDhit, row, col);
                                } else {
                                    PerformActionForMiss(row, col);
                                }

                                bssgui.setAllGuessButtonsWithoutGuessesEnabled(true);
                                bssgui.switchPlayerTurn();
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
        bssgui.setHitElement(row, col, shipIDhit);
    }

    private void PerformActionForMiss(int row, int col) {
        bssgui.setMissedElement(row, col);
    }


    public static void main(String[] args) {
//"Welcome " + username, this
       // bssgui = new BattleshipServerGUI();
    }

    public void SendGameBoardDataToOtherPlayer(int[][] myGameBoard) {
        try {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    out_client.writeInt(myGameBoard[i][j]);
                    out_client.writeInt(i);
                    out_client.writeInt(j);
                }
            }
            out_client.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void GetGameBoardFromOpponent() {

        try {
            int[][] array = new int[10][10];
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    int val = in_client.readInt();
                    int row = in_client.readInt();
                    int col = in_client.readInt();
                    array[row][col] = val;
                }
            }
            bssgui.setOpponentGameArray(array);
            doneWithGettingBoard = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGuessToOtherPlayer(int shipID, int row, int col) {
        try {
            out_client.writeUTF("move");
            out_client.writeInt(shipID);
            out_client.writeInt(row);
            out_client.writeInt(col);
            out_client.flush();
            bssgui.switchPlayerTurn();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendLossReportToOtherPlayer() {
        try {
            out_client.writeUTF("loser");
            out_client.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        bssgui = new BattleshipServerGUI("Welcome " + username, this);

    }

    public void closeSockets() {
        try {
            isShutdown = true;
            socket.close();
        } catch (IOException e) {

        }
    }
}
