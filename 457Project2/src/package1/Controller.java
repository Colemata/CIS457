package package1;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Controller - sets the variables and allows the model classes to access
 * the variables the user inputted in the GUI.
 *
 * @author David Fletcher, Taylor Coleman
 * @version 1.0 | Last Updated: 10/25/16
 */
public class Controller {

    /**
     * The port number
     */
    private int port;

    /**
     * The server host name
     */
    private String serverHostname;

    /**
     * The keyword used to search
     */
    private String keyword;

    /**
     * The command entered by the user
     */
    private String command;

    /**
     * The hostname
     */
    private String hostname;

    private String username;

    /**
     * The speed specified by the user
     */
    private String speed;

    public final String SERVER_FAILURE_TEXT = "zxczxczxc";

    public static Socket server;
    public static DataInputStream in;
    public static DataOutputStream out;

    /**
     * Sets the port number from the GUI.
     *
     * @param port the port number
     */
    void setPortNumber(int port) {

        this.port = port;
    }

    /**
     * Sets the server host name from the GUI.
     *
     * @param serverHostname the server's hostname
     */
    void setServerHostname(String serverHostname) {

        this.serverHostname = serverHostname;
    }

    /**
     * Sets the keyword from the GUI.
     *
     * @param keyword the keyword entered to search
     */
    void setKeyword(String keyword) {

        this.keyword = keyword;
    }

    /**
     * Sets the command from the GUI.
     *
     * @param command command specified by the user
     */
    void setCommand(String command) {

        this.command = command;
    }

    /**
     * Sets the host name from the GUI.
     *
     * @param hostname sets the hostname
     */
    void setHostname(String hostname) {

        this.hostname = hostname;
    }

    /**
     * The username
     */
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the speed specified from the GUI.
     *
     * @param speed sets the connection speed
     */
    void setSpeed(String speed) {

        this.speed = speed;
    }

    /**
     * Returns the port number set by the GUI.
     *
     * @return port number
     */
    int getPortNumber() {

        return port;
    }

    /**
     * Returns the server hostname set by the GUI.
     *
     * @return server hostname
     */
    String getServerHostname() {

        return serverHostname;
    }

    /**
     * Returns the keyword set by the GUI.
     *
     * @return keyword
     */
    String getKeyword() {

        return keyword;
    }

    /**
     * Returns the command set by the GUI.
     *
     * @return command
     */
    String getCommand() {

        return command;
    }

    /**
     * Returns the hostname set by the GUI.
     *
     * @return hostname
     */
    String getHostname() {

        return hostname;
    }

    /**
     * Returns the speed set by the GUI.
     *
     * @return speed
     */
    String getSpeed() {

        return speed;
    }

    public void connectToServer() {
        server = new Socket();
        try {
            server = new Socket(getServerHostname(), getPortNumber());

            if (server.isConnected()) {

                System.out.println("Connected to " + server.getInetAddress());

                //new up some input and output streams.
                //in = new DataInputStream(new BufferedInputStream(server.getInputStream()));
                out = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));

                out.writeUTF(getUsername());
                out.writeUTF(getHostname());
                out.writeUTF(getSpeed());

                out.flush();

            } else {
                System.out.println("Unable to connect to: " + server.getInetAddress());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            waitForServerACK();
        }

    }

    private void waitForServerACK() {
        try {
            in = new DataInputStream(new BufferedInputStream(server.getInputStream()));

            if (!in.readUTF().equalsIgnoreCase(SERVER_FAILURE_TEXT)) {
                //we are ok, and now we want to send our xml file to the server with file descriptions.
                sendXMLFile();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendXMLFile() {
        File dir = new File(".");
        File fileToSend = new File(dir, "filelist.xml");
        int n = 0;


        //init buffer, apparently it's ok to hardcode the size.
        byte[] buffer = new byte[4098];
        try {

            //file in will input the file from the dir to the server process
            FileInputStream fis = new FileInputStream(fileToSend);

            out = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));

            //this is writing the file size to the client so we know when to stop buffering stuff.
            out.writeLong(fileToSend.length());

            //while file in has stuff coming in, write to the client.
            while ((n = fis.read(buffer)) != -1) {
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

    public ArrayList<FileData> sendSearchCritera() {

        ArrayList<FileData> retVal = new ArrayList<FileData>();

        try {
            //send the keyword to the server.
            out = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));
            in = new DataInputStream(new BufferedInputStream(server.getInputStream()));
            out.writeUTF("search");
            out.writeUTF(getKeyword());
            out.flush();

            //wait for the response.

            while (true) {
                String speed = in.readUTF();
                if (speed.equals("search_completed")) {
                    break;
                }
                String hostname = in.readUTF();
                String filename = in.readUTF();
                FileData fd = new FileData(speed, hostname, filename);
                retVal.add(fd);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return retVal;
    }
}
