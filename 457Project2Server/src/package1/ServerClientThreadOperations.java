package package1;

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

    /** Port Number */
    private static final int PORT = 33333;
    
    /** Listening socket */
    private ServerSocket serverListener;
    
    /** Max number of connections */
    private static final int MAX_CONNECTIONS = 100;
    
    /** Shortcut to DBXML */
    public static String DBXML_DIR_SHORTCUT = System.getProperty("user.dir") + File.separator + "DBXML";

    /** Instance of Thread Pool */
    private ExecutorService executorService = Executors.newCachedThreadPool();

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
            serverListener = new ServerSocket(PORT);

            //On startup of the server, we should delete all the files in the server registry.
            File curDir = new File(DBXML_DIR_SHORTCUT);
            File[] FileList = curDir.listFiles();
            for (File f : FileList) {
                if (f.getName().contains(".xml") || f.getName().contains(".filelist")) {
                    f.delete();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Starts the server.
     */
    public void startServer() {
        for(int i = 0; i < MAX_CONNECTIONS; i++) {
            try {
                System.out.println("Waiting for a connection...");
                ServerThread serverThread = new ServerThread(serverListener.accept());
                //serverThread.run();
                executorService.submit(serverThread);
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }

}
