package gvsu457;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;


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

    /**
     * The users username
     */
    public String username;

    /**
     * Shortcut for DBXML
     */
    public static String DBXML_DIR_SHORTCUT = (new File(".").getAbsolutePath()) + File.separator + "DBXML";

    public HashMap<String, String> GameList;

    /*End of transmission for a stream.*/
    public final String EOT = "end_of_transmission";

    /**
     * Pass the socket into this thread.
     */
    public ServerThread(Socket socket) {
        this.socket = socket;
        System.out.println("Client connected from: " + socket.getInetAddress());
    }

    public void run() {

        InitalizeDataStructuresForServerCommands();

        //unless we tell it otherwise, run
        while (true) {
            try {

                //set up the streams using the global socket.
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

                System.out.println("Connection established from: " + socket.getInetAddress());

                //Once a user is connected, store their username and other relevant information in our DB.
                username = in.readUTF();
                StoreUserInDBXML(username, "" + socket.getInetAddress());

                while (true) {

                    //get the line in from the client (the command sent)
                    System.out.println("Waiting on command from: " + socket.getInetAddress());
                    String line = in.readUTF();

                    switch (line) {
                        case "games":
                            ListGamesForClient();
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

    private void ListGamesForClient() {
        try {
            for (String game : GameList.keySet()) {
                out.writeUTF(game);
            }
            out.writeUTF(EOT);
            out.flush();
        } catch (IOException e) {
            System.out.println("Something went wrong sending game list to client: " + socket.getInetAddress());
        }
    }

    private void InitalizeDataStructuresForServerCommands() {

        GameList = new HashMap<String, String>();

        GameList.put("tictactoe", "");
        GameList.put("hangman", "");
        GameList.put("battleship", "");
        GameList.put("minesweeper", "");
        GameList.put("placeholder", "");
    }

    private void StoreUserInDBXML(String username, String address) {

        File userSpecificsFile = new File(DBXML_DIR_SHORTCUT + File.separator + username + ".xml");

        //Build the dom.
        Document dom;
        Element ele = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {

            //Grab the specific user data and put it in the xml file.
            db = dbf.newDocumentBuilder();
            dom = db.newDocument();
            Element rootEle = dom.createElement("userdetails");

            ele = dom.createElement("username");
            ele.appendChild(dom.createTextNode(username));
            rootEle.appendChild(ele);

            ele = dom.createElement("hostname");
            ele.appendChild(dom.createTextNode(address));
            rootEle.appendChild(ele);
//
//            ele = dom.createElement("port");
//            ele.appendChild(dom.createTextNode("" + port));
//            rootEle.appendChild(ele);

            dom.appendChild(rootEle);

            //save the file.
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(userSpecificsFile.getPath())));

        } catch (ParserConfigurationException e) {
            System.out.println(e.getStackTrace());
        } catch (TransformerConfigurationException e) {
            System.out.println(e.getStackTrace());
        } catch (TransformerException e) {
            System.out.println(e.getStackTrace());
        } catch (FileNotFoundException e) {
            System.out.println(e.getStackTrace());
        }
    }

}

