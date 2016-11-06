package gvsu457;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


/**
 * ServerThread - This class will handle the connection between each client and the server.
 *
 * @author Taylor Coleman, David Fletcher
 */
public class ServerThread implements Runnable {

    /**
     * A DataInputStream object
     */
    private DataInputStream in;

    /**
     * A DataOutputStream object
     */
    private DataOutputStream out;

    /**
     * A socket for the connection
     */
    private Socket socket;

    /*The users username*/
    public String username;

    /**
     * Pass the socket into this thread.
     */
    public ServerThread(Socket socket) {
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

                System.out.println("Connection established from: " + socket.getInetAddress());

                //Once a user is connected, store their username and other relevant information in our DB.
                username = in.readUTF();
                StoreUserNameInDBXML(username);

                while (true) {

                    //get the line in from the client (the command sent)
                    String line = in.readUTF();

                    switch (line) {
                        case "search":
                            break;

                        case "quit":
                            socket.close();
                            Thread.currentThread().interrupt();
                            break;
                    }

                }

            } catch (IOException e) {
                System.out.println("Connection was not closed properly...");
                try {
                    in.close();
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                //kill the thread.
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}

