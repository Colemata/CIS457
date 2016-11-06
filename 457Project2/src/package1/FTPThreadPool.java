package package1;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class is to spawn threads for the clients ftp server.
 */
public class FTPThreadPool implements Runnable {

    private static int PORT = 6279;
    private ServerSocket serverListener;
    private static final int MAX_CONNECTIONS = 100;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    @Override
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

    public void setListeningPortNumber(int listeningPortNumber){
        this.PORT = listeningPortNumber;
    }
}
