package gvsu457;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ServerClientThreadOperations
 *
 * @author Taylor Coleman, David Fletcher
 */
public class ServerClientThreadOperations extends Thread {

    /**
     * Port Number
     */
    private static final int PORT = 33333;

    /**
     * Listening socket
     */
    private ServerSocket clientListener;

    /**
     * Max number of connections
     */
    private static final int MAX_CONNECTIONS = 100;

    /**
     * Instance of Thread Pool
     */
    private ExecutorService executorService = Executors.newCachedThreadPool();

    public static String DBXML_DIR_SHORTCUT = (new File(".").getAbsolutePath()) + File.separator + "DBXML";

    /**
     * Main method
     */
    public static void main(String[] args) {
        ServerClientThreadOperations serverClientThreadPool = new ServerClientThreadOperations();
        serverClientThreadPool.startServer();

    }

    /**
     * Constructor for ServerClientThreadOperations.
     */
    ServerClientThreadOperations() {
        try {
            clientListener = new ServerSocket(PORT);

        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Starts the server.
     */
    public void startServer() {

        //Delete all old xml data on startup.
        File curDir = new File(DBXML_DIR_SHORTCUT);
        File[] FileList = curDir.listFiles();
        for (File f : FileList) {
            if (f.getName().contains(".xml")) {
                f.delete();
                System.out.println("Removing old file: " + f.getName() + " from the DB directory");
            }
        }

        for (int i = 0; i < MAX_CONNECTIONS; i++) {
            try {
                System.out.println("Waiting for a connection...");
                ServerThread serverThread = new ServerThread(clientListener.accept());
                //serverThread.run();
                executorService.submit(serverThread);
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }

}

