package gvsu457.UnusedMethods;

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
 * Created by Administrator on 11/30/2016.
 */
public class UnusedMethods {

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

    public ArrayList<String> GameList;

    /*End of transmission for a stream.*/
    public final String EOT = "end_of_transmission";

    public int port;

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
}
