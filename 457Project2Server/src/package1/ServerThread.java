package package1;

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
 * This class will handle the connection between each client and the server.
 */
public class ServerThread implements Runnable {

    public static String username;
    public static String DBXML_DIR_SHORTCUT = System.getProperty("user.dir") + File.separator + "DBXML";
    public String hostname;
    public String speed;
    public int port;
    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;

    //pass the socket into this thread.
    public ServerThread(Socket socket) {
        this.socket = socket;
        System.out.println("Client connected from: " + socket.getInetAddress());
    }

    /**
     * This method will recieve the filelist from the client after they connect.
     *
     * @param in       the data input stream
     * @param username the username of the user sending the file.
     */
    private static void RetrieveFileFromServer(DataInputStream in, String username) {

        byte[] buffer = new byte[4098];

        try {

            //Make the file object for the file output stream.
            File newFile = new File(System.getProperty("user.dir") + File.separator + username + ".temp");
            FileOutputStream fos = new FileOutputStream(newFile);

            //read in the file size, this is important.
            Long filesize = in.readLong();
            int read = 0;
            int remaining = filesize.intValue();

            //while there is data being read in, read.
            while ((read = in.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                //the remaining size left is = to the remaining size minus the size of what we just read.
                remaining -= read;

                //write these bytes that are buffered to the new file.
                fos.write(buffer, 0, read);
            }

            //close the file output stream.
            fos.close();

            //Now that we have this file, let's put the contents into xml format in the proper dir.
            PutContentsInUsersExistingXMLFile(newFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Rearrange the files so that we can access and parse them later.
     *
     * @param XMLFile
     */
    public static void PutContentsInUsersExistingXMLFile(File XMLFile) {
        try {

            //Init all the dom parsing stuff.
            Document dom;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(XMLFile);
            Element doc = dom.getDocumentElement();

            //Get every node under the files tag.
            NodeList thisNodeList = doc.getElementsByTagName("file");

            //Each element under the file tags
            for (int temp = 0; temp < thisNodeList.getLength(); temp++) {
                Node nNode = thisNodeList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;
                    Element newFile = dom.createElement("file");
                    Element name = dom.createElement("name");
                    System.out.println("file  <" + eElement.getElementsByTagName("name").item(0).getTextContent() +
                            "> uploaded by user <" + username + ">");
                    name.appendChild(dom.createTextNode(eElement.getElementsByTagName("name").item(0).getTextContent()));
                    newFile.appendChild(name);
                    Element desc = dom.createElement("description");
                    desc.appendChild(dom.createTextNode(eElement.getElementsByTagName("description").item(0).getTextContent()));
                    newFile.appendChild(desc);
                }
            }

            //Save the file
            DOMSource source = new DOMSource(dom);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(DBXML_DIR_SHORTCUT + File.separator + username + ".filelist");
            transformer.transform(source, result);

            //Delete the temp file that was sent to the server from the client to keep things tidy.
            File newFile = new File(System.getProperty("user.dir") + File.separator + username + ".temp");
            newFile.delete();

        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getStackTrace());
        } catch (SAXException se) {
            System.out.println(se.getStackTrace());
        } catch (IOException ioe) {
            System.out.println(ioe.getStackTrace());
        } catch (TransformerConfigurationException e) {
            System.out.println(e.getStackTrace());
        } catch (TransformerException e) {
            System.out.println(e.getStackTrace());
        }
    }

    /**
     * This is the actually runnable method which will keep our server alive.
     */
    public void run() {

        //unless we tell it otherwise, run
        while (true) {
            try {


                //set up the streams using the global socket.
                in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));


                //The first thing we need to do is get the username, hostname, and speed and store that somewhere.
                username = in.readUTF();
                hostname = in.readUTF();
                speed = in.readUTF();
                port = in.readInt();

                //Next, we should check if this user exists, if not, create their xml file.
                File curDir = new File(DBXML_DIR_SHORTCUT);
                File[] FileList = curDir.listFiles();
                boolean userFoundFlag = false;

                for (File file : FileList) {
                    if (file.getName().equalsIgnoreCase(username + ".xmloffline")) {
                        //we have found the users file. make them appear online.
                        file.renameTo(new File(DBXML_DIR_SHORTCUT + File.separator + username + ".xml"));
                        userFoundFlag = true;
                        //Either way, now we want to write the users hostname and speed to the xml for whatever reason.
                        WriteXMLUserData(file);
                    }
                }

                if (!userFoundFlag) {
                    File newUserXML = new File(DBXML_DIR_SHORTCUT + File.separator + username + ".xml");
                    newUserXML.getParentFile().mkdirs();
                    newUserXML.createNewFile();
                    //Either way, now we want to write the users hostname and speed to the xml for whatever reason.
                    WriteXMLUserData(newUserXML);
                }


                //Now we want to send back the fact that we have registered, or found the user...
                if (userFoundFlag) {
                    out.writeUTF("We have found a data entry for user: " + username + ".");
                } else {
                    out.writeUTF("We have created a data entry for user: " + username + ".");
                }
                out.flush();

                //We know that the next thing that is going to happen is the client is going to send us a filelist.
                RetrieveFileFromServer(in, username);

                System.out.println("Handshake complete, waiting for client action...");
                while (true) {

                    //get the line in from the client (the command sent)
                    String line = in.readUTF();

                    switch (line) {
                        case "search":
                            String searchCritera = in.readUTF();

                            //Get the list of files per the user search critera.
                            ArrayList<FileData> retVal = SearchXMLForMatch(searchCritera);

                            //Send back the file data to the client.
                            for (FileData sendBack : retVal) {
                                out.writeUTF(sendBack.getSpeed());
                                out.writeUTF(sendBack.getHostname());
                                out.writeUTF(sendBack.getFilename());
                                out.writeInt(sendBack.getPort());
                            }

                            //When we are done sending file data, let the client know we are done sending stuff.
                            out.writeUTF("search_completed");
                            out.flush();
                            break;

                        case "quit":
                            socket.close();

                            //upon quit from a client delete their files.
                            curDir = new File(DBXML_DIR_SHORTCUT);
                            FileList = curDir.listFiles();
                            for (File f : FileList) {
                                if (f.getName().contains(".xml") || f.getName().contains(".filelist")) {
                                    if (f.getName().contains(username)) {
                                        f.delete();
                                    }
                                }
                            }
                            //kill the thread.
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
                //Delete this users files if they lost connection.
                File curDir = new File(DBXML_DIR_SHORTCUT);
                File[] FileList = curDir.listFiles();
                for (File f : FileList) {
                    if (f.getName().contains(".xml") || f.getName().contains(".filelist")) {
                        if (f.getName().contains(username)) {
                            f.delete();
                        }
                    }
                }
                //kill the thread.
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    /**
     * Search the xml files for a match.
     * @param searchCritera the string the client entered to search for.
     * @return the arraylist of the file data.
     */
    private ArrayList<FileData> SearchXMLForMatch(String searchCritera) {

        ArrayList<FileData> fileDataList = new ArrayList<FileData>();

        //get all files in XML dir.
        //Next, we should check if this user exists, if not, create their xml file.
        try {
            File curDir = new File(DBXML_DIR_SHORTCUT);
            File[] FileList = curDir.listFiles();

            //For each file in the current directory.
            for (File file : FileList) {

                //if it is a filelist file.
                if (file.getName().contains(".filelist")) {

                    System.out.println("Checking <" + file.getName() + "> for user <" + username + "> search params.");

                    //build the dom parser stuff.
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = null;
                    dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(file);
                    NodeList thisNodeList = doc.getElementsByTagName("file");

                    //See if each files desc meets the search criteria.
                    for (int temp = 0; temp < thisNodeList.getLength(); temp++) {
                        Node nNode = thisNodeList.item(temp);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;
                            String description = eElement.getElementsByTagName("description").item(0).getTextContent();
                            if (description.contains(searchCritera)) {

                                String hostname = getHostNameForUserName(file.getName().substring(0, file.getName().lastIndexOf(".")));
                                String filename = eElement.getElementsByTagName("name").item(0).getTextContent();
                                String speed = getSpeedForUserName(file.getName().substring(0, file.getName().lastIndexOf(".")));
                                int port = getPortForUserName(file.getName().substring(0, file.getName().lastIndexOf(".")));
                                FileData fileData = new FileData(speed, hostname, filename, port);
                                fileDataList.add(fileData);
                            }


                        }
                    }

                }

            }
        } catch (ParserConfigurationException e) {
            System.out.println(e.getStackTrace());
        } catch (SAXException e) {
            System.out.println(e.getStackTrace());
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
        return fileDataList;
    }

    /**
     * Get the port for the provided username using the xml data.
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

    private String getSpeedForUserName(String userName) {
        String speedd = "";
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
                    NodeList thisNodeList = doc.getElementsByTagName("userdetails");

                    speedd = doc.getElementsByTagName("speed").item(0).getTextContent();

                    for (int temp = 0; temp < thisNodeList.getLength(); temp++) {
                        Node nNode = thisNodeList.item(temp);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;

                            speedd = eElement.getElementsByTagName("speed").item(0).getTextContent();
                            break;
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            System.out.println(e.getStackTrace());
        } catch (SAXException e) {
            System.out.println(e.getStackTrace());
        } catch (IOException e) {
            System.out.println(e.getStackTrace());
        }
        return speedd;
    }

    /**
     * This method will grab the initial user data from the input stream and write it to their corresponding
     * file to keep their data for client to client connections.
     * @param newUserXML the file to be written to.
     */
    private void WriteXMLUserData(File newUserXML) {

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

            ele = dom.createElement("hostname");
            ele.appendChild(dom.createTextNode(hostname));
            rootEle.appendChild(ele);

            ele = dom.createElement("speed");
            ele.appendChild(dom.createTextNode(speed));
            rootEle.appendChild(ele);

            ele = dom.createElement("port");
            ele.appendChild(dom.createTextNode("" + port));
            rootEle.appendChild(ele);

            dom.appendChild(rootEle);

            //save the file.
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(newUserXML.getPath())));

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
