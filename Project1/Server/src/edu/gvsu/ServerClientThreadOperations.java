package edu.gvsu;

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

    private ExecutorService executorService = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        ServerClientThreadOperations serverClientThreadPool = new ServerClientThreadOperations();
        serverClientThreadPool.startServer();
    }

    ServerClientThreadOperations() {
        try {
            serverListener = new ServerSocket(PORT);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void startServer() {
        for(int i = 0; i < MAX_CONNECTIONS; i++) {
            try {
                System.out.println("Waiting for a connection...");
                FTPOperations ftpOp = new FTPOperations(serverListener.accept());
                ftpOp.run();
                //executorService.submit(ftpOp);
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
    }

}