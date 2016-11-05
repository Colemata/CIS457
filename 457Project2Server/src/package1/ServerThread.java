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
public class ServerThread implements Runnable {

    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;
    public static String username;
    public String hostname;
    public String speed;
    public static String DBXML_DIR_SHORTCUT = System.getProperty("user.dir") + File.separator + "DBXML";
    public final String SERVER_FAILURE_TEXT = "zxczxczxc";

    //pass the socket into this thread.
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


                //The first thing we need to do is get the username, hostname, and speed and store that somewhere.
                username = in.readUTF();
                hostname = in.readUTF();
                speed = in.readUTF();


                //Next, we should check if this user exists, if not, create their xml file.
                File curDir = new File(DBXML_DIR_SHORTCUT);
                File[] FileList = curDir.listFiles();
                boolean userFoundFlag = false;

                for (File file : FileList) {
                    if(file.getName().equalsIgnoreCase(username + ".xmloffline")){
                        //we have found the users file. make them appear online.
                        file.renameTo(new File(DBXML_DIR_SHORTCUT + File.separator + username + ".xml"));
                        userFoundFlag = true;
                        //Either way, now we want to write the users hostname and speed to the xml for whatever reason.
                        WriteXMLUserData(file);
                    }
                }

                if(!userFoundFlag){
                    File newUserXML = new File(DBXML_DIR_SHORTCUT + File.separator + username + ".xml");
                    newUserXML.getParentFile().mkdirs();
                    newUserXML.createNewFile();
                    //Either way, now we want to write the users hostname and speed to the xml for whatever reason.
                    WriteXMLUserData(newUserXML);
                }


                //Now we want to send back the fact that we have registered, or found the user...
                if(userFoundFlag) {
                    out.writeUTF("We have found a data entry for user: " + username + ".");
                }else{
                    out.writeUTF("We have created a data entry for user: " + username + ".");
                }

                out.flush();

                RetrieveFileFromServer(in, username);

