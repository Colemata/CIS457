package gvsu457;


        import java.io.*;
        import java.net.Socket;

/**
 * This is the clients server thread which will handle comms between itself and other clients
 * FTPServerThread
 *
 * @author David Fletcher, Taylor Coleman
 */
public class ClientServerThread implements Runnable {

    /**
     * DataInputStream object
     */
    private DataInputStream in;

    /**
     * DataOutputStream object
     */
    private DataOutputStream out;

    /**
     * Socket object
     */
    private Socket socket;

    /**
     * String for the username
     */
    public static String username;

    /**
     * String for the hostname
     */
    public String hostname;

    /**
     * String for the speed
     */
    public String speed;

    /**
     * Shortcut for DBXML
     */
    public static String DBXML_DIR_SHORTCUT = System.getProperty("user.dir") + File.separator + "DBXML";

    /**
     * SERVER_FAILURE_TEXT
     */
    public final String SERVER_FAILURE_TEXT = "zxczxczxc";

    public String ourName;

    public String opponentName;

    //pass the socket into this thread.
    public ClientServerThread(Socket socket) {
        this.socket = socket;
        System.out.println("Client connected from: " + socket.getInetAddress());
    }

    /**
     * The runnable for the client server
     * Runs the FTPServerThread
     */
    public void run() {

        //unless we tell it otherwise, run
        while (true) {
            try {

                //set up the streams using the global socket.
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                System.out.println("Connection Established.");

                String game = in.readUTF();
                ourName = in.readUTF();
                opponentName = in.readUTF();

                //These are the commands from the other clients.
                switch (game) {

                    case "tictactoe":
                        Player me = new Player(ourName, 1);
                        Player them = new Player(opponentName, 2);
                        TicTacToeGUI ticTacToeGame = new TicTacToeGUI(me, them, socket, ourName, true);
                        break;
                    case "hangman":
                        System.out.println("GAME STARTEDDDDDD " + game);
                        break;
                    case "battleship":
                        System.out.println("GAME STARTEDDDDDD " + game);
                        break;
                    case "minesweeper":
                        System.out.println("GAME STARTEDDDDDD " + game);
                        break;
                    case "placeholder":
                        System.out.println("GAME STARTEDDDDDD " + game);
                        break;

                }

            } catch (Exception e) {

            }
        }
    }

    /**
     * If we get a quit command, we are going to call this method to disconnect from the other client.
     * Disconnects from a client.
     */
    private void DisconnectFromOtherClient() {

        //flush and shutdown the sockets, not sure why it even matters.
        try {
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
        //kill the thread.
        Thread.currentThread().interrupt();
    }

}