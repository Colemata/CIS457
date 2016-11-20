package gvsu457;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is to spawn threads for the clients ftp server.
 * FTPThreadPool
 *
 * @author Taylor Coleman, David Fletcher
 */
public class ClientConnectionThreadPool implements Runnable {

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
                ClientServerThread serverThread = new ClientServerThread(serverListener.accept());
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
