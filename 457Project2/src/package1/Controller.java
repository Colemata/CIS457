package package1;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Controller - sets the variables and allows the model classes to access
 * the variables the user inputted in the GUI.
 *
 * @author David Fletcher, Taylor Coleman
 * @version 1.0 | Last Updated: 10/25/16
 */
public class Controller {

    /** The port number */
    private int port;

    /** The listening port number */
    private int listeningPortNumber;

    /** A socket to establish a connection */
    public Socket socket;

    /** A client socket */
    public Socket ClientSocket;

    /** A new Thread Pool */
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    /** The server host name */
    private String serverHostname;

    /** The keyword used to search */
    private String keyword;

    /** The command entered by the user */
    private String command;

    /** The hostname */
    private String hostname;

    /** The username */
    private String username;

    /** The speed specified by the user */
    private String speed;

    /** SERVER_FAILURE_TEXT */
    public final String SERVER_FAILURE_TEXT = "zxczxczxc";

    /** A server socket */
    public static Socket server;
    
    /** DataInputStream object */
    public static DataInputStream in;
    
    /** DataOutputStream object */
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
     * Sets the listening port number from the GUI.
     *
     * @param listeningPortNumber the listening port number
     */
    public void setListeningPortNumber(int listeningPortNumber) {

        this.listeningPortNumber = listeningPortNumber;
    }

    /**
     * Gets the listening port number.
     *
     * @return returns the listening port number.
     */
    public int getListeningPortNumber() {

        return listeningPortNumber;
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

    /**
     * Connects to the Server.
     *
     * @return returns true if connected to the server.
     */
    public boolean connectToServer() {
        boolean retVal = false;
        server = new Socket();
        try {
            server = new Socket(getServerHostname(), getPortNumber());

            if (server.isConnected()) {

                System.out.println("Connected to " + server.getInetAddress());
                retVal = true;
                //new up some input and output streams.
                //in = new DataInputStream(new BufferedInputStream(server.getInputStream()));
                out = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));

                out.writeUTF(getUsername());
                out.writeUTF(getHostname());
                out.writeUTF(getSpeed());
                out.writeInt(getListeningPortNumber());

                out.flush();

                FTPThreadPool serverClientThreadPool = new FTPThreadPool();
                serverClientThreadPool.setListeningPortNumber(getListeningPortNumber());
                //serverClientThreadPool.run();
                executorService.submit(serverClientThreadPool);

            } else {
                System.out.println("Unable to connect to: " + server.getInetAddress());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            waitForServerACK();
        }
        return retVal;
    }

    /**
     * Waits for acknowledgement from the server.
     */
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

    /**
     * Sends the XML file.
     */
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

    /**
     * Sends the search criteria.
     *
     * @return returns a list of file data objects.
     */
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
                int port = in.readInt();
                FileData fd = new FileData(speed, hostname, filename, port);
                retVal.add(fd);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return retVal;
    }

    /**
     * Sends commands to other clients.
     *
     * @return returns the status of the connection.
     */
    public String sendCommandToOtherClient() {

        String status = "";
        try {
            //attempt to connect to other client.
            String command = getCommand();
            String[] splitCommand = command.split("\\s+");
            if (splitCommand.length == 3) {
                if (splitCommand[0].equalsIgnoreCase("connect")) {
                    ClientSocket = new Socket(splitCommand[1], Integer.parseInt(splitCommand[2]));
                    status = "Connected to: " + ClientSocket.getInetAddress();
                }
            } else if (splitCommand.length == 2) {
                if (splitCommand[0].equalsIgnoreCase("retr")) {
                    out = new DataOutputStream(new BufferedOutputStream(ClientSocket.getOutputStream()));
                    out.writeUTF("retr");
                    String filename = splitCommand[1];
                    out.writeUTF(filename);
                    out.flush();
                    WaitForFileFromOtherClient();
                    status = "File copied from other client: " + filename;
                }
            } else if (splitCommand.length == 1) {
                if (splitCommand[0].equalsIgnoreCase("quit")) {
                    status = "Disconnected from: " + ClientSocket.getInetAddress();
                    out = new DataOutputStream(new BufferedOutputStream(ClientSocket.getOutputStream()));
                    out.writeUTF("quit");
                    out.flush();
                    ClientSocket.close();
                }
            }
        } catch (Exception e) {
            status = "Connection refused, please try again...";
        }
        return status;
    }

    /**
     * Waits for files from other clients.
     */
    private void WaitForFileFromOtherClient() {

        //Should we make this buffer a different size?
        byte[] buffer = new byte[4098];

        try {

            in = new DataInputStream(new BufferedInputStream(ClientSocket.getInputStream()));
            String dump = in.readUTF();
            String filename = in.readUTF();

            //Make the file object for the file output stream.
            File newFile = new File(System.getProperty("user.dir") + File.separator + filename);
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

            System.out.println("File copied from server: " + filename);

            //close the file output stream.
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Disconnects from the server.
     *
     * @return returns the status of the connection.
     */
    public String sendQuitCommandToServer() {
        String status = "Disconnected from: " + server.getInetAddress();
        try {
            out = new DataOutputStream(new BufferedOutputStream(server.getOutputStream()));
            //DataOutputStream outServer = new DataOutputStream((new BufferedOutputStream(socket.getOutputStream())));
            out.writeUTF("quit");
            //outServer.writeUTF("quit");
            out.flush();
           // outServer.flush();
            server.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }
}
