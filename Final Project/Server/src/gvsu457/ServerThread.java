package gvsu457;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


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

    public ArrayList<String> GameList;

    /*End of transmission for a stream.*/
    public final String EOT = "end_of_transmission";

    public int port;

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
                port = in.readInt();
                StoreUserInDBXML(username, "" + socket.getInetAddress(), port);

                while (true) {

                    //get the line in from the client (the command sent)
                    System.out.println("Waiting on command from: " + socket.getInetAddress());
                    String line = in.readUTF();
                    int gameNumber;

                    switch (line) {
                        case "games":
                            ListGamesForClient();
                            break;

                        case "quit":
                            socket.close();
                            Thread.currentThread().interrupt();
                            break;
                        case "play":
                            gameNumber = in.readInt();
                            PlayAGameWithSomeone(gameNumber);
                            break;
                        case "remove":
                            gameNumber = in.readInt();
                            RemoveFromGameQueueForGameNumber(gameNumber);
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
                } finally {
                    //kill the thread.
                    RemovePlayerFromAllQueues();
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    private void PlayAGameWithSomeone(int gameNumber) {

        System.out.println("User: " + username + " has requested to play game #" + gameNumber + ".");

        switch (gameNumber) {
            case 0:
                //tictactoe
                AddUserToQueueForGame("tictactoe", username);
                break;
            case 1:
                //hangman
                AddUserToQueueForGame("hangman", username);
                break;
            case 2:
                //battleship
                AddUserToQueueForGame("battleship", username);
                break;
            case 3:
                //minesweeper
                AddUserToQueueForGame("minesweeper", username);
                break;
            case 4:
                //placeholder
                AddUserToQueueForGame("placeholder", username);
                break;
            case 5:
                break;
        }
    }

    private void RemoveFromGameQueueForGameNumber(int gameNumber) {

        System.out.println("User: " + username + " being removed from game #" + gameNumber + ".");

        switch (gameNumber) {
            case 0:
                //tictactoe
                RemovePlayerFromGameQueue("tictactoe");
                break;
            case 1:
                //hangman
                RemovePlayerFromGameQueue("hangman");
                break;
            case 2:
                //battleship
                RemovePlayerFromGameQueue("battleship");
                break;
            case 3:
                //minesweeper
                RemovePlayerFromGameQueue("minesweeper");

                break;
            case 4:
                //placeholder
                RemovePlayerFromGameQueue("placeholder");
                break;
            case 5:
                break;
        }
    }

    private void RemovePlayerFromGameQueue(String game) {
        try {

            //for each game we are going to remove, this username.

            File queueFile = new File(DBXML_DIR_SHORTCUT + File.separator + game + ".queue");

            //Build the dom.
            Document dom;
            Element ele = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            db = dbf.newDocumentBuilder();
            dom = db.parse(queueFile);

            // <person>
            NodeList nodes = dom.getElementsByTagName("player");

            for (int i = 0; i < nodes.getLength(); i++) {

                Element player = (Element) nodes.item(i);
                // <name>
                Element name = (Element) player.getElementsByTagName("name").item(0);
                String pName = name.getTextContent();
                if (pName.equals(username)) {
                    player.getParentNode().removeChild(player);
                }
            }

            //save the file.
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(queueFile.getPath())));

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    private void RemovePlayerFromAllQueues() {
        try {

            //for each game we are going to remove, this username.
            for (String game : GameList) {

                File queueFile = new File(DBXML_DIR_SHORTCUT + File.separator + game + ".queue");

                //Build the dom.
                Document dom;
                Element ele = null;
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = null;
                db = dbf.newDocumentBuilder();
                dom = db.parse(queueFile);

                // <person>
                NodeList nodes = dom.getElementsByTagName("player");

                for (int i = 0; i < nodes.getLength(); i++) {

                    Element player = (Element) nodes.item(i);
                    // <name>
                    Element name = (Element) player.getElementsByTagName("name").item(0);
                    String pName = name.getTextContent();
                    if (pName.equals(username)) {
                        player.getParentNode().removeChild(player);
                    }
                }

                //save the file.
                Transformer tr = TransformerFactory.newInstance().newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty(OutputKeys.METHOD, "xml");
                tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(queueFile.getPath())));

            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }

    }

    private void AddUserToQueueForGame(String game, String username) {
        try {

            File queueFile = new File(DBXML_DIR_SHORTCUT + File.separator + game + ".queue");

            //Build the dom.
            Document dom;
            Element ele = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            db = dbf.newDocumentBuilder();
            dom = db.parse(queueFile);

            Element dataTag = dom.getDocumentElement();
            Element playerTag = (Element) dataTag.getElementsByTagName("players").item(0);

            Element newPlayer = dom.createElement("player");

            Element name = dom.createElement("name");
            name.setTextContent(username);

            Element ip = dom.createElement("ip");
            ip.setTextContent(String.valueOf(socket.getInetAddress()));

            Element portElement = dom.createElement("port");
            portElement.setTextContent(String.valueOf(port));

            newPlayer.appendChild(name);
            newPlayer.appendChild(ip);
            newPlayer.appendChild(portElement);

            playerTag.appendChild(newPlayer);

            //save the file.
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(queueFile.getPath())));

            System.out.println("User: " + username + " has been added to the queue for " + game + ".");

            String opponentName = CheckQueueForOtherPlayers();
            if (!opponentName.isEmpty()) {
                System.out.println("User: " + username + " has been paired against " + opponentName + " for a game of + " + game + ".");
            } else {
                System.out.println("User: " + username + " is waiting for an opponent for " + game + "...");
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private String CheckQueueForOtherPlayers() {
        try {
            //for each game we are going to remove, this username.
            for (String game : GameList) {

                File queueFile = new File(DBXML_DIR_SHORTCUT + File.separator + game + ".queue");

                //Build the dom.
                Document dom;
                Element ele = null;
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = null;
                db = dbf.newDocumentBuilder();
                dom = db.parse(queueFile);

                // <person>
                NodeList nodes = dom.getElementsByTagName("player");

                String player1 = null;
                boolean OneSet = false;
                String player2 = null;
                //we know there is a match if len is 2 or more.
                if (nodes.getLength() >= 2) {
                    //pick any two (if we had more time we should first in first out this).
                    for (int i = 0; i < nodes.getLength(); i++) {

                        Element player = (Element) nodes.item(i);
                        // <name>
                        Element name = (Element) player.getElementsByTagName("name").item(0);
                        String pName = name.getTextContent();
                        if (player1 == null) {
                            player1 = pName;
                        } else {
                            player2 = pName;
                        }

                        if(player1 != null && player2 != null) {
                            break;
                        }
                    }
                }

                //TODO: This is where I left off, just run this method over and over again until a match is found
                //we aren't going to provide multiqueuing for this project.

                
                //MatchedPlayers = new MatchedPlayers(game, player1, player2);

            }
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void ListGamesForClient() {
        try {
            for (String game : GameList) {
                out.writeUTF(game);
            }
            out.writeUTF(EOT);
            out.flush();
        } catch (IOException e) {
            System.out.println("Something went wrong sending game list to client: " + socket.getInetAddress());
        }
    }

    private void InitalizeDataStructuresForServerCommands() {

        GameList = new ArrayList<String>();

        GameList.add("tictactoe");
        GameList.add("hangman");
        GameList.add("battleship");
        GameList.add("minesweeper");
        GameList.add("placeholder");


    }

    private void StoreUserInDBXML(String username, String address, int listeningPort) {

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

            ele = dom.createElement("port");
            ele.appendChild(dom.createTextNode("" + listeningPort));
            rootEle.appendChild(ele);

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

