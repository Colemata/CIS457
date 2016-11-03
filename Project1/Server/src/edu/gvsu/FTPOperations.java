package edu.gvsu;

import java.io.*;
import java.net.Socket;

/**
 *
 */
public class FTPOperations implements Runnable {

    private DataInputStream in;
    private DataOutputStream out;
    private Socket socket;

    //pass the socket into this thread.
    public FTPOperations(Socket socket) {
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

                System.out.println("Waiting for input from the client...");
                while (true) {


                    byte[] buffer;
                    String cmd = "";
                    String filename = "";

                    //get the line in from the client
                    String line = in.readUTF();

                    //split all the spaces from the line in from the client
                    String[] strList = line.split("\\s+");


                    //get the first part of the line in for the command.
                    if(strList.length > 0){
                        cmd = line.split(" ")[0];
                    }

                    //if it's big enough, get the second item of the line passed from the client
                    if(strList.length > 1){
                        filename = strList[1];
                    }

                    //this should maybe be a switch...

                    //useless command I used for testing...
                    if (cmd.equalsIgnoreCase("server")) {
                        System.out.println("heard server");
                        out.writeUTF("You said SERVER");
                        out.flush();
                    }


                    else if (cmd.equalsIgnoreCase("quit")) {
                        System.out.println("Disconnect requested from : " + socket.getInetAddress());
                        //in.close();

                        //flush and shutdown the sockets, not sure why it even matters.
                        out.flush();
                        socket.shutdownInput();
                        socket.shutdownOutput();
                        socket.close();
                        //kill the thread.
                        Runtime.getRuntime().exit(0);

                        //pretty sure this is not needed...
                        return;


                    } else if (cmd.equalsIgnoreCase("list")) {
                        System.out.println("Listing contents for connection: " + socket.getInetAddress());
                        SendBackAllFilesInCurDir(out);


                    }else if(line.equalsIgnoreCase("retr")){

                        //I don't think this in.read is needed because we already have the variable filename assigned above, debug this and see
                        filename = in.readUTF();
                        System.out.println("Getting file " + filename +
                                "for connection: " + socket.getInetAddress());
                        GetFileForClient(out, filename);
                        System.out.println("File send done.");
                    }else if(cmd.equalsIgnoreCase("stor")){

                        //same thing here as above.
                        filename = in.readUTF();

                        //current dir, plus file sep ("/") plus filename for the new file.
                        File newFile = new File(System.getProperty("user.dir") + File.separator + filename);

                        //file output steam will stream from the server process to the file specified.
                        FileOutputStream fos = new FileOutputStream(newFile);
                        buffer = new byte[4098];

                        //need the filesize to tell us when to stop reading in
                        Long filesize = in.readLong();
                        int read = 0;

                        //init the filesize.
                        int remaining = filesize.intValue();

                        //Not sure what this Math stuff does, but basically read until there isn't anything else to read.
                        while((read = in.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
                            remaining -= read;
                            fos.write(buffer, 0, read);
                        }

                        //close the fileoutput stream.
                        fos.close();
                        System.out.println("File retrieved from client: " + filename);
                    }
                }
            } catch (IOException e) {
                System.out.println("Something went wrong...");
                System.out.println("Connection was not closed properly...");
                try {
                    in.close();
                    out.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                //kill the thread.
                Runtime.getRuntime().exit(0);
                return;
            }
        }
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
}