                System.out.println("Handshake complete, waiting for client action...");
                while (true) {

//                    byte[] buffer;
//                    String cmd = "";
//                    String filename = "";

                    //get the line in from the client
                    String line = in.readUTF();

                    switch (line) {

                        case "search":
                            String searchCritera = in.readUTF();
                            ArrayList<FileData> retVal = SearchXMLForMatch(searchCritera);

                            for(FileData sendBack: retVal){
                                out.writeUTF(sendBack.getSpeed());
                                out.writeUTF(sendBack.getHostname());
                                out.writeUTF(sendBack.getFilename());
                            }

                            out.writeUTF("search_completed");
                            out.flush();
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
                //Next, we should check if this user exists, if not, create their xml file.
                File curDir = new File(DBXML_DIR_SHORTCUT);
                File[] FileList = curDir.listFiles();
                for (File f : FileList)
                    if(f.getName().contains(".xml") || f.getName().contains(".filelist")) {
                        f.delete();
                    }
                //kill the thread.
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private ArrayList<FileData> SearchXMLForMatch(String searchCritera) {

        ArrayList<FileData> fileDataList = new ArrayList<FileData>();

        //get all files in XML dir.
        //Next, we should check if this user exists, if not, create their xml file.
        try {
        File curDir = new File(DBXML_DIR_SHORTCUT);
        File[] FileList = curDir.listFiles();

        for(File file: FileList){
            if(file.getName().contains(".filelist")){

                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = null;
                dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(file);
                NodeList thisNodeList = doc.getElementsByTagName("file");

                for (int temp = 0; temp < thisNodeList.getLength(); temp++) {
                    Node nNode = thisNodeList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        String description = eElement.getElementsByTagName("description").item(0).getTextContent();
                        if(description.contains(searchCritera)) {

                            String hostname = getHostNameForUserName(file.getName().substring(0, file.getName().lastIndexOf(".")));
                            String filename = eElement.getElementsByTagName("name").item(0).getTextContent();
                            String speed = getSpeedForUserName(file.getName().substring(0, file.getName().lastIndexOf(".")));
                            FileData fileData = new FileData(speed, hostname, filename);
                            fileDataList.add(fileData);
                        }


                    }
                }

            }

        }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileDataList;
    }

    private String getHostNameForUserName(String userName) {
        try {
            String hostname;
            File curDir = new File(DBXML_DIR_SHORTCUT);
            File[] FileList = curDir.listFiles();

            for(File file: FileList) {
                if (file.getName().equalsIgnoreCase(userName + ".xml")) {
                    Document dom;
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    dom = db.parse(file);
                    Element doc = dom.getDocumentElement();
                    NodeList thisNodeList = doc.getElementsByTagName("userdetails");

                    for (int temp = 0; temp < thisNodeList.getLength(); temp++) {
                        Node nNode = thisNodeList.item(temp);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;

                            hostname = eElement.getElementsByTagName("hostname").item(0).getTextContent();
                            break;
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hostname;
    }

    private String getSpeedForUserName(String userName) {
        try {
            String speed;
            File curDir = new File(DBXML_DIR_SHORTCUT);
            File[] FileList = curDir.listFiles();

            for(File file: FileList) {
                if (file.getName().equalsIgnoreCase(userName + ".xml")) {
                    Document dom;
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    dom = db.parse(file);
                    Element doc = dom.getDocumentElement();
                    NodeList thisNodeList = doc.getElementsByTagName("userdetails");

                    for (int temp = 0; temp < thisNodeList.getLength(); temp++) {
                        Node nNode = thisNodeList.item(temp);
                        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement = (Element) nNode;

                            speed = eElement.getElementsByTagName("speed").item(0).getTextContent();
                            break;
                        }
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return speed;
    }

    //same thing here with the current dir as below, maybe there is a better way. (does this way work on linux?)
    //
    private void GetFileForClient(DataOutputStream out, String filename) {

        File dir = new File(".");
        File fileToSend = new File(dir, filename);
        int n = 0;

        //init buffer, apparently it's ok to hardcode the size.
        byte[] buffer = new byte[4098];
        try {

            //file in will input the file from the dir to the server process
            FileInputStream fis = new FileInputStream(fileToSend);

            //this is writing the file size to the client so we know when to stop buffering stuff.
            out.writeLong(fileToSend.length());

            //while file in has stuff coming in, write to the client.
            while((n = fis.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }

            //flush the out and close the file input steam.
            out.flush();
            fis.close();

            //we should do better error handling, we can even just print out the errors to the server cmd, since it's basically our log.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //this is pretty simple, maybe we can get the current dir in a better way though, not sure.
    private void SendBackAllFilesInCurDir(DataOutputStream out) {
        try {
            File curDir = new File(".");
            File[] FileList = curDir.listFiles();

            for (File file : FileList) {
                out.writeUTF(file.getName());
            }
            out.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void RetrieveFileFromServer(DataInputStream in, String username) {

        //Should we make this buffer a different size?
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

            PutContentsInUsersExistingXMLFile(newFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void PutContentsInUsersExistingXMLFile(File XMLFile) {
        try {
            Document dom;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.parse(XMLFile);
            Element doc = dom.getDocumentElement();
            NodeList thisNodeList = doc.getElementsByTagName("file");

            for (int temp = 0; temp < thisNodeList.getLength(); temp++) {
                Node nNode = thisNodeList.item(temp);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    System.out.println("desc : " + eElement.getElementsByTagName("description").item(0).getTextContent());

                    Element newFile = dom.createElement("file");
                    Element name = dom.createElement("name");
                    name.appendChild(dom.createTextNode(eElement.getElementsByTagName("name").item(0).getTextContent()));
                    newFile.appendChild(name);
                    Element desc = dom.createElement("description");
                    desc.appendChild(dom.createTextNode(eElement.getElementsByTagName("description").item(0).getTextContent()));
                    newFile.appendChild(desc);
                }
            }

            //shitty attempt to append two xml files
//            Element newFile = dom.createElement("file");
//            Element name = dom.createElement("hostname");
//            name.appendChild(dom.createTextNode("asdasd"));
//            newFile.appendChild(name);
//            Element desc = dom.createElement("speed");
//            desc.appendChild(dom.createTextNode("123123"));
//            newFile.appendChild(desc);

            //repeat this for the user specifics files to append it to the xml doc.

            DOMSource source = new DOMSource(dom);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StreamResult result = new StreamResult(DBXML_DIR_SHORTCUT + File.separator + username + ".filelist");
            transformer.transform(source, result);

            File newFile = new File(System.getProperty("user.dir") + File.separator + username + ".temp");
            newFile.delete();


        } catch (ParserConfigurationException pce) {
            System.out.println(pce.getMessage());
        } catch (SAXException se) {
            System.out.println(se.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

    private void WriteXMLUserData(File newUserXML) {

        //This is basically straight off stack overflow...

        Document dom;
        Element e = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            dom = db.newDocument();
            Element rootEle = dom.createElement("userdetails");
            e = dom.createElement("hostname");
            e.appendChild(dom.createTextNode(hostname));
            rootEle.appendChild(e);
            e = dom.createElement("speed");
            e.appendChild(dom.createTextNode(speed));
            rootEle.appendChild(e);
            dom.appendChild(rootEle);

            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            tr.transform(new DOMSource(dom), new StreamResult(new FileOutputStream(newUserXML.getPath())));
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        } catch (TransformerConfigurationException e1) {
            e1.printStackTrace();
        } catch (TransformerException e1) {
            e1.printStackTrace();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

    }
}
