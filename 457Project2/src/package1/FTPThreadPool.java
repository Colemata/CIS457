package package1;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 11/3/2016.
 */
public class FTPThreadPool implements Runnable {

    private static final int PORT = 6279;
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
                //serverThread.run();
                executorService.submit(serverThread);
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }
}
