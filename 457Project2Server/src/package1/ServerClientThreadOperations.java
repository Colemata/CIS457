package package1;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 9/23/2016.
 */
/*
 * This class houses the server socket itself.  Handles connecting to multiple clients.
 */
public class ServerClientThreadOperations extends Thread {

    private static final int PORT = 33333;
    private ServerSocket serverListener;
    private static final int MAX_CONNECTIONS = 100;
    public static String DBXML_DIR_SHORTCUT = System.getProperty("user.dir") + File.separator + "DBXML";

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        ServerClientThreadOperations serverClientThreadPool = new ServerClientThreadOperations();
        serverClientThreadPool.startServer();
    }

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