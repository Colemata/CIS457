package gvsu457;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is to spawn threads for the clients ftp server.
 * FTPThreadPool
 *
 * @author Taylor Coleman, David Fletcher
 */
public class IssueClientConnectionForGameMatch implements Runnable {

    /**
     * A DataInputStream object
     */
    private DataInputStream in;

    /**
     * A DataOutputStream object
     */
    private DataOutputStream out;
    /** The port number */
    private static int PORT = 6279;

    private static String connectingHostName = "localhost";

    public String username;

    public String opponentName;

    public Socket OtherClient;

    public String gameTypeToPlay;


    @Override
    /**
     *
     */
    public void run() {

        try {
            if(connectingHostName.contains("/")){
                connectingHostName = connectingHostName.substring(1);
            }
            OtherClient = new Socket(connectingHostName, PORT);

            //set up the streams using the global socket.
            in = new DataInputStream(new BufferedInputStream(OtherClient.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(OtherClient.getOutputStream()));

            out.writeUTF(gameTypeToPlay);
            out.writeUTF(opponentName);
            out.writeUTF(username);
            out.flush();

            //At this point, we are connected to the other client and can start a game and send data back and fourth.

            Player them = new Player(opponentName, 1);
            Player me = new Player(username, 2);
            TicTacToeGUI ticTacToeGame = new TicTacToeGUI(them, me, OtherClient, username);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setConnectingPortNumber(int listeningPortNumber){
        this.PORT = listeningPortNumber;
    }

    public void setConnectingHostName(String connectingHostName) {
        this.connectingHostName = connectingHostName;
    }

    public void setGameTypeToPlay(String gameTypeToPlay) {
        this.gameTypeToPlay = gameTypeToPlay;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }
}
