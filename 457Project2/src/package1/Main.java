package package1;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

	private static ExecutorService executorService = Executors.newCachedThreadPool();

	public static void main(String[] args){
		FTPThreadPool serverClientThreadPool = new FTPThreadPool();
		//serverClientThreadPool.run();
		executorService.submit(serverClientThreadPool);
		new GUI();
	}
}
