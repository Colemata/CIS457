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
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;


/**
 * ServerThread - This class will handle the connection between each client and the server.
 *
 * @author Taylor Coleman, David Fletcher
 */
public class ServerThread implements Runnable {

    /*A DataInputStream object*/
    private DataInputStream in;

    /*A DataOutputStream object*/
    private DataOutputStream out;

    /*A socket for the connection*/
    private Socket socket;

    /*The users username*/
    public String username;

    /*Shortcut for DBXML*/
    public static String DBXML_DIR_SHORTCUT = (new File(".").getAbsolutePath()) + File.separator + "DBXML";

    /*The list of games that the user can play*/
    public ArrayList<String> GameList;

    /*End of transmission for a stream.*/
    public final String EOT = "end_of_transmission";

    /*The port number that the client want's to connect to peers on.*/
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
                            String user = in.readUTF();
                            RemovePlayerFromAllQueues(user);
                            break;
                        case "kill":
                            RemovePlayerFromAllQueues(username);
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

                    //Delete this users files if they lost connection.
                    File curDir = new File(DBXML_DIR_SHORTCUT);
                    File[] FileList = curDir.listFiles();
                    for (File f : FileList) {
                        if (f.getName().contains(".xml")) {
                            if (f.getName().contains(username)) {
                                f.delete();
                            }
                        }
                    }
                    //kill the thread.
                    RemovePlayerFromAllQueues(username);
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

    private void RemovePlayerFromAllQueues(String playerToRemove) {
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
                    if (pName.equals(playerToRemove)) {
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
                System.out.println("User: " + username + " has been paired against " + opponentName + " for a game of " + game + ".");

                //if a match has been made, we need to determine who is going to connect to who to start the game.
                //I vote we just do whoever is alphabetically first.

                if (username.compareToIgnoreCase(opponentName) < -1) {
                    //this means username is less than opponent name.
                    //username will connect to opponentName
                    GetConnectionInfoForClientToClientConnection(opponentName, game);
                } else {
                    out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                    out.writeUTF("skip");
                    out.writeUTF(game);
                    out.flush();
                    //GetConnectionInfoForClientToClientConnection(username, game);
                }

            }else{
                RemovePlayerFromAllQueues(username);
                out.writeUTF("no_match_found");
                out.flush();
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

    private void GetConnectionInfoForClientToClientConnection(String opponentName, String game) {

        String opponentAddress;
        int opponentPort;

        //methods merged from project2: getPortForUserName, getHostNameForUserName, getSpeedForUserName (can use for something else).
        try {
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            opponentPort = getPortForUserName(opponentName);
            opponentAddress = getHostNameForUserName(opponentName);
            out.writeUTF(opponentName);
            out.writeUTF(username);
            out.writeUTF(opponentAddress);
            out.writeInt(opponentPort);
            out.writeUTF(game);
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Returns the hostname for the username.
     *
     * @param userName
     * @return String
     */
    private String getHostNameForUserName(String userName) {
        String hostnamee = "";
        try {

            File curDir = new File(DBXML_DIR_SHORTCUT);
            File[] FileList = curDir.listFiles();

            for (File file : FileList) {
                if (file.getName().equalsIgnoreCase(userName + ".xml")) {
                    Document dom;
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    dom = db.parse(file);
                    Element doc = dom.getDocumentElement();
                    //NodeList thisNodeList = doc.getElementsByTagName("userdetails");
                    hostnamee = doc.getElementsByTagName("hostname").item(0).getTextContent();
                }
            }
        } catch (ParserConfigurationException e) {
            System.out.println(e.getStackTrace());
        } catch (SAXException e) {
            System.out.println(e.getStackTrace());
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
        return hostnamee;
    }

    /**
     * Get the port for the provided username using the xml data.
     *
     * @param userName the provided username to get the port for.
     * @return the port number.
     */
    private int getPortForUserName(String userName) {
        int portNum = 0;
        try {

            File curDir = new File(DBXML_DIR_SHORTCUT);
            File[] FileList = curDir.listFiles();

            //For each file
            for (File file : FileList) {
                //If the file is the users xml file.
                if (file.getName().equalsIgnoreCase(userName + ".xml")) {

                    //build the dom and get the port number.
                    Document dom;
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    dom = db.parse(file);
                    Element doc = dom.getDocumentElement();
                    portNum = Integer.parseInt(doc.getElementsByTagName("port").item(0).getTextContent());
                }
            }
        } catch (ParserConfigurationException e) {
            System.out.println(e.getStackTrace());
        } catch (SAXException e) {
            System.out.println(e.getStackTrace());
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
        return portNum;
    }

    private String CheckQueueForOtherPlayers() {
        boolean matchFound = false;
        int maxWaitTime = 0;
        while (true) {
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

                    NodeList nodes = dom.getElementsByTagName("player");

                    String player1 = null;
                    boolean OneSet = false;
                    String player2 = null;
                    //we know there is a match if len is 2 or more.
                    if (nodes.getLength() >= 2) {
                        //pick any two (if we had more time we should first in first out this).
                        for (int i = 0; i < nodes.getLength(); i++) {

                            Element player = (Element) nodes.item(i);
                            Element name = (Element) player.getElementsByTagName("name").item(0);
                            String pName = name.getTextContent();
                            if (player1 == null) {
                                player1 = pName;
                            } else {
                                player2 = pName;
                            }

                            if (player1 != null && player2 != null) {
                                matchFound = true;
                                break;
                            }
                        }
                    }


                    if (matchFound == true) {
                        if (!player1.equalsIgnoreCase(username)) {
                            return player1;
                        } else {
                            return player2;
                        }
                    }

                }
                Thread.sleep(1000);
                maxWaitTime++;
                if(maxWaitTime == 60){
                    System.out.println("No matches found for user: " + username + " within the alloted time...");
                    return "";
                }
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
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

