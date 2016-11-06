package package1;

/**
<<<<<<< HEAD
 * Pojo to contain file data for the search results.
=======
 * FileData - getters and setters for the various fields needed.
 *
 * @author Taylor Coleman, David Fletcher
>>>>>>> d679fe22e7e27795e31eed0db5ed32b2589fb123
 */

public class FileData {

    /**
     * A constructor that sets the speed, hostname, filename, and port.
     *
     * @param String the speed of the connection
     * @param String the hostname of the connection
     * @param String the filename
     * @param int the port number
     */
    FileData(String speed, String hostname, String filename, int port){
        this.speed = speed;
        this.filename = filename;
        this.hostname = hostname;
        this.port = port;
    }

    /** The speed of the connection */
    private String speed;
    
    /** The hostname of the connection */
    private String hostname;
    
    /** The filename */
    private String filename;
    
    /** The port number */
    private int port;

    /**
     * Returns the speed of the connection.
     *
     * @return String speed
     */
    public String getSpeed() {
        return speed;
    }

    /**
     * Sets the speed of the connection.
     *
     * @param String speed
     */
    public void setSpeed(String speed) {
        this.speed = speed;
    }

    /**
     * Returns the hostname of the connection.
     *
     * @return String hostname
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Sets the hostname of the connection.
     *
     * @param String hostname
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * Returns the filename.
     *
     * @return String filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Sets the filename.
     *
     * @param String filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Returns the port number of the connection.
     *
     * @return int port number
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the port number of the connection.
     *
     * @param int port
     */
    public void setPort(int port) {
        this.port = port;
    }
}
