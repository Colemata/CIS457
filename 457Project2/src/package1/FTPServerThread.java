package package1;

import java.io.*;
import java.net.Socket;

/**
 * FTPServerThread 
 *
 * @author David Fletcher, Taylor Coleman
 */
public class FTPServerThread implements Runnable {

    /** DataInputStream object */
    private DataInputStream in;
    
    /** DataOutputStream object */
    private DataOutputStream out;
    
    /** Socket object */
    private Socket socket;
    
    /** String for the username */
    public static String username;
    
    /** String for the hostname */
    public String hostname;
    
    /** String for the speed */
    public String speed;
    
    /** Shortcut for DBXML */
    public static String DBXML_DIR_SHORTCUT = System.getProperty("user.dir") + File.separator + "DBXML";
    
    /** SERVER_FAILURE_TEXT */
    public final String SERVER_FAILURE_TEXT = "zxczxczxc";

    //pass the socket into this thread.
    public FTPServerThread(Socket socket) {
        this.socket = socket;
        System.out.println("Client connected from: " + socket.getInetAddress());
    }

    /**
     * Runs the FTPServerThread
     */
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
                        String filename = in.readUTF();
                        GetFileForClient(filename);
                        break;
                    case "file-incoming":
                        WaitForFileFromOtherClient();
                        break;
                    case "quit":
                        DisconnectFromOtherClient();
                        break;

                }

            } catch (Exception e) {

            }
        }
    }

    /**
     * Disconnects from a client.
     */
    private void DisconnectFromOtherClient() {

        //flush and shutdown the sockets, not sure why it even matters.
        try {
            socket.shutdownInput();
            socket.shutdownOutput();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //kill the thread.
        Thread.currentThread().interrupt();
    }

    /**
     * Waits for a file from another client.
     */
    private void WaitForFileFromOtherClient() {

        //Should we make this buffer a different size?
        byte[] buffer = new byte[4098];

        try {

            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

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
     * Gets the file for the client.
     *
     * @param String filename.
     */
    private void GetFileForClient(String filename) {

        try {
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            out.writeUTF("file-incoming");
            out.writeUTF(filename);
            out.flush();

            File dir = new File(".");
            File fileToSend = new File(dir, filename);
            int n = 0;

            //init buffer, apparently it's ok to hardcode the size.
            byte[] buffer = new byte[4098];


            //file in will input the file from the dir to the server process
            FileInputStream fis = new FileInputStream(fileToSend);

            //this is writing the file size to the client so we know when to stop buffering stuff.
            out.writeLong(fileToSend.length());
            out.flush();

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
}
