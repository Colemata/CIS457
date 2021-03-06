package package1;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
<<<<<<< HEAD
 * This class is to spawn threads for the clients ftp server.
=======
 * FTPThreadPool 
 *
 * @author Taylor Coleman, David Fletcher
>>>>>>> d679fe22e7e27795e31eed0db5ed32b2589fb123
 */
public class FTPThreadPool implements Runnable {

    /** The port number */
    private static int PORT = 6279;
    
    /** A serversocket listener */
    private ServerSocket serverListener;
    
    /** Max number of connections */
    private static final int MAX_CONNECTIONS = 100;

    /** Thread Pool Object */
    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
    /**
     * 
     */
    public void run() {

        try {
            serverListener = new ServerSocket(PORT);
        } catch (IOException e) {
            throw new RuntimeException();
        }

        for(int i = 0; i < MAX_CONNECTIONS; i++) {
            try {
                System.out.println("Waiting for a connection...");
                FTPServerThread serverThread = new FTPServerThread(serverListener.accept());
                executorService.submit(serverThread);
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }

    /**
     * Sets the listening port number.
     *
     * @param int listening port number
     */
    public void setListeningPortNumber(int listeningPortNumber){
        this.PORT = listeningPortNumber;
    }
}
