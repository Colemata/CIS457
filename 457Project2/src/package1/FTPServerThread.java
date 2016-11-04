package package1;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;
import org.w3c.dom.*;

/**
 *
 */
public class FTPServerThread implements Runnable {

    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    public static String username;
    public String hostname;
    public String speed;
    public static String DBXML_DIR_SHORTCUT = System.getProperty("user.dir") + File.separator + "DBXML";
    public final String SERVER_FAILURE_TEXT = "zxczxczxc";

    //pass the socket into this thread.
    public FTPServerThread(Socket socket) {
        this.socket = socket;
        System.out.println("Client connected from: " + socket.getInetAddress());
    }

    public void run() {

        //unless we tell it otherwise, run
        while (true) {
            try {

                //set up the streams using the global socket.
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                System.out.println("Connection Established.");

                String line = in.readUTF();

                switch (line) {

                    case "search":
                        break;
                    case "retr":


                }

            } catch (Exception e) {

            }
        }
    }
}
